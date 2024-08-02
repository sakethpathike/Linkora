package com.sakethh.linkora.ui.commonComposables

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
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
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.AddANewLinkDialogBoxVM
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.ShelfBtmSheetVM
import com.sakethh.linkora.ui.screens.collections.CollectionsScreenVM
import com.sakethh.linkora.ui.screens.collections.specific.SpecificScreenType
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun AddANewLinkDialogBox(
    shouldDialogBoxAppear: MutableState<Boolean>,
    screenType: SpecificScreenType,
    onSaveClick: (isAutoDetectSelected: Boolean, webURL: String, title: String, note: String, selectedDefaultFolder: String?, selectedNonDefaultFolderID: Long?) -> Unit,
    isDataExtractingForTheLink: Boolean,
    onFolderCreateClick: (folderName: String, folderNote: String) -> Unit
) {
    val addANewLinkDialogBoxVM: AddANewLinkDialogBoxVM = hiltViewModel()
    val parentFoldersData = addANewLinkDialogBoxVM.foldersRepo.getAllRootFolders().collectAsState(
        initial = emptyList()
    )
    val isDropDownMenuIconClicked = rememberSaveable {
        mutableStateOf(false)
    }
    val isAutoDetectTitleEnabled = rememberSaveable {
        mutableStateOf(SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value)
    }
    val isCreateANewFolderIconClicked = rememberSaveable {
        mutableStateOf(false)
    }
    if (isDataExtractingForTheLink) {
        isDropDownMenuIconClicked.value = false
    }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as Activity
    val intent = activity.intent
    val intentData = rememberSaveable(inputs = arrayOf(intent)) {
        mutableStateOf(intent)
    }
    val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
    val isChildFoldersBottomSheetExpanded = mutableStateOf(false)
    val btmSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
        val childFolders =
            addANewLinkDialogBoxVM.childFolders.collectAsStateWithLifecycle()
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
                    LazyColumn(
                        modifier = Modifier
                            .animateContentSize()
                            .fillMaxSize()
                            .navigationBarsPadding()
                    ) {
                        item {
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
                                modifier = Modifier.padding(
                                    start = 20.dp,
                                    top = 30.dp,
                                    end = 20.dp
                                ),
                                lineHeight = 28.sp
                            )
                        }
                        item {
                            OutlinedTextField(readOnly = isDataExtractingForTheLink,
                                modifier = Modifier
                                    .padding(
                                        start = 20.dp, end = 20.dp, top = 20.dp
                                    )
                                    .fillMaxWidth(),
                                label = {
                                    Text(
                                        text = "Link address",
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
                        }
                        item {
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
                        }

                        item {
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
                        }
                        item {
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
                                            Icon(
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
                        }
                        item {
                            if (screenType == SpecificScreenType.ROOT_SCREEN || screenType == SpecificScreenType.INTENT_ACTIVITY) {
                                    Text(
                                        text = "Add in",
                                        color = contentColorFor(backgroundColor = AlertDialogDefaults.containerColor),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontSize = 18.sp,
                                        modifier = Modifier
                                            .padding(start = 20.dp, top = 20.dp, end = 20.dp)
                                    )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 20.dp, end = 20.dp, top = 10.dp)
                                ) {
                                    FilledTonalButton(modifier = Modifier
                                        .pulsateEffect()
                                        .fillMaxWidth(0.8f),
                                        onClick = {
                                            if (!isDataExtractingForTheLink) {
                                                isDropDownMenuIconClicked.value =
                                                    !isDropDownMenuIconClicked.value
                                                addANewLinkDialogBoxVM.subFoldersList.clear()
                                            }
                                        }) {
                                        Text(
                                            text = selectedFolderName.value,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontSize = 18.sp,
                                            maxLines = 1, overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(5.dp))
                                    FilledTonalIconButton(
                                        modifier = Modifier.pulsateEffect(
                                            0.75f
                                        ), onClick = {
                                            if (!isDataExtractingForTheLink) {
                                                isDropDownMenuIconClicked.value =
                                                    !isDropDownMenuIconClicked.value
                                                addANewLinkDialogBoxVM.subFoldersList.clear()
                                            }
                                        }) {
                                        Icon(
                                            imageVector = if (isDropDownMenuIconClicked.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }

                        if (isDropDownMenuIconClicked.value) {
                            item {
                                SelectableFolderUIComponent(
                                    onClick = {
                                        isDropDownMenuIconClicked.value = false
                                        selectedFolderName.value = "Saved Links"
                                    },
                                    folderName = "Saved Links",
                                    imageVector = Icons.Outlined.Link,
                                    isComponentSelected = selectedFolderName.value == "Saved Links"
                                )
                            }
                            item {
                                SelectableFolderUIComponent(
                                    onClick = {
                                        selectedFolderName.value = "Important Links"
                                        isDropDownMenuIconClicked.value = false
                                    },
                                    folderName = "Important Links",
                                    imageVector = Icons.Outlined.StarOutline,
                                    isComponentSelected = selectedFolderName.value == "Important Links"
                                )
                            }
                            items(parentFoldersData.value) {
                                FolderSelectorComponent(
                                    onItemClick = {
                                        selectedFolderName.value = it.folderName
                                        selectedFolderID.longValue = it.id
                                            isDropDownMenuIconClicked.value = false
                                    },
                                    isCurrentFolderSelected = mutableStateOf(it.id == selectedFolderID.longValue),
                                    folderName = it.folderName,
                                    onSubDirectoryIconClick = {
                                        addANewLinkDialogBoxVM.changeParentFolderId(it.id)
                                        addANewLinkDialogBoxVM.subFoldersList.add(it)
                                        isChildFoldersBottomSheetExpanded.value = true
                                        coroutineScope.launch {
                                            btmSheetState.expand()
                                        }
                                        selectedFolderName.value = it.folderName
                                        selectedFolderID.longValue = it.id
                                    }
                                )
                            }
                            if (!isDropDownMenuIconClicked.value) {
                                item {
                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                            }
                        }
                        item {
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
                        }
                        item {
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
                                        .pulsateEffect(),
                                    onClick = {
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
                        }
                        item {
                            HorizontalDivider(
                                modifier = Modifier.padding(20.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(0.25f)
                            )
                        }
                    }
                }
                if (isChildFoldersBottomSheetExpanded.value) {
                    ModalBottomSheet(sheetState = btmSheetState, onDismissRequest = {
                        addANewLinkDialogBoxVM.subFoldersList.clear()
                        isChildFoldersBottomSheetExpanded.value = false
                    }) {
                        LazyColumn(
                            Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            stickyHeader {
                                Column {
                                    TopAppBar(title = {
                                        Text(
                                            text = addANewLinkDialogBoxVM.subFoldersList.last().folderName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontSize = 24.sp
                                        )
                                    })
                                    LazyRow(
                                        modifier = Modifier.padding(
                                            start = 15.dp,
                                            end = 15.dp,
                                            bottom = 15.dp
                                        ),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        item {
                                            Text(
                                                text = "/",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontSize = 16.sp
                                            )
                                        }
                                        item {
                                            Icon(
                                                imageVector = Icons.Default.ArrowRight,
                                                contentDescription = ""
                                            )
                                        }
                                        itemsIndexed(addANewLinkDialogBoxVM.subFoldersList) { index, subFolder ->
                                            Text(
                                                text = subFolder.folderName,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontSize = 16.sp,
                                                modifier = Modifier.clickable {
                                                    if (addANewLinkDialogBoxVM.subFoldersList.indexOf(
                                                            subFolder
                                                        ) != addANewLinkDialogBoxVM.subFoldersList.lastIndex
                                                    ) {
                                                        addANewLinkDialogBoxVM.subFoldersList.subList(
                                                            index + 1,
                                                            addANewLinkDialogBoxVM.subFoldersList.lastIndex + 1
                                                        ).clear()
                                                        addANewLinkDialogBoxVM.changeParentFolderId(
                                                            subFolder.id
                                                        )
                                                    }
                                                }
                                            )
                                            if (subFolder.id != addANewLinkDialogBoxVM.subFoldersList.last().id) {
                                                Icon(
                                                    imageVector = Icons.Default.ArrowRight,
                                                    contentDescription = ""
                                                )
                                                Text(
                                                    text = ShelfBtmSheetVM.selectedShelfData.shelfName,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontSize = 16.sp
                                                )
                                            }
                                        }
                                    }
                                    HorizontalDivider(color = LocalContentColor.current.copy(0.25f))
                                    Spacer(modifier = Modifier.height(15.dp))
                                }
                            }
                            if (childFolders.value.isNotEmpty()) {
                                items(childFolders.value) {
                                    FolderSelectorComponent(
                                        onItemClick = {
                                            selectedFolderName.value = it.folderName
                                            selectedFolderID.longValue = it.id
                                            isDropDownMenuIconClicked.value = false
                                            addANewLinkDialogBoxVM.subFoldersList.clear()
                                            coroutineScope.launch {
                                                btmSheetState.hide()
                                            }
                                            isChildFoldersBottomSheetExpanded.value = false
                                        },
                                        isCurrentFolderSelected = mutableStateOf(it.id == selectedFolderID.longValue),
                                        folderName = it.folderName,
                                        onSubDirectoryIconClick = {
                                            selectedFolderName.value = it.folderName
                                            addANewLinkDialogBoxVM.subFoldersList.add(it)
                                            addANewLinkDialogBoxVM.changeParentFolderId(it.id)
                                            selectedFolderID.longValue = it.id
                                        }
                                    )
                                }
                            } else {
                                item {
                                    Text(
                                        text = "This folder has no sub-folders.",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontSize = 24.sp,
                                        lineHeight = 36.sp,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(15.dp)
                                    )
                                }
                                item {
                                    Button(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(15.dp),
                                        onClick = {
                                            isDropDownMenuIconClicked.value = false
                                            addANewLinkDialogBoxVM.subFoldersList.clear()
                                            coroutineScope.launch {
                                                btmSheetState.hide()
                                            }
                                            isChildFoldersBottomSheetExpanded.value = false
                                        }) {
                                        Text(
                                            text = "Save in This Folder",
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    }
                                }
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
                        isDropDownMenuIconClicked.value = false
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

@Composable
private fun FolderSelectorComponent(
    onItemClick: () -> Unit,
    isCurrentFolderSelected: MutableState<Boolean>,
    folderName: String,
    onSubDirectoryIconClick: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onItemClick()
        }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                tint = if (isCurrentFolderSelected.value) MaterialTheme.colorScheme.primary else LocalContentColor.current,
                imageVector = Icons.Outlined.Folder,
                contentDescription = null,
                modifier = Modifier
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 0.dp
                    )
                    .size(28.dp)
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isCurrentFolderSelected.value) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = if (isCurrentFolderSelected.value) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                    IconButton(onClick = {
                        onSubDirectoryIconClick()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.SubdirectoryArrowRight,
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }
        }
        Text(
            text = folderName,
            color = if (isCurrentFolderSelected.value) MaterialTheme.colorScheme.primary else LocalContentColor.current,
            style = MaterialTheme.typography.titleSmall,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            maxLines = 1, modifier = Modifier
                .padding(
                    start = 20.dp, end = 20.dp
                ),
            overflow = TextOverflow.Ellipsis
        )
        HorizontalDivider(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(0.1f)
        )
    }
}