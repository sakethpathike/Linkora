package com.sakethh.linkora.ui.screens.shelf

import com.sakethh.linkora.data.local.Panel

sealed class ShelfUIEvent {
    data class DeleteAShelfFolder(val folderId: Long) : ShelfUIEvent()
    data class InsertANewElementInHomeScreenList(
        val folderName: String,
        val folderID: Long,
        val parentShelfID: Long
    ) : ShelfUIEvent()

    data class AddANewShelf(
        val panel: Panel
    ) : ShelfUIEvent()

    data class DeleteAPanel(
        val panel: Panel
    ) : ShelfUIEvent()

    data class UpdateAShelfName(
        val newName: String,
        val selectedShelfID: Long
    ) : ShelfUIEvent()
}