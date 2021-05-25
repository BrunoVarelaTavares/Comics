package com.btavares.comics.main.domain.usecase

import com.btavares.comics.main.data.repository.HomeRepositoryImpl
import com.btavares.comics.main.domain.DomainFixtures
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException

class GetAllComicsUseCaseTest {

    @MockK
    internal  lateinit var homeRepository: HomeRepositoryImpl

    private lateinit var getAllComicsUseCase : GetAllComicsUseCase


    @Before
    fun setUp(){
        MockKAnnotations.init(this)
        getAllComicsUseCase = GetAllComicsUseCase(homeRepository)

    }



    @Test
    fun `download all comics ` () {

        //given
        val list = listOf(DomainFixtures.getComicDomainModel(), DomainFixtures.getComicDomainModel())
        coEvery { homeRepository.getAllComics() } returns list


        //when
        val result = runBlocking {getAllComicsUseCase.execute()}


        //then
        result shouldBeEqualTo GetAllComicsUseCase.Result.Success(list)

    }



    @Test
    fun `throws UnknownHostException when GetAllComicsUseCase is call` () {

        //given
        val exception = UnknownHostException()
        coEvery { homeRepository.getAllComics() } throws exception


        //when
        val result = runBlocking {getAllComicsUseCase.execute()}


        //then
        result shouldBeEqualTo GetAllComicsUseCase.Result.Error(exception)

    }



}