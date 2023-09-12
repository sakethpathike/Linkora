package com.sakethh.linkora.screens.search

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    companion object {
        val isSearchEnabled = mutableStateOf(false)
    }

    private val _historyLinksData = MutableStateFlow(emptyList<RecentlyVisited>())
    val historyLinksData = _historyLinksData.asStateFlow()

    private val _linksTableQueriedData = MutableStateFlow(emptyList<LinksTable>())
    val linksTableData = _linksTableQueriedData.asStateFlow()

    private val _impLinksQueriedData = MutableStateFlow(emptyList<ImportantLinks>())
    val impLinksQueriedData = _impLinksQueriedData.asStateFlow()

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
    ) {
        viewModelScope.launch {
            CustomFunctionsForLocalDB.localDB.crudDao()
                .deleteANoteFromRecentlyVisited(webURL = selectedWebURL)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onArchiveClick(context: Context) {
        viewModelScope.launch {
            awaitAll(async {
                CustomFunctionsForLocalDB().archiveLinkTableUpdater(archivedLinks = com.sakethh.linkora.localDB.ArchivedLinks(
                    title = HomeScreenVM.tempImpLinkData.title,
                    webURL = HomeScreenVM.tempImpLinkData.webURL,
                    baseURL = HomeScreenVM.tempImpLinkData.baseURL,
                    imgURL = HomeScreenVM.tempImpLinkData.imgURL,
                    infoForSaving = HomeScreenVM.tempImpLinkData.infoForSaving
                ), context = context, onTaskCompleted = {

                })
            }, async {
                CustomFunctionsForLocalDB.localDB.crudDao()
                    .deleteARecentlyVisitedLink(webURL = HomeScreenVM.tempImpLinkData.webURL)
            })
        }
    }

    fun onNoteChangeClickForLinks(
        webURL: String,
        newNote: String,
    ) {
        viewModelScope.launch {
            CustomFunctionsForLocalDB.localDB.crudDao().renameALinkInfoFromRecentlyVisitedLinks(
                webURL = webURL, newInfo = newNote
            )
        }
    }

    fun onTitleChangeClickForLinks(
        webURL: String,
        newTitle: String,
    ) {
        viewModelScope.launch {
            CustomFunctionsForLocalDB.localDB.crudDao().renameALinkTitleFromRecentlyVisited(
                webURL = webURL, newTitle = newTitle
            )
        }.invokeOnCompletion {
            changeHistoryRetrievedData(
                sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                    SettingsScreenVM.Settings.selectedSortingType.value
                )
            )
        }
    }

    fun onDeleteClick(
        context: Context,
        selectedWebURL: String,
        shouldDeleteBoxAppear: MutableState<Boolean>,
    ) {
        viewModelScope.launch {
            CustomFunctionsForLocalDB.localDB.crudDao().deleteARecentlyVisitedLink(
                webURL = selectedWebURL
            )
            shouldDeleteBoxAppear.value = false
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context, "deleted the link successfully", Toast.LENGTH_SHORT
                ).show()
            }
        }.invokeOnCompletion {
            changeHistoryRetrievedData(
                sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                    SettingsScreenVM.Settings.selectedSortingType.value
                )
            )
        }
    }
}