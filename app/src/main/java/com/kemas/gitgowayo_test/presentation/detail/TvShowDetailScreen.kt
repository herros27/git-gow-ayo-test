package com.kemas.gitgowayo_test.presentation.detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kemas.gitgowayo_test.domain.model.Cast
import com.kemas.gitgowayo_test.domain.model.Episode
import com.kemas.gitgowayo_test.domain.model.Season
import com.kemas.gitgowayo_test.domain.model.TvShow
import com.kemas.gitgowayo_test.presentation.list.components.TvShowItemShimmer
import com.kemas.gitgowayo_test.ui.theme.GitgowayotestTheme
import com.kemas.gitgowayo_test.ui.theme.Yellow
import com.kemas.gitgowayo_test.util.stripHtml
import com.kemas.gitgowayo_test.util.toAnnotatedString


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
                        IconButton(onClick = { onShareClick(uiState.show) }) {
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
        ) {
            when (uiState) {
                is TvShowDetailUiState.Loading -> {
//                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    TvShowDetailShimmer(modifier = Modifier.fillMaxSize())
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
                    TvShowDetailView(
                        show = uiState.show,
                        cast = uiState.cast,
                        seasons = uiState.seasons,
                        episodes = uiState.episodes
                    )
                }
            }
        }
    }
}
@Composable
fun TvShowDetailView(
    show: TvShow,
    cast: List<Cast>,
    seasons: List<Season>,
    episodes: List<Episode>,
    modifier: Modifier = Modifier
) {
    var visibleEpisodeCount by remember {
        mutableIntStateOf(10)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Big Poster
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
        // Title
        Text(
            text = show.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Info: Premiered & Rating
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Premiered: ${show.premiered}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating",
                tint = Yellow,
                modifier = Modifier.height(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = show.rating?.toString() ?: "N/A",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Summary Section
        Text(
            text = "Summary",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (show.summary.isNotBlank()) {
                show.summary.toAnnotatedString()
            } else {
                AnnotatedString("No summary available.")
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Cast Section
        if (cast.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Cast (${cast.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(
                    count = cast.size,
                    key = { index -> "${cast[index].id}_$index" }
                ) { index ->
                    val actor = cast[index]

                    CastItem(actor = actor)
                }
            }
        }

        // Seasons Section
        if (seasons.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Seasons (${seasons.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    count = seasons.size,
                    key = { index -> "${seasons[index].id}_$index" }
                ) { index ->
                    val season = seasons[index]

                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                "Season ${season.number} (${season.episodeCount} eps)"
                            )
                        }
                    )
                }
            }
        }

        // Episodes Section
        if (episodes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Episodes (${episodes.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            episodes
                .take(visibleEpisodeCount)
                .forEach { episode ->
                    EpisodeItem(episode = episode)
                    Spacer(modifier = Modifier.height(8.dp))
                }

            // Load more episodes
            if (visibleEpisodeCount < episodes.size) {
                val remainingEpisodes = episodes.size - visibleEpisodeCount

                Button(
                    onClick = {
                        visibleEpisodeCount = minOf(
                            visibleEpisodeCount + 10,
                            episodes.size
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Load More Episodes",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "$remainingEpisodes episodes remaining",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }


            }
        }
    }
}
@Composable
fun CastItem(actor: Cast) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        AsyncImage(
            model = actor.imageUrl,
            contentDescription = actor.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = actor.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            text = actor.characterName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
@Composable
fun EpisodeItem(episode: Episode) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "S${episode.season} E${episode.number}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = episode.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (episode.rating != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Yellow,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${episode.rating}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
private fun shareTvShow(context: Context, show: TvShow) {
    val shareContent = """
        📺 ${show.name}
        
        ${show.summary.toAnnotatedString()}
        
        🔗 Read more: ${show.url}
    """.trimIndent()
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, shareContent)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share ${show.name}")
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
    GitgowayotestTheme {
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
    GitgowayotestTheme {
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
    GitgowayotestTheme {
        TvShowDetailContent(
            uiState = TvShowDetailUiState.Error("Gagal memuat detail film. Periksa koneksi internet Anda."),
            onBackClick = {},
            onRetry = {},
            onShareClick = {}
        )
    }
}