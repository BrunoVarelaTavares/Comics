package com.btavares.comics.main.domain

import com.btavares.comics.main.domain.usecase.*
import com.btavares.comics.main.domain.usecase.DownloadComicsUseCase
import com.btavares.comics.main.domain.usecase.GetAllComicsUseCase
import com.btavares.comics.main.domain.usecase.GetDownloadPercentageUseCase
import com.btavares.comics.main.domain.usecase.InsertFavoriteUseCase
import com.btavares.comics.main.domain.usecase.SearchComicsUseCase
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

internal const val MODULE_NAME = "Domain"

val domainModule = Kodein.Module("${MODULE_NAME}Module") {

    bind() from singleton { GetAllComicsUseCase(instance()) }

    bind() from singleton { GetDownloadPercentageUseCase(instance()) }

    bind() from singleton { DownloadComicsUseCase(instance()) }

    bind() from singleton { SearchComicsUseCase(instance()) }

    bind() from singleton { InsertFavoriteUseCase(instance()) }

    bind() from singleton { RemoveFavoriteUseCase(instance()) }

    bind() from singleton { GetFavoritesComicsUseCase(instance()) }
}