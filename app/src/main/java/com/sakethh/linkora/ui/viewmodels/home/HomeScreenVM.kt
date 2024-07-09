package com.sakethh.linkora.ui.viewmodels.home

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.Shelf
import com.sakethh.linkora.ui.navigation.NavigationRoutes
import com.sakethh.linkora.ui.navigation.NavigationVM
import com.sakethh.linkora.ui.screens.home.ChildHomeScreen
import com.sakethh.linkora.ui.screens.home.HomeScreenBtmSheetType
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM
import com.sakethh.linkora.ui.viewmodels.collections.ArchiveScreenModal
import com.sakethh.linkora.ui.viewmodels.collections.SpecificCollectionsScreenVM
import com.sakethh.linkora.ui.viewmodels.collections.SpecificScreenType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

open class HomeScreenVM : SpecificCollectionsScreenVM() {
    val currentPhaseOfTheDay = mutableStateOf("")

    val isSelectionModeEnabled = mutableStateOf(false)

    private val _shelfData = MutableStateFlow(emptyList<Shelf>())
    val shelfData = _shelfData.asStateFlow()

    enum class HomeScreenType {
        SAVED_LINKS, IMP_LINKS, CUSTOM_LIST
    }

    val selectedSavedLinkIds = mutableStateListOf<Long>()
    val selectedImpLinkIds = mutableStateListOf<Long>()
    fun moveSelectedSavedAndImpLinksToArchive() {
        viewModelScope.launch {
            awaitAll(async {
                selectedSavedLinkIds.toList().forEach {
                    LocalDatabase.localDB.updateDao()
                        .copyLinkFromLinksTableToArchiveLinks(it)
                    LocalDatabase.localDB.deleteDao().deleteALinkFromLinksTable(it)
                }
            }, async {
                selectedImpLinkIds.toList().forEach {
                    LocalDatabase.localDB.updateDao()
                        .copyLinkFromImpLinksTableToArchiveLinks(it)
                    LocalDatabase.localDB.deleteDao().deleteALinkFromImpLinks(it)
                }
            })
        }
    }

    fun deleteSelectedSavedAndImpLinks() {
        viewModelScope.launch {
            awaitAll(async {
                selectedSavedLinkIds.toList().forEach {
                    LocalDatabase.localDB.deleteDao().deleteALinkFromLinksTable(it)
                }
            }, async {
                selectedImpLinkIds.toList().forEach {
                    LocalDatabase.localDB.deleteDao().deleteALinkFromImpLinks(it)
                }
            })
        }
    }

    val defaultScreenData = listOf(ArchiveScreenModal(name = "Saved Links", screen = {
        ChildHomeScreen(
            homeScreenType = HomeScreenType.SAVED_LINKS,
            navController = it,
            folderLinksData = emptyList(),
            childFoldersData = emptyList()
        )
    }), ArchiveScreenModal(name = "Important Links", screen = {
        ChildHomeScreen(homeScreenType = HomeScreenType.IMP_LINKS, it, emptyList(), emptyList())
    }))

    companion object {
        var currentHomeScreenType = HomeScreenType.SAVED_LINKS
        val tempImpLinkData = ImportantLinks(
            title = "", webURL = "", baseURL = "", imgURL = "", infoForSaving = ""
        )
    }

