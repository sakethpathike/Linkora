package com.sakethh.linkora.localDB

import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.screens.collections.specificScreen.SpecificScreenVM
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

// will be fixed in next commit:
object LocalDBFunctions {

    lateinit var localDB: LocalDataBase

    suspend fun addANewLink(title: String, webURL: String, noteForSaving: String) {
        val linkData = LinksTable(
            title = title,
            webURL = webURL,
            baseURL = "www.www.www",
            imgURL = "",
            isThisLinkImportant = false,
            dateSavedOn = "",
            timeSavedOn = "",
            infoForSaving = noteForSaving
        )
        localDB.localDBData().addNewLink(linkData = linkData)
    }

    suspend fun addANewLinkInAFolder(
        folderName: String,
        titleForLink: String,
        webURLOfLink: String,
        noteForSavingLink: String,
    ) {
        val linkData = LinksTable(
            title = titleForLink,
            webURL = webURLOfLink,
            baseURL = "www.www.www",
            imgURL = "",
            isThisLinkImportant = false,
            dateSavedOn = "",
            timeSavedOn = "",
            infoForSaving = noteForSavingLink
        )
        localDB.localDBData().addANewLinkInAFolder(folderName = folderName, newLinkData = linkData)
    }

    suspend fun createANewFolder(newFoldersTable: FoldersTable) {
        localDB.localDBData().addNewFolder(newFolderData = newFoldersTable)
    }

    fun getAllLinks(): Flow<List<LinksTable>> {
        return localDB.localDBData().getAllLinks()
    }

    fun getAllImportantLinks(): Flow<List<ImportantLinks>> {
        return localDB.localDBData().getAllImportantLinks()
    }

    suspend fun getAllFolders(): Flow<List<FoldersTable>> {
        return localDB.localDBData().getAllFolders()
    }

    suspend fun deleteALink(link: String) {
        localDB.localDBData().deleteThisLink(link = link)
    }

    suspend fun deleteAFolder(folderName: String) {
        localDB.localDBData().deleteThisFolder(folderName = folderName)
    }

    suspend fun deleteALinkFromThisFolder(folderName: String, link: String) {
        /*localDB.localDBData().deleteALinkFromAFolder(folderName, link)*/
    }

    suspend fun renameAFolder(existingName: String, newName: String, newNote: String) {
        coroutineScope {
            awaitAll(
                async { localDB.localDBData().renameFolderName(existingName, newName) },
                async {
                    if (newNote.isNotEmpty()) {
                        renameAFolderNote(newNote = newNote, folderName = newName)
                    }
                })
        }
    }

    suspend fun renameAFolderNote(folderName: String, newNote: String) {
        localDB.localDBData()
            .renameFolderNote(newNote = newNote, folderName = folderName)
    }

    suspend fun renameLinkTitle(newTitle: String, webURL: String) {
        localDB.localDBData()
            .changeLinkTitle(newTitle, webURL)
    }

    suspend fun doesThisFolderExists(folderName: String): Boolean {
        return localDB.localDBData().doesThisFolderExists(folderName)
    }

    suspend fun importantLinksFunctions(url: String, importantLinks: ImportantLinks?) {
        val doesThisLinkExistsInImportantLinksDB =
            localDB.localDBData().isThisLinkMarkedAsImportant(url = url)
        if (doesThisLinkExistsInImportantLinksDB) {
            SpecificScreenVM().impLinkDataForBtmSheet.linkData.isThisLinkImportant = false
            localDB.localDBData().removeALinkFromImportant(url = url)
        } else {
            SpecificScreenVM().impLinkDataForBtmSheet.linkData.isThisLinkImportant = true
            importantLinks?.let { localDB.localDBData().addALinkToImportant(importantLinks = it) }
        }
        OptionsBtmSheetVM().updateImportantCardData(url = url)
    }

    suspend fun doesThisLinkExistsInImportantLinksDB(url: String): Boolean {
        return localDB.localDBData().doesThisLinkMarkedAsImportant(url = url)
    }
}