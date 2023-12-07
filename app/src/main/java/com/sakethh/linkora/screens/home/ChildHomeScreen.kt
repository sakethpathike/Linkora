package com.sakethh.linkora.screens.home

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.RecentlyVisited
import com.sakethh.linkora.screens.DataEmptyScreen
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreenType
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificCollectionsScreenVM
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildHomeScreen(homeScreenType: HomeScreenVM.HomeScreenType, navController: NavController) {
    val homeScreenVM: HomeScreenVM = viewModel()
    val specificCollectionsScreenVM: SpecificCollectionsScreenVM = viewModel()
    LaunchedEffect(key1 = Unit) {
        awaitAll(async {
            specificCollectionsScreenVM.changeRetrievedData(
                sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                    SettingsScreenVM.Settings.selectedSortingType.value
                ),
                folderID = 0,
                screenType = SpecificScreenType.SAVED_LINKS_SCREEN,
                folderName = SpecificCollectionsScreenVM.currentClickedFolderData.value.folderName
            )
        }, async {
            specificCollectionsScreenVM.changeRetrievedData(
                sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                    SettingsScreenVM.Settings.selectedSortingType.value
                ),
                folderID = 0,
                screenType = SpecificScreenType.IMPORTANT_LINKS_SCREEN,
                folderName = SpecificCollectionsScreenVM.currentClickedFolderData.value.folderName
            )
        })
    }
    val savedLinksData = specificCollectionsScreenVM.savedLinksTable.collectAsState().value
    val impLinksData = specificCollectionsScreenVM.impLinksTable.collectAsState().value
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val btmModalSheetState = rememberModalBottomSheetState()
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
                    items(savedLinksData) {
                        LinkUIComponent(
                            LinkUIComponentParam(
                                title = it.title,
                                webBaseURL = it.baseURL,
                                imgURL = it.imgURL,
                                onMoreIconCLick = {
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
            } else {
                if (impLinksData.isNotEmpty()) {
                    items(impLinksData) {
                        LinkUIComponent(
                            LinkUIComponentParam(
                                title = it.title,
                                webBaseURL = it.baseURL,
                                imgURL = it.imgURL,
                                onMoreIconCLick = {
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
                                })
                        )
                    }
                } else {
                    item {
                        DataEmptyScreen(text = "No important links were found. To continue, please add links.")
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
                btmSheetFor = if (homeScreenType == HomeScreenVM.HomeScreenType.SAVED_LINKS) OptionsBtmSheetType.LINK else OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN,
                onRenameClick = {
                    coroutineScope.launch {
                        btmModalSheetState.hide()
                    }
                    shouldRenameDialogBoxAppear.value = true
                },
                onDeleteCardClick = {
                    shouldDeleteDialogBoxAppear.value = true
                },
                onImportantLinkAdditionInTheTable = {
                    specificCollectionsScreenVM.onImportantLinkAdditionInTheTable(
                        context, {}, HomeScreenVM.tempImpLinkData
                    )
                },
                onArchiveClick = {
                    homeScreenVM.onArchiveClick(
                        selectedCardType = if (homeScreenType == HomeScreenVM.HomeScreenType.SAVED_LINKS) HomeScreenBtmSheetType.RECENT_SAVES else HomeScreenBtmSheetType.RECENT_IMP_SAVES,
                        context
                    )
                }, noteForSaving = selectedNote.value,
                onNoteDeleteCardClick = {
                    homeScreenVM.onNoteDeleteCardClick(
                        selectedCardType = if (homeScreenType == HomeScreenVM.HomeScreenType.SAVED_LINKS) HomeScreenBtmSheetType.RECENT_SAVES else HomeScreenBtmSheetType.RECENT_IMP_SAVES,
                        selectedWebURL = HomeScreenVM.tempImpLinkData.webURL,
                        context = context
                    )
                },
                folderName = "",
                linkTitle = selectedURLTitle.value
            )
        )
    }
    DeleteDialogBox(
        DeleteDialogBoxParam(
            shouldDialogBoxAppear = shouldDeleteDialogBoxAppear,
            deleteDialogBoxType = DataDialogBoxType.LINK,
            onDeleteClick = {
                homeScreenVM.onDeleteClick(
                    selectedCardType = if (homeScreenType == HomeScreenVM.HomeScreenType.SAVED_LINKS) HomeScreenBtmSheetType.RECENT_SAVES else HomeScreenBtmSheetType.RECENT_IMP_SAVES,
                    selectedWebURL = HomeScreenVM.tempImpLinkData.webURL,
                    context = context,
                    shouldDeleteBoxAppear = shouldDeleteDialogBoxAppear
                )
            })
    )
    RenameDialogBox(
        RenameDialogBoxParam(
            shouldDialogBoxAppear = shouldRenameDialogBoxAppear,
            existingFolderName = "",
            renameDialogBoxFor = OptionsBtmSheetType.LINK,
            onNoteChangeClickForLinks = { newNote: String ->
                homeScreenVM.onNoteChangeClickForLinks(
                    selectedCardType = if (homeScreenType == HomeScreenVM.HomeScreenType.SAVED_LINKS) HomeScreenBtmSheetType.RECENT_SAVES else HomeScreenBtmSheetType.RECENT_IMP_SAVES,
                    HomeScreenVM.tempImpLinkData.webURL,
                    newNote
                )
                shouldRenameDialogBoxAppear.value = false
                Unit
            },
            onTitleChangeClickForLinks = { newTitle: String ->
                homeScreenVM.onTitleChangeClickForLinks(
                    selectedCardType = if (homeScreenType == HomeScreenVM.HomeScreenType.SAVED_LINKS) HomeScreenBtmSheetType.RECENT_SAVES else HomeScreenBtmSheetType.RECENT_IMP_SAVES,
                    HomeScreenVM.tempImpLinkData.webURL,
                    newTitle
                )
            }, currentFolderID = 0, parentFolderID = null, onTitleRenamed = {
                shouldRenameDialogBoxAppear.value = false
            }
        )
    )


    BackHandler {
        if (btmModalSheetState.isVisible) {
            coroutineScope.launch {
                btmModalSheetState.hide()
            }
        }
    }
}

enum class HomeScreenBtmSheetType {
    RECENT_SAVES, RECENT_IMP_SAVES, RECENT_VISITS
}