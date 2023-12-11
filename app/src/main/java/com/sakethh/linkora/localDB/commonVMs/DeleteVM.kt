package com.sakethh.linkora.localDB.commonVMs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.localDB.LocalDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeleteVM : ViewModel() {
    fun onRegularFolderDeleteClick(
        clickedFolderID: Long
    ) {
        viewModelScope.launch {
            LocalDataBase.localDB.deleteDao()
                .deleteAllChildFoldersAndLinksOfASpecificFolder(clickedFolderID)
            LocalDataBase.localDB.deleteDao().deleteThisFolderLinksV10(clickedFolderID)
            LocalDataBase.localDB.deleteDao()
                .deleteAFolder(
                    folderID = clickedFolderID
                )
        }
    }

    fun deleteEntireLinksAndFoldersData(onTaskCompleted: () -> Unit = {}) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                LocalDataBase.localDB.clearAllTables()
            }
        }.invokeOnCompletion {
            onTaskCompleted()
        }
    }
}