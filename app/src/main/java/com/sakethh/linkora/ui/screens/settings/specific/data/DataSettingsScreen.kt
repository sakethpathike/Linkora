package com.sakethh.linkora.ui.screens.settings.specific.data

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Html
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.LocalizedStrings.data
import com.sakethh.linkora.LocalizedStrings.deleteEntireDataPermanently
import com.sakethh.linkora.LocalizedStrings.deleteEntireDataPermanentlyDesc
import com.sakethh.linkora.LocalizedStrings.deletedEntireDataFromTheLocalDatabase
import com.sakethh.linkora.data.local.export.ExportRequestInfo
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.commonComposables.DataDialogBoxType
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBox
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBoxParam
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.screens.settings.SettingsUIElement
import com.sakethh.linkora.ui.screens.settings.composables.ImportExceptionDialogBox
import com.sakethh.linkora.ui.screens.settings.composables.PermissionDialog
import com.sakethh.linkora.ui.screens.settings.composables.RegularSettingComponent
import com.sakethh.linkora.ui.screens.settings.composables.SpecificScreenScaffold
import com.sakethh.linkora.utils.openApplicationSettings
import com.sakethh.linkora.worker.refreshLinks.RefreshLinksWorker
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
@Composable
fun DataSettingsScreen(navController: NavController, settingsScreenVM: SettingsScreenVM) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        settingsScreenVM.eventChannel.collectLatest {
            when (it) {
                is CommonUiEvent.ShowToast -> {
                    Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }
    val importModalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isPermissionDialogBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val runtimePermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            isPermissionDialogBoxVisible.value = !it
        })
    val isImportExceptionBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }

    val shouldDeleteEntireDialogBoxAppear = rememberSaveable { mutableStateOf(false) }
    var importBasedOnJsonFormat = rememberSaveable {
        false
    }
    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                settingsScreenVM.importData(
                    uri, context, importBasedOnJsonFormat
                )
            }
        }


    val successfulRefreshLinkCount =
        RefreshLinksWorker.successfulRefreshLinksCount

    SpecificScreenScaffold(
        topAppBarText = data.value,
        navController = navController
    ) { paddingValues, topAppBarScrollBehaviour ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(topAppBarScrollBehaviour.nestedScrollConnection)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            item {
                Spacer(modifier = Modifier)
            }
            item {
                Text(
                    text = LocalizedStrings.import.value,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                )
            }

            item {
                RegularSettingComponent(
                    SettingsUIElement(
                        isIconNeeded = rememberSaveable { mutableStateOf(true) },
                        title = LocalizedStrings.importUsingJsonFile.value,
                        doesDescriptionExists = true,
                        description = LocalizedStrings.importUsingJsonFileDesc.value,
                        isSwitchNeeded = false,
                        isSwitchEnabled = SettingsPreference.shouldFollowDynamicTheming,
                        onSwitchStateChange = {
                            importBasedOnJsonFormat = true
                            activityResultLauncher.launch("application/json")
                        },
                        icon = Icons.Default.DataObject,
                        shouldFilledIconBeUsed = rememberSaveable { mutableStateOf(true) }
                    )
                )
            }
            item {
                RegularSettingComponent(
                    SettingsUIElement(
                        isIconNeeded = rememberSaveable { mutableStateOf(true) },
                        title = LocalizedStrings.importDataFromHtmlFile.value,
                        doesDescriptionExists = true,
                        description = LocalizedStrings.importDataFromHtmlFileDesc.value,
                        isSwitchNeeded = false,
                        isSwitchEnabled = SettingsPreference.shouldFollowDynamicTheming,
                        onSwitchStateChange = {
                            importBasedOnJsonFormat = false
                            activityResultLauncher.launch("text/html")
                        },
                        icon = Icons.Default.Html,
                        shouldFilledIconBeUsed = rememberSaveable { mutableStateOf(true) }
                    )
                )
            }
            item {
                Text(
                    text = LocalizedStrings.export.value,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                )
            }
            item {
                RegularSettingComponent(
                    SettingsUIElement(
                        isIconNeeded = rememberSaveable { mutableStateOf(true) },
                        title = LocalizedStrings.exportDataAsJson.value,
                        doesDescriptionExists = true,
                        description = LocalizedStrings.exportDataAsJsonDesc.value,
                        isSwitchNeeded = false,
                        isSwitchEnabled = SettingsPreference.shouldFollowDynamicTheming,
                        onSwitchStateChange = {
                            ExportRequestInfo.isHTMLBasedRequest.value = false
                            settingsScreenVM.exportDataToAFile(
                                context = context,
                                isDialogBoxVisible = isPermissionDialogBoxVisible,
                                runtimePermission = runtimePermission,
                                exportInHTMLFormat = false
                            )
                        },
                        icon = Icons.Default.DataObject,
                        shouldFilledIconBeUsed = rememberSaveable { mutableStateOf(true) }
                    )
                )
            }
            item {
                RegularSettingComponent(
                    SettingsUIElement(
                        isIconNeeded = rememberSaveable { mutableStateOf(true) },
                        title = LocalizedStrings.exportDataAsHtml.value,
                        doesDescriptionExists = true,
                        description = LocalizedStrings.exportDataAsHtmlDesc.value,
                        isSwitchNeeded = false,
                        isSwitchEnabled = SettingsPreference.shouldFollowDynamicTheming,
                        onSwitchStateChange = {
                            ExportRequestInfo.isHTMLBasedRequest.value = true
                            settingsScreenVM.exportDataToAFile(
                                context = context,
                                isDialogBoxVisible = isPermissionDialogBoxVisible,
                                runtimePermission = runtimePermission,
                                exportInHTMLFormat = true
                            )
                        },
                        icon = Icons.Default.Html,
                        shouldFilledIconBeUsed = rememberSaveable { mutableStateOf(true) }
                    )
                )
            }

            item {
                HorizontalDivider(
                    Modifier.padding(
                        start = 15.dp,
                        end = 15.dp,
                        bottom = 30.dp,
                    ),
                    color = DividerDefaults.color.copy(0.5f)
                )
                RegularSettingComponent(
                    SettingsUIElement(
                        isIconNeeded = rememberSaveable { mutableStateOf(true) },
                        title = deleteEntireDataPermanently.value,
                        doesDescriptionExists = true,
                        description = deleteEntireDataPermanentlyDesc.value,
                        isSwitchNeeded = false,
                        isSwitchEnabled = SettingsPreference.shouldFollowDynamicTheming,
                        onSwitchStateChange = {
                            shouldDeleteEntireDialogBoxAppear.value = true
                        },
                        icon = Icons.Default.DeleteForever,
                        shouldFilledIconBeUsed = rememberSaveable { mutableStateOf(true) }
                    )
                )
            }
            item {
                HorizontalDivider(
                    Modifier.padding(
                        start = 15.dp,
                        end = 15.dp,
                        bottom = 30.dp,
                    ),
                    color = DividerDefaults.color.copy(0.5f)
                )
                RegularSettingComponent(
                    settingsUIElement = SettingsUIElement(
                        title = LocalizedStrings.clearImageCache.value,
                        doesDescriptionExists = true,
                        description = LocalizedStrings.clearImageCacheDesc.value,
                        isSwitchNeeded = false,
                        isIconNeeded = rememberSaveable {
                            mutableStateOf(true)
                        },
                        icon = Icons.Default.BrokenImage,
                        isSwitchEnabled = rememberSaveable {
                            mutableStateOf(false)
                        },
                        onSwitchStateChange = {
                            context.imageLoader.memoryCache?.clear()
                            context.imageLoader.diskCache?.clear()
                        }, shouldFilledIconBeUsed = rememberSaveable {
                            mutableStateOf(true)
                        }
                    )
                )
            }
            item {
                HorizontalDivider(
                    Modifier.padding(
                        start = 15.dp,
                        end = 15.dp,
                        bottom = if (SettingsScreenVM.isAnyRefreshingTaskGoingOn.value) 0.dp else 30.dp
                    ),
                    color = DividerDefaults.color.copy(0.5f)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .animateContentSize()
                ) {
                    if (!SettingsScreenVM.isAnyRefreshingTaskGoingOn.value) {
                        RegularSettingComponent(
                            settingsUIElement = SettingsUIElement(
                                title = LocalizedStrings.refreshAllLinksTitlesAndImages.value,
                                doesDescriptionExists = true,
                                description = LocalizedStrings.refreshAllLinksTitlesAndImagesDesc.value,
                                isSwitchNeeded = false,
                                isIconNeeded = rememberSaveable {
                                    mutableStateOf(true)
                                },
                                icon = Icons.Default.Refresh,
                                isSwitchEnabled = rememberSaveable {
                                    mutableStateOf(false)
                                },
                                onSwitchStateChange = {
                                    settingsScreenVM.refreshAllLinksImagesAndTitles()
                                }, shouldFilledIconBeUsed = rememberSaveable {
                                    mutableStateOf(true)
                                }
                            )
                        )
                    }
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .animateContentSize()
                ) {
                    if (SettingsScreenVM.isAnyRefreshingTaskGoingOn.value) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            Text(
                                text = LocalizedStrings.refreshingLinks.value,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(
                                    start = 15.dp,
                                    end = 15.dp
                                )
                            )
                            if (RefreshLinksWorker.totalLinksCount.intValue != 0) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 15.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    LinearProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth(0.85f),
                                        progress = {
                                            if (!(successfulRefreshLinkCount.value.toFloat() / RefreshLinksWorker.totalLinksCount.intValue.toFloat()).isNaN() && successfulRefreshLinkCount.value.toFloat() < RefreshLinksWorker.totalLinksCount.intValue.toFloat()) {
                                                successfulRefreshLinkCount.value.toFloat() / RefreshLinksWorker.totalLinksCount.intValue.toFloat()
                                            } else {
                                                0f
                                            }
                                        }
                                    )
                                    IconButton(onClick = {
                                        settingsScreenVM.cancelRefreshAllLinksImagesAndTitlesWork()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = ""
                                        )
                                    }
                                }
                            }
                            if (successfulRefreshLinkCount.collectAsStateWithLifecycle().value == 0 && RefreshLinksWorker.totalLinksCount.intValue == 0) {
                                Spacer(modifier = Modifier.height(15.dp))
                            }
                            Text(
                                text = if (successfulRefreshLinkCount.collectAsStateWithLifecycle().value == 0 && RefreshLinksWorker.totalLinksCount.intValue == 0) LocalizedStrings.workManagerDesc.value else "${successfulRefreshLinkCount.collectAsStateWithLifecycle().value} " + LocalizedStrings.of.value + " ${RefreshLinksWorker.totalLinksCount.intValue} " + LocalizedStrings.linksRefreshed.value,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(
                                    start = 15.dp,
                                    end = 15.dp
                                ),
                                lineHeight = 18.sp
                            )
                            Card(
                                border = BorderStroke(
                                    1.dp,
                                    contentColorFor(MaterialTheme.colorScheme.surface)
                                ),
                                colors = CardDefaults.cardColors(containerColor = AlertDialogDefaults.containerColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 15.dp,
                                        end = 15.dp,
                                        top = 20.dp
                                    )
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

                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(
                                                start = 10.dp, end = 10.dp
                                            )
                                    )
                                    Text(
                                        text = LocalizedStrings.refreshingLinksInfo.value,
                                        style = MaterialTheme.typography.titleSmall,
                                        lineHeight = 18.sp,
                                        modifier = Modifier.padding(end = 15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
        PermissionDialog(isVisible = isPermissionDialogBoxVisible,
            permissionDenied = when (ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )) {
                PackageManager.PERMISSION_GRANTED -> false
                else -> true
            },
            onClick = {
                context as Activity
                context.openApplicationSettings()
            })

        // TODO
        ImportExceptionDialogBox(
            isVisible = isImportExceptionBoxVisible,
            onClick = { activityResultLauncher.launch("text/html") },
            exceptionType = settingsScreenVM.exceptionType
        )

        DeleteDialogBox(
            DeleteDialogBoxParam(
                shouldDialogBoxAppear = shouldDeleteEntireDialogBoxAppear,
                deleteDialogBoxType = DataDialogBoxType.REMOVE_ENTIRE_DATA,
                onDeleteClick = {
                    settingsScreenVM.deleteEntireLinksAndFoldersData(context = context)
                    Toast.makeText(
                        context,
                        deletedEntireDataFromTheLocalDatabase.value,
                        Toast.LENGTH_SHORT
                    ).show()
                })
        )
        DataImportDialogBox()
        DataExportDialogBox()
    }
}