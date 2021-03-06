package com.btavares.comics.main.presentation.search

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.btavares.comics.R
import com.btavares.comics.app.presentation.extension.observe
import com.btavares.comics.app.presentation.extension.setOnDebouncedClickListener
import com.btavares.comics.app.presentation.fragment.InjectionFragment
import com.btavares.comics.main.presentation.search.recyclerview.SearchResultAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_search.*
import org.kodein.di.generic.instance

class SearchFragment : InjectionFragment(R.layout.fragment_search) {

    private val viewModel: SearchViewModel by instance()

    private val searchAdapter : SearchResultAdapter by instance()

    private val stateObserver = Observer<SearchViewModel.ViewState> {
         searchProgressBar.isVisible = it.isLoading
         searchErrorLayout.isVisible = it.isError
         searchResultsRV.isVisible = !it.isError
         tvSearchErrorMessage.text = getString(it.errorSearchMessageId)
         searchAdapter.comics = it.comics

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val context = requireContext()

        searchResultsRV.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }

        searchAdapter.setOnDebouncedClickListener {
            viewModel.navigateToDetailFragment(it)
        }

        backArrow.setOnDebouncedClickListener {
            viewModel.navigateBackToHomeFragment()
        }

        setUpSearchView()

        observe(viewModel.stateLiveData, stateObserver)
        viewModel.loadData()
        searchViewSearchFra.setQuery(viewModel.getSearchText(), false)
        searchViewSearchFra.isIconified = false
    }

    private fun setUpSearchView() {
        searchViewSearchFra.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty())
                    viewModel.searchComics(query)
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


        searchViewSearchFra.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                tvSearchTitle.visibility = View.GONE
            }
        }

        searchViewSearchFra.setOnCloseListener {
            tvSearchTitle.visibility= View.VISIBLE
            false
        }

    }


}