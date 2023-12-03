package com.sakethh.linkora.screens.collections.specificCollectionScreen

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
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
import com.sakethh.linkora.customComposables.CustomComposablesVM
import com.sakethh.linkora.customComposables.DataDialogBoxType
import com.sakethh.linkora.customComposables.DeleteDialogBox
import com.sakethh.linkora.customComposables.DeleteDialogBoxParam
import com.sakethh.linkora.customComposables.FloatingActionBtn
import com.sakethh.linkora.customComposables.FloatingActionBtnParam
import com.sakethh.linkora.customComposables.LinkUIComponent
import com.sakethh.linkora.customComposables.LinkUIComponentParam
import com.sakethh.linkora.customComposables.RenameDialogBox
import com.sakethh.linkora.customComposables.RenameDialogBoxParam
import com.sakethh.linkora.customWebTab.openInWeb
import com.sakethh.linkora.localDB.dto.RecentlyVisited
import com.sakethh.linkora.navigation.NavigationRoutes
import com.sakethh.linkora.screens.DataEmptyScreen
import com.sakethh.linkora.screens.collections.CollectionsScreenVM
import com.sakethh.linkora.screens.collections.FolderIndividualComponent
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecificScreen(navController: NavController) {
    val specificCollectionsScreenVM: SpecificScreenVM = viewModel()
    LaunchedEffect(key1 = Unit) {
        awaitAll(async {
            specificCollectionsScreenVM.changeRetrievedData(
                sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(SettingsScreenVM.Settings.selectedSortingType.value),
                folderID = SpecificScreenVM.currentClickedFolderData.value.id
            )
        }, async { specificCollectionsScreenVM.retrieveChildFoldersData() })
    }
    val selectedWebURL = rememberSaveable {
        mutableStateOf("")
    }
    val specificFolderLinksData = specificCollectionsScreenVM.folderLinksData.collectAsState().value
    val childFoldersData = specificCollectionsScreenVM.childFoldersData.collectAsState().value
    val savedLinksData = specificCollectionsScreenVM.savedLinksTable.collectAsState().value
    val impLinksData = specificCollectionsScreenVM.impLinksTable.collectAsState().value
    val archivedFoldersLinksData =
        specificCollectionsScreenVM.archiveFolderDataTable.collectAsState().value
    val archivedSubFoldersData =
        specificCollectionsScreenVM.archiveSubFolderData.collectAsState().value
    val tempImpLinkData = specificCollectionsScreenVM.impLinkDataForBtmSheet.copy()
    val btmModalSheetState = rememberModalBottomSheetState()
    val btmModalSheetStateForSavingLink = rememberModalBottomSheetState()
    val shouldOptionsBtmModalSheetBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldRenameDialogBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldDeleteDialogBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldSortingBottomSheetAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val sortingBtmSheetState = rememberModalBottomSheetState()
    val selectedURLOrFolderNote = rememberSaveable {
        mutableStateOf("")
    }
    val selectedURLTitle = rememberSaveable {
        mutableStateOf("")
    }
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val optionsBtmSheetVM: OptionsBtmSheetVM = viewModel()
    val topBarText = when (SpecificScreenVM.screenType.value) {
        SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
            SpecificScreenVM.currentClickedFolderData.value.folderName = "Important Links"
            "Important Links"
        }

        SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
            SpecificScreenVM.currentClickedFolderData.value.folderName
        }

        SpecificScreenType.SAVED_LINKS_SCREEN -> {
            SpecificScreenVM.currentClickedFolderData.value.folderName = "Saved Links"
            "Saved Links"
        }

        SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
            SpecificScreenVM.currentClickedFolderData.value.folderName
        }

        else -> {
            ""
        }
    }
    val shouldNewLinkDialogBoxBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldBtmSheetForNewLinkAdditionBeEnabled = rememberSaveable {
        mutableStateOf(false)
    }
    val btmModalSheetStateForSavingLinks = rememberModalBottomSheetState()
    val isMainFabRotated = rememberSaveable {
        mutableStateOf(false)
    }
    val clickedFolderName = rememberSaveable { mutableStateOf("") }
    val clickedFolderNote = rememberSaveable { mutableStateOf("") }
    val rotationAnimation = remember {
        Animatable(0f)
    }
    val shouldScreenTransparencyDecreasedBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldDialogForNewFolderAppear = rememberSaveable {
        mutableStateOf(false)
    }
    LinkoraTheme {
        Scaffold(floatingActionButtonPosition = FabPosition.End, floatingActionButton = {
            if (SpecificScreenVM.screenType.value == SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN) {
                FloatingActionBtn(
                    FloatingActionBtnParam(
                        newLinkBottomModalSheetState = btmModalSheetStateForSavingLinks,
                        shouldBtmSheetForNewLinkAdditionBeEnabled = shouldBtmSheetForNewLinkAdditionBeEnabled,
                        shouldScreenTransparencyDecreasedBoxVisible = shouldScreenTransparencyDecreasedBoxVisible,
                        shouldDialogForNewFolderAppear = shouldDialogForNewFolderAppear,
                        shouldDialogForNewLinkAppear = shouldNewLinkDialogBoxBeVisible,
                        isMainFabRotated = isMainFabRotated,
                        rotationAnimation = rotationAnimation,
                        inASpecificScreen = true
                    )
                )
            } else if (SpecificScreenVM.screenType.value != SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN && SpecificScreenVM.screenType.value != SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN) {
                FloatingActionButton(shape = RoundedCornerShape(10.dp), onClick = {
                    if (!SettingsScreenVM.Settings.isBtmSheetEnabledForSavingLinks.value) {
                        shouldNewLinkDialogBoxBeVisible.value = true
                    } else {
                        coroutineScope.launch {
                            awaitAll(async {
                                btmModalSheetStateForSavingLink.expand()
                            }, async { shouldBtmSheetForNewLinkAdditionBeEnabled.value = true })
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.AddLink, contentDescription = null
                    )
                }
            }
        }, modifier = Modifier.background(MaterialTheme.colorScheme.surface), topBar = {
            TopAppBar(colors = TopAppBarDefaults.topAppBarColors(), title = {
                Text(
                    text = topBarText,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 24.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(0.75f)
                )
            }, actions = {
                when (SpecificScreenVM.screenType.value) {
                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                        if (impLinksData.isNotEmpty()) {
                            IconButton(onClick = { shouldSortingBottomSheetAppear.value = true }) {
                                Icon(imageVector = Icons.Outlined.Sort, contentDescription = null)
                            }
                        }
                    }

                    SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                        if (archivedFoldersLinksData.isNotEmpty()) {
                            IconButton(onClick = { shouldSortingBottomSheetAppear.value = true }) {
                                Icon(imageVector = Icons.Outlined.Sort, contentDescription = null)
                            }
                        }
                    }

                    SpecificScreenType.SAVED_LINKS_SCREEN -> {
                        if (savedLinksData.isNotEmpty()) {
                            IconButton(onClick = { shouldSortingBottomSheetAppear.value = true }) {
                                Icon(imageVector = Icons.Outlined.Sort, contentDescription = null)
                            }
                        }
                    }

                    SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                        if (specificFolderLinksData.isNotEmpty()) {
                            IconButton(onClick = { shouldSortingBottomSheetAppear.value = true }) {
                                Icon(imageVector = Icons.Outlined.Sort, contentDescription = null)
                            }
                        }
                    }

                    SpecificScreenType.INTENT_ACTIVITY -> {

                    }

                    SpecificScreenType.ROOT_SCREEN -> {

                    }
                }
            })
        }) {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                when (SpecificScreenVM.screenType.value) {
                    SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                        if (childFoldersData.isNotEmpty()) {
                            items(childFoldersData) {
                                FolderIndividualComponent(folderName = it.folderName,
                                    folderNote = it.infoForSaving,
                                    onMoreIconClick = {
                                        selectedURLTitle.value = it.folderName
                                        selectedURLOrFolderNote.value = it.infoForSaving
                                        clickedFolderNote.value = it.infoForSaving
                                        coroutineScope.launch {
                                            optionsBtmSheetVM.updateArchiveFolderCardData(folderName = it.folderName)
                                        }
                                        clickedFolderName.value = it.folderName
                                        CollectionsScreenVM.selectedFolderData.id = it.id
                                        shouldOptionsBtmModalSheetBeVisible.value = true
                                        SpecificScreenVM.selectedBtmSheetType.value =
                                            OptionsBtmSheetType.FOLDER
                                    },
                                    onFolderClick = {
                                        SpecificScreenVM.currentClickedFolderData.value = it
                                        navController.navigate(NavigationRoutes.SPECIFIC_SCREEN.name)
                                    })
                            }
                        }
                        if (specificFolderLinksData.isNotEmpty()) {
                            items(specificFolderLinksData) {
                                LinkUIComponent(
                                    LinkUIComponentParam(title = it.title,
                                        webBaseURL = it.baseURL,
                                        imgURL = it.imgURL,
                                        onMoreIconCLick = {
                                            SpecificScreenVM.selectedBtmSheetType.value =
                                                OptionsBtmSheetType.LINK
                                            selectedURLTitle.value = it.title
                                            selectedWebURL.value = it.webURL
                                            selectedURLOrFolderNote.value = it.infoForSaving
                                            tempImpLinkData.apply {
                                                this.webURL = it.webURL
                                                this.baseURL = it.baseURL
                                                this.imgURL = it.imgURL
                                                this.title = it.title
                                                this.infoForSaving = it.infoForSaving
                                            }
                                            tempImpLinkData.webURL = it.webURL
                                            shouldOptionsBtmModalSheetBeVisible.value = true
                                            coroutineScope.launch {
                                                awaitAll(async {
                                                    optionsBtmSheetVM.updateImportantCardData(
                                                        url = selectedWebURL.value
                                                    )
                                                }, async {
                                                    optionsBtmSheetVM.updateArchiveLinkCardData(
                                                        url = selectedWebURL.value
                                                    )
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
                                                    uriHandler = uriHandler,
                                                    forceOpenInExternalBrowser = false
                                                )
                                            }
                                        },
                                        webURL = it.webURL,
                                        onForceOpenInExternalBrowserClicked = {
                                            specificCollectionsScreenVM.onLinkClick(
                                                RecentlyVisited(
                                                    title = it.title,
                                                    webURL = it.webURL,
                                                    baseURL = it.baseURL,
                                                    imgURL = it.imgURL,
                                                    infoForSaving = it.infoForSaving
                                                ),
                                                context = context,
                                                uriHandler = uriHandler,
                                                onTaskCompleted = {},
                                                forceOpenInExternalBrowser = true
                                            )
                                        })
                                )
                            }
                        } else {
                            item {
                                DataEmptyScreen(text = "This folder doesn't contain any links. Add links for further usage.")
                            }
                        }
                    }

                    SpecificScreenType.SAVED_LINKS_SCREEN -> {
                        if (savedLinksData.isNotEmpty()) {
                            items(savedLinksData) {
                                LinkUIComponent(
                                    LinkUIComponentParam(title = it.title,
                                        webBaseURL = it.baseURL,
                                        imgURL = it.imgURL,
                                        onMoreIconCLick = {
                                            selectedWebURL.value = it.webURL
                                            selectedURLOrFolderNote.value = it.infoForSaving
                                            tempImpLinkData.apply {
                                                this.webURL = it.webURL
                                                this.baseURL = it.baseURL
                                                this.imgURL = it.imgURL
                                                this.title = it.title
                                                this.infoForSaving = it.infoForSaving
                                            }
                                            tempImpLinkData.webURL = it.webURL
                                            shouldOptionsBtmModalSheetBeVisible.value = true
                                            coroutineScope.launch {
                                                awaitAll(async {
                                                    optionsBtmSheetVM.updateImportantCardData(
                                                        url = selectedWebURL.value
                                                    )
                                                }, async {
                                                    optionsBtmSheetVM.updateArchiveLinkCardData(
                                                        url = selectedWebURL.value
                                                    )
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
                                                    uriHandler = uriHandler,
                                                    forceOpenInExternalBrowser = false
                                                )
                                            }
                                        },
                                        webURL = it.webURL,
                                        onForceOpenInExternalBrowserClicked = {
                                            specificCollectionsScreenVM.onLinkClick(
                                                RecentlyVisited(
                                                    title = it.title,
                                                    webURL = it.webURL,
                                                    baseURL = it.baseURL,
                                                    imgURL = it.imgURL,
                                                    infoForSaving = it.infoForSaving
                                                ),
                                                context = context,
                                                uriHandler = uriHandler,
                                                onTaskCompleted = {},
                                                forceOpenInExternalBrowser = true
                                            )
                                        })
                                )
                            }
                        } else {
                            item {
                                DataEmptyScreen(text = "No links found. To continue, please add links.")
                            }
                        }
                    }

                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                        if (impLinksData.isNotEmpty()) {
                            items(impLinksData) {
                                LinkUIComponent(
                                    LinkUIComponentParam(title = it.title,
                                        webBaseURL = it.baseURL,
                                        imgURL = it.imgURL,
                                        onMoreIconCLick = {
                                            selectedWebURL.value = it.webURL
                                            selectedURLOrFolderNote.value = it.infoForSaving
                                            tempImpLinkData.apply {
                                                this.webURL = it.webURL
                                                this.baseURL = it.baseURL
                                                this.imgURL = it.imgURL
                                                this.title = it.title
                                                this.infoForSaving = it.infoForSaving
                                            }
                                            tempImpLinkData.webURL = it.webURL
                                            shouldOptionsBtmModalSheetBeVisible.value = true
                                            coroutineScope.launch {
                                                awaitAll(async {
                                                    optionsBtmSheetVM.updateImportantCardData(
                                                        url = selectedWebURL.value
                                                    )
                                                }, async {
                                                    optionsBtmSheetVM.updateArchiveLinkCardData(
                                                        url = selectedWebURL.value
                                                    )
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
                                                    uriHandler = uriHandler,
                                                    forceOpenInExternalBrowser = false
                                                )
                                            }
                                        },
                                        webURL = it.webURL,
                                        onForceOpenInExternalBrowserClicked = {
                                            specificCollectionsScreenVM.onLinkClick(
                                                RecentlyVisited(
                                                    title = it.title,
                                                    webURL = it.webURL,
                                                    baseURL = it.baseURL,
                                                    imgURL = it.imgURL,
                                                    infoForSaving = it.infoForSaving
                                                ),
                                                context = context,
                                                uriHandler = uriHandler,
                                                onTaskCompleted = {},
                                                forceOpenInExternalBrowser = true
                                            )
                                        })
                                )
                            }
                        } else {
                            item {
                                DataEmptyScreen(text = "No important links were found. To continue, please add links.")
                            }
                        }
                    }

                    SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                        if (archivedSubFoldersData.isNotEmpty()) {
                            items(archivedSubFoldersData) {
                                FolderIndividualComponent(folderName = it.folderName,
                                    folderNote = it.infoForSaving,
                                    onMoreIconClick = {
                                        selectedURLTitle.value = it.folderName
                                        selectedURLOrFolderNote.value = it.infoForSaving
                                        clickedFolderNote.value = it.infoForSaving
                                        coroutineScope.launch {
                                            optionsBtmSheetVM.updateArchiveFolderCardData(folderName = it.folderName)
                                        }
                                        clickedFolderName.value = it.folderName
                                        CollectionsScreenVM.selectedFolderData.id = it.id
                                        shouldOptionsBtmModalSheetBeVisible.value = true
                                        SpecificScreenVM.selectedBtmSheetType.value =
                                            OptionsBtmSheetType.FOLDER
                                    },
                                    onFolderClick = {
                                        SpecificScreenVM.currentClickedFolderData.value = it
                                        navController.navigate(NavigationRoutes.SPECIFIC_SCREEN.name)
                                    })
                            }
                        }
                        if (archivedFoldersLinksData.isNotEmpty()) {
                            items(archivedFoldersLinksData) {
                                LinkUIComponent(
                                    LinkUIComponentParam(title = it.title,
                                        webBaseURL = it.baseURL,
                                        imgURL = it.imgURL,
                                        onMoreIconCLick = {
                                            selectedWebURL.value = it.webURL
                                            selectedURLOrFolderNote.value = it.infoForSaving
                                            shouldOptionsBtmModalSheetBeVisible.value = true
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
                                                    uriHandler = uriHandler,
                                                    forceOpenInExternalBrowser = false
                                                )
                                            }
                                        },
                                        webURL = it.webURL,
                                        onForceOpenInExternalBrowserClicked = {
                                            specificCollectionsScreenVM.onLinkClick(
                                                RecentlyVisited(
                                                    title = it.title,
                                                    webURL = it.webURL,
                                                    baseURL = it.baseURL,
                                                    imgURL = it.imgURL,
                                                    infoForSaving = it.infoForSaving
                                                ),
                                                context = context,
                                                uriHandler = uriHandler,
                                                onTaskCompleted = {},
                                                forceOpenInExternalBrowser = true
                                            )
                                        })
                                )
                            }
                        } else {
                            item {
                                DataEmptyScreen(text = "No links were found in this archived folder.")
                            }
                        }
                    }

                    else -> {}
                }
                item {
                    Spacer(modifier = Modifier.height(175.dp))
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
        NewLinkBtmSheet(
            NewLinkBtmSheetUIParam(
                btmSheetState = btmModalSheetStateForSavingLink,
                inIntentActivity = false,
                screenType = SpecificScreenVM.screenType.value,
                folderName = SpecificScreenVM.currentClickedFolderData.value.folderName,
                shouldUIBeVisible = shouldBtmSheetForNewLinkAdditionBeEnabled,
                onLinkSaved = {
                    specificCollectionsScreenVM.changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        ), folderID = SpecificScreenVM.currentClickedFolderData.value.id
                    )
                },
                onFolderCreated = {},
                parentFolderID = null
            )
        )
        OptionsBtmSheetUI(
            OptionsBtmSheetUIParam(
                inSpecificArchiveScreen = mutableStateOf(SpecificScreenVM.screenType.value == SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN),
                inArchiveScreen = mutableStateOf(SpecificScreenVM.screenType.value == SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN),
                btmModalSheetState = btmModalSheetState,
                shouldBtmModalSheetBeVisible = shouldOptionsBtmModalSheetBeVisible,
                btmSheetFor = when (SpecificScreenVM.screenType.value) {
                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN
                    SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN
                    SpecificScreenType.SAVED_LINKS_SCREEN -> OptionsBtmSheetType.LINK
                    SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> SpecificScreenVM.selectedBtmSheetType.value
                    else -> {
                        OptionsBtmSheetType.LINK
                    }
                },
                onDeleteCardClick = {
                    shouldDeleteDialogBeVisible.value = true
                },
                onRenameClick = {
                    shouldRenameDialogBeVisible.value = true
                },
                onImportantLinkAdditionInTheTable = {
                    specificCollectionsScreenVM.onImportantLinkAdditionInTheTable(
                        context, onTaskCompleted = {
                            specificCollectionsScreenVM.changeRetrievedData(
                                folderID = SpecificScreenVM.currentClickedFolderData.value.id,
                                sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                                    SettingsScreenVM.Settings.selectedSortingType.value
                                )
                            )
                        }, tempImpLinkData
                    )
                },
                importantLinks = null,
                forAChildFolder = if (SpecificScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) mutableStateOf(
                    false
                ) else mutableStateOf(true),
                onArchiveClick = {
                    specificCollectionsScreenVM.onArchiveClick(tempImpLinkData,
                        context,
                        SpecificScreenVM.currentClickedFolderData.value.id,
                        onTaskCompleted = {
                            specificCollectionsScreenVM.changeRetrievedData(
                                folderID = SpecificScreenVM.currentClickedFolderData.value.id,
                                sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                                    SettingsScreenVM.Settings.selectedSortingType.value
                                )
                            )
                        })
                },
                noteForSaving = selectedURLOrFolderNote.value,
                onNoteDeleteCardClick = {
                    specificCollectionsScreenVM.onNoteDeleteCardClick(
                        selectedWebURL.value,
                        context,
                        folderID = CollectionsScreenVM.selectedFolderData.id
                    )
                },
                folderName = selectedURLTitle.value,
                linkTitle = tempImpLinkData.title
            )
        )
        DeleteDialogBox(
            DeleteDialogBoxParam(shouldDialogBoxAppear = shouldDeleteDialogBeVisible,
                onDeleteClick = {
                    specificCollectionsScreenVM.onDeleteClick(folderID = SpecificScreenVM.currentClickedFolderData.value.id,
                        selectedWebURL = selectedWebURL.value,
                        context,
                        onTaskCompleted = {
                            specificCollectionsScreenVM.changeRetrievedData(
                                folderID = SpecificScreenVM.currentClickedFolderData.value.id,
                                sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                                    SettingsScreenVM.Settings.selectedSortingType.value
                                )
                            )
                        })
                },
                deleteDialogBoxType = if (SpecificScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) DataDialogBoxType.LINK else DataDialogBoxType.FOLDER,
                onDeleted = {
                    specificCollectionsScreenVM.changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        ), folderID = SpecificScreenVM.currentClickedFolderData.value.id
                    )
                })
        )
        RenameDialogBox(
            RenameDialogBoxParam(
                inASpecificScreen = true,
                shouldDialogBoxAppear = shouldRenameDialogBeVisible,
                existingFolderName = topBarText,
                renameDialogBoxFor = SpecificScreenVM.selectedBtmSheetType.value,
                onNoteChangeClickForLinks = { newNote: String ->
                    specificCollectionsScreenVM.onNoteChangeClickForLinks(folderID = SpecificScreenVM.currentClickedFolderData.value.id,
                        selectedWebURL.value,
                        newNote,
                        onTaskCompleted = {

                        })
                },
                onTitleChangeClickForLinks = { newTitle: String ->
                    specificCollectionsScreenVM.onTitleChangeClickForLinks(folderID = SpecificScreenVM.currentClickedFolderData.value.id,
                        newTitle,
                        selectedWebURL.value,
                        onTaskCompleted = {
                            specificCollectionsScreenVM.changeRetrievedData(
                                folderID = SpecificScreenVM.currentClickedFolderData.value.id,
                                sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                                    SettingsScreenVM.Settings.selectedSortingType.value
                                )
                            )
                        })
                },
                onTitleRenamed = {
                    specificCollectionsScreenVM.changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        ), folderID = SpecificScreenVM.currentClickedFolderData.value.id
                    )
                },
                selectedV9ArchivedFolder = mutableStateOf(
                    SpecificScreenVM.screenType.value == SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN
                ),
                currentFolderID = CollectionsScreenVM.selectedFolderData.id,
                parentFolderID = SpecificScreenVM.currentClickedFolderData.value.id
            )
        )
        val collectionsScreenVM: CollectionsScreenVM = viewModel()
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
                parentFolderID = SpecificScreenVM.currentClickedFolderData.value.id,
                currentFolderID = null,
                inSpecificFolderScreen = true
            )
        )
        CustomComposablesVM.selectedFolderID = SpecificScreenVM.currentClickedFolderData.value.id
        AddNewLinkDialogBox(
            shouldDialogBoxAppear = shouldNewLinkDialogBoxBeVisible,
            screenType = SpecificScreenVM.screenType.value,
            onTaskCompleted = {
                specificCollectionsScreenVM.changeRetrievedData(
                    folderID = SpecificScreenVM.currentClickedFolderData.value.id,
                    sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                        SettingsScreenVM.Settings.selectedSortingType.value
                    )
                )
            },
            parentFolderID = SpecificScreenVM.currentClickedFolderData.value.parentFolderID,
            specificFolderName = SpecificScreenVM.currentClickedFolderData.value.folderName
        )
        SortingBottomSheetUI(
            shouldBottomSheetVisible = shouldSortingBottomSheetAppear, onSelectedAComponent = {
                specificCollectionsScreenVM.changeRetrievedData(
                    sortingPreferences = it,
                    folderID = SpecificScreenVM.currentClickedFolderData.value.id
                )
            }, bottomModalSheetState = sortingBtmSheetState
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
        } else {
            if (SpecificScreenVM.currentClickedFolderData.value.parentFolderID != null) {
                SpecificScreenVM.screenType.value = SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                specificCollectionsScreenVM.updateFolderData(SpecificScreenVM.currentClickedFolderData.value.parentFolderID!!)
            }
            navController.popBackStack()
        }
    }
}