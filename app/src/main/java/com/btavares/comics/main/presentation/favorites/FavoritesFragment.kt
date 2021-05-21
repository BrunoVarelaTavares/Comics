package com.btavares.comics.main.presentation.favorites

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.btavares.comics.R
import com.btavares.comics.app.presentation.extension.observe
import com.btavares.comics.app.presentation.extension.setOnDebouncedClickListener
import com.btavares.comics.app.presentation.fragment.InjectionFragment
import com.btavares.comics.main.presentation.favorites.recyclerview.FavoritesComicAdapter
import kotlinx.android.synthetic.main.fragment_favorites.*
import org.kodein.di.generic.instance

class FavoritesFragment : InjectionFragment(R.layout.fragment_favorites) {

    private val viewModel: FavoritesViewModel by instance()

    private val favoritesAdapter: FavoritesComicAdapter by instance()

    private val stateObserver = Observer<FavoritesViewModel.ViewState> {
        favoritesProgressBar.isVisible = it.isLoading
        favErrorLayout.isVisible = it.isError
        tvFavErrorMessage.text = getString(it.errorMessageId)
        favoritesAdapter.comics = it.comics

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val context = requireContext()

        rvFav.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
            adapter = favoritesAdapter
        }

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(rvFav)

        favoritesAdapter.setOnDebouncedClickListener {
            viewModel.navigateToDetailFragment(it)
        }

        favBackArrow.setOnDebouncedClickListener {
            viewModel.navigateBackHomeFragment()
        }



        observe(viewModel.stateLiveData, stateObserver)
        viewModel.loadData()

    }


}