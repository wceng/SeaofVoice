package dev.wceng.seaofvoice.ui.screens.search

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import dev.wceng.seaofvoice.data.model.UserStation
import dev.wceng.seaofvoice.ui.components.StationCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchOverlay(
    showSearchBar: Boolean,
    viewModel: SearchViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()
    val pagingItems = viewModel.searchResults.collectAsLazyPagingItems()
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()

    // Increased blur for a more premium depth effect
    val backgroundBlur by animateDpAsState(
        targetValue = if (isSearchActive) 32.dp else 0.dp,
        animationSpec = tween(600),
        label = "BackgroundBlur"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {
        // 1. App Content with Blur
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(backgroundBlur)
        ) {
            content()
        }

        // 2. Global Top Bar / Search Overlay
        if (showSearchBar) {
            Surface(
                modifier = if (isSearchActive) Modifier.fillMaxSize() else Modifier.fillMaxWidth().wrapContentHeight(),
                // Fix 1: Lower alpha to let the background blur show through clearly
                color = MaterialTheme.colorScheme.surface.copy(
                    alpha = if (isSearchActive) 0.65f else 0.8f 
                ),
                // Fix 2: Higher elevation creates better "Frosted Glass" refraction simulation
                tonalElevation = 12.dp 
            ) {
                Column(
                    modifier = Modifier
                        .then(if (!isSearchActive) Modifier.windowInsetsPadding(WindowInsets.statusBars) else Modifier)
                        .padding(bottom = 8.dp)
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange,
                        onSearch = { 
                            viewModel.onSearch(it)
                            viewModel.onSearchActiveChange(true) 
                        },
                        active = isSearchActive,
                        onActiveChange = viewModel::onSearchActiveChange,
                        placeholder = { Text("搜索电台、流派或国家...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (isSearchActive) {
                                IconButton(onClick = { viewModel.onSearchActiveChange(false) }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close Search")
                                }
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(horizontal = if (isSearchActive) 0.dp else 16.dp)
                            .semantics { traversalIndex = -1f },
                        windowInsets = if (isSearchActive) SearchBarDefaults.windowInsets else WindowInsets(0, 0, 0, 0),
                        colors = SearchBarDefaults.colors(
                            // Brighter input container to separate from the frosted bar
                            containerColor = if (isSearchActive) 
                                Color.Transparent // Let the Surface handle the fullscreen background
                            else 
                                MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp).copy(alpha = 0.9f),
                        )
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (searchQuery.isBlank() && searchHistory.isNotEmpty()) {
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "最近搜索",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        TextButton(onClick = viewModel::clearSearchHistory) {
                                            Text("清空")
                                        }
                                    }
                                }
                                item {
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        searchHistory.forEach { query ->
                                            SuggestionChip(
                                                onClick = { viewModel.onSearchQueryChange(query) },
                                                label = { Text(query) },
                                                icon = {
                                                    Icon(
                                                        Icons.Default.History,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            if (searchQuery.isNotBlank()) {
                                items(
                                    count = pagingItems.itemCount
                                ) { index ->
                                    val station = pagingItems[index]
                                    if (station != null) {
                                        val isFavorite by viewModel.isFavorite(station.uuid).collectAsState(initial = false)
                                        StationCard(
                                            userStation = UserStation(station, isFavorite),
                                            onStationClick = { viewModel.playStation(station) },
                                            onToggleFavorite = viewModel::toggleFavorite
                                        )
                                    }
                                }

                                // Loading and Error states for Paging
                                pagingItems.apply {
                                    when {
                                        loadState.refresh is LoadState.Loading -> {
                                            item {
                                                Box(
                                                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    CircularProgressIndicator()
                                                }
                                            }
                                        }

                                        loadState.append is LoadState.Loading -> {
                                            item {
                                                Box(
                                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                                                }
                                            }
                                        }

                                        loadState.refresh is LoadState.Error -> {
                                            item {
                                                val e = pagingItems.loadState.refresh as LoadState.Error
                                                Text(
                                                    text = "搜索失败: ${e.error.localizedMessage}",
                                                    color = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.padding(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
