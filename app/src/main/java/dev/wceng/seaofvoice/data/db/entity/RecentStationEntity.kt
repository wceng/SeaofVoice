package dev.wceng.seaofvoice.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_stations")
data class RecentStationEntity(
    @PrimaryKey
    val stationuuid: String,
    val playCount: Int = 1,
    val lastPlayedAt: Long = System.currentTimeMillis()
)
