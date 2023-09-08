package com.sakethh.linkora.screens.collections.specificCollectionScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.localDB.CustomFunctionsForLocalDB
import com.sakethh.linkora.localDB.ImportantLinks
import com.sakethh.linkora.localDB.LinksTable
import com.sakethh.linkora.localDB.RecentlyVisited
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SpecificScreenVM : ViewModel() {
    private val _folderLinksData = MutableStateFlow(emptyList<LinksTable>())
    val folderLinksData = _folderLinksData.asStateFlow()

    private val _savedLinksData = MutableStateFlow(emptyList<LinksTable>())
    val savedLinksTable = _savedLinksData.asStateFlow()

    private val _impLinksData = MutableStateFlow(emptyList<ImportantLinks>())
    val impLinksTable = _impLinksData.asStateFlow()

    private val _archiveFolderData = MutableStateFlow(emptyList<LinksTable>())
    val archiveFolderDataTable = _archiveFolderData.asStateFlow()

    val impLinkDataForBtmSheet = ImportantLinks(
        title = "",
        webURL = "",
        baseURL = "",
        imgURL = "",
        infoForSaving = ""
    )

    companion object {
        val currentClickedFolderName = mutableStateOf("")
        val screenType = mutableStateOf(SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN)
        val selectedArchiveFolderName = mutableStateOf("")
    }

    init {
        changeRetrievedData(
            sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(SettingsScreenVM.Settings.selectedSortingType.value),
            folderName = currentClickedFolderName.value
        )
    }

    fun changeRetrievedData(
        sortingPreferences: SettingsScreenVM.SortingPreferences,
        folderName: String,
    ) {
        when (screenType.value) {
            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                when (sortingPreferences) {
                    SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.savedLinksSorting().sortByAToZ()
                                .collect {
                                    _savedLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.savedLinksSorting().sortByZToA()
                                .collect {
                                    _savedLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.savedLinksSorting()
                                .sortByLatestToOldest().collect {
                                    _savedLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.savedLinksSorting()
                                .sortByOldestToLatest().collect {
                                    _savedLinksData.emit(it)
                                }
                        }
                    }
                }
            }

            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                when (sortingPreferences) {
                    SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.importantLinksSorting()
                                .sortByAToZ().collect {
                                    _impLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.importantLinksSorting()
                                .sortByZToA().collect {
                                    _impLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.importantLinksSorting()
                                .sortByLatestToOldest().collect {
                                    _impLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.importantLinksSorting()
                                .sortByOldestToLatest().collect {
                                    _impLinksData.emit(it)
                                }
                        }
                    }
                }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                when (sortingPreferences) {
                    SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.archivedFolderLinksSorting()
                                .sortByAToZ(folderName = folderName).collect {
                                    _archiveFolderData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.archivedFolderLinksSorting()
                                .sortByZToA(folderName = folderName).collect {
                                    _archiveFolderData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.archivedFolderLinksSorting()
                                .sortByLatestToOldest(folderName = folderName).collect {
                                    _archiveFolderData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.archivedFolderLinksSorting()
                                .sortByOldestToLatest(folderName = folderName).collect {
                                    _archiveFolderData.emit(it)
                                }
                        }
                    }
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                when (sortingPreferences) {
                    SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.regularFolderLinksSorting()
                                .sortByAToZ(folderName = folderName).collect {
                                    _folderLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.regularFolderLinksSorting()
                                .sortByZToA(folderName = folderName).collect {
                                    _folderLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.regularFolderLinksSorting()
                                .sortByLatestToOldest(folderName = folderName).collect {
                                    _folderLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.regularFolderLinksSorting()
                                .sortByOldestToLatest(folderName = folderName).collect {
                                    _folderLinksData.emit(it)
                                }
                        }
                    }
                }
            }

            SpecificScreenType.INTENT_ACTIVITY -> {

            }

            SpecificScreenType.ROOT_SCREEN -> {

            }
        }
    }

    fun onArchiveClick(
        tempImpLinkData: ImportantLinks, context: Context, folderName: String,
        onTaskCompleted: () -> Unit,
    ) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    kotlinx.coroutines.awaitAll(async {
                        CustomFunctionsForLocalDB().archiveLinkTableUpdater(
                            archivedLinks = com.sakethh.linkora.localDB.ArchivedLinks(
                                title = tempImpLinkData.title,
                                webURL = tempImpLinkData.webURL,
                                baseURL = tempImpLinkData.baseURL,
                                imgURL = tempImpLinkData.imgURL,
                                infoForSaving = tempImpLinkData.infoForSaving
                            ), context = context, onTaskCompleted = {
                                onTaskCompleted()
                            }
                        )
                    }, async {
                        CustomFunctionsForLocalDB.localDB.crudDao()
                            .deleteALinkFromImpLinks(webURL = tempImpLinkData.webURL)
                    })
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                viewModelScope.launch {
                    kotlinx.coroutines.awaitAll(async {
                        CustomFunctionsForLocalDB().archiveLinkTableUpdater(
                            archivedLinks = com.sakethh.linkora.localDB.ArchivedLinks(
                                title = tempImpLinkData.title,
                                webURL = tempImpLinkData.webURL,
                                baseURL = tempImpLinkData.baseURL,
                                imgURL = tempImpLinkData.imgURL,
                                infoForSaving = tempImpLinkData.infoForSaving
                            ), context = context, onTaskCompleted = {
                                onTaskCompleted()
                            }
                        )
                    })
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                viewModelScope.launch {
                    kotlinx.coroutines.awaitAll(async {
                        CustomFunctionsForLocalDB().archiveLinkTableUpdater(
                            archivedLinks = com.sakethh.linkora.localDB.ArchivedLinks(
                                title = tempImpLinkData.title,
                                webURL = tempImpLinkData.webURL,
                                baseURL = tempImpLinkData.baseURL,
                                imgURL = tempImpLinkData.imgURL,
                                infoForSaving = tempImpLinkData.infoForSaving
                            ), context = context, onTaskCompleted = {
                                onTaskCompleted()
                            }
                        )
                    }, async {
                        CustomFunctionsForLocalDB.localDB.crudDao()
                            .deleteALinkFromSavedLinks(webURL = tempImpLinkData.webURL)
                    })
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                viewModelScope.launch {
                    kotlinx.coroutines.awaitAll(async {
                        CustomFunctionsForLocalDB().archiveLinkTableUpdater(
                            archivedLinks = com.sakethh.linkora.localDB.ArchivedLinks(
                                title = tempImpLinkData.title,
                                webURL = tempImpLinkData.webURL,
                                baseURL = tempImpLinkData.baseURL,
                                imgURL = tempImpLinkData.imgURL,
                                infoForSaving = tempImpLinkData.infoForSaving
                            ), context = context, onTaskCompleted = {
                                onTaskCompleted()
                            }
                        )
                    }, async {
                        CustomFunctionsForLocalDB.localDB.crudDao()
                            .deleteALinkFromSpecificFolder(
                                folderName = folderName, webURL = tempImpLinkData.webURL
                            )
                    })
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            else -> {}
        }
    }

    fun onTitleChangeClickForLinks(
        folderName: String, newTitle: String, webURL: String,
        onTaskCompleted: () -> Unit,
    ) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .renameALinkTitleFromImpLinks(
                            webURL = webURL, newTitle = newTitle
                        )
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
                Unit
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .renameALinkTitleFromArchiveBasedFolderLinks(
                            webURL = webURL, newTitle = newTitle, folderName = folderName
                        )
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
                Unit
            }

            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .renameALinkTitleFromSavedLinks(
                            webURL = webURL, newTitle = newTitle
                        )
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
                Unit
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .renameALinkTitleFromFolders(
                            webURL = webURL, newTitle = newTitle, folderName = folderName
                        )
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
                Unit
            }

            else -> {}
        }

    }

    fun onNoteChangeClickForLinks(
        folderName: String, webURL: String, newNote: String,
        onTaskCompleted: () -> Unit,
    ) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .renameALinkInfoFromImpLinks(
                            webURL = webURL, newInfo = newNote
                        )
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
                Unit
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .renameALinkInfoFromArchiveBasedFolderLinks(
                            webURL = webURL, newInfo = newNote, folderName = folderName
                        )
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
                Unit
            }

            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .renameALinkInfoFromSavedLinks(
                            webURL = webURL, newInfo = newNote
                        )
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
                Unit
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .renameALinkInfoFromFolders(
                            webURL = webURL, newInfo = newNote, folderName = folderName
                        )
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
                Unit
            }

            else -> {}
        }

    }

    fun onDeleteClick(
        folderName: String, selectedWebURL: String, context: Context,
        onTaskCompleted: () -> Unit,
    ) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .deleteALinkFromImpLinks(webURL = selectedWebURL)
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .deleteALinkFromArchiveFolderBasedLinks(
                            webURL = selectedWebURL, archiveFolderName = folderName
                        )
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .deleteALinkFromSavedLinks(webURL = selectedWebURL)
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .deleteALinkFromSpecificFolder(
                            folderName = folderName, webURL = selectedWebURL
                        )
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            else -> {}
        }
        Toast.makeText(
            context, "deleted the link successfully", Toast.LENGTH_SHORT
        ).show()

    }

    fun onNoteDeleteCardClick(selectedWebURL: String, context: Context, folderName: String) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .deleteANoteFromImportantLinks(webURL = selectedWebURL)
                }.invokeOnCompletion {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .deleteALinkNoteFromArchiveBasedFolderLinks(
                            folderName = folderName, webURL = selectedWebURL
                        )
                }.invokeOnCompletion {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            }

            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .deleteALinkInfoFromSavedLinks(webURL = selectedWebURL)
                }.invokeOnCompletion {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .deleteALinkInfoOfFolders(
                            folderName = folderName, webURL = selectedWebURL
                        )
                }.invokeOnCompletion {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            }

            else -> {}
        }

    }

    fun onImportantLinkAdditionInTheTable(
        context: Context,
        onTaskCompleted: () -> Unit, tempImpLinkData: ImportantLinks,
    ) {
        viewModelScope.launch {
            if (CustomFunctionsForLocalDB.localDB.crudDao()
                    .doesThisExistsInImpLinks(webURL = tempImpLinkData.webURL)
            ) {
                CustomFunctionsForLocalDB.localDB.crudDao()
                    .deleteALinkFromImpLinks(webURL = tempImpLinkData.webURL)
                Toast.makeText(
                    context,
                    "removed link from the \"Important Links\" successfully",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                CustomFunctionsForLocalDB.localDB.crudDao().addANewLinkToImpLinks(
                    ImportantLinks(
                        title = tempImpLinkData.title,
                        webURL = tempImpLinkData.webURL,
                        baseURL = tempImpLinkData.baseURL,
                        imgURL = tempImpLinkData.imgURL,
                        infoForSaving = tempImpLinkData.infoForSaving
                    )
                )
                Toast.makeText(
                    context,
                    "added to the \"Important Links\" successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            OptionsBtmSheetVM().updateImportantCardData(tempImpLinkData.webURL)
        }.invokeOnCompletion {
            onTaskCompleted()
        }
    }

    fun onForceOpenInExternalBrowserClicked(
        recentlyVisited: RecentlyVisited,
        onTaskCompleted: () -> Unit,
    ) {
        viewModelScope.launch {
            if (!CustomFunctionsForLocalDB.localDB.crudDao()
                    .doesThisExistsInRecentlyVisitedLinks(webURL = recentlyVisited.webURL)
            ) {
                CustomFunctionsForLocalDB.localDB.crudDao()
                    .addANewLinkInRecentlyVisited(
                        recentlyVisited = RecentlyVisited(
                            title = recentlyVisited.title,
                            webURL = recentlyVisited.webURL,
                            baseURL = recentlyVisited.baseURL,
                            imgURL = recentlyVisited.imgURL,
                            infoForSaving = recentlyVisited.infoForSaving
                        )
                    )
            }
        }.invokeOnCompletion {
            onTaskCompleted()
        }
    }
}

enum class SpecificScreenType {
    IMPORTANT_LINKS_SCREEN, ARCHIVED_FOLDERS_LINKS_SCREEN, SAVED_LINKS_SCREEN, SPECIFIC_FOLDER_LINKS_SCREEN, INTENT_ACTIVITY, ROOT_SCREEN
}