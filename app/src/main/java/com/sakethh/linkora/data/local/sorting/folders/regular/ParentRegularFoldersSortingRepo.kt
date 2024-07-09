package com.sakethh.linkora.data.local.sorting.folders.regular

import com.sakethh.linkora.data.local.FoldersTable
import kotlinx.coroutines.flow.Flow

interface ParentRegularFoldersSortingRepo {

    fun sortByAToZ(): Flow<List<FoldersTable>>

    fun sortByZToA(): Flow<List<FoldersTable>>

    fun sortByLatestToOldest(): Flow<List<FoldersTable>>

    fun sortByOldestToLatest(): Flow<List<FoldersTable>>

}