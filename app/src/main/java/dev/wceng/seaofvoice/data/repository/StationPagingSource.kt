package dev.wceng.seaofvoice.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.wceng.seaofvoice.data.api.RadioBrowserApi
import dev.wceng.seaofvoice.data.db.dao.StationDao
import dev.wceng.seaofvoice.data.db.entity.toEntity
import dev.wceng.seaofvoice.data.model.Station
import dev.wceng.seaofvoice.data.model.toStation

class StationPagingSource(
    private val api: RadioBrowserApi,
    private val stationDao: StationDao,
    private val query: String
) : PagingSource<Int, Station>() {

    override fun getRefreshKey(state: PagingState<Int, Station>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Station> {
        return try {
            val page = params.key ?: 0
            val limit = params.loadSize
            val offset = page * limit

            // 1. Try fetching from Network
            try {
                val networkResponse = api.searchStations(
                    name = query,
                    limit = limit,
                    offset = offset
                )
                // 2. Save to Database
                stationDao.upsertStations(networkResponse.map { it.toEntity() })
            } catch (_: Exception) {
                // Ignore network errors, try to serve from DB
            }

            // 3. Always return data from Database to ensure Consistency
            val dbStations = stationDao.searchStationsSync(
                name = query,
                country = null,
                tag = null,
                limit = limit,
                offset = offset
            ).map { it.toStation() }

            LoadResult.Page(
                data = dbStations,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (dbStations.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
