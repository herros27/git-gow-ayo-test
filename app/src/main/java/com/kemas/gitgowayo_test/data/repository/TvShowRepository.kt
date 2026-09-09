package com.kemas.gitgowayo_test.data.repository

import com.kemas.gitgowayo_test.data.remote.TvMazeApi
import com.kemas.gitgowayo_test.domain.model.TvShow
import com.kemas.gitgowayo_test.utils.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TvShowRepository @Inject constructor(
    private val api: TvMazeApi
){
    suspend fun getShows(page: Int = 0) : List<TvShow> {
        return api.getShows(page).map { it.toDomain() }
    }

    suspend fun getDetailShows(id: Int) : TvShow {
        return api.getShowsDetail(id).toDomain()
    }
}