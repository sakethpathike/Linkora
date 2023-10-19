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

class CustomFunctionsForLocalDB : ViewModel() {


    enum class CustomFunctionsForLocalDBType {
        FOLDER_BASED_LINKS, IMP_FOLDERS_LINKS, ARCHIVE_FOLDER_LINKS, SAVED_LINKS
    }

    companion object {
        lateinit var localDB: LocalDataBase
    }

    fun createANewFolder(
        context: Context, folderName: String, infoForSaving: String,
        onTaskCompleted: () -> Unit,
    ) {
        var doesThisFolderExists = false
        viewModelScope.launch {
            doesThisFolderExists = localDB.crudDao()
                .doesThisFolderExists(folderName = folderName)
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
                    localDB.crudDao()
                        .addANewFolder(
                            FoldersTable(
                                folderName = folderName,
                                infoForSaving = infoForSaving
                            )
                        )
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
        existingFolderName: String,
        newFolderName: String,
        infoForFolder: String,
        context: Context,
        onTaskCompleted: () -> Unit,
    ) {
        if (infoForFolder.isNotEmpty()) {
            viewModelScope.launch {
                awaitAll(
                    async {
                        localDB.crudDao().renameAFolderName(existingFolderName, newFolderName)
                        localDB.crudDao().renameAFolderNote(newFolderName, infoForFolder)
                    },
                    async {
                        localDB.crudDao().renameFolderNameForExistingFolderData(
                            existingFolderName,
                            newFolderName
                        )
                    })
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {
            viewModelScope.launch {
                awaitAll(
                    async {
                        localDB.crudDao().renameAFolderName(existingFolderName, newFolderName)
                    },
                    async {
                        localDB.crudDao()
                            .renameFolderNameForExistingFolderData(
                                existingFolderName,
                                newFolderName
                            )
                    })
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
                localDB.crudDao().doesThisExistsInImpLinks(webURL = importantLinks.webURL)
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
                        localDB.crudDao().deleteALinkFromImpLinks(webURL = importantLinks.webURL)
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
                        localDB.crudDao().addANewLinkToImpLinks(importantLinks = linksData)
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
                localDB.crudDao().doesThisExistsInArchiveLinks(webURL = archivedLinks.webURL)
        }.invokeOnCompletion {
            if (doesArchiveLinkExists) {
                viewModelScope.launch {
                    localDB.crudDao().deleteALinkFromArchiveLinks(webURL = archivedLinks.webURL)
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
                    localDB.crudDao().addANewLinkToArchiveLink(archivedLinks = archivedLinks)
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

    fun archiveFolderTableUpdater(
        archivedFolders: ArchivedFolders,
        context: Context,
        onTaskCompleted: () -> Unit,
    ) {
        var doesThisExistsInArchiveFolder = false
        viewModelScope.launch {
            doesThisExistsInArchiveFolder = localDB.crudDao()
                .doesThisArchiveFolderExists(folderName = archivedFolders.archiveFolderName)
        }.invokeOnCompletion {
            if (doesThisExistsInArchiveFolder) {
                viewModelScope.launch {
                    awaitAll(async {
                        localDB.crudDao()
                            .deleteAnArchiveFolder(folderName = archivedFolders.archiveFolderName)
                    }, async {
                        localDB.crudDao()
                            .deleteThisArchiveFolderData(folderName = archivedFolders.archiveFolderName)
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
                    localDB.crudDao().addANewArchiveFolder(archivedFolders = archivedFolders)
                }.invokeOnCompletion {
                    viewModelScope.launch {
                        localDB.crudDao()
                            .moveFolderDataToArchive(folderName = archivedFolders.archiveFolderName)
                    }.invokeOnCompletion {
                        viewModelScope.launch {
                            localDB.crudDao()
                                .deleteAFolder(folderName = archivedFolders.archiveFolderName)
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
        folderName: String?,
        savingFor: CustomFunctionsForLocalDBType,
        context: Context,
        autoDetectTitle: Boolean = false,
        onTaskCompleted: () -> Unit,
    ) {
        when (savingFor) {
            CustomFunctionsForLocalDBType.FOLDER_BASED_LINKS -> {
                var doesThisLinkExists = false
                viewModelScope.launch {
                    doesThisLinkExists = localDB.crudDao()
                        .doesThisLinkExistsInAFolder(
                            webURL, folderName.toString()
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
                            val linkData = folderName?.let {
                                LinksTable(
                                    title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) linkDataExtractor.title else title,
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
                                localDB.crudDao().addANewLinkToSavedLinksOrInFolders(linkData)
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
                    doesThisLinkExistsInAFolder = localDB.crudDao()
                        .doesThisLinkExistsInAFolder(
                            folderName = folderName.toString(),
                            webURL = webURL
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
                            val linkData = folderName?.let {
                                LinksTable(
                                    title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) _linkDataExtractor.title else title,
                                    webURL = webURL,
                                    baseURL = _linkDataExtractor.baseURL,
                                    imgURL = _linkDataExtractor.imgURL,
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
                                localDB.crudDao().addANewLinkToSavedLinksOrInFolders(linkData)
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
                    doesThisLinkExists = localDB.crudDao()
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
                                keyOfLinkedFolder = "",
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "",
                                isLinkedWithArchivedFolder = false,
                                keyOfArchiveLinkedFolder = ""
                            )
                            localDB.crudDao().addANewLinkToSavedLinksOrInFolders(linkData)
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

    fun deleteEntireLinksAndFoldersData() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                localDB.clearAllTables()
            }
        }
    }

}