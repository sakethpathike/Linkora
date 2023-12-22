package com.sakethh.linkora.btmSheet

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.screens.collections.FolderIndividualComponent
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.launch


enum class SortingBtmSheetType {
    PARENT_HOME_SCREEN, COLLECTIONS_SCREEN, HISTORY_SCREEN, REGULAR_FOLDER_SCREEN, ARCHIVE_FOLDER_SCREEN, PARENT_ARCHIVE_SCREEN
}

enum class SelectedSortingTypes {
    LINKS, FOLDERS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortingBottomSheetUI(
    shouldBottomSheetVisible: MutableState<Boolean>,
    onSelectedAComponent: (SettingsScreenVM.SortingPreferences) -> Unit,
    bottomModalSheetState: SheetState,
    sortingBtmSheetType: SortingBtmSheetType,
    selectedSortingType: (linksSorting: SelectedSortingTypes, foldersSorting: SelectedSortingTypes) -> Unit = { _, _ -> }
) {
    val isFoldersSortingSelected = rememberSaveable {
        mutableStateOf(true)
    }
    val isLinksSortingSelected = rememberSaveable {
        mutableStateOf(true)
    }
    val coroutineScope = rememberCoroutineScope()
    val sortingBtmSheetVM: SortingBtmSheetVM = viewModel()
    val context = LocalContext.current
    LinkoraTheme {
        if (shouldBottomSheetVisible.value) {
            ModalBottomSheet(
                modifier = Modifier.animateContentSize(),
                sheetState = bottomModalSheetState,
                onDismissRequest = {
                    coroutineScope.launch {
                        bottomModalSheetState.hide()
                    }
                    shouldBottomSheetVisible.value = false
                }) {
                androidx.compose.material3.Text(
                    text = when (sortingBtmSheetType) {
                        SortingBtmSheetType.PARENT_HOME_SCREEN -> "Sort links by"
                        SortingBtmSheetType.COLLECTIONS_SCREEN -> "Sort folders by"
                        SortingBtmSheetType.HISTORY_SCREEN -> "Sort history links by"
                        SortingBtmSheetType.ARCHIVE_FOLDER_SCREEN -> "Sort based on"
                        SortingBtmSheetType.REGULAR_FOLDER_SCREEN -> "Sort based on"
                        else -> "Sort by"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 15.dp)
                )
                if (sortingBtmSheetType == SortingBtmSheetType.REGULAR_FOLDER_SCREEN || sortingBtmSheetType == SortingBtmSheetType.ARCHIVE_FOLDER_SCREEN) {
                    FolderIndividualComponent(
                        folderName = "Folders",
                        folderNote = "",
                        onMoreIconClick = { /*TODO*/ },
                        onFolderClick = {},
                        showMoreIcon = false,
                        showCheckBox = true,
                        checkBoxState = {
                            isFoldersSortingSelected.value = it
                        }
                    )
                    FolderIndividualComponent(
                        folderName = "Links",
                        folderNote = "",
                        folderIcon = Icons.Default.Link,
                        onMoreIconClick = { /*TODO*/ },
                        onFolderClick = { /*TODO*/ },
                        showMoreIcon = false,
                        showCheckBox = true,
                        checkBoxState = {
                            isLinksSortingSelected.value = it
                        }
                    )
                    androidx.compose.material3.Text(
                        text = "Sort by",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 15.dp, top = 20.dp)
                    )
                }
                sortingBtmSheetVM.sortingBottomSheetData(context).forEach {
                    Column(
                        modifier = Modifier
                            .clickable {
                                it.onClick()
                                onSelectedAComponent(it.sortingType)
                                coroutineScope.launch {
                                    bottomModalSheetState.hide()
                                    shouldBottomSheetVisible.value = false
                                }
                            }
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(start = 15.dp)
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.material3.Text(
                                text = it.sortingName,
                                fontSize = 16.sp,
                                style = MaterialTheme.typography.titleSmall,
                                color = if (it.sortingType == SettingsScreenVM.SortingPreferences.valueOf(
                                        SettingsScreenVM.Settings.selectedSortingType.value
                                    )
                                ) MaterialTheme.colorScheme.primary else LocalTextStyle.current.color
                            )
                            RadioButton(
                                selected = it.sortingType.name == SettingsScreenVM.Settings.selectedSortingType.value,
                                onClick = {
                                    it.onClick()
                                    onSelectedAComponent(it.sortingType)
                                    coroutineScope.launch {
                                        bottomModalSheetState.hide()
                                        shouldBottomSheetVisible.value = false
                                    }
                                }, modifier = Modifier.padding(end = 5.dp)
                            )
                        }
                    }
                }
                Spacer(
                    modifier = Modifier
                        .navigationBarsPadding()
                )
            }
        }
    }
}