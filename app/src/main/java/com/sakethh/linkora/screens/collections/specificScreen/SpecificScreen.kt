package com.sakethh.linkora.screens.collections.specificScreen

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sakethh.linkora.btmSheet.OptionsBtmSheetType
import com.sakethh.linkora.btmSheet.OptionsBtmSheetUI
import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.customWebTab.openInWeb
import com.sakethh.linkora.localDB.ArchivedLinks
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.localDB.ImportantLinks
import com.sakethh.linkora.localDB.RecentlyVisited
import com.sakethh.linkora.screens.DataEmptyScreen
import com.sakethh.linkora.screens.home.composables.AddNewLinkDialogBox
import com.sakethh.linkora.screens.home.composables.DataDialogBoxType
import com.sakethh.linkora.screens.home.composables.DeleteDialogBox
import com.sakethh.linkora.screens.home.composables.LinkUIComponent
import com.sakethh.linkora.screens.home.composables.RenameDialogBox
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecificScreen(navController: NavController) {
    val specificScreenVM: SpecificScreenVM = viewModel()
    val selectedWebURL = rememberSaveable {
        mutableStateOf("")
    }
    val foldersData = specificScreenVM.foldersData.collectAsState().value
    val linksData = specificScreenVM.linksTable.collectAsState().value
    val impLinksData = specificScreenVM.impLinksTable.collectAsState().value
    val archiveLinksData = specificScreenVM.archiveFolderDataTable.collectAsState().value
    var tempImpLinkData = specificScreenVM.impLinkDataForBtmSheet.copy()
    val btmModalSheetState = androidx.compose.material3.rememberModalBottomSheetState()
    val shouldOptionsBtmModalSheetBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldRenameDialogBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldDeleteDialogBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val selectedURLOrFolderNote = rememberSaveable {
        mutableStateOf("")
    }
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val optionsBtmSheetVM: OptionsBtmSheetVM = viewModel()
    val topBarText = when (SpecificScreenVM.screenType.value) {
        SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
            "Important Links"
        }

        SpecificScreenType.ARCHIVE_SCREEN -> {
            SpecificScreenVM.selectedArchiveFolderName.value
        }

        SpecificScreenType.LINKS_SCREEN -> {
            "Saved Links"
        }

        SpecificScreenType.SPECIFIC_FOLDER_SCREEN -> {
            SpecificScreenVM.currentClickedFolderName.value
        }
    }
    val shouldNewLinkDialogBoxBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val isDataExtractingFromTheLink = rememberSaveable {
        mutableStateOf(false)
    }
    LinkoraTheme {
        Scaffold(floatingActionButtonPosition = FabPosition.End, floatingActionButton = {
            FloatingActionButton(
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    shouldNewLinkDialogBoxBeVisible.value = true
                }) {
                Icon(
                    imageVector = Icons.Default.AddLink,
                    contentDescription = null
                )
            }
        }, modifier = Modifier.background(MaterialTheme.colorScheme.surface), topBar = {
            TopAppBar(title = {
                Text(
                    text = topBarText,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 24.sp
                )
            })
        }) {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                when (SpecificScreenVM.screenType.value) {
                    SpecificScreenType.SPECIFIC_FOLDER_SCREEN -> {
                        items(foldersData) {
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
                                    tempImpLinkData.webURL =
                                        it.webURL
                                    shouldOptionsBtmModalSheetBeVisible.value = true
                                    coroutineScope.launch {
                                        awaitAll(
                                            async {
                                                optionsBtmSheetVM.updateImportantCardData(
                                                    url = selectedWebURL.value
                                                )
                                            },
                                            async { optionsBtmSheetVM.updateArchiveLinkCardData(url = selectedWebURL.value) })
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
                                            ),
                                            context = context,
                                            uriHandler = uriHandler
                                        )
                                    }
                                },
                                webURL = it.webURL
                            )
                        }
                    }

                    SpecificScreenType.LINKS_SCREEN -> {
                        if (linksData.isNotEmpty()) {
                            items(linksData) {
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
                                            awaitAll(
                                                async {
                                                    optionsBtmSheetVM.updateImportantCardData(
                                                        url = selectedWebURL.value
                                                    )
                                                },
                                                async {
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
                                                ),
                                                context = context,
                                                uriHandler = uriHandler
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
                                        tempImpLinkData.webURL =
                                            it.webURL
                                        shouldOptionsBtmModalSheetBeVisible.value = true
                                        coroutineScope.launch {
                                            awaitAll(
                                                async {
                                                    optionsBtmSheetVM.updateImportantCardData(
                                                        url = selectedWebURL.value
                                                    )
                                                },
                                                async {
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
                                                ),
                                                context = context,
                                                uriHandler = uriHandler
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
                    }

                    SpecificScreenType.ARCHIVE_SCREEN -> {
                        items(archiveLinksData) {
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
                                            ),
                                            context = context,
                                            uriHandler = uriHandler
                                        )
                                    }
                                },
                                webURL = it.webURL
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(175.dp))
                }
            }
        }
        OptionsBtmSheetUI(
            inArchiveScreen = mutableStateOf(SpecificScreenVM.screenType.value == SpecificScreenType.ARCHIVE_SCREEN),
            btmModalSheetState = btmModalSheetState,
            shouldBtmModalSheetBeVisible = shouldOptionsBtmModalSheetBeVisible,
            coroutineScope = coroutineScope,
            btmSheetFor = when (SpecificScreenVM.screenType.value) {
                SpecificScreenType.IMPORTANT_LINKS_SCREEN -> OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN
                SpecificScreenType.ARCHIVE_SCREEN -> OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN
                SpecificScreenType.LINKS_SCREEN -> OptionsBtmSheetType.LINK
                SpecificScreenType.SPECIFIC_FOLDER_SCREEN -> OptionsBtmSheetType.LINK
            },
            onDeleteCardClick = {
                shouldDeleteDialogBeVisible.value = true
            },
            onRenameClick = {
                when (SpecificScreenVM.screenType.value) {
                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                        shouldRenameDialogBeVisible.value = true
                    }

                    SpecificScreenType.ARCHIVE_SCREEN -> {
                        shouldRenameDialogBeVisible.value = true
                    }

                    SpecificScreenType.LINKS_SCREEN -> {
                        shouldRenameDialogBeVisible.value = true
                    }

                    SpecificScreenType.SPECIFIC_FOLDER_SCREEN -> {
                        shouldRenameDialogBeVisible.value = true
                    }
                }
            },
            importantLinks = tempImpLinkData,
            onArchiveClick = {
                when (SpecificScreenVM.screenType.value) {
                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                        coroutineScope.launch {
                            awaitAll(async {
                                CustomLocalDBDaoFunctionsDecl.archiveLinkTableUpdater(
                                    archivedLinks = ArchivedLinks(
                                        title = tempImpLinkData.title,
                                        webURL = tempImpLinkData.webURL,
                                        baseURL = tempImpLinkData.baseURL,
                                        imgURL = tempImpLinkData.imgURL,
                                        infoForSaving = tempImpLinkData.infoForSaving
                                    )
                                )
                            }, async {
                                CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                                    .deleteALinkFromImpLinks(webURL = tempImpLinkData.webURL)
                            })
                        }
                    }

                    SpecificScreenType.ARCHIVE_SCREEN -> {
                        coroutineScope.launch {
                            awaitAll(async {
                                CustomLocalDBDaoFunctionsDecl.archiveLinkTableUpdater(
                                    archivedLinks = ArchivedLinks(
                                        title = tempImpLinkData.title,
                                        webURL = tempImpLinkData.webURL,
                                        baseURL = tempImpLinkData.baseURL,
                                        imgURL = tempImpLinkData.imgURL,
                                        infoForSaving = tempImpLinkData.infoForSaving
                                    )
                                )
                            })
                        }
                    }

                    SpecificScreenType.LINKS_SCREEN -> {
                        coroutineScope.launch {
                            awaitAll(async {
                                CustomLocalDBDaoFunctionsDecl.archiveLinkTableUpdater(
                                    archivedLinks = ArchivedLinks(
                                        title = tempImpLinkData.title,
                                        webURL = tempImpLinkData.webURL,
                                        baseURL = tempImpLinkData.baseURL,
                                        imgURL = tempImpLinkData.imgURL,
                                        infoForSaving = tempImpLinkData.infoForSaving
                                    )
                                )
                            }, async {
                                CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                                    .deleteALinkFromSavedLinksOrInFolders(webURL = tempImpLinkData.webURL)
                            })
                        }
                    }

                    SpecificScreenType.SPECIFIC_FOLDER_SCREEN -> {
                        coroutineScope.launch {
                            awaitAll(async {
                                CustomLocalDBDaoFunctionsDecl.archiveLinkTableUpdater(
                                    archivedLinks = ArchivedLinks(
                                        title = tempImpLinkData.title,
                                        webURL = tempImpLinkData.webURL,
                                        baseURL = tempImpLinkData.baseURL,
                                        imgURL = tempImpLinkData.imgURL,
                                        infoForSaving = tempImpLinkData.infoForSaving
                                    )
                                )
                            }, async {
                                CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                                    .deleteALinkFromSavedLinksOrInFolders(webURL = tempImpLinkData.webURL)
                            })
                        }
                    }
                }
            },
            noteForSaving = selectedURLOrFolderNote.value
        )
        DeleteDialogBox(
            shouldDialogBoxAppear = shouldDeleteDialogBeVisible,
            onDeleteClick = {
                when (SpecificScreenVM.screenType.value) {
                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                        coroutineScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                                .deleteALinkFromImpLinks(webURL = selectedWebURL.value)
                        }
                    }

                    SpecificScreenType.ARCHIVE_SCREEN -> {
                        coroutineScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                                .deleteALinkFromArchiveLinks(webURL = selectedWebURL.value)
                        }
                    }

                    SpecificScreenType.LINKS_SCREEN -> {
                        coroutineScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                                .deleteALinkFromSavedLinksOrInFolders(webURL = selectedWebURL.value)
                        }
                    }

                    SpecificScreenType.SPECIFIC_FOLDER_SCREEN -> {
                        coroutineScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                                .deleteALinkFromSavedLinksOrInFolders(webURL = selectedWebURL.value)
                        }
                    }
                }
            },
            deleteDialogBoxType = DataDialogBoxType.LINK
        )
        RenameDialogBox(
            shouldDialogBoxAppear = shouldRenameDialogBeVisible,
            coroutineScope = coroutineScope,
            existingFolderName = "",
            renameDialogBoxFor = OptionsBtmSheetType.LINK,
            webURLForTitle = selectedWebURL.value,
            onNoteChangeClickForLinks  = {webURL: String, newNote: String ->
                when (SpecificScreenVM.screenType.value) {
                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                        coroutineScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                                .renameALinkInfoFromImpLinks(
                                    webURL = webURL,
                                    newInfo =newNote
                                )
                        }.start()
                        Unit
                    }

                    SpecificScreenType.ARCHIVE_SCREEN -> {

                    }

                    SpecificScreenType.LINKS_SCREEN -> {
                        coroutineScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                                .renameALinkInfoFromSavedLinksOrInFolders(
                                    webURL = webURL,
                                    newInfo = newNote
                                )
                        }.start()
                        Unit
                    }

                    SpecificScreenType.SPECIFIC_FOLDER_SCREEN -> {
                        coroutineScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                                .renameALinkInfoFromSavedLinksOrInFolders(
                                    webURL = webURL,
                                    newInfo = newNote
                                )
                        }.start()
                        Unit
                    }
                }
            },
            onTitleChangeClickForLinks= { webURL: String, newTitle: String ->
            when (SpecificScreenVM.screenType.value) {
                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                        coroutineScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                                .renameALinkTitleFromImpLinks(
                                    webURL = webURL,
                                    newTitle = newTitle
                                )
                        }.start()
                        Unit
                    }

                    SpecificScreenType.ARCHIVE_SCREEN -> {

                    }

                    SpecificScreenType.LINKS_SCREEN -> {
                        coroutineScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                                .renameALinkTitleFromSavedLinksOrInFolders(
                                    webURL = webURL,
                                    newTitle = newTitle
                                )
                        }.start()
                        Unit
                    }

                    SpecificScreenType.SPECIFIC_FOLDER_SCREEN -> {
                        coroutineScope.launch {
                            CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                                .renameALinkTitleFromSavedLinksOrInFolders(
                                    webURL = webURL,
                                    newTitle = newTitle
                                )
                        }.start()
                        Unit
                    }
                }
            }
        )
        AddNewLinkDialogBox(
            shouldDialogBoxAppear = shouldNewLinkDialogBoxBeVisible,
            onSaveBtnClick = { title: String, webURL: String, note: String, selectedFolderName: String ->
                if (webURL.isNotEmpty()) {
                    isDataExtractingFromTheLink.value = true
                }
                when (SpecificScreenVM.screenType.value) {
                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                        coroutineScope.launch {
                            CustomLocalDBDaoFunctionsDecl.importantLinkTableUpdater(
                                importantLinks = ImportantLinks(
                                    title = title,
                                    webURL = webURL,
                                    baseURL = "",
                                    imgURL = "",
                                    infoForSaving = note
                                )
                            )
                        }.invokeOnCompletion {
                            if (webURL.isNotEmpty()) {
                                isDataExtractingFromTheLink.value = false
                                shouldNewLinkDialogBoxBeVisible.value = false
                            }
                        }
                    }

                    SpecificScreenType.ARCHIVE_SCREEN -> {

                    }

                    SpecificScreenType.LINKS_SCREEN -> {
                        coroutineScope.launch {
                            CustomLocalDBDaoFunctionsDecl.addANewLinkSpecificallyInFolders(
                                title = title,
                                webURL = webURL,
                                noteForSaving = note,
                                folderName = null,
                                savingFor = CustomLocalDBDaoFunctionsDecl.ModifiedLocalDbFunctionsType.SAVED_LINKS
                            )
                        }.invokeOnCompletion {
                            if (webURL.isNotEmpty()) {
                                isDataExtractingFromTheLink.value = false
                                shouldNewLinkDialogBoxBeVisible.value = false
                            }
                        }
                    }

                    SpecificScreenType.SPECIFIC_FOLDER_SCREEN -> {
                        coroutineScope.launch {
                            CustomLocalDBDaoFunctionsDecl.addANewLinkSpecificallyInFolders(
                                folderName = SpecificScreenVM.currentClickedFolderName.value,
                                title = title,
                                webURL = webURL,
                                noteForSaving = note,
                                savingFor = CustomLocalDBDaoFunctionsDecl.ModifiedLocalDbFunctionsType.FOLDER_BASED_LINKS
                            )
                        }.invokeOnCompletion {
                            if (webURL.isNotEmpty()) {
                                isDataExtractingFromTheLink.value = false
                                shouldNewLinkDialogBoxBeVisible.value = false
                            }
                        }
                    }
                }
            },
            isDataExtractingForTheLink = isDataExtractingFromTheLink,
            inCollectionBasedFolder = mutableStateOf(true)
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