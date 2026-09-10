package com.kemas.gitgowayo_test.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kemas.gitgowayo_test.data.repository.TvShowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvShowDetailViewModel @Inject constructor(
    val repository : TvShowRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val showId: Int = checkNotNull(savedStateHandle["showId"])

    private val _uiState = MutableStateFlow<TvShowDetailUiState>(TvShowDetailUiState.Loading)
    val uiState: StateFlow<TvShowDetailUiState> = _uiState.asStateFlow()

    init {
        fetchShowDetail()
    }

    fun fetchShowDetail() {
        viewModelScope.launch {
            _uiState.value = TvShowDetailUiState.Loading
            try {
                coroutineScope {
                    // Fetch data in parallel using async.
                    val showDeferred = async { repository.getDetailShows(showId) }
                    val castDeferred = async { repository.getShowCast(showId) }
                    val seasonsDeferred = async { repository.getShowSeasons(showId) }
                    val episodesDeferred = async { repository.getShowEpisodes(showId) }
                    _uiState.value = TvShowDetailUiState.Success(
                        show = showDeferred.await(),
                        cast = castDeferred.await(),
                        seasons = seasonsDeferred.await(),
                        episodes = episodesDeferred.await()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = TvShowDetailUiState.Error(
                    message = e.localizedMessage ?: "Failed to load movie details"
                )
            }
        }
    }
}