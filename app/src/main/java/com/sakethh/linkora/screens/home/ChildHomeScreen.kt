package com.sakethh.linkora.screens.home

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sakethh.linkora.btmSheet.OptionsBtmSheetType
import com.sakethh.linkora.btmSheet.OptionsBtmSheetUI
import com.sakethh.linkora.btmSheet.OptionsBtmSheetUIParam
import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.customComposables.DataDialogBoxType
import com.sakethh.linkora.customComposables.DeleteDialogBox
import com.sakethh.linkora.customComposables.DeleteDialogBoxParam
import com.sakethh.linkora.customComposables.LinkUIComponent
import com.sakethh.linkora.customComposables.LinkUIComponentParam
import com.sakethh.linkora.customComposables.RenameDialogBox
import com.sakethh.linkora.customComposables.RenameDialogBoxParam
import com.sakethh.linkora.customWebTab.openInWeb
import com.sakethh.linkora.localDB.commonVMs.UpdateVM
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.localDB.dto.RecentlyVisited
import com.sakethh.linkora.navigation.NavigationRoutes
import com.sakethh.linkora.screens.DataEmptyScreen
import com.sakethh.linkora.screens.collections.CollectionsScreenVM
import com.sakethh.linkora.screens.collections.FolderIndividualComponent
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificCollectionsScreenVM
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreenType
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
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
    val savedLinksData = specificCollectionsScreenVM.savedLinksTable.collectAsState().value
    val impLinksData = specificCollectionsScreenVM.impLinksTable.collectAsState().value
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
    val selectedLinkID = rememberSaveable {
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
                                title = it.title,
                                webBaseURL = it.baseURL,
                                imgURL = it.imgURL,
                                onMoreIconCLick = {
                                    selectedLinkID.longValue = it.id
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
                                },
                                isSelectionModeEnabled = mutableStateOf(false),
                                isItemSelected = mutableStateOf(false),

                                onLongClick = { -> })
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
                                title = it.title,
                                webBaseURL = it.baseURL,
                                imgURL = it.imgURL,
                                onMoreIconCLick = {
                                    selectedLinkID.longValue = it.id
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
                                },
                                isSelectionModeEnabled = mutableStateOf(false),
                                isItemSelected = mutableStateOf(false),

                                onLongClick = { -> })
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
                            onMoreIconClick = {},
                            onFolderClick = {
                                if (!homeScreenVM.isSelectionModeEnabled.value) {
                                    SpecificCollectionsScreenVM.inARegularFolder.value = true
                                    SpecificCollectionsScreenVM.screenType.value =
                                        SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                                    CollectionsScreenVM.currentClickedFolderData.value =
                                        folderElement
                                    CollectionsScreenVM.rootFolderID = folderElement.id
                                    navController.navigate(NavigationRoutes.SPECIFIC_SCREEN.name)
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
                                    selectedLinkID.longValue = it.id
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
                btmSheetFor = OptionsBtmSheetType.LINK,
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
                    SpecificCollectionsScreenVM.screenType.value =
                        SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                    homeScreenVM.onArchiveClick(
                        HomeScreenVM.tempImpLinkData,
                        context,
                        selectedLinkID.longValue,
                        {})
                }, noteForSaving = selectedNote.value,
                onNoteDeleteCardClick = {
                    SpecificCollectionsScreenVM.screenType.value =
                        SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                    SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                        OptionsBtmSheetType.LINK
                    homeScreenVM.onNoteDeleteCardClick(
                        HomeScreenVM.tempImpLinkData.webURL,
                        context,
                        folderID = 0,
                        "",
                        selectedLinkID.longValue
                    )
                },
                folderName = "",
                linkTitle = selectedURLTitle.value,
            )
        )
    }
    DeleteDialogBox(
        DeleteDialogBoxParam(
            shouldDialogBoxAppear = shouldDeleteDialogBoxAppear,
            deleteDialogBoxType = DataDialogBoxType.LINK,
            onDeleteClick = {
                SpecificCollectionsScreenVM.screenType.value =
                    SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                SpecificCollectionsScreenVM.selectedBtmSheetType.value = OptionsBtmSheetType.LINK
                homeScreenVM.onDeleteClick(
                    folderID = 0,
                    selectedWebURL = "",
                    context = context,
                    onTaskCompleted = {},
                    folderName = "",
                    linkID = selectedLinkID.longValue
                )
            })
    )
    val updateVM: UpdateVM = viewModel()
    RenameDialogBox(
        RenameDialogBoxParam(
            shouldDialogBoxAppear = shouldRenameDialogBoxAppear,
            existingFolderName = "",
            renameDialogBoxFor = OptionsBtmSheetType.LINK,
            onNoteChangeClick = { newNote: String ->
                updateVM.updateRegularLinkNote(selectedLinkID.longValue, newNote)
                shouldRenameDialogBoxAppear.value = false
            },
            onTitleChangeClick = { newTitle: String ->
                updateVM.updateRegularLinkTitle(selectedLinkID.longValue, newTitle)
                shouldRenameDialogBoxAppear.value = false
            }
        )
    )


    BackHandler {
        if (btmModalSheetState.isVisible) {
            coroutineScope.launch {
                btmModalSheetState.hide()
            }
        } else if (homeScreenVM.isSelectionModeEnabled.value) {
            homeScreenVM.isSelectionModeEnabled.value = false
            homeScreenVM.areAllLinksChecked.value = false
            homeScreenVM.areAllFoldersChecked.value = false
            homeScreenVM.selectedLinksID.clear()
            homeScreenVM.selectedFoldersData.clear()
        }
    }
}

enum class HomeScreenBtmSheetType {
    RECENT_SAVES, RECENT_IMP_SAVES, RECENT_VISITS
}