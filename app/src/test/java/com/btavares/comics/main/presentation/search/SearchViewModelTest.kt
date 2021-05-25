package com.btavares.comics.main.presentation.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.btavares.comics.R
import com.btavares.comics.app.presentation.navigation.NavManager
import com.btavares.comics.main.CoroutineRule
import com.btavares.comics.main.domain.DomainFixtures
import com.btavares.comics.main.domain.usecase.SearchComicsUseCase
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

class SearchViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutineTestRule = CoroutineRule()

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    internal lateinit var mockNavManager: NavManager

    @MockK
    internal lateinit var mockSearchFragmentArgs: SearchFragmentArgs

    @MockK
    internal lateinit var mockSearchComicsUseCase: SearchComicsUseCase


    private lateinit var viewModel : SearchViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        viewModel = SearchViewModel(
            mockNavManager,
            mockSearchFragmentArgs,
            mockSearchComicsUseCase)
    }


    @Test
    fun `Search Fragment navigate to detail fragment`() {
        // given
        val comic = DomainFixtures.getComicDomainModel()
        val navDirections = SearchFragmentDirections.actionSearchFragmentToDetailFragment(comic)

        //when
        viewModel.navigateToDetailFragment(comic)

        //then
        coVerify { mockNavManager.navigate(navDirections) }
    }


    @Test
    fun `Search Fragmentnavigate back Home`() {
        // given
        val navDirections = SearchFragmentDirections.actionSearchFragmentBackToHomeFragment()

        //when
        viewModel.navigateBackToHomeFragment()

        //then
        coVerify { mockNavManager.navigate(navDirections) }
    }


    @Test
    fun ` verify state when SearchComicsUseCase call return success`() {
        //given
        val searchText = "text"
        val comics = mutableListOf(DomainFixtures.getComicDomainModel(), DomainFixtures.getComicDomainModel())
        coEvery { mockSearchComicsUseCase.execute(searchText) } returns SearchComicsUseCase.Result.Success(comics)

        //when
        viewModel.searchComics(searchText)

        //then
        viewModel.stateLiveData.value shouldBeEqualTo SearchViewModel.ViewState(
            isLoading = false,
            isError = false,
            comics = comics
        )
    }


    @Test
    fun ` verify state when SearchComicsUseCase call return empty list`() {
        //given
        val searchText = "text"
        coEvery { mockSearchComicsUseCase.execute(any()) } returns SearchComicsUseCase.Result.Success(
            mutableListOf()
        )

        //when
        viewModel.searchComics(searchText)

        //then
        viewModel.stateLiveData.value shouldBeEqualTo SearchViewModel.ViewState(
            isLoading = false,
            isError = true,
            errorSearchMessageId = R.string.data_not_found_error_message,
            comics = mutableListOf()
        )

    }


    @Test
    fun ` verify state when SearchComicsUseCase call return UnknownHostException error`() {
        //given
        val exception = Exception()
        val searchText = "text"
        coEvery { mockSearchComicsUseCase.execute(any()) } returns SearchComicsUseCase.Result.Error(exception)

        //when
        viewModel.searchComics(searchText)

        //then
        viewModel.stateLiveData.value shouldBeEqualTo SearchViewModel.ViewState(
            isLoading = false,
            isError = true,
            errorSearchMessageId = R.string.loading_data_error_message,
            comics = mutableListOf()
        )
    }


    @Test
    fun ` verify state when SearchComicsUseCase call return IOException exception`() {
        //given
        val exception = IOException()
        val searchText = "text"
        coEvery { mockSearchComicsUseCase.execute(any()) } returns SearchComicsUseCase.Result.Error(exception)

        //when
        viewModel.searchComics(searchText)

        //then
        viewModel.stateLiveData.value shouldBeEqualTo SearchViewModel.ViewState(
            isLoading = false,
            isError = true,
            errorSearchMessageId = R.string.loading_data_error_message,
            comics = mutableListOf()
        )
    }



}