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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.R
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.ShelfBtmSheetVM
import com.sakethh.linkora.ui.theme.LinkoraTheme

data class DeleteAShelfDialogBoxParam(
    val isDialogBoxVisible: MutableState<Boolean>,
    val onDeleteClick: () -> Unit
)

@Composable
fun DeleteAShelfPanelDialogBox(deleteAShelfDialogBoxParam: DeleteAShelfDialogBoxParam) {
    if (deleteAShelfDialogBoxParam.isDialogBoxVisible.value) {
        val title = stringResource(id = R.string.are_you_sure_want_to_delete) + rememberSaveable(
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
                            text = stringResource(id = R.string.permanently_delete_the_panel),
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
                            text = stringResource(id = R.string.cancel),
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
                        text = stringResource(id = R.string.once_deleted_this_panel_cannot_be_restarted),
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