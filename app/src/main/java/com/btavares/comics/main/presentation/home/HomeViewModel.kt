package com.btavares.comics.main.presentation.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.btavares.comics.R
import com.btavares.comics.app.presentation.extension.getImageBitmap
import com.btavares.comics.app.presentation.navigation.NavManager
import com.btavares.comics.app.presentation.viewmodel.BaseAction
import com.btavares.comics.app.presentation.viewmodel.BaseViewModel
import com.btavares.comics.app.presentation.viewmodel.BaseViewState
import com.btavares.comics.app.sharedpreferences.ComicPreferences
import com.btavares.comics.main.domain.model.ComicDomainModel
import com.btavares.comics.main.domain.usecase.*
import com.btavares.comics.main.domain.usecase.DownloadComicsUseCase
import com.btavares.comics.main.domain.usecase.GetAllComicsUseCase
import com.btavares.comics.main.domain.usecase.GetDownloadPercentageUseCase
import com.btavares.comics.main.domain.usecase.InsertFavoriteUseCase
import com.btavares.comics.main.domain.usecase.SearchComicsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.UnknownHostException

internal class HomeViewModel  (
    private val navManager: NavManager,
    private val getAllComicsUseCase: GetAllComicsUseCase,
    private val getDownloadPercentageUseCase: GetDownloadPercentageUseCase,
    private val downloadComicsUseCase: DownloadComicsUseCase,
    private val searchComicsUseCase: SearchComicsUseCase,
    private val insertFavoriteUseCase: InsertFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val sharedPreferences: ComicPreferences
) : BaseViewModel<HomeViewModel.ViewState, HomeViewModel.Action>(ViewState()) {

    internal data class ViewState(
        val isLoading: Boolean = true,
        val isError: Boolean = false,
        val percentage : Int = 0,
        val errorMessageId : Int = R.string.empty_string,
        val downloadVisible : Boolean = false,
        val comics : List<ComicDomainModel> = listOf(),
        val comicsSearchResults : MutableList<ComicDomainModel> = mutableListOf(),
        val errorSearchMessageId : Int = R.string.empty_string,
        val updateData : Boolean = false
    ): BaseViewState

    internal sealed class Action : BaseAction {
        class ComicsLoadingSuccess(val comics: List<ComicDomainModel>) : Action()
        class ComicsEmptyList(val comics: List<ComicDomainModel>) : Action()
        class ComicsLoadingFailure(val errorMessageId: Int) : Action()
        class DownloadPercentageLoadingSuccess(val percentage : Int) : Action()
        object DownloadPercentageFailure : Action()
        class ComicsSearchLoadingSuccess(val searchComicsResult: MutableList<ComicDomainModel>) : Action()
        class ComicsSearchLoadingFailure(val errorMessageId: Int) : Action()
        class FavoriteInsertedSuccess(val favoriteInserted : Boolean) : Action()
        class FavoriteInsertFailure(val errorMessageId: Int) : Action()
        class FavoriteRemovedSuccess(val favoriteRemoved : Boolean) : Action()
        class FavoriteRemoveFailure(val errorMessageId: Int) : Action()
    }

    override fun onReduceState(viewAction: Action) = when(viewAction) {
        is Action.ComicsLoadingSuccess -> state.copy(
            isLoading = false,
            isError = false,
            comics = viewAction.comics,
            downloadVisible = false
        )

        is Action.ComicsLoadingFailure -> state.copy(
            isLoading = false,
            isError = true,
            errorMessageId = viewAction.errorMessageId,
            comics = listOf(),
            downloadVisible = false
        )

        is Action.ComicsEmptyList -> state.copy(
            isLoading = false,
            isError = true,
            comics = listOf(),
            downloadVisible = true
        )

        is Action.DownloadPercentageLoadingSuccess -> state.copy(
            isLoading = false,
            isError = false,
            downloadVisible = true,
            percentage = viewAction.percentage
        )

        is Action.DownloadPercentageFailure -> state.copy(
            isLoading = false,
            isError = true,
            downloadVisible = false,
            percentage = 0
        )

        is Action.ComicsSearchLoadingSuccess -> state.copy(
            comicsSearchResults = viewAction.searchComicsResult

        )

        is Action.ComicsSearchLoadingFailure -> state.copy(
            errorSearchMessageId = viewAction.errorMessageId
        )

        is Action.FavoriteInsertedSuccess -> state.copy(
            updateData = viewAction.favoriteInserted

        )

        is Action.FavoriteInsertFailure -> state.copy(
            updateData = false
        )

        is Action.FavoriteRemovedSuccess -> state.copy(
            updateData = viewAction.favoriteRemoved

        )

        is Action.FavoriteRemoveFailure -> state.copy(
            updateData = false
        )


    }

    override fun onLoadData() {
        super.onLoadData()
        getComics()
    }



    private fun getDownloadPercentage() = viewModelScope.launch {
        sendAction(Action.ComicsEmptyList(emptyList()))
        launch {
            downloadComicsUseCase.execute()
        }

        launch {
            while (!sharedPreferences.isDownloadCompleted()) {
                getDownloadPercentageUseCase.execute().also { result ->
                    val action = when(result) {
                        is GetDownloadPercentageUseCase.Result.Success -> {
                            Action.DownloadPercentageLoadingSuccess(result.percentage)
                        }
                        is GetDownloadPercentageUseCase.Result.Error ->
                            when(result.e){
                                is UnknownHostException -> Action.DownloadPercentageFailure
                                else -> Action.ComicsLoadingFailure(R.string.loading_data_error_message)
                            }
                    }
                    sendAction(action)
                    delay(500)
                }
            }
            loadData()

        }

    }

    private fun getComics() = viewModelScope.launch {
        getAllComicsUseCase.execute().also { result ->
            val action = when(result) {
                is GetAllComicsUseCase.Result.Success ->
                   if (result.data.isEmpty()) {
                       getDownloadPercentage()
                       Action.ComicsEmptyList(emptyList())
                   }
                   else
                       Action.ComicsLoadingSuccess(result.data)

                is GetAllComicsUseCase.Result.Error ->
                    when(result.e){
                        is UnknownHostException -> Action.ComicsLoadingFailure(R.string.no_network_connection_error_message)
                        else -> Action.ComicsLoadingFailure(R.string.loading_data_error_message)
                    }
            }
            sendAction(action)
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


     fun insertFavorite(comic : ComicDomainModel, context: Context)  = viewModelScope.launch {
        val imageBitmap = getImageBitmap(context, comic.imageUrl!!)
         // TODO Notifications and share comics

         insertFavoriteUseCase.execute(comic.number, imageBitmap).also { result ->
            val action = when(result) {
                is InsertFavoriteUseCase.Result.Success ->
                        Action.FavoriteInsertedSuccess(result.data)

                is InsertFavoriteUseCase.Result.Error ->
                    when(result.e){
                        else -> Action.FavoriteInsertFailure(R.string.error_inserting_favorite_message)
                    }
            }
            sendAction(action)
        }
    }

    fun removeFavorite(comicNumber : Int)  = viewModelScope.launch {
        removeFavoriteUseCase.execute(comicNumber).also { result ->
            val action = when(result) {
                is RemoveFavoriteUseCase.Result.Success ->
                    Action.FavoriteRemovedSuccess(result.data)

                is RemoveFavoriteUseCase.Result.Error ->
                    when(result.e){
                        else -> Action.FavoriteRemoveFailure(R.string.error_removing_favorite_message)
                    }
            }
            sendAction(action)
        }
    }


    fun navigateToDetailFragment(domainModel: ComicDomainModel) {
        val navDirections = HomeFragmentDirections.actionHomeFragmentToDetailFragment(domainModel)
        navManager.navigate(navDirections)
    }

    fun navigateToFavoritesFragment() {
        val navDirections = HomeFragmentDirections.actionHomeFragmentToFavoritesFragment()
        navManager.navigate(navDirections)
    }

    fun navigateToSearchFragment(searchText : String, comics : List<ComicDomainModel>) {
        val navDirections = HomeFragmentDirections.actionHomeFragmentToSearchFragment(searchText, comics.toTypedArray())
        navManager.navigate(navDirections)
    }





}