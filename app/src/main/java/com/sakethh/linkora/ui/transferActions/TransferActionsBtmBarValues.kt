package com.sakethh.linkora.ui.transferActions

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.LinksTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn

object TransferActionsBtmBarValues {
    val currentTransferActionType = mutableStateOf(TransferActionType.NOTHING)

    val sourceFolders = mutableStateListOf<FoldersTable>()

    val sourceLinks = mutableStateListOf<LinksTable>()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        combine(
            snapshotFlow { sourceLinks.toList() },
            snapshotFlow { sourceFolders.toList() }
        ) { linkData, folderData ->

            val areBothEmpty = folderData.isEmpty() && linkData.isEmpty()

            if (areBothEmpty) {
                reset()
            }

        }.launchIn(coroutineScope)
    }

    fun reset() {
        currentTransferActionType.value = TransferActionType.NOTHING
        sourceFolders.clear()
        sourceLinks.clear()
        TransferActionsBtmBarVM.currentFolderTransferProgressCount.longValue = 0
        TransferActionsBtmBarVM.currentLinkTransferProgressCount.longValue = 0
    }
}