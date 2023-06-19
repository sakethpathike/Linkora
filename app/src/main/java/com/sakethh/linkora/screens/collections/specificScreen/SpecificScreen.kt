package com.sakethh.linkora.screens.collections.specificScreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sakethh.linkora.R
import com.sakethh.linkora.btmSheet.OptionsBtmSheetType
import com.sakethh.linkora.btmSheet.OptionsBtmSheetUI
import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.customWebTab.openInWeb
import com.sakethh.linkora.localDB.LinksTable
import com.sakethh.linkora.localDB.LocalDBFunctions
import com.sakethh.linkora.screens.home.composables.AddNewLinkDialogBox
import com.sakethh.linkora.screens.home.composables.DataDialogBoxType
import com.sakethh.linkora.screens.home.composables.DeleteDialogBox
import com.sakethh.linkora.screens.home.composables.LinkUIComponent
import com.sakethh.linkora.screens.home.composables.RenameDialogBox
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
    val impLinksData = specificScreenVM.impLinksTable.collectAsState().value
    val tempImpLinkData = specificScreenVM.impLinkDataForBtmSheet.copy()
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
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val optionsBtmSheetVM: OptionsBtmSheetVM = viewModel()
    val topBarText = when (SpecificScreenVM.screenType.value) {
        SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
            "Important Links"
        }

        SpecificScreenType.ARCHIVE_SCREEN -> {
            ""
        }

        SpecificScreenType.LINKS_SCREEN -> {
            "Saved Links"
        }

        SpecificScreenType.SPECIFIC_FOLDER_SCREEN -> {
            foldersData.folderName
        }
    }
    val shouldNewLinkDialogBoxBeVisible = rememberSaveable {
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
                        items(foldersData.links) {
                            LinkUIComponent(
                                title = it.title,
                                webBaseURL = it.baseURL,
                                imgURL = "https://i.pinimg.com/originals/73/b2/a8/73b2a8acdc03a65a1c2c8901a9ed1b0b.jpg",
                                onMoreIconCLick = {
                                    selectedWebURL.value = it.webURL
                                    tempImpLinkData.linkData =
                                        it
                                    tempImpLinkData.link =
                                        it.webURL
                                    shouldOptionsBtmModalSheetBeVisible.value = true
                                    coroutineScope.launch {
                                        optionsBtmSheetVM.updateImportantCardData(url = selectedWebURL.value)
                                    }
                                },
                                onLinkClick = {
                                    openInWeb(
                                        url = it.webURL,
                                        context = context,
                                        uriHandler = uriHandler
                                    )
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
                                    imgURL = "https://i.pinimg.com/originals/73/b2/a8/73b2a8acdc03a65a1c2c8901a9ed1b0b.jpg",
                                    onMoreIconCLick = {
                                        selectedWebURL.value = it.webURL
                                        tempImpLinkData.linkData = it
                                        tempImpLinkData.link = it.webURL
                                        shouldOptionsBtmModalSheetBeVisible.value = true
                                        coroutineScope.launch {
                                            optionsBtmSheetVM.updateImportantCardData(url = selectedWebURL.value)
                                        }
                                    },
                                    onLinkClick = {
                                        openInWeb(
                                            url = it.webURL,
                                            context = context,
                                            uriHandler = uriHandler
                                        )
                                    },
                                    webURL = it.webURL
                                )
                            }
                        } else {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Column(
                                        modifier = Modifier.padding(
                                            start = 15.dp,
                                            end = 15.dp
                                        )
                                    ) {
                                        Image(
                                            contentScale = ContentScale.Crop,
                                            painter = painterResource(id = R.drawable.img1),
                                            contentDescription = "you're breathtaking",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .wrapContentSize()
                                        )
                                        Text(
                                            text = "You're Breathtaking, but it's all empty here:)",
                                            color = MaterialTheme.colorScheme.onSurface,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontSize = 20.sp,
                                            lineHeight = 24.sp,
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier.padding(top = 25.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                        if (impLinksData.isNotEmpty()) {
                            items(impLinksData) {
                                LinkUIComponent(
                                    title = it.linkData.title,
                                    webBaseURL = it.linkData.baseURL,
                                    imgURL = "https://i.pinimg.com/originals/73/b2/a8/73b2a8acdc03a65a1c2c8901a9ed1b0b.jpg",
                                    onMoreIconCLick = {
                                        selectedWebURL.value = it.linkData.webURL
                                        tempImpLinkData.linkData =
                                            it.linkData
                                        tempImpLinkData.link =
                                            it.linkData.webURL
                                        shouldOptionsBtmModalSheetBeVisible.value = true
                                        coroutineScope.launch {
                                            optionsBtmSheetVM.updateImportantCardData(url = selectedWebURL.value)
                                        }
                                    },
                                    onLinkClick = {
                                        openInWeb(
                                            url = it.linkData.webURL,
                                            context = context,
                                            uriHandler = uriHandler
                                        )
                                    },
                                    webURL = it.linkData.webURL
                                )
                            }
                        } else {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Column(
                                        modifier = Modifier.padding(
                                            start = 0.dp,
                                            end = 0.dp
                                        )
                                    ) {
                                        Image(
                                            contentScale = ContentScale.Crop,
                                            painter = painterResource(id = R.drawable.img1),
                                            contentDescription = "you're breathtaking",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .wrapContentSize()
                                        )
                                        Text(
                                            text = "You're Breathtaking, but it's all empty here:)",
                                            color = MaterialTheme.colorScheme.onSurface,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontSize = 20.sp,
                                            lineHeight = 24.sp,
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier.padding(top = 25.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    SpecificScreenType.ARCHIVE_SCREEN -> {

                    }
                }
                item {
                    Spacer(modifier = Modifier.height(175.dp))
                }
            }
        }
        OptionsBtmSheetUI(
            btmModalSheetState = btmModalSheetState,
            shouldBtmModalSheetBeVisible = shouldOptionsBtmModalSheetBeVisible,
            coroutineScope = coroutineScope,
            btmSheetFor = when (SpecificScreenVM.screenType.value) {
                SpecificScreenType.IMPORTANT_LINKS_SCREEN -> OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN
                SpecificScreenType.ARCHIVE_SCREEN -> OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN
                SpecificScreenType.LINKS_SCREEN -> OptionsBtmSheetType.LINK
                SpecificScreenType.SPECIFIC_FOLDER_SCREEN -> OptionsBtmSheetType.FOLDER
            },
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

                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {

                    }

                    SpecificScreenType.ARCHIVE_SCREEN -> {

                    }
                }
            },
            onRenameClick = {
                when (SpecificScreenVM.screenType.value) {
                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {

                    }

                    SpecificScreenType.ARCHIVE_SCREEN -> {

                    }

                    SpecificScreenType.LINKS_SCREEN -> {
                        shouldRenameDialogBeVisible.value = true
                    }

                    SpecificScreenType.SPECIFIC_FOLDER_SCREEN -> {

                    }
                }
            },
            importantLinks = tempImpLinkData
        )
        DeleteDialogBox(
            shouldDialogBoxAppear = shouldDeleteDialogBeVisible,
            coroutineScope = coroutineScope,
            webURL = selectedWebURL.value,
            folderName = "",
            deleteDialogBoxType = DataDialogBoxType.LINK
        )
        RenameDialogBox(
            shouldDialogBoxAppear = shouldRenameDialogBeVisible,
            coroutineScope = coroutineScope,
            existingFolderName = "",
            renameDialogBoxFor = OptionsBtmSheetType.LINK,
            webURLForTitle = selectedWebURL.value
        )
        AddNewLinkDialogBox(
            shouldDialogBoxAppear = shouldNewLinkDialogBoxBeVisible,
            onSaveBtnClick = { title: String, webURL: String, note: String ->
                val impLinkData = specificScreenVM.impLinkDataForBtmSheet.copy(
                    link = webURL,
                    linkData = LinksTable(
                        title = title,
                        webURL = webURL,
                        "",
                        "",
                        false,
                        "",
                        "",
                        infoForSaving = note
                    )
                )
                when (SpecificScreenVM.screenType.value) {
                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                        coroutineScope.launch {
                            LocalDBFunctions.importantLinksFunctions(
                                url = webURL,
                                importantLinks = impLinkData
                            )
                        }
                    }

                    SpecificScreenType.ARCHIVE_SCREEN -> {

                    }

                    SpecificScreenType.LINKS_SCREEN -> {
                        coroutineScope.launch {
                            LocalDBFunctions.addANewLink(
                                title = title,
                                webURL = webURL,
                                noteForSaving = note
                            )
                        }
                    }

                    SpecificScreenType.SPECIFIC_FOLDER_SCREEN -> {
                        coroutineScope.launch {
                            LocalDBFunctions.addANewLinkInAFolder(
                                folderName = foldersData.folderName,
                                titleForLink = title,
                                webURLOfLink = webURL,
                                noteForSavingLink = note
                            )
                        }
                    }
                }
            }
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