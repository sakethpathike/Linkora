package com.sakethh.linkora.localDB.dao.sorting

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.localDB.ArchivedFolders
import kotlinx.coroutines.flow.Flow

@Dao
interface ArchivedFoldersSorting {

    @Query("SELECT * FROM archived_folders_table ORDER BY archiveFolderName ASC")
    fun sortByAToZ(): Flow<List<ArchivedFolders>>

    @Query("SELECT * FROM archived_folders_table ORDER BY archiveFolderName DESC")
    fun sortByZToA(): Flow<List<ArchivedFolders>>

}