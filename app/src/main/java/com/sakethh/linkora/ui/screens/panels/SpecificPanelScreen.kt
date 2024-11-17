package com.sakethh.linkora.ui.screens.panels

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.LocalizedStrings.foldersListedInThisPanel
import com.sakethh.linkora.LocalizedStrings.noFoldersFoundInThisPanel
import com.sakethh.linkora.LocalizedStrings.youCanAddTheFollowingFoldersToThisPanel
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.navigation.HomeScreenRoute
import com.sakethh.linkora.ui.screens.DataEmptyScreen
import com.sakethh.linkora.ui.screens.home.HomeScreenVM

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SpecificPanelScreen(navController: NavController) {

    val panelsScreenVM: PanelsScreenVM = hiltViewModel()

    val foldersOfTheSelectedPanel =
        panelsScreenVM.foldersOfTheSelectedPanel.collectAsStateWithLifecycle()

    val rootFolders = panelsScreenVM.rootFolders.collectAsStateWithLifecycle()
    val topAppBarState = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        MediumTopAppBar(navigationIcon = {
            IconButton(onClick = {
                navController.navigateUp()
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
            }
        }, scrollBehavior = topAppBarState, title = {
            Text(
                text = PanelsScreenVM.selectedPanelData.panelName,
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium,
            )
        })
    }, floatingActionButton = {
        FloatingActionButton(onClick = {
            HomeScreenVM.initialStart = true
            navController.navigate(HomeScreenRoute)
        }) {
            Icon(imageVector = Icons.Default.Home, contentDescription = "")
        }
    }) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .animateContentSize()
                .nestedScroll(topAppBarState.nestedScrollConnection)
        ) {
            stickyHeader {
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(15.dp)
                ) {
                    Text(
                        text = LocalizedStrings.panels.value,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 16.sp,
                        modifier = Modifier.clickable {
                            navController.navigateUp()
                        })
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                        contentDescription = ""
                    )
                    Text(
                        text = PanelsScreenVM.selectedPanelData.panelName,
                        style = MaterialTheme.typography.titleMedium, fontSize = 16.sp
                    )
                }
                HorizontalDivider(color = LocalContentColor.current.copy(0.25f))
            }
            if (foldersOfTheSelectedPanel.value.distinct().isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = foldersListedInThisPanel.value,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(start = 10.dp, end = 15.dp)
                    )
                }
                items(foldersOfTheSelectedPanel.value.distinct()) { folderItem ->
                    FolderComponentInSpecificPanelScreen(
                        folderName = folderItem.folderName,
                        {},
                        {},
                        onRemoveClick = {
                            panelsScreenVM.onUiEvent(
                                PanelScreenUIEvent.DeleteAPanelFolder(
                                    folderId = folderItem.id,
                                    panelId = PanelsScreenVM.selectedPanelData.panelId
                                )
                            )
                        },
                        onAddClick = {},
                        shouldAddIconBeVisible = false,
                        shouldMoveUpIconVisible = false,
                        shouldMoveDownIconVisible = false
                    )
                }
            } else {
                item {
                    DataEmptyScreen(text = noFoldersFoundInThisPanel.value)
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
            if (rootFolders.value.size != foldersOfTheSelectedPanel.value.distinct().size) {
                item {
                    HorizontalDivider(
                        color = LocalContentColor.current.copy(0.1f)
                    )
                }
                item {
                    Text(
                        text = youCanAddTheFollowingFoldersToThisPanel.value,
                        lineHeight = 20.sp,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(start = 10.dp, end = 15.dp, top = 15.dp)
                    )
                }
            }
            items(rootFolders.value.filterNot {
                foldersOfTheSelectedPanel.value.map { it.folderId }.contains(it.id)
            }) { rootFolderElement ->
                    FolderComponentInSpecificPanelScreen(
                        folderName = rootFolderElement.folderName,
                        {},
                        {},
                        {},
                        onAddClick = {
                            panelsScreenVM.onUiEvent(
                                PanelScreenUIEvent.AddANewPanelFolder(
                                    folderName = rootFolderElement.folderName,
                                    folderID = rootFolderElement.id,
                                    connectedPanelId = PanelsScreenVM.selectedPanelData.panelId
                                )
                            )
                        },
                        shouldAddIconBeVisible = true,
                        shouldMoveUpIconVisible = false,
                        shouldMoveDownIconVisible = false
                    )
            }

            item {
                Spacer(modifier = Modifier.height(150.dp))
            }
        }
    }
}

@Composable
private fun FolderComponentInSpecificPanelScreen(
    folderName: String,
    onMoveUpClick: () -> Unit,
    onMoveDownClick: () -> Unit,
    onRemoveClick: () -> Unit,
    onAddClick: () -> Unit,
    shouldAddIconBeVisible: Boolean,
    shouldMoveUpIconVisible: Boolean,
    shouldMoveDownIconVisible: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pulsateEffect()
            .clickable(interactionSource = remember {
                MutableInteractionSource()
            }, indication = null, onClick = {
                if (shouldAddIconBeVisible) {
                    onAddClick()
                }
            })
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                modifier = Modifier
                    .padding(15.dp)
                    .size(28.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.40f),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = folderName,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 15.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row {
                    if (!shouldAddIconBeVisible) {
                        if (shouldMoveUpIconVisible) {
                            IconButton(onClick = {
                                onMoveUpClick()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null
                                )
                            }
                        }
                        if (shouldMoveDownIconVisible) {
                            IconButton(onClick = {
                                onMoveDownClick()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = null
                                )
                            }
                        }
                        IconButton(onClick = {
                            onRemoveClick()
                        }) {
                            Icon(
                                imageVector = Icons.Default.RemoveCircle,
                                contentDescription = null
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            onAddClick()
                        }) {
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}