package com.sakethh.linkora.data.local.sorting.links.important

import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LocalDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImportantLinksSortingImpl @Inject constructor(private val localDatabase: LocalDatabase) :
    ImportantLinksSortingRepo {
    override fun sortByAToZ(): Flow<List<ImportantLinks>> {
        return localDatabase.importantLinksSorting().sortByAToZ()
    }

    override fun sortByZToA(): Flow<List<ImportantLinks>> {
        return localDatabase.importantLinksSorting().sortByZToA()
    }

    override fun sortByLatestToOldest(): Flow<List<ImportantLinks>> {
        return localDatabase.importantLinksSorting().sortByLatestToOldest()
    }

    override fun sortByOldestToLatest(): Flow<List<ImportantLinks>> {
        return localDatabase.importantLinksSorting().sortByOldestToLatest()
    }
}