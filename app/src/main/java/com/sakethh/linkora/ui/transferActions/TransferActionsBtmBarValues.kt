package com.sakethh.linkora.ui.transferActions

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.utils.linkoraLog
import kotlinx.coroutines.flow.combine

object TransferActionsBtmBarValues {
    val currentTransferActionType = mutableStateOf(TransferActionType.NOTHING)

    var sourceFolders = mutableStateListOf<FoldersTable>()

    var sourceLinks = mutableStateListOf<LinksTable>()

    init {
        combine(
            snapshotFlow {
                sourceLinks.toList()
            },
            snapshotFlow {
                sourceFolders.toList()
            }
        ) { linkData, folderData ->
            if (folderData.toList().isEmpty() && linkData.toList().isEmpty()) {
                    reset()
                }
            linkoraLog(
                "sourceLinks : " + linkData.toList()
                    .map { "${it.title}, S.L : ${it.isLinkedWithSavedLinks}, I.L : ${it.isLinkedWithImpFolder}, F.L : ${it.isLinkedWithFolders}" }
                    .toString() + "\nTotal sourceLinks : ${linkData.count()}" + "sourceFolders : " + folderData.toList()
                    .map { it.folderName } + "\nTotal sourceFolders : ${folderData.count()}")
            }
    }

    fun reset() {
        currentTransferActionType.value = TransferActionType.NOTHING
        sourceFolders.clear()
        sourceLinks.clear()
    }
}