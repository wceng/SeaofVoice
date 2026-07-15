package dev.wceng.seaofvoice.ui.screens.browse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.wceng.seaofvoice.data.model.Category
import dev.wceng.seaofvoice.data.model.CategoryType
import dev.wceng.seaofvoice.ui.components.SearchAreaSpacer

@Composable
fun BrowseRoute(
    onCategoryClick: (Category) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BrowseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BrowseScreen(
        uiState = uiState,
        onCategoryClick = onCategoryClick,
        modifier = modifier
    )
}

@Composable
internal fun BrowseScreen(
    uiState: BrowseUiState,
    onCategoryClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            BrowseUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is BrowseUiState.Success -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        bottom = 120.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp
                ) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        SearchAreaSpacer()
                    }
                    item(span = StaggeredGridItemSpan.FullLine) {
                        SectionHeader(title = "国家 / 地区")
                    }
                    item(span = StaggeredGridItemSpan.FullLine) {
                        HorizontalCategoryGrid(
                            categories = uiState.countries,
                            onCategoryClick = onCategoryClick
                        )
                    }

                    item(span = StaggeredGridItemSpan.FullLine) {
                        SectionHeader(title = "语言")
                    }
                    item(span = StaggeredGridItemSpan.FullLine) {
                        HorizontalCategoryGrid(
                            categories = uiState.languages,
                            onCategoryClick = onCategoryClick
                        )
                    }

                    item(span = StaggeredGridItemSpan.FullLine) {
                        SectionHeader(title = "热门标签")
                    }
                    items(uiState.tags) { tag ->
                        TagCard(category = tag, onClick = { onCategoryClick(tag) })
                    }
                }
            }

            is BrowseUiState.Error -> {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun HorizontalCategoryGrid(
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            CategoryGridItem(category, onCategoryClick)
        }
    }
}

@Composable
private fun CategoryGridItem(
    category: Category,
    onCategoryClick: (Category) -> Unit
) {
    Surface(
        modifier = Modifier
            .width(180.dp)
            .fillMaxHeight()
            .clickable { onCategoryClick(category) },
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val context = LocalContext.current
            val flagResId = if (category.type == CategoryType.Country || category.type == CategoryType.Language) {
                remember(category.isoCode) {
                    if (category.isoCode != null) {
                        context.resources.getIdentifier(
                            "ic_${category.isoCode}_flag",
                            "drawable",
                            context.packageName
                        )
                    } else 0
                }
            } else 0

            if (flagResId != 0) {
                Icon(
                    painter = painterResource(id = flagResId),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
            } else {
                val icon = Icons.Default.Language
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${category.stationCount} 电台",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TagCard(
    category: Category,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "# ${category.name}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${category.stationCount} 个频道",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
