package com.sakethh.linkora

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.restore.ImportRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainActivityVM @Inject constructor(
    private val foldersRepo: FoldersRepo,
    private val importRepo: ImportRepo
) : ViewModel() {

    fun migrateArchiveFoldersV9toV10() {
        viewModelScope.launch {
            if (foldersRepo.getAllArchiveFoldersV9List()
                    .isNotEmpty()
            ) {
                importRepo.migrateArchiveFoldersV9toV10()
            }
        }
    }

    fun migrateRegularFoldersLinksDataFromV9toV10() {
        viewModelScope.launch {
            if (foldersRepo.getAllRootFoldersList()
                    .isNotEmpty()
            ) {
                importRepo.migrateRegularFoldersLinksDataFromV9toV10()
            }
        }
    }

}