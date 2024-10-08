package com.sakethh.linkora.ui.transferActions

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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

@Composable
fun TransferActionsBtmBar(currentBackStackEntry: State<NavBackStackEntry?>) {
    val transferActionsBtmBarVM: TransferActionsBtmBarVM = hiltViewModel()
    val systemUiController = rememberSystemUiController()
    val bottomAppbarContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
        BottomAppBarDefaults.ContainerElevation
    )
    systemUiController.setNavigationBarColor(
        bottomAppbarContainerColor
    )
    val isPasteButtonClicked = rememberSaveable {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bottomAppbarContainerColor)
            .wrapContentHeight()
            .animateContentSize()
            .padding(
                start = if (isPasteButtonClicked.value) 15.dp else 8.dp,
                top = 8.dp,
                end = 8.dp,
                bottom = 8.dp
            )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.animateContentSize()) {
                if (!isPasteButtonClicked.value) {
                    Row {
                        IconButton(onClick = {
                            TransferActionsBtmBarValues.reset()
                        }) {
                            Icon(Icons.Default.Cancel, null)
                        }
                    }
                }
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

                        if (TransferActionsBtmBarValues.sourceFolders.isNotEmpty()) {
                            append(
                                "Folders selected : "
                            )
                            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                append(TransferActionsBtmBarVM.totalSelectedLinksCount.longValue.toString())
                            }
                        }
                        if (TransferActionsBtmBarValues.sourceLinks.isNotEmpty() && TransferActionsBtmBarValues.sourceFolders.isNotEmpty()) {
                            append("\n")
                        }
                        if (TransferActionsBtmBarValues.sourceLinks.isNotEmpty()) {
                            append(
                                "Links selected : "
                            )
                            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                append(TransferActionsBtmBarVM.totalSelectedLinksCount.longValue.toString())
                            }
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
                Row(modifier = Modifier.animateContentSize()) {
                    if (!isPasteButtonClicked.value) {
                        if (currentBackStackEntry.value?.destination?.route == NavigationRoutes.COLLECTIONS_SCREEN.name && TransferActionsBtmBarValues.sourceLinks.isNotEmpty()) return
                        IconButton(onClick = {
                            TransferActionsBtmBarVM.totalSelectedFoldersCount.longValue =
                                TransferActionsBtmBarValues.sourceFolders.size.toLong()
                            TransferActionsBtmBarVM.totalSelectedLinksCount.longValue =
                                TransferActionsBtmBarValues.sourceLinks.size.toLong()
                            isPasteButtonClicked.value = !isPasteButtonClicked.value
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
                    }
                }
            }
        }
        if (isPasteButtonClicked.value) {
            Spacer(Modifier.height(5.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 15.dp)
            )
            Spacer(Modifier.height(5.dp))
            Text(text = buildAnnotatedString {

                if (TransferActionsBtmBarValues.sourceLinks.isNotEmpty()) {
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(TransferActionsBtmBarVM.currentLinkTransferProgressCount.longValue.toString())
                    }
                    append(" links moved.")
                }
                if (TransferActionsBtmBarValues.sourceLinks.isNotEmpty() && TransferActionsBtmBarValues.sourceFolders.isNotEmpty()) {
                    append("\n")
                }
                if (TransferActionsBtmBarValues.sourceFolders.isNotEmpty()) {
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(TransferActionsBtmBarVM.currentFolderTransferProgressCount.longValue.toString())
                    }
                    append(" folders moved.")
                }

            }, style = MaterialTheme.typography.titleSmall)
        }
    }
}