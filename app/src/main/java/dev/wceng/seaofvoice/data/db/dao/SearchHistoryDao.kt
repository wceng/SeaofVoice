package dev.wceng.seaofvoice.data.db.dao

import androidx.room.*
import dev.wceng.seaofvoice.data.db.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY lastUsedAt DESC LIMIT :limit")
    fun getRecentSearchHistory(limit: Int): Flow<List<SearchHistoryEntity>>

    @Upsert
    suspend fun upsertSearchHistory(searchHistory: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE query = :query")
    suspend fun deleteSearchHistory(query: String)

    @Query("DELETE FROM search_history")
    suspend fun deleteAll()
}
