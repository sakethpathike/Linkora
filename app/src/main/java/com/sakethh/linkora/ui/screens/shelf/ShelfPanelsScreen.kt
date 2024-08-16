package com.sakethh.linkora.ui.screens.shelf

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ViewArray
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.LocalizedStrings.noPanelsFound
import com.sakethh.linkora.LocalizedStrings.panelsInTheShelf
import com.sakethh.linkora.data.local.Shelf
import com.sakethh.linkora.ui.bottomSheets.menu.IndividualMenuComponent
import com.sakethh.linkora.ui.commonComposables.AddANewPanelInShelfDialogBox
import com.sakethh.linkora.ui.commonComposables.AddANewShelfParam
import com.sakethh.linkora.ui.commonComposables.DeleteAShelfDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.DeleteAShelfPanelDialogBox
import com.sakethh.linkora.ui.commonComposables.RenameAShelfPanelDialogBox
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.ShelfBtmSheetVM
import com.sakethh.linkora.ui.navigation.NavigationRoutes
import com.sakethh.linkora.ui.screens.DataEmptyScreen
import com.sakethh.linkora.ui.screens.home.HomeScreenVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelfPanelsScreen(navController: NavController) {
    val shelfBtmSheetVM: ShelfBtmSheetVM = hiltViewModel()
    val shelfData = shelfBtmSheetVM.shelfData.collectAsStateWithLifecycle().value
    val isDeleteAShelfDialogBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val isRenameAShelfDialogBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val isAddANewShelfDialogBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val topAppBarState = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        BottomAppBar {
            Button(
                modifier = Modifier
                    .padding(15.dp)
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .pulsateEffect(0.9f),
                onClick = {
                    isAddANewShelfDialogBoxVisible.value = true
                }) {
                Text(
                    text = LocalizedStrings.addNewPanelToShelf.value,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp
                )
            }
        }
    }, topBar = {
        MediumTopAppBar(navigationIcon = {
            IconButton(onClick = {
                navController.navigateUp()
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
            }
        }, scrollBehavior = topAppBarState, title = {
            Text(
                text = panelsInTheShelf.value,
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium,
            )
        })
    }) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .animateContentSize()
                .nestedScroll(topAppBarState.nestedScrollConnection)
        ) {
            if (shelfData.isNotEmpty()) {
                items(shelfData) {
                    IndividualMenuComponent(
                        onRenameIconClick = {
                            ShelfBtmSheetVM.selectedShelfData = it
                            isRenameAShelfDialogBoxVisible.value = true
                        },
                        onOptionClick = {
                            ShelfBtmSheetVM.selectedShelfData = it
                            navController.navigate(NavigationRoutes.SPECIFIC_PANEL_SCREEN.name)
                        },
                        elementName = "0",
                        elementImageVector = Icons.Default.ViewArray,
                        inShelfUI = true,
                        onDeleteIconClick = {
                            ShelfBtmSheetVM.selectedShelfData = it
                            isDeleteAShelfDialogBoxVisible.value = true
                        },
                        onTuneIconClick = {
                            ShelfBtmSheetVM.selectedShelfData = it
                            navController.navigate(NavigationRoutes.SPECIFIC_PANEL_SCREEN.name)
                        }
                    )
                }
            } else {
                item {
                    DataEmptyScreen(text = noPanelsFound.value)
                }
            }
        }
    }
    AddANewPanelInShelfDialogBox(
        addANewShelfParam = AddANewShelfParam(
            isDialogBoxVisible = isAddANewShelfDialogBoxVisible,
            onCreateClick = { shelfName, shelfIconName ->
                shelfBtmSheetVM.onShelfUiEvent(
                    ShelfUIEvent.AddANewShelf(
                        Shelf(
                            shelfName = shelfName,
                            shelfIconName = shelfIconName,
                            folderIds = emptyList()
                        )
                    )
                )
            }
        )
    )

    DeleteAShelfPanelDialogBox(
        deleteAShelfDialogBoxParam = DeleteAShelfDialogBoxParam(
            isDialogBoxVisible = isDeleteAShelfDialogBoxVisible,
            onDeleteClick = { ->
                shelfBtmSheetVM.onShelfUiEvent(
                    ShelfUIEvent.DeleteAShelf(
                        ShelfBtmSheetVM.selectedShelfData
                    )
                )
            }
        )
    )
    RenameAShelfPanelDialogBox(
        isDialogBoxVisible = isRenameAShelfDialogBoxVisible,
        onRenameClick = {
            shelfBtmSheetVM.onShelfUiEvent(
                ShelfUIEvent.UpdateAShelfName(
                    it, ShelfBtmSheetVM.selectedShelfData.id
                )
            )
        }
    )
    BackHandler {
        HomeScreenVM.initialStart = true
        navController.navigateUp()
    }
}