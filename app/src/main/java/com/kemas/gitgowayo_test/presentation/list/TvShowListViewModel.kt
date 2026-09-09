package com.kemas.gitgowayo_test.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kemas.gitgowayo_test.data.repository.TvShowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvShowListViewModel @Inject constructor(
    private val repository : TvShowRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow<TvShowListUiState>(TvShowListUiState.Loading)
    val uiState : StateFlow<TvShowListUiState> = _uiState.asStateFlow()

    init {
        fetchShows()
    }

    fun fetchShows() {
        viewModelScope.launch {
            _uiState.value = TvShowListUiState.Loading
            try{
                val shows = repository.getShows()
                _uiState.value = TvShowListUiState.Success(shows)
            } catch(e : Exception){
                _uiState.value = TvShowListUiState.Error(
                    message = e.localizedMessage ?: "An Unexpected Error Occurred"
                )
            }
        }
    }
}