package com.sakethh.linkora.screens.collections.specificCollectionScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.UriHandler
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.btmSheet.OptionsBtmSheetType
import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.customWebTab.openInWeb
import com.sakethh.linkora.localDB.CustomFunctionsForLocalDB
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.localDB.dto.RecentlyVisited
import com.sakethh.linkora.screens.collections.CollectionsScreenVM
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


open class SpecificCollectionsScreenVM : CollectionsScreenVM() {
    private val _folderLinksData = MutableStateFlow(emptyList<LinksTable>())
    val folderLinksData = _folderLinksData.asStateFlow()

    private val _childFoldersData = MutableStateFlow(emptyList<FoldersTable>())
    val childFoldersData = _childFoldersData.asStateFlow()

    private val _savedLinksData = MutableStateFlow(emptyList<LinksTable>())
    val savedLinksTable = _savedLinksData.asStateFlow()

    private val _impLinksData = MutableStateFlow(emptyList<ImportantLinks>())
    val impLinksTable = _impLinksData.asStateFlow()

    private val _archiveFolderData = MutableStateFlow(emptyList<LinksTable>())
    val archiveFolderDataTable = _archiveFolderData.asStateFlow()

    private val _archiveSubFolderData = MutableStateFlow(emptyList<FoldersTable>())
    val archiveSubFolderData = _archiveSubFolderData.asStateFlow()


    val impLinkDataForBtmSheet = ImportantLinks(
        title = "",
        webURL = "",
        baseURL = "",
        imgURL = "",
        infoForSaving = ""
    )

    companion object {
        val currentClickedFolderData =
            mutableStateOf(
                FoldersTable(
                    "",
                    "",
                    0,
                    0,
                    childFolderIDs = emptyList(),
                )
            )
        val screenType = mutableStateOf(SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN)
        var selectedArchiveFolderID: Long = 0
        var isSelectedV9 = false
        val selectedBtmSheetType = mutableStateOf(OptionsBtmSheetType.LINK)
        val inARegularFolder = mutableStateOf(true)
    }

    fun retrieveChildFoldersData() {
        viewModelScope.launch {
            CustomFunctionsForLocalDB.localDB.readDao().getChildFoldersOfThisParentID(
                currentClickedFolderData.value.id
            ).collect {
                _childFoldersData.emit(it)
            }
        }
    }

    fun updateFolderData(folderID: Long) {
        viewModelScope.launch {
            currentClickedFolderData.value = CustomFunctionsForLocalDB.localDB.readDao()
                .getThisFolderData(folderID)
        }
    }

