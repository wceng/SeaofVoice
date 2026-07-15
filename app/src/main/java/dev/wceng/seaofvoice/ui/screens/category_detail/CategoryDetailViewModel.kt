package dev.wceng.seaofvoice.ui.screens.category_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.wceng.seaofvoice.data.model.CategoryType
import dev.wceng.seaofvoice.data.model.UserStation
import dev.wceng.seaofvoice.data.repository.StationRepository
import dev.wceng.seaofvoice.player.PlaybackManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface CategoryDetailUiState {
    data object Loading : CategoryDetailUiState
    data class Success(
        val categoryName: String,
        val stations: List<UserStation>
    ) : CategoryDetailUiState
    data class Error(val message: String) : CategoryDetailUiState
}

@HiltViewModel(assistedFactory = CategoryDetailViewModel.Factory::class)
class CategoryDetailViewModel @AssistedInject constructor(
    @Assisted val categoryName: String,
    @Assisted val categoryType: CategoryType,
    private val stationRepository: StationRepository,
    private val playbackManager: PlaybackManager
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(categoryName: String, categoryType: CategoryType): CategoryDetailViewModel
    }

    val uiState: StateFlow<CategoryDetailUiState> = (when (categoryType) {
        CategoryType.Country -> stationRepository.searchStations(country = categoryName)
        CategoryType.Language -> stationRepository.searchStations(name = null)
        CategoryType.Tag -> stationRepository.searchStations(tag = categoryName)
    })
        .map<List<UserStation>, CategoryDetailUiState> { stations ->
            CategoryDetailUiState.Success(categoryName, stations)
        }
        .catch { e ->
            emit(CategoryDetailUiState.Error(e.message ?: "Unknown Error"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CategoryDetailUiState.Loading
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
