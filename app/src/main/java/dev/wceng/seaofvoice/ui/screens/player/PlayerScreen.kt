package dev.wceng.seaofvoice.ui.screens.player

import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.wceng.seaofvoice.ui.PlayerViewModel
import dev.wceng.seaofvoice.ui.components.FullPlayerContent
import dev.wceng.seaofvoice.ui.components.SleepTimerDialog

@Composable
fun PlayerRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val remainingTime by viewModel.sleepTimerRemainingTime.collectAsStateWithLifecycle()
    val atmosphereColors by viewModel.extractedColors.collectAsStateWithLifecycle()
    val magnitudes by viewModel.visualizerMagnitudes.collectAsStateWithLifecycle()

    var showSleepTimerDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        FullPlayerContent(
            playbackState = playbackState,
            isFavorite = isFavorite,
            remainingTime = remainingTime,
            atmosphereColors = atmosphereColors,
            magnitudes = magnitudes,
            onTogglePlayPause = viewModel::togglePlayPause,
            onToggleFavorite = viewModel::toggleFavorite,
            onSleepTimerClick = { showSleepTimerDialog = true },
            onBackClick = onBackClick,
            onIconClick = { url ->
                try {
                    val primaryColor = atmosphereColors.firstOrNull()?.toArgb()
                    val customTabsIntent = CustomTabsIntent.Builder().apply {
                        primaryColor?.let {
                            val params = CustomTabColorSchemeParams.Builder()
                                .setToolbarColor(it)
                                .build()
                            setDefaultColorSchemeParams(params)
                        }
                        setShareState(CustomTabsIntent.SHARE_STATE_ON)
                        setShowTitle(true)
                    }.build()
                    
                    customTabsIntent.launchUrl(context, url.toUri())
                } catch (_: Exception) {
                    // Fallback or ignore
                }
            }
        )

        if (showSleepTimerDialog) {
            SleepTimerDialog(
                onDismiss = { showSleepTimerDialog = false },
                onMinutesSelected = viewModel::setSleepTimer
            )
        }
    }
}
