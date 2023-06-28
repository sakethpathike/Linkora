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
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.localDB.ImportantLinks
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
                                    imgURL = it.imgURL,
                                    onMoreIconCLick = {
                                        selectedWebURL.value = it.webURL
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
                                    title = it.title,
                                    webBaseURL = it.baseURL,
                                    imgURL = it.imgURL,
                                    onMoreIconCLick = {
                                        selectedWebURL.value = it.webURL
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
            importantLinks = tempImpLinkData
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
            webURLForTitle = selectedWebURL.value
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