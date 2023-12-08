package com.sakethh.linkora.btmSheet

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.FolderDelete
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.TextSnippet
import androidx.compose.material.icons.outlined.Unarchive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.localDB.commonVMs.UpdateVM
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.screens.collections.FolderIndividualComponent
import kotlinx.coroutines.launch

data class OptionsBtmSheetUIParam @OptIn(ExperimentalMaterial3Api::class) constructor(
    val btmModalSheetState: SheetState,
    val shouldBtmModalSheetBeVisible: MutableState<Boolean>,
    val btmSheetFor: OptionsBtmSheetType,
    val onDeleteCardClick: () -> Unit,
    val onNoteDeleteCardClick: () -> Unit,
    val onRenameClick: () -> Unit,
    val onArchiveClick: () -> Unit,
    val onUnarchiveClick: () -> Unit = {},
    val onImportantLinkAdditionInTheTable: (() -> Unit?)? = null,
    val importantLinks: ImportantLinks?,
    val inArchiveScreen: MutableState<Boolean> = mutableStateOf(false),
    val inSpecificArchiveScreen: MutableState<Boolean> = mutableStateOf(false),
    val noteForSaving: String,
    val folderName: String,
    val linkTitle: String,
    val forAChildFolder: MutableState<Boolean> = mutableStateOf(false)
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OptionsBtmSheetUI(
    optionsBtmSheetUIParam: OptionsBtmSheetUIParam
) {
    val context = LocalContext.current
    val mutableStateNote =
        rememberSaveable(inputs = arrayOf(optionsBtmSheetUIParam.noteForSaving)) {
            mutableStateOf(optionsBtmSheetUIParam.noteForSaving)
        }
    val isNoteBtnSelected = rememberSaveable {
        mutableStateOf(false)
    }
    val coroutineScope = rememberCoroutineScope()
    val optionsBtmSheetVM: OptionsBtmSheetVM = viewModel()
    val updateDBVM: UpdateVM = viewModel()
    val localClipBoardManager = LocalClipboardManager.current
    if (optionsBtmSheetUIParam.shouldBtmModalSheetBeVisible.value) {
        ModalBottomSheet(
            sheetState = optionsBtmSheetUIParam.btmModalSheetState,
            onDismissRequest = {
                coroutineScope.launch {
                    if (optionsBtmSheetUIParam.btmModalSheetState.isVisible) {
                        optionsBtmSheetUIParam.btmModalSheetState.hide()
                    }
                }.invokeOnCompletion {
                    optionsBtmSheetUIParam.shouldBtmModalSheetBeVisible.value = false
                    isNoteBtnSelected.value = false
                }
            }) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                FolderIndividualComponent(
                    folderName = if (optionsBtmSheetUIParam.btmSheetFor == OptionsBtmSheetType.FOLDER) optionsBtmSheetUIParam.folderName else optionsBtmSheetUIParam.linkTitle,
                    folderNote = "",
                    onMoreIconClick = {
                        localClipBoardManager.setText(AnnotatedString(if (optionsBtmSheetUIParam.btmSheetFor == OptionsBtmSheetType.FOLDER) optionsBtmSheetUIParam.folderName else optionsBtmSheetUIParam.linkTitle))
                        Toast.makeText(context, "Title copied to clipboard", Toast.LENGTH_SHORT)
                            .show()
                    },
                    onFolderClick = { },
                    maxLines = 8,
                    showMoreIcon = false,
                    folderIcon = if (optionsBtmSheetUIParam.btmSheetFor == OptionsBtmSheetType.FOLDER) Icons.Outlined.Folder else Icons.Outlined.Link
                )
                Spacer(modifier = Modifier.height(5.dp))
                if (!isNoteBtnSelected.value) {
                    OptionsBtmSheetIndividualComponent(
                        onClick = {
                            coroutineScope.launch {
                                if (optionsBtmSheetUIParam.btmModalSheetState.isVisible) {
                                    optionsBtmSheetUIParam.btmModalSheetState.hide()
                                }
                            }.invokeOnCompletion {
                                isNoteBtnSelected.value = true
                                coroutineScope.launch {
                                    optionsBtmSheetUIParam.btmModalSheetState.show()
                                }
                            }
                        },
                        elementName = "View Note",
                        elementImageVector = Icons.Outlined.TextSnippet
                    )
                    OptionsBtmSheetIndividualComponent(
                        onClick = {
                            coroutineScope.launch {
                                if (optionsBtmSheetUIParam.btmModalSheetState.isVisible) {
                                    optionsBtmSheetUIParam.btmModalSheetState.hide()
                                }
                            }.invokeOnCompletion {
                                optionsBtmSheetUIParam.shouldBtmModalSheetBeVisible.value = false
                            }
                            optionsBtmSheetUIParam.onRenameClick()
                        },
                        elementName = "Rename",
                        elementImageVector = Icons.Outlined.DriveFileRenameOutline
                    )

                    if ((optionsBtmSheetUIParam.btmSheetFor == OptionsBtmSheetType.LINK || optionsBtmSheetUIParam.btmSheetFor == OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN) && !optionsBtmSheetUIParam.inArchiveScreen.value) {
                        OptionsBtmSheetIndividualComponent(
                            onClick = {
                                if (optionsBtmSheetUIParam.importantLinks != null && optionsBtmSheetUIParam.onImportantLinkAdditionInTheTable == null) {
                                    updateDBVM.importantLinkTableUpdater(
                                        importantLinks = optionsBtmSheetUIParam.importantLinks,
                                        context = context, onTaskCompleted = {
                                            optionsBtmSheetUIParam.onImportantLinkAdditionInTheTable?.invoke()
                                        }
                                    )
                                } else {
                                    if (optionsBtmSheetUIParam.onImportantLinkAdditionInTheTable != null) {
                                        optionsBtmSheetUIParam.onImportantLinkAdditionInTheTable.invoke()
                                    }
                                }
                                coroutineScope.launch {
                                    if (optionsBtmSheetUIParam.btmModalSheetState.isVisible) {
                                        optionsBtmSheetUIParam.btmModalSheetState.hide()
                                    }
                                    optionsBtmSheetUIParam.shouldBtmModalSheetBeVisible.value =
                                        false
                                }
                            },
                            elementName = optionsBtmSheetVM.importantCardText.value,
                            elementImageVector = optionsBtmSheetVM.importantCardIcon.value
                        )
                    }
                    if (!optionsBtmSheetUIParam.inSpecificArchiveScreen.value && optionsBtmSheetVM.archiveCardIcon.value != Icons.Outlined.Unarchive && !optionsBtmSheetUIParam.inArchiveScreen.value && !optionsBtmSheetUIParam.forAChildFolder.value) {
                        OptionsBtmSheetIndividualComponent(
                            onClick = {
                                coroutineScope.launch {
                                    if (optionsBtmSheetUIParam.btmModalSheetState.isVisible) {
                                        optionsBtmSheetUIParam.btmModalSheetState.hide()
                                    }
                                }.invokeOnCompletion {
                                    optionsBtmSheetUIParam.shouldBtmModalSheetBeVisible.value =
                                        false
                                }
                                optionsBtmSheetUIParam.onArchiveClick()
                            },
                            elementName = optionsBtmSheetVM.archiveCardText.value,
                            elementImageVector = optionsBtmSheetVM.archiveCardIcon.value
                        )
                    }
                    if (optionsBtmSheetUIParam.inArchiveScreen.value && !optionsBtmSheetUIParam.inSpecificArchiveScreen.value) {
                        OptionsBtmSheetIndividualComponent(
                            onClick = {
                                coroutineScope.launch {
                                    if (optionsBtmSheetUIParam.btmModalSheetState.isVisible) {
                                        optionsBtmSheetUIParam.btmModalSheetState.hide()
                                    }
                                }.invokeOnCompletion {
                                    optionsBtmSheetUIParam.shouldBtmModalSheetBeVisible.value =
                                        false
                                }
                                optionsBtmSheetUIParam.onUnarchiveClick()
                            },
                            elementName = "Unarchive",
                            elementImageVector = Icons.Outlined.Unarchive
                        )
                    }
                    if (mutableStateNote.value.isNotEmpty()) {
                        OptionsBtmSheetIndividualComponent(
                            onClick = {
                                coroutineScope.launch {
                                    if (optionsBtmSheetUIParam.btmModalSheetState.isVisible) {
                                        optionsBtmSheetUIParam.btmModalSheetState.hide()
                                    }
                                }.invokeOnCompletion {
                                    optionsBtmSheetUIParam.shouldBtmModalSheetBeVisible.value =
                                        false
                                }
                                optionsBtmSheetUIParam.onNoteDeleteCardClick()
                            },
                            elementName = "Delete the note",
                            elementImageVector = Icons.Outlined.Delete
                        )
                    }
                    if (optionsBtmSheetUIParam.inSpecificArchiveScreen.value || optionsBtmSheetUIParam.btmSheetFor != OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN) {
                        OptionsBtmSheetIndividualComponent(
                            onClick = {
                                coroutineScope.launch {
                                    if (optionsBtmSheetUIParam.btmModalSheetState.isVisible) {
                                        optionsBtmSheetUIParam.btmModalSheetState.hide()
                                    }
                                }.invokeOnCompletion {
                                    optionsBtmSheetUIParam.shouldBtmModalSheetBeVisible.value =
                                        false
                                }
                                optionsBtmSheetUIParam.onDeleteCardClick()
                            },
                            elementName = if (optionsBtmSheetUIParam.btmSheetFor == OptionsBtmSheetType.FOLDER) "Delete Folder" else "Delete Link",
                            elementImageVector = if (optionsBtmSheetUIParam.btmSheetFor == OptionsBtmSheetType.FOLDER) Icons.Outlined.FolderDelete else Icons.Outlined.DeleteForever
                        )
                    }
                } else {
                    if (mutableStateNote.value.isNotEmpty()) {
                        Text(
                            text = "Saved note :",
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
                                            "Note copied to clipboard",
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
                                text = "You didn't add a note for this.",
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Start,
                                lineHeight = 24.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}

@Composable
fun OptionsBtmSheetIndividualComponent(
    onClick: () -> Unit,
    elementName: String,
    elementImageVector: ImageVector,
) {
    val heightOfCard = remember {
        mutableStateOf(0.dp)
    }
    val localDensity = LocalDensity.current
    Row(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .padding(end = 10.dp)
            .wrapContentHeight()
            .fillMaxWidth()
            .onGloballyPositioned {
                heightOfCard.value = with(localDensity) {
                    it.size.height.toDp()
                }
            }) {
        IconButton(
            modifier = Modifier.padding(10.dp), onClick = { onClick() },
            colors = IconButtonDefaults.filledIconButtonColors()
        ) {
            Icon(imageVector = elementImageVector, contentDescription = null)
        }
        Box(
            modifier = Modifier.height(heightOfCard.value),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = elementName,
                style = MaterialTheme.typography.titleSmall,
                fontSize = 16.sp
            )
        }
    }
}