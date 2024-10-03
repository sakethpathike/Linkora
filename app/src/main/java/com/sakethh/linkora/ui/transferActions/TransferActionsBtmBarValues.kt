package com.sakethh.linkora.ui.transferActions

import androidx.compose.runtime.mutableStateOf
import com.sakethh.linkora.data.local.links.LinkType

object TransferActionsBtmBarValues {
    val currentTransferActionType = mutableStateOf(TransferActionType.NOTHING)

    var sourceFolderId: Long = -1
    val sourceFolderName = mutableStateOf("")

    var sourceLinkType = LinkType.SAVED_LINK
}