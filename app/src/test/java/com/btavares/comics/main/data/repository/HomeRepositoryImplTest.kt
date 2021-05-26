package com.btavares.comics.main.data.repository

import com.btavares.comics.app.sharedpreferences.ComicPreferences
import com.btavares.comics.main.data.DataFixtures
import com.btavares.comics.main.data.model.toDomainModel
import com.btavares.comics.main.data.retrofit.ComicsService
import com.btavares.comics.main.data.room.dao.ComicsDao
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.any
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test

class HomeRepositoryImplTest {

    @MockK
    internal lateinit var service: ComicsService

    @MockK
    internal lateinit var comicDao: ComicsDao

    @MockK
    private lateinit var comicPreferences: ComicPreferences
    private lateinit var homeRepositoryImpl: HomeRepositoryImpl

    @Before
    fun setUp(){
        MockKAnnotations.init(this)
        homeRepositoryImpl = HomeRepositoryImpl(
            service,
            comicDao,
            comicPreferences
        )
    }

    @Test
    fun `get comics list `() {
        //given
        coEvery {
           comicDao.getAllComics()
        } returns listOf(DataFixtures.getComicDataModelOne(), DataFixtures.getComicDataModelOne())


        coEvery {
            comicPreferences.isDownloadCompleted()
        } returns true

        coEvery {
            service.getCurrentComic()
        } returns DataFixtures.getComicDataModelOne()

        coEvery {
            service.getCurrentComic()
        } returns DataFixtures.getComicDataModelOne()

        coEvery {
            comicDao.getLastComic()
        } returns DataFixtures.getComicDataModelOne()

        coEvery {
            comicPreferences.getLastComicNumber()
        } returns DataFixtures.getComicDataModelTwo().number



        //when
        val result = runBlocking { homeRepositoryImpl.getAllComics() }

        //then
        result shouldBeEqualTo listOf(DataFixtures.getComicDataModelOne().toDomainModel(), DataFixtures.getComicDataModelOne().toDomainModel())
    }


    @Test
    fun `get comics empty list `() {
        //given
        coEvery {
            comicDao.getAllComics()
        } returns listOf(DataFixtures.getComicDataModelOne(), DataFixtures.getComicDataModelOne())

        coEvery {
            comicPreferences.isDownloadCompleted()
        } returns false


        //when
        val result = runBlocking { homeRepositoryImpl.getAllComics() }

        //then
        result shouldBeEqualTo emptyList()
    }


    @Test
    fun ` download comics `() {

        coEvery {
            service.getCurrentComic()
        } returns DataFixtures.getComicDataModelTwo()

        coEvery {
            comicDao.insert(DataFixtures.getComicDataModelTwo())
        } returns  any()

        coEvery {
            service.getComicByNumber(DataFixtures.getComicDataModelOne().number)
        } returns DataFixtures.getComicDataModelOne()

        coEvery {
            comicDao.insert(DataFixtures.getComicDataModelOne())
        } returns  any()

        coEvery {
            comicDao.getLastComic()
        } returns DataFixtures.getComicDataModelTwo()


        coEvery {
            comicPreferences.saveLastComicNumber(DataFixtures.getComicDataModelTwo().number)
        } returns Unit

        coEvery {
            comicPreferences.saveDownloadCompleted()
        } returns Unit

        //when
        val result = runBlocking { homeRepositoryImpl.downloadComics() }


        // then
        result shouldBeEqualTo Unit


    }

    @Test
    fun ` update comics `() {
        //given
        coEvery {
            service.getCurrentComic()
        } returns DataFixtures.getComicDataModelTwo()

        coEvery {
            comicDao.insert(DataFixtures.getComicDataModelTwo())
        } returns any()

        coEvery {
            comicDao.insert(DataFixtures.getComicDataModelOne())
        } returns  any()

        coEvery {
            comicDao.getLastComic()
        } returns DataFixtures.getComicDataModelOne()


        coEvery {
            service.getComicByNumber(DataFixtures.getComicDataModelOne().number)
        } returns DataFixtures.getComicDataModelOne()

        coEvery {
            comicDao.getLastComic()
        } returns DataFixtures.getComicDataModelTwo()

        coEvery {
            comicPreferences.getLastComicNumber()
        } returns DataFixtures.getComicDataModelTwo().number


        //when
       val result =  runBlocking { homeRepositoryImpl.updateComicsDb() }


        result shouldBeEqualTo Unit
    }

    @Test
    fun ` get single comic `() {

        //given
        coEvery {
            service.getComicByNumber(DataFixtures.getComicDataModelOne().number)
        } returns DataFixtures.getComicDataModelOne()

        //when
        val result = runBlocking { homeRepositoryImpl.getComic(DataFixtures.getComicDataModelOne().number) }


        //then
        result shouldBeEqualTo DataFixtures.getComicDataModelOne()

    }

    @Test
    fun ` getComicByNumber returns null `() {

        //given
        coEvery {
            service.getComicByNumber(DataFixtures.getComicDataModelOne().number)
        } returns null

        //when
        val result = runBlocking { homeRepositoryImpl.getComic(DataFixtures.getComicDataModelOne().number) }


        //then
        result shouldBeEqualTo null

    }


    @Test
    fun ` search comics `() {

        //given
        coEvery {
            comicDao.searchComics(any())
        } returns listOf(DataFixtures.getComicDataModelOne(),DataFixtures.getComicDataModelTwo() )

        //when
        val result = runBlocking { homeRepositoryImpl.searchComics(any()) }


        //then
        result shouldBeEqualTo listOf(DataFixtures.getComicDataModelOne().toDomainModel(), DataFixtures.getComicDataModelTwo().toDomainModel())

    }

    @Test
    fun ` insert favorite `() {

        //given
        coEvery {
            comicDao.setFavorite(any())
        } returns any()

        coEvery {
            comicDao.saveImage(any())
        } returns any()

        //when
        val result = runBlocking { homeRepositoryImpl.insertFavorite(any(), any()) }


        //then
        result shouldBeEqualTo Unit

    }

    @Test
    fun ` remove favorite `() {

        //given
        coEvery {
            comicDao.removeFavorite(any())
        } returns any()

        coEvery {
            comicDao.removeImage(any())
        } returns any()

        //when
        val result = runBlocking { homeRepositoryImpl.removeFavorite(any()) }


        //then
        result shouldBeEqualTo Unit

    }



}