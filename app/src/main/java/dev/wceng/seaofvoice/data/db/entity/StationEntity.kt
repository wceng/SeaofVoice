package dev.wceng.seaofvoice.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.wceng.seaofvoice.data.api.dto.NetworkStation
import dev.wceng.seaofvoice.data.model.Station

@Entity(tableName = "stations")
data class StationEntity(
    @PrimaryKey
    val stationuuid: String,
    val name: String,
    val url: String,
    val homepage: String?,
    val favicon: String?,
    val tags: String?,
    val country: String?,
    val language: String?,
    val codec: String?,
    val bitrate: Int,
    val clickcount: Int = 0,
    val votes: Int = 0,
    val addedAt: Long = System.currentTimeMillis()
)

fun Station.toEntity(): StationEntity {
    return StationEntity(
        stationuuid = uuid,
        name = name,
        url = url,
        homepage = homepage,
        favicon = favicon,
        tags = tags.joinToString(","),
        country = country,
        language = language,
        codec = codec,
        bitrate = bitrate
    )
}

fun NetworkStation.toEntity(): StationEntity {
    return StationEntity(
        stationuuid = stationuuid,
        name = name,
        url = url,
        homepage = homepage,
        favicon = favicon,
        tags = tags,
        country = country,
        language = language,
        codec = codec,
        bitrate = bitrate,
        clickcount = clickcount,
        votes = votes
    )
}
