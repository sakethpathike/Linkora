package com.sakethh.linkora.ui.screens.collections.archive

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.sakethh.linkora.data.local.ArchivedLinks
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
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenVM
import com.sakethh.linkora.ui.screens.collections.specific.SpecificScreenType
import com.sakethh.linkora.ui.screens.linkLayout.LinkLayout
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SortingPreferences
import com.sakethh.linkora.ui.theme.LinkoraTheme
import com.sakethh.linkora.utils.baseUrl
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildArchiveScreen(
    archiveScreenType: ArchiveScreenType,
    navController: NavController,
    customWebTab: CustomWebTab
) {
    val archiveScreenVM: ArchiveScreenVM = hiltViewModel()
    val archiveLinksData = archiveScreenVM.archiveLinksData.collectAsStateWithLifecycle().value
    val archiveFoldersDataV9 =
        archiveScreenVM.archiveFoldersDataV9.collectAsStateWithLifecycle().value
    val archiveFoldersDataV10 =
        archiveScreenVM.archiveFoldersDataV10.collectAsStateWithLifecycle().value
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        archiveScreenVM.channelEvent.collectLatest {
            when (it) {
                is CommonUiEvent.ShowToast -> {
                    Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }
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
    val selectedItemTitle = rememberSaveable {
        mutableStateOf("")
    }
    val selectedURLImgLink = rememberSaveable {
        mutableStateOf("")
    }
    val selectedItemNote = rememberSaveable {
        mutableStateOf("")
    }
    val optionsBtmSheetVM: OptionsBtmSheetVM = hiltViewModel()

    fun commonLinkParam(it: ArchivedLinks): LinkUIComponentParam {
        return LinkUIComponentParam(
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
            onMoreIconClick = {
                archiveScreenVM.selectedArchivedLinkData.value = it
                shouldOptionsBtmModalSheetBeVisible.value = true
                selectedURLOrFolderName.value = it.webURL
                selectedItemNote.value = it.infoForSaving
                selectedItemTitle.value = it.title
                selectedURLImgLink.value = it.imgURL
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
                        customWebTab.openInWeb(
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
                customWebTab.openInWeb(
                    recentlyVisitedData = RecentlyVisited(
                        title = it.title,
                        webURL = it.webURL,
                        baseURL = it.baseURL,
                        imgURL = it.imgURL,
                        infoForSaving = it.infoForSaving
                    ), context = context, uriHandler = uriHandler,
                    forceOpenInExternalBrowser = false
                )
            },
            isItemSelected = mutableStateOf(
                archiveScreenVM.selectedLinksData.contains(
                    it
                )
            )
        )
    }

    LinkoraTheme {
        if (archiveScreenType == ArchiveScreenType.LINKS) {
            if (archiveLinksData.isNotEmpty()) {
                when (SettingsPreference.currentlySelectedLinkLayout.value) {
                    LinkLayout.REGULAR_LIST_VIEW.name, LinkLayout.TITLE_ONLY_LIST_VIEW.name -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .animateContentSize()
                        ) {
                            itemsIndexed(
                                items = archiveLinksData,
                                key = { _, archivedLinks ->
                                    archivedLinks.id.toString() + archivedLinks.baseURL
                                }) { index, it ->
                                ListViewLinkUIComponent(
                                    commonLinkParam(it),
                                    forTitleOnlyView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.TITLE_ONLY_LIST_VIEW.name
                                )
                            }
                        }
                    }

                    LinkLayout.GRID_VIEW.name -> {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(150.dp),
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxSize()
                                .animateContentSize()
                        ) {
                            itemsIndexed(
                                items = archiveLinksData,
                                key = { _, archivedLinks ->
                                    archivedLinks.id.toString() + archivedLinks.baseURL
                                }) { index, it ->
                                GridViewLinkUIComponent(
                                    commonLinkParam(it),
                                    forStaggeredView = false
                                )
                            }
                        }
                    }

                    LinkLayout.STAGGERED_VIEW.name -> {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Adaptive(150.dp),
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxSize()
                                .animateContentSize()
                        ) {
                            itemsIndexed(
                                items = archiveLinksData,
                                key = { _, archivedLinks ->
                                    archivedLinks.id.toString() + archivedLinks.baseURL
                                }) { index, it ->
                                GridViewLinkUIComponent(
                                    commonLinkParam(it),
                                    forStaggeredView = true
                                )
                            }
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    DataEmptyScreen(text = LocalizedStrings.noLinksWereArchived.value)
                    Spacer(modifier = Modifier.height(165.dp))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
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
                                selectedItemNote.value = it.infoForSaving
                                selectedItemTitle.value = it.archiveFolderName
                                CollectionsScreenVM.selectedFolderData.value.id = it.id
                                shouldOptionsBtmModalSheetBeVisible.value = true
                                selectedURLOrFolderName.value = it.archiveFolderName
                                selectedItemNote.value = it.infoForSaving
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
                                selectedItemNote.value = it.infoForSaving
                                selectedItemTitle.value = it.folderName
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
                        DataEmptyScreen(text = LocalizedStrings.noFoldersWereArchived.value)
                    }
                }
            }
        }
        MenuBtmSheetUI(
            MenuBtmSheetParam(
                showQuickActions = mutableStateOf(
                    archiveScreenType == ArchiveScreenType.LINKS && (
                            SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name
                                    || SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.GRID_VIEW.name
                            )
                ),
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
                noteForSaving = selectedItemNote.value,
                onNoteDeleteCardClick = {
                    archiveScreenVM.onNoteDeleteCardClick(
                        archiveScreenType = archiveScreenType,
                        selectedURLOrFolderName = selectedURLOrFolderName.value,
                        onTaskCompleted = {},
                        folderID = CollectionsScreenVM.selectedFolderData.value.id
                    )
                },
                folderName = if (archiveScreenType == ArchiveScreenType.FOLDERS) selectedURLOrFolderName.value else "",
                linkTitle = if (archiveScreenType == ArchiveScreenType.LINKS) selectedItemTitle.value else "",
                imgLink = selectedURLImgLink.value,
                onRefreshClick = {
                    if (archiveScreenType == ArchiveScreenType.LINKS) {
                        archiveScreenVM.refreshArchivedLinkData(archiveScreenVM.selectedArchivedLinkData.value.id)
                    }
                },
                webUrl = selectedURLOrFolderName.value,
                onForceOpenInExternalBrowserClicked = {
                    customWebTab.openInWeb(
                        recentlyVisitedData = RecentlyVisited(
                            title = selectedItemTitle.value,
                            webURL = selectedURLOrFolderName.value,
                            baseURL = selectedURLOrFolderName.value.baseUrl(),
                            imgURL = selectedURLImgLink.value,
                            infoForSaving = selectedItemNote.value
                        ), context = context, uriHandler = uriHandler,
                        forceOpenInExternalBrowser = false
                    )
                },
            )
        )

        DeleteDialogBox(
            DeleteDialogBoxParam(shouldDialogBoxAppear = shouldDeleteDialogBoxAppear,
                deleteDialogBoxType = if (archiveScreenType == ArchiveScreenType.LINKS) DataDialogBoxType.LINK else DataDialogBoxType.FOLDER,
                onDeleteClick = {
                    archiveScreenVM.onDeleteClick(
                        archiveScreenType = archiveScreenType,
                        selectedURLOrFolderName = selectedURLOrFolderName.value,
                        onTaskCompleted = {
                            archiveScreenVM.changeRetrievedData(
                                sortingPreferences = SortingPreferences.valueOf(
                                    SettingsPreference.selectedSortingType.value
                                )
                            )
                        }
                    )
                }, onDeleted = {
                    archiveScreenVM.changeRetrievedData(
                        sortingPreferences = SortingPreferences.valueOf(
                            SettingsPreference.selectedSortingType.value
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
                                sortingPreferences = SortingPreferences.valueOf(
                                    SettingsPreference.selectedSortingType.value
                                )
                            )
                        }, folderID = CollectionsScreenVM.selectedFolderData.value.id
                    )
                },
                existingTitle = selectedItemTitle.value,
                existingNote = selectedItemNote.value
            )
        )
    }
}