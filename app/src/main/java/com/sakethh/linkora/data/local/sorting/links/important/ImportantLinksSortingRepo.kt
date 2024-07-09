package com.sakethh.linkora.data.local.sorting.links.important

import com.sakethh.linkora.data.local.ImportantLinks
import kotlinx.coroutines.flow.Flow

interface ImportantLinksSortingRepo {

    fun sortByAToZ(): Flow<List<ImportantLinks>>

    fun sortByZToA(): Flow<List<ImportantLinks>>

    fun sortByLatestToOldest(): Flow<List<ImportantLinks>>

    fun sortByOldestToLatest(): Flow<List<ImportantLinks>>

}