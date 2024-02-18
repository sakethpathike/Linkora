package com.sakethh.linkora.screens.search

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sakethh.linkora.btmSheet.OptionsBtmSheetType
import com.sakethh.linkora.btmSheet.OptionsBtmSheetUI
import com.sakethh.linkora.btmSheet.OptionsBtmSheetUIParam
import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.btmSheet.SortingBottomSheetUI
import com.sakethh.linkora.btmSheet.SortingBottomSheetUIParam
import com.sakethh.linkora.btmSheet.SortingBtmSheetType
import com.sakethh.linkora.customComposables.DataDialogBoxType
import com.sakethh.linkora.customComposables.DeleteDialogBox
import com.sakethh.linkora.customComposables.DeleteDialogBoxParam
import com.sakethh.linkora.customComposables.LinkUIComponent
import com.sakethh.linkora.customComposables.LinkUIComponentParam
import com.sakethh.linkora.customComposables.RenameDialogBox
import com.sakethh.linkora.customComposables.RenameDialogBoxParam
import com.sakethh.linkora.localDB.dto.RecentlyVisited
import com.sakethh.linkora.navigation.NavigationRoutes
import com.sakethh.linkora.screens.DataEmptyScreen
import com.sakethh.linkora.screens.home.HomeScreenVM
import com.sakethh.linkora.screens.search.SearchScreenVM.Companion.selectedFolderID
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun SearchScreen(navController: NavController) {
    val searchScreenVM: SearchScreenVM = viewModel()
    val isSelectionModeEnabled = rememberSaveable {
        mutableStateOf(false)
    }
    val recentlyVisitedLinksData = searchScreenVM.historyLinksData.collectAsState().value
    val impLinksData = searchScreenVM.impLinksQueriedData.collectAsState().value
    val linksTableData = searchScreenVM.linksTableData.collectAsState().value
    val archiveLinksTableData = searchScreenVM.archiveLinksQueriedData.collectAsState().value

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
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val coroutineScope = rememberCoroutineScope()
    val optionsBtmSheetVM = viewModel<OptionsBtmSheetVM>()
    val searchTextField = searchScreenVM.searchQuery.collectAsState().value
    val sortingBtmSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val optionsBtmSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shouldRenameDialogBoxAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldDeleteDialogBoxAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val selectedLinkTitle = rememberSaveable {
        mutableStateOf("")
    }
    if (!SearchScreenVM.isSearchEnabled.value) {
        searchScreenVM.changeSearchQuery("")
    }
    LinkoraTheme {
        Column {
            SearchBar(
                enabled = !isSelectionModeEnabled.value, interactionSource = interactionSource,
                trailingIcon = {
                    if (SearchScreenVM.isSearchEnabled.value && !isSelectionModeEnabled.value) {
                        IconButton(onClick = {
                            if (searchTextField == "") {
                                SearchScreenVM.focusRequester.freeFocus()
                                SearchScreenVM.isSearchEnabled.value = false
                            } else {
                                searchScreenVM.changeSearchQuery("")
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier
                    .animateContentSize()
                    .padding(
                        top = if (!SearchScreenVM.isSearchEnabled.value) 10.dp else 0.dp,
                        start = if (!SearchScreenVM.isSearchEnabled.value) 10.dp else 0.dp,
                        end = if (!SearchScreenVM.isSearchEnabled.value) 10.dp else 0.dp,
                    )
                    .fillMaxWidth()
                    .then(
                        if (isSelectionModeEnabled.value && !SearchScreenVM.isSearchEnabled.value) Modifier.height(
                            0.dp
                        ) else Modifier.wrapContentHeight()
                    )
                    .focusRequester(SearchScreenVM.focusRequester),
                query = searchTextField,
                onQueryChange = {
                    searchScreenVM.changeSearchQuery(it)
                },
                onSearch = {
                    searchScreenVM.changeSearchQuery(it)
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
                        text = "Search titles to find links",
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                content = {
                    if (isSelectionModeEnabled.value) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = {
                                    isSelectionModeEnabled.value =
                                        false
                                    searchScreenVM.areAllLinksChecked.value =
                                        false
                                    searchScreenVM.areAllFoldersChecked.value =
                                        false
                                    searchScreenVM.selectedImportantLinksData.clear()
                                    searchScreenVM.selectedLinksTableData.clear()
                                    searchScreenVM.selectedArchiveLinksTableData.clear()
                                    searchScreenVM.selectedHistoryLinksData.clear()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Cancel,
                                        contentDescription = null
                                    )
                                }
                                AnimatedContent(
                                    targetState = searchScreenVM.selectedLinksTableData.size +
                                            searchScreenVM.selectedHistoryLinksData.size +
                                            searchScreenVM.selectedArchiveLinksTableData.size +
                                            searchScreenVM.selectedImportantLinksData.size,
                                    label = "",
                                    transitionSpec = {
                                        ContentTransform(
                                            initialContentExit = slideOutVertically(
                                                animationSpec = tween(
                                                    150
                                                )
                                            ) + fadeOut(
                                                tween(150)
                                            ),
                                            targetContentEnter = slideInVertically(
                                                animationSpec = tween(
                                                    durationMillis = 150
                                                )
                                            ) + fadeIn(
                                                tween(150)
                                            )
                                        )
                                    }) {
                                    Text(
                                        text = it.toString(),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontSize = 18.sp
                                    )
                                }
                                Text(
                                    text = " items selected",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontSize = 18.sp
                                )
                            }
                            Row {
                                if (searchScreenVM.selectedArchiveLinksTableData.isEmpty()) {
                                    IconButton(onClick = {
                                        searchScreenVM.archiveSelectedImportantLinks()
                                        searchScreenVM.archiveSelectedLinksTableLinks()
                                        searchScreenVM.archiveSelectedHistoryLinks()
                                        isSelectionModeEnabled.value =
                                            false
                                    }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Archive,
                                            contentDescription = null
                                        )
                                    }
                                }
                                IconButton(onClick = {
                                    shouldDeleteDialogBoxAppear.value = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Outlined.DeleteForever,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        when {
                            searchTextField.isEmpty() -> {
                                item {
                                    DataEmptyScreen(text = "Search Linkora: Retrieve all the links you saved.")
                                }
                            }

                            searchTextField.isNotEmpty() && (linksTableData.isEmpty() && impLinksData.isEmpty() && archiveLinksTableData.isEmpty()) -> {
                                item {
                                    DataEmptyScreen(text = "No Matching Links Found. Try a Different Search.")
                                }
                            }

                            else -> {
                                if (impLinksData.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = "From Important Links",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(15.dp)
                                        )
                                    }
                                }
                                itemsIndexed(items = impLinksData,
                                    key = { index, importantLinks ->
                                        importantLinks.webURL + importantLinks.id.toString() + index
                                    }) { index, it ->
                                    LinkUIComponent(
                                        LinkUIComponentParam(
                                            onLongClick = {
                                                if (!isSelectionModeEnabled.value) {
                                                    isSelectionModeEnabled.value =
                                                        true
                                                    searchScreenVM.selectedImportantLinksData.add(it)
                                                }
                                            },
                                            isSelectionModeEnabled = isSelectionModeEnabled,
                                            title = it.title,
                                            webBaseURL = it.webURL,
                                            imgURL = it.imgURL,
                                            onMoreIconCLick = {
                                                SearchScreenVM.selectedLinkID = it.id
                                                selectedLinkTitle.value = it.title
                                                SearchScreenVM.selectedLinkType =
                                                    SearchScreenVM.SelectedLinkType.IMP_LINKS
                                                HomeScreenVM.tempImpLinkData.webURL = it.webURL
                                                HomeScreenVM.tempImpLinkData.baseURL = it.baseURL
                                                HomeScreenVM.tempImpLinkData.imgURL = it.imgURL
                                                HomeScreenVM.tempImpLinkData.title = it.title
                                                HomeScreenVM.tempImpLinkData.infoForSaving =
                                                    it.infoForSaving
                                                selectedURLNote.value = it.infoForSaving
                                                selectedWebURL.value = it.webURL
                                                shouldOptionsBtmModalSheetBeVisible.value = true
                                                coroutineScope.launch {
                                                    kotlinx.coroutines.awaitAll(async {
                                                        optionsBtmSheetVM.updateArchiveLinkCardData(
                                                            url = it.webURL
                                                        )
                                                    }, async {
                                                        optionsBtmSheetVM.updateImportantCardData(
                                                            url = it.webURL
                                                        )
                                                    })
                                                }
                                            },
                                            onLinkClick = {
                                                if (isSelectionModeEnabled.value) {
                                                    if (!searchScreenVM.selectedImportantLinksData.contains(
                                                            it
                                                        )
                                                    ) {
                                                        searchScreenVM.selectedImportantLinksData.add(
                                                            it
                                                        )
                                                    } else {
                                                        searchScreenVM.selectedImportantLinksData.remove(
                                                            it
                                                        )
                                                    }
                                                } else {
                                                    coroutineScope.launch {
                                                        com.sakethh.linkora.customWebTab.openInWeb(
                                                            recentlyVisitedData = RecentlyVisited(
                                                                title = it.title,
                                                                webURL = it.webURL,
                                                                baseURL = it.baseURL,
                                                                imgURL = it.imgURL,
                                                                infoForSaving = it.infoForSaving
                                                            ),
                                                            context = context,
                                                            uriHandler = uriHandler,
                                                            forceOpenInExternalBrowser = false
                                                        )
                                                    }
                                                }
                                            },
                                            webURL = it.webURL,
                                            onForceOpenInExternalBrowserClicked = {
                                                searchScreenVM.onLinkClick(
                                                    RecentlyVisited(
                                                        title = it.title,
                                                        webURL = it.webURL,
                                                        baseURL = it.baseURL,
                                                        imgURL = it.imgURL,
                                                        infoForSaving = it.infoForSaving
                                                    ),
                                                    context = context,
                                                    uriHandler = uriHandler,
                                                    onTaskCompleted = {},
                                                    forceOpenInExternalBrowser = true
                                                )
                                            },
                                            isItemSelected = mutableStateOf(
                                                searchScreenVM.selectedImportantLinksData.contains(
                                                    it
                                                )
                                            )
                                        )
                                    )
                                }
                                if (linksTableData.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = "From Folders and Saved Links",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(15.dp)
                                        )
                                    }
                                }
                                itemsIndexed(items = linksTableData,
                                    key = { index, linksTable ->
                                        linksTable.id.toString() + linksTable.keyOfLinkedFolder.toString() + linksTable.webURL + +index
                                    }) { index, it ->
                                    LinkUIComponent(
                                        LinkUIComponentParam(
                                            onLongClick = {
                                                if (!isSelectionModeEnabled.value) {
                                                    isSelectionModeEnabled.value =
                                                        true
                                                    searchScreenVM.selectedLinksTableData.add(it)
                                                }
                                            },
                                            isSelectionModeEnabled = isSelectionModeEnabled,
                                            isItemSelected = mutableStateOf(
                                                searchScreenVM.selectedLinksTableData.contains(
                                                    it
                                                )
                                            ),
                                            title = it.title,
                                            webBaseURL = it.webURL,
                                            imgURL = it.imgURL,
                                            onMoreIconCLick = {
                                                SearchScreenVM.selectedLinkID = it.id
                                                selectedLinkTitle.value = it.title
                                                when {
                                                    it.isLinkedWithArchivedFolder -> {
                                                        SearchScreenVM.selectedLinkType =
                                                            SearchScreenVM.SelectedLinkType.ARCHIVE_FOLDER_BASED_LINKS
                                                        selectedFolderID =
                                                            it.keyOfArchiveLinkedFolderV10 ?: 0
                                                    }

                                                    it.isLinkedWithFolders -> {
                                                        SearchScreenVM.selectedLinkType =
                                                            SearchScreenVM.SelectedLinkType.FOLDER_BASED_LINKS
                                                        selectedFolderID =
                                                            it.keyOfLinkedFolderV10 ?: 0
                                                    }

                                                    it.isLinkedWithSavedLinks -> {
                                                        SearchScreenVM.selectedLinkType =
                                                            SearchScreenVM.SelectedLinkType.SAVED_LINKS
                                                    }
                                                }
                                                HomeScreenVM.tempImpLinkData.webURL = it.webURL
                                                HomeScreenVM.tempImpLinkData.baseURL = it.baseURL
                                                HomeScreenVM.tempImpLinkData.imgURL = it.imgURL
                                                HomeScreenVM.tempImpLinkData.title = it.title
                                                HomeScreenVM.tempImpLinkData.infoForSaving =
                                                    it.infoForSaving
                                                selectedURLNote.value = it.infoForSaving
                                                selectedWebURL.value = it.webURL
                                                shouldOptionsBtmModalSheetBeVisible.value = true
                                                coroutineScope.launch {
                                                    kotlinx.coroutines.awaitAll(async {
                                                        optionsBtmSheetVM.updateArchiveLinkCardData(
                                                            url = it.webURL
                                                        )
                                                    }, async {
                                                        optionsBtmSheetVM.updateImportantCardData(
                                                            url = it.webURL
                                                        )
                                                    })
                                                }
                                            },
                                            onLinkClick = {
                                                if (isSelectionModeEnabled.value) {
                                                    if (!searchScreenVM.selectedLinksTableData.contains(
                                                            it
                                                        )
                                                    ) {
                                                        searchScreenVM.selectedLinksTableData.add(
                                                            it
                                                        )
                                                    } else {
                                                        searchScreenVM.selectedLinksTableData.remove(
                                                            it
                                                        )
                                                    }
                                                } else {
                                                    coroutineScope.launch {
                                                        com.sakethh.linkora.customWebTab.openInWeb(
                                                            recentlyVisitedData = RecentlyVisited(
                                                                title = it.title,
                                                                webURL = it.webURL,
                                                                baseURL = it.baseURL,
                                                                imgURL = it.imgURL,
                                                                infoForSaving = it.infoForSaving
                                                            ),
                                                            context = context,
                                                            uriHandler = uriHandler,
                                                            forceOpenInExternalBrowser = false
                                                        )
                                                    }
                                                }
                                            },
                                            webURL = it.webURL,
                                            onForceOpenInExternalBrowserClicked = {
                                                searchScreenVM.onLinkClick(
                                                    RecentlyVisited(
                                                        title = it.title,
                                                        webURL = it.webURL,
                                                        baseURL = it.baseURL,
                                                        imgURL = it.imgURL,
                                                        infoForSaving = it.infoForSaving
                                                    ),
                                                    context = context,
                                                    uriHandler = uriHandler,
                                                    onTaskCompleted = {},
                                                    forceOpenInExternalBrowser = true
                                                )
                                            })
                                    )
                                }
                                item {
                                    if (archiveLinksTableData.isNotEmpty()) {
                                        Text(
                                            text = "From Archived Links",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(15.dp)
                                        )
                                    }
                                }
                                itemsIndexed(items = archiveLinksTableData,
                                    key = { index, archivedLinks ->
                                        archivedLinks.id.toString() + archivedLinks.baseURL
                                        +index
                                    }) { index, it ->
                                    LinkUIComponent(
                                        LinkUIComponentParam(
                                            onLongClick = {
                                                if (!isSelectionModeEnabled.value) {
                                                    isSelectionModeEnabled.value =
                                                        true
                                                    searchScreenVM.selectedArchiveLinksTableData.add(
                                                        it
                                                    )
                                                }
                                            },
                                            isSelectionModeEnabled = isSelectionModeEnabled,
                                            isItemSelected = mutableStateOf(
                                                searchScreenVM.selectedArchiveLinksTableData.contains(
                                                    it
                                                )
                                            ),
                                            title = it.title,
                                            webBaseURL = it.webURL,
                                            imgURL = it.imgURL,
                                            onMoreIconCLick = {
                                                SearchScreenVM.selectedLinkID = it.id
                                                selectedLinkTitle.value = it.title
                                                SearchScreenVM.selectedLinkType =
                                                    SearchScreenVM.SelectedLinkType.ARCHIVE_LINKS
                                                HomeScreenVM.tempImpLinkData.webURL = it.webURL
                                                HomeScreenVM.tempImpLinkData.baseURL = it.baseURL
                                                HomeScreenVM.tempImpLinkData.imgURL = it.imgURL
                                                HomeScreenVM.tempImpLinkData.title = it.title
                                                HomeScreenVM.tempImpLinkData.infoForSaving =
                                                    it.infoForSaving
                                                selectedURLNote.value = it.infoForSaving
                                                selectedWebURL.value = it.webURL
                                                shouldOptionsBtmModalSheetBeVisible.value = true
                                                coroutineScope.launch {
                                                    kotlinx.coroutines.awaitAll(async {
                                                        optionsBtmSheetVM.updateArchiveLinkCardData(
                                                            url = it.webURL
                                                        )
                                                    }, async {
                                                        optionsBtmSheetVM.updateImportantCardData(
                                                            url = it.webURL
                                                        )
                                                    })
                                                }
                                            },
                                            onLinkClick = {
                                                if (isSelectionModeEnabled.value) {

                                                    if (!searchScreenVM.selectedArchiveLinksTableData.contains(
                                                            it
                                                        )
                                                    ) {
                                                        searchScreenVM.selectedArchiveLinksTableData.add(
                                                            it
                                                        )
                                                    } else {
                                                        searchScreenVM.selectedArchiveLinksTableData.remove(
                                                            it
                                                        )
                                                    }
                                                } else {
                                                    coroutineScope.launch {
                                                        com.sakethh.linkora.customWebTab.openInWeb(
                                                            recentlyVisitedData = RecentlyVisited(
                                                                title = it.title,
                                                                webURL = it.webURL,
                                                                baseURL = it.baseURL,
                                                                imgURL = it.imgURL,
                                                                infoForSaving = it.infoForSaving
                                                            ),
                                                            context = context,
                                                            uriHandler = uriHandler,
                                                            forceOpenInExternalBrowser = false
                                                        )
                                                    }
                                                }
                                            },
                                            webURL = it.webURL,
                                            onForceOpenInExternalBrowserClicked = {
                                                searchScreenVM.onLinkClick(
                                                    RecentlyVisited(
                                                        title = it.title,
                                                        webURL = it.webURL,
                                                        baseURL = it.baseURL,
                                                        imgURL = it.imgURL,
                                                        infoForSaving = it.infoForSaving
                                                    ),
                                                    context = context,
                                                    uriHandler = uriHandler,
                                                    onTaskCompleted = {},
                                                    forceOpenInExternalBrowser = true
                                                )
                                            })
                                    )
                                }
                                if (recentlyVisitedLinksData.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = "From History",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(15.dp)
                                        )
                                    }
                                }
                                itemsIndexed(items = recentlyVisitedLinksData,
                                    key = { index, archivedLinks ->
                                        archivedLinks.baseURL + archivedLinks.id.toString() + index
                                    }) { index, it ->
                                    LinkUIComponent(
                                        LinkUIComponentParam(
                                            onLongClick = {
                                                if (!isSelectionModeEnabled.value) {
                                                    isSelectionModeEnabled.value =
                                                        true
                                                    searchScreenVM.selectedHistoryLinksData.add(it)
                                                }
                                            },
                                            isSelectionModeEnabled = isSelectionModeEnabled,
                                            isItemSelected = mutableStateOf(
                                                searchScreenVM.selectedHistoryLinksData.contains(
                                                    it
                                                )
                                            ),
                                            title = it.title,
                                            webBaseURL = it.webURL,
                                            imgURL = it.imgURL,
                                            onMoreIconCLick = {
                                                SearchScreenVM.selectedLinkID = it.id
                                                selectedLinkTitle.value = it.title
                                                SearchScreenVM.selectedLinkType =
                                                    SearchScreenVM.SelectedLinkType.ARCHIVE_LINKS
                                                HomeScreenVM.tempImpLinkData.webURL = it.webURL
                                                HomeScreenVM.tempImpLinkData.baseURL = it.baseURL
                                                HomeScreenVM.tempImpLinkData.imgURL = it.imgURL
                                                HomeScreenVM.tempImpLinkData.title = it.title
                                                HomeScreenVM.tempImpLinkData.infoForSaving =
                                                    it.infoForSaving
                                                selectedURLNote.value = it.infoForSaving
                                                selectedWebURL.value = it.webURL
                                                shouldOptionsBtmModalSheetBeVisible.value = true
                                                coroutineScope.launch {
                                                    kotlinx.coroutines.awaitAll(async {
                                                        optionsBtmSheetVM.updateArchiveLinkCardData(
                                                            url = it.webURL
                                                        )
                                                    }, async {
                                                        optionsBtmSheetVM.updateImportantCardData(
                                                            url = it.webURL
                                                        )
                                                    })
                                                }
                                            },
                                            onLinkClick = {
                                                if (isSelectionModeEnabled.value) {
                                                    if (!searchScreenVM.selectedHistoryLinksData.contains(
                                                            it
                                                        )
                                                    ) {
                                                        searchScreenVM.selectedHistoryLinksData.add(
                                                            it
                                                        )
                                                    } else {
                                                        searchScreenVM.selectedHistoryLinksData.remove(
                                                            it
                                                        )
                                                    }
                                                } else {
                                                    coroutineScope.launch {
                                                        com.sakethh.linkora.customWebTab.openInWeb(
                                                            recentlyVisitedData = RecentlyVisited(
                                                                title = it.title,
                                                                webURL = it.webURL,
                                                                baseURL = it.baseURL,
                                                                imgURL = it.imgURL,
                                                                infoForSaving = it.infoForSaving
                                                            ),
                                                            context = context,
                                                            uriHandler = uriHandler,
                                                            forceOpenInExternalBrowser = false
                                                        )
                                                    }
                                                }
                                            },
                                            webURL = it.webURL,
                                            onForceOpenInExternalBrowserClicked = {
                                                searchScreenVM.onLinkClick(
                                                    RecentlyVisited(
                                                        title = it.title,
                                                        webURL = it.webURL,
                                                        baseURL = it.baseURL,
                                                        imgURL = it.imgURL,
                                                        infoForSaving = it.infoForSaving
                                                    ),
                                                    context = context,
                                                    uriHandler = uriHandler,
                                                    onTaskCompleted = {},
                                                    forceOpenInExternalBrowser = true
                                                )
                                            })
                                    )
                                }
                                item {
                                    Spacer(modifier = Modifier.height(225.dp))
                                }
                            }
                        }
                    }
                })
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                stickyHeader {
                    Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                        Row(modifier = Modifier
                            .clickable {
                                if (recentlyVisitedLinksData.isNotEmpty() && !isSelectionModeEnabled.value) {
                                    shouldSortingBottomSheetAppear.value = true
                                }
                            }
                            .fillMaxWidth()
                            .wrapContentHeight(),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                            if (isSelectionModeEnabled.value) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = {
                                        isSelectionModeEnabled.value =
                                            false
                                        searchScreenVM.areAllLinksChecked.value =
                                            false
                                        searchScreenVM.areAllFoldersChecked.value =
                                            false
                                        searchScreenVM.selectedImportantLinksData.clear()
                                        searchScreenVM.selectedLinksTableData.clear()
                                        searchScreenVM.selectedArchiveLinksTableData.clear()
                                        searchScreenVM.selectedHistoryLinksData.clear()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = null
                                        )
                                    }
                                    AnimatedContent(
                                        targetState = searchScreenVM.selectedHistoryLinksData.size + searchScreenVM.selectedFoldersData.size,
                                        label = "",
                                        transitionSpec = {
                                            ContentTransform(
                                                initialContentExit = slideOutVertically(
                                                    animationSpec = tween(
                                                        150
                                                    )
                                                ) + fadeOut(
                                                    tween(150)
                                                ), targetContentEnter = slideInVertically(
                                                    animationSpec = tween(durationMillis = 150)
                                                ) + fadeIn(
                                                    tween(150)
                                                )
                                            )
                                        }) {
                                        Text(
                                            text = it.toString(),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontSize = 18.sp
                                        )
                                    }
                                    Text(
                                        text = " items selected",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontSize = 18.sp
                                    )
                                }
                            } else {
                                Text(
                                    text = "History",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(
                                        start = 15.dp,
                                        top = if (recentlyVisitedLinksData.isNotEmpty()) 0.dp else 11.dp
                                    )
                                )
                            }
                            if (recentlyVisitedLinksData.isNotEmpty() && !isSelectionModeEnabled.value) {
                                IconButton(onClick = {
                                    shouldSortingBottomSheetAppear.value = true
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.Sort,
                                        contentDescription = null
                                    )
                                }
                            } else if (isSelectionModeEnabled.value) {
                                Row {
                                    IconButton(onClick = {
                                        isSelectionModeEnabled.value =
                                            false
                                        searchScreenVM.archiveSelectedHistoryLinks()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Archive,
                                            contentDescription = null
                                        )
                                    }
                                    IconButton(onClick = {
                                        shouldDeleteDialogBoxAppear.value = true
                                    }) {
                                        Icon(
                                            imageVector = Icons.Outlined.DeleteForever,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }
                if (recentlyVisitedLinksData.isNotEmpty()) {
                    itemsIndexed(items = recentlyVisitedLinksData,
                        key = { index, recentlyVisited ->
                            recentlyVisited.baseURL + recentlyVisited.webURL + recentlyVisited.id.toString() + index
                        }) { index, it ->
                        LinkUIComponent(
                            LinkUIComponentParam(
                                onLongClick = {
                                    if (!isSelectionModeEnabled.value) {
                                        isSelectionModeEnabled.value =
                                            true
                                        searchScreenVM.selectedHistoryLinksData.add(it)
                                    }
                                },
                                isSelectionModeEnabled = isSelectionModeEnabled,
                                title = it.title,
                                webBaseURL = it.baseURL,
                                imgURL = it.imgURL,
                                onMoreIconCLick = {
                                    SearchScreenVM.selectedLinkID = it.id
                                    selectedLinkTitle.value = it.title
                                    SearchScreenVM.selectedLinkType =
                                        SearchScreenVM.SelectedLinkType.HISTORY_LINKS
                                    HomeScreenVM.tempImpLinkData.webURL = it.webURL
                                    HomeScreenVM.tempImpLinkData.baseURL = it.baseURL
                                    HomeScreenVM.tempImpLinkData.imgURL = it.imgURL
                                    HomeScreenVM.tempImpLinkData.title = it.title
                                    HomeScreenVM.tempImpLinkData.infoForSaving = it.infoForSaving
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
                                    if (!isSelectionModeEnabled.value) {
                                        coroutineScope.launch {
                                            com.sakethh.linkora.customWebTab.openInWeb(
                                                recentlyVisitedData = RecentlyVisited(
                                                    title = it.title,
                                                    webURL = it.webURL,
                                                    baseURL = it.baseURL,
                                                    imgURL = it.imgURL,
                                                    infoForSaving = it.infoForSaving
                                                ),
                                                context = context,
                                                uriHandler = uriHandler,
                                                forceOpenInExternalBrowser = false
                                            )
                                        }
                                    } else {
                                        if (!searchScreenVM.selectedHistoryLinksData.contains(it)) {
                                            searchScreenVM.selectedHistoryLinksData.add(it)
                                        } else {
                                            searchScreenVM.selectedHistoryLinksData.remove(it)
                                        }
                                    }
                                },
                                webURL = it.webURL,
                                onForceOpenInExternalBrowserClicked = {
                                    searchScreenVM.onLinkClick(
                                        RecentlyVisited(
                                            title = it.title,
                                            webURL = it.webURL,
                                            baseURL = it.baseURL,
                                            imgURL = it.imgURL,
                                            infoForSaving = it.infoForSaving
                                        ),
                                        context = context,
                                        uriHandler = uriHandler,
                                        onTaskCompleted = {},
                                        forceOpenInExternalBrowser = true
                                    )
                                },
                                isItemSelected = mutableStateOf(
                                    searchScreenVM.selectedHistoryLinksData.contains(
                                        it
                                    )
                                )
                            )
                        )
                    }
                } else {
                    item {
                        DataEmptyScreen(text = "No Links were found in History.")
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(225.dp))
                }
            }
        }
        SortingBottomSheetUI(
            SortingBottomSheetUIParam(
                shouldBottomSheetVisible = shouldSortingBottomSheetAppear,
                onSelectedAComponent = { sortingPreferences, _, _ ->
                    searchScreenVM.changeHistoryRetrievedData(sortingPreferences = sortingPreferences)
                },
                bottomModalSheetState = sortingBtmSheetState,
                sortingBtmSheetType = SortingBtmSheetType.HISTORY_SCREEN,
                shouldFoldersSelectionBeVisible = mutableStateOf(false),
                shouldLinksSelectionBeVisible = mutableStateOf(false)
            )
        )
        OptionsBtmSheetUI(
            OptionsBtmSheetUIParam(
                btmModalSheetState = optionsBtmSheetState,
                shouldBtmModalSheetBeVisible = shouldOptionsBtmModalSheetBeVisible,
                btmSheetFor = OptionsBtmSheetType.LINK,
                onDeleteCardClick = {
                    shouldDeleteDialogBoxAppear.value = true
                },
                onNoteDeleteCardClick = {
                    searchScreenVM.onNoteDeleteCardClick(
                        context = context,
                        selectedWebURL = selectedWebURL.value,
                        selectedLinkType = SearchScreenVM.selectedLinkType,
                        folderID = selectedFolderID
                    )
                },
                onRenameClick = {
                    shouldRenameDialogBoxAppear.value = true
                },
                onArchiveClick = {
                    searchScreenVM.onArchiveClick(
                        context,
                        selectedLinkType = SearchScreenVM.selectedLinkType,
                        folderID = selectedFolderID
                    )
                },
                importantLinks = HomeScreenVM.tempImpLinkData,
                noteForSaving = selectedURLNote.value,
                folderName = "",
                linkTitle = selectedLinkTitle.value
            )
        )
        RenameDialogBox(
            RenameDialogBoxParam(
                shouldDialogBoxAppear = shouldRenameDialogBoxAppear,
                existingFolderName = "",
                onNoteChangeClick = { newNote ->
                    searchScreenVM.onNoteChangeClickForLinks(
                        HomeScreenVM.tempImpLinkData.webURL,
                        newNote,
                        selectedLinkType = SearchScreenVM.selectedLinkType,
                        folderID = selectedFolderID, linkID = SearchScreenVM.selectedLinkID
                    )
                    shouldRenameDialogBoxAppear.value = false
                },
                renameDialogBoxFor = OptionsBtmSheetType.LINK,
                onTitleChangeClick = { newTitle ->
                    searchScreenVM.onTitleChangeClickForLinks(
                        HomeScreenVM.tempImpLinkData.webURL,
                        newTitle,
                        selectedLinkType = SearchScreenVM.selectedLinkType,
                        folderID = selectedFolderID, linkID = SearchScreenVM.selectedLinkID
                    )
                    shouldRenameDialogBoxAppear.value = false
                }
            )
        )
        DeleteDialogBox(
            DeleteDialogBoxParam(
                areFoldersSelectable = isSelectionModeEnabled.value,
                shouldDialogBoxAppear = shouldDeleteDialogBoxAppear,
                deleteDialogBoxType = if (isSelectionModeEnabled.value) DataDialogBoxType.SELECTED_DATA else DataDialogBoxType.LINK,
                onDeleteClick = {
                    if (!isSelectionModeEnabled.value) {
                        searchScreenVM.onDeleteClick(
                            context = context,
                            selectedWebURL = selectedWebURL.value,
                            shouldDeleteBoxAppear = shouldDeleteDialogBoxAppear,
                            selectedLinkType = SearchScreenVM.selectedLinkType,
                            folderID = selectedFolderID
                        )
                    } else {
                        searchScreenVM.deleteSelectedHistoryLinks()
                        searchScreenVM.deleteSelectedArchivedLinks()
                        searchScreenVM.deleteSelectedImpLinksData()
                        searchScreenVM.deleteSelectedLinksTableData()
                        searchScreenVM.selectedImportantLinksData.clear()
                        searchScreenVM.selectedLinksTableData.clear()
                        searchScreenVM.selectedArchiveLinksTableData.clear()
                        searchScreenVM.selectedHistoryLinksData.clear()
                        isSelectionModeEnabled.value = false
                    }
                })
        )
    }
    val activity = LocalContext.current as? Activity
    BackHandler {
        when {
            SearchScreenVM.isSearchEnabled.value -> {
                SearchScreenVM.isSearchEnabled.value = false
            }

            else -> if (isSelectionModeEnabled.value) {
                isSelectionModeEnabled.value = false
                searchScreenVM.areAllLinksChecked.value = false
                searchScreenVM.areAllFoldersChecked.value = false
                searchScreenVM.selectedImportantLinksData.clear()
                searchScreenVM.selectedLinksTableData.clear()
                searchScreenVM.selectedArchiveLinksTableData.clear()
                searchScreenVM.selectedHistoryLinksData.clear()
            } else if (SettingsScreenVM.Settings.isHomeScreenEnabled.value) {
                navController.navigate(NavigationRoutes.HOME_SCREEN.name) {
                    popUpTo(0)
                }
            } else {
                activity?.finish()
            }
        }
    }
}