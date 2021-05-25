package com.btavares.comics.main.presentation.favorites

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.btavares.comics.R
import com.btavares.comics.app.presentation.navigation.NavManager
import com.btavares.comics.main.CoroutineRule
import com.btavares.comics.main.domain.DomainFixtures
import com.btavares.comics.main.domain.usecase.GetFavoritesComicsUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.lang.Exception

class FavoritesViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutineTestRule = CoroutineRule()

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    internal lateinit var mockNavManager: NavManager

    @MockK
    internal lateinit var mockGetFavoritesComicsUseCase: GetFavoritesComicsUseCase


    private lateinit var viewModel : FavoritesViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        viewModel = FavoritesViewModel(
            mockNavManager,
            mockGetFavoritesComicsUseCase)
    }

    @Test
    fun `navigate to detail fragment`() {
        // given
        val comic = DomainFixtures.getComicDomainModel()
        val navDirections = FavoritesFragmentDirections.actionFavoritesFragmentToDetailFragment(comic)

        //when
        viewModel.navigateToDetailFragment(comic)

        //then
        coVerify { mockNavManager.navigate(navDirections) }
    }


    @Test
    fun `navigate back Home`() {
        // given
        val navDirections = FavoritesFragmentDirections.actionFavoritesFragmentBackToHomeFragment()

        //when
        viewModel.navigateBackHomeFragment()

        //then
        coVerify { mockNavManager.navigate(navDirections) }
    }


    @Test
    fun ` verify state when GetFavoritesComicsUseCase call return success`() {
        //given
        val comics = listOf(DomainFixtures.getComicDomainModel(), DomainFixtures.getComicDomainModel())
        coEvery { mockGetFavoritesComicsUseCase.execute() } returns GetFavoritesComicsUseCase.Result.Success(comics)

        //when
        viewModel.loadData()

        //then
        viewModel.stateLiveData.value shouldBeEqualTo FavoritesViewModel.ViewState(
            isLoading = false,
            isError = false,
            comics = comics
        )
    }


    @Test
    fun ` verify state when GetFavoritesComicsUseCase call return empty list`() {
        //given
        coEvery { mockGetFavoritesComicsUseCase.execute() } returns GetFavoritesComicsUseCase.Result.Success(emptyList())

        //when
        viewModel.loadData()

        //then
        viewModel.stateLiveData.value shouldBeEqualTo FavoritesViewModel.ViewState(
            isLoading = false,
            isError = true,
            errorMessageId = R.string.data_not_found_error_message,
            comics = listOf()
        )

    }


    @Test
    fun ` verify state when GetFavoritesComicsUseCase call return UnknownHostException error`() {
        //given
        val exception = Exception()
        coEvery { mockGetFavoritesComicsUseCase.execute() } returns GetFavoritesComicsUseCase.Result.Error(exception)

        //when
        viewModel.loadData()

        //then
        viewModel.stateLiveData.value shouldBeEqualTo FavoritesViewModel.ViewState(
            isLoading = false,
            isError = true,
            errorMessageId = R.string.loading_data_error_message,
            comics = listOf()
        )
    }


    @Test
    fun ` verify state when GetFavoritesComicsUseCase call return IOException exception`() {
        //given
        //given
        val exception = IOException()
        coEvery { mockGetFavoritesComicsUseCase.execute() } returns GetFavoritesComicsUseCase.Result.Error(exception)

        //when
        viewModel.loadData()

        //then
        viewModel.stateLiveData.value shouldBeEqualTo FavoritesViewModel.ViewState(
            isLoading = false,
            isError = true,
            errorMessageId = R.string.loading_data_error_message,
            comics = listOf()
        )
    }

}