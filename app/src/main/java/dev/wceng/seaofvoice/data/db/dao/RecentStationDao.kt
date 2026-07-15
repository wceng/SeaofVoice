package dev.wceng.seaofvoice.data.db.dao

import androidx.room.*
import dev.wceng.seaofvoice.data.db.entity.RecentStationEntity
import dev.wceng.seaofvoice.data.db.entity.StationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentStationDao {
    @Query("""
        SELECT s.* FROM stations s
        INNER JOIN recent_stations r ON s.stationuuid = r.stationuuid
        ORDER BY r.lastPlayedAt DESC
        LIMIT :limit
    """)
    fun getRecentStations(limit: Int): Flow<List<StationEntity>>

    @Query("""
        SELECT s.* FROM stations s
        INNER JOIN recent_stations r ON s.stationuuid = r.stationuuid
        WHERE r.playCount >= :minPlayCount AND r.lastPlayedAt >= :since
        ORDER BY r.lastPlayedAt DESC
        LIMIT :limit
    """)
    fun getFrequentStations(minPlayCount: Int, since: Long, limit: Int): Flow<List<StationEntity>>

    @Upsert
    suspend fun upsertRecentStation(recentStation: RecentStationEntity)

    @Query("SELECT * FROM recent_stations WHERE stationuuid = :uuid")
    suspend fun getRecentStationSync(uuid: String): RecentStationEntity?

    @Transaction
    suspend fun recordPlay(uuid: String) {
        val existing = getRecentStationSync(uuid)
        if (existing == null) {
            upsertRecentStation(RecentStationEntity(stationuuid = uuid))
        } else {
            upsertRecentStation(
                existing.copy(
                    playCount = existing.playCount + 1,
                    lastPlayedAt = System.currentTimeMillis()
                )
            )
        }
    }

    @Query("DELETE FROM recent_stations")
    suspend fun deleteAll()
}
