package com.sakethh.linkora.ui.commonComposables


import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.data.localDB.LocalDataBase
import com.sakethh.linkora.localDB.commonVMs.CreateVM
import com.sakethh.linkora.ui.viewmodels.collections.CollectionsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


data class AddNewFolderDialogBoxParam(
    val shouldDialogBoxAppear: MutableState<Boolean>,
    val newFolderData: (String, Long) -> Unit = { folderName, folderID -> },
    val onCreated: () -> Unit = {},
    val parentFolderID: Long?,
    val inAChildFolderScreen: Boolean
)

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
            AlertDialog(dismissButton = {
                if (!isFolderCreationInProgress.value) {
                    androidx.compose.material3.OutlinedButton(modifier = Modifier
                        .fillMaxWidth()
                        .pulsateEffect(),
                        onClick = {
                            addNewFolderDialogBoxParam.shouldDialogBoxAppear.value = false
                        }) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                    }
                }
            },
                confirmButton = {
                    if (!isFolderCreationInProgress.value) {
                        Button(modifier = Modifier
                            .fillMaxWidth()
                            .pulsateEffect(), onClick = {
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
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 16.sp
                            )
                        }
                    }
                },
                modifier = Modifier
                    .animateContentSize()
                    .wrapContentHeight(),
                onDismissRequest = {
                    if (!isFolderCreationInProgress.value) {
                        addNewFolderDialogBoxParam.shouldDialogBoxAppear.value = false
                    }
                },
                text = {
                    Column(modifier = Modifier.verticalScroll(scrollState)) {
                        OutlinedTextField(readOnly = isFolderCreationInProgress.value,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                            label = {
                                Text(
                                    text = "Folder name",
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
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(readOnly = isFolderCreationInProgress.value,
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text(
                                    text = "Note why you're creating this folder",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 12.sp
                                )
                            },
                            textStyle = MaterialTheme.typography.titleSmall,
                            value = noteTextFieldValue.value,
                            onValueChange = {
                                noteTextFieldValue.value = it
                            })
                        if (isFolderCreationInProgress.value) {
                            Spacer(modifier = Modifier.height(40.dp))
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    }
                },
                title = {
                    Text(
                        text = if (addNewFolderDialogBoxParam.inAChildFolderScreen) "Create a new internal folder in \"${CollectionsScreenVM.currentClickedFolderData.value.folderName}\"" else "Create a new folder",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                        lineHeight = 28.sp
                    )
                })
        }
    }
}