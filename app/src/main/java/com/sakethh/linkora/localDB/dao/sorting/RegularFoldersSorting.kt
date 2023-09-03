package com.sakethh.linkora.localDB.dao.sorting

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.localDB.FoldersTable
import kotlinx.coroutines.flow.Flow

@Dao
interface RegularFoldersSorting {

    @Query("SELECT * FROM folders_table ORDER BY folderName ASC")
    fun sortByAToZ(): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table ORDER BY folderName DESC")
    fun sortByZToA(): Flow<List<FoldersTable>>

}