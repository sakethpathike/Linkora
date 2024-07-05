package com.sakethh.linkora.ui.viewmodels.localDB

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.Shelf
import com.sakethh.linkora.utils.DeleteAFolderFromShelf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeleteVM : ViewModel() {
    fun deleteAShelf(shelf: Shelf) {
        viewModelScope.launch {
            LocalDatabase.localDB.shelfCrud().deleteAShelf(shelf)
        }
    }

    fun onRegularFolderDeleteClick(
        clickedFolderID: Long
    ) {
        viewModelScope.launch {
            LocalDatabase.localDB.deleteDao()
                .deleteAllChildFoldersAndLinksOfASpecificFolder(clickedFolderID)
            LocalDatabase.localDB.deleteDao().deleteThisFolderLinksV10(clickedFolderID)
            LocalDatabase.localDB.deleteDao()
                .deleteAFolder(
                    folderID = clickedFolderID
                )
        }
        viewModelScope.launch {
            DeleteAFolderFromShelf.execute(clickedFolderID)
        }
    }

    fun deleteEntireLinksAndFoldersData(onTaskCompleted: () -> Unit = {}) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                LocalDatabase.localDB.clearAllTables()
            }
        }.invokeOnCompletion {
            onTaskCompleted()
        }
    }

    fun deleteAFolderFromShelf(folderID: Long) {
        viewModelScope.launch {
            LocalDatabase.localDB.shelfFolders().deleteAShelfFolder(folderID)
        }
    }
}