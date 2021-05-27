package com.btavares.comics.main.presentation.detail

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import coil.load
import com.afollestad.assent.Permission
import com.afollestad.assent.isAllGranted
import com.afollestad.assent.runWithPermissions
import com.btavares.comics.R
import com.btavares.comics.app.presentation.extension.getImageBitmap
import com.btavares.comics.app.presentation.extension.observe
import com.btavares.comics.app.presentation.extension.setOnDebouncedClickListener
import com.btavares.comics.app.presentation.fragment.InjectionFragment
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance

class DetailFragment : InjectionFragment(R.layout.fragment_detail) {

    private val viewModel: DetailViewModel by instance()


    private val stateObserver = Observer<DetailViewModel.ViewState> {
        detailProgressBar.isVisible = it.isLoading
        detailErrorLayout.isVisible = it.isError
        tvDetailErrorMessage.text = getString(it.errorMessageId)
        tvDetailTitle.text = it.comic?.title
        tvDetailDescription.text = it.comic?.description
        if (it.comic?.comicBitmap != null) ivDetailImage.load(it.comic.comicBitmap) else ivDetailImage.load(it.comic?.imageUrl)
        if (it.comic?.comicBitmap != null) pvFullScreenImage.load(it.comic.comicBitmap) else pvFullScreenImage.load(it.comic?.imageUrl)
        btnAddFav.isVisible = !it.comic?.favorite!!
        btnRemoveFav.isVisible = it.comic.favorite
        ivDetailFavUncheck.isVisible = !it.comic.favorite
        ivDetailFavChecked.isVisible = it.comic.favorite

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       setHasOptionsMenu(true)

        val context = requireContext()

        btnExplanation.setOnDebouncedClickListener {
            viewModel.navigateToSeeComicExplanation(context)
        }
        tvDetailDescription.movementMethod = ScrollingMovementMethod()




        btnAddFav.setOnDebouncedClickListener {
          insertFavorite()
        }

        btnRemoveFav.setOnDebouncedClickListener {
            btnRemoveFav.isVisible = false
            ivDetailFavChecked.isVisible = false
            btnAddFav.isVisible = true
            ivDetailFavUncheck.isVisible = true
            viewModel.removeFavorite()
        }

        ivDetailFavUncheck.setOnDebouncedClickListener {
            insertFavorite()
        }

        ivDetailFavChecked.setOnDebouncedClickListener {
            ivDetailFavChecked.isVisible = false
            btnRemoveFav.isVisible = false
            ivDetailFavUncheck.isVisible = true
            btnAddFav.isVisible = true
            viewModel.removeFavorite()

        }

        detailBackArrow.setOnDebouncedClickListener {
            viewModel.navigateBackHomeFragment()
        }

        ivDetailMore.setOnDebouncedClickListener {
            setUpPopupMenu(context)
        }

        ivDetailImage.setOnDebouncedClickListener {
            detailAppBarLayout.isVisible = false
            fullImageLayout.isVisible = true
            btnExplanation.isVisible = false
            hideFavoritesButtons()

        }

        btnCloseFullScreen.setOnDebouncedClickListener {
            fullImageLayout.isVisible = false
            detailAppBarLayout.isVisible = true
            btnExplanation.isVisible = true
            showFavoritesButtons()
        }

        observe(viewModel.stateLiveData, stateObserver)
        viewModel.loadData()

    }

    private fun hideFavoritesButtons() {
        if (viewModel.isFavorite())
            btnRemoveFav.isVisible = false
        else
            btnAddFav.isVisible = false
    }
    private fun showFavoritesButtons() {
        if (!viewModel.isFavorite())
            btnAddFav.isVisible = true
        else
            btnRemoveFav.isVisible = true
    }


    private fun setUpPopupMenu(context: Context) {
        val popup = PopupMenu(context, ivDetailMore)
        val inflater: MenuInflater = popup.menuInflater
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.share_link -> {
                    viewModel.shareComicLink(context)
                }
                R.id.share_image -> {
                    shareImage(context)
                }
                R.id.open_site_item -> {
                    viewModel.navigateToXkcdSite(context)
                }
                R.id.explanation_item -> {
                    viewModel.navigateToSeeComicExplanation(context)
                }
                else -> {
                }
            }
            true
        }
        inflater.inflate(R.menu.detail_menu, popup.menu)
        popup.show()
    }


    private fun shareImage( context: Context) {
        if (isAllGranted(Permission.WRITE_EXTERNAL_STORAGE))
            viewModel.shareImage(context)
        else {
            runWithPermissions(Permission.WRITE_EXTERNAL_STORAGE) {
                viewModel.shareImage(context)
            }
        }
    }

    private fun insertFavorite() {
        ivDetailFavUncheck.isVisible = false
        btnAddFav.isVisible = false
        ivDetailFavChecked.isVisible = true
        btnRemoveFav.isVisible = true
        viewModel.viewModelScope.launch {
            val imageBitmap = viewModel.getComic().imageUrl?.let { getImageBitmap(requireContext(), it) }
            viewModel.insertFavorite(imageBitmap)
        }
    }
}