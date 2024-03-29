package com.sakethh.linkora.ui.screens.collections.archiveScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import com.sakethh.linkora.ui.viewmodels.collections.ArchiveScreenType
import com.sakethh.linkora.ui.viewmodels.collections.ArchiveScreenVM
import com.sakethh.linkora.ui.viewmodels.collections.CollectionsScreenVM
import com.sakethh.linkora.ui.viewmodels.collections.SpecificCollectionsScreenVM
import com.sakethh.linkora.ui.viewmodels.collections.SpecificScreenType
import com.sakethh.linkora.ui.viewmodels.commonBtmSheets.OptionsBtmSheetType
import com.sakethh.linkora.ui.viewmodels.commonBtmSheets.OptionsBtmSheetVM
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildArchiveScreen(archiveScreenType: ArchiveScreenType, navController: NavController) {
    val archiveScreenVM: ArchiveScreenVM = viewModel()
    val archiveLinksData = archiveScreenVM.archiveLinksData.collectAsStateWithLifecycle().value
    val archiveFoldersDataV9 =
        archiveScreenVM.archiveFoldersDataV9.collectAsStateWithLifecycle().value
    val archiveFoldersDataV10 =
        archiveScreenVM.archiveFoldersDataV10.collectAsStateWithLifecycle().value
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val btmModalSheetState =
        androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shouldOptionsBtmModalSheetBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldDeleteDialogBoxAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldRenameDialogBoxAppear = rememberSaveable {
        mutableStateOf(false)
    }
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
                    itemsIndexed(
                        items = archiveLinksData,
                        key = { _, archivedLinks ->
                            archivedLinks.id.toString() + archivedLinks.baseURL
                        }) { index, it ->
                        LinkUIComponent(
                            LinkUIComponentParam(
                                onLongClick = {
                                    if (!archiveScreenVM.isSelectionModeEnabled.value) {
                                        archiveScreenVM.isSelectionModeEnabled.value =
                                            true
                                        archiveScreenVM.selectedLinksData.add(it)
                                    }
                                },
                                isSelectionModeEnabled = archiveScreenVM.isSelectionModeEnabled,
                                title = it.title,
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
                                    if (archiveScreenVM.isSelectionModeEnabled.value) {
                                        if (!archiveScreenVM.selectedLinksData.contains(it)) {
                                            archiveScreenVM.selectedLinksData.add(
                                                it
                                            )
                                        } else {
                                            archiveScreenVM.selectedLinksData.remove(
                                                it
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
                                isItemSelected = mutableStateOf(
                                    archiveScreenVM.selectedLinksData.contains(
                                        it
                                    )
                                )
                            )
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
                    itemsIndexed(items = archiveFoldersDataV9, key = { _, archivedFolders ->
                        archivedFolders.id.toString() + archivedFolders.archiveFolderName + archivedFolders.id.toString()
                    }) { index, it ->
                        FolderIndividualComponent(
                            showCheckBox = archiveScreenVM.isSelectionModeEnabled,
                            isCheckBoxChecked = mutableStateOf(
                                archiveScreenVM.selectedFoldersID.contains(
                                    it.id
                                )
                            ),
                            checkBoxState = { checkBoxState ->
                                if (checkBoxState) {
                                    archiveScreenVM.selectedFoldersID.add(
                                        it.id
                                    )
                                } else {
                                    archiveScreenVM.selectedFoldersID.removeAll { long ->
                                        long == it.id
                                    }
                                }
                            },
                            folderName = it.archiveFolderName,
                            showMoreIcon = !archiveScreenVM.isSelectionModeEnabled.value,
                            folderNote = it.infoForSaving,
                            onMoreIconClick = {
                                CollectionsScreenVM.selectedFolderData.value.id = it.id
                                shouldOptionsBtmModalSheetBeVisible.value = true
                                selectedURLOrFolderName.value = it.archiveFolderName
                                selectedFolderNote.value = it.infoForSaving
                                coroutineScope.launch {
                                    optionsBtmSheetVM.updateArchiveFolderCardData(it.id)
                                }
                            },
                            onFolderClick = { _ ->
                                CollectionsScreenVM.currentClickedFolderData.value.id = it.id
                                CollectionsScreenVM.currentClickedFolderData.value.folderName =
                                    it.archiveFolderName
                                CollectionsScreenVM.selectedFolderData.value.id =
                                    it.id
                                CollectionsScreenVM.selectedFolderData.value.folderName =
                                    it.archiveFolderName
                                SpecificCollectionsScreenVM.screenType.value =
                                    SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN
                                navController.navigate(NavigationRoutes.SPECIFIC_COLLECTION_SCREEN.name)
                            })
                    }
                }
                if (archiveFoldersDataV10.isNotEmpty()) {
                    itemsIndexed(
                        items = archiveFoldersDataV10,
                        key = { _, foldersTable ->
                            foldersTable.folderName + foldersTable.id.toString() + foldersTable.folderName
                        }) { index, it ->
                        FolderIndividualComponent(
                            showCheckBox = archiveScreenVM.isSelectionModeEnabled,
                            isCheckBoxChecked = mutableStateOf(
                                archiveScreenVM.selectedFoldersID.contains(
                                    it.id
                                )
                            ),
                            checkBoxState = { checkBoxState ->
                                if (checkBoxState) {
                                    archiveScreenVM.selectedFoldersID.add(
                                        it.id
                                    )
                                } else {
                                    archiveScreenVM.selectedFoldersID.removeAll { long ->
                                        long == it.id
                                    }
                                }
                            },
                            folderName = it.folderName,
                            folderNote = it.infoForSaving,
                            onMoreIconClick = {
                                CollectionsScreenVM.selectedFolderData.value.id = it.id
                                shouldOptionsBtmModalSheetBeVisible.value = true
                                selectedURLOrFolderName.value = it.folderName
                                selectedFolderNote.value = it.infoForSaving
                                coroutineScope.launch {
                                    optionsBtmSheetVM.updateArchiveFolderCardData(it.id)
                                }
                            },
                            showMoreIcon = !archiveScreenVM.isSelectionModeEnabled.value,
                            onFolderClick = { _ ->
                                if (!archiveScreenVM.isSelectionModeEnabled.value) {
                                    CollectionsScreenVM.currentClickedFolderData.value = it
                                    CollectionsScreenVM.selectedFolderData.value = it
                                    SpecificCollectionsScreenVM.inARegularFolder.value = false
                                    SpecificCollectionsScreenVM.screenType.value =
                                        SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN
                                    navController.navigate(NavigationRoutes.SPECIFIC_COLLECTION_SCREEN.name)
                                }
                            }, onLongClick = {
                                if (!archiveScreenVM.isSelectionModeEnabled.value) {
                                    archiveScreenVM.isSelectionModeEnabled.value =
                                        true
                                    archiveScreenVM.areAllFoldersChecked.value =
                                        false
                                    archiveScreenVM.selectedFoldersID.clear()
                                    archiveScreenVM.selectedFoldersID.add(
                                        it.id
                                    )
                                }
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
                    if (archiveScreenType == ArchiveScreenType.FOLDERS) {
                        archiveScreenVM.onUnArchiveClickV10(CollectionsScreenVM.selectedFolderData.value.id)
                    } else {
                        archiveScreenVM.onUnArchiveLinkClick(
                            archiveScreenVM.selectedArchivedLinkData.value
                        )
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
                        onTaskCompleted = {},
                        folderID = CollectionsScreenVM.selectedFolderData.value.id
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
                renameDialogBoxFor = if (archiveScreenType == ArchiveScreenType.LINKS) OptionsBtmSheetType.LINK else OptionsBtmSheetType.FOLDER,
                shouldDialogBoxAppear = shouldRenameDialogBoxAppear,
                existingFolderName = selectedURLOrFolderName.value,
                onNoteChangeClick = { newNote: String ->
                    archiveScreenVM.onNoteChangeClick(
                        archiveScreenType = archiveScreenType,
                        selectedURLOrFolderName.value,
                        newNote,
                        onTaskCompleted = {
                            shouldRenameDialogBoxAppear.value = false
                        }, folderID = CollectionsScreenVM.selectedFolderData.value.id
                    )
                },
                onTitleChangeClick = { newTitle: String ->
                    archiveScreenVM.onTitleChangeClick(
                        archiveScreenType = archiveScreenType,
                        newTitle = newTitle,
                        webURL = selectedURLOrFolderName.value,
                        onTaskCompleted = {
                            shouldRenameDialogBoxAppear.value = false
                            archiveScreenVM.changeRetrievedData(
                                sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                                    SettingsScreenVM.Settings.selectedSortingType.value
                                )
                            )
                        }, folderID = CollectionsScreenVM.selectedFolderData.value.id
                    )
                }
            )
        )
    }
}