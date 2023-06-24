package com.sakethh.linkora.screens.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.localDB.FoldersTable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CollectionsScreenVM : ViewModel() {
    private val _foldersData = MutableStateFlow(emptyList<FoldersTable>())
    val foldersData = _foldersData.asStateFlow()

    init {
        viewModelScope.launch {
            CustomLocalDBDaoFunctionsDecl.getAllFolders().collect {
                _foldersData.value = it
            }
        }
    }
}