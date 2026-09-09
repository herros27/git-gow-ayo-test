package com.kemas.gitgowayo_test.presentation.detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kemas.gitgowayo_test.domain.model.TvShow
import com.kemas.gitgowayo_test.ui.theme.Yellow
import com.kemas.gitgowayo_test.utils.stripHtml


@Composable
fun TvShowDetailScreen(
    onBackClick: () -> Unit,
    viewModel: TvShowDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    TvShowDetailContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onRetry = { viewModel.fetchShowDetail() },
        onShareClick = { show ->
            shareTvShow(context, show)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TvShowDetailContent(
    uiState: TvShowDetailUiState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onShareClick: (TvShow) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Show") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (uiState is TvShowDetailUiState.Success) {
                        IconButton(onClick = {onShareClick(uiState.show)}) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share"
                            )
                        }
                    }
                },
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
        ){
            when(uiState){
                is TvShowDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is TvShowDetailUiState.Error -> {
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

                is TvShowDetailUiState.Success -> {
                    TvShowDetailView(show = uiState.show)
                }
            }
        }
    }
}

@Composable
fun TvShowDetailView(
    show: TvShow,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AsyncImage(
            model = show.imageOriginal.ifEmpty { show.imageMedium },
            contentDescription = show.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = show.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Premiered: ${show.premiered}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating",
                tint = Yellow,
                modifier = Modifier.height(18.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = show.rating?.toString() ?: "N/A",
                style= MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Summary",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = show.summary.stripHtml().ifEmpty { "No Summary Available" },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

private fun shareTvShow(context: Context,show: TvShow){
    val shareContent = """
        📺 ${show.name} 
        
         ${show.summary.stripHtml()}
        
        🔗 Read More : ${show.url}
    """.trimIndent()

    val sentIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, shareContent)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sentIntent, "Share ${show.name}")
    context.startActivity(shareIntent)
}

@Preview(showBackground = true, name = "Detail - Success State")
@Composable
private fun TvShowDetailContentSuccessPreview() {
    val sampleShow = TvShow(
        id = 1,
        name = "Under the Dome",
        summary = "<p><b>Under the Dome</b> is the story of a small town that is suddenly and inexplicably sealed off from the rest of the world by an enormous transparent dome.</p>",
        premiered = "2013-06-24",
        url = "https://www.tvmaze.com/shows/1/under-the-dome",
        imageMedium = "https://static.tvmaze.com/uploads/images/medium_portrait/81/202627.jpg",
        imageOriginal = "https://static.tvmaze.com/uploads/images/original_untouched/81/202627.jpg",
        rating = 6.5
    )
    com.kemas.gitgowayo_test.ui.theme.GitgowayotestTheme {
        TvShowDetailContent(
            uiState = TvShowDetailUiState.Success(sampleShow),
            onBackClick = {},
            onRetry = {},
            onShareClick = {}
        )
    }
}
@Preview(showBackground = true, name = "Detail - Loading State")
@Composable
private fun TvShowDetailContentLoadingPreview() {
    com.kemas.gitgowayo_test.ui.theme.GitgowayotestTheme {
        TvShowDetailContent(
            uiState = TvShowDetailUiState.Loading,
            onBackClick = {},
            onRetry = {},
            onShareClick = {}
        )
    }
}
@Preview(showBackground = true, name = "Detail - Error State")
@Composable
private fun TvShowDetailContentErrorPreview() {
    com.kemas.gitgowayo_test.ui.theme.GitgowayotestTheme {
        TvShowDetailContent(
            uiState = TvShowDetailUiState.Error("Gagal memuat detail film. Periksa koneksi internet Anda."),
            onBackClick = {},
            onRetry = {},
            onShareClick = {}
        )
    }
}