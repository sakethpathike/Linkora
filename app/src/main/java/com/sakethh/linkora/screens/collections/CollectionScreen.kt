package com.sakethh.linkora.screens.collections

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sakethh.linkora.btmSheet.NewLinkBtmSheet
import com.sakethh.linkora.btmSheet.OptionsBtmSheetType
import com.sakethh.linkora.btmSheet.OptionsBtmSheetUI
import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.localDB.ArchivedFolders
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.navigation.NavigationRoutes
import com.sakethh.linkora.screens.DataEmptyScreen
import com.sakethh.linkora.screens.collections.specificScreen.SpecificScreenType
import com.sakethh.linkora.screens.collections.specificScreen.SpecificScreenVM
import com.sakethh.linkora.screens.home.composables.AddNewFolderDialogBox
import com.sakethh.linkora.screens.home.composables.AddNewLinkDialogBox
import com.sakethh.linkora.screens.home.composables.DataDialogBoxType
import com.sakethh.linkora.screens.home.composables.DeleteDialogBox
import com.sakethh.linkora.screens.home.composables.RenameDialogBox
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(navController: NavController) {
    val context = LocalContext.current
    val heightOfCard = remember {
        mutableStateOf(0.dp)
    }
    val localDensity = LocalDensity.current
    val shouldRenameDialogBoxBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldDeleteDialogBoxBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val activity = LocalContext.current as? Activity
    val optionsBtmSheetVM: OptionsBtmSheetVM = viewModel()
    val collectionsScreenVM: CollectionsScreenVM = viewModel()
    val foldersData = collectionsScreenVM.foldersData.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    val btmModalSheetState = androidx.compose.material3.rememberModalBottomSheetState()
    val clickedFolderName = rememberSaveable { mutableStateOf("") }
    val clickedFolderNote = rememberSaveable { mutableStateOf("") }
    val btmModalSheetStateForSavingLinks =
        androidx.compose.material3.rememberModalBottomSheetState()
    val shouldOptionsBtmModalSheetBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val isMainFabRotated = rememberSaveable {
        mutableStateOf(false)
    }
    val rotationAnimation = remember {
        Animatable(0f)
    }
    val shouldScreenTransparencyDecreasedBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val currentIconForMainFAB = remember(isMainFabRotated.value) {
        mutableStateOf(
            if (isMainFabRotated.value) {
                Icons.Default.AddLink
            } else {
                Icons.Default.Add
            }
        )
    }
    val shouldDialogForNewLinkAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldDialogForNewFolderAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val isDataExtractingFromLink = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldBtmSheetForNewLinkAdditionBeEnabled = rememberSaveable {
        mutableStateOf(false)
    }
    LinkoraTheme {
        Scaffold(floatingActionButton = {
            if (SettingsScreenVM.Settings.isBtmSheetEnabledForSavingLinks.value) {
                FloatingActionButton(
                    modifier = Modifier.padding(bottom = 60.dp),
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                        coroutineScope.launch {
                            awaitAll(async {
                                btmModalSheetState.expand()
                            }, async { shouldBtmSheetForNewLinkAdditionBeEnabled.value = true })
                        }
                    }) {
                    Icon(
                        imageVector = Icons.Default.AddLink, contentDescription = null
                    )
                }
            } else {
                Column(modifier = Modifier.padding(bottom = 60.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        if (isMainFabRotated.value) {
                            AnimatedVisibility(
                                visible = isMainFabRotated.value,
                                enter = fadeIn(tween(200)),
                                exit = fadeOut(tween(200))
                            ) {
                                Text(
                                    text = "Create new folder",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(top = 20.dp, end = 15.dp)
                                )
                            }
                        }
                        AnimatedVisibility(
                            visible = isMainFabRotated.value,
                            enter = scaleIn(animationSpec = tween(300)),
                            exit = scaleOut(
                                tween(300)
                            )
                        ) {
                            FloatingActionButton(shape = RoundedCornerShape(10.dp), onClick = {
                                shouldScreenTransparencyDecreasedBoxVisible.value = false
                                shouldDialogForNewFolderAppear.value = true
                                isMainFabRotated.value = false
                            }) {
                                Icon(
                                    imageVector = Icons.Default.CreateNewFolder,
                                    contentDescription = null
                                )
                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        if (isMainFabRotated.value) {
                            AnimatedVisibility(
                                visible = isMainFabRotated.value,
                                enter = fadeIn(tween(200)),
                                exit = fadeOut(tween(200))
                            ) {
                                Text(
                                    text = "Add new link",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(top = 20.dp, end = 15.dp)
                                )
                            }
                        }
                        FloatingActionButton(modifier = Modifier.rotate(rotationAnimation.value),
                            shape = RoundedCornerShape(10.dp),
                            onClick = {
                                if (isMainFabRotated.value) {
                                    shouldScreenTransparencyDecreasedBoxVisible.value = false
                                    shouldDialogForNewLinkAppear.value = true
                                    isMainFabRotated.value = false
                                } else {
                                    coroutineScope.launch {
                                        awaitAll(async {
                                            rotationAnimation.animateTo(
                                                360f, animationSpec = tween(300)
                                            )
                                        }, async {
                                            shouldScreenTransparencyDecreasedBoxVisible.value = true
                                            delay(10L)
                                            isMainFabRotated.value = true
                                        })
                                    }.invokeOnCompletion {
                                        coroutineScope.launch {
                                            rotationAnimation.snapTo(0f)
                                        }
                                    }
                                }
                            }) {
                            Icon(
                                imageVector = currentIconForMainFAB.value,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        },
            floatingActionButtonPosition = FabPosition.End,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            topBar = {
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
                    Divider(
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(25.dp),
                        color = MaterialTheme.colorScheme.outline.copy(0.25f)
                    )
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
                    Divider(
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(25.dp),
                        color = MaterialTheme.colorScheme.outline.copy(0.25f)
                    )
                }
                item {
                    Text(
                        text = "Folders",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                }
                item {
                    Spacer(modifier = Modifier.padding(top = 15.dp))
                }
                if (foldersData.isNotEmpty()) {
                    itemsIndexed(foldersData) { folderIndex, foldersData ->
                        FolderIndividualComponent(
                            folderName = foldersData.folderName,
                            folderNote = foldersData.infoForSaving,
                            onMoreIconClick = {
                                CollectionsScreenVM.selectedFolderData.folderName =
                                    foldersData.folderName
                                CollectionsScreenVM.selectedFolderData.infoForSaving =
                                    foldersData.infoForSaving
                                clickedFolderNote.value = foldersData.infoForSaving
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
                } else {
                    item {
                        DataEmptyScreen()
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(165.dp))
                }
            }
            if (shouldScreenTransparencyDecreasedBoxVisible.value) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(0.85f))
                    .clickable {
                        shouldScreenTransparencyDecreasedBoxVisible.value = false
                        coroutineScope
                            .launch {
                                awaitAll(async {
                                    rotationAnimation.animateTo(
                                        -360f, animationSpec = tween(300)
                                    )
                                }, async { isMainFabRotated.value = false })
                            }
                            .invokeOnCompletion {
                                coroutineScope.launch {
                                    rotationAnimation.snapTo(0f)
                                }
                            }
                    })
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
                            archiveFolderName = CollectionsScreenVM.selectedFolderData.folderName,
                            infoForSaving = CollectionsScreenVM.selectedFolderData.infoForSaving
                        ), context = context
                    )
                }
            },
            noteForSaving = clickedFolderNote.value,
            onNoteDeleteCardClick = {
                coroutineScope.launch {
                    CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                        .deleteAFolderNote(folderName = CollectionsScreenVM.selectedFolderData.folderName)
                }.invokeOnCompletion {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            },
            linkTitle = "",
            folderName = CollectionsScreenVM.selectedFolderData.folderName
        )
        RenameDialogBox(
            onNoteChangeClickForLinks = null,
            shouldDialogBoxAppear = shouldRenameDialogBoxBeVisible,
            coroutineScope = coroutineScope,
            existingFolderName = clickedFolderName.value,
            onTitleChangeClickForLinks = null
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

        AddNewLinkDialogBox(
            shouldDialogBoxAppear = shouldDialogForNewLinkAppear,
            specificFolderName = "hi lol",
            screenType = SpecificScreenType.ROOT_SCREEN
        )
        AddNewFolderDialogBox(
            shouldDialogBoxAppear = shouldDialogForNewFolderAppear, coroutineScope = coroutineScope
        )
        NewLinkBtmSheet(
            btmSheetState = btmModalSheetStateForSavingLinks,
            _inIntentActivity = false,
            screenType = SpecificScreenType.ROOT_SCREEN,
            shouldUIBeVisible = shouldBtmSheetForNewLinkAdditionBeEnabled
        )
    }
    BackHandler {
        if (isMainFabRotated.value) {
            shouldScreenTransparencyDecreasedBoxVisible.value = false
            coroutineScope.launch {
                awaitAll(async {
                    rotationAnimation.animateTo(
                        -360f, animationSpec = tween(300)
                    )
                }, async {
                    delay(10L)
                    isMainFabRotated.value = false
                })
            }.invokeOnCompletion {
                coroutineScope.launch {
                    rotationAnimation.snapTo(0f)
                }
            }
        } else if (btmModalSheetState.isVisible) {
            coroutineScope.launch {
                btmModalSheetState.hide()
            }
        } else if (!SettingsScreenVM.Settings.isHomeScreenEnabled.value) {
            activity?.finish()
        } else {
            navController.navigate(NavigationRoutes.HOME_SCREEN.name) {
                popUpTo(0)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
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
                .combinedClickable(
                    onClick = { onFolderClick() },
                    onLongClick = { onMoreIconClick() })
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
            modifier = Modifier.padding(start = 25.dp, end = 25.dp),
            color = MaterialTheme.colorScheme.outline.copy(0.25f)
        )
    }
}