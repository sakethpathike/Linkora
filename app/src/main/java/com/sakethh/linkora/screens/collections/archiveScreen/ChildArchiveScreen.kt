package com.sakethh.linkora.screens.collections.archiveScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import com.sakethh.linkora.localDB.CustomFunctionsForLocalDB
import com.sakethh.linkora.localDB.dto.RecentlyVisited
import com.sakethh.linkora.navigation.NavigationRoutes
import com.sakethh.linkora.screens.DataEmptyScreen
import com.sakethh.linkora.screens.collections.FolderIndividualComponent
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreenType
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreenVM
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildArchiveScreen(archiveScreenType: ArchiveScreenType, navController: NavController) {
    val archiveScreenVM: ArchiveScreenVM = viewModel()
    val archiveLinksData = archiveScreenVM.archiveLinksData.collectAsState().value
    val archiveFoldersDataV9 = archiveScreenVM.archiveFoldersDataV9.collectAsState().value
    val archiveFoldersDataV10 = archiveScreenVM.archiveFoldersDataV10.collectAsState().value
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val btmModalSheetState = androidx.compose.material3.rememberModalBottomSheetState()
    val shouldOptionsBtmModalSheetBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldDeleteDialogBoxAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldRenameDialogBoxAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val customFunctionsForLocalDB: CustomFunctionsForLocalDB = viewModel()
    val selectedURLOrFolderName = rememberSaveable {
        mutableStateOf("")
    }
    val selectedURLTitle = rememberSaveable {
        mutableStateOf("")
    }
    val selectedFolderNote = rememberSaveable {
        mutableStateOf("")
    }
    val optionsBtmSheetVM: OptionsBtmSheetVM = viewModel()
    LinkoraTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            if (archiveScreenType == ArchiveScreenType.LINKS) {
                if (archiveLinksData.isNotEmpty()) {
                    items(archiveLinksData) {
                        LinkUIComponent(
                            LinkUIComponentParam(title = it.title,
                                webBaseURL = it.baseURL,
                                imgURL = it.imgURL,
                                onMoreIconCLick = {
                                    archiveScreenVM.selectedArchivedLinkData.value = it
                                    shouldOptionsBtmModalSheetBeVisible.value = true
                                    selectedURLOrFolderName.value = it.webURL
                                    selectedFolderNote.value = it.infoForSaving
                                    selectedURLTitle.value = it.title
                                    coroutineScope.launch {
                                        optionsBtmSheetVM.updateArchiveLinkCardData(url = it.webURL)
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
                                    archiveScreenVM.onLinkClick(
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
                        DataEmptyScreen(text = "No links were archived.")
                    }
                    item {
                        Spacer(modifier = Modifier.height(165.dp))
                    }
                }
            } else {
                if (archiveFoldersDataV9.isNotEmpty()) {
                    items(archiveFoldersDataV9) {
                        FolderIndividualComponent(folderName = it.archiveFolderName,
                            folderNote = it.infoForSaving,
                            onMoreIconClick = {
                                SpecificScreenVM.selectedArchiveFolderID = it.id
                                shouldOptionsBtmModalSheetBeVisible.value = true
                                selectedURLOrFolderName.value = it.archiveFolderName
                                selectedFolderNote.value = it.infoForSaving
                                coroutineScope.launch {
                                    optionsBtmSheetVM.updateArchiveFolderCardData(folderName = it.archiveFolderName)
                                }
                            },
                            onFolderClick = {
                                SpecificScreenVM.isSelectedV9 = true
                                SpecificScreenVM.selectedArchiveFolderID =
                                    it.id
                                SpecificScreenVM.screenType.value =
                                    SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN
                                navController.navigate(NavigationRoutes.SPECIFIC_SCREEN.name)
                            })
                    }
                }
                if (archiveFoldersDataV10.isNotEmpty()) {
                    items(archiveFoldersDataV10) {
                        FolderIndividualComponent(folderName = it.folderName,
                            folderNote = it.infoForSaving,
                            onMoreIconClick = {
                                SpecificScreenVM.selectedArchiveFolderID = it.id
                                shouldOptionsBtmModalSheetBeVisible.value = true
                                selectedURLOrFolderName.value = it.folderName
                                selectedFolderNote.value = it.infoForSaving
                                coroutineScope.launch {
                                    optionsBtmSheetVM.updateArchiveFolderCardData(folderName = it.folderName)
                                }
                            },
                            onFolderClick = {
                                SpecificScreenVM.isSelectedV9 = false
                                SpecificScreenVM.selectedArchiveFolderID =
                                    it.id
                                SpecificScreenVM.currentClickedFolderData.value.id = it.id
                                SpecificScreenVM.screenType.value =
                                    SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN
                                navController.navigate(NavigationRoutes.SPECIFIC_SCREEN.name)
                            })
                    }
                }

                if (archiveFoldersDataV9.isEmpty() && archiveFoldersDataV10.isEmpty()) {
                    item {
                        DataEmptyScreen(text = "No folders were archived.")
                    }
                }
            }
        }
        OptionsBtmSheetUI(
            OptionsBtmSheetUIParam(
                inArchiveScreen = mutableStateOf(true),
                btmModalSheetState = btmModalSheetState,
                shouldBtmModalSheetBeVisible = shouldOptionsBtmModalSheetBeVisible,
                btmSheetFor = if (archiveScreenType == ArchiveScreenType.LINKS) OptionsBtmSheetType.LINK else OptionsBtmSheetType.FOLDER,
                onUnarchiveClick = {
                    if (SpecificScreenVM.isSelectedV9) {
                        archiveScreenVM.onUnArchiveClickV9(
                            context = context,
                            archiveScreenType = archiveScreenType,
                            selectedURLOrFolderName = selectedURLOrFolderName.value,
                            selectedURLOrFolderNote = selectedFolderNote.value,
                            onTaskCompleted = {
                                archiveScreenVM.changeRetrievedData(
                                    sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                                        SettingsScreenVM.Settings.selectedSortingType.value
                                    )
                                )
                            }, selectedFolderID = SpecificScreenVM.selectedArchiveFolderID
                        )
                    } else {
                        archiveScreenVM.onUnArchiveClickV10(SpecificScreenVM.selectedArchiveFolderID)
                    }
                },
                onDeleteCardClick = {
                    shouldDeleteDialogBoxAppear.value = true
                },
                onRenameClick = {
                    shouldRenameDialogBoxAppear.value = true
                },
                onArchiveClick = {},
                importantLinks = null,
                noteForSaving = selectedFolderNote.value,
                onNoteDeleteCardClick = {
                    archiveScreenVM.onNoteDeleteCardClick(
                        archiveScreenType = archiveScreenType,
                        selectedURLOrFolderName = selectedURLOrFolderName.value,
                        context = context,
                        onTaskCompleted = {}, folderID = SpecificScreenVM.selectedArchiveFolderID
                    )
                },
                folderName = if (archiveScreenType == ArchiveScreenType.FOLDERS) selectedURLOrFolderName.value else "",
                linkTitle = if (archiveScreenType == ArchiveScreenType.LINKS) selectedURLTitle.value else ""
            )
        )

        DeleteDialogBox(
            DeleteDialogBoxParam(shouldDialogBoxAppear = shouldDeleteDialogBoxAppear,
                deleteDialogBoxType = if (archiveScreenType == ArchiveScreenType.LINKS) DataDialogBoxType.LINK else DataDialogBoxType.FOLDER,
                onDeleteClick = {
                    archiveScreenVM.onDeleteClick(
                        archiveScreenType = archiveScreenType,
                        selectedURLOrFolderName = selectedURLOrFolderName.value,
                        context = context,
                        onTaskCompleted = {
                            archiveScreenVM.changeRetrievedData(
                                sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                                    SettingsScreenVM.Settings.selectedSortingType.value
                                )
                            )
                        }
                    )
                }, onDeleted = {
                    archiveScreenVM.changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        )
                    )
                })
        )
        RenameDialogBox(
            RenameDialogBoxParam(
                inChildArchiveFolderScreen = mutableStateOf(archiveScreenType != ArchiveScreenType.LINKS),
                renameDialogBoxFor = if (archiveScreenType == ArchiveScreenType.LINKS) OptionsBtmSheetType.LINK else OptionsBtmSheetType.FOLDER,
                shouldDialogBoxAppear = shouldRenameDialogBoxAppear,
                existingFolderName = selectedURLOrFolderName.value,
                onNoteChangeClickForLinks = { newNote: String ->
                    archiveScreenVM.onNoteChangeClickForLinks(
                        archiveScreenType = archiveScreenType,
                        selectedURLOrFolderName.value,
                        newNote,
                        onTaskCompleted = {}, folderID = SpecificScreenVM.selectedArchiveFolderID
                    )
                },
                onTitleChangeClickForLinks = { newTitle: String ->
                    archiveScreenVM.onTitleChangeClickForLinks(
                        archiveScreenType = archiveScreenType,
                        newTitle,
                        selectedURLOrFolderName.value,
                        onTaskCompleted = {
                            archiveScreenVM.changeRetrievedData(
                                sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                                    SettingsScreenVM.Settings.selectedSortingType.value
                                )
                            )
                        }, folderID = SpecificScreenVM.selectedArchiveFolderID
                    )
                },
                onTitleRenamed = {
                    archiveScreenVM.changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        )
                    )
                },
                currentFolderID = SpecificScreenVM.selectedArchiveFolderID,
                parentFolderID = null
            )
        )
    }
}