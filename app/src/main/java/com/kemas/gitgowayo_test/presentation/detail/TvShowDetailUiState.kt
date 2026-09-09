package com.kemas.gitgowayo_test.presentation.detail

import com.kemas.gitgowayo_test.domain.model.Cast
import com.kemas.gitgowayo_test.domain.model.Episode
import com.kemas.gitgowayo_test.domain.model.Season
import com.kemas.gitgowayo_test.domain.model.TvShow

sealed interface TvShowDetailUiState {
    data object Loading : TvShowDetailUiState
    data class Success(
        val show: TvShow,
        val cast: List<Cast> = emptyList(),
        val seasons: List<Season> = emptyList(),
        val episodes: List<Episode> = emptyList()
    ) : TvShowDetailUiState
    data class Error(val message: String) : TvShowDetailUiState
}