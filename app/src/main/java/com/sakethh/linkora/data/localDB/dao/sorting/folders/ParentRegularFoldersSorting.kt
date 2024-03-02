package com.sakethh.linkora.data.localDB.dao.sorting.folders

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.data.localDB.dto.FoldersTable
import kotlinx.coroutines.flow.Flow

@Dao
interface ParentRegularFoldersSorting {

    @Query("SELECT * FROM folders_table WHERE parentFolderID IS NULL AND isFolderArchived=0 ORDER BY folderName COLLATE NOCASE ASC")
    fun sortByAToZ(): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table WHERE parentFolderID IS NULL AND isFolderArchived=0 ORDER BY folderName COLLATE NOCASE DESC")
    fun sortByZToA(): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table WHERE parentFolderID IS NULL AND isFolderArchived=0 ORDER BY id COLLATE NOCASE DESC")
    fun sortByLatestToOldest(): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table WHERE parentFolderID IS NULL AND isFolderArchived=0 ORDER BY id COLLATE NOCASE ASC")
    fun sortByOldestToLatest(): Flow<List<FoldersTable>>

}