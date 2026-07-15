package dev.wceng.seaofvoice.data.model

import dev.wceng.seaofvoice.data.api.dto.NetworkStation
import dev.wceng.seaofvoice.data.db.entity.StationEntity
import kotlinx.serialization.Serializable

@Serializable
data class Station(
    val uuid: String,
    val name: String,
    val url: String,
    val homepage: String?,
    val favicon: String?,
    val tags: List<String>,
    val country: String?,
    val language: String?,
    val codec: String?,
    val bitrate: Int,
    val votes: Int = 0,
    val clickcount: Int = 0
)

data class UserStation(
    val station: Station,
    val isFavorite: Boolean
) {
    constructor(entity: StationEntity, userPreferences: UserPreferences) : this(
        station = entity.toStation(),
        isFavorite = entity.stationuuid in userPreferences.favoriteStationIds
    )

    constructor(station: Station, userPreferences: UserPreferences) : this(
        station = station,
        isFavorite = station.uuid in userPreferences.favoriteStationIds
    )
}

fun NetworkStation.toStation(): Station {
    return Station(
        uuid = stationuuid,
        name = name,
        url = url,
        homepage = homepage,
        favicon = favicon,
        tags = tags?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList(),
        country = country,
        language = language,
        codec = codec,
        bitrate = bitrate,
        votes = votes,
        clickcount = clickcount
    )
}

fun StationEntity.toStation(): Station {
    return Station(
        uuid = stationuuid,
        name = name,
        url = url,
        homepage = homepage,
        favicon = favicon,
        tags = tags?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList(),
        country = country,
        language = language,
        codec = codec,
        bitrate = bitrate,
        votes = votes,
        clickcount = clickcount
    )
}
