package com.sakethh.linkora.data.local.sorting.folders.subfolders

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.data.local.FoldersTable
import kotlinx.coroutines.flow.Flow

interface SubFoldersSortingRepo {
    fun sortSubFoldersByAToZ(parentFolderID: Long): Flow<List<FoldersTable>>

    fun sortSubFoldersByZToA(parentFolderID: Long): Flow<List<FoldersTable>>

    fun sortSubFoldersByLatestToOldest(parentFolderID: Long): Flow<List<FoldersTable>>

    fun sortSubFoldersByOldestToLatest(parentFolderID: Long): Flow<List<FoldersTable>>
}