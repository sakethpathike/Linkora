package com.sakethh.linkora.ui.viewmodels.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.localDB.LocalDataBase
import com.sakethh.linkora.data.localDB.dto.ImportantLinks
import com.sakethh.linkora.data.localDB.dto.Shelf
import com.sakethh.linkora.ui.navigation.NavigationRoutes
import com.sakethh.linkora.ui.navigation.NavigationVM
import com.sakethh.linkora.ui.screens.home.ChildHomeScreen
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM
import com.sakethh.linkora.ui.viewmodels.collections.ArchiveScreenModal
import com.sakethh.linkora.ui.viewmodels.collections.SpecificCollectionsScreenVM
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
                    LocalDataBase.localDB.updateDao()
                        .copyLinkFromLinksTableToArchiveLinks(it)
                    LocalDataBase.localDB.deleteDao().deleteALinkFromLinksTable(it)
                }
            }, async {
                selectedImpLinkIds.toList().forEach {
                    LocalDataBase.localDB.updateDao()
                        .copyLinkFromImpLinksTableToArchiveLinks(it)
                    LocalDataBase.localDB.deleteDao().deleteALinkFromImpLinks(it)
                }
            })
        }
    }

    fun deleteSelectedSavedAndImpLinks() {
        viewModelScope.launch {
            awaitAll(async {
                selectedSavedLinkIds.toList().forEach {
                    LocalDataBase.localDB.deleteDao().deleteALinkFromLinksTable(it)
                }
            }, async {
                selectedImpLinkIds.toList().forEach {
                    LocalDataBase.localDB.deleteDao().deleteALinkFromImpLinks(it)
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

                    in 16 downTo 0 -> {
                        currentPhaseOfTheDay.value = "Good Evening"
                    }

                    else -> {
                        currentPhaseOfTheDay.value = "Hey, hi\uD83D\uDC4B"
                    }
                }
            })
        }
        viewModelScope.launch {
            LocalDataBase.localDB.shelfCrud().getAllShelfItems().collectLatest {
                _shelfData.emit(it)
            }
        }
    }
}