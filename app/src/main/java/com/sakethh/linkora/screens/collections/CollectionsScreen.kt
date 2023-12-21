package com.sakethh.linkora.screens.collections

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sakethh.linkora.btmSheet.NewLinkBtmSheet
import com.sakethh.linkora.btmSheet.NewLinkBtmSheetUIParam
import com.sakethh.linkora.btmSheet.OptionsBtmSheetType
import com.sakethh.linkora.btmSheet.OptionsBtmSheetUI
import com.sakethh.linkora.btmSheet.OptionsBtmSheetUIParam
import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.btmSheet.SortingBottomSheetUI
import com.sakethh.linkora.customComposables.AddNewFolderDialogBox
import com.sakethh.linkora.customComposables.AddNewFolderDialogBoxParam
import com.sakethh.linkora.customComposables.AddNewLinkDialogBox
import com.sakethh.linkora.customComposables.DataDialogBoxType
import com.sakethh.linkora.customComposables.DeleteDialogBox
import com.sakethh.linkora.customComposables.DeleteDialogBoxParam
import com.sakethh.linkora.customComposables.FloatingActionBtn
import com.sakethh.linkora.customComposables.FloatingActionBtnParam
import com.sakethh.linkora.customComposables.RenameDialogBox
import com.sakethh.linkora.customComposables.RenameDialogBoxParam
import com.sakethh.linkora.localDB.commonVMs.CreateVM
import com.sakethh.linkora.localDB.commonVMs.DeleteVM
import com.sakethh.linkora.localDB.commonVMs.UpdateVM
import com.sakethh.linkora.navigation.NavigationRoutes
import com.sakethh.linkora.screens.DataEmptyScreen
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificCollectionsScreenVM
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreenType
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen(navController: NavController) {
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
    val btmModalSheetState = rememberModalBottomSheetState()
    val clickedFolderName = rememberSaveable { mutableStateOf("") }
    val clickedFolderNote = rememberSaveable { mutableStateOf("") }
    val btmModalSheetStateForSavingLinks =
        rememberModalBottomSheetState()
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
    val shouldSortingBottomSheetAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val sortingBtmSheetState = rememberModalBottomSheetState()
    val shouldBtmSheetForNewLinkAdditionBeEnabled = rememberSaveable {
        mutableStateOf(false)
    }
    val updateVM: UpdateVM = viewModel()
    val deleteVM: DeleteVM = viewModel()
    LinkoraTheme {
        Scaffold(floatingActionButton = {
            FloatingActionBtn(
                FloatingActionBtnParam(
                    newLinkBottomModalSheetState = btmModalSheetStateForSavingLinks,
                    shouldBtmSheetForNewLinkAdditionBeEnabled = shouldBtmSheetForNewLinkAdditionBeEnabled,
                    shouldScreenTransparencyDecreasedBoxVisible = shouldScreenTransparencyDecreasedBoxVisible,
                    shouldDialogForNewFolderAppear = shouldDialogForNewFolderAppear,
                    shouldDialogForNewLinkAppear = shouldDialogForNewLinkAppear,
                    isMainFabRotated = isMainFabRotated,
                    rotationAnimation = rotationAnimation,
                    inASpecificScreen = false
                )
            )
        },
            floatingActionButtonPosition = FabPosition.End,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            topBar = {
                Column {
                    TopAppBar(title = {
                        Text(
                            text = "Collections",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 24.sp
                        )
                    })
                    Divider(color = MaterialTheme.colorScheme.outline.copy(0.25f))
                }
            }) {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                item {
                    Card(modifier = Modifier
                        .padding(top = 20.dp, end = 20.dp, start = 20.dp)
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .onGloballyPositioned {
                            heightOfCard.value = with(localDensity) {
                                it.size.height.toDp()
                            }
                        }
                        .clickable {
                            SpecificCollectionsScreenVM.screenType.value =
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
                    Card(modifier = Modifier
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
                    Card(modifier = Modifier
                        .padding(end = 20.dp, start = 20.dp)
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .clickable {
                            SpecificCollectionsScreenVM.screenType.value =
                                SpecificScreenType.SAVED_LINKS_SCREEN
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
                        modifier = Modifier.padding(
                            top = 20.dp,
                            start = 20.dp,
                            end = 20.dp,
                            bottom = if (foldersData.isNotEmpty()) 11.dp else 25.dp
                        ),
                        color = MaterialTheme.colorScheme.outline.copy(0.25f)
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .clickable {
                                if (foldersData.isNotEmpty()) {
                                    shouldSortingBottomSheetAppear.value = true
                                }
                            }
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Folders",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 15.dp)
                        )
                        if (foldersData.isNotEmpty()) {
                            IconButton(onClick = { shouldSortingBottomSheetAppear.value = true }) {
                                Icon(imageVector = Icons.Outlined.Sort, contentDescription = null)
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.padding(top = 0.dp))
                }
                if (foldersData.isNotEmpty()) {
                    itemsIndexed(foldersData) { folderIndex, folderData ->
                        FolderIndividualComponent(
                            showMoreIcon = true,
                            folderName = folderData.folderName,
                            folderNote = folderData.infoForSaving,
                            onMoreIconClick = {
                                CollectionsScreenVM.selectedFolderData.value =
                                    folderData
                                clickedFolderNote.value = folderData.infoForSaving
                                coroutineScope.launch {
                                    optionsBtmSheetVM.updateArchiveFolderCardData(folderName = folderData.folderName)
                                }
                                clickedFolderName.value = folderData.folderName
                                CollectionsScreenVM.selectedFolderData.value = folderData
                                shouldOptionsBtmModalSheetBeVisible.value = true
                            }, onFolderClick = {
                                SpecificCollectionsScreenVM.inARegularFolder.value = true
                                SpecificCollectionsScreenVM.screenType.value =
                                    SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                                CollectionsScreenVM.currentClickedFolderData.value =
                                    folderData
                                CollectionsScreenVM.rootFolderID = folderData.id
                                navController.navigate(NavigationRoutes.SPECIFIC_SCREEN.name)
                            })
                    }
                } else {
                    item {
                        DataEmptyScreen(text = "No folders are found. Create folders for better organization of your links.")
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(225.dp))
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
            OptionsBtmSheetUIParam(
                btmModalSheetState = btmModalSheetState,
                shouldBtmModalSheetBeVisible = shouldOptionsBtmModalSheetBeVisible,
                btmSheetFor = OptionsBtmSheetType.FOLDER,
                onDeleteCardClick = {
                    shouldDeleteDialogBoxBeVisible.value = true
                },
                onRenameClick = {
                    shouldRenameDialogBoxBeVisible.value = true
                },
                importantLinks = null,
                onArchiveClick = {
                    updateVM.archiveAFolderV10(CollectionsScreenVM.selectedFolderData.value.id)
                },
                noteForSaving = clickedFolderNote.value,
                onNoteDeleteCardClick = {
                    collectionsScreenVM.onNoteDeleteClick(
                        context,
                        CollectionsScreenVM.selectedFolderData.value.id
                    )
                },
                linkTitle = "",
                folderName = CollectionsScreenVM.selectedFolderData.value.folderName
            )
        )
        RenameDialogBox(
            RenameDialogBoxParam(
                onNoteChangeClick = {
                    updateVM.updateFolderNote(
                        CollectionsScreenVM.selectedFolderData.value.id,
                        newFolderNote = it
                    )
                    shouldRenameDialogBoxBeVisible.value = false
                },
                shouldDialogBoxAppear = shouldRenameDialogBoxBeVisible,
                existingFolderName = clickedFolderName.value,
                onTitleChangeClick = {
                    updateVM.updateFolderName(
                        folderID = CollectionsScreenVM.selectedFolderData.value.id,
                        newFolderName = it
                    )
                    collectionsScreenVM.changeRetrievedFoldersData(
                        SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        )
                    )
                    shouldRenameDialogBoxBeVisible.value = false
                }
            )
        )
        DeleteDialogBox(
            DeleteDialogBoxParam(
                totalIds = mutableLongStateOf(
                    CollectionsScreenVM.selectedFolderData.value.childFolderIDs?.size?.toLong() ?: 0
                ),
                shouldDialogBoxAppear = shouldDeleteDialogBoxBeVisible,
                onDeleteClick = {
                    deleteVM.onRegularFolderDeleteClick(
                        CollectionsScreenVM.selectedFolderData.value.id
                    )
                },
                deleteDialogBoxType = DataDialogBoxType.FOLDER,
                onDeleted = {
                    collectionsScreenVM.changeRetrievedFoldersData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        )
                    )
                })
        )
        val isDataExtractingForTheLink = rememberSaveable {
            mutableStateOf(false)
        }
        val createVM: CreateVM = viewModel()
        AddNewLinkDialogBox(
            shouldDialogBoxAppear = shouldDialogForNewLinkAppear,
            screenType = SpecificScreenType.ROOT_SCREEN,
            parentFolderID = null,
            onSaveClick = { isAutoDetectSelected: Boolean, webURL: String, title: String, note: String, selectedDefaultFolderName: String?, selectedNonDefaultFolderID: Long? ->
                isDataExtractingForTheLink.value = true
                if (selectedDefaultFolderName == "Saved Links") {
                    createVM.addANewLinkInSavedLinks(
                        title = title,
                        webURL = webURL,
                        noteForSaving = note,
                        autoDetectTitle = isAutoDetectSelected,
                        onTaskCompleted = {
                            shouldDialogForNewLinkAppear.value = false
                            isDataExtractingForTheLink.value = false
                        },
                        context = context
                    )
                }
                if (selectedDefaultFolderName == "Important Links") {
                    createVM.addANewLinkInImpLinks(
                        context = context,
                        onTaskCompleted = {
                            shouldDialogForNewLinkAppear.value = false
                            isDataExtractingForTheLink.value = false
                        },
                        title = title,
                        webURL = webURL,
                        noteForSaving = note,
                        autoDetectTitle = isAutoDetectSelected
                    )
                }
                when {
                    selectedDefaultFolderName != "Important Links" && selectedDefaultFolderName != "Saved Links" -> {
                        if (selectedNonDefaultFolderID != null && selectedDefaultFolderName != null) {
                            createVM.addANewLinkInAFolderV10(
                                title = title,
                                webURL = webURL,
                                noteForSaving = note,
                                parentFolderID = selectedNonDefaultFolderID,
                                context = context,
                                folderName = selectedDefaultFolderName,
                                autoDetectTitle = isAutoDetectSelected,
                                onTaskCompleted = {
                                    shouldDialogForNewLinkAppear.value = false
                                    isDataExtractingForTheLink.value = false
                                })
                        }
                    }
                }
            },
            isDataExtractingForTheLink = isDataExtractingForTheLink.value
        )
        AddNewFolderDialogBox(
            AddNewFolderDialogBoxParam(
                shouldDialogBoxAppear = shouldDialogForNewFolderAppear,
                onCreated = {
                    collectionsScreenVM.changeRetrievedFoldersData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        )
                    )
                },
                parentFolderID = null,
                inAChildFolderScreen = false
            )
        )
        NewLinkBtmSheet(
            NewLinkBtmSheetUIParam(
                btmSheetState = btmModalSheetStateForSavingLinks,
                inIntentActivity = false,
                screenType = SpecificScreenType.ROOT_SCREEN,
                shouldUIBeVisible = shouldBtmSheetForNewLinkAdditionBeEnabled,
                onFolderCreated = {
                    collectionsScreenVM.changeRetrievedFoldersData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        )
                    )
                },
                onLinkSaveClick = { isAutoDetectSelected, webURL, title, note, selectedDefaultFolder, selectedNonDefaultFolderID ->
                    isDataExtractingForTheLink.value = true
                    if (selectedDefaultFolder == "Saved Links") {
                        createVM.addANewLinkInSavedLinks(
                            title = title,
                            webURL = webURL,
                            noteForSaving = note,
                            autoDetectTitle = isAutoDetectSelected,
                            onTaskCompleted = {
                                coroutineScope.launch {
                                    btmModalSheetStateForSavingLinks.hide()
                                    shouldBtmSheetForNewLinkAdditionBeEnabled.value = false
                                    isDataExtractingForTheLink.value = false
                                }
                            },
                            context = context
                        )
                    }
                    if (selectedDefaultFolder == "Important Links") {
                        createVM.addANewLinkInImpLinks(
                            context = context,
                            onTaskCompleted = {
                                coroutineScope.launch {
                                    btmModalSheetStateForSavingLinks.hide()
                                    shouldBtmSheetForNewLinkAdditionBeEnabled.value = false
                                    isDataExtractingForTheLink.value = false
                                }
                            },
                            title = title,
                            webURL = webURL,
                            noteForSaving = note,
                            autoDetectTitle = isAutoDetectSelected
                        )
                    }
                    when {
                        selectedDefaultFolder != "Important Links" && selectedDefaultFolder != "Saved Links" -> {
                            if (selectedNonDefaultFolderID != null && selectedDefaultFolder != null) {
                                createVM.addANewLinkInAFolderV10(
                                    title = title,
                                    webURL = webURL,
                                    noteForSaving = note,
                                    parentFolderID = selectedNonDefaultFolderID,
                                    context = context,
                                    folderName = selectedDefaultFolder,
                                    autoDetectTitle = isAutoDetectSelected,
                                    onTaskCompleted = {
                                        coroutineScope.launch {
                                            btmModalSheetStateForSavingLinks.hide()
                                            shouldBtmSheetForNewLinkAdditionBeEnabled.value = false
                                            isDataExtractingForTheLink.value = false
                                        }
                                    })
                            }
                        }
                    }
                },
                parentFolderID = null,
                isDataExtractingForTheLink = isDataExtractingForTheLink
            )
        )
        SortingBottomSheetUI(
            shouldBottomSheetVisible = shouldSortingBottomSheetAppear,
            onSelectedAComponent = { sortingPreferences ->
                collectionsScreenVM.changeRetrievedFoldersData(sortingPreferences = sortingPreferences)
            },
            bottomModalSheetState = sortingBtmSheetState
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
    maxLines: Int = 1,
    showMoreIcon: Boolean,
    folderIcon: ImageVector = Icons.Outlined.Folder,
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
                    imageVector = folderIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(20.dp)
                        .size(28.dp)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(if (showMoreIcon) 0.80f else 1f),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = folderName,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(
                        top = if (folderNote.isNotEmpty()) 10.dp else 0.dp,
                        end = if (showMoreIcon) 0.dp else 20.dp
                    ),
                    maxLines = maxLines,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = if (!showMoreIcon) 20.sp else TextUnit.Unspecified
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
            if (showMoreIcon) {
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
        }
        Divider(
            thickness = 1.dp,
            modifier = Modifier.padding(start = 25.dp, end = 25.dp),
            color = MaterialTheme.colorScheme.outline.copy(0.25f)
        )
    }
}