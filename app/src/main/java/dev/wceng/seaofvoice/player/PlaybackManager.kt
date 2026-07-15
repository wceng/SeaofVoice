package dev.wceng.seaofvoice.player

import android.content.ComponentName
import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.wceng.seaofvoice.data.datastore.UserPreferencesDataSource
import dev.wceng.seaofvoice.data.model.Station
import dev.wceng.seaofvoice.data.repository.StationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: StationRepository,
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val visualizerManager: VisualizerManager,
    private val json: Json
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var controller: MediaController? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState = _playbackState.asStateFlow()
    val visualizerMagnitudes = visualizerManager.magnitudes

    private var lastPauseTimestamp: Long = 0

    init {
        scope.launch {
            val sessionToken = SessionToken(context, ComponentName(context, MediaService::class.java))
            val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
            controller = try {
                controllerFuture.await()
            } catch (_: Exception) {
                null
            }
            controller?.addListener(playerListener)
            updateState()

            // Auto-play logic on start
            val prefs = userPreferencesDataSource.userPreferencesFlow.first()
            if (prefs.autoPlayOnStart) {
                prefs.lastPlayedStationId?.let { uuid ->
                    val lastStation = repository.getStationByUuid(uuid).firstOrNull()
                    lastStation?.let { userStation ->
                        playStation(userStation.station)
                    }
                }
            }
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (!isPlaying) {
                lastPauseTimestamp = System.currentTimeMillis()
            }
            updateState()
        }

        override fun onPlaybackStateChanged(state: Int) {
            updateState()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateState()
        }

        override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
            updateState()
        }
    }

    private fun updateState() {
        val player = controller ?: return
        _playbackState.update { 
            it.copy(
                isPlaying = player.isPlaying,
                isBuffering = player.playbackState == Player.STATE_BUFFERING,
                currentStation = player.currentMediaItem?.let { item -> toStation(item) },
                error = player.playerError?.message
            )
        }
    }

    fun playStation(station: Station) {
        scope.launch {
            val player = controller ?: return@launch
            
            // Resolve URL and increment click count via API
            val resolvedUrl = try {
                repository.getResolvedUrl(station.uuid)
            } catch (_: Exception) {
                station.url // Fallback to original URL
            }

            val mediaItem = MediaItem.Builder()
                .setMediaId(station.uuid)
                .setUri(resolvedUrl)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(station.name)
                        .setArtworkUri(station.favicon?.toUri())
                        .setExtras(android.os.Bundle().apply {
                            putString("station_json", json.encodeToString(station))
                        })
                        .build()
                )
                .build()
            
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()

            repository.markStationAsPlayed(station.uuid)
            userPreferencesDataSource.setLastPlayedStationId(station.uuid)
        }
    }

    fun togglePlayPause() {
        val player = controller ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            val currentStation = _playbackState.value.currentStation
            val pauseDuration = System.currentTimeMillis() - lastPauseTimestamp
            if (currentStation != null && lastPauseTimestamp > 0 && pauseDuration > 5 * 1000L) {
                // Re-resolve URL and reload if paused for more than 5 minutes
                playStation(currentStation)
            } else {
                player.play()
            }
        }
    }

    fun stopPlayback() {
        controller?.pause()
    }

    private fun toStation(mediaItem: MediaItem): Station? {
        val jsonString = mediaItem.mediaMetadata.extras?.getString("station_json") ?: return null
        return try {
            json.decodeFromString<Station>(jsonString)
        } catch (_: Exception) {
            null
        }
    }
}
