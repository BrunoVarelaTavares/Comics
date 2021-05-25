package com.btavares.comics.main.presentation.detail

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.viewModelScope
import com.btavares.comics.BuildConfig
import com.btavares.comics.R
import com.btavares.comics.app.presentation.extension.getImageBitmap
import com.btavares.comics.app.presentation.extension.saveImageInDirectory
import com.btavares.comics.app.presentation.navigation.NavManager
import com.btavares.comics.app.presentation.viewmodel.BaseAction
import com.btavares.comics.app.presentation.viewmodel.BaseViewModel
import com.btavares.comics.app.presentation.viewmodel.BaseViewState
import com.btavares.comics.main.domain.model.ComicDomainModel
import com.btavares.comics.main.domain.usecase.InsertFavoriteUseCase
import com.btavares.comics.main.domain.usecase.RemoveFavoriteUseCase
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

internal class DetailViewModel (
    private val navManager: NavManager,
    private val args: DetailFragmentArgs,
    private val insertFavoriteUseCase: InsertFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase
) : BaseViewModel<DetailViewModel.ViewState, DetailViewModel.Action>(ViewState()) {

    internal data class ViewState(
        val isLoading: Boolean = true,
        val isError: Boolean = false,
        val errorMessageId: Int = R.string.empty_string,
        val comic: ComicDomainModel? = null,
        val updateData : Boolean = false
    ) : BaseViewState

    internal sealed class Action : BaseAction {
        class ComicDetailLoadingSuccess(val comic: ComicDomainModel?) : Action()
        object ComicDetailLoadingFailure : Action()
        class FavoriteInsertedSuccess(val favoriteInserted : Boolean) : Action()
        class FavoriteInsertFailure(val errorMessageId: Int) : Action()
        class FavoriteRemovedSuccess(val favoriteRemoved : Boolean) : Action()
        class FavoriteRemoveFailure(val errorMessageId: Int) : Action()
    }

    override fun onReduceState(viewAction: Action) = when(viewAction) {
        is Action.ComicDetailLoadingSuccess -> state.copy(
            isLoading = false,
            isError = false,
            comic = viewAction.comic
        )

        is Action.ComicDetailLoadingFailure -> state.copy(
            isLoading = false,
            isError = true,
            comic = null
        )

        is Action.FavoriteInsertedSuccess -> state.copy(
            isLoading = false,
            isError = false,
            updateData = viewAction.favoriteInserted

        )

        is Action.FavoriteInsertFailure -> state.copy(
            isLoading = false,
            isError = true,
            updateData = false,
            errorMessageId = viewAction.errorMessageId
        )

        is Action.FavoriteRemovedSuccess -> state.copy(
            isLoading = false,
            isError = false,
            updateData = viewAction.favoriteRemoved
        )

        is Action.FavoriteRemoveFailure -> state.copy(
            isLoading = false,
            isError = true,
            updateData = false,
            errorMessageId = viewAction.errorMessageId
        )
    }


    override fun onLoadData() {
        super.onLoadData()
        if (!args.comic.title.isNullOrEmpty())
            sendAction(Action.ComicDetailLoadingSuccess(args.comic))
        else
            sendAction(Action.ComicDetailLoadingFailure)
    }



    fun navigateToSeeComicExplanation(context : Context){
        val url = "${BuildConfig.EXPLAIN_XKCD_URL}${args.comic.number}"
        val builder = CustomTabsIntent.Builder()
        builder.addDefaultShareMenuItem()
        builder.setShowTitle(true)
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }

    fun shareImage(context : Context) = viewModelScope.launch  {
        val intent = Intent(Intent.ACTION_SEND).setType("image/jpeg")
        val bitmap = getImageBitmap(context, args.comic.imageUrl!!)
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val uri = saveImageInDirectory(context, bitmap)
        if (uri != null) {
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            context.startActivity(intent)
        }
    }


    fun navigateToXkcdSite(context : Context){
        val url = "${BuildConfig.BASE_URL}${args.comic.number}"
        val builder = CustomTabsIntent.Builder()
        builder.addDefaultShareMenuItem()
        builder.setShowTitle(true)
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }

    fun navigateBackHomeFragment() {
        val navDirections = DetailFragmentDirections.actionDetailFragmentBackToHomeFragment()
       navManager.navigate(navDirections)
    }

    fun shareComicLink(context: Context){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "${BuildConfig.BASE_URL}${args.comic.number}")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    fun getComic ()= args.comic

    fun insertFavorite(imageBitmap: Bitmap?)  = viewModelScope.launch {
            if (imageBitmap != null) {
               args.comic.favorite = true
                insertFavoriteUseCase.execute(args.comic.number, imageBitmap).also { result ->
                    val action = when(result) {
                        is InsertFavoriteUseCase.Result.Success ->
                            Action.FavoriteInsertedSuccess(result.data)
                        is InsertFavoriteUseCase.Result.Error ->
                             Action.FavoriteInsertFailure(R.string.error_inserting_favorite_message)

                    }
                    sendAction(action)

                }
            }

    }

    fun removeFavorite()  = viewModelScope.launch {
        args.comic.favorite = false
        removeFavoriteUseCase.execute(args.comic.number).also { result ->
            val action = when(result) {
                is RemoveFavoriteUseCase.Result.Success ->
                    Action.FavoriteRemovedSuccess(result.data)
                is RemoveFavoriteUseCase.Result.Error ->
                      Action.FavoriteRemoveFailure(R.string.error_removing_favorite_message)

            }
            sendAction(action)
        }
    }

}