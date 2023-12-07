package com.sakethh.linkora.screens.home

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.localDB.CustomFunctionsForLocalDB
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.navigation.NavigationRoutes
import com.sakethh.linkora.navigation.NavigationVM
import com.sakethh.linkora.screens.collections.archiveScreen.ArchiveScreenModal
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreenType
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificCollectionsScreenVM
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class HomeScreenVM : SpecificCollectionsScreenVM() {
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
    }


    fun onTitleChangeClickForLinks(
        selectedCardType: HomeScreenBtmSheetType,
        webURL: String,
        newTitle: String,
    ) {
        when (selectedCardType) {
            HomeScreenBtmSheetType.RECENT_SAVES -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.updateDao().renameALinkTitleFromSavedLinks(
                        webURL = webURL, newTitle = newTitle
                    )
                }.invokeOnCompletion {
                    SpecificCollectionsScreenVM().changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        ),
                        folderID = 0,
                        screenType = SpecificScreenType.SAVED_LINKS_SCREEN,
                        folderName = SpecificCollectionsScreenVM.currentClickedFolderData.value.folderName
                    )
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_VISITS -> {

                Unit
            }

            HomeScreenBtmSheetType.RECENT_IMP_SAVES -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.updateDao()
                        .renameALinkTitleFromImpLinks(webURL = webURL, newTitle = newTitle)
                }.invokeOnCompletion {
                    SpecificCollectionsScreenVM().changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        ),
                        folderID = 0,
                        screenType = SpecificScreenType.IMPORTANT_LINKS_SCREEN,
                        folderName = SpecificCollectionsScreenVM.currentClickedFolderData.value.folderName
                    )
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
                    CustomFunctionsForLocalDB.localDB.updateDao().renameALinkInfoFromSavedLinks(
                        webURL = webURL, newInfo = newNote
                    )
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_VISITS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.updateDao()
                        .renameALinkInfoFromRecentlyVisitedLinks(
                            webURL = webURL, newInfo = newNote
                        )
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_IMP_SAVES -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.updateDao()
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
                    CustomFunctionsForLocalDB.localDB.deleteDao().deleteALinkFromSavedLinks(
                        webURL = selectedWebURL
                    )
                    shouldDeleteBoxAppear.value = false
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "deleted the link successfully", Toast.LENGTH_SHORT
                        ).show()
                    }
                }.invokeOnCompletion {
                    SpecificCollectionsScreenVM().changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        ),
                        folderID = 0,
                        screenType = SpecificScreenType.SAVED_LINKS_SCREEN,
                        folderName = SpecificCollectionsScreenVM.currentClickedFolderData.value.folderName
                    )
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_VISITS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.deleteDao().deleteARecentlyVisitedLink(
                        webURL = selectedWebURL
                    )
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_IMP_SAVES -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.deleteDao()
                        .deleteALinkFromImpLinks(webURL = selectedWebURL)
                }.invokeOnCompletion {
                    SpecificCollectionsScreenVM().changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        ),
                        folderID = 0,
                        screenType = SpecificScreenType.IMPORTANT_LINKS_SCREEN,
                        folderName = currentClickedFolderData.value.folderName
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
                    CustomFunctionsForLocalDB.localDB.deleteDao()
                        .deleteALinkInfoFromSavedLinks(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_VISITS -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.deleteDao()
                        .deleteANoteFromRecentlyVisited(webURL = selectedWebURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                    }
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_IMP_SAVES -> {
                viewModelScope.launch {
                    CustomFunctionsForLocalDB.localDB.deleteDao()
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
                        CustomFunctionsForLocalDB().archiveLinkTableUpdater(archivedLinks = ArchivedLinks(
                            title = tempImpLinkData.title,
                            webURL = tempImpLinkData.webURL,
                            baseURL = tempImpLinkData.baseURL,
                            imgURL = tempImpLinkData.imgURL,
                            infoForSaving = tempImpLinkData.infoForSaving
                        ), context = context, onTaskCompleted = {})
                    }, async {
                        CustomFunctionsForLocalDB.localDB.deleteDao()
                            .deleteALinkFromSavedLinks(webURL = tempImpLinkData.webURL)
                    })
                }.invokeOnCompletion {
                    SpecificCollectionsScreenVM().changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        ),
                        folderID = 0,
                        screenType = SpecificScreenType.SAVED_LINKS_SCREEN,
                        folderName = SpecificCollectionsScreenVM.currentClickedFolderData.value.folderName
                    )
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_VISITS -> {
                viewModelScope.launch {
                    awaitAll(async {
                        CustomFunctionsForLocalDB().archiveLinkTableUpdater(archivedLinks = ArchivedLinks(
                            title = tempImpLinkData.title,
                            webURL = tempImpLinkData.webURL,
                            baseURL = tempImpLinkData.baseURL,
                            imgURL = tempImpLinkData.imgURL,
                            infoForSaving = tempImpLinkData.infoForSaving
                        ), context = context, onTaskCompleted = {

                        })
                    }, async {
                        CustomFunctionsForLocalDB.localDB.deleteDao()
                            .deleteARecentlyVisitedLink(webURL = tempImpLinkData.webURL)
                    })
                }
                Unit
            }

            HomeScreenBtmSheetType.RECENT_IMP_SAVES -> {
                viewModelScope.launch {
                    awaitAll(async {
                        CustomFunctionsForLocalDB().archiveLinkTableUpdater(archivedLinks = ArchivedLinks(
                            title = tempImpLinkData.title,
                            webURL = tempImpLinkData.webURL,
                            baseURL = tempImpLinkData.baseURL,
                            imgURL = tempImpLinkData.imgURL,
                            infoForSaving = tempImpLinkData.infoForSaving
                        ), context = context, {})
                    }, async {
                        CustomFunctionsForLocalDB.localDB.deleteDao()
                            .deleteALinkFromImpLinks(webURL = tempImpLinkData.webURL)
                    })
                }.invokeOnCompletion {
                    SpecificCollectionsScreenVM().changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        ),
                        folderID = 0,
                        screenType = SpecificScreenType.IMPORTANT_LINKS_SCREEN,
                        folderName = SpecificCollectionsScreenVM.currentClickedFolderData.value.folderName
                    )
                }
                Unit
            }
        }
    }
}