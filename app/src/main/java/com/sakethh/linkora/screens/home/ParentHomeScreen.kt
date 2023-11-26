package com.sakethh.linkora.screens.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.sakethh.linkora.btmSheet.NewLinkBtmSheet
import com.sakethh.linkora.btmSheet.NewLinkBtmSheetUIParam
import com.sakethh.linkora.btmSheet.SortingBottomSheetUI
import com.sakethh.linkora.customComposables.AddNewFolderDialogBox
import com.sakethh.linkora.customComposables.AddNewFolderDialogBoxParam
import com.sakethh.linkora.customComposables.AddNewLinkDialogBox
import com.sakethh.linkora.customComposables.FloatingActionBtn
import com.sakethh.linkora.customComposables.FloatingActionBtnParam
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreenType
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreenVM
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalPagerApi::class, ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun ParentHomeScreen(navController: NavController) {
    val pagerState = rememberPagerState()
    val homeScreenVM: HomeScreenVM = viewModel()
    val specificScreenVM: SpecificScreenVM = viewModel()
    val coroutineScope = rememberCoroutineScope()
    val shouldSortingBottomSheetAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val sortingBtmSheetState = rememberModalBottomSheetState()
    val activity = LocalContext.current as? Activity
    val btmModalSheetStateForSavingLinks =
        rememberModalBottomSheetState()
    val shouldDialogForNewLinkAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldDialogForNewFolderAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldBtmSheetForNewLinkAdditionBeEnabled = rememberSaveable {
        mutableStateOf(false)
    }
    val isMainFabRotated = rememberSaveable {
        mutableStateOf(false)
    }
    val rotationAnimation = remember {
        Animatable(0f)
    }
    val shouldScreenTransparencyDecreasedBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }
    LinkoraTheme {
        Scaffold(
            floatingActionButton = {
                FloatingActionBtn(
                    FloatingActionBtnParam(
                        newLinkBottomModalSheetState = btmModalSheetStateForSavingLinks,
                        shouldBtmSheetForNewLinkAdditionBeEnabled = shouldBtmSheetForNewLinkAdditionBeEnabled,
                        shouldScreenTransparencyDecreasedBoxVisible = shouldScreenTransparencyDecreasedBoxVisible,
                        shouldDialogForNewFolderAppear = shouldDialogForNewFolderAppear,
                        shouldDialogForNewLinkAppear = shouldDialogForNewLinkAppear,
                        isMainFabRotated = isMainFabRotated,
                        rotationAnimation = rotationAnimation,
                        inASpecificScreen = false
                    )
                )
            },
            floatingActionButtonPosition = FabPosition.End,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            topBar = {
                TopAppBar(title = {
                    Text(
                        text = homeScreenVM.currentPhaseOfTheDay.value,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 24.sp
                    )
                }, actions = {
                    IconButton(onClick = { shouldSortingBottomSheetAppear.value = true }) {
                        Icon(imageVector = Icons.Outlined.Sort, contentDescription = null)
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
                            homeScreenVM.parentHomeScreenData.forEachIndexed { index, archiveScreenModal ->
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
                                        color = if (pagerState.currentPage == index) TabRowDefaults.contentColor else MaterialTheme.colorScheme.onSurface.copy(
                                            0.70f
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalPager(
                    count = homeScreenVM.parentHomeScreenData.size, state = pagerState
                ) {
                    homeScreenVM.parentHomeScreenData[it].screen(navController = navController)
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
        SortingBottomSheetUI(
            shouldBottomSheetVisible = shouldSortingBottomSheetAppear,
            onSelectedAComponent = {
                specificScreenVM.changeRetrievedData(
                    sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                        SettingsScreenVM.Settings.selectedSortingType.value
                    ), folderID = 0, screenType = SpecificScreenType.SAVED_LINKS_SCREEN
                )
                specificScreenVM.changeRetrievedData(
                    sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                        SettingsScreenVM.Settings.selectedSortingType.value
                    ), folderID = 0, screenType = SpecificScreenType.IMPORTANT_LINKS_SCREEN
                )
            },
            bottomModalSheetState = sortingBtmSheetState
        )
        AddNewLinkDialogBox(
            shouldDialogBoxAppear = shouldDialogForNewLinkAppear,
            screenType = SpecificScreenType.ROOT_SCREEN,
            specificFolderName = "Tea || Coffee ?",
            parentFolderID = null,
            childFoldersIDs = emptyList()
        )

        AddNewFolderDialogBox(
            AddNewFolderDialogBoxParam(
                shouldDialogBoxAppear = shouldDialogForNewFolderAppear,
                parentFolderID = null,
                childFolderIDs = emptyList(),
                currentFolderID = 0
            )
        )
        NewLinkBtmSheet(
            NewLinkBtmSheetUIParam(
                btmSheetState = btmModalSheetStateForSavingLinks,
                inIntentActivity = false,
                screenType = SpecificScreenType.ROOT_SCREEN,
                shouldUIBeVisible = shouldBtmSheetForNewLinkAdditionBeEnabled,
                onLinkSaved = {},
                onFolderCreated = {},
                parentFolderID = null, childFolderIDs = emptyList()
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
        } else {
            activity?.finish()
        }
    }
}