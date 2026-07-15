package dev.wceng.seaofvoice.domain.usecase

import dev.wceng.seaofvoice.data.model.UserStation
import dev.wceng.seaofvoice.data.repository.StationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Locale
import javax.inject.Inject

data class DiscoverData(
    val recentStations: List<UserStation>,
    val featuredStations: List<UserStation>,
    val localTopStations: List<UserStation>,
    val topVotedStations: List<UserStation>,
    val topClickedStations: List<UserStation>,
    val localFeedStations: List<UserStation>,
    val localCountryName: String
)

class GetDiscoverDataUseCase @Inject constructor(
    private val repository: StationRepository
) {
    operator fun invoke(): Flow<DiscoverData> {
        val localCountry = Locale.getDefault().getDisplayCountry(Locale.ENGLISH)
        val localCountryDisplay = Locale.getDefault().displayCountry

        return combine(
            repository.getFrequentStations(minPlayCount = 3, limit = 10),
            repository.getTopVoteStations(limit = 20), // Base for Featured
            repository.searchStations(country = localCountry, limit = 10, order = "votes"), // Local Top (by votes)
            repository.getTopVoteStations(limit = 20), // Global Top Voted
            repository.getTopClickStations(limit = 20), // Global Top Clicked
            repository.searchStations(country = localCountry, limit = 100, order = "votes") // Local Feed (by votes)
        ) { flows ->
            val frequentRecent = flows[0]
            val top20VotedForFeatured = flows[1]
            val localTop = flows[2]
            val globalTopVoted = flows[3]
            val globalTopClicked = flows[4]
            val localFeed = flows[5]

            // Featured: Top 5 from top 20 voted based on weighted factor (votes * 1.5 + clicks)
            val featured = top20VotedForFeatured
                .sortedByDescending { it.station.votes * 1.5 + it.station.clickcount }
                .take(5)

            // Local Feed: Top 100 in country excluding those in Local Top
            val localTopIds = localTop.map { it.station.uuid }.toSet()
            val filteredLocalFeed = localFeed.filter { it.station.uuid !in localTopIds }

            DiscoverData(
                recentStations = frequentRecent,
                featuredStations = featured,
                localTopStations = localTop,
                topVotedStations = globalTopVoted,
                topClickedStations = globalTopClicked,
                localFeedStations = filteredLocalFeed,
                localCountryName = localCountryDisplay
            )
        }
    }
}
