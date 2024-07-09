package com.sakethh.linkora.data.local.sorting.links.saved

import com.sakethh.linkora.data.local.LinksTable
import kotlinx.coroutines.flow.Flow

interface SavedLinksSortingRepo {

    fun sortByAToZ(): Flow<List<LinksTable>>

    fun sortByZToA(): Flow<List<LinksTable>>

    fun sortByLatestToOldest(): Flow<List<LinksTable>>

    fun sortByOldestToLatest(): Flow<List<LinksTable>>

}