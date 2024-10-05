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

    var sourceLink = mutableStateListOf<LinksTable>()

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