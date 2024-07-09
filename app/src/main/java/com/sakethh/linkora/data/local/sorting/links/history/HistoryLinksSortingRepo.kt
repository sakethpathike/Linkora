package com.sakethh.linkora.data.local.sorting.links.history

import com.sakethh.linkora.data.local.RecentlyVisited
import kotlinx.coroutines.flow.Flow

interface HistoryLinksSortingRepo {

    fun sortByAToZ(): Flow<List<RecentlyVisited>>

    fun sortByZToA(): Flow<List<RecentlyVisited>>

    fun sortByLatestToOldest(): Flow<List<RecentlyVisited>>

    fun sortByOldestToLatest(): Flow<List<RecentlyVisited>>

}