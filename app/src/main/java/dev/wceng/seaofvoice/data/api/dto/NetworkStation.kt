package dev.wceng.seaofvoice.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class NetworkStation(
    val stationuuid: String,
    val name: String,
    val url: String,
    val url_resolved: String? = null,
    val homepage: String? = null,
    val favicon: String? = null,
    val tags: String? = null,
    val country: String? = null,
    val countrycode: String? = null,
    val state: String? = null,
    val language: String? = null,
    val languagecodes: String? = null,
    val votes: Int = 0,
    val codec: String? = null,
    val bitrate: Int = 0,
    val lastcheckok: Int = 0,
    val clickcount: Int = 0
)
