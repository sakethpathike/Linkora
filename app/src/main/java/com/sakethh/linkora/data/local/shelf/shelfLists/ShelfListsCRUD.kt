package com.sakethh.linkora.data.local.shelf.shelfLists

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sakethh.linkora.data.local.HomeScreenListTable
import kotlinx.coroutines.flow.Flow

@Dao
interface ShelfListsCRUD {
    @Query("SELECT * FROM home_screen_list_table WHERE parentShelfID = :shelfID ORDER BY position ASC")
    fun getAllFoldersOfThisShelf(shelfID: Long): Flow<List<HomeScreenListTable>>

    @Query("DELETE from home_screen_list_table WHERE id = :folderID")
    suspend fun deleteAShelfFolder(folderID: Long)

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