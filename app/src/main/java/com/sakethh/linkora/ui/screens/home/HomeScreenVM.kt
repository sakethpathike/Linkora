package com.sakethh.linkora.ui.screens.home

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.HomeScreenListTable
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.Shelf
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.data.local.shelf.ShelfRepo
import com.sakethh.linkora.data.local.shelf.shelfLists.ShelfListsRepo
import com.sakethh.linkora.data.local.sorting.folders.regular.ParentRegularFoldersSortingRepo
import com.sakethh.linkora.data.local.sorting.folders.subfolders.SubFoldersSortingRepo
import com.sakethh.linkora.data.local.sorting.links.folder.archive.ArchivedFolderLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.folder.regular.RegularFolderLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.important.ImportantLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.saved.SavedLinksSortingRepo
import com.sakethh.linkora.ui.navigation.NavigationRoutes
import com.sakethh.linkora.ui.navigation.NavigationVM
import com.sakethh.linkora.ui.screens.CustomWebTab
import com.sakethh.linkora.ui.screens.collections.archive.ArchiveScreenModal
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenVM
import com.sakethh.linkora.ui.screens.collections.specific.SpecificScreenType
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
open class HomeScreenVM @Inject constructor(
    val linksRepo: LinksRepo,
    val foldersRepo: FoldersRepo,
    val savedLinksSortingRepo: SavedLinksSortingRepo,
    val importantLinksSortingRepo: ImportantLinksSortingRepo,
    val folderLinksSortingRepo: RegularFolderLinksSortingRepo,
    val archiveFolderLinksSortingRepo: ArchivedFolderLinksSortingRepo,
    val subFoldersSortingRepo: SubFoldersSortingRepo,
    val regularFoldersSortingRepo: ParentRegularFoldersSortingRepo,
    val parentRegularFoldersSortingRepo: ParentRegularFoldersSortingRepo,
    val shelfListsRepo: ShelfListsRepo,
    val shelfRepo: ShelfRepo,
    val customWebTab: CustomWebTab
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
    shelfRepo = shelfRepo,
    customWebTab = customWebTab
) {
    val currentPhaseOfTheDay = mutableStateOf("")

    val isSelectionModeEnabled = mutableStateOf(false)

    private val _shelfData = MutableStateFlow(emptyList<Shelf>())
    val shelfData = _shelfData.asStateFlow()

    private val _selectedShelfFoldersForSelectedShelf =
        MutableStateFlow(emptyList<HomeScreenListTable>())

    val selectedShelfFoldersForSelectedShelf = _selectedShelfFoldersForSelectedShelf.asStateFlow()

    fun changeSelectedShelfFoldersDataForSelectedShelf(shelfID: Long) {
        viewModelScope.launch {
            shelfListsRepo.getAllFoldersOfThisShelf(shelfID)
                .collectLatest {
                    _selectedShelfFoldersForSelectedShelf.emit(it)
                }
        }
    }
    enum class HomeScreenType {
        SAVED_LINKS, IMP_LINKS, CUSTOM_LIST
    }

    val selectedSavedLinkIds = mutableStateListOf<Long>()
    val selectedImpLinkIds = mutableStateListOf<Long>()
    fun moveSelectedSavedAndImpLinksToArchive() {
        viewModelScope.launch {
            awaitAll(async {
                selectedSavedLinkIds.toList().forEach {
                    linksRepo
                        .copyLinkFromLinksTableToArchiveLinks(it)
                    linksRepo.deleteALinkFromLinksTable(it)
                }
            }, async {
                selectedImpLinkIds.toList().forEach {
                    linksRepo.copyLinkFromImpLinksTableToArchiveLinks(it)
                    linksRepo.deleteALinkFromImpLinks(it)
                }
            })
        }
    }

    fun deleteSelectedSavedAndImpLinks() {
        viewModelScope.launch {
            awaitAll(async {
                selectedSavedLinkIds.toList().forEach {
                    linksRepo.deleteALinkFromLinksTable(it)
                }
            }, async {
                selectedImpLinkIds.toList().forEach {
                    linksRepo.deleteALinkFromImpLinks(it)
                }
            })
        }
    }

    val defaultScreenData = listOf(ArchiveScreenModal(name = "Saved Links", screen = { it, _ ->
        ChildHomeScreen(
            homeScreenType = HomeScreenType.SAVED_LINKS,
            navController = it,
            folderLinksData = emptyList(),
            childFoldersData = emptyList(),
            customWebTab
        )
    }), ArchiveScreenModal(name = "Important Links", screen = { it, _ ->
        ChildHomeScreen(
            homeScreenType = HomeScreenType.IMP_LINKS,
            it,
            emptyList(),
            emptyList(),
            customWebTab
        )
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
            shelfRepo.getAllShelfItems().collectLatest {
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
                    linksRepo.renameALinkTitleFromSavedLinks(
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
                    linksRepo.updateImpLinkTitle(linkID, newTitle = newTitle)
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
                    linksRepo.renameALinkInfoFromSavedLinks(
                        webURL = webURL, newInfo = newNote
                    )
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_VISITS -> {
                viewModelScope.launch {

                    linksRepo.renameALinkInfoFromRecentlyVisitedLinks(
                            webURL = webURL, newInfo = newNote
                        )
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_IMP_SAVES -> {
                viewModelScope.launch {

                    linksRepo.updateImpLinkNote(linkID, newInfo = newNote)
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
                    linksRepo.deleteALinkFromSavedLinksBasedOnURL(
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
                    linksRepo.deleteARecentlyVisitedLink(
                        webURL = selectedWebURL
                    )
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_IMP_SAVES -> {
                viewModelScope.launch {

                    linksRepo.deleteALinkFromImpLinks(linkID = tempImpLinkData.id)
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

                    linksRepo.deleteALinkInfoFromSavedLinks(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_VISITS -> {
                viewModelScope.launch {

                    linksRepo.deleteANoteFromRecentlyVisited(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_IMP_SAVES -> {
                viewModelScope.launch {

                    linksRepo.deleteANoteFromImportantLinks(webURL = selectedWebURL)
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
                        linksRepo.archiveLinkTableUpdater(
                            archivedLinks = ArchivedLinks(
                            title = tempImpLinkData.title,
                            webURL = tempImpLinkData.webURL,
                            baseURL = tempImpLinkData.baseURL,
                            imgURL = tempImpLinkData.imgURL,
                            infoForSaving = tempImpLinkData.infoForSaving
                        ), context = context, onTaskCompleted = {})
                    }, async {

                        linksRepo.deleteALinkFromSavedLinksBasedOnURL(webURL = tempImpLinkData.webURL)
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
                        linksRepo.archiveLinkTableUpdater(
                            archivedLinks = ArchivedLinks(
                            title = tempImpLinkData.title,
                            webURL = tempImpLinkData.webURL,
                            baseURL = tempImpLinkData.baseURL,
                            imgURL = tempImpLinkData.imgURL,
                            infoForSaving = tempImpLinkData.infoForSaving
                        ), context = context, onTaskCompleted = {

                        })
                    }, async {

                        linksRepo.deleteARecentlyVisitedLink(webURL = tempImpLinkData.webURL)
                    })
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_IMP_SAVES -> {
                viewModelScope.launch {
                    awaitAll(async {
                        linksRepo.archiveLinkTableUpdater(
                            archivedLinks = ArchivedLinks(
                            title = tempImpLinkData.title,
                            webURL = tempImpLinkData.webURL,
                            baseURL = tempImpLinkData.baseURL,
                            imgURL = tempImpLinkData.imgURL,
                            infoForSaving = tempImpLinkData.infoForSaving
                        ), context = context, {})
                    }, async {

                        linksRepo.deleteALinkFromImpLinks(linkID = tempImpLinkData.id)
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