package com.btavares.comics.main.data.repository

import android.graphics.Bitmap
import com.btavares.comics.main.data.model.ComicDataModel
import com.btavares.comics.main.data.model.toDomainModel
import com.btavares.comics.main.data.retrofit.ComicsService
import com.btavares.comics.main.data.room.dao.ComicsDao
import com.btavares.comics.main.data.room.entities.Image
import com.btavares.comics.app.sharedpreferences.ComicPreferences
import com.btavares.comics.main.domain.model.ComicDomainModel
import com.btavares.comics.main.domain.repository.HomeRepository
import retrofit2.HttpException
import java.lang.Exception

internal class HomeRepositoryImpl(
    private val comicsService: ComicsService,
    private val comicsDao: ComicsDao,
    private val sharePreferences: ComicPreferences
) : HomeRepository {

    private var percentage = 0

    override suspend fun getAllComics(): List<ComicDomainModel> {
        val comics = comicsDao.getAllComics()
        return  if (!sharePreferences.isDownloadCompleted()) {
             emptyList()
        } else {
            updateComicsDb()
            comics.map { it.toDomainModel() }
        }
    }

    override suspend fun updateComicsDb() {
        var currentComic = comicsService.getCurrentComic()
        if (currentComic != null) {
            var currentComicNumber = currentComic.number
            val lastComic = comicsDao.getLastComic()
            while (lastComic.number < currentComicNumber) {
                if (currentComic != null) {
                    comicsDao.insert(currentComic)
                }
                currentComicNumber -= 1
                currentComic = getComic(currentComicNumber)
            }

            val comic = comicsDao.getLastComic()
            if (comic.number > 0 &&  sharePreferences.getLastComicNumber() != comic.number && sharePreferences.getLastComicNumber() < comic.number)
                sharePreferences.saveLastComicNumber(comic.number)
        }
    }


    override suspend fun getDownloadPercentage(): Int {
        return percentage
    }

    override suspend fun downloadComics() {
        var currentComic = comicsService.getCurrentComic()
        var currentComicNumber = currentComic?.number
        val originalComicNumber = currentComicNumber
        while (currentComicNumber != null && currentComicNumber > 0) {
            if (currentComic != null) {
                comicsDao.insert(currentComic)
            }
            currentComicNumber -= 1

            if (currentComicNumber == 0)
                break

            percentage = 100 - (100 * currentComicNumber / originalComicNumber!!)
            currentComic = getComic(currentComicNumber)
        }

        val lastComic = comicsDao.getLastComic()
        if (lastComic.number > 0)
            sharePreferences.saveLastComicNumber(lastComic.number)

        sharePreferences.saveDownloadCompleted()

    }

    override suspend fun getComic(comicNumber : Int) : ComicDataModel? {
        try {
            return  comicsService.getComicByNumber(comicNumber)
        } catch (e : HttpException) {
        } catch (e : Exception) {
            throw(e)
        }

        return null
    }

    override suspend fun searchComics(searchText: String): List<ComicDomainModel> {
        return comicsDao.searchComics("%${searchText}%").map { it.toDomainModel() }
    }

    override suspend fun insertFavorite(comicNumber: Int, imageBitmap: Bitmap) {
        comicsDao.setFavorite(comicNumber)
        comicsDao.saveImage(Image(comicNumber, imageBitmap))
    }

    override suspend fun removeFavorite(comicNumber: Int) {
        comicsDao.removeFavorite(comicNumber)
        comicsDao.removeImage(comicNumber)
    }

    override suspend fun getFavoritesComics(): List<ComicDomainModel>? {
        return comicsDao.getAllFavorites()?.map {
            it.toDomainModel(comicsDao.getImage(it.number))
        }
    }

}