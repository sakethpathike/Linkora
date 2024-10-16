package com.sakethh.linkora.ui.screens.search

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.data.local.search.SearchRepo
import com.sakethh.linkora.data.local.panels.PanelsRepo
import com.sakethh.linkora.data.local.sorting.folders.regular.ParentRegularFoldersSortingRepo
import com.sakethh.linkora.data.local.sorting.folders.subfolders.SubFoldersSortingRepo
import com.sakethh.linkora.data.local.sorting.links.folder.archive.ArchivedFolderLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.folder.regular.RegularFolderLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.history.HistoryLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.important.ImportantLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.saved.SavedLinksSortingRepo
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.CustomWebTab
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenVM
import com.sakethh.linkora.ui.screens.home.HomeScreenVM
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SortingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenVM @Inject constructor(
    private val linksRepo: LinksRepo,
    foldersRepo: FoldersRepo,
    savedLinksSortingRepo: SavedLinksSortingRepo,
    importantLinksSortingRepo: ImportantLinksSortingRepo,
    folderLinksSortingRepo: RegularFolderLinksSortingRepo,
    archiveFolderLinksSortingRepo: ArchivedFolderLinksSortingRepo,
    subFoldersSortingRepo: SubFoldersSortingRepo,
    regularFoldersSortingRepo: ParentRegularFoldersSortingRepo,
    private val searchRepo: SearchRepo,
    private val historyLinksSortingRepo: HistoryLinksSortingRepo,
    parentRegularFoldersSortingRepo: ParentRegularFoldersSortingRepo,
    customWebTab: CustomWebTab,
    panelsRepo: PanelsRepo
) : SpecificCollectionsScreenVM(
    linksRepo,
    foldersRepo,
    savedLinksSortingRepo,
    importantLinksSortingRepo,
    folderLinksSortingRepo,
    archiveFolderLinksSortingRepo,
    subFoldersSortingRepo,
    regularFoldersSortingRepo,
    parentRegularFoldersSortingRepo,
    panelsRepo,
    customWebTab
) {
    enum class SelectedLinkType {
        HISTORY_LINKS, SAVED_LINKS, FOLDER_BASED_LINKS, IMP_LINKS, ARCHIVE_LINKS, ARCHIVE_FOLDER_BASED_LINKS
    }

    companion object {
        var selectedLinkType = SelectedLinkType.SAVED_LINKS
        val isSearchEnabled = mutableStateOf(false)
        val focusRequester = FocusRequester()
        var selectedFolderID: Long = 0
        var selectedLinkID: Long = 0
        val selectedArchiveFoldersData = mutableStateListOf<FoldersTable>()
    }

    private val _queriedUnarchivedFoldersData = MutableStateFlow(emptyList<FoldersTable>())
    val queriedUnarchivedFoldersData = _queriedUnarchivedFoldersData.asStateFlow()

    private val _queriedArchivedFoldersData = MutableStateFlow(emptyList<FoldersTable>())
    val queriedArchivedFoldersData = _queriedArchivedFoldersData.asStateFlow()

    private val _queriedSavedLinks = MutableStateFlow(emptyList<LinksTable>())
    val queriedSavedLinks = _queriedSavedLinks.asStateFlow()

    private val _queriedFolderLinks = MutableStateFlow(emptyList<LinksTable>())
    val queriedFolderLinks = _queriedFolderLinks.asStateFlow()

    private val _impLinksQueriedData = MutableStateFlow(emptyList<ImportantLinks>())
    val impLinksQueriedData = _impLinksQueriedData.asStateFlow()

    private val _archiveLinksQueriedData = MutableStateFlow(emptyList<ArchivedLinks>())
    val archiveLinksQueriedData = _archiveLinksQueriedData.asStateFlow()

    private val _historyLinksQueriedData = MutableStateFlow(emptyList<RecentlyVisited>())
    val historyLinksQueriedData = _historyLinksQueriedData.asStateFlow()

    private val _historyLinksData = MutableStateFlow(emptyList<RecentlyVisited>())
    val historyLinksData = _historyLinksData.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()


    val selectedHistoryLinksData = mutableStateListOf<RecentlyVisited>()
    val selectedImportantLinksData = mutableStateListOf<ImportantLinks>()
    val selectedLinksTableData = mutableStateListOf<LinksTable>()
    val selectedArchiveLinksTableData = mutableStateListOf<ArchivedLinks>()
    val selectedSearchFilters = mutableStateListOf<String>()

    init {
        viewModelScope.launch {
            searchQuery.collectLatest { query ->
                if (query.trim().isNotBlank() && query.trim().isNotEmpty()) {
                    retrieveQueryData(query.trim())
                } else {
                    _queriedArchivedFoldersData.emit(emptyList())
                    _queriedFolderLinks.emit(emptyList())
                    _historyLinksQueriedData.emit(emptyList())
                    _archiveLinksQueriedData.emit(emptyList())
                    _impLinksQueriedData.emit(emptyList())
                    _queriedSavedLinks.emit(emptyList())
                    _queriedUnarchivedFoldersData.emit(emptyList())
                }
            }
        }
    }

    fun deleteSelectedHistoryLinks() {
        viewModelScope.launch {
            selectedHistoryLinksData.toList().forEach {
                linksRepo.deleteARecentlyVisitedLink(it.id)
            }
        }
    }

    override fun reloadLinkData(
        linkID: Long,
        homeScreenType: HomeScreenVM.HomeScreenType
    ) {
        viewModelScope.launch {
            when (selectedLinkType) {
                SelectedLinkType.HISTORY_LINKS -> linksRepo.reloadHistoryLinksTableLink(linkID)
                SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS, SelectedLinkType.FOLDER_BASED_LINKS, SelectedLinkType.SAVED_LINKS -> linksRepo.reloadLinksTableLink(
                    linkID
                )

                SelectedLinkType.IMP_LINKS -> linksRepo.reloadImpLinksTableLink(linkID)
                SelectedLinkType.ARCHIVE_LINKS -> linksRepo.reloadArchiveLink(linkID)
            }
        }
    }

    fun archiveSelectedLinksTableLinks() {
        viewModelScope.launch {
            selectedLinksTableData.toList().forEach {
                linksRepo.addANewLinkToArchiveLink(
                    ArchivedLinks(
                        title = it.title,
                        webURL = it.webURL,
                        baseURL = it.baseURL,
                        imgURL = it.imgURL,
                        infoForSaving = it.infoForSaving
                    )
                )
            }
        }
        viewModelScope.launch {
            selectedLinksTableData.toList().forEach {
                linksRepo.deleteALinkFromLinksTable(it.id)
            }
        }
    }

    fun archiveSelectedImportantLinks() {
        viewModelScope.launch {
            selectedImportantLinksData.toList().forEach {
                linksRepo.addANewLinkToArchiveLink(
                    ArchivedLinks(
                        title = it.title,
                        webURL = it.webURL,
                        baseURL = it.baseURL,
                        imgURL = it.imgURL,
                        infoForSaving = it.infoForSaving
                    )
                )
            }
        }

        viewModelScope.launch {
            selectedImportantLinksData.toList().forEach {
                linksRepo.deleteALinkFromImpLinksBasedOnURL(it.webURL)
            }
        }
    }

    fun archiveSelectedHistoryLinks() {
        viewModelScope.launch {
            selectedHistoryLinksData.toList().forEach {
                linksRepo.addANewLinkToArchiveLink(
                    ArchivedLinks(
                        title = it.title,
                        webURL = it.webURL,
                        baseURL = it.baseURL,
                        imgURL = it.imgURL,
                        infoForSaving = it.imgURL
                    )
                )
                linksRepo.deleteARecentlyVisitedLink(it.id)
            }
        }
    }

    fun deleteSelectedLinksTableData() {
        viewModelScope.launch {
            selectedLinksTableData.toList().forEach {
                linksRepo.deleteALinkFromLinksTable(it.id)
            }
        }
    }

    fun deleteSelectedArchivedLinks() {
        viewModelScope.launch {
            selectedArchiveLinksTableData.toList().forEach {
                linksRepo.deleteALinkFromArchiveLinks(it.id)
            }
        }
    }

    fun deleteSelectedImpLinksData() {
        viewModelScope.launch {
            selectedImportantLinksData.toList().forEach {
                linksRepo.deleteALinkFromImpLinksBasedOnURL(it.webURL)
            }
        }
    }

    fun changeSearchQuery(newQuery: String) {
        viewModelScope.launch {
            _searchQuery.emit(newQuery)
        }
    }

    private fun retrieveQueryData(query: String) {
        viewModelScope.launch {
            searchRepo.getUnArchivedFolders(query).collectLatest {
                _queriedUnarchivedFoldersData.emit(it)
            }
        }
        viewModelScope.launch {
            searchRepo.getSavedLinks(query).collectLatest {
                _queriedSavedLinks.emit(it)
            }
        }
        viewModelScope.launch {
            searchRepo.getFromImportantLinks(query).collectLatest {
                _impLinksQueriedData.emit(it)
            }
        }
        viewModelScope.launch {
            searchRepo.getArchiveLinks(query).collectLatest {
                _archiveLinksQueriedData.emit(it)
            }
        }
        viewModelScope.launch {
            searchRepo.getHistoryLinks(query).collectLatest {
                _historyLinksQueriedData.emit(it)
            }
        }
        viewModelScope.launch {
            searchRepo.getLinksFromFolders(query).collectLatest {
                _queriedFolderLinks.emit(it)
            }
        }
        viewModelScope.launch {
            searchRepo.getArchivedFolders(query).collectLatest {
                _queriedArchivedFoldersData.emit(it)
            }
        }
    }

    init {
        isSearchEnabled.value = false
        changeHistoryRetrievedData(
            sortingPreferences = SortingPreferences.valueOf(
                SettingsPreference.selectedSortingType.value
            )
        )
    }

    fun changeHistoryRetrievedData(sortingPreferences: SortingPreferences) {
        when (sortingPreferences) {
            SortingPreferences.A_TO_Z -> {
                viewModelScope.launch {
                    historyLinksSortingRepo.sortByAToZ().collect {
                        _historyLinksData.emit(it)
                    }
                }
            }

            SortingPreferences.Z_TO_A -> {
                viewModelScope.launch {
                    historyLinksSortingRepo.sortByZToA().collect {
                        _historyLinksData.emit(it)
                    }
                }
            }

            SortingPreferences.NEW_TO_OLD -> {
                viewModelScope.launch {
                    historyLinksSortingRepo.sortByLatestToOldest()
                        .collect {
                            _historyLinksData.emit(it)
                        }
                }
            }

            SortingPreferences.OLD_TO_NEW -> {
                viewModelScope.launch {
                    historyLinksSortingRepo.sortByOldestToLatest()
                        .collect {
                            _historyLinksData.emit(it)
                        }
                }
            }
        }
    }

    fun onNoteDeleteCardClick(
        context: Context,
        selectedWebURL: String,
        selectedLinkType: SelectedLinkType,
        folderID: Long
    ) {
        when (selectedLinkType) {
            SelectedLinkType.HISTORY_LINKS -> {
                viewModelScope.launch {
                    linksRepo.deleteANoteFromRecentlyVisited(webURL = selectedWebURL)
                    pushAUIEvent(CommonUiEvent.ShowToast(LocalizedStrings.deletedTheNoteSuccessfully.value))
                }
            }

            SelectedLinkType.SAVED_LINKS -> {
                viewModelScope.launch {
                    linksRepo.deleteALinkInfoFromSavedLinks(webURL = selectedWebURL)
                    pushAUIEvent(CommonUiEvent.ShowToast(LocalizedStrings.deletedTheNoteSuccessfully.value))
                }
            }

            SelectedLinkType.FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    linksRepo.deleteALinkInfoOfFolders(linkID = selectedLinkID)
                    pushAUIEvent(CommonUiEvent.ShowToast(LocalizedStrings.deletedTheNoteSuccessfully.value))
                }
            }

            SelectedLinkType.IMP_LINKS -> {
                viewModelScope.launch {
                    linksRepo.deleteANoteFromImportantLinks(webURL = selectedWebURL)
                    pushAUIEvent(CommonUiEvent.ShowToast(LocalizedStrings.deletedTheNoteSuccessfully.value))
                }
            }

            SelectedLinkType.ARCHIVE_LINKS -> {
                viewModelScope.launch {
                    linksRepo.deleteANoteFromArchiveLinks(webURL = selectedWebURL)
                    pushAUIEvent(CommonUiEvent.ShowToast(LocalizedStrings.deletedTheNoteSuccessfully.value))
                }
            }

            SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    linksRepo.deleteALinkNoteFromArchiveBasedFolderLinksV10(
                        folderID = folderID,
                        webURL = selectedWebURL
                    )
                    pushAUIEvent(CommonUiEvent.ShowToast(LocalizedStrings.deletedTheNoteSuccessfully.value))
                }
            }
        }
    }

    fun onArchiveClick(
        context: Context,
        selectedLinkType: SelectedLinkType,
        folderID: Long,
    ) {
        viewModelScope.launch {
            awaitAll(async {
                linksRepo.archiveLinkTableUpdater(
                    archivedLinks = ArchivedLinks(
                        title = HomeScreenVM.tempImpLinkData.title,
                        webURL = HomeScreenVM.tempImpLinkData.webURL,
                        baseURL = HomeScreenVM.tempImpLinkData.baseURL,
                        imgURL = HomeScreenVM.tempImpLinkData.imgURL,
                        infoForSaving = HomeScreenVM.tempImpLinkData.infoForSaving
                    ), context = context, onTaskCompleted = {

                    })
            }, async {
                when (selectedLinkType) {
                    SelectedLinkType.HISTORY_LINKS -> {

                        linksRepo.deleteARecentlyVisitedLink(webURL = HomeScreenVM.tempImpLinkData.webURL)
                    }

                    SelectedLinkType.SAVED_LINKS -> {
                        linksRepo
                            .deleteALinkFromSavedLinksBasedOnURL(webURL = HomeScreenVM.tempImpLinkData.webURL)
                    }

                    SelectedLinkType.FOLDER_BASED_LINKS -> {
                        linksRepo
                            .deleteALinkFromLinksTable(selectedLinkID)
                    }

                    SelectedLinkType.IMP_LINKS -> {
                        linksRepo
                            .deleteALinkFromImpLinksBasedOnURL(HomeScreenVM.tempImpLinkData.webURL)
                    }

                    SelectedLinkType.ARCHIVE_LINKS -> {
                        linksRepo
                            .deleteALinkFromArchiveLinksV9(webURL = HomeScreenVM.tempImpLinkData.webURL)
                    }

                    SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS -> {
                        linksRepo
                            .deleteALinkFromArchiveFolderBasedLinksV10(
                                archiveFolderID = folderID,
                                webURL = HomeScreenVM.tempImpLinkData.webURL
                            )
                    }
                }
            })
        }
    }

    fun onNoteChangeClickForLinks(
        webURL: String,
        newNote: String, selectedLinkType: SelectedLinkType,
        folderID: Long, linkID: Long
    ) {
        when (selectedLinkType) {
            SelectedLinkType.HISTORY_LINKS -> {
                viewModelScope.launch {
                    linksRepo
                        .renameALinkInfoFromRecentlyVisitedLinks(
                            webURL = webURL, newInfo = newNote
                        )
                }
            }

            SelectedLinkType.SAVED_LINKS -> {
                viewModelScope.launch {
                    linksRepo.renameALinkInfoFromSavedLinks(
                        webURL = webURL, newInfo = newNote
                    )
                }
            }

            SelectedLinkType.FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    linksRepo.updateLinkInfoFromLinksTable(linkID, newNote)
                }
            }

            SelectedLinkType.IMP_LINKS -> {
                viewModelScope.launch {
                    linksRepo.updateImpLinkNote(linkID, newNote)
                }
            }

            SelectedLinkType.ARCHIVE_LINKS -> {
                viewModelScope.launch {
                    linksRepo.renameALinkInfoFromArchiveLinks(
                        webURL = webURL, newInfo = newNote
                    )
                }
            }

            SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS -> {
                viewModelScope.launch {

                    linksRepo.renameALinkInfoFromArchiveBasedFolderLinksV10(
                        webURL = webURL, newInfo = newNote, folderID = folderID
                    )
                }
            }
        }
    }

    fun onTitleChangeClickForLinks(
        webURL: String,
        newTitle: String, selectedLinkType: SelectedLinkType,
        folderID: Long, linkID: Long
    ) {
        when (selectedLinkType) {
            SelectedLinkType.HISTORY_LINKS -> {
                viewModelScope.launch {

                    linksRepo.renameALinkTitleFromRecentlyVisited(
                        webURL = webURL, newTitle = newTitle
                    )
                }
            }

            SelectedLinkType.SAVED_LINKS -> {
                viewModelScope.launch {
                    linksRepo.renameALinkTitleFromSavedLinks(
                        webURL = webURL, newTitle = newTitle
                    )
                }
            }

            SelectedLinkType.FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    linksRepo.updateLinkTitleFromLinksTable(linkID, newTitle)
                }
            }

            SelectedLinkType.IMP_LINKS -> {
                viewModelScope.launch {
                    linksRepo.updateImpLinkTitle(linkID, newTitle)
                }
            }

            SelectedLinkType.ARCHIVE_LINKS -> {
                viewModelScope.launch {
                    linksRepo.renameALinkTitleFromArchiveLinks(
                        webURL = webURL, newTitle = newTitle
                    )
                }
            }

            SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS -> {
                viewModelScope.launch {

                    linksRepo.renameALinkTitleFromArchiveBasedFolderLinksV10(
                        webURL = webURL, newTitle = newTitle, folderID = folderID
                    )
                }
            }
        }
    }

    fun onDeleteClick(
        context: Context,
        selectedWebURL: String,
        shouldDeleteBoxAppear: MutableState<Boolean>,
        selectedLinkType: SelectedLinkType,
        folderID: Long,
    ) {
        when (selectedLinkType) {
            SelectedLinkType.HISTORY_LINKS -> viewModelScope.launch {
                linksRepo.deleteARecentlyVisitedLink(
                    webURL = selectedWebURL
                )
                shouldDeleteBoxAppear.value = false
                pushAUIEvent(CommonUiEvent.ShowToast(LocalizedStrings.deletedTheLinkSuccessfully.value))
            }

            SelectedLinkType.SAVED_LINKS -> {
                viewModelScope.launch {
                    linksRepo.deleteALinkFromSavedLinksBasedOnURL(
                        webURL = selectedWebURL
                    )
                    shouldDeleteBoxAppear.value = false
                    pushAUIEvent(CommonUiEvent.ShowToast(LocalizedStrings.deletedTheLinkSuccessfully.value))
                }
            }

            SelectedLinkType.FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    linksRepo.deleteALinkFromLinksTable(selectedLinkID)
                    shouldDeleteBoxAppear.value = false
                    pushAUIEvent(CommonUiEvent.ShowToast(LocalizedStrings.deletedTheLinkSuccessfully.value))
                }
            }

            SelectedLinkType.IMP_LINKS -> {
                viewModelScope.launch {
                    linksRepo.deleteALinkFromImpLinksBasedOnURL(
                        webURL = selectedWebURL
                    )
                    shouldDeleteBoxAppear.value = false
                    pushAUIEvent(CommonUiEvent.ShowToast(LocalizedStrings.deletedTheLinkSuccessfully.value))
                }
            }

            SelectedLinkType.ARCHIVE_LINKS -> {
                viewModelScope.launch {
                    linksRepo.deleteALinkFromArchiveLinksV9(
                        webURL = selectedWebURL
                    )
                    shouldDeleteBoxAppear.value = false
                    pushAUIEvent(CommonUiEvent.ShowToast(LocalizedStrings.deletedTheLinkSuccessfully.value))
                }
            }

            SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    linksRepo
                        .deleteALinkFromArchiveFolderBasedLinksV10(
                            webURL = selectedWebURL, archiveFolderID = folderID
                        )
                    shouldDeleteBoxAppear.value = false
                    pushAUIEvent(CommonUiEvent.ShowToast(LocalizedStrings.deletedTheLinkSuccessfully.value))
                }
            }
        }
    }
}