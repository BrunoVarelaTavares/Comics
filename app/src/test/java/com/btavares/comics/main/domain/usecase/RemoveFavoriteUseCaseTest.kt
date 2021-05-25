package com.btavares.comics.main.domain.usecase

import com.btavares.comics.main.data.repository.HomeRepositoryImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.any
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test

class RemoveFavoriteUseCaseTest {

    @MockK
    internal lateinit var homeRepository: HomeRepositoryImpl

    private lateinit var removeFavoriteUseCase: RemoveFavoriteUseCase


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        removeFavoriteUseCase = RemoveFavoriteUseCase(homeRepository)

    }


    @Test
    fun ` remove comic `() {

        //given
        coEvery { homeRepository.removeFavorite(any()) } returns any()


        //when
        val result = runBlocking { removeFavoriteUseCase.execute(any()) }


        //then
        result shouldBeEqualTo RemoveFavoriteUseCase.Result.Success(true)

    }



    @Test
    fun `throws exception when RemoveFavoriteUseCase is call` () {

        //given
        val exception = Exception()
        coEvery { homeRepository.removeFavorite(any()) } throws exception


        //when
        val result = runBlocking { removeFavoriteUseCase.execute(any()) }


        //then
        result shouldBeEqualTo RemoveFavoriteUseCase.Result.Error(exception)

    }


}