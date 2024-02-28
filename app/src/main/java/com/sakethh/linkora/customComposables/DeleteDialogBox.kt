package com.sakethh.linkora.customComposables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.screens.collections.CollectionsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme

enum class DataDialogBoxType {
    LINK, FOLDER, REMOVE_ENTIRE_DATA, SELECTED_DATA
}

data class DeleteDialogBoxParam(
    val shouldDialogBoxAppear: MutableState<Boolean>,
    val deleteDialogBoxType: DataDialogBoxType,
    val onDeleteClick: () -> Unit,
    val onDeleted: () -> Unit = {},
    val totalIds: MutableLongState = mutableLongStateOf(0),
    val folderName: MutableState<String> = mutableStateOf(CollectionsScreenVM.selectedFolderData.value.folderName),
    val areFoldersSelectable: Boolean = false,
    /*
        val title:String,
        val description:String*/
)

@Composable
fun DeleteDialogBox(
    deleteDialogBoxParam: DeleteDialogBoxParam
) {
    Column {
        if (deleteDialogBoxParam.shouldDialogBoxAppear.value) {
            LinkoraTheme {
                AlertDialog(confirmButton = {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pulsateEffect(),
                        onClick = {
                            deleteDialogBoxParam.onDeleteClick()
                            deleteDialogBoxParam.shouldDialogBoxAppear.value = false
                            deleteDialogBoxParam.onDeleted()
                        }) {
                        Text(
                            text = "Delete it",
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
                            deleteDialogBoxParam.shouldDialogBoxAppear.value = false
                        }) {
                        Text(
                            text = "Never-mind",
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                    }
                }, title = {
                    Text(
                        text = if (deleteDialogBoxParam.deleteDialogBoxType == DataDialogBoxType.LINK && deleteDialogBoxParam.areFoldersSelectable) "Are you sure you want to delete all selected links?" else if (deleteDialogBoxParam.deleteDialogBoxType == DataDialogBoxType.LINK) "Are you sure you want to delete the link?" else if (deleteDialogBoxParam.deleteDialogBoxType == DataDialogBoxType.FOLDER && deleteDialogBoxParam.areFoldersSelectable) "Are you sure you want to delete all selected folders?" else if (deleteDialogBoxParam.deleteDialogBoxType == DataDialogBoxType.FOLDER) "Are you sure you want to delete the \"${deleteDialogBoxParam.folderName.value}\" folder?" else if (deleteDialogBoxParam.deleteDialogBoxType == DataDialogBoxType.SELECTED_DATA) "Are you sure you want to delete all selected items?" else "Are you sure you want to delete all folders and links?",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                        lineHeight = 27.sp,
                        textAlign = TextAlign.Start
                    )
                }, text = {
                    if (deleteDialogBoxParam.deleteDialogBoxType == DataDialogBoxType.FOLDER && deleteDialogBoxParam.totalIds.longValue > 0
                    ) {
                        Text(
                            text = "This folder deletion will also delete all its internal folder(s).",
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Start,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                    onDismissRequest = {
                        deleteDialogBoxParam.shouldDialogBoxAppear.value = false
                    })
            }
        }
    }
}