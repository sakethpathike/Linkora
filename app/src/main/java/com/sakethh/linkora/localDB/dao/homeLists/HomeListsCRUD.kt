package com.sakethh.linkora.localDB.dao.homeLists

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sakethh.linkora.localDB.dto.HomeScreenListTable
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeListsCRUD {
    @Query("SELECT * FROM home_screen_list_table")
    fun getAllHomeScreenListFolders(): Flow<List<HomeScreenListTable>>

    @Query("DELETE from home_screen_list_table WHERE id = :folderID")
    suspend fun deleteAHomeScreenListFolder(folderID: Long)

    @Insert
    suspend fun addAHomeScreenListFolder(homeScreenListTable: HomeScreenListTable)

    @Update
    suspend fun updateList(list: List<HomeScreenListTable>)
}