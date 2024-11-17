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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.ui.navigation.CollectionsScreenRoute
import com.sakethh.linkora.ui.screens.collections.CollectionsScreenVM
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenVM

@Composable
fun TransferActionsBtmBar(currentBackStackEntry: State<NavBackStackEntry?>) {
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
    val context = LocalContext.current
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
                            TransferActions.reset()
                        }) {
                            Icon(Icons.Default.Cancel, null)
                        }
                    }
                }
            }
            Column(Modifier.animateContentSize()) {
                    Text(
                        text = TransferActions.currentTransferActionType.value.name.substringBefore(
                            "_"
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 10.sp
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = buildAnnotatedString {

                            if (TransferActions.sourceFolders.isNotEmpty()) {
                                append(
                                    LocalizedStrings.foldersSelected_.value + " "
                                )
                                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                    append(TransferActions.totalSelectedFoldersCount.longValue.toString())
                                }
                            }
                            if (TransferActions.sourceLinks.isNotEmpty() && TransferActions.sourceFolders.isNotEmpty()) {
                                append("\n")
                            }
                            if (TransferActions.sourceLinks.isNotEmpty()) {
                                append(
                                    LocalizedStrings.linksSelected.value.trim() + " "
                                )
                                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                    append(TransferActions.totalSelectedLinksCount.longValue.toString())
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
                        if (currentBackStackEntry.value?.destination?.hasRoute(
                                CollectionsScreenRoute::class
                            ) == true && TransferActions.sourceLinks.isNotEmpty()
                        ) return
                        IconButton(onClick = {
                            TransferActions.isAnyActionGoingOn.value = true
                            isPasteButtonClicked.value = !isPasteButtonClicked.value

                            if (currentBackStackEntry.value?.destination?.hasRoute(
                                    CollectionsScreenRoute::class
                                ) == true && TransferActions.sourceLinks.isEmpty()
                            ) {
                                // if in collections screen then we are supposed to mark selected folders as root folders
                                TransferActions.transferFoldersJob =
                                    TransferActions.transferFolders(
                                    applyCopyImpl = TransferActions.currentTransferActionType.value == TransferActionType.COPYING_OF_FOLDERS,
                                    sourceFolderIds = TransferActions.sourceFolders.toList()
                                        .map { it.id },
                                    targetParentId = null,
                                    context
                                )
                                TransferActions.completeTransferAndReset(targetTransferFoldersOnly = true)
                                return@IconButton
                            }

                            val applyCopyImpl =
                                when (TransferActions.currentTransferActionType.value) {
                                    TransferActionType.MOVING_OF_FOLDERS, TransferActionType.MOVING_OF_LINKS -> false
                                    else -> true
                                }

                            TransferActions.transferFoldersJob = TransferActions.transferFolders(
                                applyCopyImpl = applyCopyImpl,
                                sourceFolderIds = TransferActions.sourceFolders.toList()
                                    .map { it.id },
                                targetParentId = CollectionsScreenVM.currentClickedFolderData.value.id,
                                context
                            )


                            TransferActions.transferLinksJob = TransferActions.transferLinks(
                                applyCopyImpl = applyCopyImpl,
                                TransferActions.sourceLinks.toList(),
                                SpecificCollectionsScreenVM.screenType.value,
                                context
                            )

                            TransferActions.completeTransferAndReset()

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
        }
    }
}