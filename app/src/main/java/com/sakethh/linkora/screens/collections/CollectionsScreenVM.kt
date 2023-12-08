package com.sakethh.linkora.screens.collections

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.localDB.LocalDataBase
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificCollectionsScreenVM.Companion.isSelectedV9
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class CollectionsScreenVM : ViewModel() {
    private val _foldersData = MutableStateFlow(emptyList<FoldersTable>())
    val foldersData = _foldersData.asStateFlow()

    companion object {
        var rootFolderID: Long = 0
        val selectedFolderData = mutableStateOf(FoldersTable(
            folderName = "",
            infoForSaving = "",
            parentFolderID = 0,
            childFolderIDs = emptyList(),
        ))
        var selectedLinkData = LinksTable(
            id = 0L,
            title = "",
            webURL = "",
            baseURL = "",
            imgURL = "",
            infoForSaving = "",
            isLinkedWithSavedLinks = true,
            isLinkedWithFolders = true,
            keyOfLinkedFolderV10 = null,
            keyOfLinkedFolder = null,
            isLinkedWithImpFolder = false,
            keyOfImpLinkedFolder = "",
            keyOfImpLinkedFolderV10 = null,
            isLinkedWithArchivedFolder = false,
            keyOfArchiveLinkedFolderV10 = null,
            keyOfArchiveLinkedFolder = null
        )
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
                    LocalDataBase.localDB.regularFolderSorting().sortByAToZ()
                        .collect {
                            _foldersData.emit(it)
                        }
                }
            }

            SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.regularFolderSorting().sortByZToA()
                        .collect {
                            _foldersData.emit(it)
                        }
                }
            }

            SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.regularFolderSorting()
                        .sortByLatestToOldest()
                        .collect {
                            _foldersData.emit(it)
                        }
                }
            }

            SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.regularFolderSorting()
                        .sortByOldestToLatest()
                        .collect {
                            _foldersData.emit(it)
                        }
                }
            }
        }
    }

    fun onNoteDeleteClick(context: Context, clickedFolderID: Long) {
        viewModelScope.launch {
            LocalDataBase.localDB.deleteDao()
                .deleteAFolderNote(folderID = clickedFolderID)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
            }
        }
    }

}