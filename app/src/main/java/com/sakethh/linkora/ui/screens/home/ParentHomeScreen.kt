package com.sakethh.linkora.ui.screens.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.sakethh.linkora.ui.bottomSheets.shelf.ShelfBtmSheet
import com.sakethh.linkora.ui.bottomSheets.sorting.SortingBottomSheetParam
import com.sakethh.linkora.ui.bottomSheets.sorting.SortingBottomSheetUI
import com.sakethh.linkora.ui.bottomSheets.sorting.SortingBtmSheetType
import com.sakethh.linkora.ui.commonComposables.AddNewFolderDialogBox
import com.sakethh.linkora.ui.commonComposables.AddNewFolderDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.AddNewLinkDialogBox
import com.sakethh.linkora.ui.commonComposables.DataDialogBoxType
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBox
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.FloatingActionBtn
import com.sakethh.linkora.ui.commonComposables.FloatingActionBtnParam
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenUIEvent
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenVM
import com.sakethh.linkora.ui.screens.collections.specific.SpecificScreenType
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(
    ExperimentalPagerApi::class, ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun ParentHomeScreen(navController: NavController) {
    val pagerState = rememberPagerState()
    val homeScreenVM: HomeScreenVM = viewModel()
    val specificCollectionsScreenVM: SpecificCollectionsScreenVM = viewModel()
    val coroutineScope = rememberCoroutineScope()
    val shouldSortingBottomSheetAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldShelfBottomSheetAppear = rememberSaveable {
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
    val scaleState = rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    val selectedShelfID = rememberSaveable {
        mutableLongStateOf(-1)
    }
    val shelfData = homeScreenVM.shelfData.collectAsStateWithLifecycle().value
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
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .animateContentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (!homeScreenVM.isSelectionModeEnabled.value) {
                        Column {
                            Spacer(modifier = Modifier.height(100.dp))
                            shelfData.forEach {
                                androidx.compose.material3.NavigationRailItem(
                                    modifier = Modifier.rotate(90f),
                                    selected = it.id == selectedShelfID.longValue,
                                    onClick = {
                                        selectedShelfID.longValue = it.id
                                        homeScreenVM.changeSelectedShelfFoldersDataForSelectedShelf(
                                            it.id
                                        )
                                    },
                                    icon = {
                                        Column {
                                            Icon(
                                                modifier = Modifier.rotate(180f),
                                                imageVector = if (it.id == selectedShelfID.longValue) {
                                                    Icons.Filled.Folder
                                                } else {
                                                    Icons.Outlined.Folder
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
                            androidx.compose.material3.NavigationRailItem(
                                modifier = Modifier.rotate(90f),
                                selected = (-1).toLong() == selectedShelfID.longValue,
                                onClick = {
                                    selectedShelfID.longValue = (-1).toLong()
                                },
                                icon = {
                                    Column {
                                        Icon(
                                            modifier = Modifier.rotate(180f),
                                            imageVector = if (selectedShelfID.longValue == (-1).toLong()) {
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
                                        text = "Default",
                                        style = MaterialTheme.typography.titleSmall,
                                        maxLines = 1,
                                        textAlign = TextAlign.Center
                                    )
                                })
                            Spacer(modifier = Modifier.height(20.dp))
                            IconButton(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .pulsateEffect(0.9f),
                                onClick = {
                                    shouldShelfBottomSheetAppear.value = true
                                }) {
                                Icon(imageVector = Icons.Outlined.Tune, contentDescription = null)
                            }
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    LazyColumn(modifier = Modifier.padding(it)) {
                        stickyHeader {
                            Column(modifier = Modifier.animateContentSize()) {
                                if (homeScreenList.isNotEmpty() && selectedShelfID.longValue != (-1).toLong()) {
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
                                } else if (selectedShelfID.longValue == (-1).toLong()) {
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
                                                        }.start()
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
                    if (homeScreenList.isNotEmpty() && selectedShelfID.longValue != (-1).toLong()) {
                        HorizontalPager(
                            key = {
                                it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            count = homeScreenList.size,
                            state = pagerState
                        ) {
                            TODO()
                            /*ChildHomeScreen(
                                homeScreenType = HomeScreenVM.HomeScreenType.CUSTOM_LIST,
                                navController = navController,
                                folderLinksData = when (SettingsScreenVM.Settings.selectedSortingType.value) {
                                    SettingsScreenVM.SortingPreferences.A_TO_Z.name -> {
                                        LocalDatabase.localDB.regularFolderLinksSorting()
                                            .sortByAToZV10(homeScreenList[it].id)
                                            .collectAsStateWithLifecycle(
                                                initialValue = emptyList()
                                            ).value
                                    }

                                    SettingsScreenVM.SortingPreferences.Z_TO_A.name -> {
                                        LocalDatabase.localDB.regularFolderLinksSorting()
                                            .sortByZToAV10(homeScreenList[it].id)
                                            .collectAsStateWithLifecycle(
                                                initialValue = emptyList()
                                            ).value
                                    }

                                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD.name -> {
                                        LocalDatabase.localDB.regularFolderLinksSorting()
                                            .sortByLatestToOldestV10(homeScreenList[it].id)
                                            .collectAsStateWithLifecycle(
                                                initialValue = emptyList()
                                            ).value
                                    }

                                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW.name -> {
                                        LocalDatabase.localDB.regularFolderLinksSorting()
                                            .sortByOldestToLatestV10(homeScreenList[it].id)
                                            .collectAsStateWithLifecycle(
                                                initialValue = emptyList()
                                            ).value
                                    }

                                    else -> {
                                        LocalDatabase.localDB.readDao()
                                            .getLinksOfThisFolderV10(homeScreenList[it].id)
                                            .collectAsStateWithLifecycle(
                                                initialValue = emptyList()
                                            ).value
                                    }
                                },
                                childFoldersData = when (SettingsScreenVM.Settings.selectedSortingType.value) {
                                    SettingsScreenVM.SortingPreferences.A_TO_Z.name -> {
                                        LocalDatabase.localDB.subFoldersSortingDao()
                                            .sortSubFoldersByAToZ(homeScreenList[it].id)
                                            .collectAsStateWithLifecycle(
                                                initialValue = emptyList()
                                            ).value
                                    }

                                    SettingsScreenVM.SortingPreferences.Z_TO_A.name -> {
                                        LocalDatabase.localDB.subFoldersSortingDao()
                                            .sortSubFoldersByZToA(homeScreenList[it].id)
                                            .collectAsStateWithLifecycle(
                                                initialValue = emptyList()
                                            ).value
                                    }

                                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD.name -> {
                                        LocalDatabase.localDB.subFoldersSortingDao()
                                            .sortSubFoldersByLatestToOldest(homeScreenList[it].id)
                                            .collectAsStateWithLifecycle(
                                                initialValue = emptyList()
                                            ).value
                                    }

                                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW.name -> {
                                        LocalDatabase.localDB.subFoldersSortingDao()
                                            .sortSubFoldersByOldestToLatest(homeScreenList[it].id)
                                            .collectAsStateWithLifecycle(
                                                initialValue = emptyList()
                                            ).value
                                    }

                                    else -> {
                                        LocalDatabase.localDB.readDao()
                                            .getChildFoldersOfThisParentID(homeScreenList[it].id)
                                            .collectAsStateWithLifecycle(
                                                initialValue = emptyList()
                                            ).value
                                    }
                                },
                            )*/
                        }
                    } else if (selectedShelfID.longValue == (-1).toLong()) {
                        HorizontalPager(
                            key = {
                                it
                            },
                            modifier = Modifier.fillMaxWidth(),
                            count = homeScreenVM.defaultScreenData.size,
                            state = pagerState
                        ) {
                            homeScreenVM.defaultScreenData[it].screen(navController)
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
                                            change.consumeAllChanges()
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
                                            text = buildAnnotatedString {
                                                append("To add folders into this shelf, click on the ")
                                                appendInlineContent("tuneIcon")
                                                append(" at the ")
                                                withStyle(
                                                    style = SpanStyle(
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                ) {
                                                    append("bottom of the Shelf")
                                                }
                                                append(".")
                                            },
                                            inlineContent = mapOf(
                                                Pair("tuneIcon", InlineTextContent(
                                                    Placeholder(
                                                        22.sp, 22.sp,
                                                        PlaceholderVerticalAlign.TextCenter
                                                    )
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Tune,
                                                        contentDescription = null
                                                    )
                                                })
                                            ),
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
                                            text = buildAnnotatedString {
                                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("Saved Links")
                                                }
                                                append(" and ")
                                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("Important Links")
                                                }
                                                append(" can be accessed from the ")
                                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("Default Shelf ")
                                                }
                                                append("(")
                                                appendInlineContent("defaultSectionIcon")
                                                append(").")
                                            },
                                            inlineContent = mapOf(
                                                Pair(
                                                    "defaultSectionIcon",
                                                    InlineTextContent(
                                                        Placeholder(
                                                            22.sp, 22.sp,
                                                            PlaceholderVerticalAlign.TextCenter
                                                        )
                                                    ) {
                                                        Icon(
                                                            modifier = Modifier.rotate(-90f),
                                                            imageVector = Icons.Outlined.Layers,
                                                            contentDescription = null
                                                        )
                                                    }
                                                )
                                            ),
                                            style = MaterialTheme.typography.titleSmall,
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
            SortingBottomSheetParam(
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
            onSaveClick = { isAutoDetectSelected: Boolean, webURL: String, title: String, note: String, selectedDefaultFolderName: String?, selectedNonDefaultFolderID: Long? ->
                isDataExtractingForTheLink.value = true
                if (selectedDefaultFolderName == "Saved Links") {
                    homeScreenVM.onUiEvent(SpecificCollectionsScreenUIEvent.AddANewLinkInSavedLinks(
                        title = title,
                        webURL = webURL,
                        noteForSaving = note,
                        autoDetectTitle = isAutoDetectSelected,
                        onTaskCompleted = {
                            shouldDialogForNewLinkAppear.value = false
                            isDataExtractingForTheLink.value = false
                        }
                    ))
                }
                if (selectedDefaultFolderName == "Important Links") {
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
                }
                when {
                    selectedDefaultFolderName != "Important Links" && selectedDefaultFolderName != "Saved Links" -> {
                        if (selectedNonDefaultFolderID != null && selectedDefaultFolderName != null) {
                            homeScreenVM.onUiEvent(SpecificCollectionsScreenUIEvent.AddANewLinkInAFolder(
                                title = title,
                                webURL = webURL,
                                noteForSaving = note,
                                parentFolderID = selectedNonDefaultFolderID,
                                folderName = selectedDefaultFolderName,
                                autoDetectTitle = isAutoDetectSelected,
                                onTaskCompleted = {
                                    shouldDialogForNewLinkAppear.value = false
                                    isDataExtractingForTheLink.value = false
                                }
                            ))
                        }
                    }
                }
            },
            isDataExtractingForTheLink = isDataExtractingForTheLink.value,
            onFolderCreateClick = { folderName, folderNote -> }
        )

        AddNewFolderDialogBox(
            AddNewFolderDialogBoxParam(
                shouldDialogBoxAppear = shouldDialogForNewFolderAppear,
                inAChildFolderScreen = false,
                onFolderCreateClick = { folderName, folderNote -> }
            )
        )

        ShelfBtmSheet(isBtmSheetVisible = shouldShelfBottomSheetAppear)
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