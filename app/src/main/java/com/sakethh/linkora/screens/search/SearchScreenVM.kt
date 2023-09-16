package com.sakethh.linkora.screens.search

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.localDB.ArchivedLinks
import com.sakethh.linkora.localDB.CustomFunctionsForLocalDB
import com.sakethh.linkora.localDB.ImportantLinks
import com.sakethh.linkora.localDB.LinksTable
import com.sakethh.linkora.localDB.RecentlyVisited
import com.sakethh.linkora.screens.home.HomeScreenVM
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchScreenVM : ViewModel() {

    enum class SelectedLinkType {
        HISTORY_LINKS, SAVED_LINKS, FOLDER_BASED_LINKS, IMP_LINKS, ARCHIVE_LINKS, ARCHIVE_FOLDER_BASED_LINKS
    }

    companion object {
        var selectedLinkType = SelectedLinkType.SAVED_LINKS
        val isSearchEnabled = mutableStateOf(false)
        val focusRequester = FocusRequester()
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
                    CustomFunctionsForLocalDB.localDB.linksSearching()
                        .getFromImportantLinks(query = query).collect {
                            _impLinksQueriedData.emit(it)
                        }
                }
            }, async {
                if (query.isNotEmpty()) {
                    CustomFunctionsForLocalDB.localDB.linksSearching()
                        .getFromLinksTableIncludingArchive(query = query).collect {
                            _linksTableQueriedData.emit(it)
                        }
                }
            }, async {
                if (query.isNotEmpty()) {
                    CustomFunctionsForLocalDB.localDB.linksSearching()
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
                    CustomFunctionsForLocalDB.localDB.historyLinksSorting().sortByAToZ().collect {
                        _historyLinksData.emit(it)
                    }
                }
            }

            SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.historyLinksSorting().sortByZToA().collect {
                        _historyLinksData.emit(it)
                    }
                }
            }

            SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.historyLinksSorting().sortByLatestToOldest()
                        .collect {
                            _historyLinksData.emit(it)
                        }
                }
            }

            SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.historyLinksSorting().sortByOldestToLatest()
                        .collect {
                            _historyLinksData.emit(it)
                        }
                }
            }
        }
    }

    fun onForceOpenInExternalBrowser(recentlyVisited: RecentlyVisited) {
        viewModelScope.launch {
            if (!CustomFunctionsForLocalDB.localDB.crudDao()
                    .doesThisExistsInRecentlyVisitedLinks(webURL = recentlyVisited.webURL)
            ) {
                CustomFunctionsForLocalDB.localDB.crudDao().addANewLinkInRecentlyVisited(
                    recentlyVisited = RecentlyVisited(
                        title = recentlyVisited.title,
                        webURL = recentlyVisited.webURL,
                        baseURL = recentlyVisited.baseURL,
                        imgURL = recentlyVisited.imgURL,
                        infoForSaving = recentlyVisited.infoForSaving
                    )
                )
            }
        }
    }

    fun onNoteDeleteCardClick(
        context: Context,
        selectedWebURL: String,
        selectedLinkType: SelectedLinkType,
        folderName: String,
    ) {
        when (selectedLinkType) {
            SelectedLinkType.HISTORY_LINKS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .deleteANoteFromRecentlyVisited(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            SelectedLinkType.SAVED_LINKS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .deleteALinkInfoFromSavedLinks(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            SelectedLinkType.FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .deleteALinkInfoOfFolders(webURL = selectedWebURL, folderName = folderName)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            SelectedLinkType.IMP_LINKS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .deleteANoteFromImportantLinks(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            SelectedLinkType.ARCHIVE_LINKS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .deleteANoteFromArchiveLinks(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .deleteALinkNoteFromArchiveBasedFolderLinks(
                            folderName = folderName,
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
        folderName: String,
    ) {
        viewModelScope.launch {
            awaitAll(async {
                CustomFunctionsForLocalDB().archiveLinkTableUpdater(archivedLinks = ArchivedLinks(
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
                        CustomFunctionsForLocalDB.localDB.crudDao()
                            .deleteARecentlyVisitedLink(webURL = HomeScreenVM.tempImpLinkData.webURL)
                    }

                    SelectedLinkType.SAVED_LINKS -> {
                        CustomFunctionsForLocalDB.localDB.crudDao()
                            .deleteALinkFromSavedLinks(webURL = HomeScreenVM.tempImpLinkData.webURL)
                    }

                    SelectedLinkType.FOLDER_BASED_LINKS -> {
                        CustomFunctionsForLocalDB.localDB.crudDao()
                            .deleteALinkFromSpecificFolder(
                                folderName = folderName,
                                webURL = HomeScreenVM.tempImpLinkData.webURL
                            )
                    }

                    SelectedLinkType.IMP_LINKS -> {
                        CustomFunctionsForLocalDB.localDB.crudDao()
                            .deleteALinkFromImpLinks(webURL = HomeScreenVM.tempImpLinkData.webURL)
                    }

                    SelectedLinkType.ARCHIVE_LINKS -> {
                        CustomFunctionsForLocalDB.localDB.crudDao()
                            .deleteALinkFromArchiveLinks(webURL = HomeScreenVM.tempImpLinkData.webURL)
                    }

                    SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS -> {
                        CustomFunctionsForLocalDB.localDB.crudDao()
                            .deleteALinkFromArchiveFolderBasedLinks(
                                archiveFolderName = folderName,
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
        folderName: String,
    ) {
        when (selectedLinkType) {
            SelectedLinkType.HISTORY_LINKS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .renameALinkInfoFromRecentlyVisitedLinks(
                            webURL = webURL, newInfo = newNote
                        )
                }
            }

            SelectedLinkType.SAVED_LINKS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao().renameALinkInfoFromSavedLinks(
                        webURL = webURL, newInfo = newNote
                    )
                }
            }

            SelectedLinkType.FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao().renameALinkInfoFromFolders(
                        webURL = webURL, newInfo = newNote, folderName = folderName
                    )
                }
            }

            SelectedLinkType.IMP_LINKS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao().renameALinkInfoFromImpLinks(
                        webURL = webURL, newInfo = newNote
                    )
                }
            }

            SelectedLinkType.ARCHIVE_LINKS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao().renameALinkInfoFromArchiveLinks(
                        webURL = webURL, newInfo = newNote
                    )
                }
            }

            SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .renameALinkInfoFromArchiveBasedFolderLinks(
                            webURL = webURL, newInfo = newNote, folderName = folderName
                        )
                }
            }
        }
    }

    fun onTitleChangeClickForLinks(
        webURL: String,
        newTitle: String, selectedLinkType: SelectedLinkType,
        folderName: String,
    ) {
        when (selectedLinkType) {
            SelectedLinkType.HISTORY_LINKS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao().renameALinkTitleFromRecentlyVisited(
                        webURL = webURL, newTitle = newTitle
                    )
                }
            }

            SelectedLinkType.SAVED_LINKS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao().renameALinkTitleFromSavedLinks(
                        webURL = webURL, newTitle = newTitle
                    )
                }
            }

            SelectedLinkType.FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao().renameALinkTitleFromFolders(
                        webURL = webURL, newTitle = newTitle, folderName = folderName
                    )
                }
            }

            SelectedLinkType.IMP_LINKS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao().renameALinkTitleFromImpLinks(
                        webURL = webURL, newTitle = newTitle
                    )
                }
            }

            SelectedLinkType.ARCHIVE_LINKS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao().renameALinkTitleFromArchiveLinks(
                        webURL = webURL, newTitle = newTitle
                    )
                }
            }

            SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .renameALinkTitleFromArchiveBasedFolderLinks(
                            webURL = webURL, newTitle = newTitle, folderName = folderName
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
        folderName: String,
    ) {
        when (selectedLinkType) {
            SelectedLinkType.HISTORY_LINKS -> viewModelScope.launch {
                CustomFunctionsForLocalDB.localDB.crudDao().deleteARecentlyVisitedLink(
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
                    CustomFunctionsForLocalDB.localDB.crudDao().deleteALinkFromSavedLinks(
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
                    CustomFunctionsForLocalDB.localDB.crudDao().deleteALinkFromSpecificFolder(
                        webURL = selectedWebURL, folderName = folderName
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
                    CustomFunctionsForLocalDB.localDB.crudDao().deleteALinkFromImpLinks(
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
                    CustomFunctionsForLocalDB.localDB.crudDao().deleteALinkFromArchiveLinks(
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
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .deleteALinkFromArchiveFolderBasedLinks(
                            webURL = selectedWebURL, folderName
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