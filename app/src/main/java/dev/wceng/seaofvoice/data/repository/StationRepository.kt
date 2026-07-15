package dev.wceng.seaofvoice.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.wceng.seaofvoice.data.api.RadioBrowserApi
import dev.wceng.seaofvoice.data.datastore.UserPreferencesDataSource
import dev.wceng.seaofvoice.data.db.dao.CategoryDao
import dev.wceng.seaofvoice.data.db.dao.RecentStationDao
import dev.wceng.seaofvoice.data.db.dao.SearchHistoryDao
import dev.wceng.seaofvoice.data.db.dao.StationDao
import dev.wceng.seaofvoice.data.db.entity.SearchHistoryEntity
import dev.wceng.seaofvoice.data.db.entity.StationEntity
import dev.wceng.seaofvoice.data.db.entity.toEntity
import dev.wceng.seaofvoice.data.db.entity.toModel
import dev.wceng.seaofvoice.data.model.Category
import dev.wceng.seaofvoice.data.model.CategoryType
import dev.wceng.seaofvoice.data.model.Station
import dev.wceng.seaofvoice.data.model.UserStation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

interface StationRepository {
    fun getTopClickStations(limit: Int = 20): Flow<List<UserStation>>
    fun getTopVoteStations(limit: Int = 20): Flow<List<UserStation>>
    fun searchStations(
        name: String? = null,
        country: String? = null,
        tag: String? = null,
        limit: Int = 100,
        order: String = "votes"
    ): Flow<List<UserStation>>
    fun getAllFavorites(): Flow<List<UserStation>>
    suspend fun toggleFavorite(uuid: String, isFavorite: Boolean)
    fun isFavorite(uuid: String): Flow<Boolean>
    suspend fun getResolvedUrl(uuid: String): String

    fun getCountries(): Flow<List<Category>>
    fun getLanguages(): Flow<List<Category>>
    fun getTags(limit: Int = 100): Flow<List<Category>>

    fun searchStationsByName(name: String, limit: Int = 100): Flow<List<UserStation>>

    fun getRecentStations(limit: Int = 10): Flow<List<UserStation>>
    fun getFrequentStations(minPlayCount: Int = 3, limit: Int = 10): Flow<List<UserStation>>
    fun getStationByUuid(uuid: String): Flow<UserStation?>
    suspend fun markStationAsPlayed(uuid: String)
    suspend fun clearPlaybackHistory()

    fun searchStationsPaged(query: String): Flow<PagingData<Station>>

    fun getRecentSearchHistory(limit: Int = 10): Flow<List<String>>
    suspend fun saveSearchQuery(query: String)
    suspend fun deleteSearchQuery(query: String)
    suspend fun clearSearchHistory()
}

