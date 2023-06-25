package com.sakethh.linkora.localDB

import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

object CustomLocalDBDaoFunctionsDecl {

    enum class ModifiedLocalDbFunctionsType {
        FOLDER_BASED_LINKS, IMP_FOLDERS_LINKS, ARCHIVE_FOLDER_LINKS, SAVED_LINKS
    }

    lateinit var localDB: LocalDataBase


    suspend fun updateFoldersDetails(
        existingFolderName: String,
        newFolderName: String,
        infoForFolder: String,
    ) {
        if (infoForFolder.isNotEmpty()) {
            coroutineScope {
                awaitAll(async {
                    localDB.localDBData().renameAFolderName(existingFolderName, newFolderName)
                },
                    async {
                        localDB.localDBData().renameAFolderNote(existingFolderName, infoForFolder)
                    }
                )
            }
        } else {
            localDB.localDBData().renameAFolderName(existingFolderName, newFolderName)
        }
    }

    suspend fun importantLinkTableUpdater(importantLinks: ImportantLinks) {
        if (localDB.localDBData().doesThisExistsInImpLinks(webURL = importantLinks.webURL)) {
            localDB.localDBData().deleteALinkFromImpLinks(webURL = importantLinks.webURL)
        } else {
            val linkDataExtractor = linkDataExtractor(importantLinks.webURL)
            val linksData = ImportantLinks(
                title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value) linkDataExtractor.title else importantLinks.title,
                webURL = importantLinks.webURL,
                baseURL = linkDataExtractor.baseURL,
                imgURL = linkDataExtractor.imgURL,
                infoForSaving = importantLinks.infoForSaving
            )
            localDB.localDBData().addANewLinkToImpLinks(importantLinks = linksData)
        }
        OptionsBtmSheetVM().updateImportantCardData(url = importantLinks.webURL)
    }


    suspend fun addANewLinkSpecificallyInFolders(
        title: String,
        webURL: String,
        noteForSaving: String,
        folderName: String?,
        savingFor: ModifiedLocalDbFunctionsType,
    ) {
        when (savingFor) {
            ModifiedLocalDbFunctionsType.FOLDER_BASED_LINKS -> {
                val linkDataExtractor = linkDataExtractor(webURL)
                val linkData = folderName?.let {
                    LinksTable(
                        title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value) linkDataExtractor.title else title,
                        webURL = webURL,
                        baseURL = linkDataExtractor.baseURL,
                        imgURL = linkDataExtractor.imgURL,
                        infoForSaving = noteForSaving,
                        isLinkedWithSavedLinks = false,
                        isLinkedWithFolders = true,
                        keyOfLinkedFolder = it,
                        isLinkedWithImpFolder = false,
                        keyOfImpLinkedFolder = "",
                        isLinkedWithArchivedFolder = false,
                        keyOfArchiveLinkedFolder = ""
                    )
                }
                if (linkData != null) {
                    localDB.localDBData().addANewLinkToSavedLinksOrInFolders(linkData)
                }
            }

            ModifiedLocalDbFunctionsType.IMP_FOLDERS_LINKS -> {

            }

            ModifiedLocalDbFunctionsType.ARCHIVE_FOLDER_LINKS -> {
                val linkDataExtractor = linkDataExtractor(webURL)
                val linkData = folderName?.let {
                    LinksTable(
                        title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value) linkDataExtractor.title else title,
                        webURL = webURL,
                        baseURL = linkDataExtractor.baseURL,
                        imgURL = linkDataExtractor.imgURL,
                        infoForSaving = noteForSaving,
                        isLinkedWithSavedLinks = false,
                        isLinkedWithFolders = false,
                        keyOfLinkedFolder = "",
                        isLinkedWithImpFolder = false,
                        keyOfImpLinkedFolder = "",
                        isLinkedWithArchivedFolder = true,
                        keyOfArchiveLinkedFolder = folderName
                    )
                }
                if (linkData != null) {
                    localDB.localDBData().addANewLinkToSavedLinksOrInFolders(linkData)
                }
            }

            ModifiedLocalDbFunctionsType.SAVED_LINKS -> {
                val linkDataExtractor = linkDataExtractor(webURL)
                val linkData = LinksTable(
                    title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value) linkDataExtractor.title else title,
                    webURL = webURL,
                    baseURL = linkDataExtractor.baseURL,
                    imgURL = linkDataExtractor.imgURL,
                    infoForSaving = noteForSaving,
                    isLinkedWithSavedLinks = true,
                    isLinkedWithFolders = false,
                    keyOfLinkedFolder = "",
                    isLinkedWithImpFolder = false,
                    keyOfImpLinkedFolder = "",
                    isLinkedWithArchivedFolder = false,
                    keyOfArchiveLinkedFolder = ""
                )
                localDB.localDBData().addANewLinkToSavedLinksOrInFolders(linkData)
            }
        }
    }
}