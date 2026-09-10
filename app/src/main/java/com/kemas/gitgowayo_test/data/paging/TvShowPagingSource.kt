package com.kemas.gitgowayo_test.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kemas.gitgowayo_test.data.remote.TvMazeApi
import com.kemas.gitgowayo_test.domain.model.TvShow
import com.kemas.gitgowayo_test.util.toDomain
import retrofit2.HttpException
import java.io.IOException

class TvShowPagingSource(
    private val api: TvMazeApi
) : PagingSource<Int, TvShow>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TvShow> {
        val position = params.key ?: 0

        return try {
            val response = api.getShows(page= position)
            val shows = response.map { it.toDomain() }



            val nextKey = if (shows.isEmpty()) {
                null
            } else {
                position + 1
            }

            LoadResult.Page(
                data = shows,
                prevKey = if (position == 0) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {

            LoadResult.Error(exception)

        } catch (exception: HttpException) {

            // TVMaze mengembalikan HTTP 404 jika halaman sudah habis
            if (exception.code() == 404) {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = if (position == 0) null else position - 1,
                    nextKey = null
                )
            } else {
                LoadResult.Error(exception)
            }
        }
    }
    override fun getRefreshKey(state: PagingState<Int, TvShow>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}