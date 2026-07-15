package dev.wceng.seaofvoice.di

import dev.wceng.seaofvoice.data.repository.StationRepository
import dev.wceng.seaofvoice.data.repository.OfflineFirstStationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStationRepository(
        offlineFirstStationRepository: OfflineFirstStationRepository
    ): StationRepository
}
