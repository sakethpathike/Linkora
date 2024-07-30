package com.sakethh.linkora.ui.commonComposables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.ShelfBtmSheetVM
import com.sakethh.linkora.ui.theme.LinkoraTheme

data class DeleteAShelfDialogBoxParam(
    val isDialogBoxVisible: MutableState<Boolean>,
    val onDeleteClick: () -> Unit
)

@Composable
fun DeleteAShelfPanelDialogBox(deleteAShelfDialogBoxParam: DeleteAShelfDialogBoxParam) {
    if (deleteAShelfDialogBoxParam.isDialogBoxVisible.value) {
        val title = rememberSaveable(ShelfBtmSheetVM.selectedShelfData.shelfName) {
            mutableStateOf("Are you sure you want to delete \"${ShelfBtmSheetVM.selectedShelfData.shelfName}\"?")
        }
        LinkoraTheme {
            AlertDialog(
                confirmButton = {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pulsateEffect(),
                        onClick = {
                            deleteAShelfDialogBoxParam.onDeleteClick()
                            deleteAShelfDialogBoxParam.isDialogBoxVisible.value = false
                        }) {
                        Text(
                            text = "Permanently Delete Panel",
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                    }
                }, dismissButton = {
                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pulsateEffect(),
                        onClick = {
                            deleteAShelfDialogBoxParam.isDialogBoxVisible.value = false
                        }) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                    }
                }, title = {
                    Text(
                        text = title.value, style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                        lineHeight = 27.sp,
                        textAlign = TextAlign.Start
                    )
                }, text = {
                    Text(
                        text = "Once deleted, this Panel cannot be restored.",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Start
                    )
                },
                onDismissRequest = {
                    deleteAShelfDialogBoxParam.isDialogBoxVisible.value = false
                })
        }
    }
}