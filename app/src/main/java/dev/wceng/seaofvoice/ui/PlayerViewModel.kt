package dev.wceng.seaofvoice.ui

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import dev.wceng.seaofvoice.player.PlaybackManager
import dev.wceng.seaofvoice.player.PlaybackState
import dev.wceng.seaofvoice.player.SleepTimerManager
import dev.wceng.seaofvoice.data.repository.StationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val playbackManager: PlaybackManager,
    private val stationRepository: StationRepository,
    private val sleepTimerManager: SleepTimerManager
) : ViewModel() {

    val playbackState: StateFlow<PlaybackState> = playbackManager.playbackState
    val sleepTimerRemainingTime: StateFlow<Long?> = sleepTimerManager.remainingTime
    val visualizerMagnitudes: StateFlow<FloatArray> = playbackManager.visualizerMagnitudes

    private val _extractedColors = MutableStateFlow<List<Color>>(emptyList())
    val extractedColors = _extractedColors.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val isFavorite: StateFlow<Boolean> = playbackState
        .map { it.currentStation?.uuid }
        .distinctUntilChanged()
        .flatMapLatest { uuid ->
            if (uuid == null) flowOf(false)
            else stationRepository.isFavorite(uuid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        viewModelScope.launch {
            playbackState
                .map { it.currentStation?.favicon }
                .distinctUntilChanged()
                .collect { favicon ->
                    if (!favicon.isNullOrBlank()) {
                        extractColors(favicon)
                    } else {
                        _extractedColors.value = emptyList()
                    }
                }
        }
    }

    private suspend fun extractColors(url: String) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false) // Required for Palette
            .build()

        val result = loader.execute(request)
        if (result is SuccessResult) {
            val bitmap = (result.drawable as? BitmapDrawable)?.bitmap
            if (bitmap != null) {
                val palette = Palette.from(bitmap).generate()
                val colors = listOfNotNull(
                    palette.vibrantSwatch?.rgb,
                    palette.mutedSwatch?.rgb,
                    palette.dominantSwatch?.rgb,
                    palette.darkVibrantSwatch?.rgb
                ).map { Color(it) }
                _extractedColors.value = colors
            }
        }
    }

    fun togglePlayPause() {
        playbackManager.togglePlayPause()
    }

    fun toggleFavorite() {
        val station = playbackState.value.currentStation ?: return
        val currentFavorite = isFavorite.value
        viewModelScope.launch {
            stationRepository.toggleFavorite(station.uuid, !currentFavorite)
        }
    }

    fun setSleepTimer(minutes: Int) {
        if (minutes == 0) {
            sleepTimerManager.cancelTimer()
        } else {
            sleepTimerManager.setTimer(minutes)
        }
    }
}
