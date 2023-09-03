package com.sakethh.linkora.screens.home

import android.annotation.SuppressLint
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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.btmSheet.NewLinkBtmSheet
import com.sakethh.linkora.btmSheet.OptionsBtmSheetType
import com.sakethh.linkora.btmSheet.OptionsBtmSheetUI
import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.customWebTab.openInWeb
import com.sakethh.linkora.localDB.ArchivedLinks
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.localDB.ImportantLinks
import com.sakethh.linkora.localDB.RecentlyVisited
import com.sakethh.linkora.screens.DataEmptyScreen
import com.sakethh.linkora.screens.collections.specificScreen.SpecificScreenType
import com.sakethh.linkora.screens.home.composables.AddNewFolderDialogBox
import com.sakethh.linkora.screens.home.composables.AddNewLinkDialogBox
import com.sakethh.linkora.screens.home.composables.DataDialogBoxType
import com.sakethh.linkora.screens.home.composables.DeleteDialogBox
import com.sakethh.linkora.screens.home.composables.GeneralCard
import com.sakethh.linkora.screens.home.composables.LinkUIComponent
import com.sakethh.linkora.screens.home.composables.RenameDialogBox
import com.sakethh.linkora.screens.settings.SettingsScreenVM
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
    val recentlyVisitedLinksData = homeScreenVM.recentlyVisitedLinksData.collectAsState().value
    val btmModalSheetState = androidx.compose.material3.rememberModalBottomSheetState()
    val btmModalSheetStateForSavingLinks =
        androidx.compose.material3.rememberModalBottomSheetState()
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
    val shouldBtmSheetForNewLinkAdditionBeEnabled = rememberSaveable {
        mutableStateOf(false)
    }
    if (shouldDialogForNewFolderAppear.value || shouldDialogForNewLinkAppear.value) {
        shouldScreenTransparencyDecreasedBoxVisible.value = false
        isMainFabRotated.value = false
    }
    LinkoraTheme {
        Scaffold(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            floatingActionButton = {
                if (SettingsScreenVM.Settings.isBtmSheetEnabledForSavingLinks.value) {
                    Column(modifier = Modifier.padding(bottom = 82.dp)) {
                        FloatingActionButton(
                            shape = RoundedCornerShape(10.dp),
                            onClick = {
                                coroutineScope.launch {
                                    awaitAll(
                                        async {
                                            btmModalSheetState.expand()
                                        },
                                        async {
                                            shouldBtmSheetForNewLinkAdditionBeEnabled.value = true
                                        })
                                }
                            }) {
                            Icon(
                                imageVector = Icons.Default.AddLink, contentDescription = null
                            )
                        }
                    }
                } else {
                    Column(modifier = Modifier.padding(bottom = 82.dp)) {
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
                                    } else {
                                        coroutineScope.launch {
                                            awaitAll(async {
                                                rotationAnimation.animateTo(
                                                    360f, animationSpec = tween(300)
                                                )
                                            }, async {
                                                shouldScreenTransparencyDecreasedBoxVisible.value =
                                                    true
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
                    Text(
                        text = currentPhaseOfTheDay,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(start = 15.dp, top = 25.dp)
                    )
                }
                item {
                    Divider(
                        thickness = 0.25.dp,
                        modifier = Modifier.padding(top = 20.dp, start = 15.dp, end = 30.dp)
                    )
                }
                if (recentlySavedLinksData.isNotEmpty()) {
                    item {
                        Text(
                            text = "Recently Saved Links",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 15.dp, top = 25.dp)
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
                                GeneralCard(
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
                                    }
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                        }
                    }
                }

                if (recentlySavedImpsLinksData.isNotEmpty()) {
                    item {
                        Text(
                            text = "Recent Important(s)",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 15.dp, top = 40.dp)
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
                                GeneralCard(
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
                                    }
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                        }
                    }
                }

                if (recentlyVisitedLinksData.isNotEmpty()) {
                    item {
                        Text(
                            text = "Recently Visited",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 15.dp, top = 40.dp)
                        )
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
                            webURL = it.webURL
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
            shouldDialogBoxAppear = shouldDialogForNewFolderAppear, coroutineScope = coroutineScope
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
                coroutineScope.launch {
                    if (CustomLocalDBDaoFunctionsDecl.localDB.crudDao()
                            .doesThisExistsInImpLinks(webURL = HomeScreenVM.tempImpLinkData.webURL)
                    ) {
                        CustomLocalDBDaoFunctionsDecl.localDB.crudDao()
                            .deleteALinkFromImpLinks(webURL = HomeScreenVM.tempImpLinkData.webURL)
                        Toast.makeText(
                            context, "removed link from the \"Important Links\" successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        CustomLocalDBDaoFunctionsDecl.localDB.crudDao().addANewLinkToImpLinks(
                            ImportantLinks(
                                title = HomeScreenVM.tempImpLinkData.title,
                                webURL = HomeScreenVM.tempImpLinkData.webURL,
                                baseURL = HomeScreenVM.tempImpLinkData.baseURL,
                                imgURL = HomeScreenVM.tempImpLinkData.imgURL,
                                infoForSaving = HomeScreenVM.tempImpLinkData.infoForSaving
                            )
                        )
                        Toast.makeText(
                            context, "added to the \"Important Links\" successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.invokeOnCompletion {
                    coroutineScope.launch {
                        optionsBtmSheetVM.updateImportantCardData(HomeScreenVM.tempImpLinkData.webURL)
                    }
                }
                Unit
            },
            importantLinks = null,
            onArchiveClick = {
                when (selectedCardType.value) {
                    HomeScreenBtmSheetType.RECENT_SAVES.name -> {
                        coroutineScope.launch {
                            awaitAll(async {
                                CustomLocalDBDaoFunctionsDecl.archiveLinkTableUpdater(
                                    archivedLinks = ArchivedLinks(
                                        title = HomeScreenVM.tempImpLinkData.title,
                                        webURL = HomeScreenVM.tempImpLinkData.webURL,
                                        baseURL = HomeScreenVM.tempImpLinkData.baseURL,
                                        imgURL = HomeScreenVM.tempImpLinkData.imgURL,
                                        infoForSaving = HomeScreenVM.tempImpLinkData.infoForSaving
                                    ), context = context
                                )
                            }, async {
                                CustomLocalDBDaoFunctionsDecl.localDB.crudDao()
                                    .deleteALinkFromSavedLinks(webURL = HomeScreenVM.tempImpLinkData.webURL)
                            })
                        }
                        Unit
                    }

                    HomeScreenBtmSheetType.RECENT_VISITS.name -> {
                        coroutineScope.launch {
                            awaitAll(async {
                                CustomLocalDBDaoFunctionsDecl.archiveLinkTableUpdater(
                                    archivedLinks = ArchivedLinks(
                                        title = HomeScreenVM.tempImpLinkData.title,
                                        webURL = HomeScreenVM.tempImpLinkData.webURL,
                                        baseURL = HomeScreenVM.tempImpLinkData.baseURL,
                                        imgURL = HomeScreenVM.tempImpLinkData.imgURL,
                                        infoForSaving = HomeScreenVM.tempImpLinkData.infoForSaving
                                    ), context = context
                                )
                            }, async {
                                CustomLocalDBDaoFunctionsDecl.localDB.crudDao()
                                    .deleteARecentlyVisitedLink(webURL = HomeScreenVM.tempImpLinkData.webURL)
                            })
                        }
                        Unit
                    }

                    HomeScreenBtmSheetType.RECENT_IMP_SAVES.name -> {
                        coroutineScope.launch {
                            awaitAll(async {
                                CustomLocalDBDaoFunctionsDecl.archiveLinkTableUpdater(
                                    archivedLinks = ArchivedLinks(
                                        title = HomeScreenVM.tempImpLinkData.title,
                                        webURL = HomeScreenVM.tempImpLinkData.webURL,
                                        baseURL = HomeScreenVM.tempImpLinkData.baseURL,
                                        imgURL = HomeScreenVM.tempImpLinkData.imgURL,
                                        infoForSaving = HomeScreenVM.tempImpLinkData.infoForSaving
                                    ), context = context
                                )
                            }, async {
                                CustomLocalDBDaoFunctionsDecl.localDB.crudDao()
                                    .deleteALinkFromImpLinks(webURL = HomeScreenVM.tempImpLinkData.webURL)
                            })
                        }
                        Unit
                    }
                }
            }, noteForSaving = selectedURLNote.value,
            onNoteDeleteCardClick = {
                when (selectedCardType.value) {
                    HomeScreenBtmSheetType.RECENT_SAVES.name -> {
                        coroutineScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.crudDao()
                                .deleteALinkInfoFromSavedLinks(webURL = selectedWebURL.value)
                        }.invokeOnCompletion {
                            Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                        }
                        Unit
                    }

                    HomeScreenBtmSheetType.RECENT_VISITS.name -> {
                        coroutineScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.crudDao()
                                .deleteANoteFromRecentlyVisited(webURL = selectedWebURL.value)
                        }.invokeOnCompletion {
                            Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                        }
                        Unit
                    }

                    HomeScreenBtmSheetType.RECENT_IMP_SAVES.name -> {
                        coroutineScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.crudDao()
                                .deleteANoteFromImportantLinks(webURL = selectedWebURL.value)
                        }.invokeOnCompletion {
                            Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                        }
                        Unit
                    }
                }
            },
            folderName = "",
            linkTitle = HomeScreenVM.tempImpLinkData.title
        )
    }
    DeleteDialogBox(shouldDialogBoxAppear = shouldDeleteBoxAppear,
        deleteDialogBoxType = DataDialogBoxType.LINK,
        onDeleteClick = {
            when (selectedCardType.value) {
                HomeScreenBtmSheetType.RECENT_SAVES.name -> {
                    coroutineScope.launch {
                        CustomLocalDBDaoFunctionsDecl.localDB.crudDao()
                            .deleteALinkFromSavedLinks(
                                webURL = selectedWebURL.value
                            )
                    }.invokeOnCompletion {
                        shouldDeleteBoxAppear.value = false
                        Toast.makeText(
                            context, "deleted the link successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Unit
                }

                HomeScreenBtmSheetType.RECENT_VISITS.name -> {
                    coroutineScope.launch {
                        CustomLocalDBDaoFunctionsDecl.localDB.crudDao()
                            .deleteARecentlyVisitedLink(
                                webURL = selectedWebURL.value
                            )
                    }
                    Unit
                }

                HomeScreenBtmSheetType.RECENT_IMP_SAVES.name -> {
                    coroutineScope.launch {
                        CustomLocalDBDaoFunctionsDecl.localDB.crudDao()
                            .deleteALinkFromImpLinks(webURL = selectedWebURL.value)
                    }
                    Unit
                }
            }
        })
    RenameDialogBox(shouldDialogBoxAppear = shouldRenameDialogBoxBeVisible,
        coroutineScope = coroutineScope,
        webURLForTitle = selectedWebURL.value,
        existingFolderName = "",
        renameDialogBoxFor = OptionsBtmSheetType.LINK,
        onNoteChangeClickForLinks = { webURL: String, newNote: String ->
            when (selectedCardType.value) {
                HomeScreenBtmSheetType.RECENT_SAVES.name -> {
                    coroutineScope.launch {
                        CustomLocalDBDaoFunctionsDecl.localDB.crudDao()
                            .renameALinkInfoFromSavedLinks(
                                webURL = webURL, newInfo = newNote
                            )
                    }
                    Unit
                }

                HomeScreenBtmSheetType.RECENT_VISITS.name -> {
                    coroutineScope.launch {
                        CustomLocalDBDaoFunctionsDecl.localDB.crudDao()
                            .renameALinkInfoFromRecentlyVisitedLinks(
                                webURL = webURL, newInfo = newNote
                            )
                    }
                    Unit
                }

                HomeScreenBtmSheetType.RECENT_IMP_SAVES.name -> {
                    coroutineScope.launch {
                        CustomLocalDBDaoFunctionsDecl.localDB.crudDao()
                            .renameALinkInfoFromImpLinks(webURL = webURL, newInfo = newNote)
                    }
                    Unit
                }
            }
        },
        onTitleChangeClickForLinks = { webURL: String, newTitle: String ->
            when (selectedCardType.value) {
                HomeScreenBtmSheetType.RECENT_SAVES.name -> {
                    coroutineScope.launch {
                        CustomLocalDBDaoFunctionsDecl.localDB.crudDao()
                            .renameALinkTitleFromSavedLinks(
                                webURL = webURL, newTitle = newTitle
                            )
                    }
                    Unit
                }

                HomeScreenBtmSheetType.RECENT_VISITS.name -> {
                    coroutineScope.launch {
                        CustomLocalDBDaoFunctionsDecl.localDB.crudDao()
                            .renameALinkTitleFromRecentlyVisited(
                                webURL = webURL, newTitle = newTitle
                            )
                    }
                    Unit
                }

                HomeScreenBtmSheetType.RECENT_IMP_SAVES.name -> {
                    coroutineScope.launch {
                        CustomLocalDBDaoFunctionsDecl.localDB.crudDao()
                            .renameALinkTitleFromImpLinks(webURL = webURL, newTitle = newTitle)
                    }
                    Unit
                }
            }
        })
    NewLinkBtmSheet(
        btmSheetState = btmModalSheetStateForSavingLinks,
        _inIntentActivity = false,
        screenType = SpecificScreenType.ROOT_SCREEN,
        shouldUIBeVisible = shouldBtmSheetForNewLinkAdditionBeEnabled
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