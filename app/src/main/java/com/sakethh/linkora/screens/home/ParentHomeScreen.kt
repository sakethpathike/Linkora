package com.sakethh.linkora.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.sakethh.linkora.btmSheet.SortingBottomSheetUI
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreenType
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreenVM
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
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
    val currentPhaseOfTheDay =
        rememberSaveable(inputs = arrayOf(homeScreenVM.currentPhaseOfTheDay.value)) {
            homeScreenVM.currentPhaseOfTheDay.value
        }
    LinkoraTheme {
        Scaffold(modifier = Modifier.background(MaterialTheme.colorScheme.surface), topBar = {
            TopAppBar(title = {
                Text(
                    text = currentPhaseOfTheDay,
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
        }
        SortingBottomSheetUI(
            shouldBottomSheetVisible = shouldSortingBottomSheetAppear,
            onSelectedAComponent = {
                specificScreenVM.changeRetrievedData(
                    sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                        SettingsScreenVM.Settings.selectedSortingType.value
                    ), folderName = "", screenType = SpecificScreenType.SAVED_LINKS_SCREEN
                )
                specificScreenVM.changeRetrievedData(
                    sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                        SettingsScreenVM.Settings.selectedSortingType.value
                    ), folderName = "", screenType = SpecificScreenType.IMPORTANT_LINKS_SCREEN
                )
            },
            bottomModalSheetState = sortingBtmSheetState
        )
    }
}