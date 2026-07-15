package dev.wceng.seaofvoice.data.api

import dev.wceng.seaofvoice.data.api.dto.CountryDto
import dev.wceng.seaofvoice.data.api.dto.LanguageDto
import dev.wceng.seaofvoice.data.api.dto.NetworkStation
import dev.wceng.seaofvoice.data.api.dto.ResolvedUrlDto
import dev.wceng.seaofvoice.data.api.dto.TagDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class RadioBrowserApi(private val client: HttpClient) {

    suspend fun getStationUrl(uuid: String): String {
        return client.get("url/$uuid").body<ResolvedUrlDto>().url
    }

    suspend fun getTopClickStations(limit: Int = 20): List<NetworkStation> {
        return client.get("stations/topclick/$limit").body()
    }

    suspend fun getTopVoteStations(limit: Int = 20): List<NetworkStation> {
        return client.get("stations/topvote/$limit").body()
    }

    suspend fun searchStations(
        name: String? = null,
        country: String? = null,
        tag: String? = null,
        limit: Int = 100,
        offset: Int = 0,
        order: String = "votes",
        reverse: Boolean = true
    ): List<NetworkStation> {
        return client.get("stations/search") {
            parameter("name", name)
            parameter("country", country)
            parameter("tag", tag)
            parameter("limit", limit)
            parameter("offset", offset)
            parameter("order", order)
            parameter("reverse", reverse)
            parameter("hidebroken", true)
        }.body()
    }

    suspend fun getCountries(): List<CountryDto> = client.get("countries").body()
    suspend fun getLanguages(): List<LanguageDto> = client.get("languages").body()
    suspend fun getTags(limit: Int = 100): List<TagDto> = client.get("tags") {
        parameter("limit", limit)
        parameter("order", "stationcount")
        parameter("reverse", "true")
    }.body()

    suspend fun getStationsByName(name: String, limit: Int = 100, offset: Int = 0): List<NetworkStation> {
        return client.get("stations/byname/$name") {
            parameter("limit", limit)
            parameter("offset", offset)
            parameter("hidebroken", true)
        }.body()
    }
}
