package com.sakethh.linkora.ui.commonBtmSheets

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
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
import com.sakethh.linkora.ui.IntentActivityData
import com.sakethh.linkora.ui.commonComposables.AddNewFolderDialogBox
import com.sakethh.linkora.ui.commonComposables.AddNewFolderDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.theme.LinkoraTheme
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM
import com.sakethh.linkora.ui.viewmodels.collections.SpecificScreenType
import kotlinx.coroutines.launch


data class NewLinkBtmSheetUIParam @OptIn(ExperimentalMaterial3Api::class) constructor(
    val inIntentActivity: Boolean,
    val shouldUIBeVisible: MutableState<Boolean>,
    val screenType: SpecificScreenType,
    val btmSheetState: SheetState,
    val onLinkSaveClick: (isAutoDetectSelected: Boolean, webURL: String, title: String, note: String, selectedDefaultFolder: String?, selectedNonDefaultFolderID: Long?) -> Unit,
    val parentFolderID: Long?,
    val onFolderCreated: () -> Unit,
    val currentFolder: String = "",
    val isDataExtractingForTheLink: MutableState<Boolean>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewLinkBtmSheet(
    newLinkBtmSheetUIParam: NewLinkBtmSheetUIParam
) {
    val context = LocalContext.current
    val activity = context as Activity
    val inIntentActivity =
        rememberSaveable(inputs = arrayOf(newLinkBtmSheetUIParam.inIntentActivity)) {
            mutableStateOf(newLinkBtmSheetUIParam.inIntentActivity)
        }
    val coroutineScope = rememberCoroutineScope()
    val intent = activity.intent
    val intentData = rememberSaveable(inputs = arrayOf(intent)) {
        mutableStateOf(intent)
    }
    val shouldNewFolderDialogBoxAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val folderName = rememberSaveable {
        mutableStateOf(newLinkBtmSheetUIParam.currentFolder)
    }
    val selectedFolderID = rememberSaveable {
        mutableLongStateOf(0)
    }
    val isAutoDetectTitleEnabled = rememberSaveable {
        mutableStateOf(SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value)
    }
    LinkoraTheme {
        if (newLinkBtmSheetUIParam.shouldUIBeVisible.value) {
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
                if (!newLinkBtmSheetUIParam.isDataExtractingForTheLink.value) {
                    newLinkBtmSheetUIParam.shouldUIBeVisible.value = false
                    coroutineScope.launch {
                        if (newLinkBtmSheetUIParam.btmSheetState.isVisible) {
                            newLinkBtmSheetUIParam.btmSheetState.hide()
                        }
                    }.invokeOnCompletion {
                        if (inIntentActivity.value) {
                            activity.finishAndRemoveTask()
                        }
                    }
                }
            }, sheetState = newLinkBtmSheetUIParam.btmSheetState) {
                Scaffold(bottomBar = {
                    Surface(
                        color = BottomAppBarDefaults.containerColor,
                        contentColor = contentColorFor(BottomAppBarDefaults.containerColor),
                        modifier = Modifier
                            .background(BottomAppBarDefaults.containerColor)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .animateContentSize()
                            .navigationBarsPadding()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Spacer(modifier = Modifier.requiredHeight(15.dp))
                            Text(
                                text = if (inIntentActivity.value || newLinkBtmSheetUIParam.screenType == SpecificScreenType.ROOT_SCREEN) "Selected folder:" else "Link will be added in:",
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 10.dp)
                            )
                            Spacer(modifier = Modifier.requiredHeight(8.dp))
                            Text(
                                text = when (newLinkBtmSheetUIParam.screenType) {
                                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> "Important Links"
                                    SpecificScreenType.SAVED_LINKS_SCREEN -> "Saved Links"
                                    SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> folderName.value
                                    SpecificScreenType.INTENT_ACTIVITY -> selectedFolder.value
                                    SpecificScreenType.ROOT_SCREEN -> selectedFolder.value
                                    else -> ""
                                }, style = MaterialTheme.typography.titleMedium,
                                fontSize = 20.sp,
                                maxLines = 3,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .fillMaxWidth(0.90f),
                                lineHeight = 24.sp,
                                overflow = TextOverflow.Ellipsis
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(15.dp),
                                thickness = 0.5.dp,
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
                                        modifier = Modifier.clickable {
                                            if (!newLinkBtmSheetUIParam.isDataExtractingForTheLink.value) {
                                                isAutoDetectTitleEnabled.value =
                                                    !isAutoDetectTitleEnabled.value
                                            }
                                        }, verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        androidx.compose.material3.Checkbox(enabled = !newLinkBtmSheetUIParam.isDataExtractingForTheLink.value,
                                            checked = isAutoDetectTitleEnabled.value,
                                            onCheckedChange = {
                                                isAutoDetectTitleEnabled.value = it
                                            })
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
                                    Button(modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 20.dp)
                                        .pulsateEffect(), onClick = {
                                        newLinkBtmSheetUIParam.onLinkSaveClick(
                                            isAutoDetectTitleEnabled.value,
                                            linkTextFieldValue.value,
                                            titleTextFieldValue.value,
                                            noteTextFieldValue.value,
                                            selectedFolder.value,
                                            selectedFolderID.longValue
                                        )
                                    }) {
                                        if (newLinkBtmSheetUIParam.isDataExtractingForTheLink.value) {
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
                            OutlinedTextField(readOnly = newLinkBtmSheetUIParam.isDataExtractingForTheLink.value,
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
                                value = linkTextFieldValue.value,
                                onValueChange = {
                                    linkTextFieldValue.value = it
                                })
                        }
                        item {
                            Box(modifier = Modifier.animateContentSize()) {
                                if (!SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value && !isAutoDetectTitleEnabled.value) {
                                    OutlinedTextField(modifier = Modifier
                                        .padding(
                                            start = 20.dp, end = 20.dp, top = 20.dp
                                        )
                                        .fillMaxWidth(),
                                        readOnly = newLinkBtmSheetUIParam.isDataExtractingForTheLink.value,
                                        label = {
                                            Text(
                                                text = "title of the link you're saving",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontSize = 12.sp
                                            )
                                        },
                                        textStyle = LocalTextStyle.current.copy(lineHeight = 22.sp),
                                        value = titleTextFieldValue.value,
                                        onValueChange = {
                                            titleTextFieldValue.value = it
                                        })
                                }
                            }
                        }
                        item {
                            OutlinedTextField(readOnly = newLinkBtmSheetUIParam.isDataExtractingForTheLink.value,
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
                                value = noteTextFieldValue.value,
                                onValueChange = {
                                    noteTextFieldValue.value = it
                                })
                        }
                        if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value) {
                            item {
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
                        if (newLinkBtmSheetUIParam.screenType == SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN) {
                            item {
                                HorizontalDivider(
                                    modifier = Modifier.padding(20.dp),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(0.25f)
                                )
                                OutlinedButton(border = BorderStroke(
                                    1.dp,
                                    contentColorFor(MaterialTheme.colorScheme.surface)
                                ),
                                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                        containerColor =
                                        MaterialTheme.colorScheme.surface,
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    modifier = Modifier.padding(
                                        start = 20.dp,
                                        end = 20.dp
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
                        }
                        if (inIntentActivity.value || newLinkBtmSheetUIParam.screenType == SpecificScreenType.ROOT_SCREEN) {
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
                                OutlinedButton(border = BorderStroke(
                                    1.dp,
                                    contentColorFor(MaterialTheme.colorScheme.surface)
                                ),
                                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                        containerColor =
                                        MaterialTheme.colorScheme.surface,
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    modifier = Modifier.padding(
                                        start = 20.dp, end = 20.dp
                                    ),
                                    onClick = {
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
                                HorizontalDivider(
                                    modifier = Modifier.padding(
                                        start = 25.dp, top = 20.dp, end = 25.dp
                                    ),
                                    thickness = 1.dp
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
                            item {
                                SelectableFolderUIComponent(
                                    onClick = { selectedFolder.value = "Important Links" },
                                    folderName = "Important Links",
                                    imageVector = Icons.Outlined.StarOutline,
                                    _isComponentSelected = selectedFolder.value == "Important Links"
                                )
                            }
                            items(IntentActivityData.foldersData.value) {
                                SelectableFolderUIComponent(
                                    onClick = {
                                        selectedFolderID.longValue = it.id
                                        selectedFolder.value = it.folderName
                                    },
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
                AddNewFolderDialogBoxParam(
                    shouldDialogBoxAppear = shouldNewFolderDialogBoxAppear,
                    newFolderData = { folderName, folderID ->
                        selectedFolderID.longValue = folderID
                        selectedFolder.value = folderName
                    },
                    onCreated = {
                        newLinkBtmSheetUIParam.onFolderCreated()
                    },
                    parentFolderID = newLinkBtmSheetUIParam.parentFolderID,
                    inAChildFolderScreen = newLinkBtmSheetUIParam.screenType == SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN
                )
            )
        }
        BackHandler {
            if (!newLinkBtmSheetUIParam.isDataExtractingForTheLink.value && inIntentActivity.value) {
                newLinkBtmSheetUIParam.shouldUIBeVisible.value = false
                coroutineScope.launch {
                    if (newLinkBtmSheetUIParam.btmSheetState.isVisible) {
                        newLinkBtmSheetUIParam.btmSheetState.hide()
                    }
                }.invokeOnCompletion {
                    if (inIntentActivity.value) {
                        activity.finishAndRemoveTask()
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
                    tint = if (isComponentSelected.value) MaterialTheme.colorScheme.primary else LocalContentColor.current,
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
                    .fillMaxWidth(0.80f),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = folderName,
                    color = if (isComponentSelected.value) MaterialTheme.colorScheme.primary else LocalContentColor.current,
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
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = if (isComponentSelected.value) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(start = 25.dp, end = 25.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(0.25f)
        )
    }
}