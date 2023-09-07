package com.sakethh.linkora.screens.collections.specificCollectionScreen

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
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
import com.sakethh.linkora.btmSheet.OptionsBtmSheetType
import com.sakethh.linkora.btmSheet.OptionsBtmSheetUI
import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.btmSheet.SortingBottomSheetUI
import com.sakethh.linkora.customComposables.AddNewLinkDialogBox
import com.sakethh.linkora.customComposables.DataDialogBoxType
import com.sakethh.linkora.customComposables.DeleteDialogBox
import com.sakethh.linkora.customComposables.LinkUIComponent
import com.sakethh.linkora.customComposables.RenameDialogBox
import com.sakethh.linkora.customWebTab.openInWeb
import com.sakethh.linkora.localDB.RecentlyVisited
import com.sakethh.linkora.screens.DataEmptyScreen
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecificScreen(navController: NavController) {
    val specificCollectionsScreenVM: SpecificScreenVM = viewModel()
    val selectedWebURL = rememberSaveable {
        mutableStateOf("")
    }
    val specificFolderLinksData = specificCollectionsScreenVM.folderLinksData.collectAsState().value
    val savedLinksData = specificCollectionsScreenVM.savedLinksTable.collectAsState().value
    val impLinksData = specificCollectionsScreenVM.impLinksTable.collectAsState().value
    val archivedFoldersLinksData =
        specificCollectionsScreenVM.archiveFolderDataTable.collectAsState().value
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
            SpecificScreenVM.currentClickedFolderName.value = "Important Links"
            "Important Links"
        }

        SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
            SpecificScreenVM.currentClickedFolderName.value =
                SpecificScreenVM.selectedArchiveFolderName.value
            SpecificScreenVM.selectedArchiveFolderName.value
        }

        SpecificScreenType.SAVED_LINKS_SCREEN -> {
            SpecificScreenVM.currentClickedFolderName.value = "Saved Links"
            "Saved Links"
        }

        SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
            SpecificScreenVM.currentClickedFolderName.value
        }

        else -> {
            ""
        }
    }
    val shouldNewLinkDialogBoxBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val isDataExtractingFromTheLink = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldBtmSheetForNewLinkAdditionBeEnabled = rememberSaveable {
        mutableStateOf(false)
    }
    LinkoraTheme {
        Scaffold(floatingActionButtonPosition = FabPosition.End, floatingActionButton = {
            if (SpecificScreenVM.screenType.value != SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN) {
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
            TopAppBar(title = {
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
                        if (specificFolderLinksData.isNotEmpty()) {
                            items(specificFolderLinksData) {
                                LinkUIComponent(
                                    title = it.title,
                                    webBaseURL = it.baseURL,
                                    imgURL = it.imgURL,
                                    onMoreIconCLick = {
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
                                                ), context = context, uriHandler = uriHandler
                                            )
                                        }
                                    },
                                    webURL = it.webURL,
                                    onForceOpenInExternalBrowserClicked = {
                                        specificCollectionsScreenVM.onForceOpenInExternalBrowserClicked(
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
                                    title = it.title,
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
                                                ), context = context, uriHandler = uriHandler
                                            )
                                        }
                                    },
                                    webURL = it.webURL, onForceOpenInExternalBrowserClicked = {
                                        specificCollectionsScreenVM.onForceOpenInExternalBrowserClicked(
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
                                    title = it.title,
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
                                                ), context = context, uriHandler = uriHandler
                                            )
                                        }
                                    },
                                    webURL = it.webURL, onForceOpenInExternalBrowserClicked = {
                                        specificCollectionsScreenVM.onForceOpenInExternalBrowserClicked(
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
                        } else {
                            item {
                                DataEmptyScreen(text = "No important links were found. To continue, please add links.")
                            }
                        }
                    }

                    SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                        if (archivedFoldersLinksData.isNotEmpty()) {
                            items(archivedFoldersLinksData) {
                                LinkUIComponent(
                                    title = it.title,
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
                                                ), context = context, uriHandler = uriHandler
                                            )
                                        }
                                    },
                                    webURL = it.webURL, onForceOpenInExternalBrowserClicked = {
                                        specificCollectionsScreenVM.onForceOpenInExternalBrowserClicked(
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
        }
        val isDataExtractingFromLink = rememberSaveable {
            mutableStateOf(false)
        }
        NewLinkBtmSheet(
            btmSheetState = btmModalSheetStateForSavingLink,
            _inIntentActivity = false,
            screenType = SpecificScreenVM.screenType.value,
            _folderName = topBarText,
            shouldUIBeVisible = shouldBtmSheetForNewLinkAdditionBeEnabled,
            onLinkSaved = {
                specificCollectionsScreenVM.changeRetrievedData(
                    sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                        SettingsScreenVM.Settings.selectedSortingType.value
                    ), folderName = topBarText
                )
            }
        )
        OptionsBtmSheetUI(
            inSpecificArchiveScreen = mutableStateOf(SpecificScreenVM.screenType.value == SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN),
            inArchiveScreen = mutableStateOf(SpecificScreenVM.screenType.value == SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN),
            btmModalSheetState = btmModalSheetState,
            shouldBtmModalSheetBeVisible = shouldOptionsBtmModalSheetBeVisible,
            coroutineScope = coroutineScope,
            btmSheetFor = when (SpecificScreenVM.screenType.value) {
                SpecificScreenType.IMPORTANT_LINKS_SCREEN -> OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN
                SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN
                SpecificScreenType.SAVED_LINKS_SCREEN -> OptionsBtmSheetType.LINK
                SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> OptionsBtmSheetType.LINK
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
                specificCollectionsScreenVM.onImportantLinkAdditionInTheTable(context, tempImpLinkData)
            },
            importantLinks = null,
            onArchiveClick = {
                specificCollectionsScreenVM.onArchiveClick(tempImpLinkData, context, topBarText)
            },
            noteForSaving = selectedURLOrFolderNote.value,
            onNoteDeleteCardClick = {
                specificCollectionsScreenVM.onNoteDeleteCardClick(
                    selectedWebURL.value,
                    context,
                    topBarText
                )
            },
            folderName = topBarText,
            linkTitle = tempImpLinkData.title
        )
        DeleteDialogBox(
            shouldDialogBoxAppear = shouldDeleteDialogBeVisible, onDeleteClick = {
                specificCollectionsScreenVM.onDeleteClick(
                    folderName = topBarText,
                    selectedWebURL = selectedWebURL.value,
                    context
                )
            }, deleteDialogBoxType = DataDialogBoxType.LINK,
            onDeleted = {
                specificCollectionsScreenVM.changeRetrievedData(
                    sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                        SettingsScreenVM.Settings.selectedSortingType.value
                    ), folderName = topBarText
                )
            }
        )
        RenameDialogBox(shouldDialogBoxAppear = shouldRenameDialogBeVisible,
            coroutineScope = coroutineScope,
            existingFolderName = "",
            renameDialogBoxFor = OptionsBtmSheetType.LINK,
            webURLForTitle = selectedWebURL.value,
            onNoteChangeClickForLinks = { webURL: String, newNote: String ->
                specificCollectionsScreenVM.onNoteChangeClickForLinks(
                    folderName = topBarText,
                    webURL,
                    newNote
                )
            },
            onTitleChangeClickForLinks = { webURL: String, newTitle: String ->
                specificCollectionsScreenVM.onTitleChangeClickForLinks(
                    folderName = topBarText,
                    newTitle,
                    webURL
                )
            },
            onTitleRenamed = {
                specificCollectionsScreenVM.changeRetrievedData(
                    sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                        SettingsScreenVM.Settings.selectedSortingType.value
                    ), folderName = topBarText
                )
            })
        AddNewLinkDialogBox(
            shouldDialogBoxAppear = shouldNewLinkDialogBoxBeVisible,
            specificFolderName = topBarText,
            screenType = SpecificScreenVM.screenType.value,
            onSaveClick = {
                specificCollectionsScreenVM.changeRetrievedData(
                    folderName = topBarText,
                    sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                        SettingsScreenVM.Settings.selectedSortingType.value
                    )
                )
            }
        )
        SortingBottomSheetUI(
            shouldBottomSheetVisible = shouldSortingBottomSheetAppear, onSelectedAComponent = {
                specificCollectionsScreenVM.changeRetrievedData(
                    sortingPreferences = it,
                    folderName = topBarText
                )
            }, bottomModalSheetState = sortingBtmSheetState
        )
    }
    BackHandler {
        if (btmModalSheetState.isVisible) {
            coroutineScope.launch {
                btmModalSheetState.hide()
            }
        } else {
            navController.popBackStack()
        }
    }
}