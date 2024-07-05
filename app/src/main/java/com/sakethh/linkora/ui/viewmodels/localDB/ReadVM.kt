package com.sakethh.linkora.ui.viewmodels.localDB

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.HomeScreenListTable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ReadVM : ViewModel() {
    private val _selectedShelfFoldersForShelfBtmSheet =
        MutableStateFlow(emptyList<HomeScreenListTable>())
    val selectedShelfFoldersForShelfBtmSheet = _selectedShelfFoldersForShelfBtmSheet.asStateFlow()

    private val _selectedShelfFoldersForSelectedShelf =
        MutableStateFlow(emptyList<HomeScreenListTable>())
    val selectedShelfFoldersForSelectedShelf = _selectedShelfFoldersForSelectedShelf.asStateFlow()

    fun changeSelectedShelfFoldersDataForShelfBtmSheet(shelfID: Long) {
        viewModelScope.launch {
            LocalDatabase.localDB.shelfFolders().getAllFoldersOfThisShelf(shelfID)
                .collectLatest {
                    _selectedShelfFoldersForShelfBtmSheet.emit(it)
                }
        }
    }

    fun changeSelectedShelfFoldersDataForSelectedShelf(shelfID: Long) {
        viewModelScope.launch {
            LocalDatabase.localDB.shelfFolders().getAllFoldersOfThisShelf(shelfID)
                .collectLatest {
                    _selectedShelfFoldersForSelectedShelf.emit(it)
                }
        }
    }
}