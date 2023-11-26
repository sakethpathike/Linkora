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
    val webURLForTitle: String? = null,
    val renameDialogBoxFor: OptionsBtmSheetType = OptionsBtmSheetType.FOLDER,
    val onNoteChangeClickForLinks: ((webURL: String, newNote: String) -> Unit?)?,
    val onTitleChangeClickForLinks: ((webURL: String, newTitle: String) -> Unit?)?,
    val inChildArchiveFolderScreen: MutableState<Boolean> = mutableStateOf(false),
    val onTitleRenamed: () -> Unit = {},
    val folderID: Long,
    val existingFolderName: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameDialogBox(
    renameDialogBoxParam: RenameDialogBoxParam
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var doesFolderNameAlreadyExists = false
    val localContext = LocalContext.current
    val customFunctionsForLocalDB: CustomFunctionsForLocalDB = viewModel()
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
                            )
                            .align(Alignment.End),
                        onClick = {
                            if (newFolderOrTitleName.value.isEmpty()) {
                                Toast.makeText(
                                    localContext,
                                    if (renameDialogBoxParam.renameDialogBoxFor == OptionsBtmSheetType.FOLDER) "Folder name can't be empty" else "title can't be empty",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                if (renameDialogBoxParam.renameDialogBoxFor == OptionsBtmSheetType.LINK || renameDialogBoxParam.inChildArchiveFolderScreen.value) {
                                    if (renameDialogBoxParam.onTitleChangeClickForLinks != null && renameDialogBoxParam.webURLForTitle != null) {
                                        renameDialogBoxParam.onTitleChangeClickForLinks.invoke(
                                            renameDialogBoxParam.webURLForTitle,
                                            newFolderOrTitleName.value
                                        )
                                    }
                                    if (renameDialogBoxParam.onNoteChangeClickForLinks != null && renameDialogBoxParam.webURLForTitle != null) {
                                        if (newNote.value.isNotEmpty()) {
                                            renameDialogBoxParam.onNoteChangeClickForLinks.invoke(
                                                if (renameDialogBoxParam.inChildArchiveFolderScreen.value) newFolderOrTitleName.value else renameDialogBoxParam.webURLForTitle,
                                                newNote.value
                                            )
                                        }
                                    }
                                    Toast.makeText(
                                        context,
                                        "renamed link's data successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    renameDialogBoxParam.onTitleRenamed()
                                    renameDialogBoxParam.shouldDialogBoxAppear.value = false
                                } else {
                                    if (newFolderOrTitleName.value.isEmpty()) {
                                        Toast.makeText(
                                            localContext,
                                            if (renameDialogBoxParam.renameDialogBoxFor == OptionsBtmSheetType.FOLDER) "Folder name can't be empty" else "title can't be empty",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        if (renameDialogBoxParam.renameDialogBoxFor == OptionsBtmSheetType.FOLDER) {
                                            coroutineScope.launch {
                                                doesFolderNameAlreadyExists =
                                                    renameDialogBoxParam.existingFolderName?.let {
                                                        CustomFunctionsForLocalDB.localDB.readDao()
                                                            .doesThisFolderExists(
                                                                folderID = renameDialogBoxParam.folderID,
                                                                folderName = it
                                                            )
                                                    } == true
                                            }.invokeOnCompletion {
                                                if (doesFolderNameAlreadyExists) {
                                                    Toast.makeText(
                                                        localContext,
                                                        "Folder name already exists",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    if (renameDialogBoxParam.existingFolderName != null) {
                                                        customFunctionsForLocalDB.updateFoldersDetails(
                                                            folderID = renameDialogBoxParam.folderID,
                                                            newFolderName = newFolderOrTitleName.value,
                                                            infoForFolder = newNote.value,
                                                            context = localContext,
                                                            onTaskCompleted = {
                                                                renameDialogBoxParam.onTitleRenamed()
                                                                renameDialogBoxParam.shouldDialogBoxAppear.value =
                                                                    false
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }) {
                        Text(
                            text = if (renameDialogBoxParam.renameDialogBoxFor == OptionsBtmSheetType.FOLDER) "Change folder data" else "Change title data",
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
                                if (renameDialogBoxParam.onNoteChangeClickForLinks != null && renameDialogBoxParam.webURLForTitle != null) {
                                    renameDialogBoxParam.onNoteChangeClickForLinks.invoke(
                                        renameDialogBoxParam.webURLForTitle, newNote.value
                                    )
                                }
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
                                                    folderID = renameDialogBoxParam.folderID,
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