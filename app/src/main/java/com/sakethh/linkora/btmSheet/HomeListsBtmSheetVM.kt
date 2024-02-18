package com.sakethh.linkora.btmSheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.localDB.LocalDataBase
import com.sakethh.linkora.localDB.dto.HomeScreenListTable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeListsBtmSheetVM : ViewModel() {
    private val _readHomeScreenListTable = MutableStateFlow(emptyList<HomeScreenListTable>())
    val readHomeScreenListTable = _readHomeScreenListTable.asStateFlow()

    init {
        viewModelScope.launch {
            LocalDataBase.localDB.homeListsCrud().getAllHomeScreenListFolders().collectLatest {
                _readHomeScreenListTable.emit(it)
            }
        }
    }
}