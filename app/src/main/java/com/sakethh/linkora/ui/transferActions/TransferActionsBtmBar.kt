package com.sakethh.linkora.ui.transferActions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sakethh.linkora.ui.screens.collections.CollectionsScreenVM
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenVM

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransferActionsBtmBar() {
    val transferActionsBtmBarVM: TransferActionsBtmBarVM = hiltViewModel()
    BottomAppBar {
        IconButton(onClick = {
            TransferActionsBtmBarValues.reset()
        }) {
            Icon(Icons.Default.Cancel, null)
        }
        Column {
            Text(
                text = TransferActionsBtmBarValues.currentTransferActionType.value.name.replace(
                    "_",
                    " "
                )
                    .split(" ").map { it[0] + it.substring(1).lowercase() }.joinToString { it }
                    .replace(",", ""),
                style = MaterialTheme.typography.titleSmall,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Folders selcted : ${TransferActionsBtmBarValues.sourceFolders.size}, Links selected",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .horizontalScroll(rememberScrollState()),
                maxLines = 1
            )
        }
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Row {
                IconButton(onClick = {

                    val applyCopyImpl =
                        when (TransferActionsBtmBarValues.currentTransferActionType.value) {
                            TransferActionType.MOVING_OF_FOLDERS, TransferActionType.MOVING_OF_LINKS -> false
                            else -> true
                        }

                    transferActionsBtmBarVM.transferFolders(
                        applyCopyImpl = applyCopyImpl,
                        sourceFolderIds = TransferActionsBtmBarValues.sourceFolders.toList()
                            .map { it.id },
                        targetParentId = CollectionsScreenVM.currentClickedFolderData.value.id
                    )

                    transferActionsBtmBarVM.transferLinks(
                        applyCopyImpl = applyCopyImpl,
                        TransferActionsBtmBarValues.sourceLinks.toList(),
                        SpecificCollectionsScreenVM.screenType.value,
                    )

                }) {
                    Icon(Icons.Default.ContentPaste, null)
                }
                Spacer(Modifier.width(10.dp))
            }
        }
    }
}