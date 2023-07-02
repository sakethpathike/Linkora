package com.sakethh.linkora.btmSheet

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.localDB.LocalDataBase
import com.sakethh.linkora.screens.home.composables.AddNewFolderDialogBox
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewLinkBtmSheet(_inIntentActivity: Boolean) {
    val inIntentActivity = rememberSaveable(inputs = arrayOf(_inIntentActivity)) {
        mutableStateOf(_inIntentActivity)
    }
    val btmSheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val shouldUIBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val activity = context as? Activity
    val intent = activity?.intent
    val intentData = rememberSaveable(inputs = arrayOf(intent)) {
        mutableStateOf(intent)
    }
    val noteTextFieldValue = rememberSaveable {
        mutableStateOf("")
    }
    val linkTextFieldValue = if (inIntentActivity.value) {
        rememberSaveable(
            inputs = arrayOf(
                intentData.value?.getStringExtra(
                    Intent.EXTRA_TEXT
                ).toString()
            )
        ) {
            mutableStateOf(intentData.value?.getStringExtra(Intent.EXTRA_TEXT).toString())
        }
    } else {
        rememberSaveable {
            mutableStateOf("")
        }
    }
    val titleTextFieldValue = rememberSaveable {
        mutableStateOf("")
    }
    val selectedFolder = rememberSaveable {
        mutableStateOf("Saved Links")
    }
    val isDataExtractingForTheLink = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldNewFolderDialogBoxAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val foldersData =
        CustomLocalDBDaoFunctionsDecl.localDB.localDBData().getAllFolders().collectAsState(
            initial = emptyList()
        ).value
    LaunchedEffect(key1 = Unit) {
        this.launch {
            awaitAll(async {
                btmSheetState.show()
                btmSheetState.expand()
            },
                async { shouldUIBeVisible.value = true },
                async {
                    if (inIntentActivity.value) {
                        SettingsScreenVM.Settings.readAllPreferencesValues()
                    }
                },
                async {
                    if (inIntentActivity.value) {
                        CustomLocalDBDaoFunctionsDecl.localDB =
                            LocalDataBase.getLocalDB(context = context)
                    }
                })
        }
    }
    LinkoraTheme {
        if (shouldUIBeVisible.value) {
            ModalBottomSheet(onDismissRequest = {
                if (!isDataExtractingForTheLink.value) {
                    shouldUIBeVisible.value = false
                    coroutineScope.launch {
                        if (btmSheetState.isVisible) {
                            btmSheetState.hide()
                        }
                    }.invokeOnCompletion {
                        if (inIntentActivity.value) {
                            activity?.finishAndRemoveTask()
                        }
                    }
                }
            }, sheetState = btmSheetState) {
                Scaffold(bottomBar = {
                    BottomAppBar(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Spacer(modifier = Modifier.width(15.dp))
                            Column() {
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = "Selected folder:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 12.sp,
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = selectedFolder.value,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 20.sp,
                                    maxLines = 1,
                                    modifier = Modifier.fillMaxWidth(0.50f)
                                )
                            }
                            Button(modifier = Modifier.padding(
                                start = 20.dp, bottom = 10.dp
                            ), shape = RoundedCornerShape(10.dp), onClick = {
                                if (Intent.ACTION_SEND == intentData.value?.action && intentData.value?.type != null && intentData.value!!.type == "text/plain") {
                                    isDataExtractingForTheLink.value = true
                                    if (selectedFolder.value == "Saved Links") {
                                        coroutineScope.launch {
                                            intentData.value!!.getStringExtra(Intent.EXTRA_TEXT)
                                                ?.let {
                                                    CustomLocalDBDaoFunctionsDecl.addANewLinkSpecificallyInFolders(
                                                        title = titleTextFieldValue.value,
                                                        webURL = it,
                                                        folderName = selectedFolder.value,
                                                        noteForSaving = noteTextFieldValue.value,
                                                        savingFor = CustomLocalDBDaoFunctionsDecl.ModifiedLocalDbFunctionsType.SAVED_LINKS
                                                    )
                                                }
                                        }.invokeOnCompletion {
                                            isDataExtractingForTheLink.value = false
                                            shouldUIBeVisible.value = false
                                            coroutineScope.launch {
                                                if (btmSheetState.isVisible) {
                                                    btmSheetState.hide()
                                                }
                                            }.invokeOnCompletion {
                                                if (inIntentActivity.value) {
                                                    activity?.finishAndRemoveTask()
                                                }
                                            }
                                        }
                                    } else {
                                        coroutineScope.launch {
                                            intentData.value!!.getStringExtra(Intent.EXTRA_TEXT)
                                                ?.let {
                                                    CustomLocalDBDaoFunctionsDecl.addANewLinkSpecificallyInFolders(
                                                        title = titleTextFieldValue.value,
                                                        webURL = it,
                                                        folderName = selectedFolder.value,
                                                        noteForSaving = noteTextFieldValue.value,
                                                        savingFor = CustomLocalDBDaoFunctionsDecl.ModifiedLocalDbFunctionsType.FOLDER_BASED_LINKS
                                                    )
                                                }
                                        }.invokeOnCompletion {
                                            isDataExtractingForTheLink.value = false
                                            shouldUIBeVisible.value = false
                                            coroutineScope.launch {
                                                if (btmSheetState.isVisible) {
                                                    btmSheetState.hide()
                                                }
                                            }.invokeOnCompletion {
                                                if (inIntentActivity.value) {
                                                    activity?.finishAndRemoveTask()
                                                }
                                            }
                                        }
                                    }
                                }
                            }) {
                                if (isDataExtractingForTheLink.value) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.5.dp
                                    )
                                } else {
                                    Text(
                                        text = "Save",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(15.dp))
                        }
                    }
                }) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxWidth()
                    ) {
                        item {
                            Text(
                                text = "Save a new link",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 24.sp,
                                modifier = Modifier.padding(start = 20.dp)
                            )
                        }
                        item {
                            OutlinedTextField(readOnly = isDataExtractingForTheLink.value,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 20.dp, end = 20.dp, top = 20.dp
                                    ),
                                label = {
                                    Text(
                                        text = "URL",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontSize = 12.sp
                                    )
                                },
                                textStyle = MaterialTheme.typography.titleSmall,
                                shape = RoundedCornerShape(5.dp),
                                value = linkTextFieldValue.value,
                                onValueChange = {
                                    linkTextFieldValue.value = it
                                })
                        }
                        if (!SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value) {
                            item {
                                OutlinedTextField(readOnly = isDataExtractingForTheLink.value,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 20.dp, end = 20.dp, top = 20.dp
                                        ),
                                    label = {
                                        Text(
                                            text = "title of the link you're saving",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontSize = 12.sp
                                        )
                                    },
                                    textStyle = MaterialTheme.typography.titleSmall,
                                    shape = RoundedCornerShape(5.dp),
                                    value = titleTextFieldValue.value,
                                    onValueChange = {
                                        titleTextFieldValue.value = it
                                    })
                            }
                        }
                        item {
                            OutlinedTextField(readOnly = isDataExtractingForTheLink.value,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 20.dp, end = 20.dp, top = 15.dp
                                    ),
                                label = {
                                    Text(
                                        text = "add a note for why you're saving this link",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontSize = 12.sp
                                    )
                                },
                                textStyle = MaterialTheme.typography.titleSmall,
                                shape = RoundedCornerShape(5.dp),
                                value = noteTextFieldValue.value,
                                onValueChange = {
                                    noteTextFieldValue.value = it
                                })
                        }
                        item {
                            Text(
                                text = "Save in:",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 24.sp,
                                modifier = Modifier.padding(top = 30.dp, start = 20.dp)
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                        item {
                            OutlinedButton(modifier = Modifier.padding(
                                start = 20.dp, end = 20.dp
                            ), shape = RoundedCornerShape(10.dp), onClick = {
                                shouldNewFolderDialogBoxAppear.value = true
                            }) {
                                Text(
                                    text = "Create a new folder",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxWidth()
                                )

                            }

                        }
                        item {
                            Divider(
                                thickness = 1.dp,
                                modifier = Modifier.padding(start = 25.dp, top = 20.dp, end = 25.dp)
                            )
                        }
                        item {
                            FolderForBtmSheetIndividualComponent(
                                onClick = { selectedFolder.value = "Saved Links" },
                                folderName = "Saved Links",
                                imageVector = Icons.Outlined.Link,
                                _isComponentSelected = selectedFolder.value == "Saved Links"
                            )
                        }
                        items(foldersData) {
                            FolderForBtmSheetIndividualComponent(
                                onClick = { selectedFolder.value = it.folderName },
                                folderName = it.folderName,
                                imageVector = Icons.Outlined.Folder,
                                _isComponentSelected = selectedFolder.value == it.folderName
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
        AddNewFolderDialogBox(
            coroutineScope = coroutineScope,
            shouldDialogBoxAppear = shouldNewFolderDialogBoxAppear,
            newFolderName = {
                selectedFolder.value = it
            }
        )
    }
    BackHandler {
        if (!isDataExtractingForTheLink.value && inIntentActivity.value) {
            shouldUIBeVisible.value = false
            coroutineScope.launch {
                if (btmSheetState.isVisible) {
                    btmSheetState.hide()
                }
            }.invokeOnCompletion {
                if (inIntentActivity.value) {
                    activity?.finishAndRemoveTask()
                }
            }
        }
    }
}

@Composable
fun FolderForBtmSheetIndividualComponent(
    onClick: () -> Unit,
    folderName: String,
    imageVector: ImageVector,
    _isComponentSelected: Boolean,
) {
    val isComponentSelected = rememberSaveable(inputs = arrayOf(_isComponentSelected)) {
        mutableStateOf(_isComponentSelected)
    }
    Column {
        Row(modifier = Modifier
            .clickable {
                onClick()
            }
            .fillMaxWidth()
            .requiredHeight(75.dp)) {
            Box(
                modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(20.dp)
                        .size(28.dp)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.80f),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = folderName,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (isComponentSelected.value) {
                Box(
                    modifier = Modifier
                        .requiredHeight(75.dp)
                        .fillMaxWidth(), contentAlignment = Alignment.CenterEnd
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier
                                .size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                }
            }
        }
        Divider(
            thickness = 1.dp, modifier = Modifier.padding(start = 25.dp, end = 25.dp)
        )
    }
}