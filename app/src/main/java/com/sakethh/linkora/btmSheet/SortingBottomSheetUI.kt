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
import androidx.compose.runtime.LaunchedEffect
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
    PARENT_HOME_SCREEN, COLLECTIONS_SCREEN, HISTORY_SCREEN, REGULAR_FOLDER_SCREEN, ARCHIVE_FOLDER_SCREEN, PARENT_ARCHIVE_SCREEN, SAVED_LINKS_SCREEN, IMPORTANT_LINKS_SCREEN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortingBottomSheetUI(
    shouldBottomSheetVisible: MutableState<Boolean>,
    onSelectedAComponent: (SettingsScreenVM.SortingPreferences, isLinksSortingSelected: Boolean, isFoldersSortingSelected: Boolean) -> Unit,
    bottomModalSheetState: SheetState,
    sortingBtmSheetType: SortingBtmSheetType
) {
    val coroutineScope = rememberCoroutineScope()
    val sortingBtmSheetVM: SortingBtmSheetVM = viewModel()
    val context = LocalContext.current
    LaunchedEffect(key1 = shouldBottomSheetVisible.value) {
        if (shouldBottomSheetVisible.value) {
            bottomModalSheetState.expand()
        }
    }
    val linksSortingSelectedState = rememberSaveable {
        mutableStateOf(true)
    }
    val foldersSortingSelectedState = rememberSaveable {
        mutableStateOf(true)
    }
    LinkoraTheme {
        if (shouldBottomSheetVisible.value) {
            val didAnyCheckBoxStateChanged = rememberSaveable {
                mutableStateOf(false)
            }
            ModalBottomSheet(
                sheetState = bottomModalSheetState,
                onDismissRequest = {
                    coroutineScope.launch {
                        bottomModalSheetState.hide()
                    }
                    shouldBottomSheetVisible.value = false
                }) {
                Column(
                    modifier = Modifier.animateContentSize()
                ) {
                    androidx.compose.material3.Text(
                        text = when (sortingBtmSheetType) {
                            SortingBtmSheetType.PARENT_HOME_SCREEN -> "Sort links by"
                            SortingBtmSheetType.COLLECTIONS_SCREEN -> "Sort folders by"
                            SortingBtmSheetType.HISTORY_SCREEN -> "Sort History Links by"
                            SortingBtmSheetType.ARCHIVE_FOLDER_SCREEN -> "Sort based on"
                            SortingBtmSheetType.REGULAR_FOLDER_SCREEN -> "Sort based on"
                            SortingBtmSheetType.PARENT_ARCHIVE_SCREEN -> "Sort by"
                            SortingBtmSheetType.SAVED_LINKS_SCREEN -> "Sort Saved Links by"
                            SortingBtmSheetType.IMPORTANT_LINKS_SCREEN -> "Sort Important Links by"
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
                            onMoreIconClick = { },
                            onFolderClick = {
                                didAnyCheckBoxStateChanged.value = true
                            },
                            showMoreIcon = false,
                            showCheckBox = true,
                            checkBoxState = {
                                didAnyCheckBoxStateChanged.value = true
                                foldersSortingSelectedState.value = it
                            },
                            isCheckBoxChecked = foldersSortingSelectedState
                        )
                        FolderIndividualComponent(
                            folderName = "Links",
                            folderNote = "",
                            folderIcon = Icons.Default.Link,
                            onMoreIconClick = { },
                            onFolderClick = {
                                didAnyCheckBoxStateChanged.value = true
                            },
                            showMoreIcon = false,
                            showCheckBox = true,
                            checkBoxState = {
                                linksSortingSelectedState.value = it
                                didAnyCheckBoxStateChanged.value = true
                            },
                            isCheckBoxChecked = linksSortingSelectedState
                        )
                    }
                    @Composable
                    fun SortByUI() {
                        sortingBtmSheetVM.sortingBottomSheetData(context).forEach {
                            Column(
                                modifier = Modifier
                                    .clickable {
                                        onSelectedAComponent(
                                            it.sortingType, linksSortingSelectedState.value,
                                            foldersSortingSelectedState.value
                                        )
                                        it.onClick()
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
                                            ) && !didAnyCheckBoxStateChanged.value
                                        ) MaterialTheme.colorScheme.primary else LocalTextStyle.current.color
                                    )
                                    RadioButton(
                                        selected = it.sortingType.name == SettingsScreenVM.Settings.selectedSortingType.value && !didAnyCheckBoxStateChanged.value,
                                        onClick = {
                                            onSelectedAComponent(
                                                it.sortingType, linksSortingSelectedState.value,
                                                foldersSortingSelectedState.value
                                            )
                                            it.onClick()
                                            coroutineScope.launch {
                                                bottomModalSheetState.hide()
                                                shouldBottomSheetVisible.value = false
                                            }
                                        }, modifier = Modifier.padding(end = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                    if (((sortingBtmSheetType == SortingBtmSheetType.REGULAR_FOLDER_SCREEN && foldersSortingSelectedState.value) || (sortingBtmSheetType == SortingBtmSheetType.REGULAR_FOLDER_SCREEN && linksSortingSelectedState.value)) || ((sortingBtmSheetType == SortingBtmSheetType.ARCHIVE_FOLDER_SCREEN && foldersSortingSelectedState.value) || sortingBtmSheetType == SortingBtmSheetType.ARCHIVE_FOLDER_SCREEN && linksSortingSelectedState.value)) {
                        androidx.compose.material3.Text(
                            text = "Sort by",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 15.dp, top = 20.dp)
                        )
                        SortByUI()
                    }
                    if (sortingBtmSheetType != SortingBtmSheetType.REGULAR_FOLDER_SCREEN && sortingBtmSheetType != SortingBtmSheetType.ARCHIVE_FOLDER_SCREEN) {
                        SortByUI()
                    }
                    Spacer(
                        modifier = Modifier
                            .navigationBarsPadding()
                    )
                }
            }
        }
    }
}