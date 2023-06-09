package com.sakethh.linkora.btmSheet

import android.app.Activity
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import com.sakethh.linkora.localDB.ImportantLinks
import com.sakethh.linkora.localDB.LocalDataBase
import com.sakethh.linkora.screens.collections.specificScreen.SpecificScreenType
import com.sakethh.linkora.screens.collections.specificScreen.SpecificScreenVM
import com.sakethh.linkora.screens.home.composables.AddNewFolderDialogBox
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewLinkBtmSheet(
    _inIntentActivity: Boolean,
    shouldUIBeVisible: MutableState<Boolean>,
    inASpecificFolder: Boolean,
    _folderName: String = "",
    btmSheetState: SheetState,
) {
    val isDataExtractingForTheLink = rememberSaveable {
        mutableStateOf(false)
    }
    val inIntentActivity = rememberSaveable(inputs = arrayOf(_inIntentActivity)) {
        mutableStateOf(_inIntentActivity)
    }
    val folderName = rememberSaveable(inputs = arrayOf(_folderName)) {
        mutableStateOf(_folderName)
    }
    val coroutineScope = rememberCoroutineScope()
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
                if (inIntentActivity.value) {
                    btmSheetState.show()
                }
                btmSheetState.expand()
            },
                async {
                    if (inIntentActivity.value) {
                        shouldUIBeVisible.value = true
                    }
                },
                async {
                    if (inIntentActivity.value) {
                        SettingsScreenVM.Settings.readAllPreferencesValues()
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
                            Column {
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = if (inIntentActivity.value || !inASpecificFolder) "Selected folder:" else "Will be saved in:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 12.sp,
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = if (inIntentActivity.value || !inASpecificFolder) selectedFolder.value else folderName.value,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 20.sp,
                                    maxLines = 1,
                                    modifier = Modifier.fillMaxWidth(0.50f)
                                )
                            }
                            Button(modifier = Modifier.padding(
                                start = 20.dp, bottom = 10.dp
                            ), shape = RoundedCornerShape(10.dp), onClick = {
                                if (!inASpecificFolder && !inIntentActivity.value) {
                                    coroutineScope.launch {
                                        if (selectedFolder.value == "Saved Links" && !CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                                                .doesThisExistsInSavedLinks(
                                                    linkTextFieldValue.value
                                                )
                                        ) {
                                            CustomLocalDBDaoFunctionsDecl.addANewLinkSpecificallyInFolders(
                                                title = titleTextFieldValue.value,
                                                webURL = linkTextFieldValue.value,
                                                noteForSaving = noteTextFieldValue.value,
                                                folderName = selectedFolder.value,
                                                savingFor = CustomLocalDBDaoFunctionsDecl.ModifiedLocalDbFunctionsType.SAVED_LINKS,
                                                context = context
                                            )
                                        } else {
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(
                                                    context,
                                                    "given link already exists in the \"Saved Links\"",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }.invokeOnCompletion {
                                        if (linkTextFieldValue.value.isNotEmpty()) {
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

                                    coroutineScope.launch {
                                        if (selectedFolder.value != "Saved Links" && !CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                                                .doesThisLinkExistsInAFolder(
                                                    folderName = selectedFolder.value,
                                                    webURL = linkTextFieldValue.value
                                                )
                                        ) {
                                            CustomLocalDBDaoFunctionsDecl.addANewLinkSpecificallyInFolders(
                                                title = titleTextFieldValue.value,
                                                webURL = linkTextFieldValue.value,
                                                noteForSaving = noteTextFieldValue.value,
                                                folderName = selectedFolder.value,
                                                savingFor = CustomLocalDBDaoFunctionsDecl.ModifiedLocalDbFunctionsType.FOLDER_BASED_LINKS,
                                                context = context
                                            )
                                        } else {
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(
                                                    context,
                                                    "given link already exists in the \"$selectedFolder\"",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
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

                                } else if (inASpecificFolder && !inIntentActivity.value) {
                                    if (linkTextFieldValue.value.isNotEmpty()) {
                                        isDataExtractingForTheLink.value = true
                                    }
                                    when (SpecificScreenVM.screenType.value) {
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
                                                    context = context
                                                )
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

                                        SpecificScreenType.ARCHIVE_SCREEN -> {

                                        }

                                        SpecificScreenType.LINKS_SCREEN -> {
                                            coroutineScope.launch {
                                                if (selectedFolder.value == "Saved Links" && !CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                                                        .doesThisExistsInSavedLinks(
                                                            linkTextFieldValue.value
                                                        )
                                                ) {
                                                    CustomLocalDBDaoFunctionsDecl.addANewLinkSpecificallyInFolders(
                                                        title = titleTextFieldValue.value,
                                                        webURL = linkTextFieldValue.value,
                                                        noteForSaving = noteTextFieldValue.value,
                                                        folderName = selectedFolder.value,
                                                        savingFor = CustomLocalDBDaoFunctionsDecl.ModifiedLocalDbFunctionsType.SAVED_LINKS,
                                                        context = context
                                                    )
                                                } else {
                                                    withContext(Dispatchers.Main) {
                                                        Toast.makeText(
                                                            context,
                                                            "given link already exists in the \"Saved Links\"",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            }.invokeOnCompletion {
                                                if (linkTextFieldValue.value.isNotEmpty()) {
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

                                        SpecificScreenType.SPECIFIC_FOLDER_SCREEN -> {
                                            coroutineScope.launch {
                                                if (selectedFolder.value != "Saved Links" && !CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                                                        .doesThisLinkExistsInAFolder(
                                                            folderName = selectedFolder.value,
                                                            webURL = linkTextFieldValue.value
                                                        )
                                                ) {
                                                    CustomLocalDBDaoFunctionsDecl.addANewLinkSpecificallyInFolders(
                                                        title = titleTextFieldValue.value,
                                                        webURL = linkTextFieldValue.value,
                                                        noteForSaving = noteTextFieldValue.value,
                                                        folderName = selectedFolder.value,
                                                        savingFor = CustomLocalDBDaoFunctionsDecl.ModifiedLocalDbFunctionsType.FOLDER_BASED_LINKS,
                                                        context = context
                                                    )
                                                } else {
                                                    withContext(Dispatchers.Main) {
                                                        Toast.makeText(
                                                            context,
                                                            "given link already exists in the \"$selectedFolder\"",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            }.invokeOnCompletion {
                                                if (linkTextFieldValue.value.isNotEmpty()) {
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
                                    }
                                } else {
                                    if (Intent.ACTION_SEND == intentData.value?.action && intentData.value?.type != null && intentData.value!!.type == "text/plain") {
                                        isDataExtractingForTheLink.value = true
                                        if (selectedFolder.value == "Saved Links") {
                                            coroutineScope.launch {
                                                intentData.value!!.getStringExtra(Intent.EXTRA_TEXT)
                                                    ?.let {
                                                        if (!CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                                                                .doesThisExistsInSavedLinks(
                                                                    it
                                                                )
                                                        ) {
                                                            CustomLocalDBDaoFunctionsDecl.addANewLinkSpecificallyInFolders(
                                                                title = titleTextFieldValue.value,
                                                                webURL = it,
                                                                folderName = selectedFolder.value,
                                                                noteForSaving = noteTextFieldValue.value,
                                                                savingFor = CustomLocalDBDaoFunctionsDecl.ModifiedLocalDbFunctionsType.SAVED_LINKS,
                                                                context = context
                                                            )
                                                        } else {
                                                            withContext(Dispatchers.Main) {
                                                                Toast.makeText(
                                                                    context,
                                                                    "given link already exists in the \"Saved Links\"",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        }
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
                                                        if (!CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                                                                .doesThisLinkExistsInAFolder(
                                                                    webURL = it,
                                                                    folderName = selectedFolder.value
                                                                )
                                                        ) {
                                                            CustomLocalDBDaoFunctionsDecl.addANewLinkSpecificallyInFolders(
                                                                title = titleTextFieldValue.value,
                                                                webURL = it,
                                                                folderName = selectedFolder.value,
                                                                noteForSaving = noteTextFieldValue.value,
                                                                savingFor = CustomLocalDBDaoFunctionsDecl.ModifiedLocalDbFunctionsType.FOLDER_BASED_LINKS,
                                                                context = context
                                                            )
                                                        } else {
                                                            withContext(Dispatchers.Main) {
                                                                Toast.makeText(
                                                                    context,
                                                                    "given link already exists in the \"${selectedFolder.value}\"",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        }
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
                                }
                            }) {
                                if (isDataExtractingForTheLink.value) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.5.dp,
                                        color = LocalContentColor.current
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
                                OutlinedTextField(
                                    modifier = Modifier
                                        .padding(
                                            start = 20.dp, end = 20.dp, top = 20.dp
                                        )
                                        .fillMaxWidth(),
                                    readOnly = isDataExtractingForTheLink.value,
                                    label = {
                                        Text(
                                            text = "title of the link you're saving",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontSize = 12.sp
                                        )
                                    },
                                    textStyle = LocalTextStyle.current.copy(lineHeight = 22.sp),
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
                                    .padding(
                                        start = 20.dp, end = 20.dp, top = 15.dp
                                    )
                                    .fillMaxWidth(),
                                label = {
                                    Text(
                                        text = "add a note for why you're saving this link",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontSize = 12.sp
                                    )
                                },
                                textStyle = LocalTextStyle.current.copy(lineHeight = 22.sp),
                                shape = RoundedCornerShape(5.dp),
                                value = noteTextFieldValue.value,
                                onValueChange = {
                                    noteTextFieldValue.value = it
                                })
                        }
                        if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 20.dp, end = 20.dp, top = 15.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .requiredHeight(65.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.requiredHeight(65.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Info,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .padding(start = 20.dp, end = 22.dp)
                                                    .size(28.dp)
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .height(65.dp)
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Text(
                                                text = "Title will be automatically detected as this setting is enabled.",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontSize = 14.sp,
                                                modifier = Modifier.padding(
                                                    end = 20.dp
                                                ),
                                                lineHeight = 18.sp,
                                                textAlign = TextAlign.Start
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        if (inIntentActivity.value || !inASpecificFolder) {
                            item {
                                Text(
                                    text = "Save in:",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 24.sp,
                                    modifier = Modifier.padding(top = 20.dp, start = 20.dp)
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
                                    modifier = Modifier.padding(
                                        start = 25.dp,
                                        top = 20.dp,
                                        end = 25.dp
                                    )
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
                        }
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
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