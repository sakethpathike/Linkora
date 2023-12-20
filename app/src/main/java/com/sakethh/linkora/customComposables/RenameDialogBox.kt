package com.sakethh.linkora.customComposables

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.btmSheet.OptionsBtmSheetType
import com.sakethh.linkora.ui.theme.LinkoraTheme

data class RenameDialogBoxParam(
    val shouldDialogBoxAppear: MutableState<Boolean>,
    val renameDialogBoxFor: OptionsBtmSheetType = OptionsBtmSheetType.FOLDER,
    val onNoteChangeClick: ((newNote: String) -> Unit),
    val onTitleChangeClick: ((newTitle: String) -> Unit),
    val existingFolderName: String?,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameDialogBox(
    renameDialogBoxParam: RenameDialogBoxParam
) {
    val scrollState = rememberScrollState()
    val localContext = LocalContext.current
    if (renameDialogBoxParam.shouldDialogBoxAppear.value) {
        val newFolderOrTitleName = rememberSaveable {
            mutableStateOf("")
        }
        val newNote = rememberSaveable {
            mutableStateOf("")
        }
        LinkoraTheme {
            AlertDialog(confirmButton = {
                Button(modifier = Modifier.fillMaxWidth(), onClick = {
                    renameDialogBoxParam.onNoteChangeClick(newNote.value)
                }) {
                    Text(
                        text = "Change note only",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 16.sp
                    )
                }
                Button(modifier = Modifier.fillMaxWidth(), onClick = {
                    if (newFolderOrTitleName.value.isNotEmpty()) {
                        if (newNote.value.isNotEmpty()) {
                            renameDialogBoxParam.onTitleChangeClick(newFolderOrTitleName.value)
                            renameDialogBoxParam.onNoteChangeClick(newNote.value)
                        } else {
                            renameDialogBoxParam.onTitleChangeClick(newFolderOrTitleName.value)
                        }
                    } else {
                        Toast.makeText(
                            localContext, "Title can't be empty", Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    Text(
                        text = "Change both name and note",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 16.sp
                    )
                }
            }, dismissButton = {
                OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                    renameDialogBoxParam.shouldDialogBoxAppear.value = false
                }) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 16.sp
                    )
                }
            }, title = {
                Text(
                    text = if (renameDialogBoxParam.renameDialogBoxFor != OptionsBtmSheetType.LINK) "Rename \"${renameDialogBoxParam.existingFolderName}\" folder:" else "Change Link's Data:",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 22.sp,
                    lineHeight = 27.sp,
                    textAlign = TextAlign.Start
                )
            }, text = {
                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    OutlinedTextField(maxLines = 1,
                        label = {
                            Text(
                                text = if (renameDialogBoxParam.renameDialogBoxFor == OptionsBtmSheetType.FOLDER) "New Name" else "New title",
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 12.sp
                            )
                        },
                        textStyle = MaterialTheme.typography.titleSmall,
                        singleLine = true,
                        value = newFolderOrTitleName.value,
                        onValueChange = {
                            newFolderOrTitleName.value = it
                        })
                    OutlinedTextField(label = {
                        Text(
                            text = "New note",
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 12.sp
                        )
                    },
                        textStyle = MaterialTheme.typography.titleSmall,
                        value = newNote.value,
                        onValueChange = {
                            newNote.value = it
                        })
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        border = BorderStroke(
                            1.dp, contentColorFor(MaterialTheme.colorScheme.surface)
                        ),
                        colors = CardDefaults.cardColors(containerColor = AlertDialogDefaults.containerColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(
                                    top = 10.dp, bottom = 10.dp
                                ), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    modifier = Modifier.padding(
                                        start = 10.dp, end = 10.dp
                                    )
                                )
                            }
                            Text(
                                text = "Leave above field empty, if you don't want to change the note.",
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 14.sp,
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }
            }, onDismissRequest = { renameDialogBoxParam.shouldDialogBoxAppear.value = false })
        }
    }
}