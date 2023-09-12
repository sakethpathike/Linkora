package com.sakethh.linkora.screens.home

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
import com.sakethh.linkora.screens.collections.archiveScreen.ArchiveScreenModal
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreenType
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreenVM
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class HomeScreenVM(private val specificScreenVM: SpecificScreenVM = SpecificScreenVM()) :
    ViewModel() {
    val currentPhaseOfTheDay = mutableStateOf("")

    enum class HomeScreenType {
        SAVED_LINKS, IMP_LINKS
    }

    val parentHomeScreenData = listOf(ArchiveScreenModal(name = "Saved Links", screen = {
        ChildHomeScreen(homeScreenType = HomeScreenType.SAVED_LINKS, navController = it)
    }), ArchiveScreenModal(name = "Important Links", screen = {
        ChildHomeScreen(homeScreenType = HomeScreenType.IMP_LINKS, navController = it)
    }))

    companion object {
        var tempImpLinkData = ImportantLinks(
            title = "",
            webURL = "",
            baseURL = "",
            imgURL = "",
            infoForSaving = ""
        )
        var savedLinksData = LinksTable(
            title = "",
            webURL = "",
            baseURL = "",
            imgURL = "",
            infoForSaving = "",
            isLinkedWithSavedLinks = false,
            isLinkedWithFolders = false,
            keyOfLinkedFolder = "",
            isLinkedWithImpFolder = false,
            keyOfImpLinkedFolder = "",
            isLinkedWithArchivedFolder = false,
            keyOfArchiveLinkedFolder = ""
        )
    }

    init {
        viewModelScope.launch {
            awaitAll(async {
                specificScreenVM.changeRetrievedData(
                    sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                        SettingsScreenVM.Settings.selectedSortingType.value
                    ), folderName = "", screenType = SpecificScreenType.SAVED_LINKS_SCREEN
                )
            }, async {
                specificScreenVM.changeRetrievedData(
                    sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                        SettingsScreenVM.Settings.selectedSortingType.value
                    ), folderName = "", screenType = SpecificScreenType.IMPORTANT_LINKS_SCREEN
                )
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
    }


    fun onForceOpenInExternalBrowser(recentlyVisited: RecentlyVisited) {
        viewModelScope.launch {
            if (!CustomFunctionsForLocalDB.localDB.crudDao()
                    .doesThisExistsInRecentlyVisitedLinks(webURL = recentlyVisited.webURL)
            ) {
                CustomFunctionsForLocalDB.localDB.crudDao()
                    .addANewLinkInRecentlyVisited(
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

    fun onTitleChangeClickForLinks(
        selectedCardType: HomeScreenBtmSheetType,
        webURL: String,
        newTitle: String,
    ) {
        when (selectedCardType) {
            HomeScreenBtmSheetType.RECENT_SAVES -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .renameALinkTitleFromSavedLinks(
                            webURL = webURL, newTitle = newTitle
                        )
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_VISITS -> {

                Unit
            }

            HomeScreenBtmSheetType.RECENT_IMP_SAVES -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .renameALinkTitleFromImpLinks(webURL = webURL, newTitle = newTitle)
                }
                Unit
            }
        }
    }

    fun onNoteChangeClickForLinks(
        selectedCardType: HomeScreenBtmSheetType,
        webURL: String,
        newNote: String,
    ) {
        when (selectedCardType) {
            HomeScreenBtmSheetType.RECENT_SAVES -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .renameALinkInfoFromSavedLinks(
                            webURL = webURL, newInfo = newNote
                        )
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_VISITS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .renameALinkInfoFromRecentlyVisitedLinks(
                            webURL = webURL, newInfo = newNote
                        )
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_IMP_SAVES -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .renameALinkInfoFromImpLinks(webURL = webURL, newInfo = newNote)
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
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .deleteALinkFromSavedLinks(
                            webURL = selectedWebURL
                        )
                    shouldDeleteBoxAppear.value = false
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "deleted the link successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_VISITS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .deleteARecentlyVisitedLink(
                            webURL = selectedWebURL
                        )
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_IMP_SAVES -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .deleteALinkFromImpLinks(webURL = selectedWebURL)
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
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .deleteALinkInfoFromSavedLinks(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_VISITS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
                        .deleteANoteFromRecentlyVisited(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_IMP_SAVES -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.crudDao()
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
                        CustomFunctionsForLocalDB().archiveLinkTableUpdater(
                            archivedLinks = com.sakethh.linkora.localDB.ArchivedLinks(
                                title = tempImpLinkData.title,
                                webURL = tempImpLinkData.webURL,
                                baseURL = tempImpLinkData.baseURL,
                                imgURL = tempImpLinkData.imgURL,
                                infoForSaving = tempImpLinkData.infoForSaving
                            ), context = context, onTaskCompleted = {
                            }
                        )
                    }, async {
                        CustomFunctionsForLocalDB.localDB.crudDao()
                            .deleteALinkFromSavedLinks(webURL = tempImpLinkData.webURL)
                    })
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_VISITS -> {
                viewModelScope.launch {
                    awaitAll(async {
                        CustomFunctionsForLocalDB().archiveLinkTableUpdater(
                            archivedLinks = com.sakethh.linkora.localDB.ArchivedLinks(
                                title = tempImpLinkData.title,
                                webURL = tempImpLinkData.webURL,
                                baseURL = tempImpLinkData.baseURL,
                                imgURL = tempImpLinkData.imgURL,
                                infoForSaving = tempImpLinkData.infoForSaving
                            ), context = context, onTaskCompleted = {

                            }
                        )
                    }, async {
                        CustomFunctionsForLocalDB.localDB.crudDao()
                            .deleteARecentlyVisitedLink(webURL = tempImpLinkData.webURL)
                    })
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_IMP_SAVES -> {
                viewModelScope.launch {
                    awaitAll(async {
                        CustomFunctionsForLocalDB().archiveLinkTableUpdater(
                            archivedLinks = com.sakethh.linkora.localDB.ArchivedLinks(
                                title = tempImpLinkData.title,
                                webURL = tempImpLinkData.webURL,
                                baseURL = tempImpLinkData.baseURL,
                                imgURL = tempImpLinkData.imgURL,
                                infoForSaving = tempImpLinkData.infoForSaving
                            ), context = context, {}
                        )
                    }, async {
                        CustomFunctionsForLocalDB.localDB.crudDao()
                            .deleteALinkFromImpLinks(webURL = tempImpLinkData.webURL)
                    })
                }
                Unit
            }
        }
    }
}