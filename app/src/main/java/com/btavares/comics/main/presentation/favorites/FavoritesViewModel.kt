package com.btavares.comics.main.presentation.favorites

import androidx.lifecycle.viewModelScope
import com.btavares.comics.R
import com.btavares.comics.app.presentation.navigation.NavManager
import com.btavares.comics.app.presentation.viewmodel.BaseAction
import com.btavares.comics.app.presentation.viewmodel.BaseViewModel
import com.btavares.comics.app.presentation.viewmodel.BaseViewState
import com.btavares.comics.main.domain.model.ComicDomainModel
import com.btavares.comics.main.domain.usecase.GetAllComicsUseCase
import com.btavares.comics.main.domain.usecase.GetFavoritesComicsUseCase
import com.btavares.comics.main.presentation.home.HomeFragmentDirections
import com.btavares.comics.main.presentation.home.HomeViewModel
import kotlinx.coroutines.launch
import java.net.UnknownHostException

internal class FavoritesViewModel (
    private val navManager: NavManager,
    private val getFavoritesComicsUseCase : GetFavoritesComicsUseCase
) : BaseViewModel<FavoritesViewModel.ViewState, FavoritesViewModel.Action>(ViewState()) {

    internal data class ViewState(
        val isLoading: Boolean = true,
        val isError: Boolean = false,
        val errorMessageId: Int = R.string.empty_string,
        val downloadVisible: Boolean = false,
        val comics: List<ComicDomainModel> = listOf()
    ) : BaseViewState

    internal sealed class Action : BaseAction {
        class FavoritesComicsLoadingSuccess(val comics: List<ComicDomainModel>) : Action()
        class FavoritesComicsLoadingFailure(val errorMessageId: Int) : Action()
    }

    override fun onReduceState(viewAction: Action): ViewState = when(viewAction) {
        is Action.FavoritesComicsLoadingSuccess -> state.copy(
            isLoading = false,
            isError = false,
            comics = viewAction.comics,
            downloadVisible = false
        )

        is Action.FavoritesComicsLoadingFailure -> state.copy(
            isLoading = false,
            isError = true,
            errorMessageId = viewAction.errorMessageId,
            comics = listOf(),
            downloadVisible = false
        )
    }

    override fun onLoadData() {
        super.onLoadData()
        loadFavoritesComics()
    }



    private fun loadFavoritesComics() = viewModelScope.launch {
        getFavoritesComicsUseCase.execute().also { result ->
            val action = when(result) {
                is GetFavoritesComicsUseCase.Result.Success ->
                    if (result.data.isEmpty())
                        Action.FavoritesComicsLoadingFailure(R.string.data_not_found_error_message)
                    else
                        Action.FavoritesComicsLoadingSuccess(result.data)

                is GetFavoritesComicsUseCase.Result.Error ->
                       Action.FavoritesComicsLoadingFailure(R.string.loading_data_error_message)

            }
            sendAction(action)
        }
    }


    fun navigateToDetailFragment(domainModel: ComicDomainModel) {
        val navDirections = FavoritesFragmentDirections.actionFavoritesFragmentToDetailFragment(domainModel)
        navManager.navigate(navDirections)
    }

    fun navigateBackHomeFragment() {
        val navDirections = FavoritesFragmentDirections.actionFavoritesFragmentBackToHomeFragment()
        navManager.navigate(navDirections)
    }

}