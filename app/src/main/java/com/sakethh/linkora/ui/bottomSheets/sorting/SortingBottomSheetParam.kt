package com.sakethh.linkora.ui.bottomSheets.sorting

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.MutableState
import com.sakethh.linkora.ui.screens.settings.SortingPreferences

data class SortingBottomSheetParam @OptIn(ExperimentalMaterial3Api::class) constructor(
    val shouldBottomSheetVisible: MutableState<Boolean>,
    val onSelectedAComponent: (
        SortingPreferences,
        isLinksSortingSelected: Boolean, isFoldersSortingSelected: Boolean
    ) -> Unit,
    val bottomModalSheetState: SheetState,
    val sortingBtmSheetType: SortingBtmSheetType,
    val shouldFoldersSelectionBeVisible: MutableState<Boolean>,
    val shouldLinksSelectionBeVisible: MutableState<Boolean>
)