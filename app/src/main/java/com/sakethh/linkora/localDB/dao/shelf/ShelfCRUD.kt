package com.sakethh.linkora.localDB.dao.shelf

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.sakethh.linkora.localDB.dto.Shelf
import kotlinx.coroutines.flow.Flow

@Dao
interface ShelfCRUD {
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
}