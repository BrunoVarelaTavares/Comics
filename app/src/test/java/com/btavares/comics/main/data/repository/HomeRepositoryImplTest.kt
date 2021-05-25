package com.btavares.comics.main.data.repository

import android.content.Context
import android.content.SharedPreferences
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
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import java.net.UnknownHostException

class HomeRepositoryImplTest {

    @MockK
    internal lateinit var service: ComicsService

    @MockK
    internal lateinit var comicDao: ComicsDao

    @MockK
    internal lateinit var comicPreferences: ComicPreferences

    @MockK
    internal lateinit var sharePreferences: SharedPreferences

    @MockK
    internal lateinit var editor: SharedPreferences.Editor

    @Mock
    internal lateinit var context: Context


    private lateinit var homeRepositoryImpl: HomeRepositoryImpl

    @Before
    fun setUp(){
        MockKAnnotations.init(this)
        context = Mockito.mock(Context::class.java)
        Mockito.`when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharePreferences)
        comicPreferences = ComicPreferences(context)
        editor = Mockito.mock(SharedPreferences.Editor::class.java)
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
        } returns DataFixtures.getComicDataModelOne().number


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
    fun ` downloadComics `() {
        //given
        coEvery {
            service.getCurrentComic()
        } returns DataFixtures.getComicDataModelTwo()

        coEvery {
            comicDao.insert(DataFixtures.getComicDataModelTwo())
        } returns  any()

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
            sharePreferences.edit()
        } returns editor


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
            sharePreferences.edit()
        } returns editor

        coEvery {
            comicPreferences.getLastComicNumber()
        } returns DataFixtures.getComicDataModelTwo().number


        //when
       val result =  runBlocking { homeRepositoryImpl.updateComicsDb() }


        result shouldBeEqualTo Unit
    }






    @Test
    fun ` get singleComic `() {

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
    fun ` get returns null comic `() {

        //given
        val exception = UnknownHostException()
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