@Singleton
class OfflineFirstStationRepository @Inject constructor(
    private val api: RadioBrowserApi,
    private val stationDao: StationDao,
    private val categoryDao: CategoryDao,
    private val recentStationDao: RecentStationDao,
    private val searchHistoryDao: SearchHistoryDao,
    private val userPreferencesDataSource: UserPreferencesDataSource
) : StationRepository {

    override fun getTopClickStations(limit: Int): Flow<List<UserStation>> {
        val networkFlow = flow {
            try {
                val network = api.getTopClickStations(limit)
                stationDao.upsertStations(network.map { it.toEntity() })
            } catch (_: Exception) {}
            emit(Unit)
        }.onStart { emit(Unit) }

        return combine(stationDao.getTopClickStations(limit), networkFlow) { local, _ -> local }
            .combineWithPreferences()
    }

    override fun getTopVoteStations(limit: Int): Flow<List<UserStation>> {
        val networkFlow = flow {
            try {
                val network = api.getTopVoteStations(limit)
                stationDao.upsertStations(network.map { it.toEntity() })
            } catch (_: Exception) {}
            emit(Unit)
        }.onStart { emit(Unit) }

        return combine(stationDao.getTopVoteStations(limit), networkFlow) { local, _ -> local }
            .combineWithPreferences()
    }

    override fun searchStations(
        name: String?,
        country: String?,
        tag: String?,
        limit: Int,
        order: String
    ): Flow<List<UserStation>> {
        val networkFlow = flow {
            try {
                val network = api.searchStations(
                    name = name,
                    country = country,
                    tag = tag,
                    limit = limit,
                    order = order
                )
                stationDao.upsertStations(network.map { it.toEntity() })
            } catch (_: Exception) {}
            emit(Unit)
        }.onStart { emit(Unit) }

        return combine(stationDao.searchStations(name, country, tag, limit, order), networkFlow) { local, _ -> local }
            .combineWithPreferences()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllFavorites(): Flow<List<UserStation>> {
        return userPreferencesDataSource.userPreferencesFlow
            .flatMapLatest { prefs ->
                stationDao.getStationsByUuids(prefs.favoriteStationIds)
                    .map { entities ->
                        entities.map { UserStation(it, prefs) }
                    }
            }
    }

    override suspend fun toggleFavorite(uuid: String, isFavorite: Boolean) {
        userPreferencesDataSource.toggleFavoriteStation(uuid, isFavorite)
    }

    override fun isFavorite(uuid: String): Flow<Boolean> {
        return userPreferencesDataSource.userPreferencesFlow.map {
            uuid in it.favoriteStationIds
        }
    }

    override suspend fun getResolvedUrl(uuid: String): String {
        return api.getStationUrl(uuid)
    }

    override fun getCountries(): Flow<List<Category>> {
        val networkFlow = flow {
            try {
                val network = api.getCountries()
                categoryDao.upsertCategories(
                    network.map { 
                        Category(it.name, it.stationcount, CategoryType.Country, it.iso_3166_1.lowercase()).toEntity() 
                    }
                )
            } catch (_: Exception) {}
            emit(Unit)
        }.onStart { emit(Unit) }

        val localFlow = categoryDao.getCategoriesByType(CategoryType.Country)
            .map { entities -> entities.map { it.toModel() } }

        return combine(localFlow, networkFlow) { local, _ -> local }
    }

    override fun getLanguages(): Flow<List<Category>> {
        val networkFlow = flow {
            try {
                val network = api.getLanguages()
                categoryDao.upsertCategories(
                    network.map { 
                        Category(
                            it.name, 
                            it.stationcount, 
                            CategoryType.Language, 
                            it.iso_639?.lowercase()
                        ).toEntity() 
                    }
                )
            } catch (_: Exception) {}
            emit(Unit)
        }.onStart { emit(Unit) }

        val localFlow = categoryDao.getCategoriesByType(CategoryType.Language)
            .map { entities -> entities.map { it.toModel() } }

        return combine(localFlow, networkFlow) { local, _ -> local }
    }

    override fun getTags(limit: Int): Flow<List<Category>> {
        val networkFlow = flow {
            try {
                val network = api.getTags(limit)
                categoryDao.upsertCategories(
                    network.map { Category(it.name, it.stationcount, CategoryType.Tag).toEntity() }
                )
            } catch (_: Exception) {}
            emit(Unit)
        }.onStart { emit(Unit) }

        val localFlow = categoryDao.getCategoriesByType(CategoryType.Tag)
            .map { entities -> entities.map { it.toModel() } }

        return combine(localFlow, networkFlow) { local, _ -> local }
    }

    override fun getRecentStations(limit: Int): Flow<List<UserStation>> {
        return recentStationDao.getRecentStations(limit).combineWithPreferences()
    }

    override fun getFrequentStations(minPlayCount: Int, limit: Int): Flow<List<UserStation>> {
        val twoDaysMillis = 2 * 24 * 60 * 60 * 1000L
        val since = System.currentTimeMillis() - twoDaysMillis
        return recentStationDao.getFrequentStations(minPlayCount, since, limit).combineWithPreferences()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getStationByUuid(uuid: String): Flow<UserStation?> {
        return stationDao.getStationByUuid(uuid).flatMapLatest { entity ->
            if (entity == null) flowOf(null)
            else flowOf(listOf(entity)).combineWithPreferences().map { it.firstOrNull() }
        }
    }

    override suspend fun markStationAsPlayed(uuid: String) {
        recentStationDao.recordPlay(uuid)
    }

    override suspend fun clearPlaybackHistory() {
        recentStationDao.deleteAll()
    }

    override fun getRecentSearchHistory(limit: Int): Flow<List<String>> {
        return searchHistoryDao.getRecentSearchHistory(limit).map { entities ->
            entities.map { it.query }
        }
    }

    override suspend fun saveSearchQuery(query: String) {
        if (query.isBlank()) return
        searchHistoryDao.upsertSearchHistory(SearchHistoryEntity(query = query))
    }

    override suspend fun deleteSearchQuery(query: String) {
        searchHistoryDao.deleteSearchHistory(query)
    }

    override suspend fun clearSearchHistory() {
        searchHistoryDao.deleteAll()
    }

    override fun searchStationsByName(name: String, limit: Int): Flow<List<UserStation>> {
        val networkFlow = flow {
            try {
                val network = api.getStationsByName(name, limit)
                stationDao.upsertStations(network.map { it.toEntity() })
            } catch (_: Exception) {}
            emit(Unit)
        }.onStart { emit(Unit) }

        return combine(stationDao.searchStations(name, null, null, limit), networkFlow) { local, _ -> local }
            .combineWithPreferences()
    }

    override fun searchStationsPaged(query: String): Flow<PagingData<Station>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { StationPagingSource(api, stationDao, query) }
        ).flow
    }

    private fun Flow<List<StationEntity>>.combineWithPreferences(): Flow<List<UserStation>> {
        return combine(userPreferencesDataSource.userPreferencesFlow) { entities, prefs ->
            entities.map { UserStation(it, prefs) }
        }
    }
}
