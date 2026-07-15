package dev.wceng.seaofvoice.data.model

data class UserPreferences(
    val favoriteStationIds: Set<String>,
    val darkModeConfig: DarkModeConfig,
    val useDynamicColor: Boolean,
    val autoPlayOnStart: Boolean,
    val lastPlayedStationId: String?,
    val pauseOnHeadsetDisconnect: Boolean
)

enum class DarkModeConfig {
    FOLLOW_SYSTEM, LIGHT, DARK
}
