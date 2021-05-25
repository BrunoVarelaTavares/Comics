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

class SearchComicsUseCaseTest {

    @MockK
    internal lateinit var homeRepository: HomeRepositoryImpl

    private lateinit var searchComicsUseCase: SearchComicsUseCase


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        searchComicsUseCase = SearchComicsUseCase(homeRepository)

    }


    @Test
    fun `search comics `() {

        //given
        val searchResultsList = mutableListOf(DomainFixtures.getComicDomainModel(), DomainFixtures.getComicDomainModel())
        coEvery { homeRepository.searchComics(any()) } returns searchResultsList

        //when
        val result = runBlocking { searchComicsUseCase.execute(any()) }

        //then
        result shouldBeEqualTo SearchComicsUseCase.Result.Success(searchResultsList)

    }



    @Test
    fun `throws Exception when searchComicsUseCase is call` () {

        //given
        val exception = Exception()
        coEvery { homeRepository.searchComics(any()) } throws exception


        //when
        val result = runBlocking { searchComicsUseCase.execute(any()) }


        //then
        result shouldBeEqualTo SearchComicsUseCase.Result.Error(exception)

    }


}