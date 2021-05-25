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

class GetFavoritesComicsUseCaseTest {

    @MockK
    internal lateinit var homeRepository: HomeRepositoryImpl

    private lateinit var getFavoritesComicsUseCase: GetFavoritesComicsUseCase


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getFavoritesComicsUseCase = GetFavoritesComicsUseCase(homeRepository)

    }


    @Test
    fun `download all comics `() {

        //given
        val favList = listOf(DomainFixtures.getComicDomainModel(), DomainFixtures.getComicDomainModel())
        coEvery { homeRepository.getFavoritesComics() } returns favList


        //when
        val result = runBlocking { getFavoritesComicsUseCase.execute() }


        //then
        result shouldBeEqualTo GetFavoritesComicsUseCase.Result.Success(favList)

    }



    @Test
    fun `throws Exception when GetFavoritesComicsUseCase is call` () {

        //given
        val exception = Exception()
        coEvery { homeRepository.getFavoritesComics() } throws exception


        //when
        val result = runBlocking { getFavoritesComicsUseCase.execute() }


        //then
        result shouldBeEqualTo GetFavoritesComicsUseCase.Result.Error(exception)

    }


}