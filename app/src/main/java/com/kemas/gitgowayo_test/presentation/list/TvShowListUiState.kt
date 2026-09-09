package com.kemas.gitgowayo_test.presentation.list

import com.kemas.gitgowayo_test.domain.model.TvShow

sealed interface TvShowListUiState {
    data object Loading : TvShowListUiState
    data class Success(val shows: List<TvShow>) : TvShowListUiState
    data class Error(val message: String) : TvShowListUiState
}