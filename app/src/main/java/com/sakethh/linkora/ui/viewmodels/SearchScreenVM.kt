package com.sakethh.linkora.ui.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenVM
import com.sakethh.linkora.ui.screens.home.HomeScreenVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchScreenVM : SpecificCollectionsScreenVM() {
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
                retrieveQueryData(query)
            }
        }
    }

    fun deleteSelectedHistoryLinks() {
        viewModelScope.launch {
            selectedHistoryLinksData.toList().forEach {
                LocalDatabase.localDB.deleteDao().deleteARecentlyVisitedLink(it.id)
            }
        }
    }

    fun archiveSelectedLinksTableLinks() {
        viewModelScope.launch {
            selectedLinksTableData.toList().forEach {
                LocalDatabase.localDB.createDao().addANewLinkToArchiveLink(
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
                LocalDatabase.localDB.deleteDao().deleteALinkFromLinksTable(it.id)
            }
        }
    }

    fun archiveSelectedImportantLinks() {
        viewModelScope.launch {
            selectedImportantLinksData.toList().forEach {
                LocalDatabase.localDB.createDao().addANewLinkToArchiveLink(
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
                LocalDatabase.localDB.deleteDao().deleteALinkFromImpLinksBasedOnURL(it.webURL)
            }
        }
    }

    fun archiveSelectedHistoryLinks() {
        viewModelScope.launch {
            selectedHistoryLinksData.toList().forEach {
                LocalDatabase.localDB.createDao().addANewLinkToArchiveLink(
                    ArchivedLinks(
                        title = it.title,
                        webURL = it.webURL,
                        baseURL = it.baseURL,
                        imgURL = it.imgURL,
                        infoForSaving = it.imgURL
                    )
                )
                LocalDatabase.localDB.deleteDao().deleteARecentlyVisitedLink(it.id)
            }
        }
    }

    fun deleteSelectedLinksTableData() {
        viewModelScope.launch {
            selectedLinksTableData.toList().forEach {
                LocalDatabase.localDB.deleteDao().deleteALinkFromLinksTable(it.id)
            }
        }
    }

    fun deleteSelectedArchivedLinks() {
        viewModelScope.launch {
            selectedArchiveLinksTableData.toList().forEach {
                LocalDatabase.localDB.deleteDao().deleteALinkFromArchiveLinks(it.id)
            }
        }
    }

    fun deleteSelectedImpLinksData() {
        viewModelScope.launch {
            selectedImportantLinksData.toList().forEach {
                LocalDatabase.localDB.deleteDao().deleteALinkFromImpLinksBasedOnURL(it.webURL)
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
            LocalDatabase.localDB.searchDao().getUnArchivedFolders(query).collectLatest {
                _queriedUnarchivedFoldersData.emit(it)
            }
        }
        viewModelScope.launch {
            LocalDatabase.localDB.searchDao().getSavedLinks(query).collectLatest {
                _queriedSavedLinks.emit(it)
            }
        }
        viewModelScope.launch {
            LocalDatabase.localDB.searchDao().getFromImportantLinks(query).collectLatest {
                _impLinksQueriedData.emit(it)
            }
        }
        viewModelScope.launch {
            LocalDatabase.localDB.searchDao().getArchiveLinks(query).collectLatest {
                _archiveLinksQueriedData.emit(it)
            }
        }
        viewModelScope.launch {
            LocalDatabase.localDB.searchDao().getHistoryLinks(query).collectLatest {
                _historyLinksQueriedData.emit(it)
            }
        }
        viewModelScope.launch {
            LocalDatabase.localDB.searchDao().getLinksFromFolders(query).collectLatest {
                _queriedFolderLinks.emit(it)
            }
        }
        viewModelScope.launch {
            LocalDatabase.localDB.searchDao().getArchivedFolders(query).collectLatest {
                _queriedArchivedFoldersData.emit(it)
            }
        }
    }

    init {
        isSearchEnabled.value = false
        changeHistoryRetrievedData(
            sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                SettingsScreenVM.Settings.selectedSortingType.value
            )
        )
    }

    fun changeHistoryRetrievedData(sortingPreferences: SettingsScreenVM.SortingPreferences) {
        when (sortingPreferences) {
            SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.historyLinksSorting().sortByAToZ().collect {
                        _historyLinksData.emit(it)
                    }
                }
            }

            SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.historyLinksSorting().sortByZToA().collect {
                        _historyLinksData.emit(it)
                    }
                }
            }

            SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.historyLinksSorting().sortByLatestToOldest()
                        .collect {
                            _historyLinksData.emit(it)
                        }
                }
            }

            SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.historyLinksSorting().sortByOldestToLatest()
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
                    LocalDatabase.localDB.deleteDao()
                        .deleteANoteFromRecentlyVisited(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            SelectedLinkType.SAVED_LINKS -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.deleteDao()
                        .deleteALinkInfoFromSavedLinks(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            SelectedLinkType.FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.deleteDao()
                        .deleteALinkInfoOfFolders(linkID = selectedLinkID)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            SelectedLinkType.IMP_LINKS -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.deleteDao()
                        .deleteANoteFromImportantLinks(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            SelectedLinkType.ARCHIVE_LINKS -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.deleteDao()
                        .deleteANoteFromArchiveLinks(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.deleteDao()
                        .deleteALinkNoteFromArchiveBasedFolderLinksV10(
                            folderID = folderID,
                            webURL = selectedWebURL
                        )
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
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
                updateVM.archiveLinkTableUpdater(archivedLinks = ArchivedLinks(
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
                        LocalDatabase.localDB.deleteDao()
                            .deleteARecentlyVisitedLink(webURL = HomeScreenVM.tempImpLinkData.webURL)
                    }

                    SelectedLinkType.SAVED_LINKS -> {
                        LocalDatabase.localDB.deleteDao()
                            .deleteALinkFromSavedLinksBasedOnURL(webURL = HomeScreenVM.tempImpLinkData.webURL)
                    }

                    SelectedLinkType.FOLDER_BASED_LINKS -> {
                        LocalDatabase.localDB.deleteDao()
                            .deleteALinkFromLinksTable(selectedLinkID)
                    }

                    SelectedLinkType.IMP_LINKS -> {
                        LocalDatabase.localDB.deleteDao()
                            .deleteALinkFromImpLinksBasedOnURL(HomeScreenVM.tempImpLinkData.webURL)
                    }

                    SelectedLinkType.ARCHIVE_LINKS -> {
                        LocalDatabase.localDB.deleteDao()
                            .deleteALinkFromArchiveLinksV9(webURL = HomeScreenVM.tempImpLinkData.webURL)
                    }

                    SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS -> {
                        LocalDatabase.localDB.deleteDao()
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
                    LocalDatabase.localDB.updateDao()
                        .renameALinkInfoFromRecentlyVisitedLinks(
                            webURL = webURL, newInfo = newNote
                        )
                }
            }

            SelectedLinkType.SAVED_LINKS -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.updateDao().renameALinkInfoFromSavedLinks(
                        webURL = webURL, newInfo = newNote
                    )
                }
            }

            SelectedLinkType.FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    updateVM.updateRegularLinkNote(linkID, newNote)
                }
            }

            SelectedLinkType.IMP_LINKS -> {
                viewModelScope.launch {
                    updateVM.updateImpLinkNote(linkID, newNote)
                }
            }

            SelectedLinkType.ARCHIVE_LINKS -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.updateDao().renameALinkInfoFromArchiveLinks(
                        webURL = webURL, newInfo = newNote
                    )
                }
            }

            SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.updateDao()
                        .renameALinkInfoFromArchiveBasedFolderLinksV10(
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
                    LocalDatabase.localDB.updateDao()
                        .renameALinkTitleFromRecentlyVisited(
                            webURL = webURL, newTitle = newTitle
                        )
                }
            }

            SelectedLinkType.SAVED_LINKS -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.updateDao().renameALinkTitleFromSavedLinks(
                        webURL = webURL, newTitle = newTitle
                    )
                }
            }

            SelectedLinkType.FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    updateVM.updateRegularLinkTitle(linkID, newTitle)
                }
            }

            SelectedLinkType.IMP_LINKS -> {
                viewModelScope.launch {
                    updateVM.updateImpLinkTitle(linkID, newTitle)
                }
            }

            SelectedLinkType.ARCHIVE_LINKS -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.updateDao().renameALinkTitleFromArchiveLinks(
                        webURL = webURL, newTitle = newTitle
                    )
                }
            }

            SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.updateDao()
                        .renameALinkTitleFromArchiveBasedFolderLinksV10(
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
                LocalDatabase.localDB.deleteDao().deleteARecentlyVisitedLink(
                    webURL = selectedWebURL
                )
                shouldDeleteBoxAppear.value = false
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context, "deleted the link successfully", Toast.LENGTH_SHORT
                    ).show()
                }
            }

            SelectedLinkType.SAVED_LINKS -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.deleteDao().deleteALinkFromSavedLinksBasedOnURL(
                        webURL = selectedWebURL
                    )
                    shouldDeleteBoxAppear.value = false
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "deleted the link successfully", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            SelectedLinkType.FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.deleteDao().deleteALinkFromLinksTable(selectedLinkID)
                    shouldDeleteBoxAppear.value = false
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "deleted the link successfully", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            SelectedLinkType.IMP_LINKS -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.deleteDao().deleteALinkFromImpLinksBasedOnURL(
                        webURL = selectedWebURL
                    )
                    shouldDeleteBoxAppear.value = false
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "deleted the link successfully", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            SelectedLinkType.ARCHIVE_LINKS -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.deleteDao().deleteALinkFromArchiveLinksV9(
                        webURL = selectedWebURL
                    )
                    shouldDeleteBoxAppear.value = false
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "deleted the link successfully", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.deleteDao()
                        .deleteALinkFromArchiveFolderBasedLinksV10(
                            webURL = selectedWebURL, archiveFolderID = folderID
                        )
                    shouldDeleteBoxAppear.value = false
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "deleted the link successfully", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}