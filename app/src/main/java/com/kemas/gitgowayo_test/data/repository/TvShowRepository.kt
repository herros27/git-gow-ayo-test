package com.kemas.gitgowayo_test.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kemas.gitgowayo_test.data.paging.TvShowPagingSource
import com.kemas.gitgowayo_test.data.remote.TvMazeApi
import com.kemas.gitgowayo_test.domain.model.Cast
import com.kemas.gitgowayo_test.domain.model.Episode
import com.kemas.gitgowayo_test.domain.model.Season
import com.kemas.gitgowayo_test.domain.model.TvShow
import com.kemas.gitgowayo_test.util.toDomain
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TvShowRepository @Inject constructor(
    private val api: TvMazeApi
){
    fun getShowsPager(): Flow<PagingData<TvShow>> {
        return Pager(
            config = PagingConfig(
                pageSize = 250,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TvShowPagingSource(api) }
        ).flow
    }

    suspend fun getShows(page: Int = 0) : List<TvShow> {
        return api.getShows(page).map { it.toDomain() }
    }

    suspend fun getDetailShows(id: Int) : TvShow {
        return api.getShowsDetail(id).toDomain()
    }

    suspend fun getShowCast(id: Int): List<Cast> {
        return api.getShowCast(id).map { it.toDomain() }
    }
    suspend fun getShowSeasons(id: Int): List<Season> {
        return api.getShowSeasons(id).map { it.toDomain() }
    }

    suspend fun getShowEpisodes(id: Int): List<Episode> {
        return api.getShowEpisodes(id).map { it.toDomain() }
    }
}