package com.sakethh.linkora.screens.collections.specificCollectionScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.UriHandler
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.btmSheet.OptionsBtmSheetType
import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.customWebTab.openInWeb
import com.sakethh.linkora.localDB.LocalDataBase
import com.sakethh.linkora.localDB.commonVMs.DeleteVM
import com.sakethh.linkora.localDB.commonVMs.UpdateVM
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


open class SpecificCollectionsScreenVM(
    val updateVM: UpdateVM = UpdateVM(),
    private val deleteVM: DeleteVM = DeleteVM()
) :
    CollectionsScreenVM() {
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
        val screenType = mutableStateOf(SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN)
        val selectedBtmSheetType = mutableStateOf(OptionsBtmSheetType.LINK)
        val inARegularFolder = mutableStateOf(true)
    }

    fun retrieveChildFoldersData() {
        viewModelScope.launch {
            LocalDataBase.localDB.readDao().getChildFoldersOfThisParentID(
                currentClickedFolderData.value.id
            ).collectLatest {
                _childFoldersData.emit(it)
            }
        }
    }

    fun updateFolderData(folderID: Long) {
        viewModelScope.launch {
            currentClickedFolderData.value = LocalDataBase.localDB.readDao()
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
                            LocalDataBase.localDB.savedLinksSorting().sortByAToZ()
                                .collectLatest {
                                    _savedLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            LocalDataBase.localDB.savedLinksSorting().sortByZToA()
                                .collectLatest {
                                    _savedLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            LocalDataBase.localDB.savedLinksSorting()
                                .sortByLatestToOldest().collectLatest {
                                    _savedLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            LocalDataBase.localDB.savedLinksSorting()
                                .sortByOldestToLatest().collectLatest {
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
                            LocalDataBase.localDB.importantLinksSorting()
                                .sortByAToZ().collectLatest {
                                    _impLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            LocalDataBase.localDB.importantLinksSorting()
                                .sortByZToA().collectLatest {
                                    _impLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            LocalDataBase.localDB.importantLinksSorting()
                                .sortByLatestToOldest().collectLatest {
                                    _impLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            LocalDataBase.localDB.importantLinksSorting()
                                .sortByOldestToLatest().collectLatest {
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
                                LocalDataBase.localDB.archivedFolderLinksSorting()
                                    .sortLinksByAToZV10(folderID = folderID).collectLatest {
                                        _archiveFolderData.emit(it)
                                    }
                            }, async {
                                LocalDataBase.localDB.archivedFolderLinksSorting()
                                    .sortSubFoldersByAToZ(parentFolderID = currentClickedFolderData.value.id)
                                    .collectLatest {
                                        _archiveSubFolderData.emit(it)
                                    }
                            })
                        }
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                LocalDataBase.localDB.archivedFolderLinksSorting()
                                    .sortLinksByZToAV10(folderID = folderID).collectLatest {
                                        _archiveFolderData.emit(it)
                                    }
                            }, async {
                                LocalDataBase.localDB.archivedFolderLinksSorting()
                                    .sortSubFoldersByZToA(parentFolderID = currentClickedFolderData.value.id)
                                    .collectLatest {
                                        _archiveSubFolderData.emit(it)
                                    }
                            })

                        }
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                LocalDataBase.localDB.archivedFolderLinksSorting()
                                    .sortLinksByLatestToOldestV10(folderID = folderID)
                                    .collectLatest {
                                        _archiveFolderData.emit(it)
                                    }
                            }, async {
                                LocalDataBase.localDB.archivedFolderLinksSorting()
                                    .sortSubFoldersByLatestToOldest(parentFolderID = currentClickedFolderData.value.id)
                                    .collectLatest {
                                        _archiveSubFolderData.emit(it)
                                    }
                            })
                        }
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                LocalDataBase.localDB.archivedFolderLinksSorting()
                                    .sortLinksByOldestToLatestV10(folderID = folderID)
                                    .collectLatest {
                                        _archiveFolderData.emit(it)
                                    }
                            }, async {
                                LocalDataBase.localDB.archivedFolderLinksSorting()
                                    .sortSubFoldersByOldestToLatest(parentFolderID = currentClickedFolderData.value.id)
                                    .collectLatest {
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
                            LocalDataBase.localDB.regularFolderLinksSorting()
                                .sortByAToZV10(folderID = folderID).collectLatest {
                                    _folderLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            LocalDataBase.localDB.regularFolderLinksSorting()
                                .sortByZToAV10(folderID = folderID).collectLatest {
                                    _folderLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            LocalDataBase.localDB.regularFolderLinksSorting()
                                .sortByLatestToOldestV10(folderID = folderID).collectLatest {
                                    _folderLinksData.emit(it)
                                }

                        }
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            LocalDataBase.localDB.regularFolderLinksSorting()
                                .sortByOldestToLatestV10(folderID = folderID).collectLatest {
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
        onTaskCompleted: () -> Unit, folderName: String
    ) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    awaitAll(async {
                        updateVM.archiveLinkTableUpdater(
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
                        LocalDataBase.localDB.deleteDao()
                            .deleteALinkFromImpLinks(webURL = tempImpLinkData.webURL)
                    })
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                viewModelScope.launch {
                    kotlinx.coroutines.awaitAll(async {
                        updateVM.archiveLinkTableUpdater(
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
                        updateVM.archiveLinkTableUpdater(
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
                        LocalDataBase.localDB.deleteDao()
                            .deleteALinkFromSavedLinks(webURL = tempImpLinkData.webURL)
                    })
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                viewModelScope.launch {
                    kotlinx.coroutines.awaitAll(async {
                        updateVM.archiveLinkTableUpdater(
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
                        LocalDataBase.localDB.deleteDao()
                            .deleteALinkFromSpecificFolderV10(
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

    fun onDeleteClick(
        folderID: Long, selectedWebURL: String, context: Context,
        onTaskCompleted: () -> Unit, folderName: String, linkID: Long
    ) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.deleteDao()
                        .deleteALinkFromImpLinks(webURL = selectedWebURL)
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                viewModelScope.launch {
                    if (selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        LocalDataBase.localDB.deleteDao().deleteALinkFromLinksTable(linkID)
                    } else {
                        deleteVM.onRegularFolderDeleteClick(folderID)
                    }
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.deleteDao()
                        .deleteALinkFromSavedLinks(webURL = selectedWebURL)
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                viewModelScope.launch {
                    if (selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        LocalDataBase.localDB.deleteDao()
                            .deleteALinkFromLinksTable(linkID)
                    } else {
                        deleteVM.onRegularFolderDeleteClick(folderID)
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
        folderName: String, linkID: Long
    ) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.deleteDao()
                        .deleteANoteFromImportantLinks(webURL = selectedWebURL)
                }.invokeOnCompletion {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                viewModelScope.launch {
                    if (selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        LocalDataBase.localDB.deleteDao()
                            .deleteALinkInfoOfFolders(
                                linkID = linkID
                            )
                    } else {
                        LocalDataBase.localDB.deleteDao()
                            .deleteAFolderNote(
                                folderID = folderID
                            )
                    }
                }.invokeOnCompletion {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            }

            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.deleteDao()
                        .deleteALinkInfoFromSavedLinks(webURL = selectedWebURL)
                }.invokeOnCompletion {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                viewModelScope.launch {
                    if (selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        LocalDataBase.localDB.deleteDao()
                            .deleteALinkInfoOfFolders(
                                linkID = linkID
                            )
                    } else {
                        LocalDataBase.localDB.deleteDao()
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
            if (LocalDataBase.localDB.readDao()
                    .doesThisExistsInImpLinks(webURL = tempImpLinkData.webURL)
            ) {
                LocalDataBase.localDB.deleteDao()
                    .deleteALinkFromImpLinks(webURL = tempImpLinkData.webURL)
                Toast.makeText(
                    context,
                    "removed link from the \"Important Links\" successfully",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                LocalDataBase.localDB.createDao().addANewLinkToImpLinks(
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