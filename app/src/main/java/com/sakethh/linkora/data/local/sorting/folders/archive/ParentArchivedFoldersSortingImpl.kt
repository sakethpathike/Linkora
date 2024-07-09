package com.sakethh.linkora.data.local.sorting.folders.archive

import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.LocalDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ParentArchivedFoldersSortingImpl @Inject constructor(private val localDatabase: LocalDatabase) :
    ParentArchivedFoldersSortingRepo {
    override fun sortByAToZV10(): Flow<List<FoldersTable>> {
        return localDatabase.archivedFolderSorting().sortByAToZV10()
    }

    override fun sortByZToAV10(): Flow<List<FoldersTable>> {
        return localDatabase.archivedFolderSorting().sortByZToAV10()
    }

    override fun sortByLatestToOldestV10(): Flow<List<FoldersTable>> {
        return localDatabase.archivedFolderSorting().sortByLatestToOldestV10()
    }

    override fun sortByOldestToLatestV10(): Flow<List<FoldersTable>> {
        return localDatabase.archivedFolderSorting().sortByOldestToLatestV10()
    }
}