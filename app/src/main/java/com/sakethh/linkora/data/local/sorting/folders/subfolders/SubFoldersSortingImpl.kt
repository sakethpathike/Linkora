package com.sakethh.linkora.data.local.sorting.folders.subfolders

import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.LocalDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubFoldersSortingImpl @Inject constructor(private val localDatabase: LocalDatabase) :
    SubFoldersSortingRepo {
    override fun sortSubFoldersByAToZ(parentFolderID: Long): Flow<List<FoldersTable>> {
        return localDatabase.subFoldersSortingDao().sortSubFoldersByAToZ(parentFolderID)
    }

    override fun sortSubFoldersByZToA(parentFolderID: Long): Flow<List<FoldersTable>> {
        return localDatabase.subFoldersSortingDao().sortSubFoldersByZToA(parentFolderID)
    }

    override fun sortSubFoldersByLatestToOldest(parentFolderID: Long): Flow<List<FoldersTable>> {
        return localDatabase.subFoldersSortingDao().sortSubFoldersByLatestToOldest(parentFolderID)
    }

    override fun sortSubFoldersByOldestToLatest(parentFolderID: Long): Flow<List<FoldersTable>> {
        return localDatabase.subFoldersSortingDao().sortSubFoldersByOldestToLatest(parentFolderID)
    }
}