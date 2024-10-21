package com.sakethh.linkora.ui.commonComposables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.ui.screens.panels.PanelsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme

data class DeleteAShelfDialogBoxParam(
    val isDialogBoxVisible: MutableState<Boolean>,
    val onDeleteClick: () -> Unit
)

@Composable
fun DeleteAShelfPanelDialogBox(deleteAShelfDialogBoxParam: DeleteAShelfDialogBoxParam) {
    if (deleteAShelfDialogBoxParam.isDialogBoxVisible.value) {
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
                            text = LocalizedStrings.permanentlyDeleteThePanel.value,
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
                            text = LocalizedStrings.cancel.value,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                    }
                }, title = {
                    Text(
                        text = LocalizedStrings.areYouSureWantToDeleteThePanel.value.replace(
                            "\$\$\$\$",
                            PanelsScreenVM.selectedPanelData.panelName
                        ), style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        textAlign = TextAlign.Start
                    )
                }, text = {
                    Text(
                        text = LocalizedStrings.onceDeletedThisPanelCannotBeRestarted.value,
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