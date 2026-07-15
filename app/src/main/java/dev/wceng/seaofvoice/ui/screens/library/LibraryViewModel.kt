package dev.wceng.seaofvoice.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.wceng.seaofvoice.data.model.UserStation
import dev.wceng.seaofvoice.data.repository.StationRepository
import dev.wceng.seaofvoice.player.PlaybackManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LibraryUiState {
    data object Loading : LibraryUiState
    data class Success(
        val favoriteStations: List<UserStation>
    ) : LibraryUiState
    data object Empty : LibraryUiState
}

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val stationRepository: StationRepository,
    private val playbackManager: PlaybackManager
) : ViewModel() {

    val uiState: StateFlow<LibraryUiState> = stationRepository.getAllFavorites()
        .map { stations ->
            if (stations.isEmpty()) LibraryUiState.Empty else LibraryUiState.Success(stations)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LibraryUiState.Loading
        )

    fun toggleFavorite(uuid: String, isFavorite: Boolean) {
        viewModelScope.launch {
            stationRepository.toggleFavorite(uuid, isFavorite)
        }
    }

    fun playStation(userStation: UserStation) {
        playbackManager.playStation(userStation.station)
    }
}
