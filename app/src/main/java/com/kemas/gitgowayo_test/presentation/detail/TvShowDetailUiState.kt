package com.kemas.gitgowayo_test.presentation.detail

import com.kemas.gitgowayo_test.domain.model.TvShow

sealed interface TvShowDetailUiState {
    data object Loading : TvShowDetailUiState
    data class Success(val show: TvShow) : TvShowDetailUiState
    data class Error(val message: String) : TvShowDetailUiState
}