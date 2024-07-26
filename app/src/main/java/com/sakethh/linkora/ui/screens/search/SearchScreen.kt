package com.sakethh.linkora.ui.screens.search

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
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.ui.bottomSheets.menu.MenuBtmSheetParam
import com.sakethh.linkora.ui.bottomSheets.menu.MenuBtmSheetUI
import com.sakethh.linkora.ui.bottomSheets.sorting.SortingBottomSheetParam
import com.sakethh.linkora.ui.bottomSheets.sorting.SortingBottomSheetUI
import com.sakethh.linkora.ui.bottomSheets.sorting.SortingBtmSheetType
import com.sakethh.linkora.ui.commonComposables.DataDialogBoxType
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBox
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.LinkUIComponent
import com.sakethh.linkora.ui.commonComposables.LinkUIComponentParam
import com.sakethh.linkora.ui.commonComposables.RenameDialogBox
import com.sakethh.linkora.ui.commonComposables.RenameDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.OptionsBtmSheetType
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.OptionsBtmSheetVM
import com.sakethh.linkora.ui.navigation.NavigationRoutes
import com.sakethh.linkora.ui.screens.CustomWebTab
import com.sakethh.linkora.ui.screens.DataEmptyScreen
import com.sakethh.linkora.ui.screens.collections.CollectionsScreenVM
import com.sakethh.linkora.ui.screens.collections.FolderIndividualComponent
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenUIEvent
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenVM
import com.sakethh.linkora.ui.screens.collections.specific.SpecificScreenType
import com.sakethh.linkora.ui.screens.home.HomeScreenVM
import com.sakethh.linkora.ui.screens.search.SearchScreenVM.Companion.selectedFolderID
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.UUID

