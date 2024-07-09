package com.sakethh.linkora.data.local.sorting.folders.archive

import com.sakethh.linkora.data.local.FoldersTable
import kotlinx.coroutines.flow.Flow

interface ParentArchivedFoldersSortingRepo {
    fun sortByAToZV10(): Flow<List<FoldersTable>>

    fun sortByZToAV10(): Flow<List<FoldersTable>>

    fun sortByLatestToOldestV10(): Flow<List<FoldersTable>>

    fun sortByOldestToLatestV10(): Flow<List<FoldersTable>>
}