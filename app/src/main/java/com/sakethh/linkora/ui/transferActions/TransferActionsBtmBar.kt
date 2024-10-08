package com.sakethh.linkora.ui.transferActions

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
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sakethh.linkora.ui.navigation.NavigationRoutes
import com.sakethh.linkora.ui.screens.collections.CollectionsScreenVM
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenVM
import com.sakethh.linkora.utils.linkoraLog

@Composable
fun TransferActionsBtmBar(currentBackStackEntry: State<NavBackStackEntry?>) {
    val transferActionsBtmBarVM: TransferActionsBtmBarVM = hiltViewModel()
    val systemUiController = rememberSystemUiController()
    systemUiController.setNavigationBarColor(
        MaterialTheme.colorScheme.surfaceColorAtElevation(
            BottomAppBarDefaults.ContainerElevation
        )
    )
    BottomAppBar {
        IconButton(onClick = {
            TransferActionsBtmBarValues.reset()
        }) {
            Icon(Icons.Default.Cancel, null)
        }
        Column {
            Text(
                text = TransferActionsBtmBarValues.currentTransferActionType.value.name.substringBefore(
                    "_"
                ),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 10.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = buildAnnotatedString {
                    append(
                        "Folders selected : "
                    )
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(TransferActionsBtmBarValues.sourceFolders.size.toString())
                    }
                    append(
                        "\nLinks selected : "
                    )
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(TransferActionsBtmBarValues.sourceLinks.size.toString())
                    }
                },
                style = MaterialTheme.typography.titleSmall,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .horizontalScroll(rememberScrollState()),
            )
        }
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Row {
                IconButton(onClick = {
                    if (currentBackStackEntry.value?.destination?.route == NavigationRoutes.COLLECTIONS_SCREEN.name && TransferActionsBtmBarValues.sourceLinks.isEmpty()) {
                        // if in collections screen then we are supposed to mark selected folders as root folders
                        transferActionsBtmBarVM.transferFolders(
                            applyCopyImpl = TransferActionsBtmBarValues.currentTransferActionType.value == TransferActionType.COPYING_OF_FOLDERS,
                            sourceFolderIds = TransferActionsBtmBarValues.sourceFolders.toList()
                                .map { it.id },
                            targetParentId = null
                        )
                        return@IconButton
                    }
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

                    linkoraLog("before transferActionsBtmBarVM.transferLinks : " + TransferActionsBtmBarValues.sourceLinks.size.toString())

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