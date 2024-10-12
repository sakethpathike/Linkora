package com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.folders.FoldersRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddANewLinkDialogBoxVM @Inject constructor(val foldersRepo: FoldersRepo) : ViewModel() {
    val subFoldersList = mutableStateListOf<FoldersTable>()

    private val _childFolders = MutableStateFlow(emptyList<FoldersTable>())
    val childFolders = _childFolders.asStateFlow()

    fun changeParentFolderId(parentFolderId: Long) {
        viewModelScope.launch {
            foldersRepo.getChildFoldersOfThisParentID(parentFolderId).collectLatest {
                _childFolders.emit(it)
            }
        }
    }
}