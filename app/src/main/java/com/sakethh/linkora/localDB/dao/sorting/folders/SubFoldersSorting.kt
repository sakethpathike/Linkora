package com.sakethh.linkora.localDB.dao.sorting.folders

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.localDB.dto.FoldersTable
import kotlinx.coroutines.flow.Flow

@Dao
interface SubFoldersSorting {
    @Query("SELECT * FROM folders_table WHERE parentFolderID = :parentFolderID ORDER BY folderName COLLATE NOCASE ASC")
    fun sortSubFoldersByAToZ(parentFolderID: Long): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table WHERE parentFolderID = :parentFolderID ORDER BY folderName COLLATE NOCASE DESC")
    fun sortSubFoldersByZToA(parentFolderID: Long): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table WHERE parentFolderID = :parentFolderID ORDER BY id COLLATE NOCASE DESC")
    fun sortSubFoldersByLatestToOldest(parentFolderID: Long): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table WHERE parentFolderID = :parentFolderID ORDER BY id COLLATE NOCASE ASC")
    fun sortSubFoldersByOldestToLatest(parentFolderID: Long): Flow<List<FoldersTable>>
}