package dev.wceng.seaofvoice.data.datastore

import androidx.datastore.core.DataStore
import dev.wceng.seaofvoice.data.model.DarkModeConfig
import dev.wceng.seaofvoice.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<UserPreferencesProto>
) {
    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .map { proto ->
            UserPreferences(
                favoriteStationIds = proto.favoriteStationIdsMap.keys,
                darkModeConfig = when (proto.darkModeConfig) {
                    1 -> DarkModeConfig.LIGHT
                    2 -> DarkModeConfig.DARK
                    else -> DarkModeConfig.FOLLOW_SYSTEM
                },
                useDynamicColor = proto.useDynamicColor,
                autoPlayOnStart = proto.autoPlayOnStart,
                lastPlayedStationId = proto.lastPlayedStationId.takeIf { it.isNotBlank() },
                pauseOnHeadsetDisconnect = proto.pauseOnHeadsetDisconnect
            )
        }

    suspend fun toggleFavoriteStation(uuid: String, isFavorite: Boolean) {
        dataStore.updateData { currentPreferences ->
            val builder = currentPreferences.toBuilder()
            if (isFavorite) {
                builder.putFavoriteStationIds(uuid, true)
            } else {
                builder.removeFavoriteStationIds(uuid)
            }
            builder.build()
        }
    }

    suspend fun setDarkModeConfig(config: DarkModeConfig) {
        dataStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .setDarkModeConfig(
                    when (config) {
                        DarkModeConfig.LIGHT -> 1
                        DarkModeConfig.DARK -> 2
                        DarkModeConfig.FOLLOW_SYSTEM -> 0
                    }
                )
                .build()
        }
    }

    suspend fun setUseDynamicColor(use: Boolean) {
        dataStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .setUseDynamicColor(use)
                .build()
        }
    }

    suspend fun setAutoPlayOnStart(autoPlay: Boolean) {
        dataStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .setAutoPlayOnStart(autoPlay)
                .build()
        }
    }

    suspend fun setLastPlayedStationId(uuid: String) {
        dataStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .setLastPlayedStationId(uuid)
                .build()
        }
    }

    suspend fun setPauseOnHeadsetDisconnect(pause: Boolean) {
        dataStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .setPauseOnHeadsetDisconnect(pause)
                .build()
        }
    }
}
