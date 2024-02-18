package com.sakethh.linkora.btmSheet

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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

data class SortingBottomSheetUIParam @OptIn(ExperimentalMaterial3Api::class) constructor(
    val shouldBottomSheetVisible: MutableState<Boolean>,
    val onSelectedAComponent: (
        SettingsScreenVM.SortingPreferences,
        isLinksSortingSelected: Boolean, isFoldersSortingSelected: Boolean
    ) -> Unit,
    val bottomModalSheetState: SheetState,
    val sortingBtmSheetType: SortingBtmSheetType,
    val shouldFoldersSelectionBeVisible: MutableState<Boolean>,
    val shouldLinksSelectionBeVisible: MutableState<Boolean>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortingBottomSheetUI(
    sortingBottomSheetUIParam: SortingBottomSheetUIParam
) {
    val coroutineScope = rememberCoroutineScope()
    val sortingBtmSheetVM: SortingBtmSheetVM = viewModel()
    val context = LocalContext.current
    LaunchedEffect(key1 = sortingBottomSheetUIParam.shouldBottomSheetVisible.value) {
        if (sortingBottomSheetUIParam.shouldBottomSheetVisible.value) {
            sortingBottomSheetUIParam.bottomModalSheetState.expand()
        }
    }
    val linksSortingSelectedState = rememberSaveable {
        mutableStateOf(sortingBottomSheetUIParam.shouldLinksSelectionBeVisible.value)
    }
    val foldersSortingSelectedState = rememberSaveable {
        mutableStateOf(sortingBottomSheetUIParam.shouldFoldersSelectionBeVisible.value)
    }
    LinkoraTheme {
        if (sortingBottomSheetUIParam.shouldBottomSheetVisible.value) {
            val didAnyCheckBoxStateChanged = rememberSaveable {
                mutableStateOf(false)
            }
            ModalBottomSheet(
                sheetState = sortingBottomSheetUIParam.bottomModalSheetState,
                onDismissRequest = {
                    coroutineScope.launch {
                        sortingBottomSheetUIParam.bottomModalSheetState.hide()
                    }
                    sortingBottomSheetUIParam.shouldBottomSheetVisible.value = false
                }) {
                Column(
                    modifier = Modifier.animateContentSize()
                ) {
                    androidx.compose.material3.Text(
                        text = when (sortingBottomSheetUIParam.sortingBtmSheetType) {
                            SortingBtmSheetType.COLLECTIONS_SCREEN -> "Sort folders by"
                            SortingBtmSheetType.HISTORY_SCREEN -> "Sort History Links by"
                            SortingBtmSheetType.PARENT_ARCHIVE_SCREEN -> "Sort by"
                            SortingBtmSheetType.SAVED_LINKS_SCREEN -> "Sort Saved Links by"
                            SortingBtmSheetType.IMPORTANT_LINKS_SCREEN -> "Sort Important Links by"
                            else -> {
                                "Sort based on"
                            }
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                    if (sortingBottomSheetUIParam.sortingBtmSheetType == SortingBtmSheetType.REGULAR_FOLDER_SCREEN) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    if ((sortingBottomSheetUIParam.sortingBtmSheetType == SortingBtmSheetType.REGULAR_FOLDER_SCREEN || sortingBottomSheetUIParam.sortingBtmSheetType == SortingBtmSheetType.ARCHIVE_FOLDER_SCREEN) && sortingBottomSheetUIParam.shouldFoldersSelectionBeVisible.value) {
                        FolderIndividualComponent(
                            folderName = "Folders",
                            folderNote = "",
                            onMoreIconClick = { },
                            onFolderClick = {
                                didAnyCheckBoxStateChanged.value = true
                            },
                            showMoreIcon = false,
                            showCheckBox = mutableStateOf(true),
                            checkBoxState = {
                                didAnyCheckBoxStateChanged.value = true
                                foldersSortingSelectedState.value = it
                            },
                            isCheckBoxChecked = foldersSortingSelectedState
                        )
                    }
                    if ((sortingBottomSheetUIParam.sortingBtmSheetType == SortingBtmSheetType.REGULAR_FOLDER_SCREEN || sortingBottomSheetUIParam.sortingBtmSheetType == SortingBtmSheetType.ARCHIVE_FOLDER_SCREEN) && sortingBottomSheetUIParam.shouldLinksSelectionBeVisible.value) {
                        FolderIndividualComponent(
                            folderName = "Links",
                            folderNote = "",
                            folderIcon = Icons.Default.Link,
                            onMoreIconClick = { },
                            onFolderClick = {
                                didAnyCheckBoxStateChanged.value = true
                            },
                            showMoreIcon = false,
                            showCheckBox = mutableStateOf(true),
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
                                        sortingBottomSheetUIParam.onSelectedAComponent(
                                            it.sortingType, linksSortingSelectedState.value,
                                            foldersSortingSelectedState.value
                                        )
                                        it.onClick()
                                        coroutineScope.launch {
                                            sortingBottomSheetUIParam.bottomModalSheetState.hide()
                                            sortingBottomSheetUIParam.shouldBottomSheetVisible.value =
                                                false
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
                                            sortingBottomSheetUIParam.onSelectedAComponent(
                                                it.sortingType, linksSortingSelectedState.value,
                                                foldersSortingSelectedState.value
                                            )
                                            it.onClick()
                                            coroutineScope.launch {
                                                sortingBottomSheetUIParam.bottomModalSheetState.hide()
                                                sortingBottomSheetUIParam.shouldBottomSheetVisible.value =
                                                    false
                                            }
                                        }, modifier = Modifier.padding(end = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                    if (((sortingBottomSheetUIParam.sortingBtmSheetType == SortingBtmSheetType.REGULAR_FOLDER_SCREEN && foldersSortingSelectedState.value) || (sortingBottomSheetUIParam.sortingBtmSheetType == SortingBtmSheetType.REGULAR_FOLDER_SCREEN && linksSortingSelectedState.value)) || ((sortingBottomSheetUIParam.sortingBtmSheetType == SortingBtmSheetType.ARCHIVE_FOLDER_SCREEN && foldersSortingSelectedState.value) || sortingBottomSheetUIParam.sortingBtmSheetType == SortingBtmSheetType.ARCHIVE_FOLDER_SCREEN && linksSortingSelectedState.value)) {
                        androidx.compose.material3.Text(
                            text = "Sort by",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 15.dp, top = 20.dp)
                        )
                        SortByUI()
                    }
                    if (sortingBottomSheetUIParam.sortingBtmSheetType != SortingBtmSheetType.REGULAR_FOLDER_SCREEN && sortingBottomSheetUIParam.sortingBtmSheetType != SortingBtmSheetType.ARCHIVE_FOLDER_SCREEN) {
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