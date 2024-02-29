package com.sakethh.linkora.btmSheet

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HomeMax
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.customComposables.AddANewShelfDTO
import com.sakethh.linkora.customComposables.AddANewShelfDialogBox
import com.sakethh.linkora.customComposables.DeleteAShelfDialogBox
import com.sakethh.linkora.customComposables.DeleteAShelfDialogBoxDTO
import com.sakethh.linkora.customComposables.pulsateEffect
import com.sakethh.linkora.localDB.commonVMs.CreateVM
import com.sakethh.linkora.localDB.commonVMs.DeleteVM
import com.sakethh.linkora.localDB.commonVMs.ReadVM
import com.sakethh.linkora.localDB.dto.Shelf
import com.sakethh.linkora.screens.collections.CollectionsScreenVM


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfBtmSheet(isBtmSheetVisible: MutableState<Boolean>) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val lazyListState = rememberLazyListState()
    val shelfBtmSheetVM: ShelfBtmSheetVM = viewModel()
    val shelfData = shelfBtmSheetVM.shelfData.collectAsState().value
    val isAddANewShelfDialogBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val createVM: CreateVM = viewModel()
    val deleteVM: DeleteVM = viewModel()
    val isDeleteAShelfDialogBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val isTuneIconClicked = rememberSaveable {
        mutableStateOf(false)
    }
    val selectedShelfName = rememberSaveable {
        mutableStateOf("")
    }
    val collectionsScreenVM: CollectionsScreenVM = viewModel()
    val readVM: ReadVM = viewModel()
    val selectedShelfFolders = readVM.selectedShelfFolders.collectAsState().value
    val rootFolders = collectionsScreenVM.foldersData.collectAsState().value
    if (isBtmSheetVisible.value) {
        ModalBottomSheet(
            sheetState = modalBottomSheetState,
            onDismissRequest = { isBtmSheetVisible.value = false }) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                if (shelfData.isNotEmpty()) {
                    item {
                        Text(
                            text = if (isTuneIconClicked.value) "Currently shown in ${selectedShelfName.value}" else "Currently shown in Shelf",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 15.dp, end = 20.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (!isTuneIconClicked.value) {
                        items(shelfData) {
                            OptionsBtmSheetIndividualComponent(
                                onOptionClick = {
                                    selectedShelfName.value = it.shelfName
                                    isTuneIconClicked.value = true
                                },
                                elementName = it.shelfName,
                                elementImageVector = Icons.Default.HomeMax,
                                inShelfUI = true,
                                onDeleteIconClick = {
                                    ShelfBtmSheetVM.selectedShelfData = it
                                    isDeleteAShelfDialogBoxVisible.value = true
                                },
                                onTuneIconClick = {
                                    selectedShelfName.value = it.shelfName
                                    readVM.changeSelectedShelfFoldersData(it.id)
                                    isTuneIconClicked.value = true
                                }
                            )
                        }
                    } else {
                        /* items(rootFolders) { rootFolderElement ->
                             if (!selectedShelfFolders.contains(
                                     HomeScreenListTable(
                                         id = rootFolderElement.id,
                                         position = 0L,
                                         folderName = rootFolderElement.folderName,
                                         parentShelfID = 0L
                                     )
                                 )
                             ) {
                                 ListFolderUIComponent(
                                     folderName = rootFolderElement.folderName,
                                     {},
                                     {},
                                     {},
                                     onAddClick = {
                                         createVM.insertANewElementInHomeScreenList(
                                             HomeScreenListTable(
                                                 id = rootFolderElement.id,
                                                 folderName = rootFolderElement.folderName
                                             )
                                         )
                                     },
                                     shouldAddIconBeVisible = true
                                 )
                             }
                         }*/
                    }
                } else {
                    item {
                        Card(
                            border = BorderStroke(
                                1.dp,
                                contentColorFor(MaterialTheme.colorScheme.surface)
                            ),
                            colors = CardDefaults.cardColors(containerColor = AlertDialogDefaults.containerColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(
                                        top = 10.dp, bottom = 10.dp
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(
                                                start = 10.dp, end = 10.dp
                                            )
                                    )
                                }
                                Text(
                                    text = "Shelf is empty.",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 14.sp,
                                    lineHeight = 18.sp,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .padding(end = 10.dp)
                                )
                            }
                        }
                    }
                }

                if (shelfData.size == 5 && !isTuneIconClicked.value) {
                    item {
                        Card(
                            border = BorderStroke(
                                1.dp,
                                contentColorFor(MaterialTheme.colorScheme.surface)
                            ),
                            colors = CardDefaults.cardColors(containerColor = AlertDialogDefaults.containerColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp)
                                .navigationBarsPadding()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(
                                        top = 10.dp, bottom = 10.dp
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(
                                                start = 10.dp, end = 10.dp
                                            )
                                    )
                                }
                                Text(
                                    text = "Only a maximum of 5 shelf rows can be added to the shelf. To add another shelf, you need to remove one from the list above.",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 14.sp,
                                    lineHeight = 18.sp,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .padding(end = 10.dp)
                                )
                            }
                        }
                    }
                }
                if (shelfData.size < 5 && !isTuneIconClicked.value) {
                    item {
                        Button(modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp)
                            .navigationBarsPadding()
                            .fillMaxWidth()
                            .pulsateEffect(0.9f),
                            onClick = { isAddANewShelfDialogBoxVisible.value = true }) {
                            Text(
                                text = "Create a new Shelf row",
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
    AddANewShelfDialogBox(
        addANewShelfDTO = AddANewShelfDTO(
            isDialogBoxVisible = isAddANewShelfDialogBoxVisible,
            onCreateClick = { shelfName, shelfIconName ->
                createVM.addANewShelf(
                    shelf = Shelf(
                        shelfName = shelfName,
                        shelfIconName = shelfIconName,
                        folderIds = emptyList()
                    )
                )
            })
    )

    DeleteAShelfDialogBox(
        deleteAShelfDialogBoxDTO = DeleteAShelfDialogBoxDTO(
            isDialogBoxVisible = isDeleteAShelfDialogBoxVisible,
            onDeleteClick = { ->
                deleteVM.deleteAShelf(ShelfBtmSheetVM.selectedShelfData)
            },
        )
    )
}

@Composable
private fun ListFolderUIComponent(
    folderName: String,
    onMoveUpClick: () -> Unit,
    onMoveDownClick: () -> Unit,
    onRemoveClick: () -> Unit,
    onAddClick: () -> Unit,
    shouldAddIconBeVisible: Boolean,
    shouldMoveUpIconVisible: Boolean,
    shouldMoveDownIconVisible: Boolean,
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            if (shouldAddIconBeVisible) {
                onAddClick()
            }
        }) {
        Row(
            modifier = Modifier
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                modifier = Modifier
                    .padding(20.dp)
                    .size(28.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.40f),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = folderName,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 15.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row {
                    if (!shouldAddIconBeVisible) {
                        if (shouldMoveUpIconVisible) {
                            IconButton(onClick = {
                                onMoveUpClick()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null
                                )
                            }
                        }
                        if (shouldMoveDownIconVisible) {
                            IconButton(onClick = {
                                onMoveDownClick()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null
                                )
                            }
                        }
                        IconButton(onClick = {
                            onRemoveClick()
                        }) {
                            Icon(
                                imageVector = Icons.Default.RemoveCircle,
                                contentDescription = null
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            onAddClick()
                        }) {
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(start = 25.dp, end = 25.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(0.25f)
        )
    }
}