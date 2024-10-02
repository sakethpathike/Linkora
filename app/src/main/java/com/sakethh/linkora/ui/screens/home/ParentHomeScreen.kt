package com.sakethh.linkora.ui.screens.home

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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Maximize
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material.icons.filled.ViewArray
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.ViewArray
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.ui.CustomWebTab
import com.sakethh.linkora.ui.bottomSheets.sorting.SortingBottomSheetParam
import com.sakethh.linkora.ui.bottomSheets.sorting.SortingBottomSheetUI
import com.sakethh.linkora.ui.bottomSheets.sorting.SortingBtmSheetType
import com.sakethh.linkora.ui.commonComposables.AddANewLinkDialogBox
import com.sakethh.linkora.ui.commonComposables.AddNewFolderDialogBox
import com.sakethh.linkora.ui.commonComposables.AddNewFolderDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.DataDialogBoxType
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBox
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.FloatingActionBtn
import com.sakethh.linkora.ui.commonComposables.FloatingActionBtnParam
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.navigation.NavigationRoutes
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenUIEvent
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenVM
import com.sakethh.linkora.ui.screens.collections.specific.SpecificScreenType
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.dataStore
import com.sakethh.linkora.ui.screens.settings.SettingsPreferences
import com.sakethh.linkora.ui.screens.settings.SortingPreferences
import com.sakethh.linkora.ui.theme.LinkoraTheme
import com.sakethh.linkora.utils.linkoraLog
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(
    ExperimentalPagerApi::class, ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun ParentHomeScreen(
    navController: NavController, customWebTab: CustomWebTab
) {
    val pagerState = rememberPagerState()
    val homeScreenVM: HomeScreenVM = hiltViewModel()
    val specificCollectionsScreenVM: SpecificCollectionsScreenVM = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    val shouldSortingBottomSheetAppear = rememberSaveable {
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
    val homeScreenList =
        homeScreenVM.selectedShelfFoldersForSelectedShelf.collectAsStateWithLifecycle().value
    val shouldDeleteDialogBoxAppear = rememberSaveable {
        mutableStateOf(false)
        mutableStateOf(false)
    }
    val cardOffSetX = remember { mutableStateOf(0f) }
    val cardOffSetY = remember { mutableStateOf(0f) }
    val shelfData = homeScreenVM.shelfData.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val shelfLazyColumnState = rememberLazyListState()
    LinkoraTheme {
        Scaffold(
            floatingActionButton = {
                if (!homeScreenVM.isSelectionModeEnabled.value) {
                    Column(
                        Modifier
                            .wrapContentHeight()
                    ) {
                        if (!isMainFabRotated.value) {
                            FloatingActionButton(onClick = {
                                SettingsPreference.isShelfMinimizedInHomeScreen.value =
                                    !SettingsPreference.isShelfMinimizedInHomeScreen.value
                                SettingsPreference.changeSettingPreferenceValue(
                                    booleanPreferencesKey(SettingsPreferences.SHELF_VISIBLE_STATE.name),
                                    context.dataStore,
                                    SettingsPreference.isShelfMinimizedInHomeScreen.value
                                )
                            }) {
                                Icon(
                                    imageVector = if (SettingsPreference.isShelfMinimizedInHomeScreen.value) Icons.Default.Maximize else Icons.Default.Minimize,
                                    contentDescription = ""
                                )
                            }
                        }
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
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            topBar = {
                TopAppBar(navigationIcon = {
                    if (homeScreenVM.isSelectionModeEnabled.value) {
                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                            homeScreenVM.isSelectionModeEnabled.value = false
                            homeScreenVM.areAllLinksChecked.value = false
                            homeScreenVM.areAllFoldersChecked.value = false
                            homeScreenVM.selectedLinksID.clear()
                            homeScreenVM.selectedImpLinkIds.clear()
                            homeScreenVM.selectedSavedLinkIds.clear()
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
                                targetState = homeScreenVM.selectedLinksID.size + homeScreenVM.selectedFoldersData.size + homeScreenVM.selectedSavedLinkIds.size + homeScreenVM.selectedImpLinkIds.size,
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
                                text = if (homeScreenVM.selectedLinksID.size + homeScreenVM.selectedFoldersData.size + homeScreenVM.selectedSavedLinkIds.size + homeScreenVM.selectedImpLinkIds.size == 1) " item" else " items selected",
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
                    if (homeScreenVM.selectedLinksID.size + homeScreenVM.selectedFoldersData.size + homeScreenVM.selectedSavedLinkIds.size + homeScreenVM.selectedImpLinkIds.size > 0) {
                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                            homeScreenVM.archiveSelectedMultipleFolders()
                            homeScreenVM.moveMultipleLinksFromLinksTableToArchive()
                            homeScreenVM.moveSelectedSavedAndImpLinksToArchive()
                            homeScreenVM.selectedFoldersData.clear()
                            homeScreenVM.selectedLinksID.clear()
                            homeScreenVM.selectedImpLinkIds.clear()
                            homeScreenVM.selectedSavedLinkIds.clear()
                            homeScreenVM.isSelectionModeEnabled.value = false
                        }) {
                            Icon(imageVector = Icons.Outlined.Archive, contentDescription = null)
                        }
                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                            shouldDeleteDialogBoxAppear.value = true
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteForever,
                                contentDescription = null
                            )
                        }
                    } else {
                        IconButton(
                            modifier = Modifier.pulsateEffect(),
                            onClick = { shouldSortingBottomSheetAppear.value = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Sort,
                                contentDescription = null
                            )
                        }
                    }
                })
            }) {
            Row(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = shelfLazyColumnState,
                    modifier = Modifier
                        .fillMaxHeight()
                        .animateContentSize()
                        .width(if (!homeScreenVM.isSelectionModeEnabled.value && !SettingsPreference.isShelfMinimizedInHomeScreen.value) 80.dp else 0.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(200.dp))
                    }
                    items(shelfData.value) {
                        androidx.compose.material3.NavigationRailItem(
                            modifier = Modifier.rotate(90f),
                            selected = it.id == SettingsPreference.lastSelectedPanelID.longValue,
                            onClick = {
                                coroutineScope.launch {
                                    async {
                                        pagerState.animateScrollToPage(0)
                                    }.await()
                                    SettingsPreference.lastSelectedPanelID.longValue =
                                        it.id
                                    homeScreenVM.changeSelectedShelfFoldersDataForSelectedShelf(
                                        it.id, context
                                    )
                                }
                            },
                            icon = {
                                Column {
                                    Icon(
                                        modifier = Modifier.rotate(180f),
                                        imageVector = if (it.id == SettingsPreference.lastSelectedPanelID.longValue) {
                                            Icons.Filled.ViewArray
                                        } else {
                                            Icons.Outlined.ViewArray
                                        }, contentDescription = null
                                    )
                                }
                            }, label = {
                                Text(
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .rotate(180f)
                                        .width(56.dp)
                                        .padding(bottom = 2.dp),
                                    text = it.shelfName,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center
                                )
                            })
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    item {
                        androidx.compose.material3.NavigationRailItem(
                            modifier = Modifier.rotate(90f),
                            selected = SettingsPreference.lastSelectedPanelID.longValue == (-1).toLong(),
                            onClick = {
                                coroutineScope.launch {
                                    if (shelfData.value.isEmpty() || homeScreenList.isEmpty()) {
                                        SettingsPreference.lastSelectedPanelID.longValue = -1
                                        SettingsPreference.changeSettingPreferenceValue(
                                            intPreferencesKey(SettingsPreferences.LAST_SELECTED_PANEL_ID.name),
                                            context.dataStore,
                                            newValue = -1
                                        )
                                    }
                                    pagerState.animateScrollToPage(0)
                                }.invokeOnCompletion {
                                    SettingsPreference.lastSelectedPanelID.longValue = -1
                                    SettingsPreference.changeSettingPreferenceValue(
                                        intPreferencesKey(SettingsPreferences.LAST_SELECTED_PANEL_ID.name),
                                        context.dataStore,
                                        newValue = -1
                                    )
                                }
                            },
                            icon = {
                                Column {
                                    Icon(
                                        modifier = Modifier.rotate(180f),
                                        imageVector = if (SettingsPreference.lastSelectedPanelID.longValue == (-1).toLong()) {
                                            Icons.Filled.Layers
                                        } else {
                                            Icons.Outlined.Layers
                                        }, contentDescription = null
                                    )
                                }
                            }, label = {
                                Text(
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .rotate(180f)
                                        .width(56.dp)
                                        .padding(bottom = 2.dp),
                                    text = LocalizedStrings.defaultShelf.value,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center
                                )
                            })

                    }
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                modifier = Modifier
                                    .pulsateEffect(0.9f),
                                onClick = {
                                    navController.navigate(NavigationRoutes.SHELF_SCREEN.name)
                                }) {
                                Icon(imageVector = Icons.Outlined.Tune, contentDescription = null)
                            }
                        }
                        Spacer(modifier = Modifier.height(200.dp))
                    }
                }
                if (!homeScreenVM.isSelectionModeEnabled.value && !SettingsPreference.isShelfMinimizedInHomeScreen.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(5.dp)
                            .border(
                                5f.dp, color = MaterialTheme.colorScheme.primary.copy(0.1f),
                                RoundedCornerShape(
                                    topStart = 0.dp,
                                    bottomStart = 0.dp,
                                    topEnd = 5.dp,
                                    bottomEnd = 5.dp
                                )
                            )
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    LazyColumn(modifier = Modifier.padding(it)) {
                        stickyHeader {
                            Column(modifier = Modifier.animateContentSize()) {
                                if (homeScreenList.isNotEmpty() && SettingsPreference.lastSelectedPanelID.longValue != (-1).toLong()) {
                                    ScrollableTabRow(
                                        divider = {},
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        selectedTabIndex = pagerState.currentPage
                                    ) {
                                        homeScreenList.forEachIndexed { index, homeScreenListsElement ->
                                            Tab(
                                                selected = pagerState.currentPage == index,
                                                onClick = {
                                                    coroutineScope.launch {
                                                        pagerState.animateScrollToPage(index)
                                                    }
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
                                } else if (SettingsPreference.lastSelectedPanelID.longValue == (-1).toLong()) {
                                    Column {
                                        ScrollableTabRow(
                                            divider = {},
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            selectedTabIndex = pagerState.currentPage
                                        ) {
                                            homeScreenVM.defaultScreenData.forEachIndexed { index, archiveScreenModal ->
                                                Tab(
                                                    selected = pagerState.currentPage == index,
                                                    onClick = {
                                                        coroutineScope.launch {
                                                            pagerState.animateScrollToPage(index)
                                                        }
                                                    }) {
                                                    Text(
                                                        text = archiveScreenModal.name,
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
                                    }
                                }
                            }
                        }
                    }
                    if (homeScreenList.isNotEmpty() && SettingsPreference.lastSelectedPanelID.longValue != (-1).toLong()) {
                        HorizontalPager(
                            key = {
                                it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            count = homeScreenList.size,
                            state = pagerState
                        ) {
                            ChildHomeScreen(
                                homeScreenType = HomeScreenVM.HomeScreenType.CUSTOM_LIST,
                                navController = navController,
                                folderLinksData = when (SettingsPreference.selectedSortingType.value) {
                                    SortingPreferences.A_TO_Z.name -> {
                                        homeScreenVM.folderLinksSortingRepo
                                            .sortByAToZV10(homeScreenList[it].id)
                                            .collectAsStateWithLifecycle(
                                                initialValue = emptyList()
                                            ).value
                                    }

                                    SortingPreferences.Z_TO_A.name -> {
                                        homeScreenVM.folderLinksSortingRepo
                                            .sortByZToAV10(homeScreenList[it].id)
                                            .collectAsStateWithLifecycle(
                                                initialValue = emptyList()
                                            ).value
                                    }

                                    SortingPreferences.NEW_TO_OLD.name -> {
                                        homeScreenVM.folderLinksSortingRepo
                                            .sortByLatestToOldestV10(homeScreenList[it].id)
                                            .collectAsStateWithLifecycle(
                                                initialValue = emptyList()
                                            ).value
                                    }

                                    SortingPreferences.OLD_TO_NEW.name -> {
                                        homeScreenVM.folderLinksSortingRepo
                                            .sortByOldestToLatestV10(homeScreenList[it].id)
                                            .collectAsStateWithLifecycle(
                                                initialValue = emptyList()
                                            ).value
                                    }

                                    else -> {
                                        homeScreenVM.linksRepo
                                            .getLinksOfThisFolderV10(homeScreenList[it].id)
                                            .collectAsStateWithLifecycle(
                                                initialValue = emptyList()
                                            ).value
                                    }
                                },
                                childFoldersData = when (SettingsPreference.selectedSortingType.value) {
                                    SortingPreferences.A_TO_Z.name -> {
                                        homeScreenVM.subFoldersSortingRepo.sortSubFoldersByAToZ(
                                            homeScreenList[it].id
                                        )
                                            .collectAsStateWithLifecycle(
                                                initialValue = emptyList()
                                            ).value
                                    }

                                    SortingPreferences.Z_TO_A.name -> {
                                        homeScreenVM.subFoldersSortingRepo.sortSubFoldersByZToA(
                                            homeScreenList[it].id
                                        )
                                            .collectAsStateWithLifecycle(
                                                initialValue = emptyList()
                                            ).value
                                    }

                                    SortingPreferences.NEW_TO_OLD.name -> {
                                        homeScreenVM.subFoldersSortingRepo.sortSubFoldersByLatestToOldest(
                                            homeScreenList[it].id
                                        )
                                            .collectAsStateWithLifecycle(
                                                initialValue = emptyList()
                                            ).value
                                    }

                                    SortingPreferences.OLD_TO_NEW.name -> {
                                        homeScreenVM.subFoldersSortingRepo.sortSubFoldersByOldestToLatest(
                                            homeScreenList[it].id
                                        )
                                            .collectAsStateWithLifecycle(
                                                initialValue = emptyList()
                                            ).value
                                    }

                                    else -> {
                                        homeScreenVM.foldersRepo.getChildFoldersOfThisParentID(
                                            homeScreenList[it].id
                                        )
                                            .collectAsStateWithLifecycle(
                                                initialValue = emptyList()
                                            ).value
                                    }
                                },
                                customWebTab = customWebTab
                            )
                        }
                    } else if (SettingsPreference.lastSelectedPanelID.longValue == (-1).toLong()) {
                        HorizontalPager(
                            key = {
                                it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            count = homeScreenVM.defaultScreenData.size,
                            state = pagerState
                        ) {
                            homeScreenVM.defaultScreenData[it].screen(navController, customWebTab)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset {
                                        IntOffset(
                                            cardOffSetX.value.roundToInt(),
                                            cardOffSetY.value.roundToInt()
                                        )
                                    }
                                    .pointerInput(Unit) {
                                        detectDragGestures { change, dragAmount ->
                                            change.consume()
                                            cardOffSetX.value += dragAmount.x
                                            cardOffSetY.value += dragAmount.y
                                        }
                                    }) {
                                Card(
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.onSurface.copy(0.4f)
                                    ),
                                    colors = CardDefaults.cardColors(containerColor = AlertDialogDefaults.containerColor),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 20.dp, end = 20.dp)
                                ) {
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                            .padding(
                                                10.dp
                                            )
                                    ) {
                                        Text(text = "• ")
                                        Text(
                                            text = LocalizedStrings.noFoldersAvailableInThisPanelAddFoldersToBegin.value,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontSize = 18.sp,
                                            lineHeight = 24.sp,
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier
                                                .padding(end = 10.dp)
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                            .padding(
                                                10.dp
                                            )
                                    ) {
                                        Text(text = "• ")
                                        Text(
                                            style = MaterialTheme.typography.titleSmall,
                                            text = LocalizedStrings.youCanFindSavedLinksAndImportantLinksInTheDefaultPanel.value,
                                            fontSize = 18.sp,
                                            lineHeight = 24.sp,
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier
                                                .padding(end = 10.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                            }
                        }
                    }
                }
            }
            if (shouldScreenTransparencyDecreasedBoxVisible.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(0.95f))
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
            SortingBottomSheetParam(
                shouldBottomSheetVisible = shouldSortingBottomSheetAppear,
                onSelectedAComponent = { sortingPreferences, _, _ ->
                    specificCollectionsScreenVM.changeRetrievedData(
                        sortingPreferences = SortingPreferences.valueOf(
                            sortingPreferences.name
                        ),
                        folderID = 0,
                        screenType = SpecificScreenType.SAVED_LINKS_SCREEN
                    )
                    specificCollectionsScreenVM.changeRetrievedData(
                        sortingPreferences = SortingPreferences.valueOf(
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
        AddANewLinkDialogBox(
            shouldDialogBoxAppear = shouldDialogForNewLinkAppear,
            screenType = SpecificScreenType.ROOT_SCREEN,
            onSaveClick = { isAutoDetectSelected: Boolean, webURL: String, title: String, note: String, selectedDefaultFolderName: String?, selectedNonDefaultFolderID: Long? ->
                isDataExtractingForTheLink.value = true
                if (selectedNonDefaultFolderID == (-1).toLong()) {
                    linkoraLog("add in saved links, webURL is $webURL")
                    homeScreenVM.onUiEvent(
                        SpecificCollectionsScreenUIEvent.AddANewLinkInSavedLinks(
                            title, webURL, note, isAutoDetectSelected, onTaskCompleted = {
                                shouldDialogForNewLinkAppear.value = false
                                isDataExtractingForTheLink.value = false
                            }
                        )
                    )
                    return@AddANewLinkDialogBox
                }
                if (selectedNonDefaultFolderID == (-2).toLong()) {
                    linkoraLog("add in imp links, webURL is $webURL")
                    homeScreenVM.onUiEvent(
                        SpecificCollectionsScreenUIEvent.AddANewLinkInImpLinks(
                            onTaskCompleted = {
                                shouldDialogForNewLinkAppear.value = false
                                isDataExtractingForTheLink.value = false
                            },
                            title = title,
                            webURL = webURL,
                            noteForSaving = note,
                            autoDetectTitle = isAutoDetectSelected
                        )
                    )
                    return@AddANewLinkDialogBox
                }
                when {
                    selectedNonDefaultFolderID != null && selectedDefaultFolderName != null -> {
                        linkoraLog("add in folder; id is $selectedNonDefaultFolderID, name is $selectedDefaultFolderName\n webURL is $webURL")
                        homeScreenVM.onUiEvent(
                            SpecificCollectionsScreenUIEvent.AddANewLinkInAFolder(
                                title = title,
                                webURL = webURL,
                                noteForSaving = note,
                                folderID = selectedNonDefaultFolderID,
                                folderName = selectedDefaultFolderName,
                                autoDetectTitle = isAutoDetectSelected,
                                onTaskCompleted = {
                                    shouldDialogForNewLinkAppear.value = false
                                    isDataExtractingForTheLink.value = false
                                }
                            )
                        )
                    }
                }
            },
            isDataExtractingForTheLink = isDataExtractingForTheLink.value,
            onFolderCreateClick = { folderName, folderNote ->
                homeScreenVM.onUiEvent(
                    SpecificCollectionsScreenUIEvent.CreateANewFolder(
                        FoldersTable(folderName = folderName, infoForSaving = folderNote)
                    )
                )
            }
        )

        AddNewFolderDialogBox(
            AddNewFolderDialogBoxParam(
                shouldDialogBoxAppear = shouldDialogForNewFolderAppear,
                inAChildFolderScreen = false,
                onFolderCreateClick = { folderName, folderNote ->
                    homeScreenVM.onUiEvent(
                        SpecificCollectionsScreenUIEvent.CreateANewFolder(
                            FoldersTable(folderName = folderName, infoForSaving = folderNote)
                        )
                    )
                }
            )
        )

        DeleteDialogBox(
            deleteDialogBoxParam = DeleteDialogBoxParam(
                areFoldersSelectable = true,
                shouldDialogBoxAppear = shouldDeleteDialogBoxAppear,
                deleteDialogBoxType = DataDialogBoxType.SELECTED_DATA,
                onDeleteClick = {
                    homeScreenVM.onDeleteMultipleSelectedFolders()
                    homeScreenVM.onDeleteMultipleSelectedLinks()
                    homeScreenVM.deleteSelectedSavedAndImpLinks()
                    homeScreenVM.isSelectionModeEnabled.value = false
                    homeScreenVM.selectedFoldersData.clear()
                    homeScreenVM.selectedLinksID.clear()
                    homeScreenVM.selectedImpLinkIds.clear()
                    homeScreenVM.selectedSavedLinkIds.clear()
                }
            )
        )
    }
    LaunchedEffect(
        key1 = SettingsPreference.lastSelectedPanelID.longValue,
        key2 = shelfData.value.size
    ) {
        if (SettingsPreference.lastSelectedPanelID.longValue.toInt() != -1 && HomeScreenVM.initialStart && shelfData.value.isNotEmpty()) {
            shelfData.value.find {
                it.id == SettingsPreference.lastSelectedPanelID.longValue
            }?.let {
                shelfLazyColumnState.animateScrollToItem(shelfData.value.indexOf(it))
            }

            homeScreenVM.changeSelectedShelfFoldersDataForSelectedShelf(
                (SettingsPreference.readSettingPreferenceValue(
                    intPreferencesKey(
                        SettingsPreferences.LAST_SELECTED_PANEL_ID.name
                    ), context.dataStore
                ) ?: -1).toLong(), context
            )
            HomeScreenVM.initialStart = false
        }
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
        } else if (homeScreenVM.isSelectionModeEnabled.value) {
            homeScreenVM.isSelectionModeEnabled.value = false
            homeScreenVM.areAllLinksChecked.value = false
            homeScreenVM.areAllFoldersChecked.value = false
            homeScreenVM.selectedLinksID.clear()
            homeScreenVM.selectedImpLinkIds.clear()
            homeScreenVM.selectedSavedLinkIds.clear()
            homeScreenVM.selectedFoldersData.clear()
        } else {
            activity?.moveTaskToBack(true)
        }
    }
}