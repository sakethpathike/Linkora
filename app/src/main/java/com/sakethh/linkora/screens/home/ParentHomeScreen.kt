package com.sakethh.linkora.screens.home

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Layers
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.sakethh.linkora.btmSheet.NewLinkBtmSheet
import com.sakethh.linkora.btmSheet.NewLinkBtmSheetUIParam
import com.sakethh.linkora.btmSheet.ShelfBtmSheet
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
import com.sakethh.linkora.customComposables.pulsateEffect
import com.sakethh.linkora.localDB.LocalDataBase
import com.sakethh.linkora.localDB.commonVMs.CreateVM
import com.sakethh.linkora.localDB.commonVMs.ReadVM
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificCollectionsScreenVM
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreenType
import com.sakethh.linkora.screens.settings.SettingsScreenVM
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
    val createVM: CreateVM = viewModel()
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
    val readVM: ReadVM = viewModel()
    val homeScreenList = readVM.selectedShelfFoldersForSelectedShelf.collectAsState().value
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
    val selectedShelfName = remember {
        mutableStateOf("Default")
    }
    val selectedShelfID = rememberSaveable {
        mutableLongStateOf(-1)
    }
    val shelfData = homeScreenVM.shelfData.collectAsState().value
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
                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                            homeScreenVM.archiveMultipleFolders()
                            homeScreenVM.moveMultipleLinksFromLinksTableToArchive()
                            homeScreenVM.selectedFoldersData.clear()
                            homeScreenVM.selectedLinksID.clear()
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
                        if (homeScreenList.isNotEmpty()) {
                            IconButton(
                                modifier = Modifier.pulsateEffect(),
                                onClick = { shouldSortingBottomSheetAppear.value = true }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.Sort,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                })
            }) {

            val widthOfShelfNavigation = remember {
                mutableStateOf(0.dp)
            }
            val localDensity = LocalDensity.current
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .onGloballyPositioned {
                            with(localDensity) {
                                widthOfShelfNavigation.value = it.size.width.toDp()
                            }
                        }
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(100.dp))
                        shelfData.forEach {
                            androidx.compose.material3.NavigationRailItem(
                                modifier = Modifier.rotate(90f),
                                selected = it.shelfName == selectedShelfName.value,
                                onClick = {
                                    selectedShelfName.value = it.shelfName
                                    selectedShelfID.longValue = it.id
                                    readVM.changeSelectedShelfFoldersDataForSelectedShelf(it.id)
                                },
                                icon = {
                                    Column {
                                        Icon(
                                            modifier = Modifier.rotate(180f),
                                            imageVector = if (it.shelfName == selectedShelfName.value) {
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
                            selected = "Default" == selectedShelfName.value,
                            onClick = {
                                selectedShelfName.value = "Default"
                            },
                            icon = {
                                Column {
                                    Icon(
                                        modifier = Modifier.rotate(180f),
                                        imageVector = if ("Default" == selectedShelfName.value) {
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

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    LazyColumn(modifier = Modifier.padding(it)) {
                        stickyHeader {
                                Column(modifier = Modifier.animateContentSize()) {
                                    if (homeScreenList.isNotEmpty() && selectedShelfName.value != "Default") {
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
                                        HorizontalDivider(
                                            modifier = Modifier.fillMaxWidth(),
                                            thickness = 1.dp
                                        )
                                    } else if (selectedShelfName.value == "Default") {
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
                                            HorizontalDivider(
                                                modifier = Modifier.fillMaxWidth(),
                                                thickness = 1.dp
                                            )
                                        }
                                    }
                                }
                        }
                    }
                    if (homeScreenList.isNotEmpty() && selectedShelfName.value != "Default") {
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
                                folderLinksData = when (SettingsScreenVM.Settings.selectedSortingType.value) {
                                    SettingsScreenVM.SortingPreferences.A_TO_Z.name -> {
                                        LocalDataBase.localDB.regularFolderLinksSorting()
                                            .sortByAToZV10(homeScreenList[it].id).collectAsState(
                                                initial = emptyList()
                                            ).value
                                    }

                                    SettingsScreenVM.SortingPreferences.Z_TO_A.name -> {
                                        LocalDataBase.localDB.regularFolderLinksSorting()
                                            .sortByZToAV10(homeScreenList[it].id).collectAsState(
                                                initial = emptyList()
                                            ).value
                                    }

                                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD.name -> {
                                        LocalDataBase.localDB.regularFolderLinksSorting()
                                            .sortByLatestToOldestV10(homeScreenList[it].id)
                                            .collectAsState(
                                                initial = emptyList()
                                            ).value
                                    }

                                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW.name -> {
                                        LocalDataBase.localDB.regularFolderLinksSorting()
                                            .sortByOldestToLatestV10(homeScreenList[it].id)
                                            .collectAsState(
                                                initial = emptyList()
                                            ).value
                                    }

                                    else -> {
                                        LocalDataBase.localDB.readDao()
                                            .getLinksOfThisFolderV10(homeScreenList[it].id)
                                            .collectAsState(
                                                initial = emptyList()
                                            ).value
                                    }
                                },
                                childFoldersData = when (SettingsScreenVM.Settings.selectedSortingType.value) {
                                    SettingsScreenVM.SortingPreferences.A_TO_Z.name -> {
                                        LocalDataBase.localDB.subFoldersSortingDao()
                                            .sortSubFoldersByAToZ(homeScreenList[it].id)
                                            .collectAsState(
                                                initial = emptyList()
                                            ).value
                                    }

                                    SettingsScreenVM.SortingPreferences.Z_TO_A.name -> {
                                        LocalDataBase.localDB.subFoldersSortingDao()
                                            .sortSubFoldersByZToA(homeScreenList[it].id)
                                            .collectAsState(
                                                initial = emptyList()
                                            ).value
                                    }

                                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD.name -> {
                                        LocalDataBase.localDB.subFoldersSortingDao()
                                            .sortSubFoldersByLatestToOldest(homeScreenList[it].id)
                                            .collectAsState(
                                                initial = emptyList()
                                            ).value
                                    }

                                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW.name -> {
                                        LocalDataBase.localDB.subFoldersSortingDao()
                                            .sortSubFoldersByOldestToLatest(homeScreenList[it].id)
                                            .collectAsState(
                                                initial = emptyList()
                                            ).value
                                    }

                                    else -> {
                                        LocalDataBase.localDB.readDao()
                                            .getChildFoldersOfThisParentID(homeScreenList[it].id)
                                            .collectAsState(
                                                initial = emptyList()
                                            ).value
                                    }
                                },
                            )
                        }
                    } else if (selectedShelfName.value == "Default") {
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
                                            buildAnnotatedString {
                                                append("Access and manage folders directly from the shelf itself by clicking the ")
                                                withStyle(
                                                    style = SpanStyle(
                                                        fontWeight = FontWeight.Bold,
                                                        textDecoration = TextDecoration.Underline
                                                    )
                                                ) {
                                                    append("tune icon in the bottom of Shelf")
                                                }
                                                append(" to customize your home screen UI.")
                                            },
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
                                            text = "Please note that folders need to be created by you or already exist to be shown in the list.",
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
                                                append("Saved and Important Links can be accessed from the ")
                                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("Default")
                                                }
                                                append(" section on the left-side shelf.")
                                            },
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

        ShelfBtmSheet(isBtmSheetVisible = shouldShelfBottomSheetAppear)

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
                deleteDialogBoxType = DataDialogBoxType.SELECTED_DATA,
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