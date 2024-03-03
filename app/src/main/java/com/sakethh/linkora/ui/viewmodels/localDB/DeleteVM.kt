package com.sakethh.linkora.ui.viewmodels.localDB

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.localDB.LocalDataBase
import com.sakethh.linkora.data.localDB.dto.Shelf
import com.sakethh.linkora.utils.DeleteAFolderFromShelf
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
        }
        viewModelScope.launch {
            DeleteAFolderFromShelf.execute(clickedFolderID)
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

    fun deleteAFolderFromShelf(folderID: Long) {
        viewModelScope.launch {
            LocalDataBase.localDB.shelfFolders().deleteAShelfFolder(folderID)
        }
    }
}