package com.sakethh.linkora.screens.home.composables

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewLinkDialogBox(
    shouldDialogBoxAppear: MutableState<Boolean>,
    onSaveBtnClick: (title: String, webURL: String, note: String, selectedFolder: String) -> Unit,
    isDataExtractingForTheLink: MutableState<Boolean>,
) {
    val selectedFolderName = rememberSaveable {
        mutableStateOf("Saved Links")
    }
    val foldersTableData =
        CustomLocalDBDaoFunctionsDecl.localDB.localDBData().getAllFolders().collectAsState(
            initial = emptyList()
        ).value
    val context = LocalContext.current
    val linkTextFieldValue = rememberSaveable {
        mutableStateOf("")
    }
    val titleTextField = rememberSaveable {
        mutableStateOf("")
    }
    val noteTextFieldValue = rememberSaveable {
        mutableStateOf("")
    }
    val isDropDownMenuIconClicked = rememberSaveable {
        mutableStateOf(false)
    }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    if (shouldDialogBoxAppear.value) {
        LinkoraTheme {
            AlertDialog(modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(AlertDialogDefaults.containerColor),
                onDismissRequest = {
                    if (!isDataExtractingForTheLink.value) {
                        shouldDialogBoxAppear.value = false
                    }
                }) {
                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    Text(
                        text = "Save new link",
                        color = AlertDialogDefaults.textContentColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(start = 20.dp, top = 30.dp)
                    )
                    OutlinedTextField(
                        readOnly = isDataExtractingForTheLink.value,
                        modifier = Modifier.padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 30.dp
                        ),
                        label = {
                            Text(
                                text = "Link",
                                color = AlertDialogDefaults.textContentColor,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 12.sp
                            )
                        },
                        textStyle = MaterialTheme.typography.titleSmall,
                        singleLine = true,
                        shape = RoundedCornerShape(5.dp),
                        value = linkTextFieldValue.value,
                        onValueChange = {
                            linkTextFieldValue.value = it
                        })
                    if (!SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value) {
                        OutlinedTextField(
                            readOnly = isDataExtractingForTheLink.value,
                            modifier = Modifier.padding(
                                start = 20.dp,
                                end = 20.dp,
                                top = 15.dp
                            ),
                            label = {
                                Text(
                                    text = "Title for the link",
                                    color = AlertDialogDefaults.textContentColor,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 12.sp
                                )
                            },
                            textStyle = MaterialTheme.typography.titleSmall,
                            singleLine = true,
                            shape = RoundedCornerShape(5.dp),
                            value = titleTextField.value,
                            onValueChange = {
                                titleTextField.value = it
                            })
                    }
                    OutlinedTextField(
                        readOnly = isDataExtractingForTheLink.value,
                        modifier = Modifier.padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 15.dp
                        ),
                        label = {
                            Text(
                                text = "Note for why you're saving this link",
                                color = AlertDialogDefaults.textContentColor,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 12.sp
                            )
                        },
                        textStyle = MaterialTheme.typography.titleSmall,
                        singleLine = true,
                        shape = RoundedCornerShape(5.dp),
                        value = noteTextFieldValue.value,
                        onValueChange = {
                            noteTextFieldValue.value = it
                        })
                    Row(
                        Modifier.padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 30.dp
                        ),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Save in",
                            color = AlertDialogDefaults.textContentColor,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 18.sp
                        )
                        Row() {
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = selectedFolderName.value,
                                color = AlertDialogDefaults.textContentColor,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            IconButton(onClick = {
                                isDropDownMenuIconClicked.value = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = AlertDialogDefaults.textContentColor
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                    }
                    DropdownMenu(
                        expanded = isDropDownMenuIconClicked.value,
                        onDismissRequest = { isDropDownMenuIconClicked.value = false }) {
                        DropdownMenuItem(onClick = {
                            selectedFolderName.value = "Saved Links"
                            coroutineScope.launch {
                                CustomLocalDBDaoFunctionsDecl.addANewLinkSpecificallyInFolders(
                                    title = titleTextField.value,
                                    webURL = linkTextFieldValue.value,
                                    noteForSaving = noteTextFieldValue.value,
                                    folderName = selectedFolderName.value,
                                    savingFor = CustomLocalDBDaoFunctionsDecl.ModifiedLocalDbFunctionsType.SAVED_LINKS
                                )
                            }
                        }) {
                            Icon(imageVector = Icons.Outlined.Link, contentDescription = null)
                            Spacer(modifier = Modifier.width(15.dp))
                            Text(
                                text = "Saved Links",
                                color = AlertDialogDefaults.textContentColor,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 18.sp
                            )
                        }
                        foldersTableData.forEach {
                            DropdownMenuItem(onClick = {
                                selectedFolderName.value = it.folderName
                                coroutineScope.launch {
                                    CustomLocalDBDaoFunctionsDecl.addANewLinkSpecificallyInFolders(
                                        title = titleTextField.value,
                                        webURL = linkTextFieldValue.value,
                                        noteForSaving = noteTextFieldValue.value,
                                        folderName = selectedFolderName.value,
                                        savingFor = CustomLocalDBDaoFunctionsDecl.ModifiedLocalDbFunctionsType.FOLDER_BASED_LINKS
                                    )
                                }
                            }) {
                                Icon(imageVector = Icons.Outlined.Folder, contentDescription = null)
                                Spacer(modifier = Modifier.width(15.dp))
                                Text(
                                    text = it.folderName,
                                    color = AlertDialogDefaults.textContentColor,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                    Button(colors = ButtonDefaults.buttonColors(containerColor = AlertDialogDefaults.titleContentColor),
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier
                            .padding(
                                end = 20.dp,
                                top = 20.dp,
                            )
                            .align(Alignment.End),
                        onClick = {
                            if (linkTextFieldValue.value.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "where's the link bruhh?",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                isDataExtractingForTheLink.value = true
                                onSaveBtnClick(
                                    titleTextField.value,
                                    linkTextFieldValue.value,
                                    noteTextFieldValue.value,
                                    selectedFolderName.value
                                )
                                shouldDialogBoxAppear.value = false
                            }
                        }) {
                        if (isDataExtractingForTheLink.value) {
                            Row {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.width(15.dp))
                                Text(
                                    text = "Extracting data...",
                                    color = AlertDialogDefaults.containerColor,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 16.sp
                                )
                            }
                        } else {
                            Text(
                                text = "Save",
                                color = AlertDialogDefaults.containerColor,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 16.sp
                            )
                        }
                    }
                    if (!isDataExtractingForTheLink.value) {
                        androidx.compose.material3.OutlinedButton(colors = ButtonDefaults.outlinedButtonColors(),
                            border = BorderStroke(
                                width = 1.dp,
                                color = AlertDialogDefaults.textContentColor
                            ),
                            shape = RoundedCornerShape(5.dp),
                            modifier = Modifier
                                .padding(
                                    end = 20.dp,
                                    top = 10.dp,
                                    bottom = 30.dp
                                )
                                .align(Alignment.End),
                            onClick = {
                                shouldDialogBoxAppear.value = false
                            }) {
                            Text(
                                text = "Cancel",
                                color = AlertDialogDefaults.textContentColor,
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