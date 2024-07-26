package com.sakethh.linkora.ui.bottomSheets.shelf

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.data.local.Shelf
import com.sakethh.linkora.ui.bottomSheets.menu.IndividualMenuComponent
import com.sakethh.linkora.ui.commonComposables.AddANewShelfDialogBox
import com.sakethh.linkora.ui.commonComposables.AddANewShelfParam
import com.sakethh.linkora.ui.commonComposables.DeleteAShelfDialogBox
import com.sakethh.linkora.ui.commonComposables.DeleteAShelfDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.RenameAShelfDialogBox
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.ShelfBtmSheetVM
import com.sakethh.linkora.ui.screens.collections.CollectionsScreenVM


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfBtmSheet(isBtmSheetVisible: MutableState<Boolean>) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val lazyListState = rememberLazyListState()
    val shelfBtmSheetVM: ShelfBtmSheetVM = viewModel()
    val shelfData = shelfBtmSheetVM.shelfData.collectAsStateWithLifecycle().value
    val isAddANewShelfDialogBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val isDeleteAShelfDialogBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val isTuneIconClicked = rememberSaveable {
        mutableStateOf(false)
    }
    val selectedShelfName = rememberSaveable(ShelfBtmSheetVM.selectedShelfData.shelfName) {
        mutableStateOf(ShelfBtmSheetVM.selectedShelfData.shelfName)
    }
    val collectionsScreenVM: CollectionsScreenVM = viewModel()
    val selectedShelfFolders =
        shelfBtmSheetVM.selectedShelfFoldersForSelectedShelf.collectAsStateWithLifecycle().value
    val rootFolders = collectionsScreenVM.foldersData.collectAsStateWithLifecycle().value
    val isRenameAShelfDialogBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }
    if (isBtmSheetVisible.value) {
        ModalBottomSheet(
            sheetState = modalBottomSheetState,
            onDismissRequest = {
                isBtmSheetVisible.value = false; isTuneIconClicked.value = false
            }) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                if (shelfData.isNotEmpty()) {
                    if (!isTuneIconClicked.value) {
                        item {
                            Text(
                                text = if (isTuneIconClicked.value) "Currently shown in ${selectedShelfName.value}" else "Currently shown in Shelf",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 15.dp, end = 20.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        if (isTuneIconClicked.value && selectedShelfFolders.isEmpty()) {
                            item {
                                InfoUI(infoText = buildAnnotatedString {
                                    append("No folders are added in the shelf ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(selectedShelfName.value)
                                    }
                                    append(".")
                                })
                            }
                        }
                    }
                    if (!isTuneIconClicked.value) {
                        items(shelfData) {
                            IndividualMenuComponent(
                                onRenameIconClick = {
                                    ShelfBtmSheetVM.selectedShelfData = it
                                    isRenameAShelfDialogBoxVisible.value = true
                                },
                                onOptionClick = {
                                    shelfBtmSheetVM.changeSelectedShelfFoldersDataForSelectedShelf(
                                        it.id
                                    )
                                    ShelfBtmSheetVM.selectedShelfData = it
                                    selectedShelfName.value = it.shelfName
                                    isTuneIconClicked.value = true
                                },
                                elementName = it.shelfName,
                                elementImageVector = Icons.Default.Folder,
                                inShelfUI = true,
                                onDeleteIconClick = {
                                    ShelfBtmSheetVM.selectedShelfData = it
                                    isDeleteAShelfDialogBoxVisible.value = true
                                },
                                onTuneIconClick = {
                                    selectedShelfName.value = it.shelfName
                                    ShelfBtmSheetVM.selectedShelfData = it
                                    shelfBtmSheetVM.changeSelectedShelfFoldersDataForSelectedShelf(
                                        it.id
                                    )
                                    isTuneIconClicked.value = true
                                }
                            )
                        }
                    } else {
                        if (selectedShelfFolders.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Folders in \"${selectedShelfName.value}\" Shelf",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(start = 15.dp, end = 20.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                        items(selectedShelfFolders) { selectedShelfFolder ->
                            ListFolderUIComponent(
                                folderName = selectedShelfFolder.folderName,
                                {},
                                {},
                                onRemoveClick = {
                                    shelfBtmSheetVM.onShelfUiEvent(
                                        ShelfUIEvent.DeleteAShelfFolder(
                                            selectedShelfFolder.id
                                        )
                                    )
                                },
                                onAddClick = { },
                                shouldAddIconBeVisible = false,
                                shouldMoveUpIconVisible = false,
                                shouldMoveDownIconVisible = false
                            )
                        }
                    }
                } else {
                    item {
                        InfoUI(infoText = "Shelf is empty.")
                    }
                }
                if (rootFolders.any { it.id !in selectedShelfFolders.map { it.id } } && isTuneIconClicked.value) {
                    item {
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = "Add folders into the \"${selectedShelfName.value}\" Shelf",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 15.dp, end = 20.dp),
                            color = MaterialTheme.colorScheme.primary,
                            lineHeight = 18.sp
                        )
                    }
                    items(rootFolders) { rootFolderElement ->
                        if (!selectedShelfFolders.any { it.id == rootFolderElement.id }) {
                            ListFolderUIComponent(
                                folderName = rootFolderElement.folderName,
                                {},
                                {},
                                {},
                                onAddClick = {
                                    shelfBtmSheetVM.onShelfUiEvent(
                                        ShelfUIEvent.InsertANewElementInHomeScreenList(
                                        folderName = rootFolderElement.folderName,
                                        folderID = rootFolderElement.id,
                                        parentShelfID = ShelfBtmSheetVM.selectedShelfData.id
                                        )
                                    )
                                },
                                shouldAddIconBeVisible = true,
                                shouldMoveUpIconVisible = false,
                                shouldMoveDownIconVisible = false
                            )
                        }
                    }
                }
                if (shelfData.size == 5 && !isTuneIconClicked.value) {
                    item {
                        InfoUI(infoText = "Only a maximum of 5 shelf rows can be added to the shelf. To add another shelf, you need to remove one from the list above.")
                        Spacer(modifier = Modifier.navigationBarsPadding())
                    }
                }
                if (shelfData.size < 5 && !isTuneIconClicked.value) {
                    item {
                        Button(
                            modifier = Modifier
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
                if (isTuneIconClicked.value) {
                    item {
                        Spacer(modifier = Modifier.navigationBarsPadding())
                    }
                }
            }
        }
    }
    val localContext = LocalContext.current
    AddANewShelfDialogBox(
        addANewShelfParam = AddANewShelfParam(
            isDialogBoxVisible = isAddANewShelfDialogBoxVisible,
            onCreateClick = { shelfName, shelfIconName ->
                shelfBtmSheetVM.onShelfUiEvent(
                    ShelfUIEvent.AddANewShelf(
                        Shelf(
                            shelfName = shelfName,
                            shelfIconName = shelfIconName,
                            folderIds = emptyList()
                        )
                    )
                )
            })
    )

    DeleteAShelfDialogBox(
        deleteAShelfDialogBoxParam = DeleteAShelfDialogBoxParam(
            isDialogBoxVisible = isDeleteAShelfDialogBoxVisible,
            onDeleteClick = { ->
                shelfBtmSheetVM.onShelfUiEvent(
                    ShelfUIEvent.DeleteAShelf(
                        ShelfBtmSheetVM.selectedShelfData
                    )
                )
            },
        )
    )
    RenameAShelfDialogBox(isDialogBoxVisible = isRenameAShelfDialogBoxVisible, onRenameClick = {
        shelfBtmSheetVM.onShelfUiEvent(
            ShelfUIEvent.UpdateAShelfName(
                it, ShelfBtmSheetVM.selectedShelfData.id
            )
        )
    })
}

@Composable
fun InfoUI(infoText: String) {
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
                text = infoText,
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

@Composable
fun InfoUI(infoText: AnnotatedString) {
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
                text = infoText,
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pulsateEffect()
            .clickable(interactionSource = remember {
                MutableInteractionSource()
            }, indication = null, onClick = {
                if (shouldAddIconBeVisible) {
                    onAddClick()
                }
            })
    ) {
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