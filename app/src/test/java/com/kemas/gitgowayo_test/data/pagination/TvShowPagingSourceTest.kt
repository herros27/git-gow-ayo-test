package com.kemas.gitgowayo_test.data.pagination

import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import com.kemas.gitgowayo_test.data.paging.TvShowPagingSource
import com.kemas.gitgowayo_test.data.remote.TvMazeApi
import com.kemas.gitgowayo_test.data.remote.dto.TvShowDto
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

class TvShowPagingSourceTest {

    private lateinit var api: TvMazeApi
    private lateinit var pagingSource: TvShowPagingSource

    @Before
    fun setup() {
        api = mockk()
        pagingSource = TvShowPagingSource(api)
    }

    @Test
    fun `load page 0 success should return data and next key 1`() = runTest {

        // Given
        val response = listOf(
            mockk<TvShowDto>(relaxed = true),
            mockk<TvShowDto>(relaxed = true)
        )

        coEvery {
            api.getShows(page = 0)
        } returns response

        val params = LoadParams.Refresh<Int>(
            key = null,
            loadSize = 250,
            placeholdersEnabled = false
        )

        // When
        val result = pagingSource.load(params)

        // Then
        assertTrue(result is LoadResult.Page)

        result as LoadResult.Page

        assertEquals(2, result.data.size)
        assertEquals(null, result.prevKey)
        assertEquals(1, result.nextKey)
    }

    @Test
    fun `load page 1 should return success with correct previous and next key`() =
        runTest {

            // Given
            val response = listOf(
                mockTvShowDto(id = 1),
                mockTvShowDto(id = 2)
            )

            coEvery {
                api.getShows(page = 1)
            } returns response

            val params = LoadParams.Append(
                key = 1,
                loadSize = 250,
                placeholdersEnabled = false
            )

            // When
            val result = pagingSource.load(params)

            // Then
            assertTrue(result is LoadResult.Page)

            result as LoadResult.Page

            assertEquals(2, result.data.size)
            assertEquals(0, result.prevKey)
            assertEquals(2, result.nextKey)
        }

    @Test
    fun `load empty response should return page with null next key`() =
        runTest {

            // Given
            coEvery {
                api.getShows(page = 0)
            } returns emptyList()

            val params = LoadParams.Refresh<Int>(
                key = null,
                loadSize = 250,
                placeholdersEnabled = false
            )

            // When
            val result = pagingSource.load(params)

            // Then
            assertTrue(result is LoadResult.Page)

            result as LoadResult.Page

            assertTrue(result.data.isEmpty())
            assertEquals(null, result.prevKey)
            assertEquals(null, result.nextKey)
        }

    @Test
    fun `load should return error when network throws IOException`() =
        runTest {

            // Given
            val exception = IOException("Network error")

            coEvery {
                api.getShows(page = 0)
            } throws exception

            val params = LoadParams.Refresh<Int>(
                key = null,
                loadSize = 250,
                placeholdersEnabled = false
            )

            // When
            val result = pagingSource.load(params)

            // Then
            assertTrue(result is LoadResult.Error)

            result as LoadResult.Error

            assertEquals(exception, result.throwable)
        }

    @Test
    fun `load should return empty page when API returns 404`() =
        runTest {

            // Given
            val exception = mockk<HttpException>()

            every {
                exception.code()
            } returns 404

            coEvery {
                api.getShows(page = 1)
            } throws exception

            val params = LoadParams.Append<Int>(
                key = 1,
                loadSize = 250,
                placeholdersEnabled = false
            )

            // When
            val result = pagingSource.load(params)

            // Then
            assertTrue(result is LoadResult.Page)

            result as LoadResult.Page

            assertTrue(result.data.isEmpty())
            assertEquals(0, result.prevKey)
            assertEquals(null, result.nextKey)
        }

    private fun mockTvShowDto(id: Int): TvShowDto {
        return mockk {
            every { this@mockk.id } returns id
            every { name } returns "Show $id"
            every { summary } returns "Summary $id"
            every { premiered } returns "2024-01-01"
            every { url } returns "https://tvmaze.com/shows/$id"
            every { image?.medium } returns "https://image.com/$id.jpg"
            every { image?.original } returns "https://image.com/$id-original.jpg"
            every { rating?.average } returns 8.0
        }
    }
}