package com.sakethh.linkora.screens.collections.specificCollectionScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.UriHandler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.customWebTab.openInWeb
import com.sakethh.linkora.localDB.CustomFunctionsForLocalDB
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.localDB.dto.RecentlyVisited
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


open class SpecificScreenVM : ViewModel() {
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
        val currentClickedFolderData = mutableStateOf(FoldersTable("", "", 0, 0, emptyList()))
        val screenType = mutableStateOf(SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN)
        var selectedArchiveFolderID: Long = 0
    }

    fun changeRetrievedData(
        sortingPreferences: SettingsScreenVM.SortingPreferences,
        folderID: Long,
        screenType: SpecificScreenType = Companion.screenType.value,
    ) {
        when (screenType) {
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
                                .sortByAToZ(folderID = folderID).collect {
                                    _archiveFolderData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.archivedFolderLinksSorting()
                                .sortByZToA(folderID = folderID).collect {
                                    _archiveFolderData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.archivedFolderLinksSorting()
                                .sortByLatestToOldest(folderID = folderID).collect {
                                    _archiveFolderData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.archivedFolderLinksSorting()
                                .sortByOldestToLatest(folderID = folderID).collect {
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
                                .sortByAToZ(folderID = folderID).collect {
                                    _folderLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.regularFolderLinksSorting()
                                .sortByZToA(folderID = folderID).collect {
                                    _folderLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.regularFolderLinksSorting()
                                .sortByLatestToOldest(folderID = folderID).collect {
                                    _folderLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            CustomFunctionsForLocalDB.localDB.regularFolderLinksSorting()
                                .sortByOldestToLatest(folderID = folderID).collect {
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
        tempImpLinkData: ImportantLinks, context: Context, folderID: Long,
        onTaskCompleted: () -> Unit,
    ) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    kotlinx.coroutines.awaitAll(async {
                        CustomFunctionsForLocalDB().archiveLinkTableUpdater(
                            archivedLinks = ArchivedLinks(
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
                        CustomFunctionsForLocalDB.localDB.deleteDao()
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
                            archivedLinks = ArchivedLinks(
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
                            archivedLinks = ArchivedLinks(
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
                        CustomFunctionsForLocalDB.localDB.deleteDao()
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
                            archivedLinks = ArchivedLinks(
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
                        CustomFunctionsForLocalDB.localDB.deleteDao()
                            .deleteALinkFromSpecificFolder(
                                folderID = folderID, webURL = tempImpLinkData.webURL
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
        folderID: Long, newTitle: String, webURL: String,
        onTaskCompleted: () -> Unit,
    ) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.updateDao()
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
                    CustomFunctionsForLocalDB.localDB.updateDao()
                        .renameALinkTitleFromArchiveBasedFolderLinks(
                            webURL = webURL, newTitle = newTitle, folderID = folderID
                        )
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
                Unit
            }

            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.updateDao()
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
                    CustomFunctionsForLocalDB.localDB.updateDao()
                        .renameALinkTitleFromFolders(
                            webURL = webURL, newTitle = newTitle, folderID = folderID
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
        folderID: Long, webURL: String, newNote: String,
        onTaskCompleted: () -> Unit,
    ) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.updateDao()
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
                    CustomFunctionsForLocalDB.localDB.updateDao()
                        .renameALinkInfoFromArchiveBasedFolderLinks(
                            webURL = webURL, newInfo = newNote, folderID = folderID
                        )
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
                Unit
            }

            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.updateDao()
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
                    CustomFunctionsForLocalDB.localDB.updateDao()
                        .renameALinkInfoFromFolders(
                            webURL = webURL, newInfo = newNote, folderID = folderID
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
        folderID: Long, selectedWebURL: String, context: Context,
        onTaskCompleted: () -> Unit,
    ) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.deleteDao()
                        .deleteALinkFromImpLinks(webURL = selectedWebURL)
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.deleteDao()
                        .deleteALinkFromArchiveFolderBasedLinks(
                            webURL = selectedWebURL, archiveFolderID = folderID
                        )
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.deleteDao()
                        .deleteALinkFromSavedLinks(webURL = selectedWebURL)
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.deleteDao()
                        .deleteALinkFromSpecificFolder(
                            folderID = folderID, webURL = selectedWebURL
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

    fun onNoteDeleteCardClick(selectedWebURL: String, context: Context, folderID: Long) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.deleteDao()
                        .deleteANoteFromImportantLinks(webURL = selectedWebURL)
                }.invokeOnCompletion {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.deleteDao()
                        .deleteALinkNoteFromArchiveBasedFolderLinks(
                            folderID = folderID, webURL = selectedWebURL
                        )
                }.invokeOnCompletion {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            }

            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.deleteDao()
                        .deleteALinkInfoFromSavedLinks(webURL = selectedWebURL)
                }.invokeOnCompletion {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.deleteDao()
                        .deleteALinkInfoOfFolders(
                            folderID = folderID, webURL = selectedWebURL
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
            if (CustomFunctionsForLocalDB.localDB.readDao()
                    .doesThisExistsInImpLinks(webURL = tempImpLinkData.webURL)
            ) {
                CustomFunctionsForLocalDB.localDB.deleteDao()
                    .deleteALinkFromImpLinks(webURL = tempImpLinkData.webURL)
                Toast.makeText(
                    context,
                    "removed link from the \"Important Links\" successfully",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                CustomFunctionsForLocalDB.localDB.createDao().addANewLinkToImpLinks(
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

    fun onLinkClick(
        recentlyVisited: RecentlyVisited,
        onTaskCompleted: () -> Unit,
        context: Context,
        uriHandler: UriHandler,
        forceOpenInExternalBrowser: Boolean,
    ) {
        viewModelScope.launch {
            openInWeb(
                recentlyVisitedData = RecentlyVisited(
                    title = recentlyVisited.title,
                    webURL = recentlyVisited.webURL,
                    baseURL = recentlyVisited.baseURL,
                    imgURL = recentlyVisited.imgURL,
                    infoForSaving = recentlyVisited.infoForSaving
                ),
                context = context,
                uriHandler = uriHandler,
                forceOpenInExternalBrowser = forceOpenInExternalBrowser
            )
        }.invokeOnCompletion {
            onTaskCompleted()
        }
    }
}

enum class SpecificScreenType {
    IMPORTANT_LINKS_SCREEN, ARCHIVED_FOLDERS_LINKS_SCREEN, SAVED_LINKS_SCREEN, SPECIFIC_FOLDER_LINKS_SCREEN, INTENT_ACTIVITY, ROOT_SCREEN
}