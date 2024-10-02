package com.sakethh.linkora.ui.screens.home

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.CustomWebTab
import com.sakethh.linkora.ui.bottomSheets.menu.MenuBtmSheetParam
import com.sakethh.linkora.ui.bottomSheets.menu.MenuBtmSheetUI
import com.sakethh.linkora.ui.commonComposables.DataDialogBoxType
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBox
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.RenameDialogBox
import com.sakethh.linkora.ui.commonComposables.RenameDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.link_views.LinkUIComponentParam
import com.sakethh.linkora.ui.commonComposables.link_views.components.GridViewLinkUIComponent
import com.sakethh.linkora.ui.commonComposables.link_views.components.ListViewLinkUIComponent
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.OptionsBtmSheetType
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.OptionsBtmSheetVM
import com.sakethh.linkora.ui.navigation.NavigationRoutes
import com.sakethh.linkora.ui.screens.DataEmptyScreen
import com.sakethh.linkora.ui.screens.collections.CollectionsScreenVM
import com.sakethh.linkora.ui.screens.collections.FolderIndividualComponent
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenUIEvent
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenVM
import com.sakethh.linkora.ui.screens.collections.specific.SpecificScreenType
import com.sakethh.linkora.ui.screens.linkLayout.LinkLayout
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SortingPreferences
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildHomeScreen(
    homeScreenType: HomeScreenVM.HomeScreenType,
    navController: NavController,
    folderLinksData: List<LinksTable>,
    childFoldersData: List<FoldersTable>,
    customWebTab: CustomWebTab
) {
    val homeScreenVM: HomeScreenVM = hiltViewModel()
    HomeScreenVM.currentHomeScreenType = homeScreenType
    val specificCollectionsScreenVM: SpecificCollectionsScreenVM = hiltViewModel()
    LaunchedEffect(key1 = Unit) {
        awaitAll(async {
            if (homeScreenType == HomeScreenVM.HomeScreenType.SAVED_LINKS) {
                specificCollectionsScreenVM.changeRetrievedData(
                    sortingPreferences = SortingPreferences.valueOf(
                        SettingsPreference.selectedSortingType.value
                    ),
                    folderID = 0,
                    screenType = SpecificScreenType.SAVED_LINKS_SCREEN
                )
            }
        }, async {
            if (homeScreenType == HomeScreenVM.HomeScreenType.IMP_LINKS) {
                specificCollectionsScreenVM.changeRetrievedData(
                    sortingPreferences = SortingPreferences.valueOf(
                        SettingsPreference.selectedSortingType.value
                    ),
                    folderID = 0,
                    screenType = SpecificScreenType.IMPORTANT_LINKS_SCREEN
                )
            }
        })
    }
    val context = LocalContext.current
    val shouldDeleteDialogBoxAppear = rememberSaveable {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = Unit) {
        homeScreenVM.eventChannel.collectLatest {
            when (it) {
                is CommonUiEvent.ShowToast -> {
                    Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                }

                is CommonUiEvent.ShowDeleteDialogBox -> {
                    shouldDeleteDialogBoxAppear.value = true
                }
            }
        }
    }
    val savedLinksData =
        specificCollectionsScreenVM.savedLinksTable.collectAsStateWithLifecycle().value
    val impLinksData = specificCollectionsScreenVM.impLinksTable.collectAsStateWithLifecycle().value
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()
    val btmModalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shouldOptionsBtmModalSheetBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldRenameDialogBoxAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val selectedWebURL = rememberSaveable {
        mutableStateOf("")
    }
    val selectedElementID = rememberSaveable {
        mutableLongStateOf(0)
    }
    val selectedURLTitle = rememberSaveable {
        mutableStateOf("")
    }
    val selectedNote = rememberSaveable {
        mutableStateOf("")
    }
    val optionsBtmSheetVM: OptionsBtmSheetVM = hiltViewModel()

    fun modifiedLinkUIComponentParam(linksTable: LinksTable): LinkUIComponentParam {
        return LinkUIComponentParam(
            onLongClick = {
                if (!homeScreenVM.isSelectionModeEnabled.value) {
                    homeScreenVM.isSelectionModeEnabled.value =
                        true
                    homeScreenVM.selectedSavedLinkIds.add(linksTable.id)
                }
            },
            isSelectionModeEnabled = homeScreenVM.isSelectionModeEnabled,
            isItemSelected = mutableStateOf(
                homeScreenVM.selectedSavedLinkIds.contains(
                    linksTable.id
                )
            ),
            title = linksTable.title,
            webBaseURL = linksTable.baseURL,
            imgURL = linksTable.imgURL,
            onMoreIconClick = {
                SpecificCollectionsScreenVM.screenType.value = when (homeScreenType) {
                    HomeScreenVM.HomeScreenType.SAVED_LINKS -> SpecificScreenType.SAVED_LINKS_SCREEN
                    HomeScreenVM.HomeScreenType.IMP_LINKS -> SpecificScreenType.IMPORTANT_LINKS_SCREEN
                    else -> SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                }
                SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                    OptionsBtmSheetType.LINK
                selectedElementID.longValue = linksTable.id
                HomeScreenVM.tempImpLinkData.baseURL = linksTable.baseURL
                HomeScreenVM.tempImpLinkData.imgURL = linksTable.imgURL
                HomeScreenVM.tempImpLinkData.webURL = linksTable.webURL
                HomeScreenVM.tempImpLinkData.title = linksTable.title
                HomeScreenVM.tempImpLinkData.infoForSaving =
                    linksTable.infoForSaving
                shouldOptionsBtmModalSheetBeVisible.value = true
                selectedWebURL.value = linksTable.webURL
                selectedNote.value = linksTable.infoForSaving
                selectedURLTitle.value = linksTable.title
                coroutineScope.launch {
                    awaitAll(async {
                        optionsBtmSheetVM.updateImportantCardData(
                            url = selectedWebURL.value
                        )
                    }, async {
                        optionsBtmSheetVM.updateArchiveLinkCardData(
                            url = selectedWebURL.value
                        )
                    }
                    )
                }
            },
            onLinkClick = {
                if (homeScreenVM.isSelectionModeEnabled.value) {
                    if (!homeScreenVM.selectedSavedLinkIds.contains(linksTable.id)) {
                        homeScreenVM.selectedSavedLinkIds.add(
                            linksTable.id
                        )
                    } else {
                        homeScreenVM.selectedSavedLinkIds.remove(
                            linksTable.id
                        )
                    }
                } else {
                    customWebTab.openInWeb(
                        recentlyVisitedData = RecentlyVisited(
                            title = linksTable.title,
                            webURL = linksTable.webURL,
                            baseURL = linksTable.baseURL,
                            imgURL = linksTable.imgURL,
                            infoForSaving = linksTable.infoForSaving
                        ), context = context, uriHandler = uriHandler,
                        forceOpenInExternalBrowser = false
                    )
                }
            },
            webURL = linksTable.webURL,
            onForceOpenInExternalBrowserClicked = {
                homeScreenVM.onLinkClick(
                    RecentlyVisited(
                        title = linksTable.title,
                        webURL = linksTable.webURL,
                        baseURL = linksTable.baseURL,
                        imgURL = linksTable.imgURL,
                        infoForSaving = linksTable.infoForSaving
                    ), context = context, uriHandler = uriHandler,
                    onTaskCompleted = {},
                    forceOpenInExternalBrowser = true
                )
            })
    }


    @Composable
    fun FolderIndividualComponentImpl(folderElement: FoldersTable) {
        FolderIndividualComponent(
            showCheckBox = homeScreenVM.isSelectionModeEnabled,
            isCheckBoxChecked = mutableStateOf(
                homeScreenVM.selectedFoldersData.contains(
                    folderElement
                )
            ),
            checkBoxState = { checkBoxState ->
                if (checkBoxState) {
                    homeScreenVM.selectedFoldersData.add(
                        folderElement
                    )
                } else {
                    homeScreenVM.selectedFoldersData.removeAll {
                        it == folderElement
                    }
                }
            },
            folderName = folderElement.folderName,
            folderNote = folderElement.infoForSaving,
            onMoreIconClick = {
                selectedNote.value = folderElement.infoForSaving
                selectedURLTitle.value = folderElement.folderName
                selectedElementID.longValue = folderElement.id
                SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                    OptionsBtmSheetType.FOLDER
                shouldOptionsBtmModalSheetBeVisible.value = true
                coroutineScope.launch {
                    awaitAll(async {
                        optionsBtmSheetVM.updateArchiveFolderCardData(
                            selectedElementID.longValue
                        )
                    }, async {
                        btmModalSheetState.expand()
                    })
                }
            },
            onFolderClick = {
                if (!homeScreenVM.isSelectionModeEnabled.value) {
                    SpecificCollectionsScreenVM.inARegularFolder.value =
                        true
                    SpecificCollectionsScreenVM.screenType.value =
                        SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                    CollectionsScreenVM.currentClickedFolderData.value =
                        folderElement
                    CollectionsScreenVM.rootFolderID = folderElement.id
                    navController.navigate(NavigationRoutes.SPECIFIC_COLLECTION_SCREEN.name)
                }
            },
            showMoreIcon = !homeScreenVM.isSelectionModeEnabled.value,
            onLongClick = {
                if (!homeScreenVM.isSelectionModeEnabled.value) {
                    homeScreenVM.isSelectionModeEnabled.value = true
                    specificCollectionsScreenVM.areAllFoldersChecked.value =
                        false
                    homeScreenVM.selectedFoldersData.add(
                        folderElement
                    )
                }
            }
        )
    }
    LinkoraTheme {
        when (SettingsPreference.currentlySelectedLinkLayout.value) {
            LinkLayout.TITLE_ONLY_LIST_VIEW.name, LinkLayout.REGULAR_LIST_VIEW.name -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    item {
                        Spacer(Modifier.height(5.dp))
                    }
                    if (homeScreenType == HomeScreenVM.HomeScreenType.SAVED_LINKS) {
                        if (savedLinksData.isNotEmpty()) {
                            items(items = savedLinksData, key = { linksTable ->
                                linksTable.id.toString() + linksTable.webURL
                            }) {
                                ListViewLinkUIComponent(
                                    forTitleOnlyView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.TITLE_ONLY_LIST_VIEW.name,
                                    linkUIComponentParam = modifiedLinkUIComponentParam(it)
                                )
                            }
                        } else {
                            item {
                                DataEmptyScreen(text = LocalizedStrings.welcomeBackToLinkora.value)
                            }
                            item {
                                Spacer(modifier = Modifier.height(165.dp))
                            }
                        }
                    } else if (homeScreenType == HomeScreenVM.HomeScreenType.IMP_LINKS) {
                        if (impLinksData.isNotEmpty()) {
                            items(items = impLinksData, key = { importantLinks ->
                                importantLinks.webURL + importantLinks.id.toString()
                            }) {
                                ListViewLinkUIComponent(
                                    forTitleOnlyView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.TITLE_ONLY_LIST_VIEW.name,
                                    linkUIComponentParam = modifiedLinkUIComponentParam(
                                        LinksTable(
                                            id = it.id,
                                            title = it.title,
                                            webURL = it.webURL,
                                            baseURL = it.baseURL,
                                            imgURL = it.imgURL,
                                            infoForSaving = it.infoForSaving,
                                            isLinkedWithSavedLinks = false,
                                            isLinkedWithFolders = false,
                                            keyOfLinkedFolderV10 = null,
                                            keyOfLinkedFolder = null,
                                            isLinkedWithImpFolder = false,
                                            keyOfImpLinkedFolder = "",
                                            keyOfImpLinkedFolderV10 = null,
                                            isLinkedWithArchivedFolder = false,
                                            keyOfArchiveLinkedFolderV10 = null,
                                            keyOfArchiveLinkedFolder = null
                                        )
                                    )
                                )
                            }
                        } else {
                            item {
                                DataEmptyScreen(text = LocalizedStrings.noImportantLinksWereFound.value)
                            }
                        }
                    } else {
                        if (childFoldersData.isNotEmpty()) {
                            items(childFoldersData) { folderElement ->
                                FolderIndividualComponentImpl(folderElement)
                            }
                        }
                        if (folderLinksData.isNotEmpty()) {
                            itemsIndexed(items = folderLinksData) { index, it ->
                                ListViewLinkUIComponent(
                                    forTitleOnlyView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.TITLE_ONLY_LIST_VIEW.name,
                                    linkUIComponentParam = modifiedLinkUIComponentParam(it)
                                )
                            }
                        } else {
                            item {
                                DataEmptyScreen(text = LocalizedStrings.noLinksWereFound.value)
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(350.dp))
                    }
                }
            }

            LinkLayout.GRID_VIEW.name -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(150.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 8.dp, end = 8.dp)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    item(span = {
                        GridItemSpan(maxLineSpan)
                    }) {
                        Spacer(Modifier.height(5.dp))
                    }
                    if (homeScreenType == HomeScreenVM.HomeScreenType.SAVED_LINKS) {
                        if (savedLinksData.isNotEmpty()) {
                            items(items = savedLinksData, key = { linksTable ->
                                linksTable.id.toString() + linksTable.webURL
                            }) {
                                GridViewLinkUIComponent(
                                    forStaggeredView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name,
                                    linkUIComponentParam = modifiedLinkUIComponentParam(it)
                                )
                            }
                        } else {
                            item(span = {
                                GridItemSpan(maxLineSpan)
                            }) {
                                DataEmptyScreen(text = LocalizedStrings.welcomeBackToLinkora.value)
                            }
                            item(span = {
                                GridItemSpan(maxLineSpan)
                            }) {
                                Spacer(modifier = Modifier.height(165.dp))
                            }
                        }
                    } else if (homeScreenType == HomeScreenVM.HomeScreenType.IMP_LINKS) {
                        if (impLinksData.isNotEmpty()) {
                            items(items = impLinksData, key = { importantLinks ->
                                importantLinks.webURL + importantLinks.id.toString()
                            }) {
                                GridViewLinkUIComponent(
                                    forStaggeredView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name,
                                    linkUIComponentParam = modifiedLinkUIComponentParam(
                                        LinksTable(
                                            id = it.id,
                                            title = it.title,
                                            webURL = it.webURL,
                                            baseURL = it.baseURL,
                                            imgURL = it.imgURL,
                                            infoForSaving = it.infoForSaving,
                                            isLinkedWithSavedLinks = false,
                                            isLinkedWithFolders = false,
                                            keyOfLinkedFolderV10 = null,
                                            keyOfLinkedFolder = null,
                                            isLinkedWithImpFolder = false,
                                            keyOfImpLinkedFolder = "",
                                            keyOfImpLinkedFolderV10 = null,
                                            isLinkedWithArchivedFolder = false,
                                            keyOfArchiveLinkedFolderV10 = null,
                                            keyOfArchiveLinkedFolder = null
                                        )
                                    )
                                )
                            }
                        } else {
                            item(span = {
                                GridItemSpan(maxLineSpan)
                            }) {
                                DataEmptyScreen(text = LocalizedStrings.noImportantLinksWereFound.value)
                            }
                        }
                    } else {
                        if (childFoldersData.isNotEmpty()) {
                            items(childFoldersData, span = {
                                GridItemSpan(maxLineSpan)
                            }) { folderElement ->
                                FolderIndividualComponentImpl(folderElement)
                            }
                            item(span = {
                                GridItemSpan(maxLineSpan)
                            }) {
                                Spacer(Modifier.height(5.dp))
                            }
                        }
                        if (folderLinksData.isNotEmpty()) {
                            itemsIndexed(items = folderLinksData) { index, it ->
                                GridViewLinkUIComponent(
                                    forStaggeredView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name,
                                    linkUIComponentParam = modifiedLinkUIComponentParam(it)
                                )
                            }
                        } else {
                            item(span = {
                                GridItemSpan(maxLineSpan)
                            }) {
                                DataEmptyScreen(text = LocalizedStrings.noLinksWereFound.value)
                            }
                        }
                    }
                    item(span = {
                        GridItemSpan(maxLineSpan)
                    }) {
                        Spacer(modifier = Modifier.height(350.dp))
                    }
                }
            }

            LinkLayout.STAGGERED_VIEW.name -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(150.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 8.dp, end = 8.dp)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Spacer(Modifier.height(5.dp))
                    }
                    if (homeScreenType == HomeScreenVM.HomeScreenType.SAVED_LINKS) {
                        if (savedLinksData.isNotEmpty()) {
                            items(items = savedLinksData, key = { linksTable ->
                                linksTable.id.toString() + linksTable.webURL
                            }) {
                                GridViewLinkUIComponent(
                                    forStaggeredView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name,
                                    linkUIComponentParam = modifiedLinkUIComponentParam(it)
                                )
                            }
                        } else {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                DataEmptyScreen(text = LocalizedStrings.welcomeBackToLinkora.value)
                            }
                            item(span = StaggeredGridItemSpan.FullLine) {
                                Spacer(modifier = Modifier.height(165.dp))
                            }
                        }
                    } else if (homeScreenType == HomeScreenVM.HomeScreenType.IMP_LINKS) {
                        if (impLinksData.isNotEmpty()) {
                            items(items = impLinksData, key = { importantLinks ->
                                importantLinks.webURL + importantLinks.id.toString()
                            }) {
                                GridViewLinkUIComponent(
                                    forStaggeredView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name,
                                    linkUIComponentParam = modifiedLinkUIComponentParam(
                                        LinksTable(
                                            id = it.id,
                                            title = it.title,
                                            webURL = it.webURL,
                                            baseURL = it.baseURL,
                                            imgURL = it.imgURL,
                                            infoForSaving = it.infoForSaving,
                                            isLinkedWithSavedLinks = false,
                                            isLinkedWithFolders = false,
                                            keyOfLinkedFolderV10 = null,
                                            keyOfLinkedFolder = null,
                                            isLinkedWithImpFolder = false,
                                            keyOfImpLinkedFolder = "",
                                            keyOfImpLinkedFolderV10 = null,
                                            isLinkedWithArchivedFolder = false,
                                            keyOfArchiveLinkedFolderV10 = null,
                                            keyOfArchiveLinkedFolder = null
                                        )
                                    )
                                )
                            }
                        } else {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                DataEmptyScreen(text = LocalizedStrings.noImportantLinksWereFound.value)
                            }
                        }
                    } else {
                        if (childFoldersData.isNotEmpty()) {
                            items(
                                childFoldersData,
                                span = { StaggeredGridItemSpan.FullLine }) { folderElement ->
                                FolderIndividualComponentImpl(folderElement)
                            }
                            item(span = StaggeredGridItemSpan.FullLine) {
                                Spacer(Modifier.height(5.dp))
                            }
                        }
                        if (folderLinksData.isNotEmpty()) {
                            itemsIndexed(items = folderLinksData) { index, it ->
                                GridViewLinkUIComponent(
                                    forStaggeredView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name,
                                    linkUIComponentParam = modifiedLinkUIComponentParam(it)
                                )
                            }
                        } else {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                DataEmptyScreen(text = LocalizedStrings.noLinksWereFound.value)
                            }
                        }
                    }
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Spacer(modifier = Modifier.height(350.dp))
                    }
                }
            }
        }
        MenuBtmSheetUI(
            MenuBtmSheetParam(
                webUrl = selectedWebURL.value,
                showQuickActions = mutableStateOf(SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name || SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.GRID_VIEW.name),
                btmModalSheetState = btmModalSheetState,
                shouldBtmModalSheetBeVisible = shouldOptionsBtmModalSheetBeVisible,
                btmSheetFor = if (homeScreenType.name == HomeScreenVM.HomeScreenType.IMP_LINKS.name) OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN else SpecificCollectionsScreenVM.selectedBtmSheetType.value,
                onRenameClick = {
                    coroutineScope.launch {
                        btmModalSheetState.hide()
                    }
                    shouldRenameDialogBoxAppear.value = true
                },
                onDeleteCardClick = {
                    SpecificCollectionsScreenVM.screenType.value = when (homeScreenType) {
                        HomeScreenVM.HomeScreenType.SAVED_LINKS -> SpecificScreenType.SAVED_LINKS_SCREEN
                        HomeScreenVM.HomeScreenType.IMP_LINKS -> SpecificScreenType.IMPORTANT_LINKS_SCREEN
                        else -> SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                    }
                    shouldDeleteDialogBoxAppear.value = true
                },
                onArchiveClick = {
                    if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        when (homeScreenType) {
                            HomeScreenVM.HomeScreenType.SAVED_LINKS -> {
                                SpecificCollectionsScreenVM.screenType.value =
                                    SpecificScreenType.SAVED_LINKS_SCREEN
                            }

                            HomeScreenVM.HomeScreenType.IMP_LINKS -> SpecificCollectionsScreenVM.screenType.value =
                                SpecificScreenType.IMPORTANT_LINKS_SCREEN

                            HomeScreenVM.HomeScreenType.CUSTOM_LIST -> SpecificCollectionsScreenVM.screenType.value =
                                SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                        }
                        homeScreenVM.onArchiveClick(
                            HomeScreenVM.tempImpLinkData,
                            context,
                            selectedElementID.longValue,
                            {})
                    } else {
                        homeScreenVM.onUiEvent(
                            SpecificCollectionsScreenUIEvent.ArchiveAFolder(
                                selectedElementID.longValue
                            )
                        )
                    }
                },
                noteForSaving = selectedNote.value,
                onNoteDeleteCardClick = {
                    if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        when (homeScreenType) {
                            HomeScreenVM.HomeScreenType.SAVED_LINKS -> {
                                SpecificCollectionsScreenVM.screenType.value =
                                    SpecificScreenType.SAVED_LINKS_SCREEN
                            }

                            HomeScreenVM.HomeScreenType.IMP_LINKS -> SpecificCollectionsScreenVM.screenType.value =
                                SpecificScreenType.IMPORTANT_LINKS_SCREEN

                            HomeScreenVM.HomeScreenType.CUSTOM_LIST -> SpecificCollectionsScreenVM.screenType.value =
                                SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                        }
                        homeScreenVM.onNoteDeleteCardClick(
                            HomeScreenVM.tempImpLinkData.webURL,
                            context,
                            folderID = 0,
                            "",
                            selectedElementID.longValue
                        )
                    } else {
                        specificCollectionsScreenVM.onNoteDeleteClick(
                            selectedElementID.longValue
                        )
                    }
                },
                folderName = selectedURLTitle.value,
                linkTitle = selectedURLTitle.value,
                imgLink = HomeScreenVM.tempImpLinkData.imgURL,
                onRefreshClick = {
                    homeScreenVM.reloadLinkData(
                        selectedElementID.longValue,
                        homeScreenType = homeScreenType
                    )
                }, onImportantLinkClick = {
                    SpecificCollectionsScreenVM.screenType.value =
                        SpecificScreenType.IMPORTANT_LINKS_SCREEN
                    homeScreenVM.onUiEvent(
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
                }
            )
        )
    }
    DeleteDialogBox(
        DeleteDialogBoxParam(
            folderName = selectedURLTitle,
            shouldDialogBoxAppear = shouldDeleteDialogBoxAppear,
            deleteDialogBoxType = if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.FOLDER) DataDialogBoxType.FOLDER else DataDialogBoxType.LINK,
            onDeleteClick = {
                if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.FOLDER) {
                    homeScreenVM.onUiEvent(
                        SpecificCollectionsScreenUIEvent.DeleteAFolder(
                            selectedElementID.longValue
                        )
                    )
                } else {
                    homeScreenVM.onDeleteClick(
                        folderID = 0,
                        context = context,
                        onTaskCompleted = {},
                        linkID = selectedElementID.longValue,
                        impLinkURL = selectedWebURL.value
                    )
                }
            })
    )
    RenameDialogBox(
        RenameDialogBoxParam(
            shouldDialogBoxAppear = shouldRenameDialogBoxAppear,
            existingFolderName = selectedURLTitle.value,
            renameDialogBoxFor = SpecificCollectionsScreenVM.selectedBtmSheetType.value,
            onNoteChangeClick = { newNote: String ->
                when (homeScreenType) {
                    HomeScreenVM.HomeScreenType.SAVED_LINKS -> {
                        homeScreenVM.onUiEvent(
                            SpecificCollectionsScreenUIEvent.UpdateRegularLinkNote(
                                selectedElementID.longValue,
                                newNote
                            )
                        )
                    }

                    HomeScreenVM.HomeScreenType.IMP_LINKS -> homeScreenVM.onUiEvent(
                        SpecificCollectionsScreenUIEvent.UpdateImpLinkNote(
                            selectedElementID.longValue,
                            newNote
                        )
                    )

                    else -> {
                        if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                            homeScreenVM.onUiEvent(
                                SpecificCollectionsScreenUIEvent.UpdateRegularLinkNote(
                                    selectedElementID.longValue, newNote
                                )
                            )
                        } else {
                            homeScreenVM.onUiEvent(
                                SpecificCollectionsScreenUIEvent.UpdateFolderNote(
                                    selectedElementID.longValue, newNote
                                )
                            )
                        }
                    }
                }
                shouldRenameDialogBoxAppear.value = false
            },
            onTitleChangeClick = { newTitle: String ->
                when (homeScreenType) {
                    HomeScreenVM.HomeScreenType.SAVED_LINKS -> {
                        homeScreenVM.onUiEvent(
                            SpecificCollectionsScreenUIEvent.UpdateRegularLinkTitle(
                                newTitle, selectedElementID.longValue
                            )
                        )
                    }

                    HomeScreenVM.HomeScreenType.IMP_LINKS -> homeScreenVM.onUiEvent(
                        SpecificCollectionsScreenUIEvent.UpdateImpLinkTitle(
                            newTitle, selectedElementID.longValue
                        )
                    )

                    else -> {
                        if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                            homeScreenVM.onUiEvent(
                                SpecificCollectionsScreenUIEvent.UpdateRegularLinkTitle(
                                    newTitle, selectedElementID.longValue
                                )
                            )
                        } else {
                            homeScreenVM.onUiEvent(
                                SpecificCollectionsScreenUIEvent.UpdateFolderName(
                                    newTitle, selectedElementID.longValue
                                )
                            )
                        }
                    }
                }
                shouldRenameDialogBoxAppear.value = false
            }, existingTitle = selectedURLTitle.value, existingNote = selectedNote.value
        )
    )
}

enum class HomeScreenBtmSheetType {
    RECENT_SAVES, RECENT_IMP_SAVES, RECENT_VISITS
}