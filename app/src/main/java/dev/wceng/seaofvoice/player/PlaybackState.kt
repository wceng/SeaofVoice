package dev.wceng.seaofvoice.player

import dev.wceng.seaofvoice.data.model.Station

data class PlaybackState(
    val currentStation: Station? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val error: String? = null
)
