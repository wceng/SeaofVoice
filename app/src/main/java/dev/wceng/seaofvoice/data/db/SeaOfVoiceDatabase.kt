package dev.wceng.seaofvoice.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.wceng.seaofvoice.data.db.dao.CategoryDao
import dev.wceng.seaofvoice.data.db.dao.RecentStationDao
import dev.wceng.seaofvoice.data.db.dao.SearchHistoryDao
import dev.wceng.seaofvoice.data.db.dao.StationDao
import dev.wceng.seaofvoice.data.db.entity.CategoryEntity
import dev.wceng.seaofvoice.data.db.entity.RecentStationEntity
import dev.wceng.seaofvoice.data.db.entity.SearchHistoryEntity
import dev.wceng.seaofvoice.data.db.entity.StationEntity

@Database(
    entities = [
        StationEntity::class,
        CategoryEntity::class,
        RecentStationEntity::class,
        SearchHistoryEntity::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(DataConverter::class)
abstract class SeaOfVoiceDatabase : RoomDatabase() {
    abstract fun stationDao(): StationDao
    abstract fun categoryDao(): CategoryDao
    abstract fun recentStationDao(): RecentStationDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}
