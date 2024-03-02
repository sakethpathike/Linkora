package com.sakethh.linkora.ui.screens.settings

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.SettingsInputSvideo
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sakethh.linkora.ui.commonComposables.DataDialogBoxType
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBox
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBoxParam
import com.sakethh.linkora.ui.navigation.NavigationRoutes
import com.sakethh.linkora.ui.screens.settings.composables.ImportConflictBtmSheet
import com.sakethh.linkora.ui.screens.settings.composables.ImportExceptionDialogBox
import com.sakethh.linkora.ui.screens.settings.composables.PermissionDialog
import com.sakethh.linkora.ui.screens.settings.composables.SettingsNewVersionCheckerDialogBox
import com.sakethh.linkora.ui.screens.settings.composables.SettingsNewVersionUpdateBtmContent
import com.sakethh.linkora.ui.screens.settings.composables.SettingsSectionComposable
import com.sakethh.linkora.ui.theme.LinkoraTheme
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM
import com.sakethh.linkora.ui.viewmodels.SettingsSections
import com.sakethh.linkora.ui.viewmodels.localDB.DeleteVM
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)
@PreviewLightDark
@Composable
fun SettingsScreen(navController: NavController = rememberNavController()) {
    val importModalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val settingsScreenVM: SettingsScreenVM = viewModel()
    val generalSectionData = settingsScreenVM.generalSection(context)
    val isPermissionDialogBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }
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
                context,
                isImportExceptionBoxVisible
            )
            file.delete()
        }
    val runtimePermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            isPermissionDialogBoxVisible.value = !it
        })
    val dataSectionData = settingsScreenVM.dataSection(
        runtimePermission,
        context,
        isDialogBoxVisible = isPermissionDialogBoxVisible,
        activityResultLauncher = activityResultLauncher,
        importModalBtmSheetState = isImportConflictBoxVisible
    )
    val coroutineScope = rememberCoroutineScope()
    val shouldVersionCheckerDialogAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldBtmModalSheetBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val btmModalSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val privacySectionData = settingsScreenVM.privacySection(context)
    val topAppBarScrollState = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val isCurrentSelectedSettingBtmSheetVisible = rememberSaveable {
        mutableStateOf(false)
    }
    LinkoraTheme {
        Scaffold(topBar = {
            Column {
                LargeTopAppBar(scrollBehavior = topAppBarScrollState, title = {
                    Text(
                        text = "Settings",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = if (topAppBarScrollState.state.collapsedFraction > 0.6f) 24.sp else 32.sp
                    )
                })
            }
        }) { it ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .nestedScroll(topAppBarScrollState.nestedScrollConnection)
            ) {
                item(key = "settingsCard") {
                    Card(
                        border = BorderStroke(
                            1.dp, contentColorFor(MaterialTheme.colorScheme.surface)
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = contentColorFor(MaterialTheme.colorScheme.surface)
                        ),
                        modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .animateContentSize()
                    ) {
                        Row {
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null
                                )
                            }
                            Text(
                                text = "Linkora",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .padding(top = 15.dp, bottom = 15.dp)
                                    .alignByBaseline()
                            )
                            Text(
                                text = SettingsScreenVM.APP_VERSION_NAME,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 12.sp,
                                modifier = Modifier.alignByBaseline()
                            )
                        }
                    }
                }
                item(key = "themeRow") {
                    SettingsSectionComposable(
                        onClick = {
                            isCurrentSelectedSettingBtmSheetVisible.value = true
                            settingsScreenVM.currentSelectedSettingSection.value =
                                SettingsSections.THEME
                        },
                        sectionTitle = "Theme",
                        sectionIcon = Icons.Default.ColorLens
                    )
                }
                item(key = "generalRow") {
                    SettingsSectionComposable(
                        onClick = {
                            isCurrentSelectedSettingBtmSheetVisible.value = true
                            settingsScreenVM.currentSelectedSettingSection.value =
                                SettingsSections.GENERAL
                        },
                        sectionTitle = "General",
                        sectionIcon = Icons.Default.SettingsInputSvideo
                    )
                }
                item(key = "dataRow") {
                    SettingsSectionComposable(
                        onClick = {
                            isCurrentSelectedSettingBtmSheetVisible.value = true
                            settingsScreenVM.currentSelectedSettingSection.value =
                                SettingsSections.DATA
                        },
                        sectionTitle = "Data",
                        sectionIcon = Icons.Default.Storage
                    )
                }
                item(key = "privacyRow") {
                    SettingsSectionComposable(
                        onClick = {
                            isCurrentSelectedSettingBtmSheetVisible.value = true
                            settingsScreenVM.currentSelectedSettingSection.value =
                                SettingsSections.PRIVACY
                        },
                        sectionTitle = "Privacy",
                        sectionIcon = Icons.Default.PrivacyTip
                    )
                }
                item(key = "aboutRow") {
                    SettingsSectionComposable(
                        onClick = {
                            isCurrentSelectedSettingBtmSheetVisible.value = true
                            settingsScreenVM.currentSelectedSettingSection.value =
                                SettingsSections.ABOUT
                        },
                        sectionTitle = "About",
                        sectionIcon = Icons.Default.Info
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
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
        SettingsNewVersionCheckerDialogBox(shouldDialogBoxAppear = shouldVersionCheckerDialogAppear)
        if (shouldBtmModalSheetBeVisible.value) {
            ModalBottomSheet(sheetState = btmModalSheetState, onDismissRequest = {
                coroutineScope.launch {
                    if (btmModalSheetState.isVisible) {
                        btmModalSheetState.hide()
                    }
                }.invokeOnCompletion {
                    shouldBtmModalSheetBeVisible.value = false
                }
            }) {
                SettingsNewVersionUpdateBtmContent(
                    shouldBtmModalSheetBeVisible = shouldBtmModalSheetBeVisible,
                    modalBtmSheetState = btmModalSheetState
                )
            }
        }
        ImportExceptionDialogBox(
            isVisible = isImportExceptionBoxVisible,
            onClick = { activityResultLauncher.launch("text/*") },
            exceptionType = settingsScreenVM.exceptionType
        )
        val deleteVM: DeleteVM = viewModel()
        ImportConflictBtmSheet(isUIVisible = isImportConflictBoxVisible,
            modalBottomSheetState = importModalBottomSheetState,
            onMergeClick = {
                activityResultLauncher.launch("text/*")
            },
            onDeleteExistingDataClick = {
                deleteVM.deleteEntireLinksAndFoldersData(onTaskCompleted = {
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
                                context, "Successfully Exported", Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            runtimePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            Toast.makeText(
                                context, "Permission required to write the data", Toast.LENGTH_SHORT
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
                        context, "Successfully Exported", Toast.LENGTH_SHORT
                    ).show()
                    deleteVM.deleteEntireLinksAndFoldersData(onTaskCompleted = {
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
                                context, "Permission required to write the data", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            })
        DeleteDialogBox(
            DeleteDialogBoxParam(shouldDialogBoxAppear = settingsScreenVM.shouldDeleteDialogBoxAppear,
                deleteDialogBoxType = DataDialogBoxType.REMOVE_ENTIRE_DATA,
                onDeleteClick = {
                    deleteVM.deleteEntireLinksAndFoldersData()
                    Toast.makeText(
                        context, "Deleted entire data from the local database", Toast.LENGTH_SHORT
                    ).show()
                })
        )
    }
    BackHandler {
        if (btmModalSheetState.isVisible) {
            coroutineScope.launch {
                btmModalSheetState.hide()
            }
        } else if (SettingsScreenVM.Settings.isHomeScreenEnabled.value) {
            navController.navigate(NavigationRoutes.HOME_SCREEN.name) {
                popUpTo(0)
            }
        } else {
            navController.navigate(NavigationRoutes.COLLECTIONS_SCREEN.name) {
                popUpTo(0)
            }
        }
    }
}

fun Activity.openApplicationSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null)
    ).also {
        startActivity(it)
    }
}