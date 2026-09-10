package com.kemas.gitgowayo_test.presentation.list

import androidx.paging.PagingData
import app.cash.turbine.test
import com.kemas.gitgowayo_test.data.repository.TvShowRepository
import com.kemas.gitgowayo_test.domain.model.TvShow
import com.kemas.gitgowayo_test.util.MainDispatcherRule
import com.kemas.gitgowayo_test.util.collectDataForTest
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class TvShowListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: TvShowRepository = mockk()

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
    fun `showsPagingFlow should emit PagingData`() = runTest {

        // Given
        val pagingData = PagingData.from(dummyShows)

        every {
            repository.getShowsPager()
        } returns flowOf(pagingData)

        // When
        val viewModel = TvShowListViewModel(repository)

        // Then
        viewModel.showsPagingFlow.test {

            val result = awaitItem()
            val items = result.collectDataForTest(
                mainDispatcherRule.testDispatcher
            )

            // PagingData successfully received.
            assertEquals(dummyShows, items)
        }
    }
}