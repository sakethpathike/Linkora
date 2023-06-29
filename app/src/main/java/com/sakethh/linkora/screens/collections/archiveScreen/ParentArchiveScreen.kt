package com.sakethh.linkora.screens.collections.archiveScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.sakethh.linkora.ui.theme.LinkoraTheme

@OptIn(
    ExperimentalPagerApi::class, ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun ParentArchiveScreen(navController: NavController) {
    val pagerState = rememberPagerState()
    val archiveScreenVM: ArchiveScreenVM = viewModel()
    LinkoraTheme {
        Scaffold(modifier = Modifier.background(MaterialTheme.colorScheme.surface), topBar = {
            TopAppBar(title = {
                Text(
                    text = "Archive", style = MaterialTheme.typography.titleLarge, fontSize = 24.sp
                )
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

                                }) {
                                    Text(
                                        text = archiveScreenModal.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(15.dp),
                                        color = if (pagerState.currentPage == index) TabRowDefaults.contentColor else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalPager(
                    count = archiveScreenVM.parentArchiveScreenData.size, state = pagerState
                ) {
                    archiveScreenVM.parentArchiveScreenData[it].screen(navController=navController)
                }
            }
        }
    }
}