package com.sakethh.linkora.data.local.shelf.shelfLists

import com.sakethh.linkora.data.local.HomeScreenListTable
import com.sakethh.linkora.data.local.LocalDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class ShelfListsImpl @Inject constructor(private val localDatabase: LocalDatabase) :
    ShelfListsRepo {
    override fun getAllFoldersOfThisShelf(shelfID: Long): Flow<List<HomeScreenListTable>> {
        return localDatabase.shelfListDao().getAllFoldersOfThisShelf(shelfID)
    }

    override suspend fun deleteAShelfFolder(folderID: Long) {
        return localDatabase.shelfListDao().deleteAShelfFolder(folderID)
    }

    override suspend fun addAHomeScreenListFolder(homeScreenListTable: HomeScreenListTable) {
        return localDatabase.shelfListDao().addAHomeScreenListFolder(homeScreenListTable)
    }

    override suspend fun updateElement(element: HomeScreenListTable) {
        return localDatabase.shelfListDao().updateElement(element)
    }

    override suspend fun getLastInsertedElement(): HomeScreenListTable {
        return localDatabase.shelfListDao().getLastInsertedElement()
    }
}