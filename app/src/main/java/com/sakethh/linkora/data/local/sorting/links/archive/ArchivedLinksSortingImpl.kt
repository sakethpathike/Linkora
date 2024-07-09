package com.sakethh.linkora.data.local.sorting.links.archive

import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.LocalDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ArchivedLinksSortingImpl @Inject constructor(private val localDatabase: LocalDatabase) :
    ArchivedLinksSortingRepo {
    override fun sortByAToZ(): Flow<List<ArchivedLinks>> {
        return localDatabase.archivedLinksSorting().sortByAToZ()
    }

    override fun sortByZToA(): Flow<List<ArchivedLinks>> {
        return localDatabase.archivedLinksSorting().sortByZToA()
    }

    override fun sortByLatestToOldest(): Flow<List<ArchivedLinks>> {
        return localDatabase.archivedLinksSorting().sortByLatestToOldest()
    }

    override fun sortByOldestToLatest(): Flow<List<ArchivedLinks>> {
        return localDatabase.archivedLinksSorting().sortByOldestToLatest()
    }
}