    fun changeRetrievedData(
        sortingPreferences: SettingsScreenVM.SortingPreferences,
        folderID: Long,
        folderName: String,
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
                            awaitAll(async {
                                if (isSelectedV9) {
                                    CustomFunctionsForLocalDB.localDB.archivedFolderLinksSorting()
                                        .sortLinksByAToZV9(folderID = folderName).collect {
                                            _archiveFolderData.emit(it)
                                        }
                                } else {
                                    CustomFunctionsForLocalDB.localDB.archivedFolderLinksSorting()
                                        .sortLinksByAToZV10(folderID = folderID).collect {
                                            _archiveFolderData.emit(it)
                                        }
                                }
                            }, async {
                                CustomFunctionsForLocalDB.localDB.archivedFolderLinksSorting()
                                    .sortSubFoldersByAToZ(parentFolderID = currentClickedFolderData.value.id)
                                    .collect {
                                        _archiveSubFolderData.emit(it)
                                    }
                            })
                        }
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                if (isSelectedV9) {
                                    CustomFunctionsForLocalDB.localDB.archivedFolderLinksSorting()
                                        .sortLinksByZToAV9(folderID = folderName).collect {
                                            _archiveFolderData.emit(it)
                                        }
                                } else {
                                    CustomFunctionsForLocalDB.localDB.archivedFolderLinksSorting()
                                        .sortLinksByZToAV10(folderID = folderID).collect {
                                            _archiveFolderData.emit(it)
                                        }
                                }
                            }, async {
                                CustomFunctionsForLocalDB.localDB.archivedFolderLinksSorting()
                                    .sortSubFoldersByZToA(parentFolderID = currentClickedFolderData.value.id)
                                    .collect {
                                        _archiveSubFolderData.emit(it)
                                    }
                            })

                        }
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                if (isSelectedV9) {
                                    CustomFunctionsForLocalDB.localDB.archivedFolderLinksSorting()
                                        .sortLinksByLatestToOldestV9(folderID = folderName)
                                        .collect {
                                            _archiveFolderData.emit(it)
                                        }
                                } else {
                                    CustomFunctionsForLocalDB.localDB.archivedFolderLinksSorting()
                                        .sortLinksByLatestToOldestV10(folderID = folderID).collect {
                                            _archiveFolderData.emit(it)
                                        }
                                }
                            }, async {
                                CustomFunctionsForLocalDB.localDB.archivedFolderLinksSorting()
                                    .sortSubFoldersByLatestToOldest(parentFolderID = currentClickedFolderData.value.id)
                                    .collect {
                                        _archiveSubFolderData.emit(it)
                                    }
                            })
                        }
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                if (isSelectedV9) {
                                    CustomFunctionsForLocalDB.localDB.archivedFolderLinksSorting()
                                        .sortLinksByOldestToLatestV9(folderID = folderName)
                                        .collect {
                                            _archiveFolderData.emit(it)
                                        }
                                } else {
                                    CustomFunctionsForLocalDB.localDB.archivedFolderLinksSorting()
                                        .sortLinksByOldestToLatestV10(folderID = folderID).collect {
                                            _archiveFolderData.emit(it)
                                        }
                                }
                            }, async {
                                CustomFunctionsForLocalDB.localDB.archivedFolderLinksSorting()
                                    .sortSubFoldersByOldestToLatest(parentFolderID = currentClickedFolderData.value.id)
                                    .collect {
                                        _archiveSubFolderData.emit(it)
                                    }
                            })
                        }
                    }
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                when (sortingPreferences) {
                    SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                        viewModelScope.launch {
                            if (!isSelectedV9) {
                                CustomFunctionsForLocalDB.localDB.regularFolderLinksSorting()
                                    .sortByAToZV10(folderID = folderID).collect {
                                        _folderLinksData.emit(it)
                                    }
                            } else {
                                CustomFunctionsForLocalDB.localDB.regularFolderLinksSorting()
                                    .sortByAToZV9(folderID = folderName).collect {
                                        _folderLinksData.emit(it)
                                    }
                            }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            if (!isSelectedV9) {
                                CustomFunctionsForLocalDB.localDB.regularFolderLinksSorting()
                                    .sortByZToAV10(folderID = folderID).collect {
                                        _folderLinksData.emit(it)
                                    }
                            } else {
                                CustomFunctionsForLocalDB.localDB.regularFolderLinksSorting()
                                    .sortByZToAV9(folderID = folderName).collect {
                                        _folderLinksData.emit(it)
                                    }
                            }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            if (!isSelectedV9) {
                                CustomFunctionsForLocalDB.localDB.regularFolderLinksSorting()
                                    .sortByLatestToOldestV10(folderID = folderID).collect {
                                        _folderLinksData.emit(it)
                                    }
                            } else {
                                CustomFunctionsForLocalDB.localDB.regularFolderLinksSorting()
                                    .sortByLatestToOldestV9(folderID = folderName).collect {
                                        _folderLinksData.emit(it)
                                    }
                            }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            if (!isSelectedV9) {
                                CustomFunctionsForLocalDB.localDB.regularFolderLinksSorting()
                                    .sortByOldestToLatestV10(folderID = folderID).collect {
                                        _folderLinksData.emit(it)
                                    }
                            } else {
                                CustomFunctionsForLocalDB.localDB.regularFolderLinksSorting()
                                    .sortByOldestToLatestV9(folderID = folderName).collect {
                                        _folderLinksData.emit(it)
                                    }
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
        onTaskCompleted: () -> Unit, folderName: String
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
                        if (!isSelectedV9) {
                            CustomFunctionsForLocalDB.localDB.deleteDao()
                                .deleteALinkFromSpecificFolderV10(
                                    folderID = folderID, webURL = tempImpLinkData.webURL
                                )
                        } else {
                            CustomFunctionsForLocalDB.localDB.deleteDao()
                                .deleteALinkFromSpecificFolderV9(
                                    folderName = folderName, webURL = tempImpLinkData.webURL
                                )
                        }
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
        onTaskCompleted: () -> Unit, folderName: String
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
                    if (isSelectedV9) {
                        CustomFunctionsForLocalDB.localDB.updateDao()
                            .renameALinkTitleFromArchiveBasedFolderLinksV9(
                                webURL = webURL,
                                newTitle = newTitle,
                                folderName = currentClickedFolderData.value.folderName
                            )
                    } else {
                        CustomFunctionsForLocalDB.localDB.updateDao()
                            .renameALinkTitleFromFoldersV10(
                                webURL = webURL,
                                newTitle = newTitle,
                                folderID = folderID
                            )
                    }
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
                if (isSelectedV9) {
                    viewModelScope.launch {
                        CustomFunctionsForLocalDB.localDB.updateDao()
                            .renameALinkTitleFromFoldersV9(webURL, newTitle, folderName)
                    }
                } else {
                    viewModelScope.launch {
                        CustomFunctionsForLocalDB.localDB.updateDao()
                            .renameALinkTitleFromFoldersV10(
                                webURL = webURL, newTitle = newTitle, folderID = folderID
                            )

                    }.invokeOnCompletion {
                        onTaskCompleted()
                    }
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
                    if (isSelectedV9) {
                        CustomFunctionsForLocalDB.localDB.updateDao()
                            .renameALinkInfoFromArchiveBasedFolderLinksV9(
                                webURL = webURL,
                                newInfo = newNote,
                                folderName = currentClickedFolderData.value.folderName
                            )
                    } else {
                        CustomFunctionsForLocalDB.localDB.updateDao().renameALinkInfoFromFoldersV10(
                            webURL = webURL,
                            newInfo = newNote,
                            folderID = folderID
                        )
                    }
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
                    if (isSelectedV9) {
                        CustomFunctionsForLocalDB.localDB.updateDao().renameALinkInfoFromFoldersV9(
                            webURL,
                            newNote,
                            folderName = currentClickedFolderData.value.folderName
                        )
                    } else {
                        CustomFunctionsForLocalDB.localDB.updateDao()
                            .renameALinkInfoFromFoldersV10(
                                webURL = webURL, newInfo = newNote, folderID = folderID
                            )
                    }
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
        onTaskCompleted: () -> Unit, folderName: String
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
                    if (selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        if (!isSelectedV9) {
                            CustomFunctionsForLocalDB.localDB.deleteDao()
                                .deleteALinkFromArchiveFolderBasedLinksV10(
                                    webURL = selectedWebURL, archiveFolderID = folderID
                                )
                        } else {
                            CustomFunctionsForLocalDB.localDB.deleteDao()
                                .deleteALinkFromArchiveFolderBasedLinksV9(
                                    folderName = folderName, webURL = selectedWebURL
                                )
                        }
                    } else {
                        onRegularFolderDeleteClick(folderID, folderName)
                    }
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
                    if (selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        if (!isSelectedV9) {
                            CustomFunctionsForLocalDB.localDB.deleteDao()
                                .deleteALinkFromSpecificFolderV10(
                                    folderID = folderID, webURL = selectedWebURL
                                )
                        } else {
                            CustomFunctionsForLocalDB.localDB.deleteDao()
                                .deleteALinkFromSpecificFolderV9(
                                    folderName = folderName, webURL = selectedWebURL
                                )
                        }
                    } else {
                        onRegularFolderDeleteClick(folderID, folderName)
                    }
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

    fun onNoteDeleteCardClick(
        selectedWebURL: String,
        context: Context,
        folderID: Long,
        folderName: String
    ) {
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
                    if (isSelectedV9) {
                        CustomFunctionsForLocalDB.localDB.deleteDao()
                            .deleteALinkNoteFromArchiveBasedFolderLinksV9(
                                folderName = folderName, webURL = selectedWebURL
                            )
                    } else {
                        CustomFunctionsForLocalDB.localDB.deleteDao()
                            .deleteALinkNoteFromArchiveBasedFolderLinksV10(
                                folderID = folderID, webURL = selectedWebURL
                            )
                    }
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
                    if (selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        if (!isSelectedV9) {
                            CustomFunctionsForLocalDB.localDB.deleteDao()
                                .deleteALinkInfoOfFoldersV10(
                                    folderID = folderID, webURL = selectedWebURL
                                )
                        } else {
                            CustomFunctionsForLocalDB.localDB.deleteDao()
                                .deleteALinkInfoOfFoldersV9(
                                    folderName = folderName, webURL = selectedWebURL
                                )
                        }
                    } else {
                        CustomFunctionsForLocalDB.localDB.deleteDao()
                            .deleteAFolderNote(
                                folderID = folderID
                            )
                    }
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