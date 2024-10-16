package com.sakethh.linkora.data.local.shelf.shelfLists

import com.sakethh.linkora.data.local.HomeScreenListTable
import kotlinx.coroutines.flow.Flow

interface ShelfListsRepo {
    fun getAllFoldersOfThisShelf(shelfID: Long): Flow<List<HomeScreenListTable>>

    suspend fun deleteAShelfFolder(folderID: Long)

    suspend fun addAHomeScreenListFolder(homeScreenListTable: HomeScreenListTable)

    suspend fun updateElement(element: HomeScreenListTable)

    suspend fun getLastInsertedElement(): HomeScreenListTable
}