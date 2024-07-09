package com.sakethh.linkora.data.local.sorting.links.history

import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.RecentlyVisited
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HistoryLinksSortingImpl @Inject constructor(private val localDatabase: LocalDatabase) :
    HistoryLinksSortingRepo {
    override fun sortByAToZ(): Flow<List<RecentlyVisited>> {
        return localDatabase.historyLinksSorting().sortByAToZ()
    }

    override fun sortByZToA(): Flow<List<RecentlyVisited>> {
        return localDatabase.historyLinksSorting().sortByZToA()
    }

    override fun sortByLatestToOldest(): Flow<List<RecentlyVisited>> {
        return localDatabase.historyLinksSorting().sortByLatestToOldest()
    }

    override fun sortByOldestToLatest(): Flow<List<RecentlyVisited>> {
        return localDatabase.historyLinksSorting().sortByOldestToLatest()
    }
}