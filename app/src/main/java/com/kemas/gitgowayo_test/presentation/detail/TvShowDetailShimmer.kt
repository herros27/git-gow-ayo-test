package com.kemas.gitgowayo_test.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.kemas.gitgowayo_test.ui.theme.shimmerBrush

@Composable
fun TvShowDetailShimmer(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ── Big Poster (16:9) ──────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp))
                .background(brush)
        )
        Spacer(modifier = Modifier.height(16.dp))
        // ── Title ──────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(brush)
        )
        Spacer(modifier = Modifier.height(8.dp))
        // ── Info row: "Premiered: xxx" + Star + "x.x" ─────────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(130.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.width(16.dp))
            // Star icon placeholder
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.width(4.dp))
            // Rating value placeholder
            Box(
                modifier = Modifier
                    .width(30.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        // ── Summary label ──────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(18.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )
        Spacer(modifier = Modifier.height(6.dp))
        // ── Summary body (4 lines, last line shorter) ──────────────────────
        repeat(4) { i ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (i == 3) 0.55f else 1f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
        // ── Cast Section ───────────────────────────────────────────────────
        Spacer(modifier = Modifier.height(18.dp))
        // "Cast (x)" label
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(18.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(6) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(80.dp)
                ) {
                    // Avatar circle
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(brush)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Actor name
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Character name
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush)
                    )
                }
            }
        }
        // ── Seasons Section ────────────────────────────────────────────────
        Spacer(modifier = Modifier.height(24.dp))
        // "Seasons (x)" label
        Box(
            modifier = Modifier
                .width(110.dp)
                .height(18.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )
        Spacer(modifier = Modifier.height(8.dp))
        // SuggestionChip row
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(5) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(50.dp)) // chip shape
                        .background(brush)
                )
            }
        }
        // ── Episodes Section ───────────────────────────────────────────────
        Spacer(modifier = Modifier.height(24.dp))
        // "Episodes (x)" label
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(18.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Episode cards (10 item, sama dengan default visibleEpisodeCount)
        repeat(10) {
            // Mirrors EpisodeItem layout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
