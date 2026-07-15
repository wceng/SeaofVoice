package dev.wceng.seaofvoice.data.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RadioBrowserApiTest {

    private lateinit var api: RadioBrowserApi

    @Before
    fun setup() {
        val client = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                })
            }
            defaultRequest {
                url("https://de1.api.radio-browser.info/json/")
                header("User-Agent", "SeaOfVoiceTest/1.0")
            }
        }
        api = RadioBrowserApi(client)
    }

    @Test
    fun `getTopClickStations returns non-empty list`() = runTest {
        val stations = api.getTopClickStations(limit = 5)
        println("Fetched ${stations.size} top click stations")
        stations.forEach { println("Station: ${it.name}") }
        assertTrue(stations.isNotEmpty())
    }

    @Test
    fun `getTopVoteStations returns non-empty list`() = runTest {
        val stations = api.getTopVoteStations(limit = 5)
        println("Fetched ${stations.size} top vote stations")
        stations.forEach { println("Station: ${it.name}, Votes: ${it.votes}") }
        assertTrue(stations.isNotEmpty())
    }

    @Test
    fun `searchStations with name returns results`() = runTest {
        val stations = api.searchStations(name = "jazz", limit = 10)
        println("Found ${stations.size} stations for 'jazz'")
        assertTrue(stations.isNotEmpty())
        assertTrue(stations.all { it.name.contains("jazz", ignoreCase = true) })
    }

    @Test
    fun `getCountries returns non-empty list`() = runTest {
        val countries = api.getCountries()
        println("Fetched ${countries.size} countries")
        assertTrue(countries.isNotEmpty())
        countries.take(5).forEach { println("Country: ${it.name} (${it.iso_3166_1}) - ${it.stationcount} stations") }
    }

    @Test
    fun `getLanguages returns non-empty list`() = runTest {
        val languages = api.getLanguages()
        println("Fetched ${languages.size} languages")
        assertTrue(languages.isNotEmpty())
        languages.take(5).forEach { println("Language: ${it.name} - ${it.stationcount} stations") }
    }

    @Test
    fun `getTags returns non-empty list`() = runTest {
        val tags = api.getTags(limit = 10)
        println("Fetched ${tags.size} tags")
        assertTrue(tags.isNotEmpty())
        tags.forEach { println("Tag: ${it.name} - ${it.stationcount} stations") }
    }
}
