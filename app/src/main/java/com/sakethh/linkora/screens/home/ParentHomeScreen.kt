package com.sakethh.linkora.screens.home

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.primaryContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.sakethh.linkora.btmSheet.HomeListBtmSheet
import com.sakethh.linkora.btmSheet.HomeListsBtmSheetVM
import com.sakethh.linkora.btmSheet.NewLinkBtmSheet
import com.sakethh.linkora.btmSheet.NewLinkBtmSheetUIParam
import com.sakethh.linkora.btmSheet.SortingBottomSheetUI
import com.sakethh.linkora.btmSheet.SortingBottomSheetUIParam
import com.sakethh.linkora.btmSheet.SortingBtmSheetType
import com.sakethh.linkora.customComposables.AddNewFolderDialogBox
import com.sakethh.linkora.customComposables.AddNewFolderDialogBoxParam
import com.sakethh.linkora.customComposables.AddNewLinkDialogBox
import com.sakethh.linkora.customComposables.DataDialogBoxType
import com.sakethh.linkora.customComposables.DeleteDialogBox
import com.sakethh.linkora.customComposables.DeleteDialogBoxParam
import com.sakethh.linkora.customComposables.FloatingActionBtn
import com.sakethh.linkora.customComposables.FloatingActionBtnParam
import com.sakethh.linkora.localDB.LocalDataBase
import com.sakethh.linkora.localDB.commonVMs.CreateVM
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificCollectionsScreenVM
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreenType
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalPagerApi::class, ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun ParentHomeScreen(navController: NavController) {
    val pagerState = rememberPagerState()
    val homeScreenVM: HomeScreenVM = viewModel()
    val specificCollectionsScreenVM: SpecificCollectionsScreenVM = viewModel()
    val createVM: CreateVM = viewModel()
    val coroutineScope = rememberCoroutineScope()
    val shouldSortingBottomSheetAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldListsBottomSheetAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val sortingBtmSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val activity = LocalContext.current as? Activity
    val btmModalSheetStateForSavingLinks =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shouldDialogForNewLinkAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldDialogForNewFolderAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldBtmSheetForNewLinkAdditionBeEnabled = rememberSaveable {
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
    val homeListsBtmSheetVM: HomeListsBtmSheetVM = viewModel()
    val homeScreenList = homeListsBtmSheetVM.readHomeScreenListTable.collectAsState().value
    val shouldDeleteDialogBoxAppear = rememberSaveable {
        mutableStateOf(false)
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
                TopAppBar(navigationIcon = {
                    if (homeScreenVM.isSelectionModeEnabled.value) {
                        IconButton(onClick = {
                            homeScreenVM.isSelectionModeEnabled.value = false
                            homeScreenVM.areAllLinksChecked.value = false
                            homeScreenVM.areAllFoldersChecked.value = false
                            homeScreenVM.selectedLinksID.clear()
                            homeScreenVM.selectedFoldersData.clear()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Cancel, contentDescription = null
                            )
                        }
                    }
                }, title = {
                    if (homeScreenVM.isSelectionModeEnabled.value) {
                        Row {
                            AnimatedContent(
                                targetState = homeScreenVM.selectedLinksID.size + homeScreenVM.selectedFoldersData.size,
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
                                text = " items selected",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleLarge,
                                fontSize = 18.sp
                            )
                        }
                    } else {
                        Text(
                            text = homeScreenVM.currentPhaseOfTheDay.value,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 24.sp
                        )
                    }
                }, actions = {
                    if (homeScreenVM.isSelectionModeEnabled.value && homeScreenVM.selectedFoldersData.size + homeScreenVM.selectedLinksID.size > 0) {
                        IconButton(onClick = {
                            homeScreenVM.archiveMultipleFolders()
                            homeScreenVM.moveMultipleLinksFromLinksTableToArchive()
                            homeScreenVM.selectedFoldersData.clear()
                            homeScreenVM.selectedLinksID.clear()
                            homeScreenVM.isSelectionModeEnabled.value = false
                        }) {
                            Icon(imageVector = Icons.Outlined.Archive, contentDescription = null)
                        }
                        IconButton(onClick = {
                            shouldDeleteDialogBoxAppear.value = true
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteForever,
                                contentDescription = null
                            )
                        }
                    } else {
                        IconButton(onClick = { shouldListsBottomSheetAppear.value = true }) {
                            Icon(imageVector = Icons.Outlined.Tune, contentDescription = null)
                        }
                        IconButton(onClick = { shouldSortingBottomSheetAppear.value = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Sort,
                                contentDescription = null
                            )
                        }
                    }
                })
            }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                LazyColumn(modifier = Modifier.padding(it)) {
                    stickyHeader {
                        if (homeScreenList.isNotEmpty()) {
                            Column(modifier = Modifier.animateContentSize()) {
                                ScrollableTabRow(
                                    divider = {},
                                    modifier = Modifier
                                        .fillMaxWidth(), selectedTabIndex = pagerState.currentPage
                                ) {
                                    homeScreenList.forEachIndexed { index, homeScreenListsElement ->
                                        Tab(selected = pagerState.currentPage == index, onClick = {
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(index)
                                            }.start()
                                        }) {
                                            Text(
                                                text = homeScreenListsElement.folderName,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontSize = 18.sp,
                                                modifier = Modifier.padding(15.dp),
                                                color = if (pagerState.currentPage == index) primaryContentColor else MaterialTheme.colorScheme.onSurface.copy(
                                                    0.70f
                                                )
                                            )
                                        }
                                    }
                                }
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                }
                if (homeScreenList.isNotEmpty()) {
                    HorizontalPager(
                        key = {
                            it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        count = homeScreenList.size, state = pagerState
                    ) {
                        ChildHomeScreen(
                            homeScreenType = HomeScreenVM.HomeScreenType.CUSTOM_LIST,
                            navController = navController,
                            folderLinksData = LocalDataBase.localDB.readDao()
                                .getLinksOfThisFolderV10(homeScreenList[it].id).collectAsState(
                                    initial = emptyList()
                                ).value,
                            childFoldersData = LocalDataBase.localDB.readDao()
                                .getChildFoldersOfThisParentID(homeScreenList[it].id)
                                .collectAsState(
                                    initial = emptyList()
                                ).value,
                        )
                    }
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
        SortingBottomSheetUI(
            SortingBottomSheetUIParam(
                shouldBottomSheetVisible = shouldSortingBottomSheetAppear,
                onSelectedAComponent = { sortingPreferences, _, _ ->
                    specificCollectionsScreenVM.changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            sortingPreferences.name
                        ),
                        folderID = 0,
                        screenType = SpecificScreenType.SAVED_LINKS_SCREEN
                    )
                    specificCollectionsScreenVM.changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            sortingPreferences.name
                        ),
                        folderID = 0,
                        screenType = SpecificScreenType.IMPORTANT_LINKS_SCREEN
                    )
                },
                bottomModalSheetState = sortingBtmSheetState,
                sortingBtmSheetType = SortingBtmSheetType.PARENT_HOME_SCREEN,
                shouldFoldersSelectionBeVisible = mutableStateOf(false),
                shouldLinksSelectionBeVisible = mutableStateOf(false)
            )
        )
        val isDataExtractingForTheLink = rememberSaveable {
            mutableStateOf(false)
        }
        val context = LocalContext.current
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
                parentFolderID = null, inAChildFolderScreen = false
            )
        )

        HomeListBtmSheet(isBtmSheetVisible = shouldListsBottomSheetAppear)

        NewLinkBtmSheet(
            NewLinkBtmSheetUIParam(
                btmSheetState = btmModalSheetStateForSavingLinks,
                inIntentActivity = false,
                screenType = SpecificScreenType.ROOT_SCREEN,
                shouldUIBeVisible = shouldBtmSheetForNewLinkAdditionBeEnabled,
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
                onFolderCreated = {},
                parentFolderID = null,
                isDataExtractingForTheLink = isDataExtractingForTheLink
            )
        )

        DeleteDialogBox(
            deleteDialogBoxParam = DeleteDialogBoxParam(
                areFoldersSelectable = true,
                shouldDialogBoxAppear = shouldDeleteDialogBoxAppear,
                deleteDialogBoxType = DataDialogBoxType.FOLDER,
                onDeleteClick = {
                    homeScreenVM.onDeleteMultipleSelectedFolders()
                    homeScreenVM.onDeleteMultipleSelectedLinks()
                    homeScreenVM.isSelectionModeEnabled.value = false
                    homeScreenVM.selectedFoldersData.clear()
                    homeScreenVM.selectedLinksID.clear()
                }
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
        } else {
            activity?.finish()
        }
    }
}