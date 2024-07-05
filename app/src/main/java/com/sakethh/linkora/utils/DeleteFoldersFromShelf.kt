package com.sakethh.linkora.utils

import com.sakethh.linkora.data.local.LocalDatabase

object DeleteAFolderFromShelf {
    suspend fun execute(folderID: Long) {
        LocalDatabase.localDB.shelfCrud().getShelvesOfThisFolder(folderID).forEach { shelf ->
            val newFolderIdsList = shelf.folderIds.toMutableList()
            newFolderIdsList.removeAll {
                it == folderID
            }
            LocalDatabase.localDB.shelfCrud()
                .updateFoldersOfThisShelf(folderIds = newFolderIdsList.toList(), shelf.id)
        }
        LocalDatabase.localDB.shelfFolders().deleteAShelfFolder(folderID)
    }
}