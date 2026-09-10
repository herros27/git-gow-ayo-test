package com.kemas.gitgowayo_test.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kemas.gitgowayo_test.data.repository.TvShowRepository
import com.kemas.gitgowayo_test.domain.model.TvShow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TvShowListViewModel @Inject constructor(
    repository : TvShowRepository
) : ViewModel(){

    // Paginated TV show data flow, cached within the ViewModel scope
    val showsPagingFlow: Flow<PagingData<TvShow>> =
        repository.getShowsPager().cachedIn(viewModelScope)
}