package com.sakethh.linkora.ui.screens.shelf

import com.sakethh.linkora.data.local.Shelf

sealed class ShelfUIEvent {
    data class DeleteAShelfFolder(val folderId: Long) : ShelfUIEvent()
    data class InsertANewElementInHomeScreenList(
        val folderName: String,
        val folderID: Long,
        val parentShelfID: Long
    ) : ShelfUIEvent()

    data class AddANewShelf(
        val shelf: Shelf
    ) : ShelfUIEvent()

    data class DeleteAShelf(
        val shelf: Shelf
    ) : ShelfUIEvent()

    data class UpdateAShelfName(
        val newName: String,
        val selectedShelfID: Long
    ) : ShelfUIEvent()
}