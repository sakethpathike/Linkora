package com.sakethh.linkora.screens.collections.specificCollectionScreen

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.sakethh.linkora.customComposables.LinkUIComponent
import com.sakethh.linkora.customComposables.LinkUIComponentParam
import com.sakethh.linkora.customComposables.RenameDialogBox
import com.sakethh.linkora.customComposables.RenameDialogBoxParam
import com.sakethh.linkora.customWebTab.openInWeb
import com.sakethh.linkora.localDB.commonVMs.CreateVM
import com.sakethh.linkora.localDB.commonVMs.UpdateVM
import com.sakethh.linkora.localDB.dto.ImportantLinks
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

@SuppressLint("UnrememberedMutableState", "LongLogTag")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecificCollectionScreen(navController: NavController) {
    val specificCollectionsScreenVM: SpecificCollectionsScreenVM = viewModel()
    LaunchedEffect(key1 = Unit) {
        awaitAll(async {
            specificCollectionsScreenVM.changeRetrievedData(
                sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(SettingsScreenVM.Settings.selectedSortingType.value),
                folderID = CollectionsScreenVM.currentClickedFolderData.value.id,
                isFoldersSortingSelected = true,
                isLinksSortingSelected = true
            )
        }, async { specificCollectionsScreenVM.retrieveChildFoldersData() })
    }
    val createVM: CreateVM = viewModel()
    val selectedWebURL = rememberSaveable {
        mutableStateOf("")
    }
    val isDataExtractingForTheLink = rememberSaveable {
        mutableStateOf(false)
    }
    val specificFolderLinksData = specificCollectionsScreenVM.folderLinksData.collectAsState().value
    val childFoldersData = specificCollectionsScreenVM.childFoldersData.collectAsState().value
    val savedLinksData = specificCollectionsScreenVM.savedLinksTable.collectAsState().value
    val impLinksData = specificCollectionsScreenVM.impLinksTable.collectAsState().value
    val archivedFoldersLinksData =
        specificCollectionsScreenVM.archiveFoldersLinksData.collectAsState().value
    val archivedSubFoldersData =
        specificCollectionsScreenVM.archiveSubFolderData.collectAsState().value
    val tempImpLinkData = specificCollectionsScreenVM.impLinkDataForBtmSheet
    val btmModalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val btmModalSheetStateForSavingLink =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
    val sortingBtmSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
    val topBarText = when (SpecificCollectionsScreenVM.screenType.value) {
        SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
            "Important Links"
        }

        SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
            CollectionsScreenVM.currentClickedFolderData.value.folderName
        }

        SpecificScreenType.SAVED_LINKS_SCREEN -> {
            "Saved Links"
        }

        SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
            CollectionsScreenVM.currentClickedFolderData.value.folderName
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
    val btmModalSheetStateForSavingLinks =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val areLinksSelectable = rememberSaveable {
        mutableStateOf(false)
    }
    LinkoraTheme {
        Scaffold(floatingActionButtonPosition = FabPosition.End, floatingActionButton = {
            if (SpecificCollectionsScreenVM.screenType.value == SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN) {
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
            } else if (SpecificCollectionsScreenVM.screenType.value != SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN && SpecificCollectionsScreenVM.screenType.value != SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN) {
                FloatingActionButton(onClick = {
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
            Column {
                TopAppBar(navigationIcon = {
                    if (!areLinksSelectable.value) {
                        IconButton(onClick = {
                            if (CollectionsScreenVM.currentClickedFolderData.value.parentFolderID != null
                                && (SpecificCollectionsScreenVM.screenType.value == SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                                        || SpecificCollectionsScreenVM.screenType.value == SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN)
                            ) {
                                if (SpecificCollectionsScreenVM.inARegularFolder.value) {
                                    SpecificCollectionsScreenVM.screenType.value =
                                        SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                                } else {
                                    SpecificCollectionsScreenVM.screenType.value =
                                        SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN
                                }
                                specificCollectionsScreenVM.updateFolderData(CollectionsScreenVM.currentClickedFolderData.value.parentFolderID!!)
                            }
                            navController.popBackStack()
                        }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    } else {
                        IconButton(onClick = {
                            areLinksSelectable.value = false
                            specificCollectionsScreenVM.areAllItemsChecked.value = false
                            specificCollectionsScreenVM.removeAllSelections()
                            specificCollectionsScreenVM.changeAllItemsSelectedData()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Cancel, contentDescription = null
                            )
                        }
                    }
                }, scrollBehavior = scrollBehavior,
                    title = {
                        if (areLinksSelectable.value) {
                            Row {
                                AnimatedContent(targetState = specificCollectionsScreenVM.noOfItemsSelected.intValue,
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
                                Text(text = " links selected",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontSize = 18.sp,
                                    modifier = Modifier.clickable {
                                        Log.d(
                                            "selected folders LINKORA",
                                            specificCollectionsScreenVM.selectedItemsID.toString()
                                        )
                                    })
                            }
                        } else {
                            Text(
                                text = topBarText,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 18.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    actions = {
                        if (areLinksSelectable.value) {

                        } else {
                            when (SpecificCollectionsScreenVM.screenType.value) {
                                SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                                    if (impLinksData.importantLinksList.isNotEmpty()) {
                                        IconButton(onClick = {
                                            shouldSortingBottomSheetAppear.value = true
                                        }) {
                                            Icon(
                                                imageVector = Icons.Outlined.Sort,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }

                                SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                                    if (archivedFoldersLinksData.linksTableList.isNotEmpty() || archivedSubFoldersData.foldersTableList.isNotEmpty()) {
                                        IconButton(onClick = {
                                            shouldSortingBottomSheetAppear.value = true
                                            coroutineScope.launch {
                                                sortingBtmSheetState.expand()
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Outlined.Sort,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }

                                SpecificScreenType.SAVED_LINKS_SCREEN -> {
                                    if (savedLinksData.linksTableList.isNotEmpty()) {
                                        IconButton(onClick = {
                                            shouldSortingBottomSheetAppear.value = true
                                        }) {
                                            Icon(
                                                imageVector = Icons.Outlined.Sort,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }

                                SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                                    if (specificFolderLinksData.linksTableList.isNotEmpty() || childFoldersData.foldersTableList.isNotEmpty()) {
                                        IconButton(onClick = {
                                            shouldSortingBottomSheetAppear.value = true
                                            coroutineScope.launch {
                                                sortingBtmSheetState.expand()
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Outlined.Sort,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }

                                SpecificScreenType.INTENT_ACTIVITY -> {

                                }

                                SpecificScreenType.ROOT_SCREEN -> {

                                }
                            }
                        }
                    })
            }
        }) {
            LazyColumn(
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .padding(it)
                    .fillMaxSize()
            ) {
                when (SpecificCollectionsScreenVM.screenType.value) {
                    SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                        if (childFoldersData.foldersTableList.isNotEmpty()) {
                            items(items = childFoldersData.foldersTableList, key = { foldersTable ->
                                foldersTable.id.toString() + foldersTable.folderName
                            }) {
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
                                        CollectionsScreenVM.selectedFolderData.value = it
                                        shouldOptionsBtmModalSheetBeVisible.value = true
                                        SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                            OptionsBtmSheetType.FOLDER
                                    },
                                    showMoreIcon = true,
                                    onFolderClick = { _ ->
                                        CollectionsScreenVM.currentClickedFolderData.value =
                                            it
                                        navController.navigate(NavigationRoutes.SPECIFIC_SCREEN.name)
                                    })
                            }
                        }
                        if (specificFolderLinksData.linksTableList.isNotEmpty()) {
                            itemsIndexed(
                                items = specificFolderLinksData.linksTableList,
                                key = { _, linksTable ->
                                    linksTable.id.toString() + linksTable.webURL + linksTable.baseURL
                                }) { linkIndex, linkData ->
                                LinkUIComponent(
                                    LinkUIComponentParam(
                                        onLongClick = {
                                            areLinksSelectable.value = true
                                        },
                                        isSelectionModeEnabled = areLinksSelectable,
                                        title = linkData.title,
                                        webBaseURL = linkData.baseURL,
                                        imgURL = linkData.imgURL,
                                        onMoreIconCLick = {
                                            CollectionsScreenVM.selectedFolderData.value.id =
                                                linkData.id
                                            SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                                OptionsBtmSheetType.LINK
                                            selectedURLTitle.value = linkData.title
                                            selectedWebURL.value = linkData.webURL
                                            selectedURLOrFolderNote.value = linkData.infoForSaving
                                            tempImpLinkData.apply {
                                                this.webURL.value = linkData.webURL
                                                this.baseURL.value = linkData.baseURL
                                                this.imgURL.value = linkData.imgURL
                                                this.title.value = linkData.title
                                                this.infoForSaving.value = linkData.infoForSaving
                                                this.id = linkData.id
                                            }
                                            tempImpLinkData.webURL.value = linkData.webURL
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
                                            if (areLinksSelectable.value) {
                                                specificFolderLinksData.isCheckBoxSelected[linkIndex].value =
                                                    !specificFolderLinksData.isCheckBoxSelected[linkIndex].value

                                                if (specificFolderLinksData.isCheckBoxSelected[linkIndex].value) {
                                                    specificCollectionsScreenVM.selectedItemsID.add(
                                                        linkData.id
                                                    )
                                                    specificCollectionsScreenVM.noOfItemsSelected.intValue += 1
                                                } else {
                                                    specificCollectionsScreenVM.selectedItemsID.remove(
                                                        linkData.id
                                                    )
                                                    specificCollectionsScreenVM.noOfItemsSelected.intValue -= 1
                                                }

                                            } else {
                                                coroutineScope.launch {
                                                    openInWeb(
                                                        recentlyVisitedData = RecentlyVisited(
                                                            title = linkData.title,
                                                            webURL = linkData.webURL,
                                                            baseURL = linkData.baseURL,
                                                            imgURL = linkData.imgURL,
                                                            infoForSaving = linkData.infoForSaving
                                                        ),
                                                        context = context,
                                                        uriHandler = uriHandler,
                                                        forceOpenInExternalBrowser = false
                                                    )
                                                }
                                            }
                                        },
                                        webURL = linkData.webURL,
                                        onForceOpenInExternalBrowserClicked = {
                                            specificCollectionsScreenVM.onLinkClick(
                                                RecentlyVisited(
                                                    title = linkData.title,
                                                    webURL = linkData.webURL,
                                                    baseURL = linkData.baseURL,
                                                    imgURL = linkData.imgURL,
                                                    infoForSaving = linkData.infoForSaving
                                                ),
                                                context = context,
                                                uriHandler = uriHandler,
                                                onTaskCompleted = {},
                                                forceOpenInExternalBrowser = true
                                            )
                                        },
                                        isItemSelected = specificFolderLinksData.isCheckBoxSelected[linkIndex]
                                    )
                                )
                            }
                        } else {
                            item {
                                DataEmptyScreen(text = "This folder doesn't contain any links. Add links for further usage.")
                            }
                        }
                    }

                    SpecificScreenType.SAVED_LINKS_SCREEN -> {
                        if (savedLinksData.linksTableList.isNotEmpty()) {
                            itemsIndexed(
                                items = savedLinksData.linksTableList,
                                key = { _, linksTable ->
                                    linksTable.baseURL + linksTable.id.toString() + linksTable.webURL
                                }) { linkIndex, linkData ->
                                LinkUIComponent(
                                    LinkUIComponentParam(
                                        onLongClick = {
                                            areLinksSelectable.value = true
                                        },
                                        isSelectionModeEnabled = areLinksSelectable,
                                        title = linkData.title,
                                        webBaseURL = linkData.baseURL,
                                        imgURL = linkData.imgURL,
                                        onMoreIconCLick = {
                                            SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                                OptionsBtmSheetType.LINK
                                            CollectionsScreenVM.selectedFolderData.value.id =
                                                linkData.id
                                            selectedWebURL.value = linkData.webURL
                                            selectedURLOrFolderNote.value = linkData.infoForSaving
                                            tempImpLinkData.apply {
                                                this.webURL.value = linkData.webURL
                                                this.baseURL.value = linkData.baseURL
                                                this.imgURL.value = linkData.imgURL
                                                this.title.value = linkData.title
                                                this.infoForSaving.value = linkData.infoForSaving
                                            }
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
                                            if (areLinksSelectable.value) {
                                                savedLinksData.isCheckBoxSelected[linkIndex].value =
                                                    !savedLinksData.isCheckBoxSelected[linkIndex].value

                                                if (savedLinksData.isCheckBoxSelected[linkIndex].value) {
                                                    specificCollectionsScreenVM.selectedItemsID.add(
                                                        linkData.id
                                                    )
                                                    specificCollectionsScreenVM.noOfItemsSelected.intValue += 1
                                                } else {
                                                    specificCollectionsScreenVM.selectedItemsID.remove(
                                                        linkData.id
                                                    )
                                                    specificCollectionsScreenVM.noOfItemsSelected.intValue -= 1
                                                }
                                            } else {
                                                coroutineScope.launch {
                                                    openInWeb(
                                                        recentlyVisitedData = RecentlyVisited(
                                                            title = linkData.title,
                                                            webURL = linkData.webURL,
                                                            baseURL = linkData.baseURL,
                                                            imgURL = linkData.imgURL,
                                                            infoForSaving = linkData.infoForSaving
                                                        ),
                                                        context = context,
                                                        uriHandler = uriHandler,
                                                        forceOpenInExternalBrowser = false
                                                    )
                                                }
                                            }
                                        },
                                        webURL = linkData.webURL,
                                        onForceOpenInExternalBrowserClicked = {
                                            specificCollectionsScreenVM.onLinkClick(
                                                RecentlyVisited(
                                                    title = linkData.title,
                                                    webURL = linkData.webURL,
                                                    baseURL = linkData.baseURL,
                                                    imgURL = linkData.imgURL,
                                                    infoForSaving = linkData.infoForSaving
                                                ),
                                                context = context,
                                                uriHandler = uriHandler,
                                                onTaskCompleted = {},
                                                forceOpenInExternalBrowser = true
                                            )
                                        },
                                        isItemSelected = savedLinksData.isCheckBoxSelected[linkIndex]
                                    )
                                )
                            }
                        } else {
                            item {
                                DataEmptyScreen(text = "No links found. To continue, please add links.")
                            }
                        }
                    }

                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                        if (impLinksData.importantLinksList.isNotEmpty()) {
                            itemsIndexed(
                                items = impLinksData.importantLinksList,
                                key = { _, importantLinks ->
                                    importantLinks.id.toString() + importantLinks.baseURL + importantLinks.webURL
                                }) { linkIndex, linkData ->
                                LinkUIComponent(
                                    LinkUIComponentParam(
                                        onLongClick = {
                                            areLinksSelectable.value = true
                                        },
                                        isSelectionModeEnabled = areLinksSelectable,
                                        title = linkData.title,
                                        webBaseURL = linkData.baseURL,
                                        imgURL = linkData.imgURL,
                                        onMoreIconCLick = {
                                            SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                                OptionsBtmSheetType.LINK
                                            CollectionsScreenVM.selectedFolderData.value.id =
                                                linkData.id
                                            selectedWebURL.value = linkData.webURL
                                            selectedURLOrFolderNote.value = linkData.infoForSaving
                                            tempImpLinkData.apply {
                                                this.webURL.value = linkData.webURL
                                                this.baseURL.value = linkData.baseURL
                                                this.imgURL.value = linkData.imgURL
                                                this.title.value = linkData.title
                                                this.infoForSaving.value = linkData.infoForSaving
                                            }
                                            shouldOptionsBtmModalSheetBeVisible.value = true
                                            coroutineScope.launch {
                                                awaitAll(
                                                    async {
                                                        optionsBtmSheetVM.updateImportantCardData(
                                                            url = selectedWebURL.value,
                                                        )
                                                    },
                                                    async {
                                                        optionsBtmSheetVM.updateArchiveLinkCardData(
                                                            url = selectedWebURL.value,
                                                        )
                                                    },
                                                )
                                            }
                                        },
                                        onLinkClick = {
                                            if (areLinksSelectable.value) {
                                                impLinksData.isCheckBoxSelected[linkIndex].value =
                                                    !impLinksData.isCheckBoxSelected[linkIndex].value

                                                if (impLinksData.isCheckBoxSelected[linkIndex].value) {
                                                    specificCollectionsScreenVM.selectedItemsID.add(
                                                        linkData.id
                                                    )
                                                    specificCollectionsScreenVM.noOfItemsSelected.intValue += 1
                                                } else {
                                                    specificCollectionsScreenVM.selectedItemsID.remove(
                                                        linkData.id
                                                    )
                                                    specificCollectionsScreenVM.noOfItemsSelected.intValue -= 1
                                                }
                                            } else {
                                                coroutineScope.launch {
                                                    openInWeb(
                                                        recentlyVisitedData = RecentlyVisited(
                                                            title = linkData.title,
                                                            webURL = linkData.webURL,
                                                            baseURL = linkData.baseURL,
                                                            imgURL = linkData.imgURL,
                                                            infoForSaving = linkData.infoForSaving,
                                                        ),
                                                        context = context,
                                                        uriHandler = uriHandler,
                                                        forceOpenInExternalBrowser = false,
                                                    )
                                                }
                                            }
                                        },
                                        webURL = linkData.webURL,
                                        onForceOpenInExternalBrowserClicked = {
                                            specificCollectionsScreenVM.onLinkClick(
                                                RecentlyVisited(
                                                    title = linkData.title,
                                                    webURL = linkData.webURL,
                                                    baseURL = linkData.baseURL,
                                                    imgURL = linkData.imgURL,
                                                    infoForSaving = linkData.infoForSaving,
                                                ),
                                                context = context,
                                                uriHandler = uriHandler,
                                                onTaskCompleted = {},
                                                forceOpenInExternalBrowser = true,
                                            )
                                        },
                                        isItemSelected = impLinksData.isCheckBoxSelected[linkIndex]
                                    )
                                )
                            }
                        } else {
                            item {
                                DataEmptyScreen(text = "No important links were found. To continue, please add links.")
                            }
                        }
                    }

                    SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                        if (archivedSubFoldersData.foldersTableList.isNotEmpty()) {
                            items(
                                items = archivedSubFoldersData.foldersTableList,
                                key = { foldersTable ->
                                    foldersTable.folderName + foldersTable.id.toString()
                                }) {
                                FolderIndividualComponent(folderName = it.folderName,
                                    folderNote = it.infoForSaving,
                                    onMoreIconClick = {
                                        CollectionsScreenVM.selectedFolderData.value = it
                                        selectedURLTitle.value = it.folderName
                                        selectedURLOrFolderNote.value = it.infoForSaving
                                        clickedFolderNote.value = it.infoForSaving
                                        coroutineScope.launch {
                                            optionsBtmSheetVM.updateArchiveFolderCardData(folderName = it.folderName)
                                        }
                                        clickedFolderName.value = it.folderName
                                        shouldOptionsBtmModalSheetBeVisible.value = true
                                        SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                            OptionsBtmSheetType.FOLDER
                                    },
                                    showMoreIcon = true,
                                    onFolderClick = { _ ->
                                        CollectionsScreenVM.currentClickedFolderData.value =
                                            it
                                        navController.navigate(NavigationRoutes.SPECIFIC_SCREEN.name)
                                    })
                            }
                        }
                        if (archivedFoldersLinksData.linksTableList.isNotEmpty()) {
                            itemsIndexed(
                                items = archivedFoldersLinksData.linksTableList,
                                key = { _, linksTable ->
                                    linksTable.id.toString() + linksTable.webURL + linksTable.id.toString()
                                }) { linkIndex, linkData ->
                                LinkUIComponent(
                                    LinkUIComponentParam(
                                        onLongClick = {
                                            areLinksSelectable.value = true
                                        },
                                        isSelectionModeEnabled = areLinksSelectable,
                                        title = linkData.title,
                                        webBaseURL = linkData.baseURL,
                                        imgURL = linkData.imgURL,
                                        onMoreIconCLick = {
                                            CollectionsScreenVM.selectedFolderData.value.id =
                                                linkData.id
                                            SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                                OptionsBtmSheetType.LINK
                                            tempImpLinkData.apply {
                                                this.webURL.value = linkData.webURL
                                                this.baseURL.value = linkData.baseURL
                                                this.imgURL.value = linkData.imgURL
                                                this.title.value = linkData.title
                                                this.infoForSaving.value = linkData.infoForSaving
                                            }
                                            selectedWebURL.value = linkData.webURL
                                            selectedURLOrFolderNote.value = linkData.infoForSaving
                                            shouldOptionsBtmModalSheetBeVisible.value = true
                                        },
                                        onLinkClick = {
                                            if (areLinksSelectable.value) {
                                                archivedFoldersLinksData.isCheckBoxSelected[linkIndex].value =
                                                    !archivedFoldersLinksData.isCheckBoxSelected[linkIndex].value

                                                if (archivedFoldersLinksData.isCheckBoxSelected[linkIndex].value) {
                                                    specificCollectionsScreenVM.selectedItemsID.add(
                                                        linkData.id
                                                    )
                                                    specificCollectionsScreenVM.noOfItemsSelected.intValue += 1
                                                } else {
                                                    specificCollectionsScreenVM.selectedItemsID.remove(
                                                        linkData.id
                                                    )
                                                    specificCollectionsScreenVM.noOfItemsSelected.intValue -= 1
                                                }
                                            } else {
                                                coroutineScope.launch {
                                                    openInWeb(
                                                        recentlyVisitedData = RecentlyVisited(
                                                            title = linkData.title,
                                                            webURL = linkData.webURL,
                                                            baseURL = linkData.baseURL,
                                                            imgURL = linkData.imgURL,
                                                            infoForSaving = linkData.infoForSaving
                                                        ),
                                                        context = context,
                                                        uriHandler = uriHandler,
                                                        forceOpenInExternalBrowser = false
                                                    )
                                                }
                                            }
                                        },
                                        webURL = linkData.webURL,
                                        onForceOpenInExternalBrowserClicked = {
                                            specificCollectionsScreenVM.onLinkClick(
                                                RecentlyVisited(
                                                    title = linkData.title,
                                                    webURL = linkData.webURL,
                                                    baseURL = linkData.baseURL,
                                                    imgURL = linkData.imgURL,
                                                    infoForSaving = linkData.infoForSaving
                                                ),
                                                context = context,
                                                uriHandler = uriHandler,
                                                onTaskCompleted = {},
                                                forceOpenInExternalBrowser = true
                                            )
                                        },
                                        isItemSelected = archivedFoldersLinksData.isCheckBoxSelected[linkIndex],
                                    )
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
                screenType = SpecificCollectionsScreenVM.screenType.value,
                shouldUIBeVisible = shouldBtmSheetForNewLinkAdditionBeEnabled,
                currentFolder = CollectionsScreenVM.currentClickedFolderData.value.folderName,
                onLinkSaveClick = { isAutoDetectSelected, webURL, title, note, selectedDefaultFolder, selectedNonDefaultFolderID ->
                    isDataExtractingForTheLink.value = true
                    when (SpecificCollectionsScreenVM.screenType.value) {
                        SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                            createVM.addANewLinkInAFolderV10(
                                autoDetectTitle = isAutoDetectSelected,
                                title = title,
                                webURL = webURL,
                                noteForSaving = note,
                                parentFolderID = CollectionsScreenVM.currentClickedFolderData.value.id,
                                context = context,
                                onTaskCompleted = {
                                    coroutineScope.launch {
                                        btmModalSheetStateForSavingLinks.hide()
                                        shouldBtmSheetForNewLinkAdditionBeEnabled.value = false
                                        isDataExtractingForTheLink.value = false
                                    }
                                },
                                folderName = CollectionsScreenVM.currentClickedFolderData.value.folderName
                            )
                        }

                        SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                            createVM.addANewLinkInImpLinks(
                                autoDetectTitle = isAutoDetectSelected,
                                title = title,
                                webURL = webURL,
                                noteForSaving = note,
                                context = context,
                                onTaskCompleted = {
                                    coroutineScope.launch {
                                        btmModalSheetStateForSavingLinks.hide()
                                        shouldBtmSheetForNewLinkAdditionBeEnabled.value = false
                                        isDataExtractingForTheLink.value = false
                                    }
                                },
                            )
                        }

                        SpecificScreenType.SAVED_LINKS_SCREEN -> {
                            createVM.addANewLinkInSavedLinks(
                                autoDetectTitle = isAutoDetectSelected,
                                title = title,
                                webURL = webURL,
                                noteForSaving = note,
                                context = context,
                                onTaskCompleted = {
                                    coroutineScope.launch {
                                        btmModalSheetStateForSavingLinks.hide()
                                        shouldBtmSheetForNewLinkAdditionBeEnabled.value = false
                                        isDataExtractingForTheLink.value = false
                                    }
                                },
                            )
                        }

                        else -> {}
                    }
                },
                onFolderCreated = {},
                parentFolderID = CollectionsScreenVM.currentClickedFolderData.value.id,
                isDataExtractingForTheLink = isDataExtractingForTheLink
            )
        )
        OptionsBtmSheetUI(
            OptionsBtmSheetUIParam(
                inSpecificArchiveScreen = mutableStateOf(SpecificCollectionsScreenVM.screenType.value == SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN),
                inArchiveScreen = mutableStateOf(SpecificCollectionsScreenVM.screenType.value == SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN),
                btmModalSheetState = btmModalSheetState,
                shouldBtmModalSheetBeVisible = shouldOptionsBtmModalSheetBeVisible,
                btmSheetFor = when (SpecificCollectionsScreenVM.screenType.value) {
                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN
                    SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> SpecificCollectionsScreenVM.selectedBtmSheetType.value
                    SpecificScreenType.SAVED_LINKS_SCREEN -> OptionsBtmSheetType.LINK
                    SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> SpecificCollectionsScreenVM.selectedBtmSheetType.value
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
                                folderID = CollectionsScreenVM.currentClickedFolderData.value.id,
                                sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                                    SettingsScreenVM.Settings.selectedSortingType.value
                                )
                            )
                        }, ImportantLinks(
                            title = tempImpLinkData.title.value,
                            webURL = tempImpLinkData.webURL.value,
                            baseURL = tempImpLinkData.baseURL.value,
                            imgURL = tempImpLinkData.imgURL.value,
                            infoForSaving = tempImpLinkData.infoForSaving.value
                        )
                    )
                },
                importantLinks = null,
                forAChildFolder = if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) mutableStateOf(
                    false
                ) else mutableStateOf(true),
                onArchiveClick = {
                    specificCollectionsScreenVM.onArchiveClick(
                        ImportantLinks(
                            title = tempImpLinkData.title.value,
                            webURL = tempImpLinkData.webURL.value,
                            baseURL = tempImpLinkData.baseURL.value,
                            imgURL = tempImpLinkData.imgURL.value,
                            infoForSaving = tempImpLinkData.infoForSaving.value
                        ),
                        context,
                        linkID = tempImpLinkData.id,
                        onTaskCompleted = {
                            specificCollectionsScreenVM.changeRetrievedData(
                                folderID = CollectionsScreenVM.currentClickedFolderData.value.id,
                                sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                                    SettingsScreenVM.Settings.selectedSortingType.value
                                )
                            )
                        },
                    )
                },
                noteForSaving = selectedURLOrFolderNote.value,
                onNoteDeleteCardClick = {
                    specificCollectionsScreenVM.onNoteDeleteCardClick(
                        selectedWebURL.value,
                        context,
                        folderID = CollectionsScreenVM.selectedFolderData.value.id,
                        folderName = CollectionsScreenVM.selectedFolderData.value.folderName,
                        linkID = CollectionsScreenVM.selectedFolderData.value.id
                    )
                },
                folderName = selectedURLTitle.value,
                linkTitle = tempImpLinkData.title.value
            )
        )
        val totalFoldersCount = remember(CollectionsScreenVM.selectedFolderData) {
            mutableLongStateOf(
                CollectionsScreenVM.selectedFolderData.value.childFolderIDs?.size?.toLong() ?: 0
            )
        }
        DeleteDialogBox(
            DeleteDialogBoxParam(
                totalIds = mutableLongStateOf(
                    CollectionsScreenVM.selectedFolderData.value.childFolderIDs?.size?.toLong() ?: 0
                ),
                shouldDialogBoxAppear = shouldDeleteDialogBeVisible,
                onDeleteClick = {
                    specificCollectionsScreenVM.onDeleteClick(
                        folderID = CollectionsScreenVM.selectedFolderData.value.id,
                        selectedWebURL = selectedWebURL.value,
                        context = context,
                        onTaskCompleted = {
                            specificCollectionsScreenVM.changeRetrievedData(
                                folderID = CollectionsScreenVM.currentClickedFolderData.value.id,
                                sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                                    SettingsScreenVM.Settings.selectedSortingType.value
                                )
                            )
                        },
                        folderName = CollectionsScreenVM.selectedFolderData.value.folderName,
                        linkID = CollectionsScreenVM.selectedFolderData.value.id
                    )
                },
                deleteDialogBoxType = if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) DataDialogBoxType.LINK else DataDialogBoxType.FOLDER,
                onDeleted = {
                    specificCollectionsScreenVM.changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        ),
                        folderID = CollectionsScreenVM.currentClickedFolderData.value.id
                    )
                })
        )
        val updateVM: UpdateVM = viewModel()
        RenameDialogBox(
            RenameDialogBoxParam(
                shouldDialogBoxAppear = shouldRenameDialogBeVisible,
                existingFolderName = CollectionsScreenVM.selectedFolderData.value.folderName,
                renameDialogBoxFor = SpecificCollectionsScreenVM.selectedBtmSheetType.value,
                onNoteChangeClick = { newNote: String ->
                    if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.FOLDER) {
                        updateVM.updateFolderNote(
                            CollectionsScreenVM.selectedFolderData.value.id,
                            newNote
                        )
                    } else {
                        when (SpecificCollectionsScreenVM.screenType.value) {
                            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> updateVM.updateImpLinkNote(
                                CollectionsScreenVM.selectedFolderData.value.id,
                                newNote
                            )

                            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> updateVM.updateRegularLinkNote(
                                CollectionsScreenVM.selectedFolderData.value.id,
                                newNote
                            )

                            SpecificScreenType.SAVED_LINKS_SCREEN -> updateVM.updateRegularLinkNote(
                                CollectionsScreenVM.selectedFolderData.value.id,
                                newNote
                            )

                            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> updateVM.updateRegularLinkNote(
                                CollectionsScreenVM.selectedFolderData.value.id,
                                newNote
                            )

                            else -> {}
                        }
                    }
                    shouldRenameDialogBeVisible.value = false
                },
                onTitleChangeClick = { newTitle: String ->
                    if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.FOLDER) {
                        updateVM.updateFolderName(
                            CollectionsScreenVM.selectedFolderData.value.id,
                            newTitle
                        )
                    } else {
                        when (SpecificCollectionsScreenVM.screenType.value) {
                            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> updateVM.updateImpLinkTitle(
                                CollectionsScreenVM.selectedFolderData.value.id,
                                newTitle
                            )

                            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> updateVM.updateRegularLinkTitle(
                                CollectionsScreenVM.selectedFolderData.value.id,
                                newTitle
                            )

                            SpecificScreenType.SAVED_LINKS_SCREEN -> updateVM.updateRegularLinkTitle(
                                CollectionsScreenVM.selectedFolderData.value.id,
                                newTitle
                            )

                            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> updateVM.updateRegularLinkTitle(
                                CollectionsScreenVM.selectedFolderData.value.id,
                                newTitle
                            )

                            else -> {}
                        }
                    }
                    shouldRenameDialogBeVisible.value = false
                }
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
                parentFolderID = CollectionsScreenVM.currentClickedFolderData.value.id,
                inAChildFolderScreen = true
            )
        )
        AddNewLinkDialogBox(
            shouldDialogBoxAppear = shouldNewLinkDialogBoxBeVisible,
            screenType = SpecificCollectionsScreenVM.screenType.value,
            parentFolderID = CollectionsScreenVM.currentClickedFolderData.value.id,
            onSaveClick = { isAutoDetectSelected: Boolean, webURL: String, title: String, note: String, selectedDefaultFolderName: String?, selectedNonDefaultFolderID: Long? ->
                isDataExtractingForTheLink.value = true
                when (SpecificCollectionsScreenVM.screenType.value) {
                    SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                        createVM.addANewLinkInAFolderV10(
                            autoDetectTitle = isAutoDetectSelected,
                            title = title,
                            webURL = webURL,
                            noteForSaving = note,
                            parentFolderID = CollectionsScreenVM.currentClickedFolderData.value.id,
                            context = context,
                            onTaskCompleted = {
                                shouldNewLinkDialogBoxBeVisible.value = false
                                isDataExtractingForTheLink.value = false
                            },
                            folderName = CollectionsScreenVM.currentClickedFolderData.value.folderName
                        )
                    }

                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                        createVM.addANewLinkInImpLinks(
                            autoDetectTitle = isAutoDetectSelected,
                            title = title,
                            webURL = webURL,
                            noteForSaving = note,
                            context = context,
                            onTaskCompleted = {
                                shouldNewLinkDialogBoxBeVisible.value = false
                                isDataExtractingForTheLink.value = false
                            },
                        )
                    }

                    SpecificScreenType.SAVED_LINKS_SCREEN -> {
                        createVM.addANewLinkInSavedLinks(
                            autoDetectTitle = isAutoDetectSelected,
                            title = title,
                            webURL = webURL,
                            noteForSaving = note,
                            context = context,
                            onTaskCompleted = {
                                shouldNewLinkDialogBoxBeVisible.value = false
                                isDataExtractingForTheLink.value = false
                            },
                        )
                    }

                    else -> {}
                }
            },
            isDataExtractingForTheLink = isDataExtractingForTheLink.value
        )
        SortingBottomSheetUI(
            SortingBottomSheetUIParam(
                shouldBottomSheetVisible = shouldSortingBottomSheetAppear,
                onSelectedAComponent = { sortingPreferences, isLinksSortingSelected, isFoldersSortingSelected ->
                    specificCollectionsScreenVM.changeRetrievedData(
                        sortingPreferences = sortingPreferences,
                        folderID = CollectionsScreenVM.currentClickedFolderData.value.id,
                        isLinksSortingSelected = isLinksSortingSelected,
                        isFoldersSortingSelected = isFoldersSortingSelected
                    )
                },
                bottomModalSheetState = sortingBtmSheetState,
                sortingBtmSheetType = when (SpecificCollectionsScreenVM.screenType.value) {
                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> SortingBtmSheetType.IMPORTANT_LINKS_SCREEN
                    SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> SortingBtmSheetType.ARCHIVE_FOLDER_SCREEN
                    SpecificScreenType.SAVED_LINKS_SCREEN -> SortingBtmSheetType.SAVED_LINKS_SCREEN
                    SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> SortingBtmSheetType.REGULAR_FOLDER_SCREEN
                    SpecificScreenType.INTENT_ACTIVITY -> SortingBtmSheetType.COLLECTIONS_SCREEN
                    SpecificScreenType.ROOT_SCREEN -> SortingBtmSheetType.REGULAR_FOLDER_SCREEN
                },
                shouldFoldersSelectionBeVisible = when (SpecificCollectionsScreenVM.screenType.value) {
                    SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> mutableStateOf(
                        archivedSubFoldersData.foldersTableList.isNotEmpty()
                    )

                    SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> mutableStateOf(
                        childFoldersData.foldersTableList.isNotEmpty()
                    )

                    else -> mutableStateOf(false)
                },
                shouldLinksSelectionBeVisible = when (SpecificCollectionsScreenVM.screenType.value) {
                    SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> mutableStateOf(
                        archivedFoldersLinksData.linksTableList.isNotEmpty()
                    )

                    SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> mutableStateOf(
                        specificFolderLinksData.linksTableList.isNotEmpty()
                    )

                    else -> mutableStateOf(false)
                },
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
        } else if (areLinksSelectable.value) {
            areLinksSelectable.value = false
            specificCollectionsScreenVM.areAllItemsChecked.value = false
            specificCollectionsScreenVM.changeAllItemsSelectedData()
            specificCollectionsScreenVM.removeAllSelections()
        } else {
            if (CollectionsScreenVM.currentClickedFolderData.value.parentFolderID != null
                && (SpecificCollectionsScreenVM.screenType.value == SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                        || SpecificCollectionsScreenVM.screenType.value == SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN)
            ) {
                if (SpecificCollectionsScreenVM.inARegularFolder.value) {
                    SpecificCollectionsScreenVM.screenType.value =
                        SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                } else {
                    SpecificCollectionsScreenVM.screenType.value =
                        SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN
                }
                specificCollectionsScreenVM.updateFolderData(CollectionsScreenVM.currentClickedFolderData.value.parentFolderID!!)
            }
            navController.popBackStack()
        }
    }
}