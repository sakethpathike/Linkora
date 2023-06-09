package com.sakethh.linkora.screens.collections.archiveScreen

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sakethh.linkora.btmSheet.OptionsBtmSheetType
import com.sakethh.linkora.btmSheet.OptionsBtmSheetUI
import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.customWebTab.openInWeb
import com.sakethh.linkora.localDB.ArchivedFolders
import com.sakethh.linkora.localDB.ArchivedLinks
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.localDB.LinksTable
import com.sakethh.linkora.localDB.RecentlyVisited
import com.sakethh.linkora.navigation.NavigationRoutes
import com.sakethh.linkora.screens.DataEmptyScreen
import com.sakethh.linkora.screens.collections.FolderIndividualComponent
import com.sakethh.linkora.screens.collections.specificScreen.SpecificScreenType
import com.sakethh.linkora.screens.collections.specificScreen.SpecificScreenVM
import com.sakethh.linkora.screens.home.composables.DataDialogBoxType
import com.sakethh.linkora.screens.home.composables.DeleteDialogBox
import com.sakethh.linkora.screens.home.composables.LinkUIComponent
import com.sakethh.linkora.screens.home.composables.RenameDialogBox
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildArchiveScreen(archiveScreenType: ArchiveScreenType, navController: NavController) {
    val archiveScreenVM: ArchiveScreenVM = viewModel()
    val archiveLinksData = archiveScreenVM.archiveLinksData.collectAsState().value
    val archiveFoldersData = archiveScreenVM.archiveFoldersData.collectAsState().value
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
    val selectedURLOrFolderName = rememberSaveable {
        mutableStateOf("")
    }
    val selectedURLOrFolderNote = rememberSaveable {
        mutableStateOf("")
    }
    val selectedArchivedLinkData = rememberSaveable {
        mutableStateOf(ArchivedLinks("", "", "", "", ""))
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
                            title = it.title,
                            webBaseURL = it.baseURL,
                            imgURL = it.imgURL,
                            onMoreIconCLick = {
                                selectedArchivedLinkData.value = it
                                shouldOptionsBtmModalSheetBeVisible.value = true
                                selectedURLOrFolderName.value = it.webURL
                                selectedURLOrFolderNote.value = it.infoForSaving
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
                                        ), context = context, uriHandler = uriHandler
                                    )
                                }
                            },
                            webURL = it.webURL
                        )
                    }
                } else {
                    item {
                        DataEmptyScreen()
                    }
                }
            } else {
                if (archiveFoldersData.isNotEmpty()) {
                    items(archiveFoldersData) {
                        FolderIndividualComponent(folderName = it.archiveFolderName,
                            folderNote = it.infoForSaving,
                            onMoreIconClick = {
                                shouldOptionsBtmModalSheetBeVisible.value = true
                                selectedURLOrFolderName.value = it.archiveFolderName
                                selectedURLOrFolderNote.value = it.infoForSaving
                                coroutineScope.launch {
                                    optionsBtmSheetVM.updateArchiveFolderCardData(folderName = it.archiveFolderName)
                                }
                            },
                            onFolderClick = {
                                SpecificScreenVM.selectedArchiveFolderName.value =
                                    it.archiveFolderName
                                SpecificScreenVM.screenType.value =
                                    SpecificScreenType.ARCHIVE_SCREEN
                                navController.navigate(NavigationRoutes.SPECIFIC_SCREEN.name)
                            })
                    }
                } else {
                    item {
                        DataEmptyScreen()
                    }
                }
            }
        }
        OptionsBtmSheetUI(
            inArchiveScreen = mutableStateOf(true),
            btmModalSheetState = btmModalSheetState,
            shouldBtmModalSheetBeVisible = shouldOptionsBtmModalSheetBeVisible,
            coroutineScope = coroutineScope,
            btmSheetFor = if (archiveScreenType == ArchiveScreenType.LINKS) OptionsBtmSheetType.LINK else OptionsBtmSheetType.FOLDER,
            onUnarchiveClick = {
                if (archiveScreenType == ArchiveScreenType.FOLDERS) {
                    coroutineScope.launch {
                        CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                            .moveArchiveFolderBackToFolder(folderName = selectedURLOrFolderName.value)
                        CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                            .deleteAnArchiveFolder(folderName = selectedURLOrFolderName.value)
                    }.invokeOnCompletion {
                        Toast.makeText(
                            context, "Unarchived successfully", Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    coroutineScope.launch {
                        CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                            .addANewLinkToSavedLinksOrInFolders(
                                LinksTable(
                                    title = selectedArchivedLinkData.value.title,
                                    webURL = selectedArchivedLinkData.value.webURL,
                                    baseURL = selectedArchivedLinkData.value.baseURL,
                                    imgURL = selectedArchivedLinkData.value.imgURL,
                                    infoForSaving = selectedArchivedLinkData.value.infoForSaving,
                                    isLinkedWithSavedLinks = true,
                                    isLinkedWithFolders = false,
                                    keyOfLinkedFolder = "",
                                    isLinkedWithImpFolder = false,
                                    keyOfImpLinkedFolder = "",
                                    isLinkedWithArchivedFolder = false,
                                    keyOfArchiveLinkedFolder = ""
                                )
                            )
                        CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                            .deleteALinkFromArchiveLinks(selectedArchivedLinkData.value.webURL)
                    }.invokeOnCompletion {
                        Toast.makeText(
                            context,
                            "Unarchived and moved the link to \"Saved Links\"",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
            noteForSaving = selectedURLOrFolderNote.value
        )

        DeleteDialogBox(shouldDialogBoxAppear = shouldDeleteDialogBoxAppear,
            deleteDialogBoxType = if (archiveScreenType == ArchiveScreenType.LINKS) DataDialogBoxType.LINK else DataDialogBoxType.FOLDER,
            onDeleteClick = {
                if (archiveScreenType == ArchiveScreenType.LINKS) {
                    coroutineScope.launch {
                        CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                            .deleteALinkFromArchiveLinks(webURL = selectedURLOrFolderName.value)
                    }.invokeOnCompletion {
                        Toast.makeText(
                            context, "removed the link from archive permanently", Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    coroutineScope.launch {
                        CustomLocalDBDaoFunctionsDecl.archiveFolderTableUpdater(
                            ArchivedFolders(
                                archiveFolderName = selectedURLOrFolderName.value,
                                infoForSaving = ""
                            ), context = context
                        )
                    }
                }
            })
        RenameDialogBox(
            shouldDialogBoxAppear = shouldRenameDialogBoxAppear,
            coroutineScope = coroutineScope,
            existingFolderName = "",
            onNoteChangeClickForLinks = { webURL: String, newNote: String ->
                if (archiveScreenType == ArchiveScreenType.LINKS) {
                    coroutineScope.launch {
                        CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                            .renameALinkInfoFromArchiveLinks(webURL, newNote)
                    }.invokeOnCompletion {
                        Toast.makeText(
                            context,
                            "updated data successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    coroutineScope.launch {
                        CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                            .renameALinkInfoFromArchiveFolders(
                                webURL = webURL,
                                newInfo = newNote,
                                folderName = selectedURLOrFolderName.value
                            )
                    }.invokeOnCompletion {
                        Toast.makeText(
                            context,
                            "updated data successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Unit
            },
            onTitleChangeClickForLinks = { webURL: String, newTitle: String ->
                if (archiveScreenType == ArchiveScreenType.LINKS) {
                    coroutineScope.launch {
                        CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                            .renameALinkTitleFromArchiveLinks(webURL, newTitle)
                    }.invokeOnCompletion {
                        Toast.makeText(
                            context,
                            "updated data successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    coroutineScope.launch {
                        CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                            .renameALinkTitleFromFolders(
                                webURL = webURL,
                                newTitle = newTitle,
                                folderName = selectedURLOrFolderName.value
                            )
                    }.invokeOnCompletion {
                        Toast.makeText(
                            context,
                            "updated data successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Unit
            }
        )
    }
}