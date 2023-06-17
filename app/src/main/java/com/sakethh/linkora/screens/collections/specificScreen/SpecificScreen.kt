package com.sakethh.linkora.screens.collections.specificScreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sakethh.linkora.btmSheet.OptionsBtmSheetType
import com.sakethh.linkora.btmSheet.OptionsBtmSheetUI
import com.sakethh.linkora.customWebTab.openInWeb
import com.sakethh.linkora.localDB.LocalDBFunctions
import com.sakethh.linkora.screens.home.composables.DeleteDialogBox
import com.sakethh.linkora.screens.home.composables.DeleteDialogBoxType
import com.sakethh.linkora.screens.home.composables.LinkUIComponent
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecificScreen(navController: NavController) {
    val specificScreenVM: SpecificScreenVM = viewModel()
    val selectedWebURL = rememberSaveable {
        mutableStateOf("")
    }
    val foldersData = specificScreenVM.foldersData.collectAsState().value
    val linksData = specificScreenVM.linksTable.collectAsState().value
    val btmModalSheetState = androidx.compose.material3.rememberModalBottomSheetState()
    val shouldOptionsBtmModalSheetBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldDeleteDialogBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    LinkoraTheme {
        Scaffold(modifier = Modifier.background(MaterialTheme.colorScheme.surface), topBar = {
            TopAppBar(title = {
                Text(
                    text = "Specific Screen",
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
                        items(foldersData.links) {
                            LinkUIComponent(
                                title = it.title,
                                webBaseURL = it.baseURL,
                                imgURL = "https://i.pinimg.com/originals/73/b2/a8/73b2a8acdc03a65a1c2c8901a9ed1b0b.jpg",
                                onMoreIconCLick = {
                                    selectedWebURL.value = it.webURL
                                    shouldOptionsBtmModalSheetBeVisible.value = true
                                },
                                onLinkClick = {
                                    openInWeb(
                                        url = it.webURL,
                                        context = context,
                                        uriHandler = uriHandler
                                    )
                                }
                            )
                        }
                    }

                    SpecificScreenType.LINKS_SCREEN -> {
                        items(linksData) {
                            LinkUIComponent(
                                title = it.title,
                                webBaseURL = it.baseURL,
                                imgURL = "https://i.pinimg.com/originals/73/b2/a8/73b2a8acdc03a65a1c2c8901a9ed1b0b.jpg",
                                onMoreIconCLick = {
                                    selectedWebURL.value = it.webURL
                                    shouldOptionsBtmModalSheetBeVisible.value = true
                                },
                                onLinkClick = {
                                    openInWeb(
                                        url = it.webURL,
                                        context = context,
                                        uriHandler = uriHandler
                                    )
                                }
                            )
                        }
                    }

                    SpecificScreenType.FAVORITES_SCREEN -> {

                    }

                    SpecificScreenType.ARCHIVE_SCREEN -> {

                    }
                }
            }
        }
        OptionsBtmSheetUI(
            btmModalSheetState = btmModalSheetState,
            shouldBtmModalSheetBeVisible = shouldOptionsBtmModalSheetBeVisible,
            coroutineScope = coroutineScope,
            btmSheetFor = OptionsBtmSheetType.LINK,
            onDeleteCardClick = {
                when (SpecificScreenVM.screenType.value) {
                    SpecificScreenType.SPECIFIC_FOLDER_SCREEN -> {
                        coroutineScope.launch {
                            LocalDBFunctions.deleteALinkFromThisFolder(
                                folderName = foldersData.folderName,
                                link = selectedWebURL.value
                            )
                        }
                    }

                    SpecificScreenType.LINKS_SCREEN -> {
                        shouldDeleteDialogBeVisible.value = true
                    }

                    SpecificScreenType.FAVORITES_SCREEN -> {

                    }

                    SpecificScreenType.ARCHIVE_SCREEN -> {

                    }
                }
            },
            {

            }
        )
        DeleteDialogBox(
            shouldDialogBoxAppear = shouldDeleteDialogBeVisible,
            coroutineScope = coroutineScope,
            webURL = selectedWebURL.value,
            folderName = "",
            deleteDialogBoxType = DeleteDialogBoxType.LINK
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