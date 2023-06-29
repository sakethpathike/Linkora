package com.sakethh.linkora.screens.collections.archiveScreen

import android.annotation.SuppressLint
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
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.localDB.RecentlyVisited
import com.sakethh.linkora.navigation.NavigationRoutes
import com.sakethh.linkora.screens.collections.FolderIndividualComponent
import com.sakethh.linkora.screens.collections.specificScreen.SpecificScreenType
import com.sakethh.linkora.screens.collections.specificScreen.SpecificScreenVM
import com.sakethh.linkora.screens.home.composables.LinkUIComponent
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
    val selectedURLOrFolderName = rememberSaveable {
        mutableStateOf("")
    }
    val optionsBtmSheetVM:OptionsBtmSheetVM = viewModel()
    LinkoraTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            if (archiveScreenType == ArchiveScreenType.LINKS) {
                items(archiveLinksData) {
                    LinkUIComponent(
                        title = it.title,
                        webBaseURL = it.baseURL,
                        imgURL = it.imgURL,
                        onMoreIconCLick = {
                            shouldOptionsBtmModalSheetBeVisible.value = true
                            selectedURLOrFolderName.value = it.webURL
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
                items(archiveFoldersData) {
                    FolderIndividualComponent(
                        folderName = it.archiveFolderName,
                        folderNote = it.infoForSaving,
                        onMoreIconClick = {
                            shouldOptionsBtmModalSheetBeVisible.value = true
                            selectedURLOrFolderName.value = it.archiveFolderName
                            coroutineScope.launch {
                                optionsBtmSheetVM.updateArchiveFolderCardData(folderName = it.archiveFolderName)
                            }
                        }, onFolderClick = {
                            SpecificScreenVM.screenType.value = SpecificScreenType.ARCHIVE_SCREEN
                            navController.navigate(NavigationRoutes.SPECIFIC_SCREEN.name)
                        })
                }
            }
        }
        OptionsBtmSheetUI(
            inArchiveScreen = mutableStateOf(true),
            btmModalSheetState = btmModalSheetState,
            shouldBtmModalSheetBeVisible = shouldOptionsBtmModalSheetBeVisible,
            coroutineScope = coroutineScope,
            btmSheetFor = if (archiveScreenType == ArchiveScreenType.LINKS) OptionsBtmSheetType.LINK else OptionsBtmSheetType.FOLDER,
            onDeleteCardClick = {

            },
            onRenameClick = {

            },
            onArchiveClick = {
                if (archiveScreenType == ArchiveScreenType.LINKS) {
                    coroutineScope.launch {
                        CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                            .deleteALinkFromArchiveLinks(webURL = selectedURLOrFolderName.value)
                    }
                } else {
                    coroutineScope.launch {
                        CustomLocalDBDaoFunctionsDecl.archiveFolderTableUpdater(
                            ArchivedFolders(
                                archiveFolderName = selectedURLOrFolderName.value,
                                infoForSaving = ""
                            )
                        )
                    }
                }
            },
            importantLinks = null
        )
    }
}