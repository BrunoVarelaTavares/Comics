package com.btavares.comics.main.presentation.home

import android.os.Bundle
import android.view.View
import android.widget.SearchView
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

internal class HomeFragment : InjectionFragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by instance()

    private val comicAdapter: ComicAdapter by instance()

    private val searchAdapter: SearchAdapter by instance()

    private val stateObserver = Observer<HomeViewModel.ViewState> {
        homeProgressBar.isVisible = it.isLoading
        homeErrorLayout.isVisible = it.isError
        tvPercentage.text = it.percentage.toString()
        clDownloadView.isVisible = it.downloadVisible
        downloadProgressBar.progress = it.percentage
        comicAdapter.comics = it.comics
        searchAdapter.comics = it.comicsSearchResults
        comicAdapter.updateData(it.updateData)

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

        ivHomeFavorites.setOnDebouncedClickListener {
            viewModel.navigateToFavoritesFragment()
        }



        observe(viewModel.stateLiveData, stateObserver)
        viewModel.loadData()
    }


    private fun setUpSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty())
                    viewModel.navigateToSearchFragment(query, searchAdapter.comics)
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














