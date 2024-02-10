package com.sakethh.linkora.screens.search

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.localDB.LocalDataBase
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.localDB.dto.RecentlyVisited
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificCollectionsScreenVM
import com.sakethh.linkora.screens.home.HomeScreenVM
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


data class HistoryLinkComponent(
    val historyLinksData: List<RecentlyVisited>, val isLinkSelected: List<MutableState<Boolean>>
)

class SearchScreenVM() : SpecificCollectionsScreenVM() {
    enum class SelectedLinkType {
        HISTORY_LINKS, SAVED_LINKS, FOLDER_BASED_LINKS, IMP_LINKS, ARCHIVE_LINKS, ARCHIVE_FOLDER_BASED_LINKS
    }

    companion object {
        var selectedLinkType = SelectedLinkType.SAVED_LINKS
        val isSearchEnabled = mutableStateOf(false)
        val focusRequester = FocusRequester()
        var selectedFolderID: Long = 0
        var selectedLinkID: Long = 0
    }

    private val _historyLinksData = MutableStateFlow(HistoryLinkComponent(emptyList(), emptyList()))
    val historyLinksData = _historyLinksData.asStateFlow()

    private val _linksTableQueriedData = MutableStateFlow(emptyList<LinksTable>())
    val linksTableData = _linksTableQueriedData.asStateFlow()

    private val _impLinksQueriedData = MutableStateFlow(emptyList<ImportantLinks>())
    val impLinksQueriedData = _impLinksQueriedData.asStateFlow()

    private val _archiveLinksQueriedData = MutableStateFlow(emptyList<ArchivedLinks>())
    val archiveLinksQueriedData = _archiveLinksQueriedData.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    val selectedLinksData = mutableStateListOf<ArchivedLinks>()
    init {
        viewModelScope.launch {
            searchQuery.collectLatest { query ->
                retrieveQueryData(query)
            }
        }
    }

    fun removeAllLinksSelection() {
        selectedLinksData.removeAll(historyLinksData.value.historyLinksData.map {
            ArchivedLinks(
                title = it.title,
                webURL = it.webURL,
                baseURL = it.baseURL,
                imgURL = it.imgURL,
                infoForSaving = it.infoForSaving
            )
        })
        historyLinksData.value.isLinkSelected.forEach { it.value = false }
    }

    fun deleteSelectedHistoryLinks() {
        viewModelScope.launch {
            selectedLinksData.forEach {
                LocalDataBase.localDB.deleteDao().deleteARecentlyVisitedLink(it.id)
            }
        }
    }

