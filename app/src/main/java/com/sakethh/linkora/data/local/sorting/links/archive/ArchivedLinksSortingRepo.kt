package com.sakethh.linkora.data.local.sorting.links.archive

import com.sakethh.linkora.data.local.ArchivedLinks
import kotlinx.coroutines.flow.Flow

interface ArchivedLinksSortingRepo {

    fun sortByAToZ(): Flow<List<ArchivedLinks>>

    fun sortByZToA(): Flow<List<ArchivedLinks>>

    fun sortByLatestToOldest(): Flow<List<ArchivedLinks>>

    fun sortByOldestToLatest(): Flow<List<ArchivedLinks>>

}