package dev.wceng.seaofvoice.ui.screens.category_detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.wceng.seaofvoice.data.model.UserStation
import dev.wceng.seaofvoice.ui.components.StationCard

@Composable
fun CategoryDetailRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoryDetailViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CategoryDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onPlayStation = viewModel::playStation,
        onToggleFavorite = viewModel::toggleFavorite,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CategoryDetailScreen(
    uiState: CategoryDetailUiState,
    onBackClick: () -> Unit,
    onPlayStation: (UserStation) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState is CategoryDetailUiState.Success) uiState.categoryName else "电台列表",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                CategoryDetailUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is CategoryDetailUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 120.dp)
                    ) {
                        items(uiState.stations, key = { it.station.uuid }) { userStation ->
                            StationCard(
                                userStation = userStation,
                                onStationClick = { onPlayStation(userStation) },
                                onToggleFavorite = onToggleFavorite,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                is CategoryDetailUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = uiState.message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
