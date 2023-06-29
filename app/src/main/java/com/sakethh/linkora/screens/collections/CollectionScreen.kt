package com.sakethh.linkora.screens.collections

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sakethh.linkora.btmSheet.OptionsBtmSheetType
import com.sakethh.linkora.btmSheet.OptionsBtmSheetUI
import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.localDB.ArchivedFolders
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.localDB.FoldersTable
import com.sakethh.linkora.navigation.NavigationRoutes
import com.sakethh.linkora.screens.collections.specificScreen.SpecificScreenType
import com.sakethh.linkora.screens.collections.specificScreen.SpecificScreenVM
import com.sakethh.linkora.screens.home.composables.DataDialogBoxType
import com.sakethh.linkora.screens.home.composables.DeleteDialogBox
import com.sakethh.linkora.screens.home.composables.RenameDialogBox
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(navController: NavController) {
    val heightOfCard = remember {
        mutableStateOf(0.dp)
    }
    val localDensity = LocalDensity.current
    val shouldOptionsBtmModalSheetBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldRenameDialogBoxBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldDeleteDialogBoxBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    var selectedFolderData = FoldersTable("", "")
    val optionsBtmSheetVM: OptionsBtmSheetVM = viewModel()
    val collectionsScreenVM: CollectionsScreenVM = viewModel()
    val foldersData = collectionsScreenVM.foldersData.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    val btmModalSheetState = androidx.compose.material3.rememberModalBottomSheetState()
    val clickedFolderName = rememberSaveable { mutableStateOf("") }
    LinkoraTheme {
        Scaffold(modifier = Modifier.background(MaterialTheme.colorScheme.surface), topBar = {
            TopAppBar(title = {
                Text(
                    text = "Collections",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 24.sp
                )
            })
        }) {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                item {
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
                                SpecificScreenVM.screenType.value =
                                    SpecificScreenType.IMPORTANT_LINKS_SCREEN
                                navController.navigate(NavigationRoutes.SPECIFIC_SCREEN.name)
                            }
                    ) {
                        Row(horizontalArrangement = Arrangement.Center) {
                            Icon(
                                modifier = Modifier.padding(20.dp),
                                imageVector = Icons.Outlined.StarOutline,
                                contentDescription = null
                            )
                            Box(
                                modifier = Modifier.height(heightOfCard.value),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "Important Links",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
                item {
                    Card(
                        shape = RoundedCornerShape(10.dp), modifier = Modifier
                            .padding(top = 20.dp, end = 20.dp, start = 20.dp)
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(NavigationRoutes.ARCHIVE_SCREEN.name)
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
                                    text = "Archive",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
                item {
                    Divider(thickness = 0.5.dp, modifier = Modifier.padding(25.dp))
                }
                item {
                    Card(
                        shape = RoundedCornerShape(10.dp), modifier = Modifier
                            .padding(end = 20.dp, start = 20.dp)
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .clickable {
                                SpecificScreenVM.screenType.value = SpecificScreenType.LINKS_SCREEN
                                navController.navigate(NavigationRoutes.SPECIFIC_SCREEN.name)
                            }
                    ) {
                        Row {
                            Icon(
                                modifier = Modifier.padding(20.dp),
                                imageVector = Icons.Outlined.Link,
                                contentDescription = null
                            )
                            Box(
                                modifier = Modifier.height(heightOfCard.value),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "Saved Links",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
                item {
                    Divider(thickness = 0.5.dp, modifier = Modifier.padding(25.dp))
                }
                item {
                    Text(
                        text = "Folders",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                }
                item {
                    Spacer(modifier = Modifier.padding(top = 15.dp))
                }
                itemsIndexed(foldersData) { folderIndex, foldersData ->
                    FolderIndividualComponent(
                        folderName = foldersData.folderName,
                        folderNote = foldersData.infoForSaving,
                        onMoreIconClick = {
                            selectedFolderData.folderName = foldersData.folderName
                            selectedFolderData.infoForSaving = foldersData.infoForSaving
                            coroutineScope.launch {
                                optionsBtmSheetVM.updateArchiveFolderCardData(folderName = foldersData.folderName)
                            }
                            clickedFolderName.value = foldersData.folderName
                            shouldOptionsBtmModalSheetBeVisible.value = true
                        }, onFolderClick = {
                            SpecificScreenVM.screenType.value =
                                SpecificScreenType.SPECIFIC_FOLDER_SCREEN
                            SpecificScreenVM.currentClickedFolderName.value =
                                foldersData.folderName
                            navController.navigate(NavigationRoutes.SPECIFIC_SCREEN.name)
                        })
                }
                item {
                    Spacer(modifier = Modifier.height(65.dp))
                }
            }
        }
        OptionsBtmSheetUI(
            btmModalSheetState = btmModalSheetState,
            shouldBtmModalSheetBeVisible = shouldOptionsBtmModalSheetBeVisible,
            coroutineScope = coroutineScope,
            btmSheetFor = OptionsBtmSheetType.FOLDER,
            onDeleteCardClick = {
                shouldDeleteDialogBoxBeVisible.value = true
            },
            onRenameClick = {
                shouldRenameDialogBoxBeVisible.value = true
            },
            importantLinks = null,
            onArchiveClick = {
                coroutineScope.launch {
                    CustomLocalDBDaoFunctionsDecl.archiveFolderTableUpdater(
                        archivedFolders = ArchivedFolders(
                            archiveFolderName = selectedFolderData.folderName,
                            infoForSaving = selectedFolderData.infoForSaving
                        )
                    )
                }
            }
        )
        RenameDialogBox(
            shouldDialogBoxAppear = shouldRenameDialogBoxBeVisible,
            coroutineScope = coroutineScope,
            existingFolderName = clickedFolderName.value
        )
        DeleteDialogBox(
            shouldDialogBoxAppear = shouldDeleteDialogBoxBeVisible,
            onDeleteClick = {
                coroutineScope.launch {
                    awaitAll(async {
                        CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                            .deleteAFolder(folderName = clickedFolderName.value)
                    }, async {
                        CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                            .deleteThisFolderData(folderName = clickedFolderName.value)
                    })
                }
            },
            deleteDialogBoxType = DataDialogBoxType.FOLDER
        )
    }
    BackHandler {
        if (btmModalSheetState.isVisible) {
            coroutineScope.launch {
                btmModalSheetState.hide()
            }
        } else {
            navController.navigate(NavigationRoutes.HOME_SCREEN.name) {
                popUpTo(0)
            }
        }
    }
}

@Composable
fun FolderIndividualComponent(
    folderName: String,
    folderNote: String,
    onMoreIconClick: () -> Unit,
    onFolderClick: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .clickable {
                    onFolderClick()
                }
                .fillMaxWidth()
                .requiredHeight(75.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    imageVector = Icons.Outlined.Folder,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(20.dp)
                        .size(28.dp)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.80f),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = folderName,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = if (folderNote.isNotEmpty()) 10.dp else 0.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (folderNote.isNotEmpty()) {
                    Text(
                        text = folderNote,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = if (folderNote.isNotEmpty()) 10.dp else 0.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clickable {
                        onMoreIconClick()
                    }
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .align(Alignment.CenterEnd)
                )
            }
        }
        Divider(
            thickness = 1.dp,
            modifier = Modifier.padding(start = 25.dp, end = 25.dp)
        )
    }
}