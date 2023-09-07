package com.sakethh.linkora.screens.browse.archiveScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.sakethh.linkora.localDB.ArchivedFolders
import com.sakethh.linkora.localDB.ArchivedLinks
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ArchiveScreenModal(
    val name: String,
    val screen: @Composable (navController: NavController) -> Unit,
)

enum class ArchiveScreenType {
    LINKS, FOLDERS
}

class ArchiveScreenVM : ViewModel() {
    val selectedArchivedLinkData = mutableStateOf(
        ArchivedLinks(
            title = "",
            webURL = "",
            baseURL = "",
            imgURL = "",
            infoForSaving = ""
        )
    )

    val parentArchiveScreenData = listOf(
        ArchiveScreenModal(name = "Links", screen = { navController ->
            ChildArchiveScreen(
                archiveScreenType = ArchiveScreenType.LINKS,
                navController = navController
            )
        }), ArchiveScreenModal(name = "Folders",
            screen = { navController ->
                ChildArchiveScreen(
                    archiveScreenType = ArchiveScreenType.FOLDERS,
                    navController = navController
                )
            })
    )
    private val _archiveLinksData = MutableStateFlow(emptyList<ArchivedLinks>())
    val archiveLinksData = _archiveLinksData.asStateFlow()

    private val _archiveFoldersData = MutableStateFlow(emptyList<ArchivedFolders>())
    val archiveFoldersData = _archiveFoldersData.asStateFlow()

    init {
        changeRetrievedData(
            sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                SettingsScreenVM.Settings.selectedSortingType.value
            )
        )
    }

    fun changeRetrievedData(sortingPreferences: SettingsScreenVM.SortingPreferences) {
        when (sortingPreferences) {
            SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                viewModelScope.launch {
                    awaitAll(async {
                        CustomLocalDBDaoFunctionsDecl.localDB.archivedLinksSorting().sortByAToZ()
                            .collect {
                                _archiveLinksData.emit(it)
                            }
                    }, async {
                        CustomLocalDBDaoFunctionsDecl.localDB.archivedFolderSorting().sortByAToZ()
                            .collect {
                                _archiveFoldersData.emit(it)
                            }
                    })
                }
            }

            SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                viewModelScope.launch {
                    awaitAll(async {
                        CustomLocalDBDaoFunctionsDecl.localDB.archivedLinksSorting().sortByZToA()
                            .collect {
                                _archiveLinksData.emit(it)
                            }
                    }, async {
                        CustomLocalDBDaoFunctionsDecl.localDB.archivedFolderSorting().sortByZToA()
                            .collect {
                                _archiveFoldersData.emit(it)
                            }
                    })
                }
            }

            SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                viewModelScope.launch {
                    awaitAll(async {
                        CustomLocalDBDaoFunctionsDecl.localDB.archivedLinksSorting()
                            .sortByLatestToOldest().collect {
                                _archiveLinksData.emit(it)
                            }
                    }, async {
                        CustomLocalDBDaoFunctionsDecl.localDB.archivedFolderSorting()
                            .sortByLatestToOldest().collect {
                                _archiveFoldersData.emit(it)
                            }
                    })
                }
            }

            SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                viewModelScope.launch {
                    awaitAll(async {
                        CustomLocalDBDaoFunctionsDecl.localDB.archivedLinksSorting()
                            .sortByOldestToLatest().collect {
                                _archiveLinksData.emit(it)
                            }
                    }, async {
                        CustomLocalDBDaoFunctionsDecl.localDB.archivedFolderSorting()
                            .sortByOldestToLatest().collect {
                                _archiveFoldersData.emit(it)
                            }
                    })
                }
            }
        }
    }
}