package com.sakethh.linkora.data.local.shelf

import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.Shelf
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ShelfImpl @Inject constructor(private val localDatabase: LocalDatabase) : ShelfRepo {
    override suspend fun addANewShelf(shelf: Shelf) {
        return localDatabase
    }

    override fun getAllShelfItems(): Flow<List<Shelf>> {

    }

    override suspend fun deleteAShelf(shelf: Shelf) {

    }

    override suspend fun doesThisShelfExists(shelfName: String): Boolean {

    }

    override suspend fun updateAShelfName(newShelfName: String, shelfID: Long) {

    }

    override suspend fun getShelvesOfThisFolder(folderID: Long): List<Shelf> {

    }

    override suspend fun updateFoldersOfThisShelf(folderIds: List<Long>, shelfID: Long) {

    }
}