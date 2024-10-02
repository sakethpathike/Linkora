package com.sakethh.linkora.ui.screens.collections.allLinks

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.data.local.links.LinkType
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
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenUIEvent
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenVM
import com.sakethh.linkora.ui.screens.collections.specific.SpecificScreenType
import com.sakethh.linkora.ui.screens.home.HomeScreenVM
import com.sakethh.linkora.ui.screens.linkLayout.LinkLayout
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.composables.SpecificScreenScaffold
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun AllLinksScreen(navController: NavController) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setNavigationBarColor(
        MaterialTheme.colorScheme.surfaceColorAtElevation(
            BottomAppBarDefaults.ContainerElevation
        )
    )
    val allLinksScreenVM: AllLinksScreenVM = hiltViewModel()

    val savedLinks = allLinksScreenVM.savedLinks.collectAsStateWithLifecycle(emptyList())
    val impLinks = allLinksScreenVM.importantLinks.collectAsStateWithLifecycle(emptyList())
    val historyLinks = allLinksScreenVM.historyLinks.collectAsStateWithLifecycle(emptyList())
    val archivedLinks = allLinksScreenVM.archivedLinks.collectAsStateWithLifecycle(emptyList())
    val regularFoldersLinks =
        allLinksScreenVM.regularFoldersLinks.collectAsStateWithLifecycle(emptyList())

    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()
    val btmModalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shouldOptionsBtmModalSheetBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldRenameDialogBoxAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldDeleteDialogBoxAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val selectedWebURL = rememberSaveable {
        mutableStateOf("")
    }
    val selectedElementID = rememberSaveable {
        mutableLongStateOf(0)
    }
    val selectedURLTitle = rememberSaveable {
        mutableStateOf("")
    }
    val selectedNote = rememberSaveable {
        mutableStateOf("")
    }
    val optionsBtmSheetVM: OptionsBtmSheetVM = hiltViewModel()
    val selectedLinkType = rememberSaveable {
        mutableStateOf("")
    }
    val noLinksSelectedState = allLinksScreenVM.linkTypes.map { it.isChecked.value }.all { !it }
    val customWebTab = hiltViewModel<CustomWebTab>()
    val context = LocalContext.current
    val onImportantLinkClickTriggered = rememberSaveable {
        mutableStateOf(false)
    }
    val isAllTablesEmpty = savedLinks.value.isEmpty() && impLinks.value.isEmpty() &&
            historyLinks.value.isEmpty() && archivedLinks.value.isEmpty() &&
            regularFoldersLinks.value.isEmpty()
    LaunchedEffect(Unit) {
        allLinksScreenVM.uiChannel.collectLatest {
            when (it) {
                is CommonUiEvent.ShowDeleteDialogBox -> shouldDeleteDialogBoxAppear.value = true
                is CommonUiEvent.ShowToast -> Toast.makeText(context, it.msg, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    fun modifiedLinkUIComponentParam(
        linksTable: LinksTable,
        linkType: LinkType,
        selectedBtmSheetType: SpecificScreenType
    ): LinkUIComponentParam {
        return LinkUIComponentParam(
            onLongClick = {},
            isSelectionModeEnabled = mutableStateOf(false),
            isItemSelected = mutableStateOf(
                false
            ),
            title = linksTable.title,
            webBaseURL = linksTable.baseURL,
            imgURL = linksTable.imgURL,
            onMoreIconClick = {
                SpecificCollectionsScreenVM.screenType.value = selectedBtmSheetType
                selectedLinkType.value = linkType.name
                SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                    OptionsBtmSheetType.LINK
                selectedElementID.longValue = linksTable.id
                HomeScreenVM.tempImpLinkData.baseURL = linksTable.baseURL
                HomeScreenVM.tempImpLinkData.imgURL = linksTable.imgURL
                HomeScreenVM.tempImpLinkData.webURL = linksTable.webURL
                HomeScreenVM.tempImpLinkData.title = linksTable.title
                HomeScreenVM.tempImpLinkData.infoForSaving =
                    linksTable.infoForSaving
                shouldOptionsBtmModalSheetBeVisible.value = true
                selectedWebURL.value = linksTable.webURL
                selectedNote.value = linksTable.infoForSaving
                selectedURLTitle.value = linksTable.title
                coroutineScope.launch {
                    awaitAll(async {
                        optionsBtmSheetVM.updateImportantCardData(
                            url = selectedWebURL.value
                        )
                    }, async {
                        optionsBtmSheetVM.updateArchiveLinkCardData(
                            url = selectedWebURL.value
                        )
                    }
                    )
                }
            },
            onLinkClick = {
                customWebTab.openInWeb(
                    recentlyVisitedData = RecentlyVisited(
                        title = linksTable.title,
                        webURL = linksTable.webURL,
                        baseURL = linksTable.baseURL,
                        imgURL = linksTable.imgURL,
                        infoForSaving = linksTable.infoForSaving
                    ), context = context, uriHandler = uriHandler,
                    forceOpenInExternalBrowser = false
                )
            },
            webURL = linksTable.webURL,
            onForceOpenInExternalBrowserClicked = {
                customWebTab.openInWeb(
                    recentlyVisitedData = RecentlyVisited(
                        title = linksTable.title,
                        webURL = linksTable.webURL,
                        baseURL = linksTable.baseURL,
                        imgURL = linksTable.imgURL,
                        infoForSaving = linksTable.infoForSaving
                    ), context = context, uriHandler = uriHandler,
                    forceOpenInExternalBrowser = true
                )
            })
    }
    SpecificScreenScaffold(
        topAppBarText = LocalizedStrings.allLinks.value,
        navController = navController,
        actions = {
            if (!isAllTablesEmpty) {
                IconButton(onClick = {
                    navController.navigate(NavigationRoutes.LINK_LAYOUT_SETTINGS.name)
                }) {
                    Icon(Icons.Default.Dashboard, null)
                }
            }
        }, bottomBar = {
            if (isAllTablesEmpty) {
                return@SpecificScreenScaffold
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(
                            BottomAppBarDefaults.ContainerElevation
                        )
                    )
            ) {
                Text(
                    LocalizedStrings.filterBasedOn.value,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = 15.dp, top = 15.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(15.dp))
                    allLinksScreenVM.linkTypes.forEach {
                        if (it.linkType == LinkType.SAVED_LINK && savedLinks.value.isEmpty()) return@forEach
                        if (it.linkType == LinkType.IMP_LINK && impLinks.value.isEmpty()) return@forEach
                        if (it.linkType == LinkType.HISTORY_LINK && historyLinks.value.isEmpty()) return@forEach
                        if (it.linkType == LinkType.ARCHIVE_LINK && archivedLinks.value.isEmpty()) return@forEach
                        if (it.linkType == LinkType.FOLDER_LINK && regularFoldersLinks.value.isEmpty()) return@forEach
                        FilterChip(modifier = Modifier.animateContentSize(), onClick = {
                            it.isChecked.value = !it.isChecked.value
                        }, selected = it.isChecked.value, label = {
                            Text(
                                when (it.linkType) {
                                    LinkType.SAVED_LINK -> LocalizedStrings.savedLinks.value
                                    LinkType.IMP_LINK -> LocalizedStrings.importantLinks.value
                                    LinkType.FOLDER_LINK -> LocalizedStrings.foldersLinks.value
                                    LinkType.HISTORY_LINK -> LocalizedStrings.linksFromHistory.value
                                    LinkType.ARCHIVE_LINK -> LocalizedStrings.archivedLinks.value
                                }, style = MaterialTheme.typography.titleSmall
                            )
                        })
                        Spacer(Modifier.width(10.dp))
                    }
                }
            }
        }) { paddingValues, topAppBarScrollBehaviour ->
        val commonModifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .nestedScroll(topAppBarScrollBehaviour.nestedScrollConnection)
            .animateContentSize()

        if (isAllTablesEmpty) {
            LazyColumn(
                modifier = commonModifier
            ) {
                item {
                    DataEmptyScreen(LocalizedStrings.noLinksWereFound.value)
                }
            }
            return@SpecificScreenScaffold
        }
        when (SettingsPreference.currentlySelectedLinkLayout.value) {
            LinkLayout.REGULAR_LIST_VIEW.name, LinkLayout.TITLE_ONLY_LIST_VIEW.name -> {
                LazyColumn(
                    modifier = commonModifier
                ) {
                    if (allLinksScreenVM.linkTypes.find { it.linkType == LinkType.SAVED_LINK }!!.isChecked.value || noLinksSelectedState) {
                        items(savedLinks.value) {
                            ListViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.SAVED_LINK,
                                    selectedBtmSheetType = SpecificScreenType.SAVED_LINKS_SCREEN
                                ),
                                forTitleOnlyView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.TITLE_ONLY_LIST_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == LinkType.IMP_LINK }!!.isChecked.value || noLinksSelectedState) {
                        items(impLinks.value.map {
                            LinksTable(
                                id = it.id,
                                title = it.title,
                                webURL = it.webURL,
                                baseURL = it.baseURL,
                                imgURL = it.imgURL,
                                infoForSaving = it.infoForSaving,
                                isLinkedWithSavedLinks = false,
                                isLinkedWithFolders = false,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "Graduation",
                                isLinkedWithArchivedFolder = false
                            )
                        }) {
                            ListViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.IMP_LINK,
                                    selectedBtmSheetType = SpecificScreenType.IMPORTANT_LINKS_SCREEN
                                ),
                                forTitleOnlyView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.TITLE_ONLY_LIST_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == LinkType.HISTORY_LINK }!!.isChecked.value || noLinksSelectedState) {
                        items(historyLinks.value.map {
                            LinksTable(
                                id = it.id,
                                title = it.title,
                                webURL = it.webURL,
                                baseURL = it.baseURL,
                                imgURL = it.imgURL,
                                infoForSaving = it.infoForSaving,
                                isLinkedWithSavedLinks = false,
                                isLinkedWithFolders = false,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "Illmatic",
                                isLinkedWithArchivedFolder = false
                            )
                        }) {
                            ListViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.HISTORY_LINK,
                                    selectedBtmSheetType = SpecificScreenType.ROOT_SCREEN
                                ),
                                forTitleOnlyView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.TITLE_ONLY_LIST_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == LinkType.ARCHIVE_LINK }!!.isChecked.value || noLinksSelectedState) {
                        items(archivedLinks.value.map {
                            LinksTable(
                                id = it.id,
                                title = it.title,
                                webURL = it.webURL,
                                baseURL = it.baseURL,
                                imgURL = it.imgURL,
                                infoForSaving = it.infoForSaving,
                                isLinkedWithSavedLinks = false,
                                isLinkedWithFolders = false,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "It Was Written",
                                isLinkedWithArchivedFolder = false
                            )
                        }) {
                            ListViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.ARCHIVE_LINK,
                                    selectedBtmSheetType = SpecificScreenType.ROOT_SCREEN
                                ),
                                forTitleOnlyView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.TITLE_ONLY_LIST_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == LinkType.FOLDER_LINK }!!.isChecked.value || noLinksSelectedState) {
                        items(regularFoldersLinks.value) {
                            if (!it.isLinkedWithArchivedFolder)
                                ListViewLinkUIComponent(
                                    linkUIComponentParam = modifiedLinkUIComponentParam(
                                        linksTable = it,
                                        linkType = LinkType.FOLDER_LINK,
                                        selectedBtmSheetType = SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                                    ),
                                    forTitleOnlyView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.TITLE_ONLY_LIST_VIEW.name
                                )
                        }
                    }
                }
            }

            LinkLayout.GRID_VIEW.name -> {
                LazyVerticalGrid(columns = GridCells.Adaptive(150.dp), modifier = commonModifier) {

                    if (allLinksScreenVM.linkTypes.find { it.linkType == LinkType.SAVED_LINK }!!.isChecked.value || noLinksSelectedState) {
                        items(savedLinks.value) {
                            GridViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.SAVED_LINK,
                                    selectedBtmSheetType = SpecificScreenType.SAVED_LINKS_SCREEN
                                ),
                                forStaggeredView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == LinkType.IMP_LINK }!!.isChecked.value || noLinksSelectedState) {
                        items(impLinks.value.map {
                            LinksTable(
                                id = it.id,
                                title = it.title,
                                webURL = it.webURL,
                                baseURL = it.baseURL,
                                imgURL = it.imgURL,
                                infoForSaving = it.infoForSaving,
                                isLinkedWithSavedLinks = false,
                                isLinkedWithFolders = false,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "",
                                isLinkedWithArchivedFolder = false
                            )
                        }) {
                            GridViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.IMP_LINK,
                                    selectedBtmSheetType = SpecificScreenType.IMPORTANT_LINKS_SCREEN
                                ),
                                forStaggeredView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == LinkType.HISTORY_LINK }!!.isChecked.value || noLinksSelectedState) {
                        items(historyLinks.value.map {
                            LinksTable(
                                id = it.id,
                                title = it.title,
                                webURL = it.webURL,
                                baseURL = it.baseURL,
                                imgURL = it.imgURL,
                                infoForSaving = it.infoForSaving,
                                isLinkedWithSavedLinks = false,
                                isLinkedWithFolders = false,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "",
                                isLinkedWithArchivedFolder = false
                            )
                        }) {
                            GridViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.HISTORY_LINK,
                                    selectedBtmSheetType = SpecificScreenType.ROOT_SCREEN
                                ),
                                forStaggeredView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == LinkType.ARCHIVE_LINK }!!.isChecked.value || noLinksSelectedState) {
                        items(archivedLinks.value.map {
                            LinksTable(
                                id = it.id,
                                title = it.title,
                                webURL = it.webURL,
                                baseURL = it.baseURL,
                                imgURL = it.imgURL,
                                infoForSaving = it.infoForSaving,
                                isLinkedWithSavedLinks = false,
                                isLinkedWithFolders = false,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "",
                                isLinkedWithArchivedFolder = false
                            )
                        }) {
                            GridViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.ARCHIVE_LINK,
                                    selectedBtmSheetType = SpecificScreenType.ROOT_SCREEN
                                ),
                                forStaggeredView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == LinkType.FOLDER_LINK }!!.isChecked.value || noLinksSelectedState) {
                        items(regularFoldersLinks.value) {
                            if (!it.isLinkedWithArchivedFolder)
                                GridViewLinkUIComponent(
                                    linkUIComponentParam = modifiedLinkUIComponentParam(
                                        linksTable = it,
                                        linkType = LinkType.FOLDER_LINK,
                                        selectedBtmSheetType = SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                                    ),
                                    forStaggeredView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name
                                )
                        }
                    }
                }
            }

            LinkLayout.STAGGERED_VIEW.name -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(150.dp), modifier = commonModifier
                ) {

                    if (allLinksScreenVM.linkTypes.find { it.linkType == LinkType.SAVED_LINK }!!.isChecked.value || noLinksSelectedState) {
                        items(savedLinks.value) {
                            GridViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.SAVED_LINK,
                                    selectedBtmSheetType = SpecificScreenType.SAVED_LINKS_SCREEN
                                ),
                                forStaggeredView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == LinkType.IMP_LINK }!!.isChecked.value || noLinksSelectedState) {
                        items(impLinks.value.map {
                            LinksTable(
                                id = it.id,
                                title = it.title,
                                webURL = it.webURL,
                                baseURL = it.baseURL,
                                imgURL = it.imgURL,
                                infoForSaving = it.infoForSaving,
                                isLinkedWithSavedLinks = false,
                                isLinkedWithFolders = false,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "",
                                isLinkedWithArchivedFolder = false
                            )
                        }) {
                            GridViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.IMP_LINK,
                                    selectedBtmSheetType = SpecificScreenType.IMPORTANT_LINKS_SCREEN
                                ),
                                forStaggeredView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == LinkType.HISTORY_LINK }!!.isChecked.value || noLinksSelectedState) {
                        items(historyLinks.value.map {
                            LinksTable(
                                id = it.id,
                                title = it.title,
                                webURL = it.webURL,
                                baseURL = it.baseURL,
                                imgURL = it.imgURL,
                                infoForSaving = it.infoForSaving,
                                isLinkedWithSavedLinks = false,
                                isLinkedWithFolders = false,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "",
                                isLinkedWithArchivedFolder = false
                            )
                        }) {
                            GridViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.HISTORY_LINK,
                                    selectedBtmSheetType = SpecificScreenType.ROOT_SCREEN
                                ),
                                forStaggeredView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == LinkType.ARCHIVE_LINK }!!.isChecked.value || noLinksSelectedState) {
                        items(archivedLinks.value.map {
                            LinksTable(
                                id = it.id,
                                title = it.title,
                                webURL = it.webURL,
                                baseURL = it.baseURL,
                                imgURL = it.imgURL,
                                infoForSaving = it.infoForSaving,
                                isLinkedWithSavedLinks = false,
                                isLinkedWithFolders = false,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "",
                                isLinkedWithArchivedFolder = false
                            )
                        }) {
                            GridViewLinkUIComponent(
                                linkUIComponentParam = modifiedLinkUIComponentParam(
                                    linksTable = it,
                                    linkType = LinkType.ARCHIVE_LINK,
                                    selectedBtmSheetType = SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN
                                ),
                                forStaggeredView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name
                            )
                        }
                    }
                    if (allLinksScreenVM.linkTypes.find { it.linkType == LinkType.FOLDER_LINK }!!.isChecked.value || noLinksSelectedState) {
                        items(regularFoldersLinks.value) {
                            if (!it.isLinkedWithArchivedFolder)
                                GridViewLinkUIComponent(
                                    linkUIComponentParam = modifiedLinkUIComponentParam(
                                        linksTable = it,
                                        linkType = LinkType.FOLDER_LINK,
                                        selectedBtmSheetType = SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                                    ),
                                    forStaggeredView = SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name
                                )
                        }
                    }
                }
            }
        }
        val showQuickActions =
            rememberSaveable(SettingsPreference.currentlySelectedLinkLayout.value) {
                mutableStateOf(SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.STAGGERED_VIEW.name || SettingsPreference.currentlySelectedLinkLayout.value == LinkLayout.GRID_VIEW.name)
            }
        MenuBtmSheetUI(
            MenuBtmSheetParam(
                shouldImportantLinkOptionBeVisible = mutableStateOf(selectedLinkType.value == LinkType.IMP_LINK.name),
                inSpecificArchiveScreen = mutableStateOf(false),
                onUnarchiveClick = {
                    allLinksScreenVM.onUIEvent(
                        SpecificCollectionsScreenUIEvent.UnArchiveAnExistingLink(
                            ArchivedLinks(
                                title = HomeScreenVM.tempImpLinkData.title,
                                webURL = HomeScreenVM.tempImpLinkData.webURL,
                                baseURL = HomeScreenVM.tempImpLinkData.baseURL,
                                imgURL = HomeScreenVM.tempImpLinkData.imgURL,
                                infoForSaving = HomeScreenVM.tempImpLinkData.infoForSaving,
                                id = selectedElementID.longValue
                            )
                        )
                    )
                },
                webUrl = selectedWebURL.value,
                inArchiveScreen = rememberSaveable(selectedLinkType.value) {
                    mutableStateOf(selectedLinkType.value == LinkType.ARCHIVE_LINK.name)
                },
                showQuickActions = showQuickActions,
                btmModalSheetState = btmModalSheetState,
                shouldBtmModalSheetBeVisible = shouldOptionsBtmModalSheetBeVisible,
                btmSheetFor = when (SpecificCollectionsScreenVM.screenType.value) {
                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN
                    else -> OptionsBtmSheetType.LINK
                },
                onRenameClick = {
                    coroutineScope.launch {
                        btmModalSheetState.hide()
                    }
                    shouldRenameDialogBoxAppear.value = true
                },
                onDeleteCardClick = {
                    shouldDeleteDialogBoxAppear.value = true
                },
                onArchiveClick = {
                    allLinksScreenVM.onUIEvent(
                        SpecificCollectionsScreenUIEvent.ArchiveAnExistingLink(
                            ArchivedLinks(
                                id = selectedElementID.longValue,
                                title = HomeScreenVM.tempImpLinkData.title,
                                webURL = HomeScreenVM.tempImpLinkData.webURL,
                                baseURL = HomeScreenVM.tempImpLinkData.baseURL,
                                imgURL = HomeScreenVM.tempImpLinkData.imgURL,
                                infoForSaving = HomeScreenVM.tempImpLinkData.infoForSaving
                            ), context, LinkType.valueOf(selectedLinkType.value)
                        )
                    )
                },
                noteForSaving = selectedNote.value,
                onNoteDeleteCardClick = {
                    allLinksScreenVM.onUIEvent(
                        SpecificCollectionsScreenUIEvent.DeleteAnExistingNote(
                            selectedElementID.longValue, LinkType.valueOf(selectedLinkType.value)
                        )
                    )
                },
                folderName = selectedURLTitle.value,
                linkTitle = selectedURLTitle.value,
                imgLink = HomeScreenVM.tempImpLinkData.imgURL,
                onRefreshClick = {
                    allLinksScreenVM.onUIEvent(
                        SpecificCollectionsScreenUIEvent.OnLinkRefresh(
                            selectedElementID.longValue, LinkType.valueOf(selectedLinkType.value)
                        )
                    )
                }, onImportantLinkClick = {
                    onImportantLinkClickTriggered.value = true
                    allLinksScreenVM.onUIEvent(
                        SpecificCollectionsScreenUIEvent.AddExistingLinkToImportantLink(
                            ImportantLinks(
                                title = HomeScreenVM.tempImpLinkData.title,
                                webURL = HomeScreenVM.tempImpLinkData.webURL,
                                baseURL = HomeScreenVM.tempImpLinkData.baseURL,
                                imgURL = HomeScreenVM.tempImpLinkData.imgURL,
                                infoForSaving = HomeScreenVM.tempImpLinkData.infoForSaving
                            )
                        )
                    )
                }
            )
        )
    }
    DeleteDialogBox(
        DeleteDialogBoxParam(
            folderName = selectedURLTitle,
            shouldDialogBoxAppear = shouldDeleteDialogBoxAppear,
            deleteDialogBoxType = if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) DataDialogBoxType.LINK else DataDialogBoxType.FOLDER,
            onDeleteClick = {
                allLinksScreenVM.onUIEvent(
                    SpecificCollectionsScreenUIEvent.DeleteAnExistingLink(
                        selectedElementID.longValue, LinkType.valueOf(selectedLinkType.value)
                    )
                )
            })
    )
    RenameDialogBox(
        RenameDialogBoxParam(
            shouldDialogBoxAppear = shouldRenameDialogBoxAppear,
            existingFolderName = selectedURLTitle.value,
            renameDialogBoxFor = SpecificCollectionsScreenVM.selectedBtmSheetType.value,
            onNoteChangeClick = { newNote: String ->
                allLinksScreenVM.onUIEvent(
                    SpecificCollectionsScreenUIEvent.UpdateLinkNote(
                        linkId = selectedElementID.longValue,
                        newNote = newNote,
                        linkType = LinkType.valueOf(selectedLinkType.value)
                    )
                )
                shouldRenameDialogBoxAppear.value = false
            },
            onTitleChangeClick = { newTitle: String ->
                allLinksScreenVM.onUIEvent(
                    SpecificCollectionsScreenUIEvent.UpdateLinkTitle(
                        linkId = selectedElementID.longValue,
                        newTitle = newTitle,
                        linkType = LinkType.valueOf(selectedLinkType.value)
                    )
                )
                shouldRenameDialogBoxAppear.value = false
            }, existingTitle = selectedURLTitle.value, existingNote = selectedNote.value
        )
    )
}