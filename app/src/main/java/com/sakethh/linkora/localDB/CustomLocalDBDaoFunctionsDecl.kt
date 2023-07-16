package com.sakethh.linkora.localDB

import android.content.Context
import android.widget.Toast
import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
                        localDB.localDBData().renameAFolderNote(newFolderName, infoForFolder)
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

    suspend fun updateArchivedFoldersDetails(
        existingFolderName: String,
        newFolderName: String,
        infoForFolder: String,
        context: Context,
    ) {
        if (infoForFolder.isNotEmpty()) {
            localDB.localDBData().renameAFolderArchiveName(existingFolderName, newFolderName)
            localDB.localDBData().renameArchivedFolderNote(existingFolderName, infoForFolder)
            localDB.localDBData()
                .renameFolderNameForExistingArchivedFolderData(existingFolderName, newFolderName)
        } else {
            coroutineScope {
                awaitAll(
                    async {
                        localDB.localDBData()
                            .renameAFolderArchiveName(existingFolderName, newFolderName)
                    },
                    async {
                        localDB.localDBData()
                            .renameFolderNameForExistingArchivedFolderData(
                                existingFolderName,
                                newFolderName
                            )
                    })
            }
        }
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "updated archived folder data", Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun importantLinkTableUpdater(
        importantLinks: ImportantLinks,
        context: Context,
        inImportantLinksScreen: Boolean = false,
    ) {

        if (localDB.localDBData().doesThisExistsInImpLinks(webURL = importantLinks.webURL)) {
            if (inImportantLinksScreen) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "given link already exists in the \"Important Links\"",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                localDB.localDBData().deleteALinkFromImpLinks(webURL = importantLinks.webURL)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "deleted the link from Important Links",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        } else {
            val linkDataExtractor =
                linkDataExtractor(webURL = importantLinks.webURL, context = context)
            val linksData = ImportantLinks(
                title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value) linkDataExtractor.title else importantLinks.title,
                webURL = importantLinks.webURL,
                baseURL = linkDataExtractor.baseURL,
                imgURL = linkDataExtractor.imgURL,
                infoForSaving = importantLinks.infoForSaving
            )
            if (linkDataExtractor.networkError) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "network error, check your network connection and try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (linkDataExtractor.errorInGivenURL) {
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
                Toast.makeText(context, "moved the link to archive(s)", Toast.LENGTH_SHORT).show()
            }
        }
        OptionsBtmSheetVM().updateArchiveLinkCardData(url = archivedLinks.webURL)
    }

    suspend fun archiveFolderTableUpdater(archivedFolders: ArchivedFolders, context: Context) {
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
            val coroutineJob = Job()
            CoroutineScope(coroutineJob).launch {
                localDB.localDBData().addANewArchiveFolder(archivedFolders = archivedFolders)
            }.invokeOnCompletion {
                CoroutineScope(coroutineJob).launch {
                    localDB.localDBData()
                        .moveFolderDataToArchive(folderName = archivedFolders.archiveFolderName)
                }.invokeOnCompletion {
                    CoroutineScope(coroutineJob).launch {
                        localDB.localDBData()
                            .deleteAFolder(folderName = archivedFolders.archiveFolderName)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "moved the folder to archive(s)",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }.invokeOnCompletion {
                        coroutineJob.cancel()
                    }
                }
            }
            OptionsBtmSheetVM().updateArchiveFolderCardData(folderName = archivedFolders.archiveFolderName)
        }
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
                val linkDataExtractor = linkDataExtractor(context, webURL)
                if (linkDataExtractor.networkError) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "network error, check your network connection and try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (linkDataExtractor.errorInGivenURL) {
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
                val linkDataExtractor = linkDataExtractor(context, webURL)
                if (linkDataExtractor.networkError) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "network error, check your network connection and try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (linkDataExtractor.errorInGivenURL) {
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
                val linkDataExtractor = linkDataExtractor(context, webURL)
                if (linkDataExtractor.networkError) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "network error, check your network connection and try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (linkDataExtractor.errorInGivenURL) {
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

    fun deleteEntireLinksAndFoldersData() {
        localDB.clearAllTables()
    }
}