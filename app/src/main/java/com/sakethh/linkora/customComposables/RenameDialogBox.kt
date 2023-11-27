package com.sakethh.linkora.customComposables

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.btmSheet.OptionsBtmSheetType
import com.sakethh.linkora.localDB.CustomFunctionsForLocalDB
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.launch

data class RenameDialogBoxParam(
    val shouldDialogBoxAppear: MutableState<Boolean>,
    val renameDialogBoxFor: OptionsBtmSheetType = OptionsBtmSheetType.FOLDER,
    val onNoteChangeClickForLinks: ((newNote: String) -> Unit),
    val onTitleChangeClickForLinks: ((newTitle: String) -> Unit),
    val inChildArchiveFolderScreen: MutableState<Boolean> = mutableStateOf(false),
    val onTitleRenamed: () -> Unit = {},
    val currentFolderID: Long,
    val parentFolderID: Long?,
    val existingFolderName: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameDialogBox(
    renameDialogBoxParam: RenameDialogBoxParam
) {
    val scrollState = rememberScrollState()
    val localContext = LocalContext.current
    val customComposablesVM: CustomComposablesVM = viewModel()
    val coroutineScope = rememberCoroutineScope()
    if (renameDialogBoxParam.shouldDialogBoxAppear.value) {
        val newFolderOrTitleName = rememberSaveable {
            mutableStateOf("")
        }
        val newNote = rememberSaveable {
            mutableStateOf("")
        }
        LinkoraTheme {
            AlertDialog(modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(AlertDialogDefaults.containerColor),
                onDismissRequest = { renameDialogBoxParam.shouldDialogBoxAppear.value = false }) {
                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    Text(
                        text = if (renameDialogBoxParam.renameDialogBoxFor != OptionsBtmSheetType.LINK) "Rename \"${renameDialogBoxParam.existingFolderName}\" folder:" else "Change Link's Data:",
                        color = AlertDialogDefaults.titleContentColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(start = 20.dp, top = 30.dp, end = 25.dp),
                        lineHeight = 27.sp,
                        textAlign = TextAlign.Start
                    )
                    OutlinedTextField(maxLines = 1,
                        modifier = Modifier.padding(
                            start = 20.dp, end = 20.dp, top = 30.dp
                        ),
                        label = {
                            Text(
                                text = if (renameDialogBoxParam.renameDialogBoxFor == OptionsBtmSheetType.FOLDER) "New Name" else "New title",
                                color = AlertDialogDefaults.textContentColor,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 12.sp
                            )
                        },
                        textStyle = MaterialTheme.typography.titleSmall,
                        singleLine = true,
                        shape = RoundedCornerShape(5.dp),
                        value = newFolderOrTitleName.value,
                        onValueChange = {
                            newFolderOrTitleName.value = it
                        })
                    OutlinedTextField(maxLines = 1,
                        modifier = Modifier.padding(
                            start = 20.dp, end = 20.dp, top = 15.dp
                        ),
                        label = {
                            Text(
                                text = "New note",
                                color = AlertDialogDefaults.textContentColor,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 12.sp
                            )
                        },
                        textStyle = MaterialTheme.typography.titleSmall,
                        singleLine = true,
                        shape = RoundedCornerShape(5.dp),
                        value = newNote.value,
                        onValueChange = {
                            newNote.value = it
                        })
                    Text(
                        text = "Leave above field empty, if you don't want to change the note.",
                        color = AlertDialogDefaults.textContentColor,
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 20.dp, top = 10.dp, end = 20.dp),
                        lineHeight = 16.sp
                    )
                    Button(colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .padding(
                                end = 20.dp,
                                top = 20.dp,
                                start = 40.dp
                            )
                            .align(Alignment.End),
                        onClick = {
                            if (renameDialogBoxParam.renameDialogBoxFor == OptionsBtmSheetType.FOLDER) {
                                customComposablesVM.updateBothNameAndNote(
                                    UpdateBothNameAndNoteParam(
                                        renameDialogBoxParam = RenameDialogBoxParam(
                                            shouldDialogBoxAppear = renameDialogBoxParam.shouldDialogBoxAppear,
                                            currentFolderID = renameDialogBoxParam.currentFolderID,
                                            existingFolderName = renameDialogBoxParam.existingFolderName,
                                            parentFolderID = renameDialogBoxParam.parentFolderID,
                                            onNoteChangeClickForLinks = {},
                                            onTitleChangeClickForLinks = {},
                                            inChildArchiveFolderScreen = renameDialogBoxParam.inChildArchiveFolderScreen
                                        ),
                                        context = localContext,
                                        newFolderOrTitleName = newFolderOrTitleName.value,
                                        newNote = newNote.value,
                                        parentFolderID = renameDialogBoxParam.parentFolderID,
                                    )
                                )
                            } else {
                                if (newFolderOrTitleName.value.isEmpty()) {
                                    Toast.makeText(
                                        localContext,
                                        "title name can't be empty",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (newNote.value == "") {
                                    renameDialogBoxParam.onTitleChangeClickForLinks(
                                        newFolderOrTitleName.value
                                    )
                                } else {
                                    renameDialogBoxParam.onTitleChangeClickForLinks(
                                        newFolderOrTitleName.value
                                    )
                                    renameDialogBoxParam.onNoteChangeClickForLinks(newNote.value)
                                }
                            }
                        }) {
                        Text(
                            text = "Change both name and note",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                    }
                    Button(colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .padding(
                                end = 20.dp,
                                top = 10.dp,
                            )
                            .align(Alignment.End),
                        onClick = {
                            if (renameDialogBoxParam.renameDialogBoxFor == OptionsBtmSheetType.LINK || renameDialogBoxParam.inChildArchiveFolderScreen.value) {
                                    renameDialogBoxParam.onNoteChangeClickForLinks(newNote.value)
                                renameDialogBoxParam.shouldDialogBoxAppear.value = false
                            } else {
                                if (newNote.value.isEmpty()) {
                                    Toast.makeText(
                                        localContext, "note can't be empty", Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    coroutineScope.launch {
                                        if (renameDialogBoxParam.existingFolderName != null) {
                                            CustomFunctionsForLocalDB.localDB.updateDao()
                                                .renameAFolderNote(
                                                    folderID = renameDialogBoxParam.currentFolderID,
                                                    newNote = newNote.value
                                                )
                                        }
                                    }
                                    renameDialogBoxParam.shouldDialogBoxAppear.value = false
                                }
                            }
                        }) {
                        Text(
                            text = "Change note only",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                    }
                    OutlinedButton(colors = ButtonDefaults.outlinedButtonColors(),
                        border = BorderStroke(
                            width = 1.dp, color = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier
                            .padding(
                                end = 20.dp, top = 10.dp, bottom = 30.dp
                            )
                            .align(Alignment.End),
                        onClick = {
                            renameDialogBoxParam.shouldDialogBoxAppear.value = false
                        }) {
                        Text(
                            text = "Cancel",
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