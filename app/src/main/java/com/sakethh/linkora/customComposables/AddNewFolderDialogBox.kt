package com.sakethh.linkora.customComposables


import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.localDB.LocalDataBase
import com.sakethh.linkora.localDB.commonVMs.CreateVM
import com.sakethh.linkora.screens.collections.CollectionsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


data class AddNewFolderDialogBoxParam(
    val shouldDialogBoxAppear: MutableState<Boolean>,
    val newFolderData: (String, Long) -> Unit = { folderName, folderID -> },
    val onCreated: () -> Unit = {},
    val parentFolderID: Long?,
    val currentFolderID: Long?,
    val inAChildFolderScreen: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewFolderDialogBox(
    addNewFolderDialogBoxParam: AddNewFolderDialogBoxParam
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val createDBVM: CreateVM = viewModel()
    val coroutineScope = rememberCoroutineScope()
    val isFolderCreationInProgress = rememberSaveable {
        mutableStateOf(false)
    }
    if (addNewFolderDialogBoxParam.shouldDialogBoxAppear.value) {
        val folderNameTextFieldValue = rememberSaveable {
            mutableStateOf("")
        }
        val noteTextFieldValue = rememberSaveable {
            mutableStateOf("")
        }
        LinkoraTheme {
            AlertDialog(modifier = Modifier
                .wrapContentHeight()
                .animateContentSize()
                .clip(RoundedCornerShape(10.dp))
                .background(AlertDialogDefaults.containerColor),
                onDismissRequest = {
                    if (!isFolderCreationInProgress.value) {
                        addNewFolderDialogBoxParam.shouldDialogBoxAppear.value = false
                    }
                }) {
                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    Text(
                        text = "Create new folder",
                        color = AlertDialogDefaults.titleContentColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(start = 20.dp, top = 30.dp)
                    )
                    OutlinedTextField(readOnly = isFolderCreationInProgress.value,
                        maxLines = 1,
                        modifier = Modifier.padding(
                            start = 20.dp, end = 20.dp, top = 20.dp
                        ),
                        label = {
                            Text(
                                text = "Folder name",
                                color = AlertDialogDefaults.textContentColor,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 12.sp
                            )
                        },
                        textStyle = MaterialTheme.typography.titleSmall,
                        singleLine = true,
                        value = folderNameTextFieldValue.value,
                        onValueChange = {
                            folderNameTextFieldValue.value = it
                        })
                    OutlinedTextField(readOnly = isFolderCreationInProgress.value,
                        maxLines = 1,
                        modifier = Modifier.padding(
                            start = 20.dp, end = 20.dp, top = 15.dp
                        ),
                        label = {
                            Text(
                                text = "Note why you're creating this folder",
                                color = AlertDialogDefaults.textContentColor,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 12.sp
                            )
                        },
                        textStyle = MaterialTheme.typography.titleSmall,
                        singleLine = true,
                        value = noteTextFieldValue.value,
                        onValueChange = {
                            noteTextFieldValue.value = it
                        })
                    if (!isFolderCreationInProgress.value) {
                        Button(colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .padding(
                                    end = 20.dp,
                                    top = 20.dp,
                                    start = 20.dp
                                )
                                .fillMaxWidth()
                                .align(Alignment.End),
                            onClick = {
                                isFolderCreationInProgress.value = true
                                if (folderNameTextFieldValue.value.isEmpty()) {
                                    Toast.makeText(
                                        context, "folder name can't be empty", Toast.LENGTH_SHORT
                                    ).show()
                                    isFolderCreationInProgress.value = false
                                } else if (folderNameTextFieldValue.value == "Saved Links") {
                                    Toast.makeText(
                                        context,
                                        "\"Saved Links\" already exists by default, choose another name :)",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    isFolderCreationInProgress.value = false
                                } else {
                                    createDBVM.createANewFolder(
                                        context = context,
                                        folderName = folderNameTextFieldValue.value,
                                        infoForSaving = noteTextFieldValue.value,
                                        onTaskCompleted = {
                                            coroutineScope.launch {
                                                async {
                                                    addNewFolderDialogBoxParam.newFolderData(
                                                        folderNameTextFieldValue.value,
                                                        LocalDataBase.localDB.readDao()
                                                            .getLatestAddedFolder().id
                                                    )
                                                }.await()
                                            }
                                            addNewFolderDialogBoxParam.onCreated()
                                            addNewFolderDialogBoxParam.shouldDialogBoxAppear.value =
                                                false
                                            isFolderCreationInProgress.value = false
                                        },
                                        parentFolderID = addNewFolderDialogBoxParam.parentFolderID,
                                        inAChildFolderScreen = addNewFolderDialogBoxParam.inAChildFolderScreen,
                                        rootParentID = CollectionsScreenVM.rootFolderID
                                    )
                                }
                            }) {
                            Text(
                                text = "Create",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 16.sp
                            )
                        }
                        androidx.compose.material3.OutlinedButton(colors = ButtonDefaults.outlinedButtonColors(),
                            border = BorderStroke(
                                width = 1.dp, color = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 20.dp, end = 20.dp, top = 10.dp, bottom = 30.dp
                                )
                                .align(Alignment.End),
                            onClick = {
                                addNewFolderDialogBoxParam.shouldDialogBoxAppear.value = false
                            }) {
                            Text(
                                text = "Cancel",
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(40.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}