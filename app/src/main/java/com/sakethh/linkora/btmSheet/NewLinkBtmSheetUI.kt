package com.sakethh.linkora.btmSheet

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.BottomAppBarDefaults
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
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sakethh.linkora.IntentActivityData
import com.sakethh.linkora.customComposables.AddNewFolderDialogBox
import com.sakethh.linkora.localDB.CustomFunctionsForLocalDB
import com.sakethh.linkora.localDB.ImportantLinks
import com.sakethh.linkora.localDB.LocalDataBase
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreenType
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewLinkBtmSheet(
    _inIntentActivity: Boolean,
    shouldUIBeVisible: MutableState<Boolean>,
    screenType: SpecificScreenType,
    _folderName: String = "",
    btmSheetState: SheetState,
    onLinkSaved: () -> Unit,
    onFolderCreated: () -> Unit,
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
    val shouldNewFolderDialogBoxAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val customFunctionsForLocalDB: CustomFunctionsForLocalDB = viewModel()
    val localDensity = LocalDensity.current
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
                        coroutineScope.launch {
                            SettingsScreenVM.Settings.readAllPreferencesValues(context)
                        }.invokeOnCompletion {
                            if (SettingsScreenVM.Settings.isSendCrashReportsEnabled.value) {
                                val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
                                firebaseCrashlytics.setCrashlyticsCollectionEnabled(true)
                                firebaseCrashlytics.log("logged in :- v${SettingsScreenVM.currentAppVersion}")
                            }
                        }
                    }
                },
                async {
                    coroutineScope.launch {
                        if (inIntentActivity.value) {
                            CustomFunctionsForLocalDB.localDB =
                                LocalDataBase.getLocalDB(context)
                        }
                    }.invokeOnCompletion {
                        coroutineScope.launch {
                            CustomFunctionsForLocalDB.localDB.crudDao().getAllFolders()
                                .collect {
                                    IntentActivityData.foldersData.value = it
                                }
                        }
                    }
                })
        }
    }
    val isAutoDetectTitleEnabled = rememberSaveable {
        mutableStateOf(SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value)
    }
    LinkoraTheme {
        if (shouldUIBeVisible.value) {
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
                    Surface(
                        color = BottomAppBarDefaults.containerColor,
                        contentColor = contentColorFor(BottomAppBarDefaults.containerColor),
                        modifier = Modifier
                            .background(BottomAppBarDefaults.containerColor)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .animateContentSize()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Spacer(modifier = Modifier.requiredHeight(15.dp))
                            Text(
                                text = if (inIntentActivity.value || screenType == SpecificScreenType.ROOT_SCREEN) "Selected folder:" else "Will be saved in:",
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 10.dp)
                            )
                            Spacer(modifier = Modifier.requiredHeight(8.dp))
                            Text(
                                text = if (inIntentActivity.value || screenType == SpecificScreenType.ROOT_SCREEN) selectedFolder.value else folderName.value,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 20.sp,
                                maxLines = 3,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .fillMaxWidth(0.90f),
                                lineHeight = 24.sp,
                                overflow = TextOverflow.Ellipsis
                            )
                            Divider(
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(15.dp),
                                color = MaterialTheme.colorScheme.outline.copy(0.25f)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (!SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value) {
                                    Row(
                                        modifier = Modifier
                                            .clickable {
                                                if (!isDataExtractingForTheLink.value) {
                                                    isAutoDetectTitleEnabled.value =
                                                        !isAutoDetectTitleEnabled.value
                                                }
                                            },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        androidx.compose.material3.Checkbox(
                                            enabled = !isDataExtractingForTheLink.value,
                                            checked = isAutoDetectTitleEnabled.value,
                                            onCheckedChange = {
                                                isAutoDetectTitleEnabled.value = it
                                            }
                                        )
                                        Text(
                                            text = "Force Auto-detect title",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                ) {
                                    Button(
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .padding(end = 20.dp),
                                        onClick = {
                                            if (linkTextFieldValue.value.isNotEmpty()) {
                                                isDataExtractingForTheLink.value = true
                                            }
                                            when (screenType) {
                                                SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                                                    customFunctionsForLocalDB.importantLinkTableUpdater(
                                                        ImportantLinks(
                                                            title = titleTextFieldValue.value,
                                                            webURL = linkTextFieldValue.value,
                                                            infoForSaving = noteTextFieldValue.value,
                                                            baseURL = "",
                                                            imgURL = ""
                                                        ),
                                                        context = context,
                                                        inImportantLinksScreen = true,
                                                        autoDetectTitle = isAutoDetectTitleEnabled.value,
                                                        onTaskCompleted = {
                                                            onLinkSaved()
                                                            isDataExtractingForTheLink.value = false
                                                            coroutineScope.launch {
                                                                if (btmSheetState.isVisible) {
                                                                    btmSheetState.hide()
                                                                }
                                                            }.invokeOnCompletion {
                                                                shouldUIBeVisible.value = false
                                                                if (inIntentActivity.value) {
                                                                    activity?.finishAndRemoveTask()
                                                                }
                                                            }
                                                        }
                                                    )
                                                }

                                                SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {

                                                }

                                                SpecificScreenType.SAVED_LINKS_SCREEN -> {
                                                    customFunctionsForLocalDB.addANewLinkSpecificallyInFolders(
                                                        title = titleTextFieldValue.value,
                                                        webURL = linkTextFieldValue.value,
                                                        noteForSaving = noteTextFieldValue.value,
                                                        folderName = selectedFolder.value,
                                                        savingFor = CustomFunctionsForLocalDB.CustomFunctionsForLocalDBType.SAVED_LINKS,
                                                        context = context,
                                                        autoDetectTitle = isAutoDetectTitleEnabled.value,
                                                        onTaskCompleted = {
                                                            onLinkSaved()
                                                            isDataExtractingForTheLink.value = false
                                                            coroutineScope.launch {
                                                                if (btmSheetState.isVisible) {
                                                                    btmSheetState.hide()
                                                                }
                                                            }.invokeOnCompletion {
                                                                shouldUIBeVisible.value = false
                                                                if (inIntentActivity.value) {
                                                                    activity?.finishAndRemoveTask()
                                                                }
                                                            }
                                                        }
                                                    )
                                                }

                                                SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                                                    customFunctionsForLocalDB.addANewLinkSpecificallyInFolders(
                                                        title = titleTextFieldValue.value,
                                                        webURL = linkTextFieldValue.value,
                                                        noteForSaving = noteTextFieldValue.value,
                                                        folderName = folderName.value,
                                                        savingFor = CustomFunctionsForLocalDB.CustomFunctionsForLocalDBType.FOLDER_BASED_LINKS,
                                                        context = context,
                                                        autoDetectTitle = isAutoDetectTitleEnabled.value,
                                                        onTaskCompleted = {
                                                            onLinkSaved()
                                                            isDataExtractingForTheLink.value = false
                                                            coroutineScope.launch {
                                                                if (btmSheetState.isVisible) {
                                                                    btmSheetState.hide()
                                                                }
                                                            }.invokeOnCompletion {
                                                                shouldUIBeVisible.value = false
                                                                if (inIntentActivity.value) {
                                                                    activity?.finishAndRemoveTask()
                                                                }
                                                            }
                                                        }
                                                    )
                                                }

                                                SpecificScreenType.INTENT_ACTIVITY -> {
                                                    if (Intent.ACTION_SEND == intentData.value?.action && intentData.value?.type != null && intentData.value!!.type == "text/plain") {
                                                        isDataExtractingForTheLink.value = true
                                                        if (selectedFolder.value == "Saved Links") {
                                                            intentData.value!!.getStringExtra(
                                                                Intent.EXTRA_TEXT
                                                            )
                                                                ?.let {
                                                                    linkTextFieldValue.value =
                                                                        it
                                                                    customFunctionsForLocalDB.addANewLinkSpecificallyInFolders(
                                                                        title = titleTextFieldValue.value,
                                                                        webURL = linkTextFieldValue.value,
                                                                        folderName = selectedFolder.value,
                                                                        noteForSaving = noteTextFieldValue.value,
                                                                        savingFor = CustomFunctionsForLocalDB.CustomFunctionsForLocalDBType.SAVED_LINKS,
                                                                        context = context,
                                                                        autoDetectTitle = isAutoDetectTitleEnabled.value,
                                                                        onTaskCompleted = {
                                                                            isDataExtractingForTheLink.value =
                                                                                false
                                                                            coroutineScope.launch {
                                                                                if (btmSheetState.isVisible) {
                                                                                    btmSheetState.hide()
                                                                                }
                                                                            }.invokeOnCompletion {
                                                                                shouldUIBeVisible.value =
                                                                                    false
                                                                                if (inIntentActivity.value) {
                                                                                    activity?.finishAndRemoveTask()
                                                                                }
                                                                            }
                                                                        }
                                                                    )
                                                                }
                                                        } else {
                                                            intentData.value!!.getStringExtra(
                                                                Intent.EXTRA_TEXT
                                                            )
                                                                ?.let {
                                                                    linkTextFieldValue.value =
                                                                        it
                                                                    customFunctionsForLocalDB.addANewLinkSpecificallyInFolders(
                                                                        title = titleTextFieldValue.value,
                                                                        webURL = linkTextFieldValue.value,
                                                                        folderName = selectedFolder.value,
                                                                        noteForSaving = noteTextFieldValue.value,
                                                                        savingFor = CustomFunctionsForLocalDB.CustomFunctionsForLocalDBType.FOLDER_BASED_LINKS,
                                                                        context = context,
                                                                        autoDetectTitle = isAutoDetectTitleEnabled.value,
                                                                        onTaskCompleted = {
                                                                            isDataExtractingForTheLink.value =
                                                                                false
                                                                            coroutineScope.launch {
                                                                                if (btmSheetState.isVisible) {
                                                                                    btmSheetState.hide()
                                                                                }
                                                                            }.invokeOnCompletion {
                                                                                shouldUIBeVisible.value =
                                                                                    false
                                                                                if (inIntentActivity.value) {
                                                                                    activity?.finishAndRemoveTask()
                                                                                }
                                                                            }
                                                                        }
                                                                    )
                                                                }
                                                        }
                                                    }
                                                }

                                                SpecificScreenType.ROOT_SCREEN -> {
                                                    if (selectedFolder.value == "Saved Links") {
                                                        isDataExtractingForTheLink.value = true
                                                        customFunctionsForLocalDB.addANewLinkSpecificallyInFolders(
                                                            title = titleTextFieldValue.value,
                                                            webURL = linkTextFieldValue.value,
                                                            noteForSaving = noteTextFieldValue.value,
                                                            folderName = selectedFolder.value,
                                                            savingFor = CustomFunctionsForLocalDB.CustomFunctionsForLocalDBType.SAVED_LINKS,
                                                            context = context,
                                                            autoDetectTitle = isAutoDetectTitleEnabled.value,
                                                            onTaskCompleted = {
                                                                isDataExtractingForTheLink.value =
                                                                    false
                                                                coroutineScope.launch {
                                                                    if (btmSheetState.isVisible) {
                                                                        btmSheetState.hide()
                                                                    }
                                                                }.invokeOnCompletion {
                                                                    shouldUIBeVisible.value = false
                                                                    if (inIntentActivity.value) {
                                                                        activity?.finishAndRemoveTask()
                                                                    }
                                                                }
                                                            }
                                                        )
                                                    } else {
                                                        customFunctionsForLocalDB.addANewLinkSpecificallyInFolders(
                                                            title = titleTextFieldValue.value,
                                                            webURL = linkTextFieldValue.value,
                                                            folderName = selectedFolder.value,
                                                            noteForSaving = noteTextFieldValue.value,
                                                            savingFor = CustomFunctionsForLocalDB.CustomFunctionsForLocalDBType.FOLDER_BASED_LINKS,
                                                            context = context,
                                                            autoDetectTitle = isAutoDetectTitleEnabled.value,
                                                            onTaskCompleted = {
                                                                isDataExtractingForTheLink.value =
                                                                    false
                                                                coroutineScope.launch {
                                                                    if (btmSheetState.isVisible) {
                                                                        btmSheetState.hide()
                                                                    }
                                                                }.invokeOnCompletion {
                                                                    shouldUIBeVisible.value = false
                                                                    if (inIntentActivity.value) {
                                                                        activity?.finishAndRemoveTask()
                                                                    }
                                                                }
                                                            }
                                                        )
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
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
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
                        if (inIntentActivity.value || screenType == SpecificScreenType.ROOT_SCREEN) {
                            item {
                                Text(
                                    text = "Save in:",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 24.sp,
                                    modifier = Modifier.padding(top = 20.dp, start = 20.dp)
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.requiredHeight(20.dp))
                            }
                            item {
                                OutlinedButton(modifier = Modifier.padding(
                                    start = 20.dp, end = 20.dp
                                ), onClick = {
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
                                SelectableFolderUIComponent(
                                    onClick = { selectedFolder.value = "Saved Links" },
                                    folderName = "Saved Links",
                                    imageVector = Icons.Outlined.Link,
                                    _isComponentSelected = selectedFolder.value == "Saved Links"
                                )
                            }
                            items(IntentActivityData.foldersData.value) {
                                SelectableFolderUIComponent(
                                    onClick = { selectedFolder.value = it.folderName },
                                    folderName = it.folderName,
                                    imageVector = Icons.Outlined.Folder,
                                    _isComponentSelected = selectedFolder.value == it.folderName
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.requiredHeight(20.dp))
                        }
                    }
                }
            }
            AddNewFolderDialogBox(
                shouldDialogBoxAppear = shouldNewFolderDialogBoxAppear,
                newFolderName = {
                    selectedFolder.value = it
                },
                onCreated = {
                    onFolderCreated()
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
fun SelectableFolderUIComponent(
    onClick: () -> Unit,
    folderName: String,
    imageVector: ImageVector,
    _isComponentSelected: Boolean,
    _forBtmSheetUI: Boolean = false,
) {
    val isComponentSelected = rememberSaveable(inputs = arrayOf(_isComponentSelected)) {
        mutableStateOf(_isComponentSelected)
    }
    val forBtmSheetUI = rememberSaveable(inputs = arrayOf(_forBtmSheetUI)) {
        mutableStateOf(_forBtmSheetUI)
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
                        .padding(
                            start = 20.dp,
                            bottom = 20.dp,
                            end = 20.dp,
                            top = if (forBtmSheetUI.value) 0.dp else 20.dp
                        )
                        .size(28.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.80f), contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = folderName,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    maxLines = if (forBtmSheetUI.value) 6 else 1,
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
            thickness = 1.dp,
            modifier = Modifier.padding(start = 25.dp, end = 25.dp),
            color = MaterialTheme.colorScheme.outline.copy(0.25f)
        )
    }
}