package com.kemas.gitgowayo_test.presentation.list

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kemas.gitgowayo_test.domain.model.TvShow
import com.kemas.gitgowayo_test.presentation.list.components.TvShowItem
import com.kemas.gitgowayo_test.ui.theme.GitgowayotestTheme

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TvShowListScreen(
    onShowClick: (Int) -> Unit,
    viewModel: TvShowListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Oper state dan event ke fungsi Content di bawah
    TvShowListContent(
        uiState = uiState,
        onShowClick = onShowClick,
        onRetry = { viewModel.fetchShows() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvShowListContent(
    uiState: TvShowListUiState,
    onShowClick: (Int) -> Unit,
    onRetry: () -> Unit,
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
            when (uiState) {
                is TvShowListUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is TvShowListUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = uiState.message,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onRetry) {
                            Text("Retry")
                        }
                    }
                }
                is TvShowListUiState.Success -> {
                    TvShowGrid(
                        shows = uiState.shows,
                        onShowClick = onShowClick
                    )
                }
            }
        }
    }
}

@Composable
fun TvShowGrid(
    shows: List<TvShow>,
    onShowClick: (Int) -> Unit,
    modifier: Modifier = Modifier
){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement= Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(shows, key = {it.id}){ show ->
            TvShowItem(
                show = show,
                onClick = {onShowClick(show.id)}
            )
        }
    }

}

// 1. Preview Tampilan Berhasil (Success)
@Preview(showBackground = true, name = "Success State")
@Composable
fun TvShowListContentSuccessPreview() {
    val dummyShows = listOf(
        TvShow(
            id = 1,
            name = "Under the Dome",
            summary = "Sample summary 1",
            premiered = "2013-06-24",
            url = "",
            imageMedium = "https://static.tvmaze.com/uploads/images/medium_portrait/81/202627.jpg",
            imageOriginal = "",
            rating = 6.5
        ),
        TvShow(
            id = 2,
            name = "Person of Interest",
            summary = "Sample summary 2",
            premiered = "2011-09-22",
            url = "",
            imageMedium = "https://static.tvmaze.com/uploads/images/medium_portrait/163/407679.jpg",
            imageOriginal = "",
            rating = 8.8
        )
    )

    GitgowayotestTheme {
        TvShowListContent(
            uiState = TvShowListUiState.Success(dummyShows),
            onShowClick = {},
            onRetry = {}
        )
    }
}

// 2. Preview Tampilan Error
@Preview(showBackground = true, name = "Error State")
@Composable
fun TvShowListContentErrorPreview() {
    GitgowayotestTheme {
        TvShowListContent(
            uiState = TvShowListUiState.Error("Failed to connect to server"),
            onShowClick = {},
            onRetry = {}
        )
    }
}