    fun archiveSelectedHistoryLinks() {
        viewModelScope.launch {
            selectedLinksData.forEach {
                LocalDataBase.localDB.createDao().addANewLinkToArchiveLink(it)
                LocalDataBase.localDB.deleteDao().deleteARecentlyVisitedLink(it.id)
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
            awaitAll(async {
                if (query.isNotEmpty()) {
                    _impLinksQueriedData.emit(
                        LocalDataBase.localDB.linksSearching()
                            .getFromImportantLinks(query = query)
                    )
                }
            }, async {
                if (query.isNotEmpty()) {
                    _linksTableQueriedData.emit(
                        LocalDataBase.localDB.linksSearching()
                            .getFromLinksTable(query = query)
                    )
                }
            }, async {
                if (query.isNotEmpty()) {
                    _archiveLinksQueriedData.emit(
                        LocalDataBase.localDB.linksSearching()
                            .getFromArchiveLinks(query = query)
                    )
                }
            })
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
                    LocalDataBase.localDB.historyLinksSorting().sortByAToZ().collect {
                        val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                        List(it.size) { index ->
                            mutableBooleanList.add(index, mutableStateOf(false))
                        }
                        _historyLinksData.emit(HistoryLinkComponent(it, mutableBooleanList))
                    }
                }
            }

            SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.historyLinksSorting().sortByZToA().collect {
                        val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                        List(it.size) { index ->
                            mutableBooleanList.add(index, mutableStateOf(false))
                        }
                        _historyLinksData.emit(HistoryLinkComponent(it, mutableBooleanList))
                    }
                }
            }

            SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.historyLinksSorting().sortByLatestToOldest()
                        .collect {
                            val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                            List(it.size) { index ->
                                mutableBooleanList.add(index, mutableStateOf(false))
                            }
                            _historyLinksData.emit(HistoryLinkComponent(it, mutableBooleanList))
                        }
                }
            }

            SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.historyLinksSorting().sortByOldestToLatest()
                        .collect {
                            val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                            List(it.size) { index ->
                                mutableBooleanList.add(index, mutableStateOf(false))
                            }
                            _historyLinksData.emit(HistoryLinkComponent(it, mutableBooleanList))
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
                    LocalDataBase.localDB.deleteDao()
                        .deleteANoteFromRecentlyVisited(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            SelectedLinkType.SAVED_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.deleteDao()
                        .deleteALinkInfoFromSavedLinks(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            SelectedLinkType.FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.deleteDao()
                        .deleteALinkInfoOfFolders(linkID = selectedLinkID)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            SelectedLinkType.IMP_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.deleteDao()
                        .deleteANoteFromImportantLinks(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            SelectedLinkType.ARCHIVE_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.deleteDao()
                        .deleteANoteFromArchiveLinks(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.deleteDao()
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
                        LocalDataBase.localDB.deleteDao()
                            .deleteARecentlyVisitedLink(webURL = HomeScreenVM.tempImpLinkData.webURL)
                    }

                    SelectedLinkType.SAVED_LINKS -> {
                        LocalDataBase.localDB.deleteDao()
                            .deleteALinkFromSavedLinksBasedOnURL(webURL = HomeScreenVM.tempImpLinkData.webURL)
                    }

                    SelectedLinkType.FOLDER_BASED_LINKS -> {
                        LocalDataBase.localDB.deleteDao()
                            .deleteALinkFromSpecificFolderV10(
                                folderID = folderID,
                                webURL = HomeScreenVM.tempImpLinkData.webURL
                            )
                    }

                    SelectedLinkType.IMP_LINKS -> {
                        LocalDataBase.localDB.deleteDao()
                            .deleteALinkFromImpLinks(HomeScreenVM.tempImpLinkData.id)
                    }

                    SelectedLinkType.ARCHIVE_LINKS -> {
                        LocalDataBase.localDB.deleteDao()
                            .deleteALinkFromArchiveLinksV9(webURL = HomeScreenVM.tempImpLinkData.webURL)
                    }

                    SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS -> {
                        LocalDataBase.localDB.deleteDao()
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
                    LocalDataBase.localDB.updateDao()
                        .renameALinkInfoFromRecentlyVisitedLinks(
                            webURL = webURL, newInfo = newNote
                        )
                }
            }

            SelectedLinkType.SAVED_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.updateDao().renameALinkInfoFromSavedLinks(
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
                    LocalDataBase.localDB.updateDao().renameALinkInfoFromArchiveLinks(
                        webURL = webURL, newInfo = newNote
                    )
                }
            }

            SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.updateDao()
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
                    LocalDataBase.localDB.updateDao()
                        .renameALinkTitleFromRecentlyVisited(
                            webURL = webURL, newTitle = newTitle
                        )
                }.invokeOnCompletion {
                    retrieveQueryData(searchQuery.value)
                }
            }

            SelectedLinkType.SAVED_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.updateDao().renameALinkTitleFromSavedLinks(
                        webURL = webURL, newTitle = newTitle
                    )
                }.invokeOnCompletion {
                    retrieveQueryData(searchQuery.value)
                }
            }

            SelectedLinkType.FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    updateVM.updateRegularLinkTitle(linkID, newTitle)
                }.invokeOnCompletion {
                    retrieveQueryData(searchQuery.value)
                }
            }

            SelectedLinkType.IMP_LINKS -> {
                viewModelScope.launch {
                    updateVM.updateImpLinkTitle(linkID, newTitle)
                }.invokeOnCompletion {
                    retrieveQueryData(searchQuery.value)
                }
            }

            SelectedLinkType.ARCHIVE_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.updateDao().renameALinkTitleFromArchiveLinks(
                        webURL = webURL, newTitle = newTitle
                    )
                }.invokeOnCompletion {
                    retrieveQueryData(searchQuery.value)
                }
            }

            SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.updateDao()
                        .renameALinkTitleFromArchiveBasedFolderLinksV10(
                            webURL = webURL, newTitle = newTitle, folderID = folderID
                        )
                }.invokeOnCompletion {
                    retrieveQueryData(searchQuery.value)
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
                LocalDataBase.localDB.deleteDao().deleteARecentlyVisitedLink(
                    webURL = selectedWebURL
                )
                shouldDeleteBoxAppear.value = false
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context, "deleted the link successfully", Toast.LENGTH_SHORT
                    ).show()
                }
            }.invokeOnCompletion {
                retrieveQueryData(searchQuery.value)
            }

            SelectedLinkType.SAVED_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.deleteDao().deleteALinkFromSavedLinksBasedOnURL(
                        webURL = selectedWebURL
                    )
                    shouldDeleteBoxAppear.value = false
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "deleted the link successfully", Toast.LENGTH_SHORT
                        ).show()
                    }
                }.invokeOnCompletion {
                    retrieveQueryData(searchQuery.value)
                }
            }

            SelectedLinkType.FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.deleteDao().deleteALinkFromSpecificFolderV10(
                        webURL = selectedWebURL, folderID = folderID
                    )
                    shouldDeleteBoxAppear.value = false
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "deleted the link successfully", Toast.LENGTH_SHORT
                        ).show()
                    }
                }.invokeOnCompletion {
                    retrieveQueryData(searchQuery.value)
                }
            }

            SelectedLinkType.IMP_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.deleteDao().deleteALinkFromImpLinksBasedOnURL(
                        webURL = selectedWebURL
                    )
                    shouldDeleteBoxAppear.value = false
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "deleted the link successfully", Toast.LENGTH_SHORT
                        ).show()
                    }
                }.invokeOnCompletion {
                    retrieveQueryData(searchQuery.value)
                }
            }

            SelectedLinkType.ARCHIVE_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.deleteDao().deleteALinkFromArchiveLinksV9(
                        webURL = selectedWebURL
                    )
                    shouldDeleteBoxAppear.value = false
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "deleted the link successfully", Toast.LENGTH_SHORT
                        ).show()
                    }
                }.invokeOnCompletion {
                    retrieveQueryData(searchQuery.value)
                }
            }

            SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.deleteDao()
                        .deleteALinkFromArchiveFolderBasedLinksV10(
                            webURL = selectedWebURL, archiveFolderID = folderID
                        )
                    shouldDeleteBoxAppear.value = false
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "deleted the link successfully", Toast.LENGTH_SHORT
                        ).show()
                    }
                }.invokeOnCompletion {
                    retrieveQueryData(searchQuery.value)
                }
            }
        }
    }
}