    init {
        viewModelScope.launch {
            awaitAll(async {
                NavigationVM.startDestination.value =
                    if (SettingsScreenVM.Settings.isHomeScreenEnabled.value) {
                        NavigationRoutes.HOME_SCREEN.name
                    } else {
                        NavigationRoutes.COLLECTIONS_SCREEN.name
                    }
            }, async {
                when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                    in 0..11 -> {
                        currentPhaseOfTheDay.value = "Good Morning"
                    }

                    in 12..15 -> {
                        currentPhaseOfTheDay.value = "Good Afternoon"
                    }

                    in 16..22 -> {
                        currentPhaseOfTheDay.value = "Good Evening"
                    }

                    in 23 downTo 0 -> {
                        currentPhaseOfTheDay.value = "Good Night?"
                    }

                    else -> {
                        currentPhaseOfTheDay.value = "Hey, hi\uD83D\uDC4B"
                    }
                }
            })
        }
        viewModelScope.launch {
            LocalDatabase.localDB.shelfCrud().getAllShelfItems().collectLatest {
                _shelfData.emit(it)
            }
        }
    }


    fun onTitleChangeClickForLinks(
        selectedCardType: HomeScreenBtmSheetType,
        webURL: String,
        linkID: Long,
        newTitle: String,
    ) {
        when (selectedCardType) {
            HomeScreenBtmSheetType.RECENT_SAVES -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.updateDao().renameALinkTitleFromSavedLinks(
                        webURL = webURL, newTitle = newTitle
                    )
                }.invokeOnCompletion {
                    changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        ),
                        folderID = 0,
                        screenType = SpecificScreenType.SAVED_LINKS_SCREEN
                    )
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_VISITS -> {

                Unit
            }

            HomeScreenBtmSheetType.RECENT_IMP_SAVES -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.updateDao()
                        .renameALinkTitleFromImpLinks(linkID, newTitle = newTitle)
                }.invokeOnCompletion {
                    changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        ),
                        folderID = 0,
                        screenType = SpecificScreenType.IMPORTANT_LINKS_SCREEN
                    )
                }
                Unit
            }
        }
    }

    fun onNoteChangeClickForLinks(
        selectedCardType: HomeScreenBtmSheetType,
        webURL: String,
        linkID: Long,
        newNote: String,
    ) {
        when (selectedCardType) {
            HomeScreenBtmSheetType.RECENT_SAVES -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.updateDao().renameALinkInfoFromSavedLinks(
                        webURL = webURL, newInfo = newNote
                    )
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_VISITS -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.updateDao()
                        .renameALinkInfoFromRecentlyVisitedLinks(
                            webURL = webURL, newInfo = newNote
                        )
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_IMP_SAVES -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.updateDao()
                        .renameALinkInfoFromImpLinks(linkID, newInfo = newNote)
                }
                Unit
            }
        }
    }

    fun onDeleteClick(
        selectedCardType: HomeScreenBtmSheetType,
        context: Context,
        selectedWebURL: String,
        shouldDeleteBoxAppear: MutableState<Boolean>,
    ) {
        when (selectedCardType) {
            HomeScreenBtmSheetType.RECENT_SAVES -> {
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
                }.invokeOnCompletion {
                    changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        ),
                        folderID = 0,
                        screenType = SpecificScreenType.SAVED_LINKS_SCREEN
                    )
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_VISITS -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.deleteDao().deleteARecentlyVisitedLink(
                        webURL = selectedWebURL
                    )
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_IMP_SAVES -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.deleteDao()
                        .deleteALinkFromImpLinks(linkID = tempImpLinkData.id)
                }.invokeOnCompletion {
                    changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        ),
                        folderID = 0,
                        screenType = SpecificScreenType.IMPORTANT_LINKS_SCREEN
                    )
                }
                Unit
            }
        }
    }

    fun onNoteDeleteCardClick(
        selectedCardType: HomeScreenBtmSheetType,
        context: Context,
        selectedWebURL: String,
    ) {
        when (selectedCardType) {
            HomeScreenBtmSheetType.RECENT_SAVES -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.deleteDao()
                        .deleteALinkInfoFromSavedLinks(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_VISITS -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.deleteDao()
                        .deleteANoteFromRecentlyVisited(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_IMP_SAVES -> {
                viewModelScope.launch {
                    LocalDatabase.localDB.deleteDao()
                        .deleteANoteFromImportantLinks(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
                Unit
            }
        }

    }

    fun onArchiveClick(selectedCardType: HomeScreenBtmSheetType, context: Context) {
        when (selectedCardType) {
            HomeScreenBtmSheetType.RECENT_SAVES -> {
                viewModelScope.launch {
                    awaitAll(async {
                        updateVM.archiveLinkTableUpdater(archivedLinks = ArchivedLinks(
                            title = tempImpLinkData.title,
                            webURL = tempImpLinkData.webURL,
                            baseURL = tempImpLinkData.baseURL,
                            imgURL = tempImpLinkData.imgURL,
                            infoForSaving = tempImpLinkData.infoForSaving
                        ), context = context, onTaskCompleted = {})
                    }, async {
                        LocalDatabase.localDB.deleteDao()
                            .deleteALinkFromSavedLinksBasedOnURL(webURL = tempImpLinkData.webURL)
                    })
                }.invokeOnCompletion {
                    changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        ),
                        folderID = 0,
                        screenType = SpecificScreenType.SAVED_LINKS_SCREEN
                    )
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_VISITS -> {
                viewModelScope.launch {
                    awaitAll(async {
                        updateVM.archiveLinkTableUpdater(archivedLinks = ArchivedLinks(
                            title = tempImpLinkData.title,
                            webURL = tempImpLinkData.webURL,
                            baseURL = tempImpLinkData.baseURL,
                            imgURL = tempImpLinkData.imgURL,
                            infoForSaving = tempImpLinkData.infoForSaving
                        ), context = context, onTaskCompleted = {

                        })
                    }, async {
                        LocalDatabase.localDB.deleteDao()
                            .deleteARecentlyVisitedLink(webURL = tempImpLinkData.webURL)
                    })
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_IMP_SAVES -> {
                viewModelScope.launch {
                    awaitAll(async {
                        updateVM.archiveLinkTableUpdater(archivedLinks = ArchivedLinks(
                            title = tempImpLinkData.title,
                            webURL = tempImpLinkData.webURL,
                            baseURL = tempImpLinkData.baseURL,
                            imgURL = tempImpLinkData.imgURL,
                            infoForSaving = tempImpLinkData.infoForSaving
                        ), context = context, {})
                    }, async {
                        LocalDatabase.localDB.deleteDao()
                            .deleteALinkFromImpLinks(linkID = tempImpLinkData.id)
                    })
                }.invokeOnCompletion {
                    changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        ),
                        folderID = 0,
                        screenType = SpecificScreenType.IMPORTANT_LINKS_SCREEN
                    )
                }
                Unit
            }
        }
    }
}