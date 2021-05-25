package com.btavares.comics.main.domain.usecase

import com.btavares.comics.main.data.repository.HomeRepositoryImpl
import com.btavares.comics.main.domain.DomainFixtures
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.any
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException

class DownloadComicsUseCaseTest {

    @MockK
    internal  lateinit var homeRepository: HomeRepositoryImpl

    private lateinit var downloadComicsUseCase : DownloadComicsUseCase


    @Before
    fun setUp(){
        MockKAnnotations.init(this)
        downloadComicsUseCase = DownloadComicsUseCase(homeRepository)

    }



    @Test
    fun `download comics ` () {

        //given
        coEvery { homeRepository.downloadComics() } returns any()


        //when
        val result = runBlocking {downloadComicsUseCase.execute()}


        //then
        result shouldBeEqualTo DownloadComicsUseCase.Result.Success(true)

    }



    @Test
    fun `throws UnknownHostException when  DownloadComicsUseCase is call` () {

        //given
        val exception = UnknownHostException()
        coEvery { homeRepository.downloadComics() } throws exception


        //when
        val result = runBlocking {downloadComicsUseCase.execute()}


        //then
        result shouldBeEqualTo DownloadComicsUseCase.Result.Error(exception)

    }


}