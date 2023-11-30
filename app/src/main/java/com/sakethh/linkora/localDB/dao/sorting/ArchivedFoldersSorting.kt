package com.sakethh.linkora.localDB.dao.sorting

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.localDB.dto.ArchivedFolders
import com.sakethh.linkora.localDB.dto.FoldersTable
import kotlinx.coroutines.flow.Flow

@Dao
interface ArchivedFoldersSorting {

    @Query("SELECT * FROM archived_folders_table ORDER BY archiveFolderName COLLATE NOCASE ASC")
    fun sortByAToZV9(): Flow<List<ArchivedFolders>>

    @Query("SELECT * FROM archived_folders_table ORDER BY archiveFolderName COLLATE NOCASE DESC")
    fun sortByZToAV9(): Flow<List<ArchivedFolders>>

    @Query("SELECT * FROM archived_folders_table ORDER BY id COLLATE NOCASE DESC")
    fun sortByLatestToOldestV9(): Flow<List<ArchivedFolders>>

    @Query("SELECT * FROM archived_folders_table ORDER BY id COLLATE NOCASE ASC")
    fun sortByOldestToLatestV9(): Flow<List<ArchivedFolders>>

    @Query("SELECT * FROM folders_table WHERE isFolderArchived=1 ORDER BY folderName COLLATE NOCASE ASC")
    fun sortByAToZV10(): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table WHERE isFolderArchived=1 ORDER BY folderName COLLATE NOCASE DESC")
    fun sortByZToAV10(): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table WHERE isFolderArchived=1 ORDER BY id COLLATE NOCASE DESC")
    fun sortByLatestToOldestV10(): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table WHERE isFolderArchived=1 ORDER BY id COLLATE NOCASE ASC")
    fun sortByOldestToLatestV10(): Flow<List<FoldersTable>>

}