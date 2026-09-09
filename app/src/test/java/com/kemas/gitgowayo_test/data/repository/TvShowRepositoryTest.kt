package com.kemas.gitgowayo_test.data.repository

import com.kemas.gitgowayo_test.data.remote.TvMazeApi
import com.kemas.gitgowayo_test.data.remote.dto.ImageDto
import com.kemas.gitgowayo_test.data.remote.dto.RatingDto
import com.kemas.gitgowayo_test.data.remote.dto.TvShowDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class TvShowRepositoryTest {

    private val api: TvMazeApi = mockk()
    private lateinit var repository: TvShowRepository

    @Before
    fun setUp(){
        repository = TvShowRepository(api)
    }

    @Test
    fun `getShows should fetch from api and map to domain model`() = runTest {
        //Given
        val dtoList = listOf(
            TvShowDto(
                id = 1,
                name = "Breaking Bad",
                summary = "A chemistry teacher turned cook",
                premiered = "2008-01-20",
                url = "https://tvmaze.com/shows/1",
                image = ImageDto(medium = "med.jpg", original = "orig.jpg"),
                rating = RatingDto(average = 9.5)
            )
        )
        coEvery { api.getShows(0) } returns dtoList

        //When
        val result = repository.getShows(0)

        //Then
        coVerify(exactly = 1) {api.getShows(0)}
        assertEquals(1, result.size)
        assertEquals("Breaking Bad", result[0].name)
        assertEquals(9.5, result[0].rating)
    }

    @Test
    fun `getDetailShow should fetch detail from api and map correctly` ()= runTest {
        //Given
        val showId = 1
        val dto = TvShowDto(
            id = showId,
            name = "Breaking Bad",
            summary = "Summary text",
            premiered = "2008-01-20",
            url = "https://tvmaze.com/shows/1",
            image = ImageDto(medium = "med.jpg", original = "orig.jpg"),
            rating = RatingDto(average = 9.5)
        )

        coEvery { api.getShowsDetail(showId) } returns dto

        //When
        val result = repository.getDetailShows(showId)

        //Then
        coVerify(exactly = 1) {api.getShowsDetail(showId)}
        assertEquals(showId, result.id)
        assertEquals("Breaking Bad", result.name)
    }
}