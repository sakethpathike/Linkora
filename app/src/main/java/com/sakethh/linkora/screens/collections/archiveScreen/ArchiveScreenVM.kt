package com.sakethh.linkora.screens.collections.archiveScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.sakethh.linkora.localDB.ArchivedFolders
import com.sakethh.linkora.localDB.ArchivedLinks
import com.sakethh.linkora.localDB.CustomFunctionsForLocalDB
import com.sakethh.linkora.localDB.FoldersTable
import com.sakethh.linkora.localDB.LinksTable
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

    private val _archiveFoldersData = MutableStateFlow(emptyList<ArchivedFolders>())
    val archiveFoldersData = _archiveFoldersData.asStateFlow()

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
    ) {
        if (archiveScreenType == ArchiveScreenType.LINKS) {
            viewModelScope.launch {
                CustomFunctionsForLocalDB.localDB.crudDao()
                    .renameALinkInfoFromArchiveLinks(webURL, newNote)
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {
            viewModelScope.launch {
                CustomFunctionsForLocalDB.localDB.crudDao()
                    .renameArchivedFolderNote(
                        folderName = webURL,
                        newNote = newNote
                    )
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        }
    }

    fun onTitleChangeClickForLinks(
        archiveScreenType: ArchiveScreenType,
        selectedURLOrFolderName: String,
        newTitle: String,
        webURL: String,
        onTaskCompleted: () -> Unit,
    ) {
        if (archiveScreenType == ArchiveScreenType.LINKS) {
            viewModelScope.launch {
                CustomFunctionsForLocalDB.localDB.crudDao()
                    .renameALinkTitleFromArchiveLinks(webURL = webURL, newTitle = newTitle)
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {
            viewModelScope.launch {
                CustomFunctionsForLocalDB.localDB.crudDao()
                    .renameAFolderArchiveName(selectedURLOrFolderName, newTitle)
                CustomFunctionsForLocalDB.localDB.crudDao()
                    .renameFolderNameForExistingArchivedFolderData(
                        selectedURLOrFolderName,
                        newTitle
                    )
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
                        CustomFunctionsForLocalDB.localDB.archivedFolderSorting().sortByAToZ()
                            .collect {
                                _archiveFoldersData.emit(it)
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
                        CustomFunctionsForLocalDB.localDB.archivedFolderSorting().sortByZToA()
                            .collect {
                                _archiveFoldersData.emit(it)
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
                            .sortByLatestToOldest().collect {
                                _archiveFoldersData.emit(it)
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
                            .sortByOldestToLatest().collect {
                                _archiveFoldersData.emit(it)
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
                CustomFunctionsForLocalDB.localDB.crudDao()
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
            CustomFunctionsForLocalDB().archiveFolderTableUpdater(
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
    ) {
        if (archiveScreenType == ArchiveScreenType.FOLDERS) {
            viewModelScope.launch {
                CustomFunctionsForLocalDB.localDB.crudDao()
                    .deleteArchiveFolderNote(folderName = selectedURLOrFolderName)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {
            viewModelScope.launch {
                CustomFunctionsForLocalDB.localDB.crudDao()
                    .deleteANoteFromArchiveLinks(webURL = selectedURLOrFolderName)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        }
    }

    fun onUnArchiveClick(
        context: Context,
        archiveScreenType: ArchiveScreenType,
        selectedURLOrFolderName: String,
        selectedURLOrFolderNote: String,
        onTaskCompleted: () -> Unit,
    ) {
        if (archiveScreenType == ArchiveScreenType.FOLDERS) {
            viewModelScope.launch {
                if (CustomFunctionsForLocalDB.localDB.crudDao()
                        .doesThisFolderExists(folderName = selectedURLOrFolderName)
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
                        CustomFunctionsForLocalDB.localDB.crudDao()
                            .addANewFolder(
                                foldersTable = FoldersTable(
                                    folderName = selectedURLOrFolderName,
                                    infoForSaving = selectedURLOrFolderNote
                                )
                            )
                    }, async {
                        CustomFunctionsForLocalDB.localDB.crudDao()
                            .deleteAnArchiveFolder(folderName = selectedURLOrFolderName)
                    })
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .moveArchiveFolderBackToFolder(folderName = selectedURLOrFolderName)
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
                if (CustomFunctionsForLocalDB.localDB.crudDao()
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
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .addANewLinkToSavedLinksOrInFolders(
                            LinksTable(
                                title = selectedArchivedLinkData.value.title,
                                webURL = selectedArchivedLinkData.value.webURL,
                                baseURL = selectedArchivedLinkData.value.baseURL,
                                imgURL = selectedArchivedLinkData.value.imgURL,
                                infoForSaving = selectedArchivedLinkData.value.infoForSaving,
                                isLinkedWithSavedLinks = true,
                                isLinkedWithFolders = false,
                                keyOfLinkedFolder = "",
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "",
                                isLinkedWithArchivedFolder = false,
                                keyOfArchiveLinkedFolder = ""
                            )
                        )
                    CustomFunctionsForLocalDB.localDB.crudDao()
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