package com.sakethh.linkora.localDB

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.localDB.dto.ArchivedFolders
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class CustomFunctionsForLocalDB : ViewModel() {


    enum class CustomFunctionsForLocalDBType {
        FOLDER_BASED_LINKS, IMP_FOLDERS_LINKS, ARCHIVE_FOLDER_LINKS, SAVED_LINKS
    }

    companion object {
        lateinit var localDB: LocalDataBase
    }

    fun createANewFolder(
        context: Context, infoForSaving: String,
        onTaskCompleted: () -> Unit,
        parentFolderID: Long?,
        folderName: String,
        inSpecificFolderScreen: Boolean,
        rootParentID: Long
    ) {
        var doesThisFolderExists = false
        viewModelScope.launch {
            doesThisFolderExists = if (!inSpecificFolderScreen) {
                localDB.readDao()
                    .doesThisRootFolderExists(folderName = folderName)
            } else {
                localDB.readDao()
                    .doesThisChildFolderExists(
                        folderName = folderName,
                        parentFolderID = parentFolderID
                    ) >= 1
            }
        }.invokeOnCompletion {
            if (doesThisFolderExists) {
                viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "\"${folderName}\" already exists",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.invokeOnCompletion {
                    onTaskCompleted()
                }

            } else {
                viewModelScope.launch {
                    localDB.createDao()
                        .addANewFolder(
                            FoldersTable(
                                folderName = folderName,
                                infoForSaving = infoForSaving,
                                parentFolderID = parentFolderID,
                                childFolderIDs = emptyList()
                            )
                        )
                    if (parentFolderID != null) {
                        localDB.createDao().addANewChildIdToARootAndParentFolders(
                            rootParentID = rootParentID, currentID = localDB.readDao()
                                .getLatestAddedFolder().id, parentID = parentFolderID
                        )
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "\"${folderName}\" folder created successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }
        }
    }

    fun updateFoldersDetails(
        folderID: Long,
        newFolderName: String,
        infoForFolder: String,
        context: Context,
        onTaskCompleted: () -> Unit,
    ) {
        if (infoForFolder.isNotEmpty()) {
            viewModelScope.launch {
                localDB.updateDao()
                    .renameAFolderName(folderID = folderID, newFolderName = newFolderName)
                localDB.updateDao().renameAFolderNote(folderID = folderID, infoForFolder)
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {
            viewModelScope.launch {
                localDB.updateDao().renameAFolderName(folderID, newFolderName)
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        }
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "updated folder data", Toast.LENGTH_SHORT).show()
            }
        }

    }


    fun importantLinkTableUpdater(
        importantLinks: ImportantLinks,
        context: Context,
        inImportantLinksScreen: Boolean = false,
        autoDetectTitle: Boolean = false,
        onTaskCompleted: () -> Unit,
    ) {
        var doesLinkExists = false
        viewModelScope.launch {
            doesLinkExists =
                localDB.readDao().doesThisExistsInImpLinks(webURL = importantLinks.webURL)
        }.invokeOnCompletion {
            if (doesLinkExists) {
                if (inImportantLinksScreen) {
                    viewModelScope.launch {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "given link already exists in the \"Important Links\"",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }.invokeOnCompletion {
                        onTaskCompleted()
                    }
                } else {
                    viewModelScope.launch {
                        localDB.deleteDao().deleteALinkFromImpLinks(webURL = importantLinks.webURL)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "deleted the link from Important Links",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }.invokeOnCompletion {
                        onTaskCompleted()
                    }
                }
            } else {
                if (!isNetworkAvailable(context)) {
                    viewModelScope.launch {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "network error, check your network connection and try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }.invokeOnCompletion {
                        onTaskCompleted()
                    }
                } else if (try {
                        importantLinks.webURL.split("/")[2]
                        false
                    } catch (_: Exception) {
                        true
                    }
                ) {
                    viewModelScope.launch {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "invalid url", Toast.LENGTH_SHORT).show()
                        }
                    }.invokeOnCompletion {
                        onTaskCompleted()
                    }
                } else {
                    viewModelScope.launch {
                        val linkDataExtractor =
                            linkDataExtractor(importantLinks.webURL)
                        val linksData = ImportantLinks(
                            title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) linkDataExtractor.title else importantLinks.title,
                            webURL = importantLinks.webURL,
                            baseURL = linkDataExtractor.baseURL,
                            imgURL = linkDataExtractor.imgURL,
                            infoForSaving = importantLinks.infoForSaving
                        )
                        localDB.createDao().addANewLinkToImpLinks(importantLinks = linksData)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "added the link to Important Links",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }.invokeOnCompletion {
                        onTaskCompleted()
                    }
                }
            }
            viewModelScope.launch { OptionsBtmSheetVM().updateImportantCardData(url = importantLinks.webURL) }
        }
    }

    fun archiveLinkTableUpdater(
        archivedLinks: ArchivedLinks, context: Context,
        onTaskCompleted: () -> Unit,
    ) {
        var doesArchiveLinkExists = false
        viewModelScope.launch {
            doesArchiveLinkExists =
                localDB.readDao().doesThisExistsInArchiveLinks(webURL = archivedLinks.webURL)
        }.invokeOnCompletion {
            if (doesArchiveLinkExists) {
                viewModelScope.launch {
                    localDB.deleteDao().deleteALinkFromArchiveLinks(webURL = archivedLinks.webURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "removed the link from archive(s)",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            } else {
                viewModelScope.launch {
                    localDB.createDao().addANewLinkToArchiveLink(archivedLinks = archivedLinks)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "moved the link to archive(s)", Toast.LENGTH_SHORT)
                            .show()
                    }
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }
            viewModelScope.launch { OptionsBtmSheetVM().updateArchiveLinkCardData(url = archivedLinks.webURL) }
        }
    }

    fun archiveAFolderV10(folderID: Long) {
        viewModelScope.launch {
            localDB.updateDao().moveAFolderToArchivesV10(folderID)
        }
    }

    fun archiveFolderTableUpdaterV9(
        archivedFolders: ArchivedFolders,
        context: Context,
        onTaskCompleted: () -> Unit,
    ) {
        var doesThisExistsInArchiveFolder = false
        viewModelScope.launch {
            doesThisExistsInArchiveFolder = localDB.readDao()
                .doesThisArchiveFolderExists(folderName = archivedFolders.archiveFolderName)
        }.invokeOnCompletion {
            if (doesThisExistsInArchiveFolder) {
                viewModelScope.launch {
                    awaitAll(async {
                        localDB.deleteDao()
                            .deleteAnArchiveFolder(folderName = archivedFolders.archiveFolderName)
                    }, async {
                        localDB.deleteDao()
                            .deleteThisArchiveFolderData(folderID = archivedFolders.id)
                    })
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
                viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "deleted the archived folder and it's data permanently",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                viewModelScope.launch {
                    localDB.createDao().addANewArchiveFolder(archivedFolders = archivedFolders)
                }.invokeOnCompletion {
                    viewModelScope.launch {
                        localDB.updateDao()
                            .moveFolderLinksDataToArchive(folderID = archivedFolders.id)
                    }.invokeOnCompletion {
                        viewModelScope.launch {
                            localDB.deleteDao()
                                .deleteAFolder(folderID = archivedFolders.id)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "moved the folder to archive(s)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }.invokeOnCompletion {
                            onTaskCompleted()
                        }
                    }
                }
                viewModelScope.launch {
                    OptionsBtmSheetVM().updateArchiveFolderCardData(folderName = archivedFolders.archiveFolderName)
                }
            }
        }
    }


    fun addANewLinkSpecificallyInFolders(
        title: String,
        webURL: String,
        noteForSaving: String,
        folderID: Long,
        savingFor: CustomFunctionsForLocalDBType,
        context: Context,
        autoDetectTitle: Boolean = false,
        onTaskCompleted: () -> Unit,
        folderName: String
    ) {
        when (savingFor) {
            CustomFunctionsForLocalDBType.FOLDER_BASED_LINKS -> {
                var doesThisLinkExists = false
                viewModelScope.launch {
                    doesThisLinkExists = localDB.readDao()
                        .doesThisLinkExistsInAFolder(
                            webURL, folderID
                        )
                }.invokeOnCompletion {
                    if (!isNetworkAvailable(context)) {
                        viewModelScope.launch {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "network error, check your network connection and try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }.invokeOnCompletion {
                            onTaskCompleted()
                        }
                    } else if (try {
                            webURL.split("/")[2]
                            false
                        } catch (_: Exception) {
                            true
                        }
                    ) {
                        viewModelScope.launch {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "invalid url", Toast.LENGTH_SHORT).show()
                            }
                        }.invokeOnCompletion {
                            onTaskCompleted()
                        }
                    } else if (doesThisLinkExists) {
                        viewModelScope.launch {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "given link already exists in the \"${folderName.toString()}\"",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }.invokeOnCompletion {
                            onTaskCompleted()
                        }
                    } else {
                        viewModelScope.launch {
                            val linkDataExtractor = linkDataExtractor(webURL)
                            val linkData = LinksTable(
                                title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) linkDataExtractor.title else title,
                                webURL = webURL,
                                baseURL = linkDataExtractor.baseURL,
                                imgURL = linkDataExtractor.imgURL,
                                infoForSaving = noteForSaving,
                                isLinkedWithSavedLinks = false,
                                isLinkedWithFolders = true,
                                keyOfLinkedFolder = folderID,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = 0,
                                isLinkedWithArchivedFolder = false,
                                keyOfArchiveLinkedFolder = 0
                            )
                            if (linkData != null) {
                                localDB.createDao().addANewLinkToSavedLinksOrInFolders(linkData)
                            }
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "added the url", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }.invokeOnCompletion {
                            onTaskCompleted()
                        }
                    }
                }
            }

            CustomFunctionsForLocalDBType.IMP_FOLDERS_LINKS -> {
            }

            CustomFunctionsForLocalDBType.ARCHIVE_FOLDER_LINKS -> {
                var doesThisLinkExistsInAFolder = false
                viewModelScope.launch {
                    doesThisLinkExistsInAFolder = localDB.readDao()
                        .doesThisLinkExistsInAFolder(webURL, folderID)
                }.invokeOnCompletion {
                    if (!isNetworkAvailable(context)) {
                        viewModelScope.launch {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "network error, check your network connection and try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }.invokeOnCompletion {
                            onTaskCompleted()
                        }
                    } else if (try {
                            webURL.split("/")[2]
                            false
                        } catch (_: Exception) {
                            true
                        }
                    ) {
                        viewModelScope.launch {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "invalid url", Toast.LENGTH_SHORT).show()
                            }
                        }.invokeOnCompletion {
                            onTaskCompleted()
                        }
                    } else if (doesThisLinkExistsInAFolder) {
                        viewModelScope.launch {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "given link already exists in the \"${folderName.toString()}\"",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }.invokeOnCompletion {
                            onTaskCompleted()
                        }
                    } else {
                        viewModelScope.launch {
                            val _linkDataExtractor = linkDataExtractor(webURL)
                            val linkData = LinksTable(
                                title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) _linkDataExtractor.title else title,
                                webURL = webURL,
                                baseURL = _linkDataExtractor.baseURL,
                                imgURL = _linkDataExtractor.imgURL,
                                infoForSaving = noteForSaving,
                                isLinkedWithSavedLinks = false,
                                isLinkedWithFolders = false,
                                keyOfLinkedFolder = 0,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = 0,
                                isLinkedWithArchivedFolder = true,
                                keyOfArchiveLinkedFolder = folderID
                            )
                            if (linkData != null) {
                                localDB.createDao().addANewLinkToSavedLinksOrInFolders(linkData)
                            }
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "added the link", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }.invokeOnCompletion {
                            onTaskCompleted()
                        }
                    }
                }
            }

            CustomFunctionsForLocalDBType.SAVED_LINKS -> {
                var doesThisLinkExists = false
                viewModelScope.launch {
                    doesThisLinkExists = localDB.readDao()
                        .doesThisExistsInSavedLinks(
                            webURL
                        )
                }.invokeOnCompletion {
                    if (!isNetworkAvailable(context)) {
                        viewModelScope.launch {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "network error, check your network connection and try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }.invokeOnCompletion {
                            onTaskCompleted()
                        }
                    } else if (try {
                            webURL.split("/")[2]
                            false
                        } catch (_: Exception) {
                            true
                        }
                    ) {
                        viewModelScope.launch {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "invalid url", Toast.LENGTH_SHORT).show()
                            }
                        }.invokeOnCompletion {
                            onTaskCompleted()
                        }
                    } else if (doesThisLinkExists) {
                        viewModelScope.launch {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "given link already exists in the \"Saved Links\"",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }.invokeOnCompletion {
                            onTaskCompleted()
                        }
                    } else {
                        viewModelScope.launch {
                            val _linkDataExtractor = linkDataExtractor(webURL)
                            val linkData = LinksTable(
                                title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) _linkDataExtractor.title else title,
                                webURL = webURL,
                                baseURL = _linkDataExtractor.baseURL,
                                imgURL = _linkDataExtractor.imgURL,
                                infoForSaving = noteForSaving,
                                isLinkedWithSavedLinks = true,
                                isLinkedWithFolders = false,
                                keyOfLinkedFolder = 0,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = 0,
                                isLinkedWithArchivedFolder = false,
                                keyOfArchiveLinkedFolder = 0
                            )
                            localDB.createDao().addANewLinkToSavedLinksOrInFolders(linkData)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "added the link", Toast.LENGTH_SHORT).show()
                            }
                        }.invokeOnCompletion {
                            onTaskCompleted()
                        }
                    }
                }
            }
        }
    }

    fun deleteEntireLinksAndFoldersData(onTaskCompleted: () -> Unit = {}) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                localDB.clearAllTables()
            }
        }.invokeOnCompletion {
            onTaskCompleted()
        }
    }

}