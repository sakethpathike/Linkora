package com.sakethh.linkora.ui.transferActions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferActionsBtmBarVM @Inject constructor(
    private val foldersRepo: FoldersRepo,
    private val linksRepo: LinksRepo
) : ViewModel() {

    fun changeTheParentIdOfASpecificFolder(sourceFolderIds: List<Long>, targetParentId: Long?) {
        viewModelScope.launch {
            foldersRepo.changeTheParentIdOfASpecificFolder(sourceFolderIds, targetParentId)
        }
    }

    companion object {
        fun reset() {
            TransferActionsBtmBarValues.currentTransferActionType.value = TransferActionType.NOTHING
            TransferActionsBtmBarValues.sourceFolders.clear()
        }
    }
}