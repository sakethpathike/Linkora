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
import com.sakethh.linkora.LocalizedStringsVM
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.ShelfBtmSheetVM
import com.sakethh.linkora.ui.theme.LinkoraTheme

data class DeleteAShelfDialogBoxParam(
    val isDialogBoxVisible: MutableState<Boolean>,
    val onDeleteClick: () -> Unit,
    val localizedStringsVM: LocalizedStringsVM
)

@Composable
fun DeleteAShelfPanelDialogBox(deleteAShelfDialogBoxParam: DeleteAShelfDialogBoxParam) {
    if (deleteAShelfDialogBoxParam.isDialogBoxVisible.value) {
        val title =
            deleteAShelfDialogBoxParam.localizedStringsVM.areYouSureWantToDelete.value + rememberSaveable(
            ShelfBtmSheetVM.selectedShelfData.shelfName
        ) {
            mutableStateOf(" \"${ShelfBtmSheetVM.selectedShelfData.shelfName}\"?")
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
                            text = deleteAShelfDialogBoxParam.localizedStringsVM.permanentlyDeleteThePanel.value,
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
                            text = deleteAShelfDialogBoxParam.localizedStringsVM.cancel.value,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                    }
                }, title = {
                    Text(
                        text = title, style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                        lineHeight = 27.sp,
                        textAlign = TextAlign.Start
                    )
                }, text = {
                    Text(
                        text = deleteAShelfDialogBoxParam.localizedStringsVM.onceDeletedThisPanelCannotBeRestarted.value,
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