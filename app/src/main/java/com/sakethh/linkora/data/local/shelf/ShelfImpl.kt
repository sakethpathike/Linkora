package com.sakethh.linkora.data.local.shelf

import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.Shelf
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ShelfImpl @Inject constructor(private val localDatabase: LocalDatabase) : ShelfRepo {
    override suspend fun addANewShelf(shelf: Shelf) {
        localDatabase.shelfDao().addANewShelf(shelf)
    }

    override suspend fun deleteAFolderFromShelf(folderID: Long) {
        getShelvesOfThisFolder(folderID).forEach { shelf ->
            val newFolderIdsList = shelf.folderIds.toMutableList()
            newFolderIdsList.removeAll {
                it == folderID
            }
            updateFoldersOfThisShelf(folderIds = newFolderIdsList.toList(), shelf.id)
        }
        localDatabase.shelfListDao().deleteAShelfFolder(folderID)
    }

    override fun getAllShelfItems(): Flow<List<Shelf>> {
        return localDatabase.shelfDao().getAllShelfItems()
    }

    override suspend fun deleteAShelf(shelf: Shelf) {
        return localDatabase.shelfDao().deleteAShelf(shelf)
    }

    override suspend fun doesThisShelfExists(shelfName: String): Boolean {
        return localDatabase.shelfDao().doesThisShelfExists(shelfName)
    }

    override suspend fun updateAShelfName(newShelfName: String, shelfID: Long) {
        return localDatabase.shelfDao().updateAShelfName(newShelfName, shelfID)
    }

    override suspend fun getShelvesOfThisFolder(folderID: Long): List<Shelf> {
        return localDatabase.shelfDao().getShelvesOfThisFolder(folderID)
    }

    override suspend fun updateFoldersOfThisShelf(folderIds: List<Long>, shelfID: Long) {
        return localDatabase.shelfDao().updateFoldersOfThisShelf(folderIds, shelfID)
    }
}