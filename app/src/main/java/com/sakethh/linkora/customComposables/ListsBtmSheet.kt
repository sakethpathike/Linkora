package com.sakethh.linkora.customComposables

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListBtmSheet(isBtmSheetVisible: MutableState<Boolean>) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val lazyListState = rememberLazyListState()
    val localContext = LocalContext.current
    if (isBtmSheetVisible.value) {
        ModalBottomSheet(
            sheetState = modalBottomSheetState,
            onDismissRequest = { isBtmSheetVisible.value = false }) {
            LazyColumn(state = lazyListState, modifier = Modifier.pointerInput(Unit) {

            }) {
                items(5) {
                    Text(text = "\t\tCurrently in $it\n")
                }
            }
        }
    }
}