@SuppressLint("UnrememberedMutableState")
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class, ExperimentalMaterialApi::class
)
@Composable
fun SearchScreen(navController: NavController, customWebTab: CustomWebTab) {
    val searchScreenVM: SearchScreenVM = viewModel()
    val isSelectionModeEnabled = rememberSaveable {
        mutableStateOf(false)
    }
    val queriedUnarchivedFoldersData =
        searchScreenVM.queriedUnarchivedFoldersData.collectAsStateWithLifecycle().value
    val queriedArchivedFoldersData =
        searchScreenVM.queriedArchivedFoldersData.collectAsStateWithLifecycle().value
    val queriedSavedLinks = searchScreenVM.queriedSavedLinks.collectAsStateWithLifecycle().value
    val queriedFolderLinks = searchScreenVM.queriedFolderLinks.collectAsStateWithLifecycle().value
    val impLinksQueriedData = searchScreenVM.impLinksQueriedData.collectAsStateWithLifecycle().value
    val archiveLinksQueriedData =
        searchScreenVM.archiveLinksQueriedData.collectAsStateWithLifecycle().value
    val historyLinksQueriedData =
        searchScreenVM.historyLinksQueriedData.collectAsStateWithLifecycle().value

    val historyLinksData = searchScreenVM.historyLinksData.collectAsStateWithLifecycle().value
    val selectedSearchFilters = searchScreenVM.selectedSearchFilters
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
    val searchTextField = searchScreenVM.searchQuery.collectAsStateWithLifecycle().value
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
    val clickedFolderName = rememberSaveable { mutableStateOf("") }
    val clickedFolderNote = rememberSaveable { mutableStateOf("") }
    LinkoraTheme {
        Column {
            SearchBar(
                enabled = !isSelectionModeEnabled.value, interactionSource = interactionSource,
                trailingIcon = {
                    if (SearchScreenVM.isSearchEnabled.value && !isSelectionModeEnabled.value) {
                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
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
                        if (!isSelectionModeEnabled.value && !SearchScreenVM.isSearchEnabled.value) 15.dp else 0.dp
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
                        text = "Search titles to find links and folders",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.basicMarquee(),
                        maxLines = 1
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
                                IconButton(modifier = Modifier.pulsateEffect(), onClick = {
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
                                    searchScreenVM.selectedFoldersData.clear()
                                    SearchScreenVM.selectedArchiveFoldersData.clear()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Cancel,
                                        contentDescription = null
                                    )
                                }
                                AnimatedContent(
                                    targetState = searchScreenVM.selectedLinksTableData.size +
                                            searchScreenVM.selectedHistoryLinksData.size +
                                            searchScreenVM.selectedArchiveLinksTableData.size + SearchScreenVM.selectedArchiveFoldersData.size +
                                            searchScreenVM.selectedImportantLinksData.size + searchScreenVM.selectedFoldersData.size,
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
                                if (searchScreenVM.selectedArchiveLinksTableData.isEmpty() && SearchScreenVM.selectedArchiveFoldersData.isEmpty()) {
                                    IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                        searchScreenVM.archiveSelectedImportantLinks()
                                        searchScreenVM.archiveSelectedLinksTableLinks()
                                        searchScreenVM.archiveSelectedHistoryLinks()
                                        searchScreenVM.archiveSelectedMultipleFolders()
                                        searchScreenVM.selectedFoldersData.clear()
                                        searchScreenVM.selectedImportantLinksData.clear()
                                        searchScreenVM.selectedLinksTableData.clear()
                                        searchScreenVM.selectedArchiveLinksTableData.clear()
                                        searchScreenVM.selectedHistoryLinksData.clear()
                                        isSelectionModeEnabled.value =
                                            false
                                        SearchScreenVM.isSearchEnabled.value = false
                                    }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Archive,
                                            contentDescription = null
                                        )
                                    }
                                }
                                IconButton(modifier = Modifier.pulsateEffect(), onClick = {
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
                        modifier = Modifier
                            .fillMaxSize()
                            .background(SearchBarDefaults.colors().containerColor)
                    ) {
                        if (searchScreenVM.searchQuery.value.isNotEmpty()) {
                            stickyHeader {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .background(SearchBarDefaults.colors().containerColor)
                                        .horizontalScroll(rememberScrollState())
                                        .padding(top = 2.dp)
                                ) {
                                    listOf(
                                        "Saved Links",
                                        "Important Links",
                                        "Archived Links",
                                        "Folders",
                                        "Archived Folders",
                                        "Links from folders",
                                        "History"
                                    ).forEach {
                                        if (it == "Saved Links" && queriedSavedLinks.isNotEmpty() || it == "Important Links" && impLinksQueriedData.isNotEmpty() ||
                                            it == "Archived Links" && archiveLinksQueriedData.isNotEmpty() || it == "Folders" && queriedUnarchivedFoldersData.isNotEmpty()
                                            || it == "Archived Folders" && queriedArchivedFoldersData.isNotEmpty() || it == "Links from folders" && queriedFolderLinks.isNotEmpty()
                                            || it == "History" && historyLinksQueriedData.isNotEmpty()
                                        ) {
                                            Row(modifier = Modifier.animateContentSize()) {
                                                Spacer(modifier = Modifier.width(10.dp))
                                                androidx.compose.material3.FilterChip(
                                                    selected = selectedSearchFilters.contains(it),
                                                    onClick = {
                                                        if (selectedSearchFilters.contains(it)) {
                                                            selectedSearchFilters.remove(it)
                                                        } else {
                                                            selectedSearchFilters.add(it)
                                                        }
                                                    },
                                                    label = {
                                                        Text(
                                                            text = it,
                                                            style = MaterialTheme.typography.titleSmall
                                                        )
                                                    }, leadingIcon = {
                                                        if (selectedSearchFilters.contains(it)) {
                                                            Icon(
                                                                imageVector = Icons.Default.Check,
                                                                contentDescription = null
                                                            )
                                                        }
                                                    })
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                }
                            }
                        }
                        when {
                            searchTextField.isEmpty() -> {
                                item {
                                    DataEmptyScreen(text = "Search Linkora: Retrieve all the links you saved.")
                                }
                            }

                            searchTextField.isNotEmpty() &&
                                    queriedUnarchivedFoldersData.isEmpty()
                                    && queriedArchivedFoldersData.isEmpty()
                                    && queriedSavedLinks.isEmpty()
                                    && queriedFolderLinks.isEmpty()
                                    && impLinksQueriedData.isEmpty()
                                    && archiveLinksQueriedData.isEmpty()
                                    && historyLinksQueriedData.isEmpty() -> {
                                item {
                                    DataEmptyScreen(text = "No Matching items Found. Try a Different Search.")
                                }
                            }

                            else -> {
                                if (queriedUnarchivedFoldersData.isNotEmpty() && (selectedSearchFilters.contains(
                                        "Folders"
                                    ) || selectedSearchFilters.isEmpty())
                                ) {
                                    item {
                                        Text(
                                            text = "From Folders",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(15.dp)
                                        )
                                    }
                                    itemsIndexed(
                                        items = queriedUnarchivedFoldersData,
                                        key = { index, folderData ->
                                            folderData.folderName + folderData.id.toString() + index
                                        }) { index, folderData ->
                                        FolderIndividualComponent(
                                            showMoreIcon = !isSelectionModeEnabled.value,
                                            folderName = folderData.folderName,
                                            folderNote = folderData.infoForSaving,
                                            onMoreIconClick = {
                                                SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                                    OptionsBtmSheetType.FOLDER
                                                CollectionsScreenVM.selectedFolderData.value =
                                                    folderData
                                                clickedFolderNote.value = folderData.infoForSaving
                                                coroutineScope.launch {
                                                    optionsBtmSheetVM.updateArchiveFolderCardData(
                                                        folderData.id
                                                    )
                                                }
                                                clickedFolderName.value = folderData.folderName
                                                shouldOptionsBtmModalSheetBeVisible.value = true
                                            },
                                            onFolderClick = {
                                                if (!isSelectionModeEnabled.value) {
                                                    SpecificCollectionsScreenVM.inARegularFolder.value =
                                                        true
                                                    SpecificCollectionsScreenVM.screenType.value =
                                                        SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                                                    CollectionsScreenVM.currentClickedFolderData.value =
                                                        folderData
                                                    CollectionsScreenVM.rootFolderID = folderData.id
                                                    navController.navigate(NavigationRoutes.SPECIFIC_COLLECTION_SCREEN.name)
                                                }
                                            },
                                            onLongClick = {
                                                if (!isSelectionModeEnabled.value) {
                                                    isSelectionModeEnabled.value = true
                                                    searchScreenVM.areAllFoldersChecked.value =
                                                        false
                                                    searchScreenVM.changeAllFoldersSelectedData()
                                                    searchScreenVM.selectedFoldersData.add(
                                                        folderData
                                                    )
                                                }
                                            },
                                            showCheckBox = isSelectionModeEnabled,
                                            isCheckBoxChecked = mutableStateOf(
                                                searchScreenVM.selectedFoldersData.contains(
                                                    folderData
                                                )
                                            ),
                                            checkBoxState = { checkBoxState ->
                                                if (checkBoxState) {
                                                    searchScreenVM.selectedFoldersData.add(
                                                        folderData
                                                    )
                                                } else {
                                                    searchScreenVM.selectedFoldersData.removeAll {
                                                        it == folderData
                                                    }
                                                }
                                            })
                                    }
                                }
                                if (queriedSavedLinks.isNotEmpty() && (selectedSearchFilters.contains(
                                        "Saved Links"
                                    ) || selectedSearchFilters.isEmpty())
                                ) {
                                    item {
                                        Text(
                                            text = "From Saved Links",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(15.dp)
                                        )
                                    }
                                    itemsIndexed(items = queriedSavedLinks,
                                        key = { index, linksTable ->
                                            linksTable.id.toString() + linksTable.keyOfLinkedFolder.toString() + UUID.randomUUID()
                                                .toString()
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
                                                    SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                                        OptionsBtmSheetType.LINK
                                                    SearchScreenVM.selectedLinkID = it.id
                                                    selectedLinkTitle.value = it.title
                                                    SearchScreenVM.selectedLinkType =
                                                        SearchScreenVM.SelectedLinkType.SAVED_LINKS
                                                    HomeScreenVM.tempImpLinkData.webURL = it.webURL
                                                    HomeScreenVM.tempImpLinkData.baseURL =
                                                        it.baseURL
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
                                                        customWebTab.openInWeb(
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
                                }
                                if (impLinksQueriedData.isNotEmpty() && (selectedSearchFilters.contains(
                                        "Important Links"
                                    ) || selectedSearchFilters.isEmpty())
                                ) {
                                    item {
                                        Text(
                                            text = "From Important Links",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(15.dp)
                                        )
                                    }
                                    itemsIndexed(items = impLinksQueriedData,
                                        key = { index, impLink ->
                                            impLink.id.toString() + impLink.baseURL + index.toString() + UUID.randomUUID()
                                                .toString()
                                        }) { index, it ->
                                        LinkUIComponent(
                                            LinkUIComponentParam(
                                                onLongClick = {
                                                    if (!isSelectionModeEnabled.value) {
                                                        isSelectionModeEnabled.value =
                                                            true
                                                        searchScreenVM.selectedImportantLinksData.add(
                                                            it
                                                        )
                                                    }
                                                },
                                                isSelectionModeEnabled = isSelectionModeEnabled,
                                                isItemSelected = mutableStateOf(
                                                    searchScreenVM.selectedImportantLinksData.contains(
                                                        it
                                                    )
                                                ),
                                                title = it.title,
                                                webBaseURL = it.webURL,
                                                imgURL = it.imgURL,
                                                onMoreIconCLick = {
                                                    SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                                        OptionsBtmSheetType.LINK
                                                    SearchScreenVM.selectedLinkID = it.id
                                                    selectedLinkTitle.value = it.title
                                                    SearchScreenVM.selectedLinkType =
                                                        SearchScreenVM.SelectedLinkType.IMP_LINKS
                                                    HomeScreenVM.tempImpLinkData.webURL = it.webURL
                                                    HomeScreenVM.tempImpLinkData.baseURL =
                                                        it.baseURL
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
                                                        customWebTab.openInWeb(
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
                                }
                                if (queriedFolderLinks.isNotEmpty() && (selectedSearchFilters.contains(
                                        "Links from folders"
                                    ) || selectedSearchFilters.isEmpty())
                                ) {
                                    item {
                                        Text(
                                            text = "Links from folders",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(15.dp)
                                        )
                                    }
                                    itemsIndexed(items = queriedFolderLinks,
                                        key = { index, folderLink ->
                                            folderLink.baseURL + folderLink.id.toString() + UUID.randomUUID()
                                                .toString()
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
                                                    SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                                        OptionsBtmSheetType.LINK
                                                    SearchScreenVM.selectedLinkID = it.id
                                                    selectedLinkTitle.value = it.title
                                                    SearchScreenVM.selectedLinkType =
                                                        SearchScreenVM.SelectedLinkType.FOLDER_BASED_LINKS
                                                    HomeScreenVM.tempImpLinkData.webURL = it.webURL
                                                    HomeScreenVM.tempImpLinkData.baseURL =
                                                        it.baseURL
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
                                                        customWebTab.openInWeb(
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
                                }
                                if (historyLinksQueriedData.isNotEmpty() && (selectedSearchFilters.contains(
                                        "History"
                                    ) || selectedSearchFilters.isEmpty())
                                ) {
                                    item {
                                        Text(
                                            text = "Links from History",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(15.dp)
                                        )
                                    }
                                    itemsIndexed(items = historyLinksQueriedData,
                                        key = { index, historyLink ->
                                            historyLink.baseURL + historyLink.id.toString() + UUID.randomUUID()
                                                .toString()
                                        }) { index, it ->
                                        LinkUIComponent(
                                            LinkUIComponentParam(
                                                onLongClick = {
                                                    if (!isSelectionModeEnabled.value) {
                                                        isSelectionModeEnabled.value =
                                                            true
                                                        searchScreenVM.selectedHistoryLinksData.add(
                                                            it
                                                        )
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
                                                    SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                                        OptionsBtmSheetType.LINK
                                                    SearchScreenVM.selectedLinkID = it.id
                                                    selectedLinkTitle.value = it.title
                                                    SearchScreenVM.selectedLinkType =
                                                        SearchScreenVM.SelectedLinkType.HISTORY_LINKS
                                                    HomeScreenVM.tempImpLinkData.webURL = it.webURL
                                                    HomeScreenVM.tempImpLinkData.baseURL =
                                                        it.baseURL
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
                                                        customWebTab.openInWeb(
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
                                }
                                if (archiveLinksQueriedData.isNotEmpty() && (selectedSearchFilters.contains(
                                        "Archived Links"
                                    ) || selectedSearchFilters.isEmpty())
                                ) {
                                    item {
                                        Text(
                                            text = "Links from Archive",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(15.dp)
                                        )
                                    }
                                    itemsIndexed(items = archiveLinksQueriedData,
                                        key = { index, archiveLink ->
                                            archiveLink.baseURL + archiveLink.id.toString() + UUID.randomUUID()
                                                .toString()
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
                                                    SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                                        OptionsBtmSheetType.LINK
                                                    SearchScreenVM.selectedLinkID = it.id
                                                    selectedLinkTitle.value = it.title
                                                    SearchScreenVM.selectedLinkType =
                                                        SearchScreenVM.SelectedLinkType.ARCHIVE_LINKS
                                                    HomeScreenVM.tempImpLinkData.webURL = it.webURL
                                                    HomeScreenVM.tempImpLinkData.baseURL =
                                                        it.baseURL
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
                                                        customWebTab.openInWeb(
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
                                }
                                if (queriedArchivedFoldersData.isNotEmpty() && (selectedSearchFilters.contains(
                                        "Archived Folders"
                                    ) || selectedSearchFilters.isEmpty())
                                ) {
                                    item {
                                        Text(
                                            text = "From Archived Folders",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(15.dp)
                                        )
                                    }
                                    itemsIndexed(
                                        items = queriedArchivedFoldersData,
                                        key = { index, folderData ->
                                            folderData.folderName + folderData.id.toString() + UUID.randomUUID()
                                                .toString()
                                        }) { index, folderData ->
                                        FolderIndividualComponent(
                                            showMoreIcon = !isSelectionModeEnabled.value,
                                            folderName = folderData.folderName,
                                            folderNote = folderData.infoForSaving,
                                            onMoreIconClick = {
                                                SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                                    OptionsBtmSheetType.FOLDER
                                                CollectionsScreenVM.selectedFolderData.value =
                                                    folderData
                                                clickedFolderNote.value = folderData.infoForSaving
                                                coroutineScope.launch {
                                                    optionsBtmSheetVM.updateArchiveFolderCardData(
                                                        folderData.id
                                                    )
                                                }
                                                clickedFolderName.value = folderData.folderName
                                                CollectionsScreenVM.selectedFolderData.value =
                                                    folderData
                                                shouldOptionsBtmModalSheetBeVisible.value = true
                                            },
                                            onFolderClick = {
                                                if (!isSelectionModeEnabled.value) {
                                                    SpecificCollectionsScreenVM.inARegularFolder.value =
                                                        true
                                                    SpecificCollectionsScreenVM.screenType.value =
                                                        SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN
                                                    CollectionsScreenVM.currentClickedFolderData.value =
                                                        folderData
                                                    CollectionsScreenVM.rootFolderID = folderData.id
                                                    navController.navigate(NavigationRoutes.SPECIFIC_COLLECTION_SCREEN.name)
                                                }
                                            },
                                            onLongClick = {
                                                if (!isSelectionModeEnabled.value) {
                                                    isSelectionModeEnabled.value = true
                                                    SearchScreenVM.selectedArchiveFoldersData.clear()
                                                    SearchScreenVM.selectedArchiveFoldersData.add(
                                                        folderData
                                                    )
                                                }
                                            },
                                            showCheckBox = isSelectionModeEnabled,
                                            isCheckBoxChecked = mutableStateOf(
                                                SearchScreenVM.selectedArchiveFoldersData.contains(
                                                    folderData
                                                )
                                            ),
                                            checkBoxState = { checkBoxState ->
                                                if (checkBoxState) {
                                                    SearchScreenVM.selectedArchiveFoldersData.add(
                                                        folderData
                                                    )
                                                } else {
                                                    SearchScreenVM.selectedArchiveFoldersData.removeAll {
                                                        it == folderData
                                                    }
                                                }
                                            })
                                    }
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
                                if (historyLinksData.isNotEmpty() && !isSelectionModeEnabled.value) {
                                    shouldSortingBottomSheetAppear.value = true
                                }
                            }
                            .fillMaxWidth()
                            .wrapContentHeight(),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                            if (isSelectionModeEnabled.value) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(modifier = Modifier.pulsateEffect(), onClick = {
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
                                        start = 15.dp
                                    )
                                )
                            }
                            if (!isSelectionModeEnabled.value) {
                                IconButton(
                                    modifier = if (historyLinksData.isNotEmpty()) Modifier
                                        .clickable(
                                            onClick = {},
                                            indication = null,
                                            interactionSource = remember {
                                                MutableInteractionSource()
                                            })
                                        .pulsateEffect() else Modifier, onClick = {
                                        if (historyLinksData.isNotEmpty()) {
                                            shouldSortingBottomSheetAppear.value = true
                                        }
                                    }) {
                                    if (historyLinksData.isNotEmpty()) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Outlined.Sort,
                                            contentDescription = null
                                        )
                                    }
                                }
                            } else if (isSelectionModeEnabled.value) {
                                Row {
                                    IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                        isSelectionModeEnabled.value =
                                            false
                                        searchScreenVM.archiveSelectedHistoryLinks()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Archive,
                                            contentDescription = null
                                        )
                                    }
                                    IconButton(modifier = Modifier.pulsateEffect(), onClick = {
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
                if (historyLinksData.isNotEmpty()) {
                    itemsIndexed(
                        items = historyLinksData,
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
                                    SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                        OptionsBtmSheetType.LINK
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
                                        customWebTab.openInWeb(
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
            SortingBottomSheetParam(
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
        MenuBtmSheetUI(
            MenuBtmSheetParam(
                btmModalSheetState = optionsBtmSheetState,
                shouldBtmModalSheetBeVisible = shouldOptionsBtmModalSheetBeVisible,
                btmSheetFor = SpecificCollectionsScreenVM.selectedBtmSheetType.value,
                onDeleteCardClick = {
                    shouldDeleteDialogBoxAppear.value = true
                },
                onNoteDeleteCardClick = {
                    if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        searchScreenVM.onNoteDeleteCardClick(
                            context = context,
                            selectedWebURL = selectedWebURL.value,
                            selectedLinkType = SearchScreenVM.selectedLinkType,
                            folderID = selectedFolderID
                        )
                    } else {
                        searchScreenVM.onNoteDeleteClick(
                            context,
                            CollectionsScreenVM.selectedFolderData.value.id
                        )
                    }
                },
                onRenameClick = {
                    shouldRenameDialogBoxAppear.value = true
                },
                onArchiveClick = {
                    if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        searchScreenVM.onArchiveClick(
                            context,
                            selectedLinkType = SearchScreenVM.selectedLinkType,
                            folderID = selectedFolderID
                        )
                    } else {
                        searchScreenVM.onUiEvent(
                            SpecificCollectionsScreenUIEvent.ArchiveAFolder(
                                CollectionsScreenVM.selectedFolderData.value.id
                            )
                        )
                    }
                },
                noteForSaving = selectedURLNote.value,
                folderName = CollectionsScreenVM.selectedFolderData.value.folderName,
                linkTitle = selectedLinkTitle.value
            )
        )
        RenameDialogBox(
            RenameDialogBoxParam(
                shouldDialogBoxAppear = shouldRenameDialogBoxAppear,
                existingFolderName = CollectionsScreenVM.selectedFolderData.value.folderName,
                onNoteChangeClick = { newNote ->
                    if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        searchScreenVM.onNoteChangeClickForLinks(
                            HomeScreenVM.tempImpLinkData.webURL,
                            newNote,
                            selectedLinkType = SearchScreenVM.selectedLinkType,
                            folderID = selectedFolderID, linkID = SearchScreenVM.selectedLinkID
                        )
                    } else {
                        searchScreenVM.onUiEvent(
                            SpecificCollectionsScreenUIEvent.UpdateFolderNote(
                            CollectionsScreenVM.selectedFolderData.value.id,
                            newNote
                            )
                        )
                    }
                    shouldRenameDialogBoxAppear.value = false
                },
                renameDialogBoxFor = SpecificCollectionsScreenVM.selectedBtmSheetType.value,
                onTitleChangeClick = { newTitle ->
                    if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        searchScreenVM.onTitleChangeClickForLinks(
                            HomeScreenVM.tempImpLinkData.webURL,
                            newTitle,
                            selectedLinkType = SearchScreenVM.selectedLinkType,
                            folderID = selectedFolderID, linkID = SearchScreenVM.selectedLinkID
                        )
                    } else {
                        searchScreenVM.onUiEvent(
                            SpecificCollectionsScreenUIEvent.UpdateFolderName(
                                newTitle,
                                CollectionsScreenVM.selectedFolderData.value.id
                            )
                        )
                    }
                    shouldRenameDialogBoxAppear.value = false
                }
            )
        )
        DeleteDialogBox(
            DeleteDialogBoxParam(
                folderName = mutableStateOf(CollectionsScreenVM.selectedFolderData.value.folderName),
                areFoldersSelectable = isSelectionModeEnabled.value,
                shouldDialogBoxAppear = shouldDeleteDialogBoxAppear,
                deleteDialogBoxType = if (isSelectionModeEnabled.value) DataDialogBoxType.SELECTED_DATA else if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) DataDialogBoxType.LINK else DataDialogBoxType.FOLDER,
                onDeleteClick = {
                    if (!isSelectionModeEnabled.value) {
                        if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                            searchScreenVM.onDeleteClick(
                                context = context,
                                selectedWebURL = selectedWebURL.value,
                                shouldDeleteBoxAppear = shouldDeleteDialogBoxAppear,
                                selectedLinkType = SearchScreenVM.selectedLinkType,
                                folderID = selectedFolderID
                            )
                        } else {
                            searchScreenVM.onUiEvent(
                                SpecificCollectionsScreenUIEvent.DeleteAFolder(
                                    CollectionsScreenVM.selectedFolderData.value.id
                                )
                            )
                        }
                    } else {
                        searchScreenVM.deleteSelectedHistoryLinks()
                        searchScreenVM.deleteSelectedArchivedLinks()
                        searchScreenVM.deleteSelectedImpLinksData()
                        searchScreenVM.deleteSelectedLinksTableData()
                        searchScreenVM.onDeleteMultipleSelectedFolders()
                        searchScreenVM.selectedFoldersData.clear()
                        searchScreenVM.selectedImportantLinksData.clear()
                        searchScreenVM.selectedLinksTableData.clear()
                        searchScreenVM.selectedArchiveLinksTableData.clear()
                        searchScreenVM.selectedHistoryLinksData.clear()
                        SearchScreenVM.selectedArchiveFoldersData.clear()
                        SearchScreenVM.isSearchEnabled.value = false
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
                if (isSelectionModeEnabled.value) {
                    searchScreenVM.selectedFoldersData.clear()
                    searchScreenVM.selectedImportantLinksData.clear()
                    searchScreenVM.selectedLinksTableData.clear()
                    searchScreenVM.selectedArchiveLinksTableData.clear()
                    searchScreenVM.selectedHistoryLinksData.clear()
                    SearchScreenVM.selectedArchiveFoldersData.clear()
                    isSelectionModeEnabled.value = false
                }
            }

            else -> if (isSelectionModeEnabled.value) {
                isSelectionModeEnabled.value = false
                searchScreenVM.areAllLinksChecked.value = false
                searchScreenVM.areAllFoldersChecked.value = false
                searchScreenVM.selectedImportantLinksData.clear()
                searchScreenVM.selectedLinksTableData.clear()
                searchScreenVM.selectedArchiveLinksTableData.clear()
                searchScreenVM.selectedHistoryLinksData.clear()
                SearchScreenVM.selectedArchiveFoldersData.clear()
            } else if (SettingsScreenVM.Settings.isHomeScreenEnabled.value) {
                navController.navigate(NavigationRoutes.HOME_SCREEN.name) {
                    popUpTo(0)
                }
            } else {
                activity?.moveTaskToBack(true)
            }
        }
    }
}