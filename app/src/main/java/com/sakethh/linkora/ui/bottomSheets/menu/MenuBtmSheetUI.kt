package com.sakethh.linkora.ui.bottomSheets.menu

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DriveFileMove
import androidx.compose.material.icons.automirrored.outlined.TextSnippet
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.FolderCopy
import androidx.compose.material.icons.outlined.FolderDelete
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Unarchive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.ui.CoilImage
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.OptionsBtmSheetType
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.OptionsBtmSheetVM
import com.sakethh.linkora.ui.screens.collections.FolderIndividualComponent
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.utils.fadedEdges
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MenuBtmSheetUI(
    menuBtmSheetParam: MenuBtmSheetParam
) {
    val context = LocalContext.current
    val mutableStateNote =
        rememberSaveable(inputs = arrayOf(menuBtmSheetParam.noteForSaving)) {
            mutableStateOf(menuBtmSheetParam.noteForSaving)
        }
    val isNoteBtnSelected = rememberSaveable {
        mutableStateOf(false)
    }
    val coroutineScope = rememberCoroutineScope()
    val optionsBtmSheetVM: OptionsBtmSheetVM = hiltViewModel()
    val localClipBoardManager = LocalClipboardManager.current
    val isImageAssociatedWithTheLinkIsExpanded = rememberSaveable {
        mutableStateOf(false)
    }
    if (menuBtmSheetParam.shouldBtmModalSheetBeVisible.value) {
        ModalBottomSheet(
            dragHandle = {},
            sheetState = menuBtmSheetParam.btmModalSheetState,
            onDismissRequest = {
                coroutineScope.launch {
                    if (menuBtmSheetParam.btmModalSheetState.isVisible) {
                        menuBtmSheetParam.btmModalSheetState.hide()
                    }
                }.invokeOnCompletion {
                    menuBtmSheetParam.shouldBtmModalSheetBeVisible.value = false
                    isNoteBtnSelected.value = false
                }
            }) {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
            ) {
                if (menuBtmSheetParam.imgLink.isNotEmpty() && SettingsPreference.showAssociatedImagesInLinkMenu.value && (menuBtmSheetParam.btmSheetFor == OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN || menuBtmSheetParam.btmSheetFor == OptionsBtmSheetType.LINK)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 15.dp)
                            .wrapContentHeight()
                    ) {
                        CoilImage(
                            modifier = Modifier
                                .animateContentSize()
                                .fillMaxWidth()
                                .then(
                                    if (isImageAssociatedWithTheLinkIsExpanded.value) Modifier.wrapContentHeight() else Modifier.heightIn(
                                        max = 150.dp
                                    )
                                )
                                .fadedEdges(MaterialTheme.colorScheme),
                            imgURL = menuBtmSheetParam.imgLink
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                                .padding(end = 15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledTonalIconButton(
                                onClick = {
                                    isImageAssociatedWithTheLinkIsExpanded.value =
                                        !isImageAssociatedWithTheLinkIsExpanded.value
                                }, modifier = Modifier
                                    .alpha(0.75f)
                                    .padding(5.dp)
                            ) {
                                Icon(
                                    imageVector = if (!isImageAssociatedWithTheLinkIsExpanded.value) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = ""
                                )
                            }
                            Text(
                                text = menuBtmSheetParam.linkTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 16.sp,
                                maxLines = 2,
                                lineHeight = 20.sp,
                                textAlign = TextAlign.Start,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 25.dp, end = 25.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(0.25f)
                    )
                } else {
                    FolderIndividualComponent(
                        folderName = if (menuBtmSheetParam.btmSheetFor == OptionsBtmSheetType.FOLDER) menuBtmSheetParam.folderName else menuBtmSheetParam.linkTitle,
                        folderNote = "",
                        onMoreIconClick = {
                            localClipBoardManager.setText(AnnotatedString(if (menuBtmSheetParam.btmSheetFor == OptionsBtmSheetType.FOLDER) menuBtmSheetParam.folderName else menuBtmSheetParam.linkTitle))
                            Toast.makeText(
                                context,
                                LocalizedStrings.titleCopiedToClipboard.value,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        },
                        onFolderClick = { },
                        maxLines = 2,
                        showMoreIcon = false,
                        folderIcon = if (menuBtmSheetParam.btmSheetFor == OptionsBtmSheetType.FOLDER) Icons.Outlined.Folder else Icons.Outlined.Link
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }
                if (!isNoteBtnSelected.value) {
                    IndividualMenuComponent(
                        onOptionClick = {
                            coroutineScope.launch {
                                if (menuBtmSheetParam.btmModalSheetState.isVisible) {
                                    menuBtmSheetParam.btmModalSheetState.hide()
                                }
                            }.invokeOnCompletion {
                                isNoteBtnSelected.value = true
                                coroutineScope.launch {
                                    menuBtmSheetParam.btmModalSheetState.show()
                                }
                            }
                        },
                        elementName = LocalizedStrings.viewNote.value,
                        elementImageVector = Icons.AutoMirrored.Outlined.TextSnippet
                    )
                    IndividualMenuComponent(
                        onOptionClick = {
                            coroutineScope.launch {
                                if (menuBtmSheetParam.btmModalSheetState.isVisible) {
                                    menuBtmSheetParam.btmModalSheetState.hide()
                                }
                            }.invokeOnCompletion {
                                menuBtmSheetParam.shouldBtmModalSheetBeVisible.value = false
                            }
                            menuBtmSheetParam.onRenameClick()
                        },
                        elementName = LocalizedStrings.rename.value,
                        elementImageVector = Icons.Outlined.DriveFileRenameOutline
                    )
                    if (menuBtmSheetParam.btmSheetFor == OptionsBtmSheetType.LINK || menuBtmSheetParam.btmSheetFor == OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN) {
                        IndividualMenuComponent(
                            onOptionClick = {
                                Toast.makeText(
                                    context,
                                    LocalizedStrings.refreshingTitleAndImage.value,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                menuBtmSheetParam.onRefreshClick()
                                coroutineScope.launch {
                                    if (menuBtmSheetParam.btmModalSheetState.isVisible) {
                                        menuBtmSheetParam.btmModalSheetState.hide()
                                    }
                                }.invokeOnCompletion {
                                    menuBtmSheetParam.shouldBtmModalSheetBeVisible.value = false
                                }
                            },
                            elementName = LocalizedStrings.refreshImageAndTitle.value,
                            elementImageVector = Icons.Outlined.Refresh
                        )
                    }

                    if (menuBtmSheetParam.shouldImportantLinkOptionBeVisible.value && (menuBtmSheetParam.btmSheetFor == OptionsBtmSheetType.LINK || menuBtmSheetParam.btmSheetFor == OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN) && !menuBtmSheetParam.inArchiveScreen.value) {
                        IndividualMenuComponent(
                            onOptionClick = {
                                menuBtmSheetParam.onImportantLinkClick?.let { it() }
                                coroutineScope.launch {
                                    if (menuBtmSheetParam.btmModalSheetState.isVisible) {
                                        menuBtmSheetParam.btmModalSheetState.hide()
                                    }
                                    menuBtmSheetParam.shouldBtmModalSheetBeVisible.value =
                                        false
                                }
                            },
                            elementName = optionsBtmSheetVM.importantCardText.value,
                            elementImageVector = optionsBtmSheetVM.importantCardIcon.value
                        )
                    }
                    if (!menuBtmSheetParam.inSpecificArchiveScreen.value && optionsBtmSheetVM.archiveCardIcon.value != Icons.Outlined.Unarchive && !menuBtmSheetParam.inArchiveScreen.value) {
                        IndividualMenuComponent(
                            onOptionClick = {
                                menuBtmSheetParam.onArchiveClick()
                                coroutineScope.launch {
                                    if (menuBtmSheetParam.btmModalSheetState.isVisible) {
                                        menuBtmSheetParam.btmModalSheetState.hide()
                                    }
                                }.invokeOnCompletion {
                                    menuBtmSheetParam.shouldBtmModalSheetBeVisible.value =
                                        false
                                }
                            },
                            elementName = optionsBtmSheetVM.archiveCardText.value,
                            elementImageVector = optionsBtmSheetVM.archiveCardIcon.value
                        )
                    }
                    if (menuBtmSheetParam.inArchiveScreen.value && !menuBtmSheetParam.inSpecificArchiveScreen.value) {
                        IndividualMenuComponent(
                            onOptionClick = {
                                menuBtmSheetParam.onUnarchiveClick()
                                coroutineScope.launch {
                                    if (menuBtmSheetParam.btmModalSheetState.isVisible) {
                                        menuBtmSheetParam.btmModalSheetState.hide()
                                    }
                                }.invokeOnCompletion {
                                    menuBtmSheetParam.shouldBtmModalSheetBeVisible.value =
                                        false
                                }
                            },
                            elementName = LocalizedStrings.unarchive.value,
                            elementImageVector = Icons.Outlined.Unarchive
                        )
                    }
                    if (mutableStateNote.value.isNotEmpty()) {
                        IndividualMenuComponent(
                            onOptionClick = {
                                menuBtmSheetParam.onNoteDeleteCardClick()
                                coroutineScope.launch {
                                    if (menuBtmSheetParam.btmModalSheetState.isVisible) {
                                        menuBtmSheetParam.btmModalSheetState.hide()
                                    }
                                }.invokeOnCompletion {
                                    menuBtmSheetParam.shouldBtmModalSheetBeVisible.value =
                                        false
                                }
                            },
                            elementName = LocalizedStrings.deleteTheNote.value,
                            elementImageVector = Icons.Outlined.Delete
                        )
                    }
                    if (menuBtmSheetParam.inSpecificArchiveScreen.value || menuBtmSheetParam.btmSheetFor != OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN) {
                        IndividualMenuComponent(
                            onOptionClick = {

                            },
                            elementName = if (menuBtmSheetParam.btmSheetFor == OptionsBtmSheetType.FOLDER) "Copy Folder" else LocalizedStrings.deleteLink.value,
                            elementImageVector = if (menuBtmSheetParam.btmSheetFor == OptionsBtmSheetType.FOLDER) Icons.Outlined.FolderCopy else Icons.Outlined.DeleteForever
                        )
                    }
                    if (menuBtmSheetParam.inSpecificArchiveScreen.value || menuBtmSheetParam.btmSheetFor != OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN) {
                        IndividualMenuComponent(
                            onOptionClick = {

                            },
                            elementName = if (menuBtmSheetParam.btmSheetFor == OptionsBtmSheetType.FOLDER) "Move To Other Folder" else LocalizedStrings.deleteLink.value,
                            elementImageVector = if (menuBtmSheetParam.btmSheetFor == OptionsBtmSheetType.FOLDER) Icons.AutoMirrored.Outlined.DriveFileMove else Icons.Outlined.DeleteForever
                        )
                    }
                    if (menuBtmSheetParam.inSpecificArchiveScreen.value || menuBtmSheetParam.btmSheetFor != OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN) {
                        IndividualMenuComponent(
                            onOptionClick = {
                                menuBtmSheetParam.onDeleteCardClick()
                                coroutineScope.launch {
                                    if (menuBtmSheetParam.btmModalSheetState.isVisible) {
                                        menuBtmSheetParam.btmModalSheetState.hide()
                                    }
                                }.invokeOnCompletion {
                                    menuBtmSheetParam.shouldBtmModalSheetBeVisible.value =
                                        false
                                }
                            },
                            elementName = if (menuBtmSheetParam.btmSheetFor == OptionsBtmSheetType.FOLDER) LocalizedStrings.deleteFolder.value else LocalizedStrings.deleteLink.value,
                            elementImageVector = if (menuBtmSheetParam.btmSheetFor == OptionsBtmSheetType.FOLDER) Icons.Outlined.FolderDelete else Icons.Outlined.DeleteForever
                        )
                    }
                } else {
                    if (mutableStateNote.value.isNotEmpty()) {
                        Text(
                            text = LocalizedStrings.savedNote.value,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(20.dp)
                        )
                        Text(
                            text = mutableStateNote.value,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(onClick = {}, onLongClick = {
                                    localClipBoardManager.setText(AnnotatedString(mutableStateNote.value))
                                    Toast
                                        .makeText(
                                            context,
                                            LocalizedStrings.noteCopiedToClipboard.value,
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                })
                                .padding(
                                    start = 20.dp, end = 25.dp
                                ),
                            textAlign = TextAlign.Start,
                            lineHeight = 24.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    } else {
                        Spacer(modifier = Modifier.height(20.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = LocalizedStrings.youDidNotAddNoteForThis.value,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Start,
                                lineHeight = 24.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
                if (menuBtmSheetParam.showQuickActions.value) {
                    QuickActions(
                        onForceOpenInExternalBrowserClicked = menuBtmSheetParam.onForceOpenInExternalBrowserClicked,
                        webUrl = menuBtmSheetParam.webUrl
                    )
                }
            }
        }
        BackHandler {
            coroutineScope.launch {
                menuBtmSheetParam.btmModalSheetState.hide()
            }
        }
    }
}

@Composable
private fun QuickActions(onForceOpenInExternalBrowserClicked: () -> Unit, webUrl: String) {
    val localURIHandler = LocalUriHandler.current
    val localClipBoardManager = LocalClipboardManager.current
    val context = LocalContext.current
    Column {
        HorizontalDivider(Modifier.padding(start = 15.dp, end = 15.dp, bottom = 15.dp, top = 5.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilledTonalIconButton(onClick = {
                onForceOpenInExternalBrowserClicked()
                try {
                    localURIHandler.openUri(webUrl)
                } catch (_: android.content.ActivityNotFoundException) {
                    Toast.makeText(
                        context,
                        LocalizedStrings.noActivityFoundToHandleIntent.value,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
                Icon(
                    imageVector = Icons.Outlined.OpenInBrowser,
                    contentDescription = null
                )
            }
            FilledTonalIconButton(onClick = {
                localClipBoardManager.setText(
                    AnnotatedString(webUrl)
                )
                Toast.makeText(
                    context,
                    LocalizedStrings.linkCopiedToTheClipboard.value,
                    Toast.LENGTH_SHORT
                ).show()
            }) {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = null
                )
            }
            FilledTonalIconButton(onClick = {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, webUrl)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(intent, null)
                context.startActivity(shareIntent)
            }) {
                Icon(imageVector = Icons.Outlined.Share, contentDescription = null)
            }
        }
        Spacer(Modifier.height(15.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IndividualMenuComponent(
    onOptionClick: () -> Unit,
    elementName: String,
    elementImageVector: ImageVector,
    elementImageRes: Int = 0,
    inShelfUI: Boolean = false,
    onDeleteIconClick: () -> Unit = {},
    onTuneIconClick: () -> Unit = {},
    onRenameIconClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .combinedClickable(interactionSource = remember {
                MutableInteractionSource()
            }, indication = null,
                onClick = {
                    onOptionClick()
                },
                onLongClick = {

                })
            .pulsateEffect()
            .padding(end = 10.dp)
            .wrapContentHeight()
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                modifier = Modifier.padding(10.dp), onClick = { onOptionClick() },
                colors = IconButtonDefaults.filledTonalIconButtonColors()
            ) {
                if (elementImageRes != 0) {
                    Icon(
                        painter = painterResource(id = elementImageRes),
                        modifier = Modifier.size(24.dp),
                        contentDescription = null
                    )
                } else {
                    Icon(imageVector = elementImageVector, contentDescription = null)
                }
            }
            Text(
                text = elementName,
                style = MaterialTheme.typography.titleSmall,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(if (inShelfUI) 0.4f else 1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (inShelfUI) {
            Row {
                IconButton(onClick = { onRenameIconClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.DriveFileRenameOutline,
                        contentDescription = null
                    )
                }
                IconButton(onClick = { onTuneIconClick() }
                ) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = null)
                }
                IconButton(onClick = { onDeleteIconClick() }
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                }
            }
        }
    }
}