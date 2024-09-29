package com.sakethh.linkora.ui.screens.settings.specific

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Cancel
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
import com.sakethh.linkora.LocalizedStrings.deletedEntireDataFromTheLocalDatabase
import com.sakethh.linkora.LocalizedStrings.importFeatureIsPolishedNotPerfectDesc
import com.sakethh.linkora.LocalizedStrings.permissionRequiredToWriteTheData
import com.sakethh.linkora.LocalizedStrings.successfullyExported
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.commonComposables.DataDialogBoxType
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBox
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBoxParam
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.screens.settings.SettingsUIElement
import com.sakethh.linkora.ui.screens.settings.composables.ImportConflictBtmSheet
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
    val isImportConflictBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            val file = createTempFile()
            uri?.let { context.contentResolver.openInputStream(it) }.use { input ->
                file.outputStream().use { output ->
                    input?.copyTo(output)
                }
            }
            settingsScreenVM.importData(
                settingsScreenVM.exceptionType,
                file.readText(),
                isImportExceptionBoxVisible
            )
            file.delete()
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
                    text = importFeatureIsPolishedNotPerfectDesc.value,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(start = 15.dp, end = 15.dp)
                )
            }

            items(
                settingsScreenVM.dataSection(
                    runtimePermission,
                    context,
                    isDialogBoxVisible = isPermissionDialogBoxVisible,
                    activityResultLauncher = activityResultLauncher,
                    importModalBtmSheetState = isImportConflictBoxVisible
                )
            ) {
                RegularSettingComponent(
                    settingsUIElement = it
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
        ImportExceptionDialogBox(
            isVisible = isImportExceptionBoxVisible,
            onClick = { activityResultLauncher.launch("text/*") },
            exceptionType = settingsScreenVM.exceptionType
        )

        ImportConflictBtmSheet(isUIVisible = isImportConflictBoxVisible,
            modalBottomSheetState = importModalBottomSheetState,
            onMergeClick = {
                activityResultLauncher.launch("text/*")
            },
            onDeleteExistingDataClick = {
                settingsScreenVM.deleteEntireLinksAndFoldersData(onTaskCompleted = {
                    activityResultLauncher.launch("text/*")
                }, context)
            },
            onDataExportClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    settingsScreenVM.exportDataToAFile(
                        context = context,
                        isDialogBoxVisible = isPermissionDialogBoxVisible,
                        runtimePermission = runtimePermission
                    )
                } else {
                    when (ContextCompat.checkSelfPermission(
                        context, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )) {
                        PackageManager.PERMISSION_GRANTED -> {
                            settingsScreenVM.exportDataToAFile(
                                context = context,
                                isDialogBoxVisible = isPermissionDialogBoxVisible,
                                runtimePermission = runtimePermission
                            )
                            Toast.makeText(
                                context,
                                successfullyExported.value,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            runtimePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            Toast.makeText(
                                context,
                                successfullyExported.value,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            },
            onExportAndThenImportClick = {
                fun exportDataToAFile() {
                    settingsScreenVM.exportDataToAFile(
                        context = context,
                        isDialogBoxVisible = isPermissionDialogBoxVisible,
                        runtimePermission = runtimePermission
                    )
                    Toast.makeText(
                        context,
                        successfullyExported.value,
                        Toast.LENGTH_SHORT
                    ).show()
                    settingsScreenVM.deleteEntireLinksAndFoldersData(onTaskCompleted = {
                        activityResultLauncher.launch("text/*")
                    }, context)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    exportDataToAFile()
                } else {
                    when (ContextCompat.checkSelfPermission(
                        context, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )) {
                        PackageManager.PERMISSION_GRANTED -> {
                            exportDataToAFile()
                        }

                        else -> {
                            runtimePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            Toast.makeText(
                                context,
                                permissionRequiredToWriteTheData.value,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            })
        DeleteDialogBox(
            DeleteDialogBoxParam(shouldDialogBoxAppear = settingsScreenVM.shouldDeleteDialogBoxAppear,
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
    }
}