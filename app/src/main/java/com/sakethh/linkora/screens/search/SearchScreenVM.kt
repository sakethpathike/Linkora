package com.sakethh.linkora.screens.search

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
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
    }

    private val _historyLinksData = MutableStateFlow(emptyList<RecentlyVisited>())
    val historyLinksData = _historyLinksData.asStateFlow()

    private val _linksTableQueriedData = MutableStateFlow(emptyList<LinksTable>())
    val linksTableData = _linksTableQueriedData.asStateFlow()

    private val _impLinksQueriedData = MutableStateFlow(emptyList<ImportantLinks>())
    val impLinksQueriedData = _impLinksQueriedData.asStateFlow()

    private val _archiveLinksQueriedData = MutableStateFlow(emptyList<ArchivedLinks>())
    val archiveLinksQueriedData = _archiveLinksQueriedData.asStateFlow()

    fun retrieveSearchQueryData(query: String) {
        viewModelScope.launch {
            awaitAll(async {
                if (query.isNotEmpty()) {
                    LocalDataBase.localDB.linksSearching()
                        .getFromImportantLinks(query = query).collect {
                            _impLinksQueriedData.emit(it)
                        }
                }
            }, async {
                if (query.isNotEmpty()) {
                    LocalDataBase.localDB.linksSearching()
                        .getFromLinksTableIncludingArchive(query = query).collect {
                            _linksTableQueriedData.emit(it)
                        }
                }
            }, async {
                if (query.isNotEmpty()) {
                    LocalDataBase.localDB.linksSearching()
                        .getFromArchiveLinks(query = query).collect {
                            _archiveLinksQueriedData.emit(it)
                        }
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
                        _historyLinksData.emit(it)
                    }
                }
            }

            SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.historyLinksSorting().sortByZToA().collect {
                        _historyLinksData.emit(it)
                    }
                }
            }

            SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.historyLinksSorting().sortByLatestToOldest()
                        .collect {
                            _historyLinksData.emit(it)
                        }
                }
            }

            SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.historyLinksSorting().sortByOldestToLatest()
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
                        .deleteALinkInfoOfFoldersV10(webURL = selectedWebURL, folderID = folderID)
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
                            .deleteALinkFromSavedLinks(webURL = HomeScreenVM.tempImpLinkData.webURL)
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
                            .deleteALinkFromImpLinks(webURL = HomeScreenVM.tempImpLinkData.webURL)
                    }

                    SelectedLinkType.ARCHIVE_LINKS -> {
                        LocalDataBase.localDB.deleteDao()
                            .deleteALinkFromArchiveLinks(webURL = HomeScreenVM.tempImpLinkData.webURL)
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
        folderID: Long
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
                    LocalDataBase.localDB.updateDao().renameALinkInfoFromFoldersV10(
                        webURL = webURL, newInfo = newNote, folderID = folderID
                    )
                }
            }

            SelectedLinkType.IMP_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.updateDao().renameALinkInfoFromImpLinks(
                        webURL = webURL, newInfo = newNote
                    )
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
        folderID: Long,
    ) {
        when (selectedLinkType) {
            SelectedLinkType.HISTORY_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.updateDao()
                        .renameALinkTitleFromRecentlyVisited(
                            webURL = webURL, newTitle = newTitle
                        )
                }
            }

            SelectedLinkType.SAVED_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.updateDao().renameALinkTitleFromSavedLinks(
                        webURL = webURL, newTitle = newTitle
                    )
                }
            }

            SelectedLinkType.FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.updateDao().renameALinkTitleFromFoldersV10(
                        webURL = webURL, newTitle = newTitle, folderID = folderID
                    )
                }
            }

            SelectedLinkType.IMP_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.updateDao().renameALinkTitleFromImpLinks(
                        webURL = webURL, newTitle = newTitle
                    )
                }
            }

            SelectedLinkType.ARCHIVE_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.updateDao().renameALinkTitleFromArchiveLinks(
                        webURL = webURL, newTitle = newTitle
                    )
                }
            }

            SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.updateDao()
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
                LocalDataBase.localDB.deleteDao().deleteARecentlyVisitedLink(
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
                    LocalDataBase.localDB.deleteDao().deleteALinkFromSavedLinks(
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
                    LocalDataBase.localDB.deleteDao().deleteALinkFromSpecificFolderV10(
                        webURL = selectedWebURL, folderID = folderID
                    )
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
                    LocalDataBase.localDB.deleteDao().deleteALinkFromImpLinks(
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
                    LocalDataBase.localDB.deleteDao().deleteALinkFromArchiveLinks(
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
                }
            }
        }
    }
}