package com.sakethh.linkora.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sakethh.linkora.data.localDB.dto.FoldersTable
import com.sakethh.linkora.data.localDB.dto.ImportantLinks
import com.sakethh.linkora.data.localDB.dto.LinksTable
import com.sakethh.linkora.data.localDB.dto.RecentlyVisited
import com.sakethh.linkora.ui.commonBtmSheets.OptionsBtmSheetUI
import com.sakethh.linkora.ui.commonBtmSheets.OptionsBtmSheetUIParam
import com.sakethh.linkora.ui.commonComposables.DataDialogBoxType
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBox
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.LinkUIComponent
import com.sakethh.linkora.ui.commonComposables.LinkUIComponentParam
import com.sakethh.linkora.ui.commonComposables.RenameDialogBox
import com.sakethh.linkora.ui.commonComposables.RenameDialogBoxParam
import com.sakethh.linkora.ui.navigation.NavigationRoutes
import com.sakethh.linkora.ui.screens.DataEmptyScreen
import com.sakethh.linkora.ui.screens.collections.FolderIndividualComponent
import com.sakethh.linkora.ui.screens.openInWeb
import com.sakethh.linkora.ui.theme.LinkoraTheme
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM
import com.sakethh.linkora.ui.viewmodels.collections.CollectionsScreenVM
import com.sakethh.linkora.ui.viewmodels.collections.SpecificCollectionsScreenVM
import com.sakethh.linkora.ui.viewmodels.collections.SpecificScreenType
import com.sakethh.linkora.ui.viewmodels.commonBtmSheets.OptionsBtmSheetType
import com.sakethh.linkora.ui.viewmodels.commonBtmSheets.OptionsBtmSheetVM
import com.sakethh.linkora.ui.viewmodels.home.HomeScreenVM
import com.sakethh.linkora.ui.viewmodels.localDB.DeleteVM
import com.sakethh.linkora.ui.viewmodels.localDB.UpdateVM
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildHomeScreen(
    homeScreenType: HomeScreenVM.HomeScreenType,
    navController: NavController,
    folderLinksData: List<LinksTable>,
    childFoldersData: List<FoldersTable>
) {
    val homeScreenVM: HomeScreenVM = viewModel()
    HomeScreenVM.currentHomeScreenType = homeScreenType
    val specificCollectionsScreenVM: SpecificCollectionsScreenVM = viewModel()
    LaunchedEffect(key1 = Unit) {
        awaitAll(async {
            if (homeScreenType == HomeScreenVM.HomeScreenType.SAVED_LINKS) {
                specificCollectionsScreenVM.changeRetrievedData(
                    sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                        SettingsScreenVM.Settings.selectedSortingType.value
                    ),
                    folderID = 0,
                    screenType = SpecificScreenType.SAVED_LINKS_SCREEN
                )
            }
        }, async {
            if (homeScreenType == HomeScreenVM.HomeScreenType.IMP_LINKS) {
                specificCollectionsScreenVM.changeRetrievedData(
                    sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                        SettingsScreenVM.Settings.selectedSortingType.value
                    ),
                    folderID = 0,
                    screenType = SpecificScreenType.IMPORTANT_LINKS_SCREEN
                )
            }
        })
    }
    val savedLinksData =
        specificCollectionsScreenVM.savedLinksTable.collectAsStateWithLifecycle().value
    val impLinksData = specificCollectionsScreenVM.impLinksTable.collectAsStateWithLifecycle().value
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val btmModalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shouldOptionsBtmModalSheetBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldDeleteDialogBoxAppear = rememberSaveable {
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
    val optionsBtmSheetVM: OptionsBtmSheetVM = viewModel()
    LinkoraTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            if (homeScreenType == HomeScreenVM.HomeScreenType.SAVED_LINKS) {
                if (savedLinksData.isNotEmpty()) {
                    items(items = savedLinksData, key = { linksTable ->
                        linksTable.id.toString() + linksTable.webURL
                    }) {
                        LinkUIComponent(
                            LinkUIComponentParam(
                                onLongClick = {
                                    if (!homeScreenVM.isSelectionModeEnabled.value) {
                                        homeScreenVM.isSelectionModeEnabled.value =
                                            true
                                        homeScreenVM.selectedSavedLinkIds.add(it.id)
                                    }
                                },
                                isSelectionModeEnabled = homeScreenVM.isSelectionModeEnabled,
                                isItemSelected = mutableStateOf(
                                    homeScreenVM.selectedSavedLinkIds.contains(
                                        it.id
                                    )
                                ),
                                title = it.title,
                                webBaseURL = it.baseURL,
                                imgURL = it.imgURL,
                                onMoreIconCLick = {
                                    SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                        OptionsBtmSheetType.LINK
                                    selectedElementID.longValue = it.id
                                    HomeScreenVM.tempImpLinkData.baseURL = it.baseURL
                                    HomeScreenVM.tempImpLinkData.imgURL = it.imgURL
                                    HomeScreenVM.tempImpLinkData.webURL = it.webURL
                                    HomeScreenVM.tempImpLinkData.title = it.title
                                    HomeScreenVM.tempImpLinkData.infoForSaving = it.infoForSaving
                                    shouldOptionsBtmModalSheetBeVisible.value = true
                                    selectedWebURL.value = it.webURL
                                    selectedNote.value = it.infoForSaving
                                    selectedURLTitle.value = it.title
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
                                        if (!homeScreenVM.selectedSavedLinkIds.contains(it.id)) {
                                            homeScreenVM.selectedSavedLinkIds.add(
                                                it.id
                                            )
                                        } else {
                                            homeScreenVM.selectedSavedLinkIds.remove(
                                                it.id
                                            )
                                        }
                                    } else {
                                        coroutineScope.launch {
                                            openInWeb(
                                                recentlyVisitedData = RecentlyVisited(
                                                    title = it.title,
                                                    webURL = it.webURL,
                                                    baseURL = it.baseURL,
                                                    imgURL = it.imgURL,
                                                    infoForSaving = it.infoForSaving
                                                ), context = context, uriHandler = uriHandler,
                                                forceOpenInExternalBrowser = false
                                            )
                                        }
                                    }
                                },
                                webURL = it.webURL,
                                onForceOpenInExternalBrowserClicked = {
                                    homeScreenVM.onLinkClick(
                                        RecentlyVisited(
                                            title = it.title,
                                            webURL = it.webURL,
                                            baseURL = it.baseURL,
                                            imgURL = it.imgURL,
                                            infoForSaving = it.infoForSaving
                                        ), context = context, uriHandler = uriHandler,
                                        onTaskCompleted = {},
                                        forceOpenInExternalBrowser = true
                                    )
                                })
                        )
                    }
                } else {
                    item {
                        DataEmptyScreen(text = "Welcome back to Linkora! No links found. To continue, please add links.")
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
                        LinkUIComponent(
                            LinkUIComponentParam(
                                onLongClick = {
                                    if (!homeScreenVM.isSelectionModeEnabled.value) {
                                        homeScreenVM.isSelectionModeEnabled.value =
                                            true
                                        homeScreenVM.selectedImpLinkIds.add(it.id)
                                    }
                                },
                                isSelectionModeEnabled = homeScreenVM.isSelectionModeEnabled,
                                isItemSelected = mutableStateOf(
                                    homeScreenVM.selectedImpLinkIds.contains(
                                        it.id
                                    )
                                ),
                                title = it.title,
                                webBaseURL = it.baseURL,
                                imgURL = it.imgURL,
                                onMoreIconCLick = {
                                    SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                        OptionsBtmSheetType.LINK
                                    selectedElementID.longValue = it.id
                                    HomeScreenVM.tempImpLinkData.baseURL = it.baseURL
                                    HomeScreenVM.tempImpLinkData.imgURL = it.imgURL
                                    HomeScreenVM.tempImpLinkData.webURL = it.webURL
                                    HomeScreenVM.tempImpLinkData.title = it.title
                                    HomeScreenVM.tempImpLinkData.infoForSaving = it.infoForSaving
                                    shouldOptionsBtmModalSheetBeVisible.value = true
                                    selectedWebURL.value = it.webURL
                                    selectedNote.value = it.infoForSaving
                                    selectedURLTitle.value = it.title
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
                                        if (!homeScreenVM.selectedImpLinkIds.contains(it.id)) {
                                            homeScreenVM.selectedImpLinkIds.add(
                                                it.id
                                            )
                                        } else {
                                            homeScreenVM.selectedImpLinkIds.remove(
                                                it.id
                                            )
                                        }
                                    } else {
                                        coroutineScope.launch {
                                            openInWeb(
                                                recentlyVisitedData = RecentlyVisited(
                                                    title = it.title,
                                                    webURL = it.webURL,
                                                    baseURL = it.baseURL,
                                                    imgURL = it.imgURL,
                                                    infoForSaving = it.infoForSaving
                                                ), context = context, uriHandler = uriHandler,
                                                forceOpenInExternalBrowser = false
                                            )
                                        }
                                    }
                                },
                                webURL = it.webURL,
                                onForceOpenInExternalBrowserClicked = {
                                    homeScreenVM.onLinkClick(
                                        RecentlyVisited(
                                            title = it.title,
                                            webURL = it.webURL,
                                            baseURL = it.baseURL,
                                            imgURL = it.imgURL,
                                            infoForSaving = it.infoForSaving
                                        ), context = context, uriHandler = uriHandler,
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
            } else {
                if (childFoldersData.isNotEmpty()) {
                    itemsIndexed(childFoldersData) { index, folderElement ->
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
                                    SpecificCollectionsScreenVM.inARegularFolder.value = true
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
                }
                if (folderLinksData.isNotEmpty()) {
                    itemsIndexed(items = folderLinksData) { index, it ->
                        LinkUIComponent(
                            linkUIComponentParam = LinkUIComponentParam(
                                onLongClick = {
                                    if (!homeScreenVM.isSelectionModeEnabled.value) {
                                        homeScreenVM.isSelectionModeEnabled.value =
                                            true
                                        homeScreenVM.selectedLinksID.add(it.id)
                                    }
                                },
                                isSelectionModeEnabled = homeScreenVM.isSelectionModeEnabled,
                                isItemSelected = mutableStateOf(
                                    homeScreenVM.selectedLinksID.contains(
                                        it.id
                                    )
                                ),
                                title = it.title,
                                webBaseURL = it.baseURL,
                                imgURL = it.imgURL,
                                onMoreIconCLick = {
                                    SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                        OptionsBtmSheetType.LINK
                                    selectedElementID.longValue = it.id
                                    HomeScreenVM.tempImpLinkData.baseURL = it.baseURL
                                    HomeScreenVM.tempImpLinkData.imgURL = it.imgURL
                                    HomeScreenVM.tempImpLinkData.webURL = it.webURL
                                    HomeScreenVM.tempImpLinkData.title = it.title
                                    HomeScreenVM.tempImpLinkData.infoForSaving = it.infoForSaving
                                    shouldOptionsBtmModalSheetBeVisible.value = true
                                    selectedWebURL.value = it.webURL
                                    selectedNote.value = it.infoForSaving
                                    selectedURLTitle.value = it.title
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
                                        if (!homeScreenVM.selectedLinksID.contains(it.id)) {
                                            homeScreenVM.selectedLinksID.add(
                                                it.id
                                            )
                                        } else {
                                            homeScreenVM.selectedLinksID.remove(
                                                it.id
                                            )
                                        }
                                    } else {
                                        coroutineScope.launch {
                                            openInWeb(
                                                recentlyVisitedData = RecentlyVisited(
                                                    title = it.title,
                                                    webURL = it.webURL,
                                                    baseURL = it.baseURL,
                                                    imgURL = it.imgURL,
                                                    infoForSaving = it.infoForSaving
                                                ), context = context, uriHandler = uriHandler,
                                                forceOpenInExternalBrowser = false
                                            )
                                        }
                                    }
                                },
                                webURL = it.webURL,
                                onForceOpenInExternalBrowserClicked = {
                                    homeScreenVM.onLinkClick(
                                        RecentlyVisited(
                                            title = it.title,
                                            webURL = it.webURL,
                                            baseURL = it.baseURL,
                                            imgURL = it.imgURL,
                                            infoForSaving = it.infoForSaving
                                        ), context = context, uriHandler = uriHandler,
                                        onTaskCompleted = {},
                                        forceOpenInExternalBrowser = true
                                    )
                                })
                        )
                    }
                } else {
                    item {
                        DataEmptyScreen(text = "No links were found. To continue, please add links.")
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(225.dp))
            }
        }
        val updateVM: UpdateVM = viewModel()
        OptionsBtmSheetUI(
            OptionsBtmSheetUIParam(
                importantLinks = ImportantLinks(
                    title = HomeScreenVM.tempImpLinkData.title,
                    webURL = HomeScreenVM.tempImpLinkData.webURL,
                    baseURL = HomeScreenVM.tempImpLinkData.baseURL,
                    imgURL = HomeScreenVM.tempImpLinkData.imgURL,
                    infoForSaving = HomeScreenVM.tempImpLinkData.infoForSaving
                ),
                btmModalSheetState = btmModalSheetState,
                shouldBtmModalSheetBeVisible = shouldOptionsBtmModalSheetBeVisible,
                btmSheetFor = SpecificCollectionsScreenVM.selectedBtmSheetType.value,
                onRenameClick = {
                    coroutineScope.launch {
                        btmModalSheetState.hide()
                    }
                    shouldRenameDialogBoxAppear.value = true
                },
                onDeleteCardClick = {
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
                        updateVM.archiveAFolderV10(selectedElementID.longValue)
                    }
                }, noteForSaving = selectedNote.value,
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
                            context,
                            selectedElementID.longValue
                        )
                    }
                },
                folderName = selectedURLTitle.value,
                linkTitle = selectedURLTitle.value,
            )
        )
    }
    val deleteVM: DeleteVM = viewModel()
    DeleteDialogBox(
        DeleteDialogBoxParam(
            folderName = selectedURLTitle,
            shouldDialogBoxAppear = shouldDeleteDialogBoxAppear,
            deleteDialogBoxType = if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) DataDialogBoxType.LINK else DataDialogBoxType.FOLDER,
            onDeleteClick = {
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
                    homeScreenVM.onDeleteClick(
                        folderID = 0,
                        selectedWebURL = "",
                        context = context,
                        onTaskCompleted = {},
                        folderName = "",
                        linkID = selectedElementID.longValue
                    )
                } else {
                    deleteVM.onRegularFolderDeleteClick(selectedElementID.longValue)
                }
            })
    )
    val updateVM: UpdateVM = viewModel()
    RenameDialogBox(
        RenameDialogBoxParam(
            shouldDialogBoxAppear = shouldRenameDialogBoxAppear,
            existingFolderName = selectedURLTitle.value,
            renameDialogBoxFor = SpecificCollectionsScreenVM.selectedBtmSheetType.value,
            onNoteChangeClick = { newNote: String ->
                when (homeScreenType) {
                    HomeScreenVM.HomeScreenType.SAVED_LINKS -> {
                        updateVM.updateRegularLinkNote(selectedElementID.longValue, newNote)
                    }

                    HomeScreenVM.HomeScreenType.IMP_LINKS -> updateVM.updateImpLinkNote(
                        selectedElementID.longValue,
                        newNote
                    )

                    else -> {
                        if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                            updateVM.updateRegularLinkNote(selectedElementID.longValue, newNote)
                        } else {
                            updateVM.updateFolderNote(selectedElementID.longValue, newNote)
                        }
                    }
                }
                shouldRenameDialogBoxAppear.value = false
            },
            onTitleChangeClick = { newTitle: String ->
                when (homeScreenType) {
                    HomeScreenVM.HomeScreenType.SAVED_LINKS -> {
                        updateVM.updateRegularLinkTitle(selectedElementID.longValue, newTitle)
                    }

                    HomeScreenVM.HomeScreenType.IMP_LINKS -> updateVM.updateImpLinkTitle(
                        selectedElementID.longValue,
                        newTitle
                    )

                    else -> {
                        if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                            updateVM.updateRegularLinkTitle(selectedElementID.longValue, newTitle)
                        } else {
                            updateVM.updateFolderName(selectedElementID.longValue, newTitle)
                        }
                    }
                }
                shouldRenameDialogBoxAppear.value = false
            }
        )
    )
}

enum class HomeScreenBtmSheetType {
    RECENT_SAVES, RECENT_IMP_SAVES, RECENT_VISITS
}