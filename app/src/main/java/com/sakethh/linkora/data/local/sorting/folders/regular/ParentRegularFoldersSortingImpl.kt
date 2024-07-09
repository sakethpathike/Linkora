package com.sakethh.linkora.data.local.sorting.folders.regular

import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.LocalDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ParentRegularFoldersSortingImpl @Inject constructor(private val localDatabase: LocalDatabase) :
    ParentRegularFoldersSortingRepo {
    override fun sortByAToZ(): Flow<List<FoldersTable>> {
        return localDatabase.regularFolderSorting().sortByAToZ()
    }

    override fun sortByZToA(): Flow<List<FoldersTable>> {
        return localDatabase.regularFolderSorting().sortByZToA()
    }

    override fun sortByLatestToOldest(): Flow<List<FoldersTable>> {
        return localDatabase.regularFolderSorting().sortByLatestToOldest()
    }

    override fun sortByOldestToLatest(): Flow<List<FoldersTable>> {
        return localDatabase.regularFolderSorting().sortByOldestToLatest()
    }
}