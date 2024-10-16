package com.sakethh.linkora.ui.screens.shelf

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.ShelfBtmSheetVM
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
    val context = LocalContext.current
    /*Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
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
                        elementName = it.shelfName,
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
                    ShelfUIEvent.DeleteAPanel(
                        ShelfBtmSheetVM.selectedShelfData
                    )
                )
                if (SettingsPreference.lastSelectedPanelID.longValue == ShelfBtmSheetVM.selectedShelfData.id) {
                    SettingsPreference.lastSelectedPanelID.longValue = -1
                    SettingsPreference.changeSettingPreferenceValue(
                        intPreferencesKey(SettingsPreferences.LAST_SELECTED_PANEL_ID.name),
                        context.dataStore,
                        newValue = -1
                    )
                }
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
    )*/
    BackHandler {
        HomeScreenVM.initialStart = true
        navController.navigateUp()
    }
}