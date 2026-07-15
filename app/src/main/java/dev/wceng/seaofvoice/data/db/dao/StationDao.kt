package dev.wceng.seaofvoice.data.db.dao

import androidx.room.*
import dev.wceng.seaofvoice.data.db.entity.StationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StationDao {

    @Query("SELECT * FROM stations WHERE stationuuid IN (:uuids)")
    fun getStationsByUuids(uuids: Set<String>): Flow<List<StationEntity>>

    @Query("SELECT * FROM stations WHERE stationuuid = :uuid")
    fun getStationByUuid(uuid: String): Flow<StationEntity?>

    @Query("SELECT * FROM stations ORDER BY clickcount DESC LIMIT :limit")
    fun getTopClickStations(limit: Int): Flow<List<StationEntity>>

    @Query("SELECT * FROM stations ORDER BY votes DESC LIMIT :limit")
    fun getTopVoteStations(limit: Int): Flow<List<StationEntity>>

    @Query("""
        SELECT * FROM stations 
        WHERE (:name IS NULL OR name LIKE '%' || :name || '%')
        AND (:country IS NULL OR country LIKE '%' || :country || '%')
        AND (:tag IS NULL OR tags LIKE '%' || :tag || '%')
        ORDER BY 
            CASE WHEN :order = 'votes' THEN votes END DESC,
            CASE WHEN :order = 'clickcount' THEN clickcount END DESC
        LIMIT :limit
    """)
    fun searchStations(
        name: String?, 
        country: String?, 
        tag: String?, 
        limit: Int, 
        order: String = "votes"
    ): Flow<List<StationEntity>>

    @Query("""
        SELECT * FROM stations 
        WHERE (:name IS NULL OR name LIKE '%' || :name || '%')
        AND (:country IS NULL OR country LIKE '%' || :country || '%')
        AND (:tag IS NULL OR tags LIKE '%' || :tag || '%')
        ORDER BY 
            CASE WHEN :order = 'votes' THEN votes END DESC,
            CASE WHEN :order = 'clickcount' THEN clickcount END DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun searchStationsSync(
        name: String?, 
        country: String?, 
        tag: String?, 
        limit: Int, 
        offset: Int,
        order: String = "votes"
    ): List<StationEntity>

    @Upsert
    suspend fun upsertStations(stations: List<StationEntity>)

    @Upsert
    suspend fun upsertStation(station: StationEntity)
}
