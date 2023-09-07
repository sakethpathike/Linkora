package com.sakethh.linkora.screens.home

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sort
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.btmSheet.NewLinkBtmSheet
import com.sakethh.linkora.btmSheet.OptionsBtmSheetType
import com.sakethh.linkora.btmSheet.OptionsBtmSheetUI
import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.btmSheet.SortingBottomSheetUI
import com.sakethh.linkora.customComposables.AddNewFolderDialogBox
import com.sakethh.linkora.customComposables.AddNewLinkDialogBox
import com.sakethh.linkora.customComposables.DataDialogBoxType
import com.sakethh.linkora.customComposables.DeleteDialogBox
import com.sakethh.linkora.customComposables.FloatingActionBtn
import com.sakethh.linkora.customComposables.LinkCard
import com.sakethh.linkora.customComposables.LinkUIComponent
import com.sakethh.linkora.customComposables.RenameDialogBox
import com.sakethh.linkora.customWebTab.openInWeb
import com.sakethh.linkora.localDB.ImportantLinks
import com.sakethh.linkora.localDB.RecentlyVisited
import com.sakethh.linkora.screens.DataEmptyScreen
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreenType
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val homeScreenVM: HomeScreenVM = viewModel()
    val recentlySavedImpsLinksData = homeScreenVM.recentlySavedImpLinksData.collectAsState().value
    val recentlySavedLinksData = homeScreenVM.recentlySavedLinksData.collectAsState().value
    val recentlyVisitedLinksData = homeScreenVM.historyLinksData.collectAsState().value
    val btmModalSheetState = rememberModalBottomSheetState()
    val btmModalSheetStateForSavingLinks =
        rememberModalBottomSheetState()
    val selectedCardType = rememberSaveable {
        mutableStateOf(HomeScreenBtmSheetType.RECENT_IMP_SAVES.name)
    }
    val shouldOptionsBtmModalSheetBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldRenameDialogBoxBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val activity = LocalContext.current as? Activity
    val isMainFabRotated = rememberSaveable {
        mutableStateOf(false)
    }
    val rotationAnimation = remember {
        Animatable(0f)
    }
    val shouldScreenTransparencyDecreasedBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val selectedWebURL = rememberSaveable {
        mutableStateOf("")
    }
    val selectedURLNote = rememberSaveable {
        mutableStateOf("")
    }
    val shouldDeleteBoxAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val optionsBtmSheetVM: OptionsBtmSheetVM = viewModel()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val shouldDialogForNewLinkAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldDialogForNewFolderAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldBtmSheetForNewLinkAdditionBeEnabled = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldSortingBottomSheetAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val sortingBtmSheetState = rememberModalBottomSheetState()
    if (shouldDialogForNewFolderAppear.value || shouldDialogForNewLinkAppear.value) {
        shouldScreenTransparencyDecreasedBoxVisible.value = false
        isMainFabRotated.value = false
    }
    val specificScreenVM = viewModel<SpecificScreenVM>()
    LinkoraTheme {
        Scaffold(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            floatingActionButton = {
                FloatingActionBtn(
                    newLinkBottomModalSheetState = btmModalSheetStateForSavingLinks,
                    shouldBtmSheetForNewLinkAdditionBeEnabled = shouldBtmSheetForNewLinkAdditionBeEnabled,
                    shouldScreenTransparencyDecreasedBoxVisible = shouldScreenTransparencyDecreasedBoxVisible,
                    shouldDialogForNewFolderAppear = shouldDialogForNewFolderAppear,
                    shouldDialogForNewLinkAppear = shouldDialogForNewLinkAppear,
                    isMainFabRotated = isMainFabRotated,
                    rotationAnimation = rotationAnimation
                )
            },
            floatingActionButtonPosition = FabPosition.End
        ) {
            val currentPhaseOfTheDay =
                rememberSaveable(inputs = arrayOf(homeScreenVM.currentPhaseOfTheDay.value)) {
                    homeScreenVM.currentPhaseOfTheDay.value
                }
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                item {
                    TopAppBar(title = {
                        Text(
                            text = currentPhaseOfTheDay,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 24.sp
                        )
                    })
                }
                item {
                    Divider(
                        thickness = 0.25.dp,
                        modifier = Modifier.padding(start = 15.dp, end = 30.dp)
                    )
                }
                if (recentlySavedLinksData.isNotEmpty()) {
                    item {
                        Text(
                            text = "Recently Saved Links",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 15.dp, top = 20.dp)
                        )
                    }
                    item {
                        LazyRow(
                            modifier = Modifier
                                .padding(top = 15.dp)
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            item {
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            items(recentlySavedLinksData) {
                                LinkCard(
                                    title = it.title,
                                    webBaseURL = it.webURL,
                                    imgURL = it.imgURL,
                                    onMoreIconClick = {
                                        HomeScreenVM.tempImpLinkData.webURL = it.webURL
                                        HomeScreenVM.tempImpLinkData.baseURL = it.baseURL
                                        HomeScreenVM.tempImpLinkData.imgURL = it.imgURL
                                        HomeScreenVM.tempImpLinkData.title = it.title
                                        HomeScreenVM.tempImpLinkData.infoForSaving =
                                            it.infoForSaving
                                        selectedURLNote.value = it.infoForSaving
                                        selectedWebURL.value = it.webURL
                                        shouldOptionsBtmModalSheetBeVisible.value = true
                                        selectedCardType.value =
                                            HomeScreenBtmSheetType.RECENT_SAVES.name
                                        coroutineScope.launch {
                                            awaitAll(async {
                                                optionsBtmSheetVM.updateArchiveLinkCardData(url = it.webURL)
                                            }, async {
                                                optionsBtmSheetVM.updateImportantCardData(url = it.webURL)
                                            })
                                        }
                                    },
                                    webURL = it.webURL,
                                    onCardClick = {
                                        coroutineScope.launch {
                                            openInWeb(
                                                recentlyVisitedData = RecentlyVisited(
                                                    title = it.title,
                                                    webURL = it.webURL,
                                                    baseURL = it.baseURL,
                                                    imgURL = it.imgURL,
                                                    infoForSaving = it.infoForSaving
                                                ),
                                                context = context,
                                                uriHandler = uriHandler
                                            )
                                        }
                                    },
                                    onForceOpenInExternalBrowserClicked = {
                                        homeScreenVM.onForceOpenInExternalBrowser(
                                            RecentlyVisited(
                                                title = it.title,
                                                webURL = it.webURL,
                                                baseURL = it.baseURL,
                                                imgURL = it.imgURL,
                                                infoForSaving = it.infoForSaving
                                            )
                                        )
                                    }
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                        }
                    }
                    item {
                        Divider(
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp),
                            color = MaterialTheme.colorScheme.outline.copy(0.25f)
                        )
                    }
                }
                if (recentlySavedImpsLinksData.isNotEmpty()) {
                    item {
                        Text(
                            text = "Recent Important(s)",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 15.dp, top = 15.dp)
                        )
                    }
                    item {
                        LazyRow(
                            modifier = Modifier
                                .padding(top = 15.dp)
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            item {
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            items(recentlySavedImpsLinksData) {
                                LinkCard(
                                    title = it.title,
                                    webBaseURL = it.webURL,
                                    imgURL = it.imgURL,
                                    onMoreIconClick = {
                                        HomeScreenVM.tempImpLinkData.webURL = it.webURL
                                        HomeScreenVM.tempImpLinkData.baseURL = it.baseURL
                                        HomeScreenVM.tempImpLinkData.imgURL = it.imgURL
                                        HomeScreenVM.tempImpLinkData.title = it.title
                                        HomeScreenVM.tempImpLinkData.infoForSaving =
                                            it.infoForSaving
                                        selectedURLNote.value = it.infoForSaving
                                        selectedWebURL.value = it.webURL
                                        shouldOptionsBtmModalSheetBeVisible.value = true
                                        selectedCardType.value =
                                            HomeScreenBtmSheetType.RECENT_IMP_SAVES.name
                                        coroutineScope.launch {
                                            awaitAll(async {
                                                optionsBtmSheetVM.updateArchiveLinkCardData(url = it.webURL)
                                            }, async {
                                                optionsBtmSheetVM.updateImportantCardData(url = it.webURL)
                                            })
                                        }
                                    },
                                    webURL = it.webURL,
                                    onCardClick = {
                                        coroutineScope.launch {
                                            openInWeb(
                                                recentlyVisitedData = RecentlyVisited(
                                                    title = it.title,
                                                    webURL = it.webURL,
                                                    baseURL = it.baseURL,
                                                    imgURL = it.imgURL,
                                                    infoForSaving = it.infoForSaving
                                                ),
                                                context = context,
                                                uriHandler = uriHandler
                                            )
                                        }
                                    }, onForceOpenInExternalBrowserClicked = {
                                        homeScreenVM.onForceOpenInExternalBrowser(
                                            RecentlyVisited(
                                                title = it.title,
                                                webURL = it.webURL,
                                                baseURL = it.baseURL,
                                                imgURL = it.imgURL,
                                                infoForSaving = it.infoForSaving
                                            )
                                        )
                                    }
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                        }
                    }
                    item {
                        Divider(
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp),
                            color = MaterialTheme.colorScheme.outline.copy(0.25f)
                        )
                    }
                }
                if (recentlyVisitedLinksData.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .clickable {
                                    shouldSortingBottomSheetAppear.value = true
                                }
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "History",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(start = 15.dp)
                            )
                            IconButton(onClick = { shouldSortingBottomSheetAppear.value = true }) {
                                Icon(imageVector = Icons.Outlined.Sort, contentDescription = null)
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                    items(recentlyVisitedLinksData) {
                        LinkUIComponent(
                            title = it.title,
                            webBaseURL = it.baseURL,
                            imgURL = it.imgURL,
                            onMoreIconCLick = {
                                HomeScreenVM.tempImpLinkData.webURL = it.webURL
                                HomeScreenVM.tempImpLinkData.baseURL = it.baseURL
                                HomeScreenVM.tempImpLinkData.imgURL = it.imgURL
                                HomeScreenVM.tempImpLinkData.title = it.title
                                HomeScreenVM.tempImpLinkData.infoForSaving = it.infoForSaving
                                selectedURLNote.value = it.infoForSaving
                                selectedWebURL.value = it.webURL
                                selectedCardType.value = HomeScreenBtmSheetType.RECENT_VISITS.name
                                shouldOptionsBtmModalSheetBeVisible.value = true
                                coroutineScope.launch {
                                    awaitAll(async {
                                        optionsBtmSheetVM.updateArchiveLinkCardData(url = it.webURL)
                                    }, async {
                                        optionsBtmSheetVM.updateImportantCardData(url = it.webURL)
                                    })
                                }
                            },
                            onLinkClick = {
                                coroutineScope.launch {
                                    openInWeb(
                                        recentlyVisitedData = RecentlyVisited(
                                            title = it.title,
                                            webURL = it.webURL,
                                            baseURL = it.baseURL,
                                            imgURL = it.imgURL,
                                            infoForSaving = it.infoForSaving
                                        ),
                                        context = context,
                                        uriHandler = uriHandler
                                    )
                                }
                            },
                            webURL = it.webURL,
                            onForceOpenInExternalBrowserClicked = {
                                homeScreenVM.onForceOpenInExternalBrowser(
                                    RecentlyVisited(
                                        title = it.title,
                                        webURL = it.webURL,
                                        baseURL = it.baseURL,
                                        imgURL = it.imgURL,
                                        infoForSaving = it.infoForSaving
                                    )
                                )
                            }
                        )
                    }
                }
                if (recentlySavedImpsLinksData.isEmpty() && recentlySavedLinksData.isEmpty() && recentlyVisitedLinksData.isEmpty()) {
                    item {
                        DataEmptyScreen(text = "Welcome back to Linkora! No recent activity related to saving links has been found. Your recent links will show up here.")
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(200.dp))
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
        AddNewLinkDialogBox(
            shouldDialogBoxAppear = shouldDialogForNewLinkAppear,
            screenType = SpecificScreenType.ROOT_SCREEN,
            specificFolderName = "Tea || Coffee ?"
        )
        AddNewFolderDialogBox(
            shouldDialogBoxAppear = shouldDialogForNewFolderAppear
        )
        OptionsBtmSheetUI(
            btmModalSheetState = btmModalSheetState,
            shouldBtmModalSheetBeVisible = shouldOptionsBtmModalSheetBeVisible,
            coroutineScope = coroutineScope,
            btmSheetFor = OptionsBtmSheetType.LINK,
            onRenameClick = {
                coroutineScope.launch {
                    btmModalSheetState.hide()
                }
                shouldRenameDialogBoxBeVisible.value = true
            },
            onDeleteCardClick = {
                shouldDeleteBoxAppear.value = true
            },
            onImportantLinkAdditionInTheTable = {
                specificScreenVM.onImportantLinkAdditionInTheTable(
                    context, ImportantLinks(
                        title = HomeScreenVM.tempImpLinkData.title,
                        webURL = HomeScreenVM.tempImpLinkData.webURL,
                        baseURL = HomeScreenVM.tempImpLinkData.baseURL,
                        imgURL = HomeScreenVM.tempImpLinkData.imgURL,
                        infoForSaving = HomeScreenVM.tempImpLinkData.infoForSaving
                    )
                )
            },
            importantLinks = null,
            onArchiveClick = {
                homeScreenVM.onArchiveClick(
                    selectedCardType = HomeScreenBtmSheetType.valueOf(
                        selectedCardType.value
                    ), context
                )
            }, noteForSaving = selectedURLNote.value,
            onNoteDeleteCardClick = {
                homeScreenVM.onNoteDeleteCardClick(
                    selectedCardType = HomeScreenBtmSheetType.valueOf(
                        selectedCardType.value
                    ), selectedWebURL = selectedWebURL.value, context = context
                )
            },
            folderName = "",
            linkTitle = HomeScreenVM.tempImpLinkData.title
        )
    }
    DeleteDialogBox(shouldDialogBoxAppear = shouldDeleteBoxAppear,
        deleteDialogBoxType = DataDialogBoxType.LINK,
        onDeleteClick = {
            homeScreenVM.onDeleteClick(
                selectedCardType = HomeScreenBtmSheetType.valueOf(
                    selectedCardType.value
                ),
                selectedWebURL = selectedWebURL.value,
                context = context,
                shouldDeleteBoxAppear = shouldDeleteBoxAppear
            )
        })
    RenameDialogBox(shouldDialogBoxAppear = shouldRenameDialogBoxBeVisible,
        coroutineScope = coroutineScope,
        webURLForTitle = selectedWebURL.value,
        existingFolderName = "",
        renameDialogBoxFor = OptionsBtmSheetType.LINK,
        onNoteChangeClickForLinks = { webURL: String, newNote: String ->
            homeScreenVM.onNoteChangeClickForLinks(
                selectedCardType = HomeScreenBtmSheetType.valueOf(
                    selectedCardType.value
                ), webURL, newNote
            )
        },
        onTitleChangeClickForLinks = { webURL: String, newTitle: String ->
            homeScreenVM.onTitleChangeClickForLinks(
                selectedCardType = HomeScreenBtmSheetType.valueOf(
                    selectedCardType.value
                ), webURL, newTitle
            )
        })
    NewLinkBtmSheet(
        btmSheetState = btmModalSheetStateForSavingLinks,
        _inIntentActivity = false,
        screenType = SpecificScreenType.ROOT_SCREEN,
        shouldUIBeVisible = shouldBtmSheetForNewLinkAdditionBeEnabled
    )
    SortingBottomSheetUI(
        shouldBottomSheetVisible = shouldSortingBottomSheetAppear,
        onSelectedAComponent = {
            homeScreenVM.changeHistoryRetrievedData(sortingPreferences = it)
        },
        bottomModalSheetState = sortingBtmSheetState
    )
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
        } else {
            activity?.finish()
        }
    }
}

enum class HomeScreenBtmSheetType {
    RECENT_SAVES, RECENT_IMP_SAVES, RECENT_VISITS
}