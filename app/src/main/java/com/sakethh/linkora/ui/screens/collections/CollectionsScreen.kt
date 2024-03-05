package com.sakethh.linkora.ui.screens.collections

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sakethh.linkora.localDB.commonVMs.CreateVM
import com.sakethh.linkora.ui.commonBtmSheets.NewLinkBtmSheet
import com.sakethh.linkora.ui.commonBtmSheets.NewLinkBtmSheetUIParam
import com.sakethh.linkora.ui.commonBtmSheets.OptionsBtmSheetUI
import com.sakethh.linkora.ui.commonBtmSheets.OptionsBtmSheetUIParam
import com.sakethh.linkora.ui.commonBtmSheets.SortingBottomSheetUI
import com.sakethh.linkora.ui.commonBtmSheets.SortingBottomSheetUIParam
import com.sakethh.linkora.ui.commonBtmSheets.SortingBtmSheetType
import com.sakethh.linkora.ui.commonComposables.AddNewFolderDialogBox
import com.sakethh.linkora.ui.commonComposables.AddNewFolderDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.AddNewLinkDialogBox
import com.sakethh.linkora.ui.commonComposables.DataDialogBoxType
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBox
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.FloatingActionBtn
import com.sakethh.linkora.ui.commonComposables.FloatingActionBtnParam
import com.sakethh.linkora.ui.commonComposables.RenameDialogBox
import com.sakethh.linkora.ui.commonComposables.RenameDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.navigation.NavigationRoutes
import com.sakethh.linkora.ui.screens.DataEmptyScreen
import com.sakethh.linkora.ui.theme.LinkoraTheme
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM
import com.sakethh.linkora.ui.viewmodels.collections.CollectionsScreenVM
import com.sakethh.linkora.ui.viewmodels.collections.SpecificCollectionsScreenVM
import com.sakethh.linkora.ui.viewmodels.collections.SpecificScreenType
import com.sakethh.linkora.ui.viewmodels.commonBtmSheets.OptionsBtmSheetType
import com.sakethh.linkora.ui.viewmodels.commonBtmSheets.OptionsBtmSheetVM
import com.sakethh.linkora.ui.viewmodels.localDB.DeleteVM
import com.sakethh.linkora.ui.viewmodels.localDB.UpdateVM
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState", "LongLogTag")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    val foldersData = collectionsScreenVM.foldersData.collectAsStateWithLifecycle().value
    val coroutineScope = rememberCoroutineScope()
    val btmModalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val clickedFolderName = rememberSaveable { mutableStateOf("") }
    val clickedFolderNote = rememberSaveable { mutableStateOf("") }
    val btmModalSheetStateForSavingLinks = rememberModalBottomSheetState()
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
    val areFoldersSelectable = rememberSaveable {
        mutableStateOf(false)
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
    val sortingBtmSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shouldBtmSheetForNewLinkAdditionBeEnabled = rememberSaveable {
        mutableStateOf(false)
    }
    val updateVM: UpdateVM = viewModel()
    val deleteVM: DeleteVM = viewModel()
    if (collectionsScreenVM.selectedFoldersData.size == 0) {
        collectionsScreenVM.areAllFoldersChecked.value = false
    }
    LinkoraTheme {
        Scaffold(
            floatingActionButton = {
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
                    TopAppBar(actions = {
                        if (areFoldersSelectable.value && (collectionsScreenVM.selectedFoldersData.size != 0) && foldersData.isNotEmpty()) {
                            IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                shouldDeleteDialogBoxBeVisible.value = true
                            }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                            }
                            IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                collectionsScreenVM.archiveSelectedMultipleFolders()
                                areFoldersSelectable.value = false
                                collectionsScreenVM.areAllFoldersChecked.value = false
                                collectionsScreenVM.changeAllFoldersSelectedData()
                            }) {
                                Icon(imageVector = Icons.Default.Archive, contentDescription = null)
                            }
                        }
                    }, navigationIcon = {
                        if (areFoldersSelectable.value && foldersData.isNotEmpty()) {
                            IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                areFoldersSelectable.value = false
                                collectionsScreenVM.areAllFoldersChecked.value = false
                                collectionsScreenVM.changeAllFoldersSelectedData()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Cancel, contentDescription = null
                                )
                            }
                        }
                    }, title = {
                        if (areFoldersSelectable.value && foldersData.isNotEmpty()) {
                            Row {
                                AnimatedContent(
                                    targetState = collectionsScreenVM.selectedFoldersData.size,
                                    label = "",
                                    transitionSpec = {
                                        ContentTransform(
                                            initialContentExit = slideOutVertically(
                                                animationSpec = tween(
                                                    150
                                                )
                                            ) + fadeOut(
                                                tween(150)
                                            ), targetContentEnter = slideInVertically(
                                                animationSpec = tween(durationMillis = 150)
                                            ) + fadeIn(
                                                tween(150)
                                            )
                                        )
                                    }) {
                                    Text(
                                        text = it.toString(),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontSize = 18.sp
                                    )
                                }
                                Text(
                                    text = " folders selected",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontSize = 18.sp
                                )
                            }
                        } else {
                            Text(
                                text = "Collections",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleLarge,
                                fontSize = 24.sp
                            )
                        }
                    })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.25f))
                }
            }) {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                item {
                    Box(modifier = Modifier.animateContentSize()) {
                        Column {
                            if (!areFoldersSelectable.value || foldersData.isEmpty()) {
                                Card(
                                    modifier = Modifier
                                        .padding(
                                            top = 20.dp, end = 20.dp, start = 20.dp
                                        )
                                        .wrapContentHeight()
                                        .fillMaxWidth()
                                        .onGloballyPositioned {
                                            heightOfCard.value = with(localDensity) {
                                                it.size.height.toDp()
                                            }
                                        }
                                        .combinedClickable(interactionSource = remember {
                                            MutableInteractionSource()
                                        }, indication = null,
                                            onClick = {
                                                SpecificCollectionsScreenVM.screenType.value =
                                                    SpecificScreenType.IMPORTANT_LINKS_SCREEN
                                                navController.navigate(NavigationRoutes.SPECIFIC_SCREEN.name)
                                            },
                                            onLongClick = {

                                            })
                                        .pulsateEffect()
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
                                Card(
                                    modifier = Modifier
                                        .padding(
                                            top = 20.dp, end = 20.dp, start = 20.dp
                                        )
                                        .wrapContentHeight()
                                        .fillMaxWidth()
                                        .combinedClickable(interactionSource = remember {
                                            MutableInteractionSource()
                                        }, indication = null,
                                            onClick = {
                                                navController.navigate(NavigationRoutes.ARCHIVE_SCREEN.name)
                                            },
                                            onLongClick = {

                                            })
                                        .pulsateEffect()
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
                                HorizontalDivider(
                                    modifier = Modifier.padding(25.dp),
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(0.25f)
                                )
                                Card(
                                    modifier = Modifier
                                        .padding(end = 20.dp, start = 20.dp)
                                        .wrapContentHeight()
                                        .fillMaxWidth()
                                        .combinedClickable(interactionSource = remember {
                                            MutableInteractionSource()
                                        }, indication = null,
                                            onClick = {
                                                SpecificCollectionsScreenVM.screenType.value =
                                                    SpecificScreenType.SAVED_LINKS_SCREEN
                                                navController.navigate(NavigationRoutes.SPECIFIC_SCREEN.name)
                                            },
                                            onLongClick = {})
                                        .pulsateEffect()
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
                                HorizontalDivider(
                                    modifier = Modifier.padding(
                                        top = 20.dp,
                                        start = 20.dp,
                                        end = 20.dp,
                                        bottom = if (foldersData.isNotEmpty()) 11.dp else 25.dp
                                    ),
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(0.25f)
                                )
                            }
                        }
                    }
                }
                item {
                    Row(modifier = Modifier
                        .clickable {
                            if (foldersData.isNotEmpty() && !areFoldersSelectable.value) {
                                shouldSortingBottomSheetAppear.value = true
                                coroutineScope.launch {
                                    sortingBtmSheetState.expand()
                                }
                            } else {
                                collectionsScreenVM.areAllFoldersChecked.value =
                                    !collectionsScreenVM.areAllFoldersChecked.value
                                collectionsScreenVM.changeAllFoldersSelectedData()
                            }
                        }
                        .fillMaxWidth()
                        .wrapContentHeight(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Folders",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 15.dp)
                        )
                        if (foldersData.isNotEmpty() && !areFoldersSelectable.value) {
                            IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                shouldSortingBottomSheetAppear.value = true
                                coroutineScope.launch {
                                    sortingBtmSheetState.expand()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.Sort,
                                    contentDescription = null
                                )
                            }
                        } else if (areFoldersSelectable.value && foldersData.isNotEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Select all folders",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Checkbox(checked = collectionsScreenVM.areAllFoldersChecked.value,
                                    onCheckedChange = {
                                        collectionsScreenVM.areAllFoldersChecked.value = it
                                        collectionsScreenVM.changeAllFoldersSelectedData()
                                    })
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.padding(top = 0.dp))
                }
                if (foldersData.isNotEmpty()) {
                    itemsIndexed(
                        items = foldersData,
                        key = { folderIndex, foldersData ->
                            foldersData.id.toString() + foldersData.folderName
                        }) { folderIndex, folderData ->
                        FolderIndividualComponent(
                            showMoreIcon = !areFoldersSelectable.value,
                            folderName = folderData.folderName,
                            folderNote = folderData.infoForSaving,
                            onMoreIconClick = {
                                CollectionsScreenVM.selectedFolderData.value = folderData
                                clickedFolderNote.value = folderData.infoForSaving
                                coroutineScope.launch {
                                    optionsBtmSheetVM.updateArchiveFolderCardData(folderData.id)
                                }
                                clickedFolderName.value = folderData.folderName
                                CollectionsScreenVM.selectedFolderData.value = folderData
                                shouldOptionsBtmModalSheetBeVisible.value = true
                            },
                            onFolderClick = {
                                if (!areFoldersSelectable.value) {
                                    SpecificCollectionsScreenVM.inARegularFolder.value = true
                                    SpecificCollectionsScreenVM.screenType.value =
                                        SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                                    CollectionsScreenVM.currentClickedFolderData.value = folderData
                                    CollectionsScreenVM.rootFolderID = folderData.id
                                    navController.navigate(NavigationRoutes.SPECIFIC_SCREEN.name)
                                }
                            },
                            onLongClick = {
                                if (!areFoldersSelectable.value) {
                                    areFoldersSelectable.value = true
                                    collectionsScreenVM.areAllFoldersChecked.value = false
                                    collectionsScreenVM.changeAllFoldersSelectedData()
                                    collectionsScreenVM.selectedFoldersData.add(folderData)
                                }
                            },
                            showCheckBox = areFoldersSelectable,
                            isCheckBoxChecked = mutableStateOf(
                                collectionsScreenVM.selectedFoldersData.contains(
                                    folderData
                                )
                            ),
                            checkBoxState = { checkBoxState ->
                                if (checkBoxState) {
                                    collectionsScreenVM.selectedFoldersData.add(folderData)
                                } else {
                                    collectionsScreenVM.selectedFoldersData.removeAll {
                                        it == folderData
                                    }
                                }
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
                        context, CollectionsScreenVM.selectedFolderData.value.id
                    )
                },
                linkTitle = "",
                folderName = CollectionsScreenVM.selectedFolderData.value.folderName
            )
        )
        RenameDialogBox(
            RenameDialogBoxParam(onNoteChangeClick = {
                updateVM.updateFolderNote(
                    CollectionsScreenVM.selectedFolderData.value.id, newFolderNote = it
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
                })
        )
        DeleteDialogBox(
            DeleteDialogBoxParam(
                areFoldersSelectable = areFoldersSelectable.value,
                totalIds = mutableLongStateOf(
                    CollectionsScreenVM.selectedFolderData.value.childFolderIDs?.size?.toLong() ?: 0
                ),
                shouldDialogBoxAppear = shouldDeleteDialogBoxBeVisible,
                onDeleteClick = {
                    if (areFoldersSelectable.value) {
                        collectionsScreenVM.onDeleteMultipleSelectedFolders()
                        areFoldersSelectable.value = false
                        collectionsScreenVM.areAllFoldersChecked.value = false
                        collectionsScreenVM.changeAllFoldersSelectedData()
                    } else {
                        deleteVM.onRegularFolderDeleteClick(
                            CollectionsScreenVM.selectedFolderData.value.id
                        )
                    }
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
                            createVM.addANewLinkInAFolderV10(title = title,
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
                shouldDialogBoxAppear = shouldDialogForNewFolderAppear, onCreated = {
                    collectionsScreenVM.changeRetrievedFoldersData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        )
                    )
                }, parentFolderID = null, inAChildFolderScreen = false
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
                                createVM.addANewLinkInAFolderV10(title = title,
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
            SortingBottomSheetUIParam(
                shouldBottomSheetVisible = shouldSortingBottomSheetAppear,
                onSelectedAComponent = { sortingPreferences, _, _ ->
                    collectionsScreenVM.changeRetrievedFoldersData(sortingPreferences = sortingPreferences)
                },
                bottomModalSheetState = sortingBtmSheetState,
                sortingBtmSheetType = SortingBtmSheetType.COLLECTIONS_SCREEN,
                shouldFoldersSelectionBeVisible = mutableStateOf(false),
                shouldLinksSelectionBeVisible = mutableStateOf(false)
            )
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
        } else if (areFoldersSelectable.value) {
            areFoldersSelectable.value = false
            collectionsScreenVM.areAllFoldersChecked.value = false
            collectionsScreenVM.changeAllFoldersSelectedData()
        } else if (!SettingsScreenVM.Settings.isHomeScreenEnabled.value) {
            activity?.moveTaskToBack(true)
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
    onFolderClick: (checkBoxState: Boolean) -> Unit,
    maxLines: Int = 1,
    showMoreIcon: Boolean,
    folderIcon: ImageVector = Icons.Outlined.Folder,
    showCheckBox: MutableState<Boolean> = rememberSaveable {
        mutableStateOf(false)
    },
    checkBoxState: (Boolean) -> Unit = {},
    isCheckBoxChecked: MutableState<Boolean> = rememberSaveable {
        mutableStateOf(false)
    },
    onLongClick: () -> Unit = {},
    inSelectionMode: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (showCheckBox.value && isCheckBoxChecked.value) MaterialTheme.colorScheme.primary.copy(
                    0.25f
                ) else Color.Transparent
            )
    ) {
        Row(
            modifier = Modifier
                .combinedClickable(interactionSource = remember {
                    MutableInteractionSource()
                }, indication = null,
                    onClick = {
                        onFolderClick(isCheckBoxChecked.value)
                        isCheckBoxChecked.value = !isCheckBoxChecked.value
                        checkBoxState(isCheckBoxChecked.value)
                    },
                    onLongClick = {
                        onLongClick()
                    })
                .pulsateEffect()
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = folderIcon,
                contentDescription = null,
                modifier = Modifier
                    .padding(20.dp)
                    .size(28.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(if (showMoreIcon) 0.80f else if (showCheckBox.value) 0.78f else 1f),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = folderName,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(
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
                        modifier = Modifier.padding(top = if (folderNote.isNotEmpty()) 5.dp else 0.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (showMoreIcon) {
                IconButton(onClick = { onMoreIconClick() }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = null
                    )
                }
            }
            if (showCheckBox.value && inSelectionMode) {
                Checkbox(
                    checked = isCheckBoxChecked.value,
                    onCheckedChange = {
                        checkBoxState(it)
                        isCheckBoxChecked.value = it
                    })
            } else if (showCheckBox.value && !inSelectionMode) {
                Checkbox(
                    checked = isCheckBoxChecked.value,
                    onCheckedChange = {
                        checkBoxState(it)
                        isCheckBoxChecked.value = it
                    })
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(start = 25.dp, end = 25.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(0.25f)
        )
    }
}