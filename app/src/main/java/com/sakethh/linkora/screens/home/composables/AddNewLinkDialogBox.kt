package com.sakethh.linkora.screens.home.composables

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewLinkDialogBox(
    shouldDialogBoxAppear: MutableState<Boolean>,
    onSaveBtnClick: (title: String, webURL: String, note: String) -> Unit,
) {
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
    val scrollState = rememberScrollState()
    if (shouldDialogBoxAppear.value) {
        LinkoraTheme {
            AlertDialog(modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(AlertDialogDefaults.containerColor),
                onDismissRequest = { shouldDialogBoxAppear.value = false }) {
                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    Text(
                        text = "Save new link",
                        color = AlertDialogDefaults.textContentColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(start = 20.dp, top = 30.dp)
                    )
                    OutlinedTextField(
                        maxLines = 1,
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
                            maxLines = 1,
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
                        maxLines = 1,
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
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
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
                                onSaveBtnClick(
                                    titleTextField.value,
                                    linkTextFieldValue.value,
                                    noteTextFieldValue.value
                                )
                                shouldDialogBoxAppear.value = false
                            }
                        }) {
                        Text(
                            text = "Save",
                            color = AlertDialogDefaults.containerColor,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                    }
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