package com.sakethh.linkora.ui.screens.collections.archiveScreen

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Unarchive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.primaryContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.sakethh.linkora.ui.commonBtmSheets.SortingBottomSheetUI
import com.sakethh.linkora.ui.commonBtmSheets.SortingBottomSheetUIParam
import com.sakethh.linkora.ui.commonBtmSheets.SortingBtmSheetType
import com.sakethh.linkora.ui.commonComposables.DataDialogBoxType
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBox
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.theme.LinkoraTheme
import com.sakethh.linkora.ui.viewmodels.collections.ArchiveScreenVM
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@OptIn(
    ExperimentalPagerApi::class, ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun ParentArchiveScreen(navController: NavController) {
    val pagerState = rememberPagerState()
    val archiveScreenVM: ArchiveScreenVM = viewModel()
    val coroutineScope = rememberCoroutineScope()
    val shouldSortingBottomSheetAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val sortingBtmSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val shouldDeleteDialogBoxAppear = rememberSaveable {
        mutableStateOf(false)
    }
    LinkoraTheme {
        Scaffold(modifier = Modifier.background(MaterialTheme.colorScheme.surface), topBar = {
            TopAppBar(navigationIcon = {
                if (archiveScreenVM.isSelectionModeEnabled.value) {
                    IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                        archiveScreenVM.isSelectionModeEnabled.value = false
                        archiveScreenVM.areAllLinksChecked.value = false
                        archiveScreenVM.areAllFoldersChecked.value = false
                        archiveScreenVM.selectedLinksData.clear()
                        archiveScreenVM.selectedFoldersID.clear()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Cancel, contentDescription = null
                        )
                    }
                }
            }, title = {
                if (archiveScreenVM.isSelectionModeEnabled.value) {
                    Row {
                        AnimatedContent(
                            targetState = archiveScreenVM.selectedLinksData.size + archiveScreenVM.selectedFoldersID.size,
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
                        text = "Archive",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 24.sp
                    )
                }
            }, actions = {
                if (archiveScreenVM.isSelectionModeEnabled.value && archiveScreenVM.selectedFoldersID.size + archiveScreenVM.selectedLinksData.size > 0) {
                    IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                        archiveScreenVM.unArchiveMultipleFolders()
                        archiveScreenVM.unArchiveMultipleSelectedLinks()
                        archiveScreenVM.isSelectionModeEnabled.value = false
                    }) {
                        Icon(imageVector = Icons.Outlined.Unarchive, contentDescription = null)
                    }
                    IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                        shouldDeleteDialogBoxAppear.value = true
                    }) {
                        Icon(imageVector = Icons.Outlined.DeleteForever, contentDescription = null)
                    }
                } else {
                    IconButton(
                        modifier = Modifier.pulsateEffect(),
                        onClick = { shouldSortingBottomSheetAppear.value = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Sort,
                            contentDescription = null
                        )
                    }
                }
            })
        }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                LazyColumn(modifier = Modifier.padding(it)) {
                    stickyHeader {
                        TabRow(selectedTabIndex = pagerState.currentPage) {
                            archiveScreenVM.parentArchiveScreenData.forEachIndexed { index, archiveScreenModal ->
                                Tab(selected = pagerState.currentPage == index, onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }.start()
                                }) {
                                    Text(
                                        text = archiveScreenModal.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(15.dp),
                                        color = if (pagerState.currentPage == index) primaryContentColor else MaterialTheme.colorScheme.onSurface.copy(
                                            0.70f
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalPager(
                    count = archiveScreenVM.parentArchiveScreenData.size, state = pagerState
                ) {
                    archiveScreenVM.parentArchiveScreenData[it].screen(navController = navController)
                }
            }
        }
        SortingBottomSheetUI(
            SortingBottomSheetUIParam(
                shouldBottomSheetVisible = shouldSortingBottomSheetAppear,
                onSelectedAComponent = { sortingPreferences, _, _ ->
                    archiveScreenVM.changeRetrievedData(sortingPreferences = sortingPreferences)
                },
                bottomModalSheetState = sortingBtmSheetState,
                sortingBtmSheetType = SortingBtmSheetType.PARENT_ARCHIVE_SCREEN,
                shouldFoldersSelectionBeVisible = mutableStateOf(false),
                shouldLinksSelectionBeVisible = mutableStateOf(false),
            )
        )

        DeleteDialogBox(
            deleteDialogBoxParam = DeleteDialogBoxParam(
                areFoldersSelectable = true,
                shouldDialogBoxAppear = shouldDeleteDialogBoxAppear,
                deleteDialogBoxType = DataDialogBoxType.FOLDER,
                onDeleteClick = {
                    archiveScreenVM.deleteMultipleSelectedFolders()
                    archiveScreenVM.deleteMultipleSelectedLinks()
                    archiveScreenVM.selectedLinksData.clear()
                    archiveScreenVM.selectedFoldersID.clear()
                    archiveScreenVM.isSelectionModeEnabled.value = false
                }
            )
        )
    }
    BackHandler {
        if (archiveScreenVM.isSelectionModeEnabled.value) {
            archiveScreenVM.isSelectionModeEnabled.value = false
            archiveScreenVM.areAllLinksChecked.value = false
            archiveScreenVM.areAllFoldersChecked.value = false
            archiveScreenVM.selectedLinksData.clear()
            archiveScreenVM.selectedFoldersID.clear()
        } else {
            navController.popBackStack()
        }
    }
}