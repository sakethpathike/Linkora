package com.sakethh.linkora.ui.transferActions

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.links.LinkType
import com.sakethh.linkora.utils.linkoraLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

object TransferActionsBtmBarValues {
    val currentTransferActionType = mutableStateOf(TransferActionType.NOTHING)

    var sourceFolders = mutableStateListOf<FoldersTable>()

    var sourceLink = LinksTable(
        id = 0L,
        title = "",
        webURL = "",
        baseURL = "",
        imgURL = "",
        infoForSaving = "",
        isLinkedWithSavedLinks = false,
        isLinkedWithFolders = false,
        keyOfLinkedFolderV10 = null,
        keyOfLinkedFolder = null,
        isLinkedWithImpFolder = false,
        keyOfImpLinkedFolder = "",
        keyOfImpLinkedFolderV10 = null,
        isLinkedWithArchivedFolder = false,
        keyOfArchiveLinkedFolderV10 = null,
        keyOfArchiveLinkedFolder = null
    )

    var sourceLinkType = LinkType.SAVED_LINK

    init {
        CoroutineScope(Dispatchers.Main).launch {
            snapshotFlow {
                sourceFolders.toList()
            }.collectLatest {
                if (it.toList().isEmpty()) {
                    reset()
                }
                linkoraLog(it.toList().map { it.folderName }.toString())
            }
        }
    }

    fun reset() {
        currentTransferActionType.value = TransferActionType.NOTHING
        sourceFolders.clear()
    }
}