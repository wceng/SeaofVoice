package dev.wceng.seaofvoice.ui

import androidx.lifecycle.ViewModel
import dev.wceng.seaofvoice.player.PlaybackManager
import dev.wceng.seaofvoice.player.PlaybackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val playbackManager: PlaybackManager
) : ViewModel() {

    val playbackState: StateFlow<PlaybackState> = playbackManager.playbackState

    fun togglePlayPause() {
        playbackManager.togglePlayPause()
    }
}
