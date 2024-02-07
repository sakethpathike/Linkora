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
import com.sakethh.linkora.localDB.dto.HomeScreenListTable
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.isNetworkAvailable
import com.sakethh.linkora.localDB.linkDataExtractor
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateVM : ViewModel() {

    fun updateFolderName(folderID: Long, newFolderName: String) {
        viewModelScope.launch {
            LocalDataBase.localDB.updateDao().renameAFolderName(folderID, newFolderName)
        }
    }

    fun moveLinkFromLinksTableToArchiveLinks(id: Long) {
        viewModelScope.launch {
            val localDataBase = LocalDataBase
            localDataBase.localDB.updateDao()
                .moveLinkFromLinksTableToArchiveLinks(id)
            localDataBase.localDB.deleteDao().deleteALinkFromLinksTable(id)
        }
    }

    fun updateRegularLinkTitle(linkID: Long, newTitle: String) {
        viewModelScope.launch {
            async {
                LocalDataBase.localDB.updateDao().renameALinkTitle(linkID, newTitle)
            }.await()
        }
    }

    fun updateImpLinkTitle(linkID: Long, newTitle: String) {
        viewModelScope.launch {
            LocalDataBase.localDB.updateDao().renameALinkTitleFromImpLinks(linkID, newTitle)
        }
    }

    fun updateRegularLinkNote(linkID: Long, newNote: String) {
        viewModelScope.launch {
            async {
                LocalDataBase.localDB.updateDao().renameALinkInfo(linkID, newNote)
            }.await()
        }
    }

    fun updateImpLinkNote(linkID: Long, newNote: String) {
        viewModelScope.launch {
            LocalDataBase.localDB.updateDao().renameALinkInfoFromImpLinks(linkID, newNote)
        }
    }

    fun updateFolderNote(folderID: Long, newFolderNote: String) {
        viewModelScope.launch {
            LocalDataBase.localDB.updateDao().renameAFolderNoteV10(folderID, newFolderNote)
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
                            .deleteALinkFromImpLinks(linkID = importantLinks.id)
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
                        .deleteALinkFromArchiveLinksV9(webURL = archivedLinks.webURL)
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

    fun migrateRegularFoldersLinksDataFromV9toV10() {
        val localDataBase = LocalDataBase.localDB
        viewModelScope.launch {
            localDataBase.readDao().getAllRootFolders().collect { rootFolders ->
                rootFolders.forEach { currentFolder ->
                    async {
                        currentFolder.childFolderIDs = emptyList()
                        localDataBase.updateDao().updateAFolderData(currentFolder)
                    }.await()
                    localDataBase.readDao().getLinksOfThisFolderV9(currentFolder.folderName)
                        .collect { links ->
                            links.forEach { currentLink ->
                                async {
                                    currentLink.keyOfLinkedFolderV10 = currentFolder.id
                                    localDataBase.updateDao()
                                        .updateALinkDataFromLinksTable(currentLink)
                                }.await()
                            }
                        }
                }
            }
        }
    }

    fun migrateArchiveFoldersV9toV10() {
        val localDataBase = LocalDataBase.localDB
        viewModelScope.launch {
            localDataBase.readDao().getAllArchiveFoldersV9().collect { archiveFolders ->
                archiveFolders.forEach { currentFolder ->
                    async {
                        val foldersTable = FoldersTable(
                            folderName = currentFolder.archiveFolderName,
                            infoForSaving = currentFolder.infoForSaving,
                            parentFolderID = null,
                            isFolderArchived = true,
                            isMarkedAsImportant = false
                        )
                        foldersTable.childFolderIDs = emptyList()
                        localDataBase.createDao().addANewFolder(
                            foldersTable
                        )
                    }.await()
                    val latestAddedFolderID = async {
                        localDataBase.readDao().getLatestAddedFolder().id
                    }.await()
                    localDataBase.readDao()
                        .getThisArchiveFolderLinksV9(currentFolder.archiveFolderName)
                        .collect { archiveLinks ->
                            archiveLinks.forEach { currentArchiveLink ->
                                async {
                                    currentArchiveLink.isLinkedWithFolders = true
                                    currentArchiveLink.keyOfLinkedFolderV10 = latestAddedFolderID
                                    localDataBase.updateDao()
                                        .updateALinkDataFromLinksTable(currentArchiveLink)
                                }.await()
                            }
                            async {
                                localDataBase.deleteDao()
                                    .deleteAnArchiveFolderV9(currentFolder.id)
                            }.await()
                        }
                }
            }
        }
    }

    fun updateHomeListElement(homeScreenListTableElement: HomeScreenListTable) {
        viewModelScope.launch {
            LocalDataBase.localDB.homeListsCrud().updateElement(homeScreenListTableElement)
        }
    }
}