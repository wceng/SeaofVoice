package dev.wceng.seaofvoice.player

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepTimerManager @Inject constructor(
    private val playbackManager: PlaybackManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var timerJob: Job? = null

    private val _remainingTime = MutableStateFlow<Long?>(null) // milliseconds
    val remainingTime = _remainingTime.asStateFlow()

    fun setTimer(minutes: Int) {
        timerJob?.cancel()
        if (minutes <= 0) {
            _remainingTime.value = null
            return
        }

        val endTime = System.currentTimeMillis() + minutes * 60 * 1000L
        _remainingTime.value = minutes * 60 * 1000L

        timerJob = scope.launch {
            while (System.currentTimeMillis() < endTime) {
                delay(1000)
                _remainingTime.value = (endTime - System.currentTimeMillis()).coerceAtLeast(0)
            }
            playbackManager.stopPlayback()
            _remainingTime.value = null
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        _remainingTime.value = null
    }
}
