package dev.wceng.seaofvoice.ui.screens.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.wceng.seaofvoice.data.model.UserStation
import dev.wceng.seaofvoice.data.repository.StationRepository
import dev.wceng.seaofvoice.domain.usecase.GetDiscoverDataUseCase
import dev.wceng.seaofvoice.player.PlaybackManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DiscoverUiState {
    data object Loading : DiscoverUiState
    data class Success(
        val recentStations: List<UserStation>,
        val featuredStations: List<UserStation>,
        val localTopStations: List<UserStation>,
        val topVotedStations: List<UserStation>,
        val topClickedStations: List<UserStation>,
        val localFeedStations: List<UserStation>,
        val localCountryName: String
    ) : DiscoverUiState
    data class Error(val message: String) : DiscoverUiState
}

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val stationRepository: StationRepository,
    private val getDiscoverDataUseCase: GetDiscoverDataUseCase,
    private val playbackManager: PlaybackManager
) : ViewModel() {

    val uiState: StateFlow<DiscoverUiState> = getDiscoverDataUseCase()
        .map<dev.wceng.seaofvoice.domain.usecase.DiscoverData, DiscoverUiState> { data ->
            // Only transition to Success if we have at least featured or feed data
            // This prevents "flickering" empty headers during initial DB-to-Network transition
            if (data.featuredStations.isEmpty() && data.localFeedStations.isEmpty()) {
                DiscoverUiState.Loading
            } else {
                DiscoverUiState.Success(
                    recentStations = data.recentStations,
                    featuredStations = data.featuredStations,
                    localTopStations = data.localTopStations,
                    topVotedStations = data.topVotedStations,
                    topClickedStations = data.topClickedStations,
                    localFeedStations = data.localFeedStations,
                    localCountryName = data.localCountryName
                )
            }
        }
        .catch { e -> emit(DiscoverUiState.Error(e.message ?: "Unknown Error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DiscoverUiState.Loading
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
