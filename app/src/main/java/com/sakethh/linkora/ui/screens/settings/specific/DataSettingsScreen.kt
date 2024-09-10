package com.sakethh.linkora.ui.screens.settings.specific

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
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
import com.sakethh.linkora.ui.screens.settings.composables.ImportConflictBtmSheet
import com.sakethh.linkora.ui.screens.settings.composables.ImportExceptionDialogBox
import com.sakethh.linkora.ui.screens.settings.composables.PermissionDialog
import com.sakethh.linkora.ui.screens.settings.composables.RegularSettingComponent
import com.sakethh.linkora.ui.screens.settings.composables.SpecificScreenScaffold
import com.sakethh.linkora.utils.openApplicationSettings
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
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
                })
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
                    })
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
                    settingsScreenVM.deleteEntireLinksAndFoldersData()
                    Toast.makeText(
                        context,
                        deletedEntireDataFromTheLocalDatabase.value,
                        Toast.LENGTH_SHORT
                    ).show()
                })
        )
    }
}