package com.kemas.gitgowayo_test.data.remote

import com.kemas.gitgowayo_test.data.remote.dto.TvShowDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvMazeApi {
    @GET("shows")
    suspend fun getShows(
        @Query("page") page: Int = 0
    ): List<TvShowDto>

    @GET("shows/{id}")
    suspend fun getShowsDetail(
        @Path("id") id: Int
    ): TvShowDto
}