package com.btavares.comics.main.domain.usecase

import com.btavares.comics.main.data.repository.HomeRepositoryImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test


class GetDownloadPercentageUseCaseTest {

    @MockK
    internal lateinit var homeRepository: HomeRepositoryImpl

    private lateinit var getDownloadPercentageUseCase: GetDownloadPercentageUseCase


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getDownloadPercentageUseCase = GetDownloadPercentageUseCase(homeRepository)

    }


    @Test
    fun `download all comics `() {

        //given
        val percentage = 10
        coEvery { homeRepository.getDownloadPercentage() } returns percentage


        //when
        val result = runBlocking { getDownloadPercentageUseCase.execute() }


        //then
        result shouldBeEqualTo GetDownloadPercentageUseCase.Result.Success(percentage)

    }

}