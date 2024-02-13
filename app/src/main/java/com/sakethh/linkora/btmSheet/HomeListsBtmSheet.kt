package com.sakethh.linkora.btmSheet

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.localDB.commonVMs.CreateVM
import com.sakethh.linkora.localDB.commonVMs.DeleteVM
import com.sakethh.linkora.localDB.commonVMs.UpdateVM
import com.sakethh.linkora.screens.collections.CollectionsScreenVM


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeListBtmSheet(isBtmSheetVisible: MutableState<Boolean>) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val lazyListState = rememberLazyListState()
    val collectionsScreenVM: CollectionsScreenVM = viewModel()
    val homeListsBtmSheetVM: HomeListsBtmSheetVM = viewModel()
    val createVM: CreateVM = viewModel()
    val deleteVM: DeleteVM = viewModel()
    val updateVM: UpdateVM = viewModel()
    val homeScreenList = homeListsBtmSheetVM.readHomeScreenListTable.collectAsState().value
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
                    .navigationBarsPadding()
            ) {
                if (homeScreenList.isNotEmpty()) {
                    item {
                        Text(
                            text = "Currently shown in Home",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 15.dp, end = 20.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
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
                                .padding(start = 20.dp, end = 20.dp)
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
                                    text = "No folders are shown on the Home Screen.",
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
                itemsIndexed(items = homeScreenList) { currentIndex, listElement ->
                    ListFolderUIComponent(
                        folderName = listElement.folderName,
                        onMoveUpClick = {
                            val mutableList = homeScreenList.toMutableList()
                            --mutableList[currentIndex].position
                            ++mutableList[currentIndex - 1].position
                            updateVM.updateHomeListElement(mutableList[currentIndex])
                            updateVM.updateHomeListElement(mutableList[currentIndex - 1])
                        },
                        onMoveDownClick = {
                            val mutableList = homeScreenList.toMutableList()
                            ++mutableList[currentIndex].position
                            --mutableList[currentIndex + 1].position
                            updateVM.updateHomeListElement(mutableList[currentIndex])
                            updateVM.updateHomeListElement(mutableList[currentIndex + 1])
                        },
                        onRemoveClick = {
                            deleteVM.deleteAnElementFromHomeScreenList(listElement.id)
                        }, onAddClick = {}, shouldAddIconBeVisible = false,
                        shouldMoveUpIconVisible = currentIndex != 0,
                        shouldMoveDownIconVisible = currentIndex != homeScreenList.size - 1
                    )
                }
                if (homeScreenList.size == 5) {
                    item {
                        Spacer(modifier = Modifier.navigationBarsPadding())
                    }
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
                                    text = "Maximum of 5 folders can be added to the Home List. Remove any folder from the list above to add another folder.",
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
                if (homeScreenList.size < 5 && homeScreenList.size != rootFolders.size) {
                    item {
                        Text(
                            text = if (homeScreenList.isEmpty()) "Select folders" else "Select other folders",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 15.dp, end = 20.dp, top = 15.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    items(items = rootFolders) { rootFolderElement ->
                        if (!homeScreenList.any {
                                it.id == rootFolderElement.id
                            }
                        ) {
                            ListFolderUIComponent(
                                folderName = rootFolderElement.folderName,
                                {},
                                {},
                                {},
                                onAddClick = {
                                    createVM.insertANewElementInHomeScreenList(
                                        folderID = rootFolderElement.id,
                                        folderName = rootFolderElement.folderName
                                    )
                                },
                                shouldAddIconBeVisible = true,
                                shouldMoveUpIconVisible = false,
                                shouldMoveDownIconVisible = false,
                            )
                        }
                    }
                }
            }
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
        Divider(
            thickness = 1.dp,
            modifier = Modifier.padding(start = 25.dp, end = 25.dp),
            color = MaterialTheme.colorScheme.outline.copy(0.25f)
        )
    }
}