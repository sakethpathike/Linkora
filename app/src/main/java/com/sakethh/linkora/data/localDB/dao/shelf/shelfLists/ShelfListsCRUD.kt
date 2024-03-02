package com.sakethh.linkora.data.localDB.dao.shelf.shelfLists

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sakethh.linkora.data.localDB.dto.HomeScreenListTable
import kotlinx.coroutines.flow.Flow

@Dao
interface ShelfListsCRUD {
    @Query("SELECT * FROM home_screen_list_table WHERE parentShelfID = :shelfID ORDER BY position ASC")
    fun getAllHomeScreenListFoldersOfThisShelf(shelfID: Long): Flow<List<HomeScreenListTable>>

    @Query("DELETE from home_screen_list_table WHERE id = :folderID")
    suspend fun deleteAHomeScreenListFolder(folderID: Long)

    @Insert
    suspend fun addAHomeScreenListFolder(homeScreenListTable: HomeScreenListTable)

    @Update
    suspend fun updateElement(element: HomeScreenListTable)

    @Query(
        "SELECT * FROM home_screen_list_table\n" +
                "ORDER BY id DESC\n" +
                "LIMIT 1;"
    )
    suspend fun getLastInsertedElement(): HomeScreenListTable
}