package com.sakethh.linkora.ui.commonComposables

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.R
import com.sakethh.linkora.ui.screens.collections.CollectionsScreenVM
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
                            text = stringResource(id = R.string.delete_it),
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
                            text = stringResource(id = R.string.cancel),
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                    }
                }, title = {
                    Text(
                        text = if (deleteDialogBoxParam.deleteDialogBoxType == DataDialogBoxType.LINK && deleteDialogBoxParam.areFoldersSelectable) stringResource(
                            id = R.string.are_you_sure_you_want_to_delete_all_selected_links
                        ) else if (deleteDialogBoxParam.deleteDialogBoxType == DataDialogBoxType.LINK) stringResource(
                            id = R.string.are_you_sure_you_want_to_delete_the_link
                        ) else if (deleteDialogBoxParam.deleteDialogBoxType == DataDialogBoxType.FOLDER && deleteDialogBoxParam.areFoldersSelectable) stringResource(
                            id = R.string.are_you_sure_you_want_to_delete_all_selected_folders
                        ) else if (deleteDialogBoxParam.deleteDialogBoxType == DataDialogBoxType.FOLDER) stringResource(
                            id = R.string.are_you_sure_want_to_delete_the
                        ) + " \"${deleteDialogBoxParam.folderName.value}\" " + stringResource(id = R.string.folder) + "?" else if (deleteDialogBoxParam.deleteDialogBoxType == DataDialogBoxType.SELECTED_DATA) stringResource(
                            id = R.string.are_you_sure_you_want_to_delete_all_selected_items
                        ) else stringResource(id = R.string.are_you_sure_you_want_to_delete_all_folders_and_links),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                        lineHeight = 27.sp,
                        textAlign = TextAlign.Start
                    )
                }, text = {
                    if (deleteDialogBoxParam.deleteDialogBoxType == DataDialogBoxType.FOLDER && deleteDialogBoxParam.totalIds.longValue > 0
                    ) {
                        Text(
                            text = stringResource(id = R.string.this_folder_deletion_will_also_delete_all_its_internal_folders),
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