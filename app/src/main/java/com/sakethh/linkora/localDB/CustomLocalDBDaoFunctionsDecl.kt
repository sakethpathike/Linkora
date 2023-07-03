package com.sakethh.linkora.localDB

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object CustomLocalDBDaoFunctionsDecl {

    enum class ModifiedLocalDbFunctionsType {
        FOLDER_BASED_LINKS, IMP_FOLDERS_LINKS, ARCHIVE_FOLDER_LINKS, SAVED_LINKS
    }

    lateinit var localDB: LocalDataBase


    suspend fun updateFoldersDetails(
        existingFolderName: String,
        newFolderName: String,
        infoForFolder: String,
        context: Context,
    ) {
        if (infoForFolder.isNotEmpty()) {
            coroutineScope {
                awaitAll(
                    async {
                        localDB.localDBData().renameAFolderName(existingFolderName, newFolderName)
                    },
                    async {
                        localDB.localDBData().renameAFolderNote(existingFolderName, infoForFolder)
                    },
                    async {
                        localDB.localDBData().renameFolderNameForExistingFolderData(
                            existingFolderName,
                            newFolderName
                        )
                    })
            }
        } else {
            coroutineScope {
                awaitAll(
                    async {
                        localDB.localDBData().renameAFolderName(existingFolderName, newFolderName)
                    },
                    async {
                        localDB.localDBData()
                            .renameFolderNameForExistingFolderData(
                                existingFolderName,
                                newFolderName
                            )
                    })
            }
        }
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "updated folder data", Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun importantLinkTableUpdater(importantLinks: ImportantLinks, context: Context) {

        if (localDB.localDBData().doesThisExistsInImpLinks(webURL = importantLinks.webURL)) {
            localDB.localDBData().deleteALinkFromImpLinks(webURL = importantLinks.webURL)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "deleted the link from Important Links", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            val linkDataExtractor = linkDataExtractor(importantLinks.webURL)
            val linksData = ImportantLinks(
                title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value) linkDataExtractor.title else importantLinks.title,
                webURL = importantLinks.webURL,
                baseURL = linkDataExtractor.baseURL,
                imgURL = linkDataExtractor.imgURL,
                infoForSaving = importantLinks.infoForSaving
            )
            if (linkDataExtractor.errorInGivenURL) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "invalid url", Toast.LENGTH_SHORT).show()
                }
            } else {
                localDB.localDBData().addANewLinkToImpLinks(importantLinks = linksData)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "added the link to Important Links", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        OptionsBtmSheetVM().updateImportantCardData(url = importantLinks.webURL)
    }

    suspend fun archiveLinkTableUpdater(archivedLinks: ArchivedLinks, context: Context) {
        if (localDB.localDBData().doesThisExistsInArchiveLinks(webURL = archivedLinks.webURL)) {
            localDB.localDBData().deleteALinkFromArchiveLinks(webURL = archivedLinks.webURL)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "removed the link from archive(s)", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            localDB.localDBData().addANewLinkToArchiveLink(archivedLinks = archivedLinks)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "added the link to archive(s)", Toast.LENGTH_SHORT).show()
            }
        }
        OptionsBtmSheetVM().updateArchiveLinkCardData(url = archivedLinks.webURL)
    }

    @OptIn(FlowPreview::class)
    suspend fun archiveFolderTableUpdater(archivedFolders: ArchivedFolders, context: Context) {
        val coroutineJob = Job()
        if (localDB.localDBData()
                .doesThisArchiveFolderExists(folderName = archivedFolders.archiveFolderName)
        ) {
            coroutineScope {
                awaitAll(async {
                    localDB.localDBData()
                        .deleteAnArchiveFolder(folderName = archivedFolders.archiveFolderName)
                }, async {
                    localDB.localDBData()
                        .deleteThisArchiveFolderData(folderName = archivedFolders.archiveFolderName)
                })
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "deleted the archived folder and it's data permanently",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            localDB.localDBData().addANewArchiveFolder(archivedFolders = archivedFolders)
            CoroutineScope(coroutineJob).launch {
                val listOfData = localDB.localDBData()
                    .getThisFolderData(folderName = archivedFolders.archiveFolderName).toList()
                Log.d("archive data", listOfData.toString())
                listOfData[0].forEach {
                    it.isLinkedWithFolders = false
                    it.isLinkedWithArchivedFolder = true
                    it.keyOfArchiveLinkedFolder = archivedFolders.archiveFolderName
                    it.keyOfLinkedFolder = ""
                    it.keyOfImpLinkedFolder = ""
                    it.isLinkedWithImpFolder = false
                    it.isLinkedWithSavedLinks = false
                }
                localDB.localDBData().addListOfDataInLinksTable(listOfData[0])
                Log.d("archive data", listOfData[0].toString())
            }.invokeOnCompletion {
                CoroutineScope(coroutineJob).launch {
                    awaitAll(async {
                        localDB.localDBData()
                            .deleteAFolder(folderName = archivedFolders.archiveFolderName)
                    }, async {
                        localDB.localDBData()
                            .deleteThisFolderData(folderName = archivedFolders.archiveFolderName)
                    }
                    )
                }
            }
        }
        OptionsBtmSheetVM().updateArchiveFolderCardData(folderName = archivedFolders.archiveFolderName)
    }


    suspend fun addANewLinkSpecificallyInFolders(
        title: String,
        webURL: String,
        noteForSaving: String,
        folderName: String?,
        savingFor: ModifiedLocalDbFunctionsType,
        context: Context,
    ) {
        when (savingFor) {
            ModifiedLocalDbFunctionsType.FOLDER_BASED_LINKS -> {
                val linkDataExtractor = linkDataExtractor(webURL)
                if (linkDataExtractor.errorInGivenURL) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "invalid url", Toast.LENGTH_SHORT).show()
                    }
                } else {
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
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "added the url", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            ModifiedLocalDbFunctionsType.IMP_FOLDERS_LINKS -> {
            }

            ModifiedLocalDbFunctionsType.ARCHIVE_FOLDER_LINKS -> {
                val linkDataExtractor = linkDataExtractor(webURL)
                if (linkDataExtractor.errorInGivenURL) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "invalid url", Toast.LENGTH_SHORT).show()
                    }
                } else {
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
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "added the link", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }

            ModifiedLocalDbFunctionsType.SAVED_LINKS -> {
                val linkDataExtractor = linkDataExtractor(webURL)
                if (linkDataExtractor.errorInGivenURL) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "invalid url", Toast.LENGTH_SHORT).show()
                    }
                } else {
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
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "added the link", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}