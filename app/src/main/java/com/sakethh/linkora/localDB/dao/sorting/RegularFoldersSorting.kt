package com.sakethh.linkora.localDB.dao.sorting

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.localDB.FoldersTable
import kotlinx.coroutines.flow.Flow

@Dao
interface RegularFoldersSorting {

    @Query("SELECT * FROM folders_table ORDER BY folderName COLLATE NOCASE ASC")
    fun sortByAToZ(): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table ORDER BY folderName COLLATE NOCASE DESC")
    fun sortByZToA(): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table ORDER BY id COLLATE NOCASE DESC")
    fun sortByLatestToOldest(): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table ORDER BY id COLLATE NOCASE ASC")
    fun sortByOldestToLatest(): Flow<List<FoldersTable>>

}