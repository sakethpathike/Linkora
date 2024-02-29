package com.sakethh.linkora.btmSheet

import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.localDB.dto.HomeScreenListTable
import com.sakethh.linkora.localDB.dto.Shelf
import com.sakethh.linkora.screens.home.HomeScreenVM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShelfBtmSheetVM : HomeScreenVM() {
    private val _readHomeScreenListTable = MutableStateFlow(emptyList<HomeScreenListTable>())
    val readHomeScreenListTable = _readHomeScreenListTable.asStateFlow()

    companion object {
        var selectedShelfData =
            Shelf(id = 0L, shelfName = "", shelfIconName = "", folderIds = listOf())
    }

    init {
        viewModelScope.launch {

        }
    }
}