package com.sakethh.linkora.data.local.sorting.links.saved

import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.LocalDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SavedLinksSortingImpl @Inject constructor(private val localDatabase: LocalDatabase) :
    SavedLinksSortingRepo {
    override fun sortByAToZ(): Flow<List<LinksTable>> {
        return localDatabase.savedLinksSorting().sortByAToZ()
    }

    override fun sortByZToA(): Flow<List<LinksTable>> {
        return localDatabase.savedLinksSorting().sortByZToA()
    }

    override fun sortByLatestToOldest(): Flow<List<LinksTable>> {
        return localDatabase.savedLinksSorting().sortByLatestToOldest()
    }

    override fun sortByOldestToLatest(): Flow<List<LinksTable>> {
        return localDatabase.savedLinksSorting().sortByOldestToLatest()
    }
}