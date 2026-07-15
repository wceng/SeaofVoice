package dev.wceng.seaofvoice.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dev.wceng.seaofvoice.data.model.Station
import dev.wceng.seaofvoice.data.model.UserStation
import dev.wceng.seaofvoice.data.repository.StationRepository
import dev.wceng.seaofvoice.player.PlaybackManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val stationRepository: StationRepository,
    private val playbackManager: PlaybackManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive = _isSearchActive.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults: Flow<PagingData<Station>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) flowOf(PagingData.empty())
            else stationRepository.searchStationsPaged(query)
        }
        .cachedIn(viewModelScope)

    fun isFavorite(uuid: String): Flow<Boolean> {
        return stationRepository.isFavorite(uuid)
    }

    val searchHistory: StateFlow<List<String>> = stationRepository.getRecentSearchHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSearchActiveChange(active: Boolean) {
        _isSearchActive.value = active
        if (!active) {
            _searchQuery.value = ""
        }
    }

    fun onSearch(query: String) {
        viewModelScope.launch {
            stationRepository.saveSearchQuery(query)
        }
    }

    fun playStation(station: Station) {
        playbackManager.playStation(station)
        onSearchActiveChange(false)
        onSearch(station.name)
    }

    fun toggleFavorite(uuid: String, isFavorite: Boolean) {
        viewModelScope.launch {
            stationRepository.toggleFavorite(uuid, isFavorite)
        }
    }

    fun deleteSearchQuery(query: String) {
        viewModelScope.launch {
            stationRepository.deleteSearchQuery(query)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            stationRepository.clearSearchHistory()
        }
    }
}
