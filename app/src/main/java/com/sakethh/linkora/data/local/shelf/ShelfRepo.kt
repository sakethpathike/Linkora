package com.sakethh.linkora.data.local.shelf

import com.sakethh.linkora.data.local.Shelf
import kotlinx.coroutines.flow.Flow

interface ShelfRepo {
    suspend fun addANewShelf(shelf: Shelf)

    suspend fun deleteAFolderFromShelf(folderID: Long)
    fun getAllShelfItems(): Flow<List<Shelf>>

    suspend fun deleteAShelf(shelf: Shelf)

    suspend fun doesThisShelfExists(shelfName: String): Boolean

    suspend fun updateAShelfName(newShelfName: String, shelfID: Long)

    suspend fun getShelvesOfThisFolder(folderID: Long): List<Shelf>

    suspend fun updateFoldersOfThisShelf(folderIds: List<Long>, shelfID: Long)
}