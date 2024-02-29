package com.sakethh.linkora.localDB.commonVMs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.localDB.LocalDataBase
import com.sakethh.linkora.localDB.dto.HomeScreenListTable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ReadVM : ViewModel() {
    private val _selectedShelfFolders = MutableStateFlow(emptyList<HomeScreenListTable>())
    val selectedShelfFolders = _selectedShelfFolders.asStateFlow()

    fun changeSelectedShelfFoldersData(shelfID: Long) {
        viewModelScope.launch {
            LocalDataBase.localDB.homeListsCrud().getAllHomeScreenListFoldersOfThisShelf(shelfID)
                .collectLatest {
                    _selectedShelfFolders.emit(it)
                }
        }
    }
}