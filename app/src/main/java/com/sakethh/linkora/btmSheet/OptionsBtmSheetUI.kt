package com.sakethh.linkora.btmSheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.FolderDelete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.localDB.ImportantLinks
import com.sakethh.linkora.localDB.LocalDBFunctions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsBtmSheetUI(
    btmModalSheetState: SheetState,
    shouldBtmModalSheetBeVisible: MutableState<Boolean>,
    coroutineScope: CoroutineScope,
    btmSheetFor: OptionsBtmSheetType,
    onDeleteCardClick: () -> Unit,
    onRenameClick: () -> Unit,
    importantLinks: ImportantLinks?,
) {
    val heightOfCard = remember {
        mutableStateOf(0.dp)
    }
    val localDensity = LocalDensity.current
    val optionsBtmSheetVM: OptionsBtmSheetVM = viewModel()
    if (shouldBtmModalSheetBeVisible.value) {
        ModalBottomSheet(sheetState = btmModalSheetState, onDismissRequest = {
            coroutineScope.launch {
                if (btmModalSheetState.isVisible) {
                    btmModalSheetState.hide()
                }
            }.invokeOnCompletion {
                shouldBtmModalSheetBeVisible.value = false
            }
        }) {
            if (btmSheetFor != OptionsBtmSheetType.LINK) {
                Card(
                    shape = RoundedCornerShape(10.dp), modifier = Modifier
                        .padding(top = 20.dp, end = 20.dp, start = 20.dp)
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .clickable {
                            coroutineScope
                                .launch {
                                    if (btmModalSheetState.isVisible) {
                                        btmModalSheetState.hide()
                                    }
                                }
                                .invokeOnCompletion {
                                    shouldBtmModalSheetBeVisible.value = false
                                }
                            onRenameClick()
                        }
                ) {
                    Row {
                        Icon(
                            modifier = Modifier.padding(20.dp),
                            imageVector = Icons.Outlined.DriveFileRenameOutline,
                            contentDescription = null
                        )
                        Box(
                            modifier = Modifier.height(heightOfCard.value),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = "Rename",
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
            if (btmSheetFor == OptionsBtmSheetType.LINK) {
                Card(
                    shape = RoundedCornerShape(10.dp), modifier = Modifier
                        .padding(top = 20.dp, end = 20.dp, start = 20.dp)
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .clickable {
                            coroutineScope
                                .launch {
                                    if (btmModalSheetState.isVisible) {
                                        btmModalSheetState.hide()
                                    }
                                    importantLinks?.link?.let {
                                        LocalDBFunctions.importantLinksFunctions(
                                            url = it,
                                            importantLinks = importantLinks
                                        )
                                    }
                                }
                                .invokeOnCompletion {
                                    shouldBtmModalSheetBeVisible.value = false
                                }
                            onRenameClick()
                        }
                ) {
                    Row {
                        Icon(
                            modifier = Modifier.padding(20.dp),
                            imageVector = optionsBtmSheetVM.importantCardIcon.value,
                            contentDescription = null
                        )
                        Box(
                            modifier = Modifier.height(heightOfCard.value),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = optionsBtmSheetVM.importantCardText.value,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
            Card(
                shape = RoundedCornerShape(10.dp), modifier = Modifier
                    .padding(top = 20.dp, end = 20.dp, start = 20.dp)
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        heightOfCard.value = with(localDensity) {
                            it.size.height.toDp()
                        }
                    }
                    .clickable {
                        coroutineScope
                            .launch {
                                if (btmModalSheetState.isVisible) {
                                    btmModalSheetState.hide()
                                }
                            }
                            .invokeOnCompletion {
                                shouldBtmModalSheetBeVisible.value = false
                            }
                    }
            ) {
                Row {
                    Icon(
                        modifier = Modifier.padding(20.dp),
                        imageVector = Icons.Outlined.Archive,
                        contentDescription = null
                    )
                    Box(
                        modifier = Modifier.height(heightOfCard.value),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = if (btmSheetFor == OptionsBtmSheetType.FOLDER) "Archive Folder" else "Archive Link",
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                    }
                }
            }
            Card(
                shape = RoundedCornerShape(10.dp), modifier = Modifier
                    .padding(top = 20.dp, end = 20.dp, start = 20.dp)
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        heightOfCard.value = with(localDensity) {
                            it.size.height.toDp()
                        }
                    }
                    .clickable {
                        coroutineScope
                            .launch {
                                if (btmModalSheetState.isVisible) {
                                    btmModalSheetState.hide()
                                }
                            }
                            .invokeOnCompletion {
                                shouldBtmModalSheetBeVisible.value = false
                            }
                        onDeleteCardClick()
                    }
            ) {
                Row {
                    Icon(
                        modifier = Modifier.padding(20.dp),
                        imageVector = if (btmSheetFor == OptionsBtmSheetType.FOLDER) Icons.Outlined.FolderDelete else Icons.Outlined.DeleteForever,
                        contentDescription = null
                    )
                    Box(
                        modifier = Modifier.height(heightOfCard.value),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = if (btmSheetFor == OptionsBtmSheetType.FOLDER) "Delete Folder" else "Delete Link",
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}