package com.btavares.comics.main.presentation.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.btavares.comics.R
import com.btavares.comics.app.presentation.navigation.NavManager
import com.btavares.comics.app.sharedpreferences.ComicPreferences
import com.btavares.comics.main.CoroutineRule
import com.btavares.comics.main.domain.DomainFixtures
import com.btavares.comics.main.domain.usecase.*
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
import java.net.UnknownHostException

class HomeViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutineTestRule = CoroutineRule()

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    internal lateinit var mockNavManager: NavManager

    @MockK
    internal lateinit var mockGetAllComicsUseCase: GetAllComicsUseCase

    @MockK
    internal lateinit var mockGetDownloadPercentageUseCase: GetDownloadPercentageUseCase

    @MockK
    internal lateinit var mockDownloadComicsUseCase: DownloadComicsUseCase

    @MockK
    internal lateinit var mockSearchComicsUseCase: SearchComicsUseCase

    @MockK
    internal lateinit var mockInsertFavoriteUseCase: InsertFavoriteUseCase

    @MockK
    internal lateinit var mockRemoveFavoriteUseCase: RemoveFavoriteUseCase

    @MockK
    internal lateinit var mockSharedPreferences: ComicPreferences

    private lateinit var viewModel : HomeViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        viewModel = HomeViewModel(mockNavManager,
            mockGetAllComicsUseCase,
            mockGetDownloadPercentageUseCase,
            mockDownloadComicsUseCase,
            mockSearchComicsUseCase,
            mockInsertFavoriteUseCase,
            mockRemoveFavoriteUseCase,
            mockSharedPreferences)

    }

    @Test
    fun `navigate to detail fragment`() {
        // given
        val comic = DomainFixtures.getComicDomainModel()
        val navDirections = HomeFragmentDirections.actionHomeFragmentToDetailFragment(comic)

        //when
        viewModel.navigateToDetailFragment(comic)

        //then
        coVerify { mockNavManager.navigate(navDirections) }
    }


    @Test
    fun `navigate to favorites fragment`() {
        // given
        val navDirections = HomeFragmentDirections.actionHomeFragmentToFavoritesFragment()

        //when
        viewModel.navigateToFavoritesFragment()

        //then
        coVerify { mockNavManager.navigate(navDirections) }
    }


    @Test
    fun `navigate to search fragment`() {
        // given
        val searchText = "text"
        val comicsSearchList = listOf(DomainFixtures.getComicDomainModel(), DomainFixtures.getComicDomainModel()).toTypedArray()
        val navDirections = HomeFragmentDirections.actionHomeFragmentToSearchFragment(searchText, comicsSearchList)

        //when
        viewModel.navigateToSearchFragment(searchText, comicsSearchList)

        //then
        coVerify { mockNavManager.navigate(navDirections) }
    }

    @Test
    fun ` verify state when GetAllComicsUseCase call return comics list`() {
        //given
        val comics = listOf(DomainFixtures.getComicDomainModel(), DomainFixtures.getComicDomainModel())
        coEvery { mockGetAllComicsUseCase.execute() } returns GetAllComicsUseCase.Result.Success(comics)

        //when
        viewModel.loadData()

        //then
        viewModel.stateLiveData.value shouldBeEqualTo HomeViewModel.ViewState(
            isLoading = false,
            isError = false,
            comics = comics,
            downloadVisible = false
        )
    }

    @Test
    fun ` verify state when GetAllComicsUseCase call throws UnknownHostException`() {
        //given
        val exception = UnknownHostException()
        coEvery { mockGetAllComicsUseCase.execute() } returns GetAllComicsUseCase.Result.Error(exception)

        //when
        viewModel.loadData()

        //then
        viewModel.stateLiveData.value shouldBeEqualTo HomeViewModel.ViewState(
            isLoading = false,
            isError = true,
            comics = emptyList(),
            errorMessageId = R.string.no_network_connection_error_message,
        )
    }

    @Test
    fun ` verify state when GetAllComicsUseCase call throws IOException`() {
        //given
        val exception = IOException()
        coEvery { mockGetAllComicsUseCase.execute() } returns GetAllComicsUseCase.Result.Error(exception)

        //when
        viewModel.loadData()

        //then
        viewModel.stateLiveData.value shouldBeEqualTo HomeViewModel.ViewState(
            isLoading = false,
            isError = true,
            comics = emptyList(),
            errorMessageId = R.string.loading_data_error_message,
        )
    }



    @Test
    fun ` verify state when DownloadComicsUseCase comics is complete `() {
        //given
        coEvery { mockSharedPreferences.isDownloadCompleted() } returns true
        coEvery { mockGetAllComicsUseCase.execute() } returns GetAllComicsUseCase.Result.Success(emptyList())
        coEvery { mockDownloadComicsUseCase.execute() } returns DownloadComicsUseCase.Result.Success(true)

        //when
        viewModel.loadData()

        //then
        viewModel.stateLiveData.value shouldBeEqualTo HomeViewModel.ViewState(
            isLoading=false,
            downloadVisible=true,
            downloadCompleted=true,
            isErrorDownloadL=false
        )


    }

    @Test
    fun ` verify state when DownloadComicsUseCase comics throws IOException `()  {
        //given
        val exception = IOException()
        coEvery { mockGetAllComicsUseCase.execute() } returns GetAllComicsUseCase.Result.Success(emptyList())
        coEvery { mockSharedPreferences.isDownloadCompleted() } returns false
        coEvery { mockDownloadComicsUseCase.execute() } returns DownloadComicsUseCase.Result.Error(exception)
        coEvery { mockGetDownloadPercentageUseCase.execute() } returns GetDownloadPercentageUseCase.Result.Success(0)


        //when
        viewModel.loadData()

        //then
        viewModel.stateLiveData.value shouldBeEqualTo HomeViewModel.ViewState(
            isLoading=false,
            downloadVisible=true,
            downloadCompleted=false,
            downloadErrorMessageId= R.string.loading_data_error_message,
            isErrorDownloadL=true
        )

    }


    @Test
    fun ` verify state when DownloadComicsUseCase comics throws UnknownHostException `()  {
        //given
        val exception = UnknownHostException()
        coEvery { mockGetAllComicsUseCase.execute() } returns GetAllComicsUseCase.Result.Success(emptyList())
        coEvery { mockSharedPreferences.isDownloadCompleted() } returns false
        coEvery { mockDownloadComicsUseCase.execute() } returns DownloadComicsUseCase.Result.Error(exception)
        coEvery { mockGetDownloadPercentageUseCase.execute() } returns GetDownloadPercentageUseCase.Result.Success(0)


        //when
        viewModel.loadData()

        //then
        viewModel.stateLiveData.value shouldBeEqualTo HomeViewModel.ViewState(
            isLoading=false,
            downloadVisible=true,
            downloadCompleted=false,
            downloadErrorMessageId= R.string.no_network_connection_error_message,
            isErrorDownloadL=true
        )

    }
}