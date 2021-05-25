package com.btavares.comics.main.presentation

import androidx.fragment.app.Fragment
import com.btavares.comics.app.di.KotlinViewModelProvider
import com.btavares.comics.main.presentation.detail.DetailViewModel
import com.btavares.comics.main.presentation.favorites.FavoritesViewModel
import com.btavares.comics.main.presentation.favorites.recyclerview.FavoritesComicAdapter
import com.btavares.comics.main.presentation.home.HomeViewModel
import com.btavares.comics.main.presentation.home.recyclerview.ComicAdapter
import com.btavares.comics.main.presentation.home.recyclerview.SearchAdapter
import com.btavares.comics.main.presentation.search.SearchViewModel
import com.btavares.comics.main.presentation.search.recyclerview.SearchResultAdapter
import org.kodein.di.Kodein
import org.kodein.di.android.x.AndroidLifecycleScope
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.scoped
import org.kodein.di.generic.singleton

internal const val MODULE_NAME = "Presentation"

val presentationModule = Kodein.Module("${MODULE_NAME}Module") {


    bind<HomeViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context){HomeViewModel(instance(), instance(), instance(), instance(),
                instance(), instance(), instance(), instance())}
    }

    bind<DetailViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context){DetailViewModel(instance(), instance(), instance(), instance())}
    }

    bind<FavoritesViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context){FavoritesViewModel(instance(), instance())}
    }

    bind<SearchViewModel>() with scoped<Fragment>(AndroidLifecycleScope).singleton {
        KotlinViewModelProvider.of(context){SearchViewModel(instance(), instance(), instance())}
    }

   bind() from singleton { ComicAdapter() }

   bind() from singleton { SearchAdapter() }

   bind() from singleton { SearchResultAdapter() }

   bind() from singleton { FavoritesComicAdapter() }

}