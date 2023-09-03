package com.sakethh.linkora.btmSheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortingBottomSheetUI(
    shouldBottomSheetVisible: MutableState<Boolean>,
    onSelectedAComponent: (SettingsScreenVM.SortingPreferences) -> Unit,
    bottomModalSheetState: SheetState,
) {
    val coroutineScope = rememberCoroutineScope()
    val sortingBtmSheetVM: SortingBtmSheetVM = viewModel()
    LinkoraTheme {
        if (shouldBottomSheetVisible.value) {
            ModalBottomSheet(sheetState = bottomModalSheetState, onDismissRequest = {
                if (bottomModalSheetState.isVisible) {
                    coroutineScope.launch {
                        bottomModalSheetState.hide()
                        shouldBottomSheetVisible.value = false
                    }
                }
            }) {
                Text(text = "Sort by", style = MaterialTheme.typography.headlineSmall)
                sortingBtmSheetVM.sortingBottomSheetData.forEach {
                    Column(
                        modifier = Modifier
                            .clickable {
                                it.onClick()
                                onSelectedAComponent(it.sortingType)
                                if (bottomModalSheetState.isVisible) {
                                    coroutineScope.launch {
                                        bottomModalSheetState.hide()
                                        shouldBottomSheetVisible.value = false
                                    }
                                }
                            }
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = it.sortingName)
                            RadioButton(
                                selected = it.sortingType.name == SettingsScreenVM.Settings.selectedSortingType.value,
                                onClick = {
                                    it.onClick()
                                    onSelectedAComponent(it.sortingType)
                                    if (bottomModalSheetState.isVisible) {
                                        coroutineScope.launch {
                                            bottomModalSheetState.hide()
                                            shouldBottomSheetVisible.value = false
                                        }
                                    }
                                })
                        }
                        // Spacer(modifier = Modifier.height(15.dp))
                    }
                }
            }
        }
    }
}