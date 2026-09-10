package com.kemas.gitgowayo_test.presentation.list

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.kemas.gitgowayo_test.domain.model.TvShow
import com.kemas.gitgowayo_test.presentation.list.components.TvShowItem
import com.kemas.gitgowayo_test.presentation.list.components.TvShowItemShimmer

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TvShowListScreen(
    onShowClick: (Int) -> Unit,
    viewModel: TvShowListViewModel = hiltViewModel()
) {
    val shows = viewModel.showsPagingFlow.collectAsLazyPagingItems()

    TvShowListContent(
        shows = shows,
        onShowClick = onShowClick
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TvShowListContent(
    shows: LazyPagingItems<TvShow>,
    onShowClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TV Shows") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val refreshState = shows.loadState.refresh) {
                is LoadState.Loading -> {
//                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(6) { // tampilkan 6 skeleton card
                            TvShowItemShimmer()
                        }
                    }
                }
                is LoadState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = refreshState.error.localizedMessage ?: "Terjadi kesalahan koneksi",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = { shows.retry() }) {
                            Text("Retry")
                        }
                    }
                }
                is LoadState.NotLoading -> {
                    TvShowPagingGrid(
                        shows = shows,
                        onShowClick = onShowClick
                    )
                }
            }
        }
    }
}


@Composable
fun TvShowPagingGrid(
    shows: LazyPagingItems<TvShow>,
    onShowClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(
            count = shows.itemCount,
            key = shows.itemKey { it.id }
        ) { index ->
            val show = shows[index]
            if (show != null) {
                TvShowItem(
                    show = show,
                    onClick = { onShowClick(show.id) }
                )
            }
        }
        // Loading indicator at the bottom during scrolling (Append Loading)
        if (shows.loadState.append is LoadState.Loading) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
//                    CircularProgressIndicator()
                    TvShowItemShimmer()
                }
            }
        }
        // "Error & Retry" at the bottom when scrolling fails (Append Error)
        if (shows.loadState.append is LoadState.Error) {
            item(span = { GridItemSpan(2) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Gagal memuat halaman berikutnya",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { shows.retry() }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}