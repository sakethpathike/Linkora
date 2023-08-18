package com.sakethh.linkora.screens.home.composables

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import com.sakethh.linkora.btmSheet.SelectableFolderUIComponent
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.localDB.ImportantLinks
import com.sakethh.linkora.screens.collections.specificScreen.SpecificScreenType
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.launch
import okhttp3.internal.trimSubstring

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewLinkDialogBox(
    shouldDialogBoxAppear: MutableState<Boolean>,
    screenType: SpecificScreenType,
    specificFolderName: String,
) {
    val isDataExtractingForTheLink = rememberSaveable {
        mutableStateOf(false)
    }
    val foldersTableData =
        CustomLocalDBDaoFunctionsDecl.localDB.localDBData().getAllFolders().collectAsState(
            initial = emptyList()
        ).value
    val context = LocalContext.current
    val isDropDownMenuIconClicked = rememberSaveable {
        mutableStateOf(false)
    }
    val isCreateANewFolderIconClicked = rememberSaveable {
        mutableStateOf(false)
    }
    val btmModalSheetState = androidx.compose.material3.rememberModalBottomSheetState()
    if (isDataExtractingForTheLink.value) {
        isDropDownMenuIconClicked.value = false
    }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    if (shouldDialogBoxAppear.value) {
        val linkTextFieldValue = rememberSaveable {
            mutableStateOf("")
        }
        val titleTextFieldValue = rememberSaveable {
            mutableStateOf("")
        }
        val noteTextFieldValue = rememberSaveable {
            mutableStateOf("")
        }
        val selectedFolderName = rememberSaveable {
            mutableStateOf("Saved Links")
        }
        LinkoraTheme {
            AlertDialog(modifier = Modifier
                .wrapContentHeight()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
                onDismissRequest = {
                    if (!isDataExtractingForTheLink.value) {
                        shouldDialogBoxAppear.value = false
                    }
                }) {
                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    Text(
                        text = "Save new link",
                        color = MaterialTheme.colorScheme.onSurface,
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 12.sp
                                )
                            },
                            textStyle = MaterialTheme.typography.titleSmall,
                            singleLine = true,
                            shape = RoundedCornerShape(5.dp),
                            value = titleTextFieldValue.value,
                            onValueChange = {
                                titleTextFieldValue.value = it
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    if (screenType == SpecificScreenType.ROOT_SCREEN) {
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
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(top = 15.dp)
                            )
                            Row(modifier = Modifier
                                .padding(start = 15.dp, end = 15.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .border(
                                    shape = RoundedCornerShape(50.dp),
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                .clickable {
                                    if (!isDataExtractingForTheLink.value) {
                                        isDropDownMenuIconClicked.value = true
                                    }
                                }) {
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (selectedFolderName.value.length <= 9) selectedFolderName.value else selectedFolderName.value.trimSubstring(
                                        0,
                                        6
                                    ) + "...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 18.sp,
                                    maxLines = 1,
                                    modifier = Modifier.padding(start = 15.dp, top = 15.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                IconButton(onClick = {
                                    isDropDownMenuIconClicked.value = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                        }
                    }
                    Button(colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .padding(
                                end = 20.dp,
                                top = 20.dp,
                            )
                            .align(Alignment.End),
                        onClick = {
                            if (!isDataExtractingForTheLink.value && linkTextFieldValue.value.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "where's the link bruhh?",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (!isDataExtractingForTheLink.value && linkTextFieldValue.value.isNotEmpty()) {
                                isDataExtractingForTheLink.value = true
                                when (screenType) {
                                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                                        coroutineScope.launch {
                                            CustomLocalDBDaoFunctionsDecl.importantLinkTableUpdater(
                                                ImportantLinks(
                                                    title = titleTextFieldValue.value,
                                                    webURL = linkTextFieldValue.value,
                                                    infoForSaving = noteTextFieldValue.value,
                                                    baseURL = "",
                                                    imgURL = ""
                                                ),
                                                context = context,
                                                inImportantLinksScreen = true
                                            )
                                        }.invokeOnCompletion {
                                            if (linkTextFieldValue.value.isNotEmpty()) {
                                                isDataExtractingForTheLink.value = false
                                                shouldDialogBoxAppear.value = false
                                            }
                                        }
                                    }

                                    SpecificScreenType.ARCHIVE_SCREEN -> {

                                    }

                                    SpecificScreenType.LINKS_SCREEN -> {
                                        coroutineScope.launch {
                                            CustomLocalDBDaoFunctionsDecl.addANewLinkSpecificallyInFolders(
                                                title = titleTextFieldValue.value,
                                                webURL = linkTextFieldValue.value,
                                                noteForSaving = noteTextFieldValue.value,
                                                folderName = selectedFolderName.value,
                                                savingFor = CustomLocalDBDaoFunctionsDecl.ModifiedLocalDbFunctionsType.SAVED_LINKS,
                                                context = context
                                            )
                                        }.invokeOnCompletion {
                                            if (linkTextFieldValue.value.isNotEmpty()) {
                                                isDataExtractingForTheLink.value = false
                                                shouldDialogBoxAppear.value = false
                                            }
                                        }
                                    }

                                    SpecificScreenType.SPECIFIC_FOLDER_SCREEN -> {
                                        coroutineScope.launch {
                                            CustomLocalDBDaoFunctionsDecl.addANewLinkSpecificallyInFolders(
                                                title = titleTextFieldValue.value,
                                                webURL = linkTextFieldValue.value,
                                                noteForSaving = noteTextFieldValue.value,
                                                folderName = specificFolderName,
                                                savingFor = CustomLocalDBDaoFunctionsDecl.ModifiedLocalDbFunctionsType.FOLDER_BASED_LINKS,
                                                context = context
                                            )
                                        }.invokeOnCompletion {
                                            if (linkTextFieldValue.value.isNotEmpty()) {
                                                isDataExtractingForTheLink.value = false
                                                shouldDialogBoxAppear.value = false
                                            }
                                        }
                                    }

                                    SpecificScreenType.INTENT_ACTIVITY -> {

                                    }

                                    SpecificScreenType.ROOT_SCREEN -> {
                                        if (selectedFolderName.value == "Saved Links") {
                                            isDataExtractingForTheLink.value = true
                                            coroutineScope.launch {
                                                CustomLocalDBDaoFunctionsDecl.addANewLinkSpecificallyInFolders(
                                                    title = titleTextFieldValue.value,
                                                    webURL = linkTextFieldValue.value,
                                                    folderName = selectedFolderName.value,
                                                    noteForSaving = noteTextFieldValue.value,
                                                    savingFor = CustomLocalDBDaoFunctionsDecl.ModifiedLocalDbFunctionsType.SAVED_LINKS,
                                                    context = context
                                                )
                                            }.invokeOnCompletion {
                                                isDataExtractingForTheLink.value = false
                                                shouldDialogBoxAppear.value = false
                                            }
                                        } else {
                                            coroutineScope.launch {
                                                CustomLocalDBDaoFunctionsDecl.addANewLinkSpecificallyInFolders(
                                                    title = titleTextFieldValue.value,
                                                    webURL = linkTextFieldValue.value,
                                                    folderName = selectedFolderName.value,
                                                    noteForSaving = noteTextFieldValue.value,
                                                    savingFor = CustomLocalDBDaoFunctionsDecl.ModifiedLocalDbFunctionsType.FOLDER_BASED_LINKS,
                                                    context = context
                                                )
                                            }.invokeOnCompletion {
                                                isDataExtractingForTheLink.value = false
                                                shouldDialogBoxAppear.value = false
                                            }
                                        }
                                    }
                                }
                                if (!isDataExtractingForTheLink.value) {
                                    shouldDialogBoxAppear.value = false
                                }
                            }
                        }) {
                        if (isDataExtractingForTheLink.value) {
                            Column {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.5.dp,
                                    color = LocalContentColor.current
                                )
                            }
                        } else {
                            Text(
                                text = "Save",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 16.sp
                            )
                        }
                    }
                    if (!isDataExtractingForTheLink.value) {
                        androidx.compose.material3.OutlinedButton(colors = ButtonDefaults.outlinedButtonColors(),
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
                                shouldDialogBoxAppear.value = false
                            }) {
                            Text(
                                text = "Cancel",
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 16.sp
                            )
                        }
                    }
                    if (isDataExtractingForTheLink.value) {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
                if (isDropDownMenuIconClicked.value) {
                    ModalBottomSheet(sheetState = btmModalSheetState, onDismissRequest = {
                        coroutineScope.launch {
                            if (btmModalSheetState.isVisible) {
                                btmModalSheetState.hide()
                            }
                        }.invokeOnCompletion {
                            isDropDownMenuIconClicked.value = false
                        }
                    }) {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Save in :",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 24.sp,
                                    modifier = Modifier
                                        .padding(
                                            start = 20.dp
                                        )
                                )
                                Icon(
                                    imageVector = Icons.Outlined.CreateNewFolder,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .clickable {
                                            isCreateANewFolderIconClicked.value = true
                                        }
                                        .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                                        .size(30.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Divider(
                                modifier = Modifier.padding(
                                    start = 20.dp,
                                    end = 65.dp
                                ),
                                color = MaterialTheme.colorScheme.outline.copy(0.25f)
                            )
                            SelectableFolderUIComponent(
                                onClick = {
                                    selectedFolderName.value = "Saved Links"
                                    coroutineScope.launch {
                                        if (btmModalSheetState.isVisible) {
                                            btmModalSheetState.hide()
                                        }
                                    }.invokeOnCompletion {
                                        coroutineScope.launch {
                                            if (btmModalSheetState.isVisible) {
                                                btmModalSheetState.hide()
                                            }
                                        }.invokeOnCompletion {
                                            isDropDownMenuIconClicked.value = false
                                        }
                                    }
                                },
                                folderName = "Saved Links",
                                imageVector = Icons.Outlined.Link,
                                _isComponentSelected = selectedFolderName.value == "Saved Links"
                            )
                            foldersTableData.forEach {
                                SelectableFolderUIComponent(
                                    onClick = {
                                        selectedFolderName.value = it.folderName
                                        coroutineScope.launch {
                                            if (btmModalSheetState.isVisible) {
                                                btmModalSheetState.hide()
                                            }
                                        }.invokeOnCompletion {
                                            isDropDownMenuIconClicked.value = false
                                        }
                                    },
                                    folderName = it.folderName,
                                    imageVector = Icons.Outlined.Folder,
                                    _isComponentSelected = selectedFolderName.value == it.folderName
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
                AddNewFolderDialogBox(
                    coroutineScope = coroutineScope,
                    shouldDialogBoxAppear = isCreateANewFolderIconClicked,
                    newFolderName = {
                        selectedFolderName.value = it
                    })
            }
        }
    }
}