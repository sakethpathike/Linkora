package com.sakethh.linkora.ui.screens.collections.savedLinks

import android.widget.Toast
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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.bottomSheets.menu.MenuBtmSheetParam
import com.sakethh.linkora.ui.bottomSheets.menu.MenuBtmSheetUI
import com.sakethh.linkora.ui.bottomSheets.sorting.SortingBottomSheetParam
import com.sakethh.linkora.ui.bottomSheets.sorting.SortingBottomSheetUI
import com.sakethh.linkora.ui.bottomSheets.sorting.SortingBtmSheetType
import com.sakethh.linkora.ui.commonComposables.AddANewLinkDialogBox
import com.sakethh.linkora.ui.commonComposables.DataDialogBoxType
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBox
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.FloatingActionBtn
import com.sakethh.linkora.ui.commonComposables.FloatingActionBtnParam
import com.sakethh.linkora.ui.commonComposables.RenameDialogBox
import com.sakethh.linkora.ui.commonComposables.RenameDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.link_views.LinkUIComponentParam
import com.sakethh.linkora.ui.commonComposables.link_views.components.GridViewLinkUIComponent
import com.sakethh.linkora.ui.commonComposables.link_views.components.ListViewLinkUIComponent
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.OptionsBtmSheetType
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.OptionsBtmSheetVM
import com.sakethh.linkora.ui.screens.DataEmptyScreen
import com.sakethh.linkora.ui.screens.collections.CollectionsScreenVM
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenUIEvent
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenVM
import com.sakethh.linkora.ui.screens.collections.specific.SpecificScreenType
import com.sakethh.linkora.ui.screens.home.HomeScreenVM
import com.sakethh.linkora.ui.screens.linkLayout.LinkLayout
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SortingPreferences
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedLinksScreen(navController: NavController) {
    val specificCollectionsScreenVM: SpecificCollectionsScreenVM = hiltViewModel()
    val context = LocalContext.current
    val shouldDeleteDialogBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = Unit) {
        specificCollectionsScreenVM.eventChannel.collectLatest {
            when (it) {
                is CommonUiEvent.ShowToast -> {
                    Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                }

                CommonUiEvent.ShowDeleteDialogBox -> shouldDeleteDialogBeVisible.value = true
            }
        }
    }
    val selectedWebURL = rememberSaveable {
        mutableStateOf("")
    }
    val selectedImageURL = rememberSaveable {
        mutableStateOf("")
    }
    val isDataExtractingForTheLink = rememberSaveable {
        mutableStateOf(false)
    }
    val savedLinksData =
        specificCollectionsScreenVM.savedLinksTable.collectAsStateWithLifecycle().value
    val btmModalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val btmModalSheetStateForSavingLink =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shouldOptionsBtmModalSheetBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldRenameDialogBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldSortingBottomSheetAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val sortingBtmSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val selectedURLOrFolderNote = rememberSaveable {
        mutableStateOf("")
    }
    val selectedItemTitle = rememberSaveable {
        mutableStateOf("")
    }
    val selectedItemNote = rememberSaveable {
        mutableStateOf("")
    }
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()
    val optionsBtmSheetVM: OptionsBtmSheetVM = hiltViewModel()
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
    val areElementsSelectable = rememberSaveable {
        mutableStateOf(false)
    }

    fun commonLinkComponentParam(linkData: LinksTable): LinkUIComponentParam {
        return LinkUIComponentParam(
            onLongClick = {
                if (!areElementsSelectable.value) {
                    areElementsSelectable.value = true
                    specificCollectionsScreenVM.selectedLinksID.add(
                        linkData.id
                    )
                }
            },
            isSelectionModeEnabled = areElementsSelectable,
            title = linkData.title,
            webBaseURL = linkData.baseURL,
            imgURL = linkData.imgURL,
            onMoreIconClick = {
                selectedImageURL.value = linkData.imgURL
                selectedItemTitle.value = linkData.title
                selectedItemNote.value = linkData.infoForSaving
                SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                    OptionsBtmSheetType.LINK
                CollectionsScreenVM.selectedFolderData.value.id =
                    linkData.id
                selectedWebURL.value = linkData.webURL
                selectedURLOrFolderNote.value = linkData.infoForSaving
                HomeScreenVM.tempImpLinkData.apply {
                    this.webURL = linkData.webURL
                    this.baseURL = linkData.baseURL
                    this.imgURL = linkData.imgURL
                    this.title = linkData.title
                    this.infoForSaving = linkData.infoForSaving
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
                if (areElementsSelectable.value) {
                    if (!specificCollectionsScreenVM.selectedLinksID.contains(
                            linkData.id
                        )
                    ) {
                        specificCollectionsScreenVM.selectedLinksID.add(
                            linkData.id
                        )
                    } else {
                        specificCollectionsScreenVM.selectedLinksID.remove(
                            linkData.id
                        )
                    }
                } else {
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
                        forceOpenInExternalBrowser = false,
                        onTaskCompleted = {}
                    )
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
            isItemSelected = mutableStateOf(
                specificCollectionsScreenVM.selectedLinksID.contains(
                    linkData.id
                )
            )
        )
    }

    LinkoraTheme {
        Scaffold(floatingActionButtonPosition = FabPosition.End, floatingActionButton = {
            if (!areElementsSelectable.value && SpecificCollectionsScreenVM.screenType.value == SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN) {
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
            } else if (!areElementsSelectable.value && SpecificCollectionsScreenVM.screenType.value != SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN && SpecificCollectionsScreenVM.screenType.value != SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN) {
                FloatingActionButton(onClick = {
                    if (!SettingsPreference.isBtmSheetEnabledForSavingLinks.value) {
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
                    if (!areElementsSelectable.value) {
                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                            navController.navigateUp()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    } else {
                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                            areElementsSelectable.value = false
                            specificCollectionsScreenVM.areAllLinksChecked.value = false
                            specificCollectionsScreenVM.areAllFoldersChecked.value = false
                            specificCollectionsScreenVM.selectedImpLinks.clear()
                            specificCollectionsScreenVM.removeAllLinkSelections()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Cancel, contentDescription = null
                            )
                        }
                    }
                }, scrollBehavior = scrollBehavior,
                    title = {
                        if (areElementsSelectable.value) {
                            Row {
                                AnimatedContent(
                                    targetState = specificCollectionsScreenVM.selectedLinksID.size,
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
                                    text = if (specificCollectionsScreenVM.selectedLinksID.size == 1) " link " else {
                                        " links "
                                    } + "selected",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontSize = 18.sp
                                )
                            }
                        } else {
                            Text(
                                text = LocalizedStrings.savedLinks.value,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 18.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    actions = {
                        if (areElementsSelectable.value && specificCollectionsScreenVM.selectedLinksID.size != 0) {
                            IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                shouldDeleteDialogBeVisible.value = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null
                                )
                            }
                            IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                specificCollectionsScreenVM.moveMultipleLinksFromLinksTableToArchive()
                                areElementsSelectable.value = false
                                specificCollectionsScreenVM.areAllLinksChecked.value =
                                    false
                                specificCollectionsScreenVM.areAllFoldersChecked.value =
                                    false
                                specificCollectionsScreenVM.removeAllLinkSelections()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Archive,
                                    contentDescription = null
                                )
                            }
                        } else if (savedLinksData.isNotEmpty()) {
                            IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                shouldSortingBottomSheetAppear.value = true
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.Sort,
                                    contentDescription = null
                                )
                            }
                        }
                    })
            }
        }) {
            val commonModifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(it)
                .fillMaxSize()
                .animateContentSize()
            if (savedLinksData.isNotEmpty()) {
                when (SettingsPreference.currentlySelectedLinkLayout.value) {
                    LinkLayout.REGULAR_LIST_VIEW.name, LinkLayout.TITLE_ONLY_LIST_VIEW.name -> {
                        LazyColumn(
                            modifier = commonModifier
                        ) {
                            itemsIndexed(
                                items = savedLinksData,
                                key = { _, linksTable ->
                                    linksTable.baseURL + linksTable.id.toString() + linksTable.webURL
                                }) { linkIndex, linkData ->
                                ListViewLinkUIComponent(
                                    commonLinkComponentParam(linkData),
                                    forTitleOnlyView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.TITLE_ONLY_LIST_VIEW.name
                                )
                            }
                        }
                    }

                    LinkLayout.GRID_VIEW.name -> {
                        LazyVerticalGrid(
                            GridCells.Adaptive(150.dp),
                            modifier = commonModifier
                        ) {
                            itemsIndexed(
                                items = savedLinksData,
                                key = { _, linksTable ->
                                    linksTable.baseURL + linksTable.id.toString() + linksTable.webURL
                                }) { linkIndex, linkData ->
                                GridViewLinkUIComponent(
                                    commonLinkComponentParam(linkData),
                                    forStaggeredView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name
                                )
                            }
                        }
                    }

                    LinkLayout.STAGGERED_VIEW.name -> {
                        LazyVerticalStaggeredGrid(
                            modifier = commonModifier,
                            columns = StaggeredGridCells.Adaptive(150.dp)
                        ) {
                            itemsIndexed(
                                items = savedLinksData,
                                key = { _, linksTable ->
                                    linksTable.baseURL + linksTable.id.toString() + linksTable.webURL
                                }) { linkIndex, linkData ->
                                GridViewLinkUIComponent(
                                    commonLinkComponentParam(linkData),
                                    forStaggeredView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name
                                )
                            }
                        }
                    }
                }
            } else {
                DataEmptyScreen(text = LocalizedStrings.noLinksWereFound.value)
            }
            if (shouldScreenTransparencyDecreasedBoxVisible.value) {
                Box(modifier = Modifier
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
        MenuBtmSheetUI(
            MenuBtmSheetParam(
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
                forAChildFolder = if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) mutableStateOf(
                    false
                ) else mutableStateOf(true),
                onImportantLinkClick = {
                    specificCollectionsScreenVM.onUiEvent(
                        SpecificCollectionsScreenUIEvent.AddExistingLinkToImportantLink(
                            ImportantLinks(
                                title = HomeScreenVM.tempImpLinkData.title,
                                webURL = HomeScreenVM.tempImpLinkData.webURL,
                                baseURL = HomeScreenVM.tempImpLinkData.baseURL,
                                imgURL = HomeScreenVM.tempImpLinkData.imgURL,
                                infoForSaving = HomeScreenVM.tempImpLinkData.infoForSaving
                            )
                        )
                    )
                },
                onArchiveClick = {
                    specificCollectionsScreenVM.onArchiveClick(
                        ImportantLinks(
                            title = HomeScreenVM.tempImpLinkData.title,
                            webURL = HomeScreenVM.tempImpLinkData.webURL,
                            baseURL = HomeScreenVM.tempImpLinkData.baseURL,
                            imgURL = HomeScreenVM.tempImpLinkData.imgURL,
                            infoForSaving = HomeScreenVM.tempImpLinkData.infoForSaving
                        ),
                        context,
                        linkID = HomeScreenVM.tempImpLinkData.id,
                        onTaskCompleted = {
                            specificCollectionsScreenVM.changeRetrievedData(
                                folderID = CollectionsScreenVM.currentClickedFolderData.value.id,
                                sortingPreferences = SortingPreferences.valueOf(
                                    SettingsPreference.selectedSortingType.value
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
                folderName = selectedItemTitle.value,
                linkTitle = selectedItemTitle.value,
                imgLink = selectedImageURL.value,
                onRefreshClick = {
                    specificCollectionsScreenVM.reloadLinkData(
                        CollectionsScreenVM.selectedFolderData.value.id,
                        HomeScreenVM.HomeScreenType.CUSTOM_LIST
                    )
                }
            )
        )
        DeleteDialogBox(
            DeleteDialogBoxParam(
                areFoldersSelectable = areElementsSelectable.value,
                totalIds = mutableLongStateOf(
                    CollectionsScreenVM.selectedFolderData.value.childFolderIDs?.size?.toLong() ?: 0
                ),
                shouldDialogBoxAppear = shouldDeleteDialogBeVisible,
                onDeleteClick = {
                    if (areElementsSelectable.value) {
                        specificCollectionsScreenVM.onDeleteMultipleSelectedFolders()
                        specificCollectionsScreenVM.onDeleteMultipleSelectedLinks()
                        areElementsSelectable.value = false
                        specificCollectionsScreenVM.areAllLinksChecked.value = false
                        specificCollectionsScreenVM.removeAllLinkSelections()
                        specificCollectionsScreenVM.changeRetrievedData(
                            folderID = CollectionsScreenVM.currentClickedFolderData.value.id,
                            sortingPreferences = SortingPreferences.valueOf(
                                SettingsPreference.selectedSortingType.value
                            )
                        )
                    } else {
                        specificCollectionsScreenVM.onDeleteClick(
                            folderID = CollectionsScreenVM.selectedFolderData.value.id,
                            context = context,
                            onTaskCompleted = {
                                specificCollectionsScreenVM.changeRetrievedData(
                                    folderID = CollectionsScreenVM.currentClickedFolderData.value.id,
                                    sortingPreferences = SortingPreferences.valueOf(
                                        SettingsPreference.selectedSortingType.value
                                    )
                                )
                            },
                            linkID = CollectionsScreenVM.selectedFolderData.value.id
                        )
                    }
                },
                deleteDialogBoxType = if (areElementsSelectable.value) DataDialogBoxType.SELECTED_DATA else DataDialogBoxType.LINK,
                onDeleted = {
                    specificCollectionsScreenVM.changeRetrievedData(
                        sortingPreferences = SortingPreferences.valueOf(
                            SettingsPreference.selectedSortingType.value
                        ),
                        folderID = CollectionsScreenVM.currentClickedFolderData.value.id
                    )
                })
        )
        RenameDialogBox(
            RenameDialogBoxParam(
                shouldDialogBoxAppear = shouldRenameDialogBeVisible,
                existingFolderName = CollectionsScreenVM.selectedFolderData.value.folderName,
                renameDialogBoxFor = SpecificCollectionsScreenVM.selectedBtmSheetType.value,
                onNoteChangeClick = { newNote: String ->
                    SpecificCollectionsScreenUIEvent.UpdateRegularLinkNote(
                        CollectionsScreenVM.selectedFolderData.value.id,
                        newNote
                    )
                    shouldRenameDialogBeVisible.value = false
                },
                onTitleChangeClick = { newTitle: String ->
                    SpecificCollectionsScreenUIEvent.UpdateRegularLinkTitle(
                        newTitle,
                        CollectionsScreenVM.selectedFolderData.value.id
                    )
                    shouldRenameDialogBeVisible.value = false
                }, existingTitle = selectedItemTitle.value, existingNote = selectedItemNote.value
            )
        )
        AddANewLinkDialogBox(
            shouldDialogBoxAppear = shouldNewLinkDialogBoxBeVisible,
            screenType = SpecificCollectionsScreenVM.screenType.value,
            onSaveClick = { isAutoDetectSelected: Boolean, webURL: String, title: String, note: String, selectedDefaultFolderName: String?, selectedNonDefaultFolderID: Long? ->
                isDataExtractingForTheLink.value = true
                specificCollectionsScreenVM.onUiEvent(
                    SpecificCollectionsScreenUIEvent.AddANewLinkInSavedLinks(
                        autoDetectTitle = isAutoDetectSelected,
                        title = title,
                        webURL = webURL,
                        noteForSaving = note,
                        onTaskCompleted = {
                            shouldNewLinkDialogBoxBeVisible.value = false
                            isDataExtractingForTheLink.value = false
                        },
                    )
                )
            },
            isDataExtractingForTheLink = isDataExtractingForTheLink.value,
            onFolderCreateClick = { _, _ -> })
        SortingBottomSheetUI(
            SortingBottomSheetParam(
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
                sortingBtmSheetType = SortingBtmSheetType.SAVED_LINKS_SCREEN,
                shouldFoldersSelectionBeVisible = mutableStateOf(false),
                shouldLinksSelectionBeVisible = mutableStateOf(false),
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
        } else if (areElementsSelectable.value) {
            areElementsSelectable.value = false
            specificCollectionsScreenVM.areAllLinksChecked.value = false
            specificCollectionsScreenVM.removeAllLinkSelections()
        } else {
            navController.navigateUp()
        }
    }
}