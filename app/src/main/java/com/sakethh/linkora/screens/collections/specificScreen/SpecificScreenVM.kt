package com.sakethh.linkora.screens.collections.specificScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.localDB.ImportantLinks
import com.sakethh.linkora.localDB.LinksTable
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SpecificScreenVM : ViewModel() {
    private val _folderLinksData = MutableStateFlow(emptyList<LinksTable>())
    val folderLinksData = _folderLinksData.asStateFlow()

    private val _savedLinksData = MutableStateFlow(emptyList<LinksTable>())
    val savedLinksTable = _savedLinksData.asStateFlow()

    private val _impLinksData = MutableStateFlow(emptyList<ImportantLinks>())
    val impLinksTable = _impLinksData.asStateFlow()

    private val _archiveFolderData = MutableStateFlow(emptyList<LinksTable>())
    val archiveFolderDataTable = _archiveFolderData.asStateFlow()

    val impLinkDataForBtmSheet = ImportantLinks(
        title = "",
        webURL = "",
        baseURL = "",
        imgURL = "",
        infoForSaving = ""
    )

    companion object {
        val currentClickedFolderName = mutableStateOf("")
        val screenType = mutableStateOf(SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN)
        val selectedArchiveFolderName = mutableStateOf("")
    }

    init {
        changeRetrievedData(
            sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(SettingsScreenVM.Settings.selectedSortingType.value),
            folderName = currentClickedFolderName.value
        )
    }

    fun changeRetrievedData(
        sortingPreferences: SettingsScreenVM.SortingPreferences,
        folderName: String,
    ) {
        when (screenType.value) {
            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                when (sortingPreferences) {
                    SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                        viewModelScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.savedLinksSorting().sortByAToZ()
                                .collect {
                                    _savedLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.savedLinksSorting().sortByZToA()
                                .collect {
                                    _savedLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.savedLinksSorting()
                                .sortByLatestToOldest().collect {
                                    _savedLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.savedLinksSorting()
                                .sortByOldestToLatest().collect {
                                    _savedLinksData.emit(it)
                                }
                        }
                    }
                }
            }

            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                when (sortingPreferences) {
                    SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                        viewModelScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.importantLinksSorting()
                                .sortByAToZ().collect {
                                    _impLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.importantLinksSorting()
                                .sortByZToA().collect {
                                    _impLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.importantLinksSorting()
                                .sortByLatestToOldest().collect {
                                    _impLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.importantLinksSorting()
                                .sortByOldestToLatest().collect {
                                    _impLinksData.emit(it)
                                }
                        }
                    }
                }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                when (sortingPreferences) {
                    SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                        viewModelScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.archivedFolderLinksSorting()
                                .sortByAToZ(folderName = folderName).collect {
                                    _archiveFolderData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.archivedFolderLinksSorting()
                                .sortByZToA(folderName = folderName).collect {
                                    _archiveFolderData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.archivedFolderLinksSorting()
                                .sortByLatestToOldest(folderName = folderName).collect {
                                    _archiveFolderData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.archivedFolderLinksSorting()
                                .sortByOldestToLatest(folderName = folderName).collect {
                                    _archiveFolderData.emit(it)
                                }
                        }
                    }
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                when (sortingPreferences) {
                    SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                        viewModelScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.regularFolderLinksSorting()
                                .sortByAToZ(folderName = folderName).collect {
                                    _folderLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.regularFolderLinksSorting()
                                .sortByZToA(folderName = folderName).collect {
                                    _folderLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.regularFolderLinksSorting()
                                .sortByLatestToOldest(folderName = folderName).collect {
                                    _folderLinksData.emit(it)
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.regularFolderLinksSorting()
                                .sortByOldestToLatest(folderName = folderName).collect {
                                    _folderLinksData.emit(it)
                                }
                        }
                    }
                }
            }

            SpecificScreenType.INTENT_ACTIVITY -> {

            }

            SpecificScreenType.ROOT_SCREEN -> {

            }
        }
    }
}

enum class SpecificScreenType {
    IMPORTANT_LINKS_SCREEN, ARCHIVED_FOLDERS_LINKS_SCREEN, SAVED_LINKS_SCREEN, SPECIFIC_FOLDER_LINKS_SCREEN, INTENT_ACTIVITY, ROOT_SCREEN
}