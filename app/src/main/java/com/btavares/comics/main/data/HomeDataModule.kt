package com.btavares.comics.main.data

import com.btavares.comics.main.data.repository.HomeRepositoryImpl
import com.btavares.comics.main.data.retrofit.ComicsService
import com.btavares.comics.main.data.room.db.ComicsDatabase
import com.btavares.comics.app.sharedpreferences.ComicPreferences
import com.btavares.comics.main.domain.repository.HomeRepository
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import retrofit2.Retrofit

internal const val MODULE_NAME = "Data"

val dataModule = Kodein.Module("${MODULE_NAME}Module") {

    bind<HomeRepository>() with singleton { HomeRepositoryImpl(instance(), instance(), instance()) }

    bind() from singleton { instance<Retrofit>().create(ComicsService::class.java) }

    bind() from singleton { ComicsDatabase(instance()) }

    bind() from singleton { instance<ComicsDatabase>().comicsDao() }


}