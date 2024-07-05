package com.sakethh.linkora.data.local.shelf

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.sakethh.linkora.data.local.Shelf
import kotlinx.coroutines.flow.Flow

@Dao
interface ShelfDao {
    @Insert
    suspend fun addANewShelf(shelf: Shelf)

    @Query("SELECT * FROM shelf")
    fun getAllShelfItems(): Flow<List<Shelf>>

    @Delete
    suspend fun deleteAShelf(shelf: Shelf)

    @Query("SELECT COUNT(*) FROM shelf WHERE shelfName = :shelfName")
    suspend fun doesThisShelfExists(shelfName: String): Boolean

    @Query("UPDATE SHELF SET shelfName=:newShelfName WHERE id = :shelfID")
    suspend fun updateAShelfName(newShelfName: String, shelfID: Long)

    @Query("SELECT * FROM SHELF WHERE :folderID in (folderIds)")
    suspend fun getShelvesOfThisFolder(folderID: Long): List<Shelf>

    @Query("UPDATE SHELF SET folderIds = :folderIds WHERE id=:shelfID")
    suspend fun updateFoldersOfThisShelf(folderIds: List<Long>, shelfID: Long)
}