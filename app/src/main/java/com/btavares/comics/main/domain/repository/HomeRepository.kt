package com.btavares.comics.main.domain.repository

import android.graphics.Bitmap
import com.btavares.comics.main.data.model.ComicDataModel
import com.btavares.comics.main.domain.model.ComicDomainModel

internal interface HomeRepository {

    suspend fun getAllComics() : List<ComicDomainModel>?

    suspend fun updateComicsDb()

    suspend fun getComic(comicNumber: Int) : ComicDataModel?

    suspend fun getDownloadPercentage() : Int

    suspend fun downloadComics()

    suspend fun searchComics(searchText : String) : List<ComicDomainModel>

    suspend fun insertFavorite(comicNumber : Int, imageBitmap: Bitmap)

    suspend fun removeFavorite(comicNumber : Int)

    suspend fun getFavoritesComics() : List<ComicDomainModel>?
}