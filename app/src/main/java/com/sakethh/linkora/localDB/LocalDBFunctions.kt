package com.sakethh.linkora.localDB

import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.screens.collections.specificScreen.SpecificScreenVM
import kotlinx.coroutines.flow.Flow

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

    suspend fun renameAFolder(existingName: String, newName: String) {
        localDB.localDBData().renameFolderName(existingName, newName)
    }

    suspend fun importantLinksFunctions(url: String, importantLinks: ImportantLinks?) {
        val doesThisLinkExistsInImportantLinksDB =
            localDB.localDBData().doesThisLinkMarkedAsImportant(url = url)
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