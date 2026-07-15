package dev.wceng.seaofvoice.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.wceng.seaofvoice.data.datastore.UserPreferencesDataSource
import dev.wceng.seaofvoice.data.model.DarkModeConfig
import dev.wceng.seaofvoice.data.model.UserPreferences
import dev.wceng.seaofvoice.data.repository.StationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val settings: UserPreferences) : SettingsUiState
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val stationRepository: StationRepository,
    private val userPreferencesDataSource: UserPreferencesDataSource
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = userPreferencesDataSource.userPreferencesFlow
        .map { SettingsUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState.Loading
        )

    fun updateDarkModeConfig(config: DarkModeConfig) {
        viewModelScope.launch {
            userPreferencesDataSource.setDarkModeConfig(config)
        }
    }

    fun updateDynamicColorPreference(useDynamicColor: Boolean) {
        viewModelScope.launch {
            userPreferencesDataSource.setUseDynamicColor(useDynamicColor)
        }
    }

    fun updateAutoPlayPreference(autoPlay: Boolean) {
        viewModelScope.launch {
            userPreferencesDataSource.setAutoPlayOnStart(autoPlay)
        }
    }

    fun updatePauseOnHeadsetDisconnect(pause: Boolean) {
        viewModelScope.launch {
            userPreferencesDataSource.setPauseOnHeadsetDisconnect(pause)
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    fun clearImageCache() {
        viewModelScope.launch(Dispatchers.IO) {
            val imageLoader = context.imageLoader
            imageLoader.memoryCache?.clear()
            imageLoader.diskCache?.clear()
        }
    }

    fun clearPlaybackHistory() {
        viewModelScope.launch {
            stationRepository.clearPlaybackHistory()
            userPreferencesDataSource.setLastPlayedStationId("")
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            stationRepository.clearSearchHistory()
        }
    }
}
