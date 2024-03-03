package com.sakethh.linkora.utils

import com.sakethh.linkora.data.localDB.LocalDataBase

object DeleteAFolderFromShelf {
    suspend fun execute(folderID: Long) {
        LocalDataBase.localDB.shelfCrud().getShelvesOfThisFolder(folderID).forEach { shelf ->
            val newFolderIdsList = shelf.folderIds.toMutableList()
            newFolderIdsList.removeAll {
                it == folderID
            }
            LocalDataBase.localDB.shelfCrud()
                .updateFoldersOfThisShelf(folderIds = newFolderIdsList.toList(), shelf.id)
        }
        LocalDataBase.localDB.shelfFolders().deleteAShelfFolder(folderID)
    }
}