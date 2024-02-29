package com.sakethh.linkora.localDB.commonVMs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.localDB.LocalDataBase
import com.sakethh.linkora.localDB.dto.Shelf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeleteVM : ViewModel() {
    fun deleteAShelf(shelf: Shelf) {
        viewModelScope.launch {
            LocalDataBase.localDB.shelfCrud().deleteAShelf(shelf)
        }
    }

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
            deleteAnElementFromHomeScreenList(clickedFolderID)
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

    fun deleteAnElementFromHomeScreenList(folderID: Long) {
        viewModelScope.launch {
            LocalDataBase.localDB.homeListsCrud().deleteAHomeScreenListFolder(folderID)
        }
    }
}