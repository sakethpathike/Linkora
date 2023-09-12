package com.sakethh.linkora.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.sakethh.linkora.btmSheet.SortingBottomSheetUI
import com.sakethh.linkora.customComposables.DataDialogBoxType
import com.sakethh.linkora.customComposables.DeleteDialogBox
import com.sakethh.linkora.customComposables.LinkUIComponent
import com.sakethh.linkora.customComposables.RenameDialogBox
import com.sakethh.linkora.screens.home.HomeScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    val searchScreenVM: SearchScreenVM = viewModel()
    val recentlyVisitedLinksData = searchScreenVM.historyLinksData.collectAsState().value
    val impLinksData = searchScreenVM.impLinksQueriedData.collectAsState().value
    val linksTableData = searchScreenVM.linksTableData.collectAsState().value
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val shouldSortingBottomSheetAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldOptionsBtmModalSheetBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val selectedWebURL = rememberSaveable {
        mutableStateOf("")
    }
    val selectedURLNote = rememberSaveable {
        mutableStateOf("")
    }
    val coroutineScope = rememberCoroutineScope()
    val optionsBtmSheetVM = viewModel<OptionsBtmSheetVM>()
    val query = rememberSaveable {
        mutableStateOf("")
    }
    val sortingBtmSheetState = rememberModalBottomSheetState()
    val optionsBtmSheetState = rememberModalBottomSheetState()
    val shouldRenameDialogBoxAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldDeleteDialogBoxAppear = rememberSaveable {
        mutableStateOf(false)
    }
    LinkoraTheme {
        Column {
            SearchBar(
                modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp),
                query = query.value,
                onQueryChange = {
                    query.value = it
                    searchScreenVM.retrieveSearchQueryData(query = it)
                },
                onSearch = {

                },
                active = SearchScreenVM.isSearchEnabled.value,
                onActiveChange = {
                    SearchScreenVM.isSearchEnabled.value = !SearchScreenVM.isSearchEnabled.value
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                placeholder = {
                    Text(
                        text = "Search",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                content = {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        items(impLinksData) {
                            LinkUIComponent(
                                title = it.title,
                                webBaseURL = it.webURL,
                                imgURL = it.imgURL,
                                onMoreIconCLick = { },
                                onLinkClick = { },
                                webURL = it.webURL,
                                onForceOpenInExternalBrowserClicked = {}
                            )
                        }
                        items(linksTableData) {
                            LinkUIComponent(
                                title = it.title,
                                webBaseURL = it.webURL,
                                imgURL = it.imgURL,
                                onMoreIconCLick = { },
                                onLinkClick = { },
                                webURL = it.webURL,
                                onForceOpenInExternalBrowserClicked = {}
                            )
                        }
                    }
                })
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (recentlyVisitedLinksData.isNotEmpty()) {
                    item {
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(5.dp))
                    }
                    item {
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier
                                .clickable {
                                    shouldSortingBottomSheetAppear.value = true
                                }
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text(
                                text = "History",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(start = 15.dp)
                            )
                            androidx.compose.material3.IconButton(onClick = {
                                shouldSortingBottomSheetAppear.value = true
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.Sort,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                    item {
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(5.dp))
                    }
                    items(recentlyVisitedLinksData) {
                        LinkUIComponent(
                            title = it.title,
                            webBaseURL = it.baseURL,
                            imgURL = it.imgURL,
                            onMoreIconCLick = {
                                HomeScreenVM.tempImpLinkData.webURL =
                                    it.webURL
                                HomeScreenVM.tempImpLinkData.baseURL =
                                    it.baseURL
                                HomeScreenVM.tempImpLinkData.imgURL =
                                    it.imgURL
                                HomeScreenVM.tempImpLinkData.title =
                                    it.title
                                HomeScreenVM.tempImpLinkData.infoForSaving =
                                    it.infoForSaving
                                selectedURLNote.value = it.infoForSaving
                                selectedWebURL.value = it.webURL
                                shouldOptionsBtmModalSheetBeVisible.value = true
                                coroutineScope.launch {
                                    kotlinx.coroutines.awaitAll(async {
                                        optionsBtmSheetVM.updateArchiveLinkCardData(url = it.webURL)
                                    }, async {
                                        optionsBtmSheetVM.updateImportantCardData(url = it.webURL)
                                    })
                                }
                            },
                            onLinkClick = {
                                coroutineScope.launch {
                                    com.sakethh.linkora.customWebTab.openInWeb(
                                        recentlyVisitedData = com.sakethh.linkora.localDB.RecentlyVisited(
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
                            webURL = it.webURL,
                            onForceOpenInExternalBrowserClicked = {
                                searchScreenVM.onForceOpenInExternalBrowser(
                                    com.sakethh.linkora.localDB.RecentlyVisited(
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
                }
            }
        }
        SortingBottomSheetUI(
            shouldBottomSheetVisible = shouldSortingBottomSheetAppear,
            onSelectedAComponent = {
                searchScreenVM.changeHistoryRetrievedData(sortingPreferences = it)
            },
            bottomModalSheetState = sortingBtmSheetState
        )
        OptionsBtmSheetUI(
            btmModalSheetState = optionsBtmSheetState,
            shouldBtmModalSheetBeVisible = shouldOptionsBtmModalSheetBeVisible,
            coroutineScope = coroutineScope,
            btmSheetFor = OptionsBtmSheetType.LINK,
            onDeleteCardClick = {
                shouldDeleteDialogBoxAppear.value = true
            },
            onNoteDeleteCardClick = {
                searchScreenVM.onNoteDeleteCardClick(
                    context = context,
                    selectedWebURL = selectedWebURL.value
                )
            },
            onRenameClick = {
                shouldRenameDialogBoxAppear.value = true
            },
            onArchiveClick = {
                searchScreenVM.onArchiveClick(context)
            },
            importantLinks = HomeScreenVM.tempImpLinkData,
            noteForSaving = HomeScreenVM.tempImpLinkData.infoForSaving,
            folderName = "",
            linkTitle = HomeScreenVM.tempImpLinkData.title
        )
        RenameDialogBox(
            shouldDialogBoxAppear = shouldDeleteDialogBoxAppear,
            coroutineScope = coroutineScope,
            existingFolderName = "",
            onNoteChangeClickForLinks = { webURL, newNote ->
                searchScreenVM.onNoteChangeClickForLinks(webURL, newNote)
            },
            renameDialogBoxFor = OptionsBtmSheetType.LINK,
            onTitleChangeClickForLinks = { webURL, newTitle ->
                searchScreenVM.onTitleChangeClickForLinks(webURL, newTitle)
            }, onTitleRenamed = {}
        )
        DeleteDialogBox(
            shouldDialogBoxAppear = shouldDeleteDialogBoxAppear,
            deleteDialogBoxType = DataDialogBoxType.LINK,
            onDeleteClick = {
                searchScreenVM.onDeleteClick(
                    context = context,
                    selectedWebURL = selectedWebURL.value,
                    shouldDeleteBoxAppear = shouldDeleteDialogBoxAppear
                )
            })
    }
}