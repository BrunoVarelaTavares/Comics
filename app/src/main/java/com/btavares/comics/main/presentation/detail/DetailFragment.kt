package com.btavares.comics.main.presentation.detail

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import coil.load
import com.btavares.comics.R
import com.btavares.comics.app.presentation.extension.observe
import com.btavares.comics.app.presentation.extension.setOnDebouncedClickListener
import com.btavares.comics.app.presentation.fragment.InjectionFragment
import kotlinx.android.synthetic.main.fragment_detail.*
import org.kodein.di.generic.instance

class DetailFragment : InjectionFragment(R.layout.fragment_detail) {

    private val viewModel: DetailViewModel by instance()

    private val stateObserver = Observer<DetailViewModel.ViewState> {
        tvDetailTitle.text = it.comic?.title
        tvDetailDescription.text = it.comic?.description
        if (it.comic?.comicBitmap != null) ivDetailImage.load(it.comic.comicBitmap) else ivDetailImage.load(it.comic?.imageUrl)
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
            btnAddFav.isVisible = false
            ivDetailFavUncheck.isVisible = false
            btnRemoveFav.isVisible = true
            ivDetailFavChecked.isVisible = true
            viewModel.insertFavorite(context)
        }

        btnRemoveFav.setOnDebouncedClickListener {
            btnRemoveFav.isVisible = false
            ivDetailFavChecked.isVisible = false
            btnAddFav.isVisible = true
            ivDetailFavUncheck.isVisible = true
            viewModel.removeFavorite()
        }

        ivDetailFavUncheck.setOnDebouncedClickListener {
            ivDetailFavUncheck.isVisible = false
            btnAddFav.isVisible = false
            ivDetailFavChecked.isVisible = true
            btnRemoveFav.isVisible = true
            viewModel.insertFavorite(context)
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

        observe(viewModel.stateLiveData, stateObserver)
        viewModel.loadData()

    }

    private fun setUpPopupMenu(context: Context) {
        val popup = PopupMenu(context, ivDetailMore)
        val inflater: MenuInflater = popup.menuInflater
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.share_item -> {
                    viewModel.shareComicLink(context)
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
}