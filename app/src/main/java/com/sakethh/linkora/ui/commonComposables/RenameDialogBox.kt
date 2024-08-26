package com.sakethh.linkora.ui.commonComposables

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.OptionsBtmSheetType
import com.sakethh.linkora.ui.theme.LinkoraTheme

data class RenameDialogBoxParam(
    val shouldDialogBoxAppear: MutableState<Boolean>,
    val renameDialogBoxFor: OptionsBtmSheetType = OptionsBtmSheetType.FOLDER,
    val onNoteChangeClick: ((newNote: String) -> Unit),
    val onTitleChangeClick: ((newTitle: String) -> Unit),
    val existingFolderName: String?,
    val existingTitle: String,
    val existingNote: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameDialogBox(
    renameDialogBoxParam: RenameDialogBoxParam
) {
    val localContext = LocalContext.current
    if (renameDialogBoxParam.shouldDialogBoxAppear.value) {
        val newFolderOrTitleName = rememberSaveable(renameDialogBoxParam.existingTitle) {
            mutableStateOf(renameDialogBoxParam.existingTitle)
        }
        val newNote = rememberSaveable(renameDialogBoxParam.existingNote) {
            mutableStateOf(renameDialogBoxParam.existingNote)
        }
        LinkoraTheme {
            BasicAlertDialog(modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
                .background(AlertDialogDefaults.containerColor),
                properties = DialogProperties(usePlatformDefaultWidth = false),
                onDismissRequest = { renameDialogBoxParam.shouldDialogBoxAppear.value = false }) {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(15.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    item {
                        Text(
                            text = if (renameDialogBoxParam.renameDialogBoxFor != OptionsBtmSheetType.LINK)
                                LocalizedStrings.renameFolder.value.replace(
                                    "\$\$\$\$",
                                    renameDialogBoxParam.existingFolderName ?: ""
                                ) else
                                LocalizedStrings.changeLinkData.value,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 22.sp,
                            lineHeight = 27.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                    item {
                        OutlinedTextField(
                            label = {
                                Text(
                                    text = if (renameDialogBoxParam.renameDialogBoxFor == OptionsBtmSheetType.FOLDER)
                                        LocalizedStrings.newName.value
                                    else LocalizedStrings.newTitle.value,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 12.sp
                                )
                            },
                            textStyle = MaterialTheme.typography.titleSmall,
                            value = newFolderOrTitleName.value,
                            onValueChange = {
                                newFolderOrTitleName.value = it
                            }, modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            label = {
                                Text(
                                    text = LocalizedStrings.newNote.value,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 12.sp
                                )
                            },
                            textStyle = MaterialTheme.typography.titleSmall,
                            value = newNote.value,
                            onValueChange = {
                                newNote.value = it
                            }, modifier = Modifier.fillMaxWidth()
                        )
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
                                    text = LocalizedStrings.leaveAboveFieldEmptyIfYouDoNotWantToChangeTheNote.value,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 14.sp,
                                    lineHeight = 18.sp,
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                    }

                    item {
                        Button(modifier = Modifier
                            .fillMaxWidth()
                            .pulsateEffect(), onClick = {
                            renameDialogBoxParam.onNoteChangeClick(newNote.value)
                        }) {
                            Text(
                                text = LocalizedStrings.changeNoteOnly.value,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Button(modifier = Modifier
                            .fillMaxWidth()
                            .pulsateEffect(), onClick = {
                            if (newFolderOrTitleName.value.isNotEmpty()) {
                                if (newNote.value.isNotEmpty()) {
                                    renameDialogBoxParam.onTitleChangeClick(newFolderOrTitleName.value)
                                    renameDialogBoxParam.onNoteChangeClick(newNote.value)
                                } else {
                                    renameDialogBoxParam.onTitleChangeClick(newFolderOrTitleName.value)
                                }
                            } else {
                                Toast.makeText(
                                    localContext,
                                    LocalizedStrings.titleCannotBeEmpty.value,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                            Text(
                                text = LocalizedStrings.changeBothNameAndNote.value,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        OutlinedButton(modifier = Modifier
                            .fillMaxWidth()
                            .pulsateEffect(), onClick = {
                            renameDialogBoxParam.shouldDialogBoxAppear.value = false
                        }) {
                            Text(
                                text = LocalizedStrings.cancel.value,
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