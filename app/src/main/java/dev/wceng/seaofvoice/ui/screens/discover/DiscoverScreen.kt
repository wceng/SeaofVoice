package dev.wceng.seaofvoice.ui.screens.discover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.wceng.seaofvoice.data.model.UserStation
import dev.wceng.seaofvoice.ui.components.FeaturedStationCard
import dev.wceng.seaofvoice.ui.components.SearchAreaSpacer
import dev.wceng.seaofvoice.ui.components.StationCard
import dev.wceng.seaofvoice.ui.components.StationImage
import dev.wceng.seaofvoice.ui.components.shimmer

@Composable
fun DiscoverRoute(
    modifier: Modifier = Modifier,
    viewModel: DiscoverViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DiscoverScreen(
        uiState = uiState,
        onPlayStation = viewModel::playStation,
        onToggleFavorite = viewModel::toggleFavorite,
        modifier = modifier
    )
}

@Composable
internal fun DiscoverScreen(
    uiState: DiscoverUiState,
    onPlayStation: (UserStation) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        DiscoverUiState.Loading -> {
            DiscoverSkeleton(modifier = modifier)
        }
        is DiscoverUiState.Success -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                item {
                    SearchAreaSpacer()
                }
                item {
                    DiscoverHeader()
                }

                // 1. 最近常听 (Recent) - Show only if not empty
                if (uiState.recentStations.isNotEmpty()) {
                    item {
                        HorizontalSection(
                            title = "最近常听",
                            stations = uiState.recentStations,
                            onPlayClick = onPlayStation
                        )
                    }
                }

                // 2. 热门推荐 (Featured)
                if (uiState.featuredStations.isNotEmpty()) {
                    item {
                        FeaturedSection(
                            stations = uiState.featuredStations,
                            onPlayClick = onPlayStation
                        )
                    }
                }

                // 3. 本地热门 (Local Top)
                if (uiState.localTopStations.isNotEmpty()) {
                    item {
                        HorizontalSection(
                            title = "本地热门 (${uiState.localCountryName})",
                            stations = uiState.localTopStations,
                            onPlayClick = onPlayStation
                        )
                    }
                }

                // 4. 投票最高 (Top Voted)
                if (uiState.topVotedStations.isNotEmpty()) {
                    item {
                        HorizontalSection(
                            title = "投票排行榜",
                            stations = uiState.topVotedStations,
                            onPlayClick = onPlayStation
                        )
                    }
                }

                // 5. 点击最高 (Top Clicked)
                if (uiState.topClickedStations.isNotEmpty()) {
                    item {
                        HorizontalSection(
                            title = "点击率排行榜",
                            stations = uiState.topClickedStations,
                            onPlayClick = onPlayStation
                        )
                    }
                }

                // 6. 本地发现 (Local Feed) - Vertical list
                if (uiState.localFeedStations.isNotEmpty()) {
                    item {
                        SectionHeader(title = "本地发现")
                    }
                    items(uiState.localFeedStations, key = { it.station.uuid }) { userStation ->
                        StationCard(
                            userStation = userStation,
                            onStationClick = { onPlayStation(userStation) },
                            onToggleFavorite = onToggleFavorite,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
        is DiscoverUiState.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = uiState.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun DiscoverHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "发现新声音",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "精选全球广播，触动你的心灵",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FeaturedSection(
    stations: List<UserStation>,
    onPlayClick: (UserStation) -> Unit
) {
    Column {
        SectionHeader(title = "热门精选")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(stations, key = { it.station.uuid }) { userStation ->
                FeaturedStationCard(
                    userStation = userStation,
                    onStationClick = { onPlayClick(userStation) }
                )
            }
        }
    }
}

@Composable
private fun HorizontalSection(
    title: String,
    stations: List<UserStation>,
    onPlayClick: (UserStation) -> Unit
) {
    Column {
        SectionHeader(title = title)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(stations, key = { it.station.uuid }) { userStation ->
                StationCardSmall(
                    userStation = userStation,
                    onPlayClick = { onPlayClick(userStation) }
                )
            }
        }
    }
}

@Composable
private fun StationCardSmall(
    userStation: UserStation,
    onPlayClick: () -> Unit
) {
    Card(
        onClick = onPlayClick,
        modifier = Modifier.width(160.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            StationImage(
                url = userStation.station.favicon,
                name = userStation.station.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = userStation.station.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = userStation.station.country ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
private fun DiscoverSkeleton(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item { SearchAreaSpacer() }
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(modifier = Modifier.width(180.dp).height(32.dp).clip(MaterialTheme.shapes.small).shimmer())
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.width(240.dp).height(20.dp).clip(MaterialTheme.shapes.small).shimmer())
            }
        }

        // Featured Section Skeleton
        item {
            Column {
                SectionHeaderSkeleton()
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    userScrollEnabled = false
                ) {
                    items(3) {
                        Box(
                            modifier = Modifier
                                .width(280.dp)
                                .height(200.dp)
                                .clip(MaterialTheme.shapes.extraLarge)
                                .shimmer()
                        )
                    }
                }
            }
        }

        // Horizontal Sections Skeletons
        repeat(2) {
            item {
                Column {
                    SectionHeaderSkeleton()
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        userScrollEnabled = false
                    ) {
                        items(4) {
                            Column(modifier = Modifier.width(160.dp)) {
                                Box(modifier = Modifier.fillMaxWidth().height(100.dp).clip(MaterialTheme.shapes.medium).shimmer())
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(modifier = Modifier.width(100.dp).height(16.dp).clip(MaterialTheme.shapes.small).shimmer())
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.width(60.dp).height(12.dp).clip(MaterialTheme.shapes.small).shimmer())
                            }
                        }
                    }
                }
            }
        }

        // Vertical List Skeleton
        item { SectionHeaderSkeleton() }
        items(5) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .height(72.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(56.dp).clip(MaterialTheme.shapes.small).shimmer())
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.fillMaxWidth(0.6f).height(20.dp).clip(MaterialTheme.shapes.small).shimmer())
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth(0.4f).height(16.dp).clip(MaterialTheme.shapes.small).shimmer())
                }
            }
        }
    }
}

@Composable
private fun SectionHeaderSkeleton() {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .width(120.dp)
            .height(28.dp)
            .clip(MaterialTheme.shapes.small)
            .shimmer()
    )
}
