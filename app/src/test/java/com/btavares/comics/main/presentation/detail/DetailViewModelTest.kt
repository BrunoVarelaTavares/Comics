package com.btavares.comics.main.presentation.detail

import android.graphics.Bitmap
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.btavares.comics.R
import com.btavares.comics.app.presentation.navigation.NavManager
import com.btavares.comics.main.CoroutineRule
import com.btavares.comics.main.domain.DomainFixtures
import com.btavares.comics.main.domain.usecase.InsertFavoriteUseCase
import com.btavares.comics.main.domain.usecase.RemoveFavoriteUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import java.io.IOException

class DetailViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutineTestRule = CoroutineRule()

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    internal lateinit var mockNavManager: NavManager

    @MockK
    internal lateinit var mockDetailFragmentArgs: DetailFragmentArgs

    @MockK
    internal lateinit var mockInsertFavoriteUseCase: InsertFavoriteUseCase

    @MockK
    internal lateinit var mockRemoveFavoriteUseCase: RemoveFavoriteUseCase

    @MockK
    internal lateinit var map : Bitmap


    private lateinit var viewModel : DetailViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        viewModel = DetailViewModel(
            mockNavManager,
            mockDetailFragmentArgs,
            mockInsertFavoriteUseCase,
            mockRemoveFavoriteUseCase)
    }



    @Test
    fun `Detail Fragment navigate back Home`() {
        // given
        val navDirections = DetailFragmentDirections.actionDetailFragmentBackToHomeFragment()

        //when
        viewModel.navigateBackHomeFragment()

        //then
        coVerify { mockNavManager.navigate(navDirections) }
    }

    @Test
    fun ` verify state when InsertFavoriteUseCase call return success`() {
        //given
        val result = true
        coEvery { mockDetailFragmentArgs.comic } returns DomainFixtures.getComicDomainModel()
        coEvery { mockInsertFavoriteUseCase.execute(any(), any()) } returns InsertFavoriteUseCase.Result.Success(result)

        //when
        viewModel.insertFavorite(map)

        //then
        viewModel.stateLiveData.value shouldBeEqualTo DetailViewModel.ViewState(
            isLoading = false,
            isError = false,
            updateData = true
        )
    }

    @Test
    fun ` verify state when InsertFavoriteUseCase call return exception`() {
        //given
        val exception = IOException()
        coEvery { mockDetailFragmentArgs.comic } returns DomainFixtures.getComicDomainModel()
        coEvery { mockInsertFavoriteUseCase.execute(any(), any()) } returns InsertFavoriteUseCase.Result.Error(exception)

        //when

        viewModel.insertFavorite(map)

        //then
        viewModel.stateLiveData.value shouldBeEqualTo DetailViewModel.ViewState(
            isLoading = false,
            isError = true,
            errorMessageId = R.string.error_inserting_favorite_message

        )
    }


    @Test
    fun ` verify state when RemoveFavoriteUseCase call return success`() {
        //given
        val result = true
        coEvery { mockDetailFragmentArgs.comic } returns DomainFixtures.getComicDomainModel()
        coEvery { mockRemoveFavoriteUseCase.execute(any()) } returns RemoveFavoriteUseCase.Result.Success(result)

        //when
        viewModel.removeFavorite()

        //then
        viewModel.stateLiveData.value shouldBeEqualTo DetailViewModel.ViewState(
            isLoading = false,
            isError = false,
            updateData = true
        )
    }

    @Test
    fun ` verify state when RemoveFavoriteUseCase call return exception`() {
        //given
        val exception = IOException()
        coEvery { mockDetailFragmentArgs.comic } returns DomainFixtures.getComicDomainModel()
        coEvery { mockRemoveFavoriteUseCase.execute(any()) } returns RemoveFavoriteUseCase.Result.Error(exception)

        //when
        viewModel.removeFavorite()

        //then
        viewModel.stateLiveData.value shouldBeEqualTo DetailViewModel.ViewState(
            isLoading = false,
            isError = true,
            errorMessageId = R.string.error_removing_favorite_message

        )
    }


}