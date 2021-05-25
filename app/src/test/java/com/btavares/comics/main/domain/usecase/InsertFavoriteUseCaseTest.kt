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

class InsertFavoriteUseCaseTest {

    @MockK
    internal lateinit var homeRepository: HomeRepositoryImpl

    private lateinit var insertFavoriteUseCase: InsertFavoriteUseCase


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        insertFavoriteUseCase = InsertFavoriteUseCase(homeRepository)

    }


    @Test
    fun ` insert comics `() {

        //given
        coEvery { homeRepository.insertFavorite(any(), any()) } returns any()


        //when
        val result = runBlocking { insertFavoriteUseCase.execute(any(), any()) }


        //then
        result shouldBeEqualTo InsertFavoriteUseCase.Result.Success(true)

    }



    @Test
    fun `throws exception when InsertFavoriteUseCase is call` () {

        //given
        val exception = Exception()
        coEvery { homeRepository.insertFavorite(any(),any()) } throws exception


        //when
        val result = runBlocking { insertFavoriteUseCase.execute(any(), any()) }


        //then
        result shouldBeEqualTo InsertFavoriteUseCase.Result.Error(exception)

    }


}