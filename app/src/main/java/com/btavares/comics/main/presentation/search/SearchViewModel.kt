package com.btavares.comics.main.presentation.search

import androidx.lifecycle.viewModelScope
import com.btavares.comics.R
import com.btavares.comics.app.presentation.navigation.NavManager
import com.btavares.comics.app.presentation.viewmodel.BaseAction
import com.btavares.comics.app.presentation.viewmodel.BaseViewModel
import com.btavares.comics.app.presentation.viewmodel.BaseViewState
import com.btavares.comics.main.domain.model.ComicDomainModel
import com.btavares.comics.main.domain.usecase.SearchComicsUseCase
import kotlinx.coroutines.launch
import java.net.UnknownHostException

internal class SearchViewModel (
    private val navManager: NavManager,
    private val args: SearchFragmentArgs,
    private val searchComicsUseCase: SearchComicsUseCase
) : BaseViewModel<SearchViewModel.ViewState, SearchViewModel.Action>(ViewState()) {

    internal data class ViewState(
        val isLoading: Boolean = true,
        val isError: Boolean = false,
        val searchText : String = "",
        val comics: MutableList<ComicDomainModel> = arrayListOf(),
        val errorSearchMessageId : Int = R.string.empty_string
    ) : BaseViewState

    internal sealed class Action : BaseAction {
        class SearchedComicsLoadingSuccess(val args: SearchFragmentArgs) : Action()
        object SearchedPercentageFailure : Action()
        class ComicsSearchLoadingSuccess(val searchComicsResult: MutableList<ComicDomainModel>) : Action()
        class ComicsSearchLoadingFailure(val errorMessageId: Int) : Action()
    }

    override fun onReduceState(viewAction: Action)= when(viewAction) {
        is Action.SearchedComicsLoadingSuccess -> state.copy(
            isLoading = false,
            isError = false,
            searchText = viewAction.args.searchText,
            comics = viewAction.args.comics.toMutableList()
        )

        is Action.SearchedPercentageFailure -> state.copy(
            isLoading = false,
            isError = true,
            comics = arrayListOf()
        )

        is Action.ComicsSearchLoadingSuccess -> state.copy(
            comics = viewAction.searchComicsResult

        )

        is Action.ComicsSearchLoadingFailure -> state.copy(
            errorSearchMessageId = viewAction.errorMessageId
        )
    }


    override fun onLoadData() {
        super.onLoadData()
        if (args.comics.isNotEmpty()){
            sendAction(Action.SearchedComicsLoadingSuccess(args))
        }
    }

    fun searchComics(searchText: String)  = viewModelScope.launch {
        searchComicsUseCase.execute(searchText).also { result ->
            val action = when(result) {
                is SearchComicsUseCase.Result.Success ->
                    if (result.data.isEmpty())
                        Action.ComicsSearchLoadingFailure(R.string.data_not_found_error_message)
                    else
                        Action.ComicsSearchLoadingSuccess(result.data)

                is SearchComicsUseCase.Result.Error ->
                    when(result.e){
                        is UnknownHostException -> Action.ComicsSearchLoadingFailure(R.string.no_network_connection_error_message)
                        else -> Action.ComicsSearchLoadingFailure(R.string.loading_data_error_message)
                    }
            }
            sendAction(action)
        }
    }

    fun navigateToDetailFragment(domainModel: ComicDomainModel) {
        val navDirections = SearchFragmentDirections.actionSearchFragmentToDetailFragment(domainModel)
        navManager.navigate(navDirections)
    }

    fun navigateBackToHomeFragment() {
        val navDirections = SearchFragmentDirections.actionSearchFragmentBackToHomeFragment()
        navManager.navigate(navDirections)
    }



}