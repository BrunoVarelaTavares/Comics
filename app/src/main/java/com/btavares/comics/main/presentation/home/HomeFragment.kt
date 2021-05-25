package com.btavares.comics.main.presentation.home

import android.content.Context
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.SearchView
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.btavares.comics.R
import com.btavares.comics.app.presentation.extension.observe
import com.btavares.comics.app.presentation.extension.setOnDebouncedClickListener
import com.btavares.comics.app.presentation.fragment.InjectionFragment
import com.btavares.comics.main.presentation.home.recyclerview.ComicAdapter
import com.btavares.comics.main.presentation.home.recyclerview.SearchAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import org.kodein.di.generic.instance
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import com.afollestad.assent.Permission
import com.afollestad.assent.isAllGranted
import com.afollestad.assent.runWithPermissions
import com.btavares.comics.app.presentation.extension.getCurrentPosition
import com.btavares.comics.main.domain.model.ComicDomainModel
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_home.ivDetailMore

internal class HomeFragment : InjectionFragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by instance()

    private val comicAdapter: ComicAdapter by instance()

    private val searchAdapter: SearchAdapter by instance()

    private val stateObserver = Observer<HomeViewModel.ViewState> {
        homeProgressBar.isVisible = it.isLoading
        homeErrorLayout.isVisible = it.isError
        searchView.isVisible = !it.isError
        tvHomeErrorMessage.text = getText(it.errorMessageId)
        tvPercentage.text = it.percentage.toString()
        homeAppBarLayout.isVisible = !it.downloadVisible
        clDownloadView.isVisible = it.downloadVisible
        downloadProgressBar.progress = it.percentage
        comicAdapter.comics = it.comics
        searchAdapter.comics = it.comicsSearchResults
        comicAdapter.updateData(it.updateData)
        tvDownloadErrorMessage.text = getText(it.downloadErrorMessageId)
        clDownloadErrorLayout.isVisible = it.isErrorDownloadL
        clDownloadLayout.isVisible = !it.isErrorDownloadL


    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()

        comicsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
            adapter = comicAdapter
        }




        comicAdapter.setOnDebouncedClickListener {

            viewModel.navigateToDetailFragment(it)
        }

        comicAdapter.setOnAddFavoriteDebouncedClickListener {
            viewModel.insertFavorite(it, context)
        }

        comicAdapter.setOnRemoveFavoriteDebouncedClickListener {
            viewModel.removeFavorite(it.number)
        }

        searchAdapter.setOnDebouncedClickListener {
            searchAdapter.clearData()
            viewModel.navigateToDetailFragment(it)
        }



        searchRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }

        setUpSearchView()

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(comicsRecyclerView)

        ivDetailMore.setOnDebouncedClickListener {
            setUpPopupMenu(context)
        }

        btnHomeTryAgain.setOnDebouncedClickListener {
            homeProgressBar.isVisible = true
            viewModel.loadData()
        }

        btnDownloadTryAgain.setOnDebouncedClickListener {
            homeProgressBar.isVisible = true
            clDownloadErrorLayout.isVisible = false
            viewModel.downloadComics()
        }


        btnFavorites.setOnDebouncedClickListener {
            viewModel.navigateToFavoritesFragment()
        }



        observe(viewModel.stateLiveData, stateObserver)
        viewModel.loadData()
    }


    private fun setUpPopupMenu(context: Context) {
        val popup = PopupMenu(context, ivDetailMore)
        val inflater: MenuInflater = popup.menuInflater
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_favorites -> {
                    viewModel.navigateToFavoritesFragment()
                }
                R.id.share_link -> {
                    val comic = getCurrentComic()
                    if(comic != null)
                        viewModel.shareComicLink(context, comic)
                }
                R.id.share_link -> {
                    val comic = getCurrentComic()
                    if(comic != null)
                        viewModel.shareComicLink(context, comic)
                }
                R.id.share_image -> {
                    shareImage(context)
                }
                R.id.open_site_item -> {
                    val comic = getCurrentComic()
                    if(comic != null)
                        viewModel.navigateToXkcdSite(context, comic)
                }
                R.id.explanation_item -> {
                    val comic = getCurrentComic()
                    if(comic != null)
                        viewModel.navigateToSeeComicExplanation(context, comic)
                }
                else -> {
                }
            }
            true
        }
        inflater.inflate(R.menu.home_menu, popup.menu)
        popup.show()
    }


    private fun getCurrentComic() : ComicDomainModel? {
        val position = comicsRecyclerView.getCurrentPosition()
        if (position >= 0)
            return comicAdapter.comics[position]
        return null
    }



    private fun shareImage( context: Context) {
        val comic = getCurrentComic()
        if (isAllGranted(Permission.WRITE_EXTERNAL_STORAGE)) {
            if(comic != null)
                viewModel.shareImage(context, comic)
        }else {
            runWithPermissions(Permission.WRITE_EXTERNAL_STORAGE) {
                if(comic != null)
                    viewModel.shareImage(context, comic)
            }
        }
    }




    private fun setUpSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty())
                    viewModel.navigateToSearchFragment(query, searchAdapter.comics.toTypedArray())
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrEmpty())
                        searchAdapter.clearData()
                    else
                        viewModel.searchComics(newText)
                return false
            }
        })

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                searchRecyclerView.visibility = View.VISIBLE
            }
        }

        searchView.setOnCloseListener {
            searchRecyclerView.visibility = View.GONE
            false
        }

    }


}














