package com.sakethh.linkora.screens.collections.archiveScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.sakethh.linkora.localDB.CustomFunctionsForLocalDB
import com.sakethh.linkora.localDB.dto.ArchivedFolders
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreenVM
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ArchiveScreenModal(
    val name: String,
    val screen: @Composable (navController: NavController) -> Unit,
)

enum class ArchiveScreenType {
    LINKS, FOLDERS
}

class ArchiveScreenVM : SpecificScreenVM() {
    val selectedArchivedLinkData = mutableStateOf(
        ArchivedLinks(
            title = "",
            webURL = "",
            baseURL = "",
            imgURL = "",
            infoForSaving = ""
        )
    )

    val parentArchiveScreenData = listOf(
        ArchiveScreenModal(name = "Links", screen = { navController ->
            ChildArchiveScreen(
                archiveScreenType = ArchiveScreenType.LINKS,
                navController = navController
            )
        }), ArchiveScreenModal(name = "Folders",
            screen = { navController ->
                ChildArchiveScreen(
                    archiveScreenType = ArchiveScreenType.FOLDERS,
                    navController = navController
                )
            })
    )
    private val _archiveLinksData = MutableStateFlow(emptyList<ArchivedLinks>())
    val archiveLinksData = _archiveLinksData.asStateFlow()

    private val _archiveFoldersDataV9 = MutableStateFlow(emptyList<ArchivedFolders>())
    val archiveFoldersDataV9 = _archiveFoldersDataV9.asStateFlow()

    private val _archiveFoldersDataV10 = MutableStateFlow(emptyList<FoldersTable>())
    val archiveFoldersDataV10 = _archiveFoldersDataV10.asStateFlow()

