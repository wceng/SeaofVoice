package dev.wceng.seaofvoice.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.wceng.seaofvoice.player.PlaybackState

@Composable
fun FullPlayerContent(
    playbackState: PlaybackState,
    isFavorite: Boolean,
    remainingTime: Long?,
    atmosphereColors: List<Color>,
    magnitudes: FloatArray,
    onTogglePlayPause: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSleepTimerClick: () -> Unit,
    onBackClick: () -> Unit,
    onIconClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val station = playbackState.currentStation ?: return

    // Spring animation for play button interaction
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 400f),
        label = "PlayButtonScale"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Atmosphere Layer
        AtmosphereBackground(colors = atmosphereColors)

        // 2. Content Layer
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Close button at top
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Close",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Large Artwork - Clean and static with Shimmer and Letter fallback
            Surface(
                modifier = Modifier
                    .size(280.dp)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .clickable(
                        enabled = !station.homepage.isNullOrBlank(),
                        onClick = { station.homepage?.let { onIconClick(it) } }
                    ),
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 8.dp,
                color = Color.Transparent
            ) {
                StationImage(
                    url = station.favicon,
                    name = station.name,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Title and Info
            Text(
                text = station.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = station.country ?: "Unknown",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            station.codec?.let {
                Text(
                    text = "$it • ${station.bitrate} kbps",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Linear Visualizer (ECG Style) between Info and Controls
            LinearVisualizer(
                magnitudes = magnitudes,
                color = atmosphereColors.firstOrNull() ?: MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .fillMaxWidth(0.8f)
            )

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Favorite Button
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        modifier = Modifier.size(32.dp),
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    )
                }

                // Main Play/Pause
                if (playbackState.isBuffering) {
                    CircularProgressIndicator(modifier = Modifier.size(64.dp))
                } else {
                    IconButton(
                        onClick = onTogglePlayPause,
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .size(96.dp)
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                    ) {
                        Icon(
                            imageVector = if (playbackState.isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Sleep Timer Button
                Box(contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = onSleepTimerClick,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.HourglassEmpty,
                                contentDescription = "Sleep Timer",
                                modifier = Modifier.size(32.dp),
                                tint = if (remainingTime != null) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            )
                        }

                        if (remainingTime != null) {
                            val minutes = (remainingTime / 1000 / 60).toInt()
                            val seconds = (remainingTime / 1000 % 60).toInt()
                            Text(
                                text = "%02d:%02d".format(minutes, seconds),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                        MaterialTheme.shapes.extraSmall
                                    )
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
