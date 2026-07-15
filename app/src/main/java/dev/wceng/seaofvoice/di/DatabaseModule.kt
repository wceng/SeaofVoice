package dev.wceng.seaofvoice.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.wceng.seaofvoice.data.db.SeaOfVoiceDatabase
import dev.wceng.seaofvoice.data.db.dao.CategoryDao
import dev.wceng.seaofvoice.data.db.dao.RecentStationDao
import dev.wceng.seaofvoice.data.db.dao.SearchHistoryDao
import dev.wceng.seaofvoice.data.db.dao.StationDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SeaOfVoiceDatabase {
        return Room.databaseBuilder(
            context,
            SeaOfVoiceDatabase::class.java,
            "sea_of_voice_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideStationDao(database: SeaOfVoiceDatabase): StationDao {
        return database.stationDao()
    }

    @Provides
    fun provideCategoryDao(database: SeaOfVoiceDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideRecentStationDao(database: SeaOfVoiceDatabase): RecentStationDao {
        return database.recentStationDao()
    }

    @Provides
    fun provideSearchHistoryDao(database: SeaOfVoiceDatabase): SearchHistoryDao {
        return database.searchHistoryDao()
    }
}
