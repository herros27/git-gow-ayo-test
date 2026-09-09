package com.kemas.gitgowayo_test.presentation.list

import app.cash.turbine.test
import com.kemas.gitgowayo_test.data.repository.TvShowRepository
import com.kemas.gitgowayo_test.domain.model.TvShow
import com.kemas.gitgowayo_test.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class TvShowListViewModelTest {

    @get:Rule
    val mainDispatcherRule= MainDispatcherRule()

    private val repository: TvShowRepository = mockk()
    private lateinit var viewModel: TvShowListViewModel

    private val dummyShows = listOf(
        TvShow(
            id = 1,
            name = "Under the Dome",
            summary = "A thrilling drama",
            premiered = "2013-06-24",
            url = "https://tvmaze.com/shows/1",
            imageMedium = "https://image.com/medium.jpg",
            imageOriginal = "https://image.com/original.jpg",
            rating = 6.5
        )
    )

    @Test
    fun `fetchShows success should emit Success state with data` () = runTest {
        //Given
        coEvery { repository.getShows(0) } returns dummyShows

        //When
        viewModel = TvShowListViewModel(repository)

        //Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is TvShowListUiState.Success)
            assertEquals(dummyShows, (state as TvShowListUiState.Success).shows)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetchShows failure should emit Error state with error message` () = runTest {
        //Given
        val errorMessage = "Network Error"
        coEvery { repository.getShows(0) } throws RuntimeException(errorMessage)

        //When
        viewModel = TvShowListViewModel(repository)

        //Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is TvShowListUiState.Error)
            assertEquals(errorMessage, (state as TvShowListUiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }
}