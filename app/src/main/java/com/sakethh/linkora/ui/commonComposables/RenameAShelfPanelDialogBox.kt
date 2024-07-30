package com.sakethh.linkora.ui.commonComposables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.ShelfBtmSheetVM

@Composable
fun RenameAShelfPanelDialogBox(
    isDialogBoxVisible: MutableState<Boolean>,
    onRenameClick: (String) -> Unit
) {
    if (isDialogBoxVisible.value) {
        val newShelfName = rememberSaveable {
            mutableStateOf("")
        }
        AlertDialog(
            onDismissRequest = {
                isDialogBoxVisible.value = false
            },
            confirmButton = {
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .pulsateEffect(), onClick = {
                    onRenameClick(newShelfName.value)
                    isDialogBoxVisible.value = false
                }) {
                    Text(
                        text = "Change Panel name",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 16.sp
                    )
                }
            },
            title = {
                Text(
                    text = "Edit \"${ShelfBtmSheetVM.selectedShelfData.shelfName}\" Panel Name",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 22.sp,
                    lineHeight = 27.sp,
                    textAlign = TextAlign.Start
                )
            },
            text = {
                OutlinedTextField(label = {
                    Text(
                        text = "New Name for Panel",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 12.sp
                    )
                },
                    textStyle = MaterialTheme.typography.titleSmall,
                    value = newShelfName.value,
                    onValueChange = {
                        newShelfName.value = it
                    })
            },
            dismissButton = {
                OutlinedButton(modifier = Modifier
                    .fillMaxWidth()
                    .pulsateEffect(), onClick = {
                    isDialogBoxVisible.value = false
                }) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 16.sp
                    )
                }
            })
    }
}