package dev.wceng.seaofvoice.ui.screens.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.wceng.seaofvoice.data.model.Category
import dev.wceng.seaofvoice.data.model.UserStation
import dev.wceng.seaofvoice.data.repository.StationRepository
import dev.wceng.seaofvoice.player.PlaybackManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface BrowseUiState {
    data object Loading : BrowseUiState
    data class Success(
        val countries: List<Category>,
        val languages: List<Category>,
        val tags: List<Category>
    ) : BrowseUiState
    data class Error(val message: String) : BrowseUiState
}

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val stationRepository: StationRepository,
    private val playbackManager: PlaybackManager
) : ViewModel() {

    val uiState: StateFlow<BrowseUiState> = combine(
        stationRepository.getCountries(),
        stationRepository.getLanguages(),
        stationRepository.getTags(limit = 100)
    ) { countries, languages, tags ->
        BrowseUiState.Success(
            countries = countries.sortedByDescending { it.stationCount },
            languages = languages.sortedByDescending { it.stationCount },
            tags = tags.sortedByDescending { it.stationCount }
        )
    }.map<BrowseUiState.Success, BrowseUiState> { it }
    .catch { e ->
        emit(BrowseUiState.Error(e.message ?: "Unknown error"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BrowseUiState.Loading
    )

    fun playStation(userStation: UserStation) {
        playbackManager.playStation(userStation.station)
    }

    fun toggleFavorite(uuid: String, isFavorite: Boolean) {
        viewModelScope.launch {
            stationRepository.toggleFavorite(uuid, isFavorite)
        }
    }
}
