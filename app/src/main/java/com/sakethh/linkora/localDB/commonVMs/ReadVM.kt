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
    private val _readHomeScreenListTable = MutableStateFlow(emptyList<HomeScreenListTable>())
    val readHomeScreenListTable = _readHomeScreenListTable.asStateFlow()

    fun readHomeScreenListTable() {
        viewModelScope.launch {
            LocalDataBase.localDB.homeListsCrud().getAllHomeScreenListFolders().collectLatest {
                _readHomeScreenListTable.emit(it)
            }
        }
    }
}