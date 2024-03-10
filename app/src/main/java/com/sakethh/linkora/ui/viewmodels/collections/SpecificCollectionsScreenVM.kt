package com.sakethh.linkora.ui.viewmodels.collections

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.UriHandler
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.localDB.LocalDataBase
import com.sakethh.linkora.data.localDB.dto.ArchivedLinks
import com.sakethh.linkora.data.localDB.dto.FoldersTable
import com.sakethh.linkora.data.localDB.dto.ImportantLinks
import com.sakethh.linkora.data.localDB.dto.LinksTable
import com.sakethh.linkora.data.localDB.dto.RecentlyVisited
import com.sakethh.linkora.ui.screens.openInWeb
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM
import com.sakethh.linkora.ui.viewmodels.commonBtmSheets.OptionsBtmSheetType
import com.sakethh.linkora.ui.viewmodels.localDB.DeleteVM
import com.sakethh.linkora.ui.viewmodels.localDB.UpdateVM
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class MutableImportantLinks(
    val title: MutableState<String>,
    val webURL: MutableState<String>,
    val baseURL: MutableState<String>,
    val imgURL: MutableState<String>,
    val infoForSaving: MutableState<String>,
    var id: Long = 0,
)

open class SpecificCollectionsScreenVM(
    val updateVM: UpdateVM = UpdateVM(), private val deleteVM: DeleteVM = DeleteVM()
) : CollectionsScreenVM() {


    private val _folderLinksData = MutableStateFlow(
        emptyList<LinksTable>()
    )
    val folderLinksData = _folderLinksData.asStateFlow()

    private val _childFoldersData = MutableStateFlow(emptyList<FoldersTable>())
    val childFoldersData = _childFoldersData.asStateFlow()

    private val _savedLinksData = MutableStateFlow(
        emptyList<LinksTable>()
    )
    val savedLinksTable = _savedLinksData.asStateFlow()

    private val _impLinksData = MutableStateFlow(
        emptyList<ImportantLinks>()
    )
    val impLinksTable = _impLinksData.asStateFlow()

    private val _archiveFolderLinksData =
        MutableStateFlow(emptyList<LinksTable>())
    val archiveFoldersLinksData = _archiveFolderLinksData.asStateFlow()

    private val _archiveSubFolderData = MutableStateFlow(emptyList<FoldersTable>())
    val archiveSubFolderData = _archiveSubFolderData.asStateFlow()


    val selectedLinksID = mutableStateListOf<Long>()
    val selectedImpLinks = mutableStateListOf<String>()
    val areAllLinksChecked = mutableStateOf(false)
    fun removeAllLinkSelections() {
        val selectedIds = when (screenType.value) {
            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                savedLinksTable.value.map { it.id }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                folderLinksData.value.map { it.id }
            }

            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                impLinksTable.value.map { it.id }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                archiveFoldersLinksData.value.map { it.id }
            }

            else -> {
                emptyList()
            }
        }
        selectedLinksID.removeAll(selectedIds)
    }

    val impLinkDataForBtmSheet = MutableImportantLinks(
        title = mutableStateOf(""),
        webURL = mutableStateOf(""),
        baseURL = mutableStateOf(""),
        imgURL = mutableStateOf(""),
        infoForSaving = mutableStateOf("")
    )

    companion object {
        val screenType = mutableStateOf(SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN)
        val selectedBtmSheetType = mutableStateOf(OptionsBtmSheetType.LINK)
        val inARegularFolder = mutableStateOf(true)
    }

    private fun retrieveChildFoldersData() {
        viewModelScope.launch {
            LocalDataBase.localDB.readDao().getChildFoldersOfThisParentID(
                currentClickedFolderData.value.id
            ).collectLatest { it ->
                _childFoldersData.emit(
                    it
                )
            }
        }
    }

    fun moveMultipleLinksFromImpLinksToArchive() {
        viewModelScope.launch {
            selectedImpLinks.toList().forEach {
                LocalDataBase.localDB.updateDao()
                    .copyLinkFromImpTableToArchiveLinks(it)
                LocalDataBase.localDB.deleteDao().deleteALinkFromImpLinksBasedOnURL(it)
            }
        }
    }

    fun moveMultipleLinksFromLinksTableToArchive() {
        viewModelScope.launch {
            selectedLinksID.toList().forEach {
                LocalDataBase.localDB.updateDao()
                    .copyLinkFromLinksTableToArchiveLinks(it)
                LocalDataBase.localDB.deleteDao().deleteALinkFromLinksTable(it)
            }
        }
    }

    fun updateFolderData(folderID: Long) {
        viewModelScope.launch {
            currentClickedFolderData.value =
                LocalDataBase.localDB.readDao().getThisFolderData(folderID)
        }
    }

    init {
        viewModelScope.launch {
            changeRetrievedData(
                sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(SettingsScreenVM.Settings.selectedSortingType.value),
                folderID = currentClickedFolderData.value.id,
                isFoldersSortingSelected = true,
                isLinksSortingSelected = true
            )
            retrieveChildFoldersData()
        }
    }

    // ::
    fun changeRetrievedData(
        sortingPreferences: SettingsScreenVM.SortingPreferences,
        folderID: Long,
        screenType: SpecificScreenType = Companion.screenType.value,
        isFoldersSortingSelected: Boolean = false,
        isLinksSortingSelected: Boolean = false
    ) {
        when (screenType) {
            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                val sortedData = when (sortingPreferences) {
                    SettingsScreenVM.SortingPreferences.A_TO_Z -> LocalDataBase.localDB.savedLinksSorting()
                        .sortByAToZ()

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> LocalDataBase.localDB.savedLinksSorting()
                        .sortByZToA()

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> LocalDataBase.localDB.savedLinksSorting()
                        .sortByLatestToOldest()

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> LocalDataBase.localDB.savedLinksSorting()
                        .sortByOldestToLatest()
                }
                viewModelScope.launch {
                    sortedData.collectLatest {
                        _savedLinksData.emit(it)
                    }
                }
            }

            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                val sortedData = when (sortingPreferences) {
                    SettingsScreenVM.SortingPreferences.A_TO_Z -> LocalDataBase.localDB.importantLinksSorting()
                        .sortByAToZ()

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> LocalDataBase.localDB.importantLinksSorting()
                        .sortByZToA()

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> LocalDataBase.localDB.importantLinksSorting()
                        .sortByLatestToOldest()

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> LocalDataBase.localDB.importantLinksSorting()
                        .sortByOldestToLatest()
                }
                viewModelScope.launch {
                    sortedData.collectLatest {
                        _impLinksData.emit(it)
                    }
                }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                val (sortedLinks, sortedFolders) = when (sortingPreferences) {
                    SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                        Pair(
                            LocalDataBase.localDB.archivedFolderLinksSorting()
                                .sortLinksByAToZV10(folderID),
                            LocalDataBase.localDB.subFoldersSortingDao()
                                .sortSubFoldersByAToZ(parentFolderID = currentClickedFolderData.value.id)
                        )
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        Pair(
                            LocalDataBase.localDB.archivedFolderLinksSorting()
                                .sortLinksByAToZV10(folderID),
                            LocalDataBase.localDB.subFoldersSortingDao()
                                .sortSubFoldersByAToZ(parentFolderID = currentClickedFolderData.value.id)
                        )
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        Pair(
                            LocalDataBase.localDB.archivedFolderLinksSorting()
                                .sortLinksByAToZV10(folderID),
                            LocalDataBase.localDB.subFoldersSortingDao()
                                .sortSubFoldersByAToZ(parentFolderID = currentClickedFolderData.value.id)
                        )
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        Pair(
                            LocalDataBase.localDB.archivedFolderLinksSorting()
                                .sortLinksByAToZV10(folderID),
                            LocalDataBase.localDB.subFoldersSortingDao()
                                .sortSubFoldersByAToZ(parentFolderID = currentClickedFolderData.value.id)
                        )
                    }
                }
                viewModelScope.launch {
                    applySortedData(
                        isLinksSortingSelected = isLinksSortingSelected,
                        isFoldersSortingSelected = isFoldersSortingSelected,
                        sortedLinksDataFlow = { sortedLinks },
                        sortedFoldersDataFlow = { sortedFolders },
                        sortedLinksDataEmit = _archiveFolderLinksData,
                        sortedFoldersDataEmit = _archiveSubFolderData
                    )
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                val (sortedLinks, sortedFolders) = when (sortingPreferences) {
                    SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                        Pair(
                            LocalDataBase.localDB.regularFolderLinksSorting()
                                .sortByZToAV10(folderID),
                            LocalDataBase.localDB.subFoldersSortingDao()
                                .sortSubFoldersByAToZ(parentFolderID = currentClickedFolderData.value.id)
                        )
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        Pair(
                            LocalDataBase.localDB.regularFolderLinksSorting()
                                .sortByAToZV10(folderID),
                            LocalDataBase.localDB.subFoldersSortingDao()
                                .sortSubFoldersByAToZ(parentFolderID = currentClickedFolderData.value.id)
                        )
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        Pair(
                            LocalDataBase.localDB.regularFolderLinksSorting()
                                .sortByLatestToOldestV10(folderID),
                            LocalDataBase.localDB.subFoldersSortingDao()
                                .sortSubFoldersByAToZ(parentFolderID = currentClickedFolderData.value.id)
                        )
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        Pair(
                            LocalDataBase.localDB.regularFolderLinksSorting()
                                .sortByOldestToLatestV10(folderID),
                            LocalDataBase.localDB.subFoldersSortingDao()
                                .sortSubFoldersByAToZ(parentFolderID = currentClickedFolderData.value.id)
                        )
                    }
                }
                viewModelScope.launch {
                    applySortedData(
                        isLinksSortingSelected = isLinksSortingSelected,
                        isFoldersSortingSelected = isFoldersSortingSelected,
                        sortedLinksDataFlow = { sortedLinks },
                        sortedFoldersDataFlow = { sortedFolders },
                        sortedLinksDataEmit = _folderLinksData,
                        sortedFoldersDataEmit = _childFoldersData
                    )
                }
            }

            else -> {}
        }
    }

    private suspend inline fun <T1, T2> applySortedData(
        isLinksSortingSelected: Boolean,
        isFoldersSortingSelected: Boolean,
        sortedLinksDataFlow: () -> Flow<List<T1>>,
        sortedFoldersDataFlow: () -> Flow<List<T2>>,
        sortedLinksDataEmit: MutableStateFlow<List<T1>>,
        sortedFoldersDataEmit: MutableStateFlow<List<T2>>,
    ) {
        if (isLinksSortingSelected) {
            sortedLinksDataFlow().collectLatest {
                sortedLinksDataEmit.emit(it)
            }
        }
        if (isFoldersSortingSelected) {
            sortedFoldersDataFlow().collectLatest {
                sortedFoldersDataEmit.emit(it)
            }
        }
    }

    fun onArchiveClick(
        tempImpLinkData: ImportantLinks, context: Context, linkID: Long, onTaskCompleted: () -> Unit
    ) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    awaitAll(async {
                        updateVM.archiveLinkTableUpdater(archivedLinks = ArchivedLinks(
                            title = tempImpLinkData.title,
                            webURL = tempImpLinkData.webURL,
                            baseURL = tempImpLinkData.baseURL,
                            imgURL = tempImpLinkData.imgURL,
                            infoForSaving = tempImpLinkData.infoForSaving
                        ), context = context, onTaskCompleted = {
                            onTaskCompleted()
                        })
                    }, async {
                        LocalDataBase.localDB.deleteDao()
                            .deleteALinkFromImpLinksBasedOnURL(tempImpLinkData.webURL)
                    })
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                viewModelScope.launch {
                    awaitAll(async {
                        updateVM.archiveLinkTableUpdater(archivedLinks = ArchivedLinks(
                            title = tempImpLinkData.title,
                            webURL = tempImpLinkData.webURL,
                            baseURL = tempImpLinkData.baseURL,
                            imgURL = tempImpLinkData.imgURL,
                            infoForSaving = tempImpLinkData.infoForSaving
                        ), context = context, onTaskCompleted = {
                            onTaskCompleted()
                        })
                    })
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                viewModelScope.launch {
                    awaitAll(async {
                        updateVM.archiveLinkTableUpdater(archivedLinks = ArchivedLinks(
                            title = tempImpLinkData.title,
                            webURL = tempImpLinkData.webURL,
                            baseURL = tempImpLinkData.baseURL,
                            imgURL = tempImpLinkData.imgURL,
                            infoForSaving = tempImpLinkData.infoForSaving
                        ), context = context, onTaskCompleted = {
                            onTaskCompleted()
                        })
                    }, async {
                        LocalDataBase.localDB.deleteDao()
                            .deleteALinkFromSavedLinksBasedOnURL(webURL = tempImpLinkData.webURL)
                    })
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                viewModelScope.launch {
                    awaitAll(async {
                        updateVM.archiveLinkTableUpdater(archivedLinks = ArchivedLinks(
                            title = tempImpLinkData.title,
                            webURL = tempImpLinkData.webURL,
                            baseURL = tempImpLinkData.baseURL,
                            imgURL = tempImpLinkData.imgURL,
                            infoForSaving = tempImpLinkData.infoForSaving
                        ), context = context, onTaskCompleted = {
                            onTaskCompleted()
                        })
                    }, async {
                        LocalDataBase.localDB.deleteDao().deleteALinkFromLinksTable(linkID)
                    })
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            else -> {}
        }
    }

        fun onDeleteMultipleSelectedLinks() {
            selectedBtmSheetType.value = OptionsBtmSheetType.LINK
            when (screenType.value) {
                SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                    viewModelScope.launch {
                        selectedImpLinks.toList().forEach {
                            LocalDataBase.localDB.deleteDao().deleteALinkFromImpLinksBasedOnURL(it)
                        }
                    }
                }

                else -> {
                viewModelScope.launch {
                    selectedLinksID.toList().forEach {
                        LocalDataBase.localDB.deleteDao().deleteALinkFromLinksTable(it)
                    }
                }
            }
        }
    }

    fun onDeleteClick(
        folderID: Long,
        selectedWebURL: String,
        context: Context,
        onTaskCompleted: () -> Unit,
        folderName: String,
        linkID: Long,
        shouldShowToastOnCompletion: Boolean = true
    ) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.deleteDao()
                        .deleteALinkFromImpLinks(linkID = linkID)
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
                        .deleteALinkFromLinksTable(linkID = linkID)
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
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

            else -> {}
        }
        if (shouldShowToastOnCompletion) {
            Toast.makeText(
                context, "deleted the link successfully", Toast.LENGTH_SHORT
            ).show()
        }

    }

    fun onNoteDeleteCardClick(
        selectedWebURL: String, context: Context, folderID: Long, folderName: String, linkID: Long
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
                        LocalDataBase.localDB.deleteDao().deleteALinkInfoOfFolders(
                            linkID = linkID
                        )
                    } else {
                        LocalDataBase.localDB.deleteDao().deleteAFolderNote(
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
                        LocalDataBase.localDB.deleteDao().deleteALinkInfoOfFolders(
                            linkID = linkID
                        )
                    } else {
                        LocalDataBase.localDB.deleteDao().deleteAFolderNote(
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