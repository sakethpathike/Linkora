package com.sakethh.linkora.ui.commonComposables

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.ui.theme.LinkoraTheme
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM
import com.sakethh.linkora.ui.viewmodels.collections.CollectionsScreenVM
import com.sakethh.linkora.ui.viewmodels.collections.SpecificScreenType
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewLinkDialogBox(
    shouldDialogBoxAppear: MutableState<Boolean>,
    screenType: SpecificScreenType,
    onSaveClick: (isAutoDetectSelected: Boolean, webURL: String, title: String, note: String, selectedDefaultFolder: String?, selectedNonDefaultFolderID: Long?) -> Unit,
    isDataExtractingForTheLink: Boolean,
    onFolderCreateClick: (folderName: String, folderNote: String) -> Unit
) {
    val isDropDownMenuIconClicked = rememberSaveable {
        mutableStateOf(false)
    }
    val isAutoDetectTitleEnabled = rememberSaveable {
        mutableStateOf(SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value)
    }
    val isCreateANewFolderIconClicked = rememberSaveable {
        mutableStateOf(false)
    }
    val btmModalSheetState =
        androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (isDataExtractingForTheLink) {
        isDropDownMenuIconClicked.value = false
    }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as Activity
    val intent = activity.intent
    val intentData = rememberSaveable(inputs = arrayOf(intent)) {
        mutableStateOf(intent)
    }
    val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
    LaunchedEffect(key1 = Unit) {
        awaitAll(async {
            if (screenType == SpecificScreenType.INTENT_ACTIVITY) {
                this.launch {
                    SettingsScreenVM.Settings.readAllPreferencesValues(context)
                }.invokeOnCompletion {
                    firebaseCrashlytics.setCrashlyticsCollectionEnabled(SettingsScreenVM.Settings.isSendCrashReportsEnabled.value)
                }
            }
        })
    }
    if (shouldDialogBoxAppear.value) {
        val linkTextFieldValue = if (screenType == SpecificScreenType.INTENT_ACTIVITY) {
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
        val noteTextFieldValue = rememberSaveable {
            mutableStateOf("")
        }
        val selectedFolderName = rememberSaveable {
            mutableStateOf("Saved Links")
        }
        val selectedFolderID = rememberSaveable {
            mutableLongStateOf(0)
        }
        LinkoraTheme {
            BasicAlertDialog(
                onDismissRequest = {
                    if (!isDataExtractingForTheLink) {
                        shouldDialogBoxAppear.value = false
                    }
                }, modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AlertDialogDefaults.containerColor),
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.verticalScroll(scrollState)) {
                        Text(
                            text = when (screenType) {
                                SpecificScreenType.IMPORTANT_LINKS_SCREEN -> "Add a new link in \"Important Links\""
                                SpecificScreenType.SAVED_LINKS_SCREEN -> "Add a new link in \"Saved Links\""
                                SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> "Add a new link in \"${CollectionsScreenVM.currentClickedFolderData.value.folderName}\""
                                else -> "Add a new link"
                            },
                            color = AlertDialogDefaults.titleContentColor,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 22.sp,
                            modifier = Modifier.padding(start = 20.dp, top = 30.dp, end = 20.dp),
                            lineHeight = 28.sp
                        )
                        OutlinedTextField(readOnly = isDataExtractingForTheLink,
                            modifier = Modifier
                                .padding(
                                    start = 20.dp, end = 20.dp, top = 20.dp
                                )
                                .fillMaxWidth(),
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
                        Box(modifier = Modifier.animateContentSize()) {
                            if (!SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value && !isAutoDetectTitleEnabled.value) {
                                OutlinedTextField(readOnly = isDataExtractingForTheLink,
                                    modifier = Modifier
                                        .padding(
                                            start = 20.dp, end = 20.dp, top = 15.dp
                                        )
                                        .fillMaxWidth(),
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
                                    value = titleTextFieldValue.value,
                                    onValueChange = {
                                        titleTextFieldValue.value = it
                                    })
                            }
                        }
                        OutlinedTextField(readOnly = isDataExtractingForTheLink,
                            modifier = Modifier
                                .padding(
                                    start = 20.dp, end = 20.dp, top = 15.dp
                                )
                                .fillMaxWidth(),
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
                            value = noteTextFieldValue.value,
                            onValueChange = {
                                noteTextFieldValue.value = it
                            })
                        if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value) {
                            Card(
                                border = BorderStroke(
                                    1.dp,
                                    contentColorFor(MaterialTheme.colorScheme.surface)
                                ),
                                colors = CardDefaults.cardColors(containerColor = AlertDialogDefaults.containerColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 20.dp, end = 20.dp, top = 15.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .padding(
                                            top = 10.dp, bottom = 10.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        androidx.compose.material3.Icon(
                                            imageVector = Icons.Outlined.Info,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .padding(
                                                    start = 10.dp, end = 10.dp
                                                )
                                        )
                                    }
                                    Text(
                                        text = "Title will be automatically detected as this setting is enabled.",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontSize = 14.sp,
                                        lineHeight = 18.sp,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier
                                            .padding(end = 10.dp)
                                    )
                                }
                            }
                        }
                        if (screenType == SpecificScreenType.ROOT_SCREEN || screenType == SpecificScreenType.INTENT_ACTIVITY) {
                            Row(
                                Modifier.padding(
                                    start = 20.dp, end = 20.dp, top = 20.dp
                                ), horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Add in",
                                    color = contentColorFor(backgroundColor = AlertDialogDefaults.containerColor),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(top = 15.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                OutlinedButton(modifier = Modifier.pulsateEffect(),
                                    border = BorderStroke(
                                        width = 1.dp, color = MaterialTheme.colorScheme.primary
                                    ),
                                    onClick = {
                                        if (!isDataExtractingForTheLink) {
                                            isDropDownMenuIconClicked.value = true
                                            coroutineScope.launch {
                                                btmModalSheetState.expand()
                                            }
                                        }
                                    }) {
                                    Text(
                                        text = selectedFolderName.value,
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontSize = 18.sp,
                                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.fillMaxWidth(0.80f)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Icon(tint = MaterialTheme.colorScheme.primary,
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        modifier = Modifier.clickable {
                                            if (!isDataExtractingForTheLink) {
                                                isDropDownMenuIconClicked.value = true
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                }
                            }
                        }
                        if (!SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value) {
                            Row(
                                modifier = Modifier
                                    .padding(top = 20.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        if (!isDataExtractingForTheLink) {
                                            isAutoDetectTitleEnabled.value =
                                                !isAutoDetectTitleEnabled.value
                                        }
                                    }
                                    .padding(
                                        start = 10.dp, end = 20.dp
                                    ), verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material3.Checkbox(enabled = !isDataExtractingForTheLink,
                                    checked = isAutoDetectTitleEnabled.value,
                                    onCheckedChange = {
                                        isAutoDetectTitleEnabled.value = it
                                    })
                                Text(
                                    text = "Force Auto-detect title",
                                    color = contentColorFor(backgroundColor = AlertDialogDefaults.containerColor),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 16.sp
                                )
                            }
                        }
                        if (!isDataExtractingForTheLink) {
                            OutlinedButton(colors = ButtonDefaults.outlinedButtonColors(),
                                border = BorderStroke(
                                    width = 1.dp, color = MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier
                                    .padding(
                                        end = 20.dp, top = 10.dp, start = 20.dp
                                    )
                                    .fillMaxWidth()
                                    .align(Alignment.End)
                                    .pulsateEffect(),
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
                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .padding(
                                        end = 20.dp,
                                        top = 10.dp,
                                        start = 20.dp
                                    )
                                    .fillMaxWidth()
                                    .align(Alignment.End)
                                    .pulsateEffect(),
                                onClick = {
                                    if (screenType == SpecificScreenType.INTENT_ACTIVITY) {
                                        //LocalDatabase.localDB = LocalDatabase.getLocalDB(context)
                                    }
                                    onSaveClick(
                                        isAutoDetectTitleEnabled.value,
                                        linkTextFieldValue.value,
                                        titleTextFieldValue.value,
                                        noteTextFieldValue.value,
                                        selectedFolderName.value,
                                        selectedFolderID.longValue
                                    )
                                }) {
                                Text(
                                    text = "Save",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 16.sp
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.height(30.dp))
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 20.dp, end = 20.dp)
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(20.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(0.25f)
                        )
                    }
                }
                if (isDropDownMenuIconClicked.value) {
                    val foldersTableData = emptyList<FoldersTable>()
                    ModalBottomSheet(sheetState = btmModalSheetState, onDismissRequest = {
                        coroutineScope.launch {
                            if (btmModalSheetState.isVisible) {
                                btmModalSheetState.hide()
                            }
                        }.invokeOnCompletion {
                            isDropDownMenuIconClicked.value = false
                        }
                    }) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .navigationBarsPadding()
                        ) {
                            item {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Add in:",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontSize = 24.sp,
                                        modifier = Modifier.padding(
                                            start = 20.dp
                                        )
                                    )
                                    Icon(imageVector = Icons.Outlined.CreateNewFolder,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .clickable {
                                                isCreateANewFolderIconClicked.value = true
                                            }
                                            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                                            .size(30.dp),
                                        tint = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                            item {
                                HorizontalDivider(
                                    modifier = Modifier.padding(
                                        start = 20.dp, end = 65.dp
                                    ), color = MaterialTheme.colorScheme.outline.copy(0.25f)
                                )
                            }
                            item {
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
                            }
                            item {
                                SelectableFolderUIComponent(
                                    onClick = {
                                        selectedFolderName.value = "Important Links"
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
                                    folderName = "Important Links",
                                    imageVector = Icons.Outlined.StarOutline,
                                    _isComponentSelected = selectedFolderName.value == "Important Links"
                                )
                            }
                            items(foldersTableData) {
                                SelectableFolderUIComponent(
                                    onClick = {
                                        selectedFolderName.value = it.folderName
                                        selectedFolderID.longValue = it.id
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
                            item {
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }
                }
            }
            AddNewFolderDialogBox(
                AddNewFolderDialogBoxParam(
                    shouldDialogBoxAppear = isCreateANewFolderIconClicked,
                    newFolderData = { folderName, folderID ->
                        selectedFolderName.value = folderName
                        selectedFolderID.longValue = folderID
                    },
                    onCreated = {
                        coroutineScope.launch {
                            if (btmModalSheetState.isVisible) {
                                btmModalSheetState.hide()
                            }
                        }.invokeOnCompletion {
                            isDropDownMenuIconClicked.value = false
                        }
                    },
                    inAChildFolderScreen = screenType == SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN,
                    onFolderCreateClick = { folderName, folderNote ->
                        onFolderCreateClick(folderName, folderNote)
                    }
                )
            )
        }
    }
}