    init {
        changeRetrievedData(
            sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                SettingsScreenVM.Settings.selectedSortingType.value
            )
        )
    }

    fun onNoteChangeClickForLinks(
        archiveScreenType: ArchiveScreenType,
        webURL: String,
        newNote: String,
        onTaskCompleted: () -> Unit,
        folderID: Long
    ) {
        if (archiveScreenType == ArchiveScreenType.LINKS) {
            viewModelScope.launch {
                CustomFunctionsForLocalDB.localDB.updateDao()
                    .renameALinkInfoFromArchiveLinks(webURL, newNote)
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {
            viewModelScope.launch {
                CustomFunctionsForLocalDB.localDB.updateDao()
                    .renameArchivedFolderNote(
                        folderID = folderID,
                        newNote = newNote
                    )
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        }
    }
    fun onTitleChangeClickForLinksV9(
        archiveScreenType: ArchiveScreenType,
        newTitle: String,
        webURL: String,
        onTaskCompleted: () -> Unit,
        folderID: Long
    ) {
        if (archiveScreenType == ArchiveScreenType.LINKS) {
            viewModelScope.launch {
                CustomFunctionsForLocalDB.localDB.updateDao()
                    .renameALinkTitleFromArchiveLinks(webURL = webURL, newTitle = newTitle)
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {
            viewModelScope.launch {
                CustomFunctionsForLocalDB.localDB.updateDao()
                    .renameAFolderArchiveName(folderID, newTitle)
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        }
    }

    fun changeRetrievedData(sortingPreferences: SettingsScreenVM.SortingPreferences) {
        when (sortingPreferences) {
            SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                viewModelScope.launch {
                    awaitAll(async {
                        CustomFunctionsForLocalDB.localDB.archivedLinksSorting().sortByAToZ()
                            .collect {
                                _archiveLinksData.emit(it)
                            }
                    }, async {
                        CustomFunctionsForLocalDB.localDB.archivedFolderSorting().sortByAToZV9()
                            .collect {
                                _archiveFoldersDataV9.emit(it)
                            }
                    }, async {
                        CustomFunctionsForLocalDB.localDB.archivedFolderSorting().sortByAToZV10()
                            .collect {
                                _archiveFoldersDataV10.emit(it)
                            }
                    })
                }
            }

            SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                viewModelScope.launch {
                    awaitAll(async {
                        CustomFunctionsForLocalDB.localDB.archivedLinksSorting().sortByZToA()
                            .collect {
                                _archiveLinksData.emit(it)
                            }
                    }, async {
                        CustomFunctionsForLocalDB.localDB.archivedFolderSorting().sortByZToAV9()
                            .collect {
                                _archiveFoldersDataV9.emit(it)
                            }
                    }, async {
                        CustomFunctionsForLocalDB.localDB.archivedFolderSorting().sortByZToAV10()
                            .collect {
                                _archiveFoldersDataV10.emit(it)
                            }
                    })
                }
            }

            SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                viewModelScope.launch {
                    awaitAll(async {
                        CustomFunctionsForLocalDB.localDB.archivedLinksSorting()
                            .sortByLatestToOldest().collect {
                                _archiveLinksData.emit(it)
                            }
                    }, async {
                        CustomFunctionsForLocalDB.localDB.archivedFolderSorting()
                            .sortByLatestToOldestV9().collect {
                                _archiveFoldersDataV9.emit(it)
                            }
                    }, async {
                        CustomFunctionsForLocalDB.localDB.archivedFolderSorting()
                            .sortByLatestToOldestV10().collect {
                                _archiveFoldersDataV10.emit(it)
                            }
                    })
                }
            }

            SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                viewModelScope.launch {
                    awaitAll(async {
                        CustomFunctionsForLocalDB.localDB.archivedLinksSorting()
                            .sortByOldestToLatest().collect {
                                _archiveLinksData.emit(it)
                            }
                    }, async {
                        CustomFunctionsForLocalDB.localDB.archivedFolderSorting()
                            .sortByOldestToLatestV9().collect {
                                _archiveFoldersDataV9.emit(it)
                            }
                    }, async {
                        CustomFunctionsForLocalDB.localDB.archivedFolderSorting()
                            .sortByOldestToLatestV10().collect {
                                _archiveFoldersDataV10.emit(it)
                            }
                    })
                }
            }
        }
    }

    fun onDeleteClick(
        archiveScreenType: ArchiveScreenType,
        selectedURLOrFolderName: String,
        context: Context,
        onTaskCompleted: () -> Unit,
    ) {
        if (archiveScreenType == ArchiveScreenType.LINKS) {
            viewModelScope.launch {
                CustomFunctionsForLocalDB.localDB.deleteDao()
                    .deleteALinkFromArchiveLinks(webURL = selectedURLOrFolderName)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context, "removed the link from archive permanently", Toast.LENGTH_SHORT
                    ).show()
                }
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {
            CustomFunctionsForLocalDB().archiveFolderTableUpdaterV9(
                ArchivedFolders(
                    archiveFolderName = selectedURLOrFolderName,
                    infoForSaving = ""
                ), context = context, onTaskCompleted = {
                    onTaskCompleted()
                }
            )
        }

    }

    fun onNoteDeleteCardClick(
        archiveScreenType: ArchiveScreenType,
        selectedURLOrFolderName: String,
        context: Context,
        onTaskCompleted: () -> Unit,
        folderID: Long
    ) {
        if (archiveScreenType == ArchiveScreenType.FOLDERS) {
            viewModelScope.launch {
                CustomFunctionsForLocalDB.localDB.deleteDao()
                    .deleteArchiveFolderNote(folderID)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {
            viewModelScope.launch {
                CustomFunctionsForLocalDB.localDB.deleteDao()
                    .deleteANoteFromArchiveLinks(webURL = selectedURLOrFolderName)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        }
    }

    fun onUnArchiveClickV10(folderID: Long) {
        viewModelScope.launch {
            CustomFunctionsForLocalDB.localDB.updateDao()
                .moveArchivedFolderToRegularFolderV10(folderID)
        }
    }

    fun onUnArchiveClickV9(
        context: Context,
        archiveScreenType: ArchiveScreenType,
        selectedURLOrFolderName: String,
        selectedURLOrFolderNote: String,
        onTaskCompleted: () -> Unit,
        selectedFolderID: Long
    ) {
        if (archiveScreenType == ArchiveScreenType.FOLDERS) {
            viewModelScope.launch {
                if (CustomFunctionsForLocalDB.localDB.readDao()
                        .doesThisRootFolderExists(
                            folderName = selectedURLOrFolderName
                        )
                ) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "folder name already exists, rename any one either to unarchive this folder",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    awaitAll(async {
                        CustomFunctionsForLocalDB.localDB.createDao()
                            .addANewFolder(
                                foldersTable = FoldersTable(
                                    folderName = selectedURLOrFolderName,
                                    infoForSaving = selectedURLOrFolderNote, parentFolderID = null,
                                    id = selectedFolderID, childFolderIDs = emptyList(), 
                                )
                            )
                    }, async {
                        CustomFunctionsForLocalDB.localDB.deleteDao()
                            .deleteAnArchiveFolder(folderName = selectedURLOrFolderName)
                    })
                    CustomFunctionsForLocalDB.localDB.updateDao()
                        .moveArchiveFolderBackToRootFolder(folderID = selectedFolderID)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "Unarchived successfully", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {
            viewModelScope.launch {
                if (CustomFunctionsForLocalDB.localDB.readDao()
                        .doesThisExistsInSavedLinks(webURL = selectedArchivedLinkData.value.webURL)
                ) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Link already exists in \"Saved Links\"",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    CustomFunctionsForLocalDB.localDB.createDao()
                        .addANewLinkToSavedLinksOrInFolders(
                            LinksTable(
                                title = selectedArchivedLinkData.value.title,
                                webURL = selectedArchivedLinkData.value.webURL,
                                baseURL = selectedArchivedLinkData.value.baseURL,
                                imgURL = selectedArchivedLinkData.value.imgURL,
                                infoForSaving = selectedArchivedLinkData.value.infoForSaving,
                                isLinkedWithSavedLinks = true,
                                isLinkedWithFolders = false,
                                keyOfLinkedFolder = 0,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = 0,
                                isLinkedWithArchivedFolder = false,
                                keyOfArchiveLinkedFolder = 0
                            )
                        )
                    CustomFunctionsForLocalDB.localDB.deleteDao()
                        .deleteALinkFromArchiveLinks(selectedArchivedLinkData.value.webURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Unarchived and moved the link to \"Saved Links\"",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        }
    }
}