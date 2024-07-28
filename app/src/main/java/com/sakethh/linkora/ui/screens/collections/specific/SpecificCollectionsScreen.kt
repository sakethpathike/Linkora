package com.sakethh.linkora.ui.screens.collections.specific

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.ui.bottomSheets.menu.MenuBtmSheetParam
import com.sakethh.linkora.ui.bottomSheets.menu.MenuBtmSheetUI
import com.sakethh.linkora.ui.bottomSheets.sorting.SortingBottomSheetParam
import com.sakethh.linkora.ui.bottomSheets.sorting.SortingBottomSheetUI
import com.sakethh.linkora.ui.bottomSheets.sorting.SortingBtmSheetType
import com.sakethh.linkora.ui.commonComposables.AddNewFolderDialogBox
import com.sakethh.linkora.ui.commonComposables.AddNewFolderDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.AddNewLinkDialogBox
import com.sakethh.linkora.ui.commonComposables.DataDialogBoxType
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBox
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.FloatingActionBtn
import com.sakethh.linkora.ui.commonComposables.FloatingActionBtnParam
import com.sakethh.linkora.ui.commonComposables.LinkUIComponent
import com.sakethh.linkora.ui.commonComposables.LinkUIComponentParam
import com.sakethh.linkora.ui.commonComposables.RenameDialogBox
import com.sakethh.linkora.ui.commonComposables.RenameDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.OptionsBtmSheetType
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.OptionsBtmSheetVM
import com.sakethh.linkora.ui.navigation.NavigationRoutes
import com.sakethh.linkora.ui.screens.DataEmptyScreen
import com.sakethh.linkora.ui.screens.collections.CollectionsScreenVM
import com.sakethh.linkora.ui.screens.collections.FolderIndividualComponent
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState", "LongLogTag")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecificCollectionScreen(navController: NavController) {
    val specificCollectionsScreenVM: SpecificCollectionsScreenVM = hiltViewModel()
    val selectedWebURL = rememberSaveable {
        mutableStateOf("")
    }
    val isDataExtractingForTheLink = rememberSaveable {
        mutableStateOf(false)
    }
    val specificFolderLinksData =
        specificCollectionsScreenVM.folderLinksData.collectAsStateWithLifecycle().value
    val childFoldersData =
        specificCollectionsScreenVM.childFoldersData.collectAsStateWithLifecycle().value
    val savedLinksData =
        specificCollectionsScreenVM.savedLinksTable.collectAsStateWithLifecycle().value
    val impLinksData = specificCollectionsScreenVM.impLinksTable.collectAsStateWithLifecycle().value
    val archivedFoldersLinksData =
        specificCollectionsScreenVM.archiveFoldersLinksData.collectAsStateWithLifecycle().value
    val archivedSubFoldersData =
        specificCollectionsScreenVM.archiveSubFolderData.collectAsStateWithLifecycle().value
    val tempImpLinkData = specificCollectionsScreenVM.impLinkDataForBtmSheet
    val btmModalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val btmModalSheetStateForSavingLink =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shouldOptionsBtmModalSheetBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldRenameDialogBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldDeleteDialogBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldSortingBottomSheetAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val sortingBtmSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val selectedURLOrFolderNote = rememberSaveable {
        mutableStateOf("")
    }
    val selectedURLTitle = rememberSaveable {
        mutableStateOf("")
    }
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val optionsBtmSheetVM: OptionsBtmSheetVM = hiltViewModel()
    val topBarText = when (SpecificCollectionsScreenVM.screenType.value) {
        SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
            "Important Links"
        }

        SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
            CollectionsScreenVM.currentClickedFolderData.value.folderName
        }

        SpecificScreenType.SAVED_LINKS_SCREEN -> {
            "Saved Links"
        }

        SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
            try {
                CollectionsScreenVM.currentClickedFolderData.value.folderName
            } catch (_: java.lang.NullPointerException) {
                ""
            }
        }

        else -> {
            ""
        }
    }
    val shouldNewLinkDialogBoxBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldBtmSheetForNewLinkAdditionBeEnabled = rememberSaveable {
        mutableStateOf(false)
    }
    val btmModalSheetStateForSavingLinks =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isMainFabRotated = rememberSaveable {
        mutableStateOf(false)
    }
    val clickedFolderName = rememberSaveable { mutableStateOf("") }
    val clickedFolderNote = rememberSaveable { mutableStateOf("") }
    val rotationAnimation = remember {
        Animatable(0f)
    }
    val shouldScreenTransparencyDecreasedBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldDialogForNewFolderAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val areElementsSelectable = rememberSaveable {
        mutableStateOf(false)
    }
    LinkoraTheme {
        Scaffold(floatingActionButtonPosition = FabPosition.End, floatingActionButton = {
            if (!areElementsSelectable.value && SpecificCollectionsScreenVM.screenType.value == SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN) {
                FloatingActionBtn(
                    FloatingActionBtnParam(
                        newLinkBottomModalSheetState = btmModalSheetStateForSavingLinks,
                        shouldBtmSheetForNewLinkAdditionBeEnabled = shouldBtmSheetForNewLinkAdditionBeEnabled,
                        shouldScreenTransparencyDecreasedBoxVisible = shouldScreenTransparencyDecreasedBoxVisible,
                        shouldDialogForNewFolderAppear = shouldDialogForNewFolderAppear,
                        shouldDialogForNewLinkAppear = shouldNewLinkDialogBoxBeVisible,
                        isMainFabRotated = isMainFabRotated,
                        rotationAnimation = rotationAnimation,
                        inASpecificScreen = true
                    )
                )
            } else if (!areElementsSelectable.value && SpecificCollectionsScreenVM.screenType.value != SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN && SpecificCollectionsScreenVM.screenType.value != SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN) {
                FloatingActionButton(onClick = {
                    if (!SettingsScreenVM.Settings.isBtmSheetEnabledForSavingLinks.value) {
                        shouldNewLinkDialogBoxBeVisible.value = true
                    } else {
                        coroutineScope.launch {
                            awaitAll(async {
                                btmModalSheetStateForSavingLink.expand()
                            }, async { shouldBtmSheetForNewLinkAdditionBeEnabled.value = true })
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.AddLink, contentDescription = null
                    )
                }
            }
        }, modifier = Modifier.background(MaterialTheme.colorScheme.surface), topBar = {
            Column {
                TopAppBar(navigationIcon = {
                    if (!areElementsSelectable.value) {
                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                            if (CollectionsScreenVM.currentClickedFolderData.value.parentFolderID != null
                                && (SpecificCollectionsScreenVM.screenType.value == SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                                        || SpecificCollectionsScreenVM.screenType.value == SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN)
                            ) {
                                if (SpecificCollectionsScreenVM.inARegularFolder.value) {
                                    SpecificCollectionsScreenVM.screenType.value =
                                        SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                                } else {
                                    SpecificCollectionsScreenVM.screenType.value =
                                        SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN
                                }
                                specificCollectionsScreenVM.updateFolderData(CollectionsScreenVM.currentClickedFolderData.value.parentFolderID!!)
                            }
                            navController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    } else {
                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                            areElementsSelectable.value = false
                            specificCollectionsScreenVM.areAllLinksChecked.value = false
                            specificCollectionsScreenVM.areAllFoldersChecked.value = false
                            specificCollectionsScreenVM.selectedImpLinks.clear()
                            specificCollectionsScreenVM.removeAllLinkSelections()
                            specificCollectionsScreenVM.changeAllFoldersSelectedData(
                                childFoldersData
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Default.Cancel, contentDescription = null
                            )
                        }
                    }
                }, scrollBehavior = scrollBehavior,
                    title = {
                        if (areElementsSelectable.value) {
                            Row {
                                AnimatedContent(
                                    targetState = specificCollectionsScreenVM.selectedImpLinks.size + specificCollectionsScreenVM.selectedLinksID.size + specificCollectionsScreenVM.selectedFoldersData.size,
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
                                Text(text = " items selected",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontSize = 18.sp,
                                    modifier = Modifier.clickable {

                                    })
                            }
                        } else {
                            Text(
                                text = topBarText,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 18.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    actions = {
                        if (areElementsSelectable.value) {
                            when (SpecificCollectionsScreenVM.screenType.value) {
                                SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                                    if ((specificCollectionsScreenVM.selectedImpLinks.size + specificCollectionsScreenVM.selectedFoldersData.size != 0)) {
                                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                            shouldDeleteDialogBeVisible.value = true
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null
                                            )
                                        }
                                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                            specificCollectionsScreenVM.moveMultipleLinksFromImpLinksToArchive()
                                            areElementsSelectable.value = false
                                            specificCollectionsScreenVM.areAllLinksChecked.value =
                                                false
                                            specificCollectionsScreenVM.areAllFoldersChecked.value =
                                                false
                                            specificCollectionsScreenVM.selectedImpLinks.clear()
                                            specificCollectionsScreenVM.removeAllLinkSelections()
                                            specificCollectionsScreenVM.changeAllFoldersSelectedData(
                                                childFoldersData
                                            )
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Archive,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }

                                SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                                    if ((specificCollectionsScreenVM.selectedLinksID.size + specificCollectionsScreenVM.selectedFoldersData.size != 0)) {
                                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                            shouldDeleteDialogBeVisible.value = true
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null
                                            )
                                        }
                                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                            specificCollectionsScreenVM.moveMultipleLinksFromLinksTableToArchive()
                                            areElementsSelectable.value = false
                                            specificCollectionsScreenVM.areAllLinksChecked.value =
                                                false
                                            specificCollectionsScreenVM.areAllFoldersChecked.value =
                                                false
                                            specificCollectionsScreenVM.removeAllLinkSelections()
                                            specificCollectionsScreenVM.changeAllFoldersSelectedData(
                                                childFoldersData
                                            )
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Archive,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }

                                SpecificScreenType.SAVED_LINKS_SCREEN -> {
                                    if ((specificCollectionsScreenVM.selectedLinksID.size + specificCollectionsScreenVM.selectedFoldersData.size != 0)) {
                                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                            shouldDeleteDialogBeVisible.value = true
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null
                                            )
                                        }
                                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                            specificCollectionsScreenVM.moveMultipleLinksFromLinksTableToArchive()
                                            areElementsSelectable.value = false
                                            specificCollectionsScreenVM.areAllLinksChecked.value =
                                                false
                                            specificCollectionsScreenVM.areAllFoldersChecked.value =
                                                false
                                            specificCollectionsScreenVM.removeAllLinkSelections()
                                            specificCollectionsScreenVM.changeAllFoldersSelectedData(
                                                childFoldersData
                                            )
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Archive,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }

                                SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                                    if ((specificCollectionsScreenVM.selectedLinksID.size + specificCollectionsScreenVM.selectedFoldersData.size != 0)) {
                                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                            shouldDeleteDialogBeVisible.value = true
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null
                                            )
                                        }

                                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                            specificCollectionsScreenVM.moveMultipleLinksFromLinksTableToArchive()
                                            specificCollectionsScreenVM.archiveSelectedMultipleFolders()
                                            areElementsSelectable.value = false
                                            specificCollectionsScreenVM.areAllLinksChecked.value =
                                                false
                                            specificCollectionsScreenVM.areAllFoldersChecked.value =
                                                false
                                            specificCollectionsScreenVM.removeAllLinkSelections()
                                            specificCollectionsScreenVM.changeAllFoldersSelectedData(
                                                childFoldersData
                                            )
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Archive,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }

                                else -> {}
                            }
                        } else {
                            when (SpecificCollectionsScreenVM.screenType.value) {
                                SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                                    if (impLinksData.isNotEmpty()) {
                                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                            shouldSortingBottomSheetAppear.value = true
                                        }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Outlined.Sort,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }

                                SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                                    if (archivedFoldersLinksData.isNotEmpty() || archivedSubFoldersData.isNotEmpty()) {
                                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                            shouldSortingBottomSheetAppear.value = true
                                            coroutineScope.launch {
                                                sortingBtmSheetState.expand()
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Outlined.Sort,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }

                                SpecificScreenType.SAVED_LINKS_SCREEN -> {
                                    if (savedLinksData.isNotEmpty()) {
                                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                            shouldSortingBottomSheetAppear.value = true
                                        }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Outlined.Sort,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }

                                SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                                    if (specificFolderLinksData.isNotEmpty() || childFoldersData.isNotEmpty()) {
                                        IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                                            shouldSortingBottomSheetAppear.value = true
                                            coroutineScope.launch {
                                                sortingBtmSheetState.expand()
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Outlined.Sort,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }

                                SpecificScreenType.INTENT_ACTIVITY -> {

                                }

                                SpecificScreenType.ROOT_SCREEN -> {

                                }
                            }
                        }
                    })
            }
        }) {
            LazyColumn(
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .padding(it)
                    .fillMaxSize()
                    .animateContentSize()
            ) {
                when (SpecificCollectionsScreenVM.screenType.value) {
                    SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                        if (childFoldersData.isNotEmpty()) {
                            itemsIndexed(
                                items = childFoldersData,
                                key = { _, foldersTable ->
                                    foldersTable.id.toString() + foldersTable.folderName
                                }) { folderIndex, folderData ->
                                FolderIndividualComponent(
                                    showCheckBox = areElementsSelectable,
                                    isCheckBoxChecked = mutableStateOf(
                                        specificCollectionsScreenVM.selectedFoldersData.contains(
                                            folderData
                                        )
                                    ),
                                    checkBoxState = { checkBoxState ->
                                        if (checkBoxState) {
                                            specificCollectionsScreenVM.selectedFoldersData.add(
                                                folderData
                                            )
                                        } else {
                                            specificCollectionsScreenVM.selectedFoldersData.removeAll {
                                                it == folderData
                                            }
                                        }
                                    },
                                    folderName = folderData.folderName,
                                    folderNote = folderData.infoForSaving,
                                    onMoreIconClick = {
                                        selectedURLTitle.value = folderData.folderName
                                        selectedURLOrFolderNote.value = folderData.infoForSaving
                                        clickedFolderNote.value = folderData.infoForSaving
                                        coroutineScope.launch {
                                            optionsBtmSheetVM.updateArchiveFolderCardData(folderData.id)
                                        }
                                        clickedFolderName.value = folderData.folderName
                                        CollectionsScreenVM.selectedFolderData.value = folderData
                                        shouldOptionsBtmModalSheetBeVisible.value = true
                                        SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                            OptionsBtmSheetType.FOLDER
                                    },
                                    showMoreIcon = !areElementsSelectable.value,
                                    onFolderClick = { _ ->
                                        if (!areElementsSelectable.value) {
                                            CollectionsScreenVM.currentClickedFolderData.value =
                                                folderData
                                            navController.navigate(NavigationRoutes.SPECIFIC_COLLECTION_SCREEN.name)
                                        }
                                    }, onLongClick = {
                                        if (!areElementsSelectable.value) {
                                            areElementsSelectable.value = true
                                            specificCollectionsScreenVM.areAllFoldersChecked.value =
                                                false
                                            specificCollectionsScreenVM.changeAllFoldersSelectedData()
                                            specificCollectionsScreenVM.selectedFoldersData.add(
                                                folderData
                                            )
                                        }
                                    })
                            }
                        }
                        if (specificFolderLinksData.isNotEmpty()) {
                            itemsIndexed(
                                items = specificFolderLinksData,
                                key = { _, linksTable ->
                                    linksTable.id.toString() + linksTable.webURL + linksTable.baseURL
                                }) { linkIndex, linkData ->
                                LinkUIComponent(
                                    LinkUIComponentParam(
                                        onLongClick = {
                                            if (!areElementsSelectable.value) {
                                                areElementsSelectable.value = true
                                                specificCollectionsScreenVM.selectedLinksID.add(
                                                    linkData.id
                                                )
                                            }
                                        },
                                        isSelectionModeEnabled = areElementsSelectable,
                                        title = linkData.title,
                                        webBaseURL = linkData.baseURL,
                                        imgURL = linkData.imgURL,
                                        onMoreIconCLick = {
                                            CollectionsScreenVM.selectedFolderData.value.id =
                                                linkData.id
                                            SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                                OptionsBtmSheetType.LINK
                                            selectedURLTitle.value = linkData.title
                                            selectedWebURL.value = linkData.webURL
                                            selectedURLOrFolderNote.value = linkData.infoForSaving
                                            tempImpLinkData.apply {
                                                this.webURL.value = linkData.webURL
                                                this.baseURL.value = linkData.baseURL
                                                this.imgURL.value = linkData.imgURL
                                                this.title.value = linkData.title
                                                this.infoForSaving.value = linkData.infoForSaving
                                                this.id = linkData.id
                                            }
                                            tempImpLinkData.webURL.value = linkData.webURL
                                            shouldOptionsBtmModalSheetBeVisible.value = true
                                            coroutineScope.launch {
                                                awaitAll(async {
                                                    optionsBtmSheetVM.updateImportantCardData(
                                                        url = selectedWebURL.value
                                                    )
                                                }, async {
                                                    optionsBtmSheetVM.updateArchiveLinkCardData(
                                                        url = selectedWebURL.value
                                                    )
                                                })
                                            }
                                        },
                                        onLinkClick = {
                                            if (areElementsSelectable.value) {

                                                if (!specificCollectionsScreenVM.selectedLinksID.contains(
                                                        linkData.id
                                                    )
                                                ) {
                                                    specificCollectionsScreenVM.selectedLinksID.add(
                                                        linkData.id
                                                    )
                                                } else {
                                                    specificCollectionsScreenVM.selectedLinksID.remove(
                                                        linkData.id
                                                    )
                                                }
                                            } else {
                                                specificCollectionsScreenVM.onLinkClick(
                                                    recentlyVisited = RecentlyVisited(
                                                        title = linkData.title,
                                                        webURL = linkData.webURL,
                                                        baseURL = linkData.baseURL,
                                                        imgURL = linkData.imgURL,
                                                        infoForSaving = linkData.infoForSaving
                                                    ),
                                                    context = context,
                                                    uriHandler = uriHandler,
                                                    forceOpenInExternalBrowser = false,
                                                    onTaskCompleted = {}
                                                )
                                            }
                                        },
                                        webURL = linkData.webURL,
                                        onForceOpenInExternalBrowserClicked = {
                                            specificCollectionsScreenVM.onLinkClick(
                                                RecentlyVisited(
                                                    title = linkData.title,
                                                    webURL = linkData.webURL,
                                                    baseURL = linkData.baseURL,
                                                    imgURL = linkData.imgURL,
                                                    infoForSaving = linkData.infoForSaving
                                                ),
                                                context = context,
                                                uriHandler = uriHandler,
                                                onTaskCompleted = {},
                                                forceOpenInExternalBrowser = true
                                            )
                                        },
                                        isItemSelected = mutableStateOf(
                                            specificCollectionsScreenVM.selectedLinksID.contains(
                                                linkData.id
                                            )
                                        )
                                    )
                                )
                            }
                        } else {
                            item {
                                DataEmptyScreen(text = "This folder doesn't contain any links. Add links for further usage.")
                            }
                        }
                    }

                    SpecificScreenType.SAVED_LINKS_SCREEN -> {
                        if (savedLinksData.isNotEmpty()) {
                            itemsIndexed(
                                items = savedLinksData,
                                key = { _, linksTable ->
                                    linksTable.baseURL + linksTable.id.toString() + linksTable.webURL
                                }) { linkIndex, linkData ->
                                LinkUIComponent(
                                    LinkUIComponentParam(
                                        onLongClick = {
                                            if (!areElementsSelectable.value) {
                                                areElementsSelectable.value = true
                                                specificCollectionsScreenVM.selectedLinksID.add(
                                                    linkData.id
                                                )
                                            }
                                        },
                                        isSelectionModeEnabled = areElementsSelectable,
                                        title = linkData.title,
                                        webBaseURL = linkData.baseURL,
                                        imgURL = linkData.imgURL,
                                        onMoreIconCLick = {
                                            SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                                OptionsBtmSheetType.LINK
                                            CollectionsScreenVM.selectedFolderData.value.id =
                                                linkData.id
                                            selectedWebURL.value = linkData.webURL
                                            selectedURLOrFolderNote.value = linkData.infoForSaving
                                            tempImpLinkData.apply {
                                                this.webURL.value = linkData.webURL
                                                this.baseURL.value = linkData.baseURL
                                                this.imgURL.value = linkData.imgURL
                                                this.title.value = linkData.title
                                                this.infoForSaving.value = linkData.infoForSaving
                                            }
                                            shouldOptionsBtmModalSheetBeVisible.value = true
                                            coroutineScope.launch {
                                                awaitAll(async {
                                                    optionsBtmSheetVM.updateImportantCardData(
                                                        url = selectedWebURL.value
                                                    )
                                                }, async {
                                                    optionsBtmSheetVM.updateArchiveLinkCardData(
                                                        url = selectedWebURL.value
                                                    )
                                                })
                                            }
                                        },
                                        onLinkClick = {
                                            if (areElementsSelectable.value) {
                                                if (!specificCollectionsScreenVM.selectedLinksID.contains(
                                                        linkData.id
                                                    )
                                                ) {
                                                    specificCollectionsScreenVM.selectedLinksID.add(
                                                        linkData.id
                                                    )
                                                } else {
                                                    specificCollectionsScreenVM.selectedLinksID.remove(
                                                        linkData.id
                                                    )
                                                }
                                            } else {
                                                specificCollectionsScreenVM.onLinkClick(
                                                    RecentlyVisited(
                                                            title = linkData.title,
                                                            webURL = linkData.webURL,
                                                            baseURL = linkData.baseURL,
                                                            imgURL = linkData.imgURL,
                                                            infoForSaving = linkData.infoForSaving
                                                        ),
                                                        context = context,
                                                        uriHandler = uriHandler,
                                                    forceOpenInExternalBrowser = false,
                                                    onTaskCompleted = {}
                                                    )
                                            }
                                        },
                                        webURL = linkData.webURL,
                                        onForceOpenInExternalBrowserClicked = {
                                            specificCollectionsScreenVM.onLinkClick(
                                                RecentlyVisited(
                                                    title = linkData.title,
                                                    webURL = linkData.webURL,
                                                    baseURL = linkData.baseURL,
                                                    imgURL = linkData.imgURL,
                                                    infoForSaving = linkData.infoForSaving
                                                ),
                                                context = context,
                                                uriHandler = uriHandler,
                                                onTaskCompleted = {},
                                                forceOpenInExternalBrowser = true
                                            )
                                        },
                                        isItemSelected = mutableStateOf(
                                            specificCollectionsScreenVM.selectedLinksID.contains(
                                                linkData.id
                                            )
                                        )
                                    )
                                )
                            }
                        } else {
                            item {
                                DataEmptyScreen(text = "No links found. To continue, please add links.")
                            }
                        }
                    }

                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                        if (impLinksData.isNotEmpty()) {
                            itemsIndexed(
                                items = impLinksData,
                                key = { _, importantLinks ->
                                    importantLinks.id.toString() + importantLinks.baseURL + importantLinks.webURL
                                }) { linkIndex, linkData ->
                                LinkUIComponent(
                                    LinkUIComponentParam(
                                        onLongClick = {
                                            if (!areElementsSelectable.value) {
                                                areElementsSelectable.value = true
                                                specificCollectionsScreenVM.selectedImpLinks.add(
                                                    linkData.webURL
                                                )
                                            }
                                        },
                                        isSelectionModeEnabled = areElementsSelectable,
                                        title = linkData.title,
                                        webBaseURL = linkData.baseURL,
                                        imgURL = linkData.imgURL,
                                        onMoreIconCLick = {
                                            SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                                OptionsBtmSheetType.LINK
                                            CollectionsScreenVM.selectedFolderData.value.id =
                                                linkData.id
                                            selectedWebURL.value = linkData.webURL
                                            selectedURLOrFolderNote.value = linkData.infoForSaving
                                            tempImpLinkData.apply {
                                                this.webURL.value = linkData.webURL
                                                this.baseURL.value = linkData.baseURL
                                                this.imgURL.value = linkData.imgURL
                                                this.title.value = linkData.title
                                                this.infoForSaving.value = linkData.infoForSaving
                                            }
                                            shouldOptionsBtmModalSheetBeVisible.value = true
                                            coroutineScope.launch {
                                                awaitAll(
                                                    async {
                                                        optionsBtmSheetVM.updateImportantCardData(
                                                            url = selectedWebURL.value,
                                                        )
                                                    },
                                                    async {
                                                        optionsBtmSheetVM.updateArchiveLinkCardData(
                                                            url = selectedWebURL.value,
                                                        )
                                                    },
                                                )
                                            }
                                        },
                                        onLinkClick = {
                                            if (areElementsSelectable.value) {

                                                if (!specificCollectionsScreenVM.selectedImpLinks.contains(
                                                        linkData.webURL
                                                    )
                                                ) {
                                                    specificCollectionsScreenVM.selectedImpLinks.add(
                                                        linkData.webURL
                                                    )
                                                } else {
                                                    specificCollectionsScreenVM.selectedImpLinks.remove(
                                                        linkData.webURL
                                                    )
                                                }
                                            } else {
                                                specificCollectionsScreenVM.onLinkClick(
                                                    RecentlyVisited(
                                                            title = linkData.title,
                                                            webURL = linkData.webURL,
                                                            baseURL = linkData.baseURL,
                                                            imgURL = linkData.imgURL,
                                                            infoForSaving = linkData.infoForSaving,
                                                        ),
                                                        context = context,
                                                        uriHandler = uriHandler,
                                                    forceOpenInExternalBrowser = false,
                                                    onTaskCompleted = {}
                                                    )
                                            }
                                        },
                                        webURL = linkData.webURL,
                                        onForceOpenInExternalBrowserClicked = {
                                            specificCollectionsScreenVM.onLinkClick(
                                                RecentlyVisited(
                                                    title = linkData.title,
                                                    webURL = linkData.webURL,
                                                    baseURL = linkData.baseURL,
                                                    imgURL = linkData.imgURL,
                                                    infoForSaving = linkData.infoForSaving,
                                                ),
                                                context = context,
                                                uriHandler = uriHandler,
                                                onTaskCompleted = {},
                                                forceOpenInExternalBrowser = true,
                                            )
                                        },
                                        isItemSelected = mutableStateOf(
                                            specificCollectionsScreenVM.selectedImpLinks.contains(
                                                linkData.webURL
                                            )
                                        )
                                    )
                                )
                            }
                        } else {
                            item {
                                DataEmptyScreen(text = "No important links were found. To continue, please add links.")
                            }
                        }
                    }

                    SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                        if (archivedSubFoldersData.isNotEmpty() && !areElementsSelectable.value) {
                            itemsIndexed(
                                items = archivedSubFoldersData,
                                key = { folderIndex, foldersTable ->
                                    foldersTable.folderName + foldersTable.id.toString()
                                }) { folderIndex, folderData ->
                                FolderIndividualComponent(
                                    showCheckBox = areElementsSelectable,
                                    isCheckBoxChecked = mutableStateOf(
                                        specificCollectionsScreenVM.selectedFoldersData.contains(
                                            folderData
                                        )
                                    ),
                                    checkBoxState = { checkBoxState ->
                                        if (checkBoxState) {
                                            specificCollectionsScreenVM.selectedFoldersData.add(
                                                folderData
                                            )
                                        } else {
                                            specificCollectionsScreenVM.selectedFoldersData.removeAll {
                                                it == folderData
                                            }
                                        }
                                    },
                                    folderName = folderData.folderName,
                                    folderNote = folderData.infoForSaving,
                                    onMoreIconClick = {
                                        CollectionsScreenVM.selectedFolderData.value = folderData
                                        selectedURLTitle.value = folderData.folderName
                                        selectedURLOrFolderNote.value = folderData.infoForSaving
                                        clickedFolderNote.value = folderData.infoForSaving
                                        coroutineScope.launch {
                                            optionsBtmSheetVM.updateArchiveFolderCardData(folderData.id)
                                        }
                                        clickedFolderName.value = folderData.folderName
                                        shouldOptionsBtmModalSheetBeVisible.value = true
                                        SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                            OptionsBtmSheetType.FOLDER
                                    },
                                    showMoreIcon = !areElementsSelectable.value,
                                    onLongClick = {
                                        if (!areElementsSelectable.value) {
                                            areElementsSelectable.value = true
                                            specificCollectionsScreenVM.areAllFoldersChecked.value =
                                                false
                                            specificCollectionsScreenVM.changeAllFoldersSelectedData()
                                            specificCollectionsScreenVM.selectedFoldersData.add(
                                                folderData
                                            )
                                        }
                                    },
                                    onFolderClick = { _ ->
                                        if (!areElementsSelectable.value) {
                                            CollectionsScreenVM.currentClickedFolderData.value =
                                                folderData
                                            navController.navigate(NavigationRoutes.SPECIFIC_COLLECTION_SCREEN.name)
                                        }
                                    })
                            }
                        }
                        if (archivedFoldersLinksData.isNotEmpty()) {
                            itemsIndexed(
                                items = archivedFoldersLinksData,
                                key = { _, linksTable ->
                                    linksTable.id.toString() + linksTable.webURL + linksTable.id.toString()
                                }) { linkIndex, linkData ->
                                LinkUIComponent(
                                    LinkUIComponentParam(
                                        onLongClick = {
                                            if (!areElementsSelectable.value) {
                                                areElementsSelectable.value = true
                                                specificCollectionsScreenVM.selectedLinksID.add(
                                                    linkData.id
                                                )
                                            }
                                        },
                                        isSelectionModeEnabled = areElementsSelectable,
                                        title = linkData.title,
                                        webBaseURL = linkData.baseURL,
                                        imgURL = linkData.imgURL,
                                        onMoreIconCLick = {
                                            CollectionsScreenVM.selectedFolderData.value.id =
                                                linkData.id
                                            SpecificCollectionsScreenVM.selectedBtmSheetType.value =
                                                OptionsBtmSheetType.LINK
                                            tempImpLinkData.apply {
                                                this.webURL.value = linkData.webURL
                                                this.baseURL.value = linkData.baseURL
                                                this.imgURL.value = linkData.imgURL
                                                this.title.value = linkData.title
                                                this.infoForSaving.value = linkData.infoForSaving
                                            }
                                            selectedWebURL.value = linkData.webURL
                                            selectedURLOrFolderNote.value = linkData.infoForSaving
                                            shouldOptionsBtmModalSheetBeVisible.value = true
                                        },
                                        onLinkClick = {
                                            if (areElementsSelectable.value) {
                                                if (!specificCollectionsScreenVM.selectedLinksID.contains(
                                                        linkData.id
                                                    )
                                                ) {
                                                    specificCollectionsScreenVM.selectedLinksID.add(
                                                        linkData.id
                                                    )
                                                } else {
                                                    specificCollectionsScreenVM.selectedLinksID.remove(
                                                        linkData.id
                                                    )
                                                }
                                            } else {
                                                specificCollectionsScreenVM.onLinkClick(
                                                    RecentlyVisited(
                                                            title = linkData.title,
                                                            webURL = linkData.webURL,
                                                            baseURL = linkData.baseURL,
                                                            imgURL = linkData.imgURL,
                                                            infoForSaving = linkData.infoForSaving
                                                        ),
                                                        context = context,
                                                        uriHandler = uriHandler,
                                                    forceOpenInExternalBrowser = false,
                                                    onTaskCompleted = {}
                                                    )
                                            }
                                        },
                                        webURL = linkData.webURL,
                                        onForceOpenInExternalBrowserClicked = {
                                            specificCollectionsScreenVM.onLinkClick(
                                                RecentlyVisited(
                                                    title = linkData.title,
                                                    webURL = linkData.webURL,
                                                    baseURL = linkData.baseURL,
                                                    imgURL = linkData.imgURL,
                                                    infoForSaving = linkData.infoForSaving
                                                ),
                                                context = context,
                                                uriHandler = uriHandler,
                                                onTaskCompleted = {},
                                                forceOpenInExternalBrowser = true
                                            )
                                        },
                                        isItemSelected = mutableStateOf(
                                            specificCollectionsScreenVM.selectedLinksID.contains(
                                                linkData.id
                                            )
                                        ),
                                    )
                                )
                            }
                        } else {
                            item {
                                DataEmptyScreen(text = "No links were found in this archived folder.")
                            }
                        }
                    }

                    else -> {}
                }
                item {
                    Spacer(modifier = Modifier.height(175.dp))
                }
            }
            if (shouldScreenTransparencyDecreasedBoxVisible.value) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(0.85f))
                    .clickable {
                        shouldScreenTransparencyDecreasedBoxVisible.value = false
                        coroutineScope
                            .launch {
                                awaitAll(async {
                                    rotationAnimation.animateTo(
                                        -360f, animationSpec = tween(300)
                                    )
                                }, async { isMainFabRotated.value = false })
                            }
                            .invokeOnCompletion {
                                coroutineScope.launch {
                                    rotationAnimation.snapTo(0f)
                                }
                            }
                    })
            }
        }
        MenuBtmSheetUI(
            MenuBtmSheetParam(
                inSpecificArchiveScreen = mutableStateOf(SpecificCollectionsScreenVM.screenType.value == SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN),
                inArchiveScreen = mutableStateOf(SpecificCollectionsScreenVM.screenType.value == SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN),
                btmModalSheetState = btmModalSheetState,
                shouldBtmModalSheetBeVisible = shouldOptionsBtmModalSheetBeVisible,
                btmSheetFor = when (SpecificCollectionsScreenVM.screenType.value) {
                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> OptionsBtmSheetType.IMPORTANT_LINKS_SCREEN
                    SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> SpecificCollectionsScreenVM.selectedBtmSheetType.value
                    SpecificScreenType.SAVED_LINKS_SCREEN -> OptionsBtmSheetType.LINK
                    SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> SpecificCollectionsScreenVM.selectedBtmSheetType.value
                    else -> {
                        OptionsBtmSheetType.LINK
                    }
                },
                onDeleteCardClick = {
                    shouldDeleteDialogBeVisible.value = true
                },
                onRenameClick = {
                    shouldRenameDialogBeVisible.value = true
                },
                forAChildFolder = if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) mutableStateOf(
                    false
                ) else mutableStateOf(true),
                onArchiveClick = {
                    if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        specificCollectionsScreenVM.onArchiveClick(
                            ImportantLinks(
                                title = tempImpLinkData.title.value,
                                webURL = tempImpLinkData.webURL.value,
                                baseURL = tempImpLinkData.baseURL.value,
                                imgURL = tempImpLinkData.imgURL.value,
                                infoForSaving = tempImpLinkData.infoForSaving.value
                            ),
                            context,
                            linkID = tempImpLinkData.id,
                            onTaskCompleted = {
                                specificCollectionsScreenVM.changeRetrievedData(
                                    folderID = CollectionsScreenVM.currentClickedFolderData.value.id,
                                    sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                                        SettingsScreenVM.Settings.selectedSortingType.value
                                    )
                                )
                            },
                        )
                    } else {
                        specificCollectionsScreenVM.onUiEvent(
                            SpecificCollectionsScreenUIEvent.ArchiveAFolder(
                                CollectionsScreenVM.selectedFolderData.value.id
                            )
                        )
                    }
                },
                noteForSaving = selectedURLOrFolderNote.value,
                onNoteDeleteCardClick = {
                    specificCollectionsScreenVM.onNoteDeleteCardClick(
                        selectedWebURL.value,
                        context,
                        folderID = CollectionsScreenVM.selectedFolderData.value.id,
                        folderName = CollectionsScreenVM.selectedFolderData.value.folderName,
                        linkID = CollectionsScreenVM.selectedFolderData.value.id
                    )
                },
                folderName = selectedURLTitle.value,
                linkTitle = tempImpLinkData.title.value
            )
        )
        DeleteDialogBox(
            DeleteDialogBoxParam(
                areFoldersSelectable = areElementsSelectable.value,
                totalIds = mutableLongStateOf(
                    CollectionsScreenVM.selectedFolderData.value.childFolderIDs?.size?.toLong() ?: 0
                ),
                shouldDialogBoxAppear = shouldDeleteDialogBeVisible,
                onDeleteClick = {
                    if (areElementsSelectable.value) {
                        specificCollectionsScreenVM.onDeleteMultipleSelectedFolders()
                        specificCollectionsScreenVM.onDeleteMultipleSelectedLinks()
                        areElementsSelectable.value = false
                        specificCollectionsScreenVM.areAllLinksChecked.value = false
                        specificCollectionsScreenVM.removeAllLinkSelections()
                        specificCollectionsScreenVM.changeRetrievedData(
                            folderID = CollectionsScreenVM.currentClickedFolderData.value.id,
                            sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                                SettingsScreenVM.Settings.selectedSortingType.value
                            )
                        )
                    } else {
                        specificCollectionsScreenVM.onDeleteClick(
                            folderID = CollectionsScreenVM.selectedFolderData.value.id,
                            context = context,
                            onTaskCompleted = {
                                specificCollectionsScreenVM.changeRetrievedData(
                                    folderID = CollectionsScreenVM.currentClickedFolderData.value.id,
                                    sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                                        SettingsScreenVM.Settings.selectedSortingType.value
                                    )
                                )
                            },
                            linkID = CollectionsScreenVM.selectedFolderData.value.id
                        )
                    }
                },
                deleteDialogBoxType = if (areElementsSelectable.value) DataDialogBoxType.SELECTED_DATA else if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.LINK) DataDialogBoxType.LINK else DataDialogBoxType.FOLDER,
                onDeleted = {
                    specificCollectionsScreenVM.changeRetrievedData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        ),
                        folderID = CollectionsScreenVM.currentClickedFolderData.value.id
                    )
                    specificCollectionsScreenVM.selectedImpLinks.clear()
                })
        )
        RenameDialogBox(
            RenameDialogBoxParam(
                shouldDialogBoxAppear = shouldRenameDialogBeVisible,
                existingFolderName = CollectionsScreenVM.selectedFolderData.value.folderName,
                renameDialogBoxFor = SpecificCollectionsScreenVM.selectedBtmSheetType.value,
                onNoteChangeClick = { newNote: String ->
                    if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.FOLDER) {
                        specificCollectionsScreenVM.onUiEvent(
                            SpecificCollectionsScreenUIEvent.UpdateFolderNote(
                                CollectionsScreenVM.selectedFolderData.value.id,
                                newNote
                            )
                        )
                    } else {
                        when (SpecificCollectionsScreenVM.screenType.value) {
                            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> specificCollectionsScreenVM.onUiEvent(
                                SpecificCollectionsScreenUIEvent.UpdateImpLinkNote(
                                    CollectionsScreenVM.selectedFolderData.value.id,
                                    newNote
                                )
                            )

                            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN, SpecificScreenType.SAVED_LINKS_SCREEN, SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> specificCollectionsScreenVM.onUiEvent(
                                SpecificCollectionsScreenUIEvent.UpdateRegularLinkNote(
                                    CollectionsScreenVM.selectedFolderData.value.id,
                                    newNote
                                )
                            )

                            else -> {}
                        }
                    }
                    shouldRenameDialogBeVisible.value = false
                },
                onTitleChangeClick = { newTitle: String ->
                    if (SpecificCollectionsScreenVM.selectedBtmSheetType.value == OptionsBtmSheetType.FOLDER) {
                        specificCollectionsScreenVM.onUiEvent(
                            SpecificCollectionsScreenUIEvent.UpdateFolderName(
                                newTitle,
                                CollectionsScreenVM.selectedFolderData.value.id
                            )
                        )
                    } else {
                        when (SpecificCollectionsScreenVM.screenType.value) {
                            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> specificCollectionsScreenVM.onUiEvent(
                                SpecificCollectionsScreenUIEvent.UpdateImpLinkTitle(
                                    newTitle,
                                    CollectionsScreenVM.selectedFolderData.value.id
                                )
                            )

                            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN, SpecificScreenType.SAVED_LINKS_SCREEN, SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> specificCollectionsScreenVM.onUiEvent(
                                SpecificCollectionsScreenUIEvent.UpdateRegularLinkTitle(
                                    newTitle,
                                    CollectionsScreenVM.selectedFolderData.value.id
                                )
                            )

                            else -> {}
                        }
                    }
                    shouldRenameDialogBeVisible.value = false
                }
            )
        )
        val collectionsScreenVM: CollectionsScreenVM = hiltViewModel()
        AddNewFolderDialogBox(
            AddNewFolderDialogBoxParam(
                shouldDialogBoxAppear = shouldDialogForNewFolderAppear,
                onCreated = {
                    collectionsScreenVM.changeRetrievedFoldersData(
                        sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                            SettingsScreenVM.Settings.selectedSortingType.value
                        )
                    )
                },
                inAChildFolderScreen = true,
                onFolderCreateClick = { folderName, folderNote ->
                    specificCollectionsScreenVM.onUiEvent(
                        SpecificCollectionsScreenUIEvent.CreateANewFolder(
                            FoldersTable(
                                folderName = folderName,
                                infoForSaving = folderNote,
                                parentFolderID = CollectionsScreenVM.currentClickedFolderData.value.id
                            )
                        )
                    )
                }
            )
        )
        AddNewLinkDialogBox(
            shouldDialogBoxAppear = shouldNewLinkDialogBoxBeVisible,
            screenType = SpecificCollectionsScreenVM.screenType.value,
            onSaveClick = { isAutoDetectSelected: Boolean, webURL: String, title: String, note: String, selectedDefaultFolderName: String?, selectedNonDefaultFolderID: Long? ->
                isDataExtractingForTheLink.value = true
                when (SpecificCollectionsScreenVM.screenType.value) {
                    SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                        specificCollectionsScreenVM.onUiEvent(
                            SpecificCollectionsScreenUIEvent.AddANewLinkInAFolder(
                            autoDetectTitle = isAutoDetectSelected,
                            title = title,
                            webURL = webURL,
                            noteForSaving = note,
                                folderID = CollectionsScreenVM.currentClickedFolderData.value.id,
                            onTaskCompleted = {
                                shouldNewLinkDialogBoxBeVisible.value = false
                                isDataExtractingForTheLink.value = false
                            },
                            folderName = CollectionsScreenVM.currentClickedFolderData.value.folderName
                            )
                        )
                    }

                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                        specificCollectionsScreenVM.onUiEvent(SpecificCollectionsScreenUIEvent.AddANewLinkInImpLinks(
                            autoDetectTitle = isAutoDetectSelected,
                            title = title,
                            webURL = webURL,
                            noteForSaving = note,
                            onTaskCompleted = {
                                shouldNewLinkDialogBoxBeVisible.value = false
                                isDataExtractingForTheLink.value = false
                            }
                        ))
                    }

                    SpecificScreenType.SAVED_LINKS_SCREEN -> {
                        specificCollectionsScreenVM.onUiEvent(
                            SpecificCollectionsScreenUIEvent.AddANewLinkInSavedLinks(
                            autoDetectTitle = isAutoDetectSelected,
                            title = title,
                            webURL = webURL,
                            noteForSaving = note,
                            onTaskCompleted = {
                                shouldNewLinkDialogBoxBeVisible.value = false
                                isDataExtractingForTheLink.value = false
                            },
                            )
                        )
                    }

                    else -> {}
                }
            },
            isDataExtractingForTheLink = isDataExtractingForTheLink.value,
            onFolderCreateClick = { folderName, folderNote ->
                specificCollectionsScreenVM.onUiEvent(
                    SpecificCollectionsScreenUIEvent.CreateANewFolder(
                        FoldersTable(folderName = folderName, infoForSaving = folderNote)
                    )
                )
            })
        SortingBottomSheetUI(
            SortingBottomSheetParam(
                shouldBottomSheetVisible = shouldSortingBottomSheetAppear,
                onSelectedAComponent = { sortingPreferences, isLinksSortingSelected, isFoldersSortingSelected ->
                    specificCollectionsScreenVM.changeRetrievedData(
                        sortingPreferences = sortingPreferences,
                        folderID = CollectionsScreenVM.currentClickedFolderData.value.id,
                        isLinksSortingSelected = isLinksSortingSelected,
                        isFoldersSortingSelected = isFoldersSortingSelected
                    )
                },
                bottomModalSheetState = sortingBtmSheetState,
                sortingBtmSheetType = when (SpecificCollectionsScreenVM.screenType.value) {
                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> SortingBtmSheetType.IMPORTANT_LINKS_SCREEN
                    SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> SortingBtmSheetType.ARCHIVE_FOLDER_SCREEN
                    SpecificScreenType.SAVED_LINKS_SCREEN -> SortingBtmSheetType.SAVED_LINKS_SCREEN
                    SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> SortingBtmSheetType.REGULAR_FOLDER_SCREEN
                    SpecificScreenType.INTENT_ACTIVITY -> SortingBtmSheetType.COLLECTIONS_SCREEN
                    SpecificScreenType.ROOT_SCREEN -> SortingBtmSheetType.REGULAR_FOLDER_SCREEN
                },
                shouldFoldersSelectionBeVisible = when (SpecificCollectionsScreenVM.screenType.value) {
                    SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> mutableStateOf(
                        archivedSubFoldersData.isNotEmpty()
                    )

                    SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> mutableStateOf(
                        childFoldersData.isNotEmpty()
                    )

                    else -> mutableStateOf(false)
                },
                shouldLinksSelectionBeVisible = when (SpecificCollectionsScreenVM.screenType.value) {
                    SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> mutableStateOf(
                        archivedFoldersLinksData.isNotEmpty()
                    )

                    SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> mutableStateOf(
                        specificFolderLinksData.isNotEmpty()
                    )

                    else -> mutableStateOf(false)
                },
            )
        )
    }
    BackHandler {
        if (isMainFabRotated.value) {
            shouldScreenTransparencyDecreasedBoxVisible.value = false
            coroutineScope.launch {
                awaitAll(async {
                    rotationAnimation.animateTo(
                        -360f, animationSpec = tween(300)
                    )
                }, async {
                    delay(10L)
                    isMainFabRotated.value = false
                })
            }.invokeOnCompletion {
                coroutineScope.launch {
                    rotationAnimation.snapTo(0f)
                }
            }
        } else if (btmModalSheetState.isVisible) {
            coroutineScope.launch {
                btmModalSheetState.hide()
            }
        } else if (areElementsSelectable.value) {
            areElementsSelectable.value = false
            specificCollectionsScreenVM.areAllLinksChecked.value = false
            specificCollectionsScreenVM.areAllFoldersChecked.value = false
            specificCollectionsScreenVM.removeAllLinkSelections()
            specificCollectionsScreenVM.changeAllFoldersSelectedData(childFoldersData)
            specificCollectionsScreenVM.selectedImpLinks.clear()
        } else {
            if (CollectionsScreenVM.currentClickedFolderData.value.parentFolderID != null
                && (SpecificCollectionsScreenVM.screenType.value == SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                        || SpecificCollectionsScreenVM.screenType.value == SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN)
            ) {
                if (SpecificCollectionsScreenVM.inARegularFolder.value) {
                    SpecificCollectionsScreenVM.screenType.value =
                        SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                } else {
                    SpecificCollectionsScreenVM.screenType.value =
                        SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN
                }
                specificCollectionsScreenVM.updateFolderData(CollectionsScreenVM.currentClickedFolderData.value.parentFolderID!!)
            }
            navController.popBackStack()
        }
    }
}