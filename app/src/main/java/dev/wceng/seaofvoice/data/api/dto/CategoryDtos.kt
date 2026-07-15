package dev.wceng.seaofvoice.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class CountryDto(
    val name: String,
    val iso_3166_1: String,
    val stationcount: Int
)

@Serializable
data class LanguageDto(
    val name: String,
    val iso_639: String? = null,
    val stationcount: Int
)

@Serializable
data class TagDto(
    val name: String,
    val stationcount: Int
)
