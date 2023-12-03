package com.sakethh.linkora.customComposables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.ui.theme.LinkoraTheme

enum class DataDialogBoxType {
    LINK, FOLDER, REMOVE_ENTIRE_DATA
}

data class DeleteDialogBoxParam(
    val shouldDialogBoxAppear: MutableState<Boolean>,
    val deleteDialogBoxType: DataDialogBoxType,
    val onDeleteClick: () -> Unit,
    val onDeleted: () -> Unit = {},
    val totalIds: Long = 0,
    val folderName: MutableState<String> = mutableStateOf("")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteDialogBox(
    deleteDialogBoxParam: DeleteDialogBoxParam
) {
    Column {
        if (deleteDialogBoxParam.shouldDialogBoxAppear.value) {
            LinkoraTheme {
                AlertDialog(modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(AlertDialogDefaults.containerColor),
                    onDismissRequest = {
                        deleteDialogBoxParam.shouldDialogBoxAppear.value = false
                    }) {
                    Column {
                        Text(
                            text = if (deleteDialogBoxParam.deleteDialogBoxType == DataDialogBoxType.LINK) "Are you sure you want to delete the link?" else if (deleteDialogBoxParam.deleteDialogBoxType == DataDialogBoxType.FOLDER) "Are you sure you want to delete the \"${deleteDialogBoxParam.folderName.value}\" folder?" else "Are you sure you want to delete all folders and links?",
                            color = AlertDialogDefaults.titleContentColor,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 22.sp,
                            modifier = Modifier.padding(start = 20.dp, top = 30.dp, end = 15.dp),
                            lineHeight = 27.sp,
                            textAlign = TextAlign.Start
                        )
                        if (deleteDialogBoxParam.deleteDialogBoxType == DataDialogBoxType.FOLDER && deleteDialogBoxParam.totalIds > 0) {
                            Text(
                                text = "This folder deletion will also delete all its internal folder(s).",
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(
                                    top = 15.dp, start = 20.dp, end = 15.dp
                                ),
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Start,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Button(colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .padding(
                                    end = 20.dp,
                                    top = 20.dp,
                                )
                                .align(Alignment.End),
                            onClick = {
                                deleteDialogBoxParam.onDeleteClick()
                                deleteDialogBoxParam.shouldDialogBoxAppear.value = false
                                deleteDialogBoxParam.onDeleted()
                            }) {
                            Text(
                                text = "Delete it",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 16.sp
                            )
                        }
                        OutlinedButton(colors = ButtonDefaults.outlinedButtonColors(),
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier
                                .padding(
                                    end = 20.dp,
                                    top = 10.dp,
                                    bottom = 30.dp
                                )
                                .align(Alignment.End),
                            onClick = {
                                deleteDialogBoxParam.shouldDialogBoxAppear.value = false
                            }) {
                            Text(
                                text = "Never-mind",
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}