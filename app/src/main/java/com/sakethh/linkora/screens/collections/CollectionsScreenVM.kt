package com.sakethh.linkora.screens.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.localDB.FoldersTable
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CollectionsScreenVM : ViewModel() {
    private val _foldersData = MutableStateFlow(emptyList<FoldersTable>())
    val foldersData = _foldersData.asStateFlow()

    companion object {
        val selectedFolderData = FoldersTable(folderName = "", infoForSaving = "")
    }

    init {
        changeRetrievedFoldersData(
            sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                SettingsScreenVM.Settings.selectedSortingType.value
            )
        )
    }

    fun changeRetrievedFoldersData(sortingPreferences: SettingsScreenVM.SortingPreferences) {
        when (sortingPreferences) {
            SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                viewModelScope.launch {
                    CustomLocalDBDaoFunctionsDecl.localDB.regularFolderSorting().sortByAToZ()
                        .collect {
                            _foldersData.emit(it)
                        }
                }
            }

            SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                viewModelScope.launch {
                    CustomLocalDBDaoFunctionsDecl.localDB.regularFolderSorting().sortByZToA()
                        .collect {
                            _foldersData.emit(it)
                        }
                }
            }

            SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                viewModelScope.launch {
                    CustomLocalDBDaoFunctionsDecl.localDB.regularFolderSorting()
                        .sortByLatestToOldest()
                        .collect {
                            _foldersData.emit(it)
                        }
                }
            }

            SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                viewModelScope.launch {
                    CustomLocalDBDaoFunctionsDecl.localDB.regularFolderSorting()
                        .sortByOldestToLatest()
                        .collect {
                            _foldersData.emit(it)
                        }
                }
            }
        }
    }
}