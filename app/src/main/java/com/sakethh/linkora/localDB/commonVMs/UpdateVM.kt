package com.sakethh.linkora.localDB.commonVMs

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.localDB.LocalDataBase
import com.sakethh.linkora.localDB.dto.ArchivedFolders
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.localDB.isNetworkAvailable
import com.sakethh.linkora.localDB.linkDataExtractor
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateVM : ViewModel() {

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
                LocalDataBase.localDB.readDao()
                    .doesThisExistsInImpLinks(webURL = importantLinks.webURL)
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
                        LocalDataBase.localDB.deleteDao()
                            .deleteALinkFromImpLinks(webURL = importantLinks.webURL)
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
                        LocalDataBase.localDB.createDao()
                            .addANewLinkToImpLinks(importantLinks = linksData)
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
                LocalDataBase.localDB.readDao()
                    .doesThisExistsInArchiveLinks(webURL = archivedLinks.webURL)
        }.invokeOnCompletion {
            if (doesArchiveLinkExists) {
                viewModelScope.launch {
                    LocalDataBase.localDB.deleteDao()
                        .deleteALinkFromArchiveLinks(webURL = archivedLinks.webURL)
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
                    LocalDataBase.localDB.createDao()
                        .addANewLinkToArchiveLink(archivedLinks = archivedLinks)
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
            LocalDataBase.localDB.updateDao().moveAFolderToArchivesV10(folderID)
        }
    }

    fun archiveFolderTableUpdaterV9(
        archivedFolders: ArchivedFolders,
        context: Context,
        onTaskCompleted: () -> Unit,
    ) {
        var doesThisExistsInArchiveFolder = false
        viewModelScope.launch {
            doesThisExistsInArchiveFolder = LocalDataBase.localDB.readDao()
                .doesThisArchiveFolderExistsV9(folderName = archivedFolders.archiveFolderName)
        }.invokeOnCompletion {
            if (doesThisExistsInArchiveFolder) {
                viewModelScope.launch {
                    awaitAll(async {
                        LocalDataBase.localDB.deleteDao()
                            .deleteAnArchiveFolderV9(folderName = archivedFolders.archiveFolderName)
                    }, async {
                        LocalDataBase.localDB.deleteDao()
                            .deleteThisArchiveFolderDataV9(folderID = archivedFolders.archiveFolderName)
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
                    LocalDataBase.localDB.createDao()
                        .addANewArchiveFolder(archivedFolders = archivedFolders)
                }.invokeOnCompletion {
                    viewModelScope.launch {
                        LocalDataBase.localDB.updateDao()
                            .moveFolderLinksDataToArchiveV9(folderName = archivedFolders.archiveFolderName)
                    }.invokeOnCompletion {
                        viewModelScope.launch {
                            LocalDataBase.localDB.deleteDao()
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
}