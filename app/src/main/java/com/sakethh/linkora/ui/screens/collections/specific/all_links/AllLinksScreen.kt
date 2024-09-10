package com.sakethh.linkora.ui.screens.collections.specific.all_links

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
import androidx.compose.material.icons.automirrored.filled.ViewQuilt
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sakethh.linkora.ui.commonComposables.link_views.LinkUIComponentParam
import com.sakethh.linkora.ui.commonComposables.link_views.components.GridViewLinkUIComponent
import com.sakethh.linkora.ui.commonComposables.link_views.components.ListViewLinkUIComponent
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.navigation.NavigationRoutes
import com.sakethh.linkora.ui.screens.link_view.LinkView
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.composables.SpecificScreenScaffold

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun AllLinksScreen(navController: NavController) {
    val allLinksScreenVM: AllLinksScreenVM = hiltViewModel()
    val savedLinks = allLinksScreenVM.savedLinks.collectAsStateWithLifecycle(emptyList())
    SpecificScreenScaffold(topAppBarText = "All Links", navController = navController, actions = {
        IconButton(modifier = Modifier.pulsateEffect(),
            onClick = { /*shouldSortingBottomSheetAppear.value = true*/ }) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Sort, contentDescription = null
            )
        }
        IconButton(onClick = {
            navController.navigate(NavigationRoutes.LINK_VIEW_SETTINGS.name)
        }) {
            Icon(Icons.AutoMirrored.Filled.ViewQuilt, null)
        }
    }, bottomBar = {
        LinksSelectionChips(allLinksScreenVM)
    }) { paddingValues, topAppBarScrollBehaviour ->
        val commonModifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .nestedScroll(topAppBarScrollBehaviour.nestedScrollConnection)

        when (SettingsPreference.currentlySelectedLinkView.value) {
            LinkView.REGULAR_LIST_VIEW.name, LinkView.TITLE_ONLY_LIST_VIEW.name -> {
                LazyColumn(
                    modifier = commonModifier
                ) {
                    items(savedLinks.value) {
                        ListViewLinkUIComponent(
                            linkUIComponentParam = LinkUIComponentParam(title = it.title,
                                webBaseURL = it.baseURL,
                                imgURL = it.imgURL,
                                onMoreIconClick = {},
                                onLinkClick = {},
                                webURL = it.webURL,
                                onForceOpenInExternalBrowserClicked = { },
                                isSelectionModeEnabled = mutableStateOf(false),
                                isItemSelected = mutableStateOf(false),
                                onLongClick = {

                                }),
                            forTitleOnlyView = SettingsPreference.currentlySelectedLinkView.value == LinkView.TITLE_ONLY_LIST_VIEW.name
                        )
                    }
                }
            }

            LinkView.GRID_VIEW.name -> {
                LazyVerticalGrid(columns = GridCells.Adaptive(150.dp), modifier = commonModifier) {
                    items(savedLinks.value) {
                        GridViewLinkUIComponent(
                            linkUIComponentParam = LinkUIComponentParam(title = it.title,
                                webBaseURL = it.baseURL,
                                imgURL = it.imgURL,
                                onMoreIconClick = {},
                                onLinkClick = {},
                                webURL = it.webURL,
                                onForceOpenInExternalBrowserClicked = { },
                                isSelectionModeEnabled = mutableStateOf(false),
                                isItemSelected = mutableStateOf(false),
                                onLongClick = {

                                }),
                            forStaggeredView = SettingsPreference.currentlySelectedLinkView.value == LinkView.STAGGERED_VIEW.name
                        )
                    }
                }
            }

            LinkView.STAGGERED_VIEW.name -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(150.dp), modifier = commonModifier
                ) {
                    items(savedLinks.value) {
                        GridViewLinkUIComponent(
                            linkUIComponentParam = LinkUIComponentParam(title = it.title,
                                webBaseURL = it.baseURL,
                                imgURL = it.imgURL,
                                onMoreIconClick = {},
                                onLinkClick = {},
                                webURL = it.webURL,
                                onForceOpenInExternalBrowserClicked = { },
                                isSelectionModeEnabled = mutableStateOf(false),
                                isItemSelected = mutableStateOf(false),
                                onLongClick = {

                                }),
                            forStaggeredView = SettingsPreference.currentlySelectedLinkView.value == LinkView.STAGGERED_VIEW.name
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LinksSelectionChips(allLinksScreenVM: AllLinksScreenVM) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(BottomAppBarDefaults.ContainerElevation))
    ) {
        Text(
            "Filter based on",
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
                FilterChip(modifier = Modifier.animateContentSize(), onClick = {
                    it.isChecked.value = !it.isChecked.value
                }, selected = it.isChecked.value, label = {
                    Text(it.linkType, style = MaterialTheme.typography.titleSmall)
                })
                Spacer(Modifier.width(10.dp))
            }
        }
    }
}