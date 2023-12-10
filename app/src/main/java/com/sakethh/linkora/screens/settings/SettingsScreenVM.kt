package com.sakethh.linkora.screens.settings

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.localDB.LocalDataBase
import com.sakethh.linkora.localDB._import.ImportImpl
import com.sakethh.linkora.localDB.export.ExportImpl
import com.sakethh.linkora.screens.settings.SettingsScreenVM.Settings.dataStore
import com.sakethh.linkora.screens.settings.SettingsScreenVM.Settings.isSendCrashReportsEnabled
import com.sakethh.linkora.screens.settings.appInfo.dto.AppInfoDTO
import com.sakethh.linkora.screens.settings.appInfo.dto.MutableStateAppInfoDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup

data class SettingsUIElement(
    val title: String,
    val doesDescriptionExists: Boolean,
    val description: String?,
    val isSwitchNeeded: Boolean,
    val isSwitchEnabled: MutableState<Boolean>,
    val onSwitchStateChange: () -> Unit,
    val icon: ImageVector? = null,
)

class SettingsScreenVM(
    private val exportImpl: ExportImpl = ExportImpl()
) : ViewModel() {

    val shouldDeleteDialogBoxAppear = mutableStateOf(false)
    val exceptionType: MutableState<String?> = mutableStateOf(null)

    companion object {
        const val currentAppVersion = "0.4.0"
        val latestAppInfoFromServer = MutableStateAppInfoDTO(
            mutableStateOf(""),
            mutableStateOf(""),
            mutableStateOf(""),
            mutableStateOf(""),
            mutableStateOf(""),
            mutableStateOf(""),
            mutableStateOf("")
        )
    }

    val privacySection: (context: Context) -> SettingsUIElement = {
        SettingsUIElement(title = "Send crash reports",
            doesDescriptionExists = false,
            description = "",
            isSwitchNeeded = true,
            isSwitchEnabled = isSendCrashReportsEnabled,
            onSwitchStateChange = {
                viewModelScope.launch {
                    Settings.changeSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(
                            SettingsPreferences.SEND_CRASH_REPORTS.name
                        ), dataStore = it.dataStore, newValue = !isSendCrashReportsEnabled.value
                    )
                    isSendCrashReportsEnabled.value = Settings.readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.SEND_CRASH_REPORTS.name),
                        dataStore = it.dataStore
                    ) == true
                }
            })
    }
    val generalSection: (context: Context) -> List<SettingsUIElement> = {
        listOf(
            SettingsUIElement(title = "Use in-app browser",
                doesDescriptionExists = false,
                description = "If this is enabled, links will be opened within the app; if this setting is not enabled, your default browser will open every time you click on a link when using this app.",
                isSwitchNeeded = true,
                isSwitchEnabled = Settings.isInAppWebTabEnabled,
                onSwitchStateChange = {
                    viewModelScope.launch {
                        Settings.changeSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(
                                SettingsPreferences.CUSTOM_TABS.name
                            ),
                            dataStore = it.dataStore,
                            newValue = !Settings.isInAppWebTabEnabled.value
                        )
                        Settings.isInAppWebTabEnabled.value = Settings.readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.CUSTOM_TABS.name),
                            dataStore = it.dataStore
                        ) == true
                    }
                }),
            SettingsUIElement(title = "Enable Home Screen",
                doesDescriptionExists = false,
                description = "If this is enabled, Home Screen option will be shown in Bottom Navigation Bar; if this setting is not enabled, Home screen option will NOT be shown.",
                isSwitchNeeded = true,
                isSwitchEnabled = Settings.isHomeScreenEnabled,
                onSwitchStateChange = {
                    viewModelScope.launch {
                        Settings.changeSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(
                                SettingsPreferences.HOME_SCREEN_VISIBILITY.name
                            ),
                            dataStore = it.dataStore,
                            newValue = !Settings.isHomeScreenEnabled.value
                        )
                        Settings.isHomeScreenEnabled.value = Settings.readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.HOME_SCREEN_VISIBILITY.name),
                            dataStore = it.dataStore
                        ) == true
                    }
                }),
            SettingsUIElement(title = "Use Bottom Sheet UI for saving links",
                doesDescriptionExists = false,
                description = "If this is enabled, Bottom sheet will pop-up while saving a link; if this setting is not enabled, a dialog box will be shown instead of bottom sheet.",
                isSwitchNeeded = true,
                isSwitchEnabled = Settings.isBtmSheetEnabledForSavingLinks,
                onSwitchStateChange = {
                    viewModelScope.launch {
                        Settings.changeSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(
                                SettingsPreferences.BTM_SHEET_FOR_SAVING_LINKS.name
                            ),
                            dataStore = it.dataStore,
                            newValue = !Settings.isBtmSheetEnabledForSavingLinks.value
                        )
                        Settings.isBtmSheetEnabledForSavingLinks.value =
                            Settings.readSettingPreferenceValue(
                                preferenceKey = booleanPreferencesKey(SettingsPreferences.BTM_SHEET_FOR_SAVING_LINKS.name),
                                dataStore = it.dataStore
                            ) == true
                    }
                }),
            SettingsUIElement(title = "Auto-Detect Title",
                doesDescriptionExists = true,
                description = "Note: This may not detect every website.",
                isSwitchNeeded = true,
                isSwitchEnabled = Settings.isAutoDetectTitleForLinksEnabled,
                onSwitchStateChange = {
                    viewModelScope.launch {
                        Settings.changeSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(
                                SettingsPreferences.AUTO_DETECT_TITLE_FOR_LINK.name
                            ),
                            dataStore = it.dataStore,
                            newValue = !Settings.isAutoDetectTitleForLinksEnabled.value
                        )
                        Settings.isAutoDetectTitleForLinksEnabled.value =
                            Settings.readSettingPreferenceValue(
                                preferenceKey = booleanPreferencesKey(SettingsPreferences.AUTO_DETECT_TITLE_FOR_LINK.name),
                                dataStore = it.dataStore
                            ) == true
                    }
                }),
        )
    }

    fun importData(
        exceptionType: MutableState<String?>,
        json: String,
        context: Context,
        shouldErrorDialogBeVisible: MutableState<Boolean>
    ) {
        viewModelScope.launch {
            ImportImpl().importToLocalDB(
                exceptionType = exceptionType,
                jsonString = json,
                context = context,
                shouldErrorDialogBeVisible = shouldErrorDialogBeVisible
            )
        }
    }

    fun exportDataToAFile(
        context: Context,
        isDialogBoxVisible: MutableState<Boolean>,
        runtimePermission: ManagedActivityResultLauncher<String, Boolean>
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            exportImpl.exportToAFile()
            viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context, "Successfully Exported", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            when (ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )) {
                PackageManager.PERMISSION_GRANTED -> {
                    exportImpl.exportToAFile()
                    viewModelScope.launch {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context, "Successfully Exported", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    isDialogBoxVisible.value = false
                }

                else -> {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        runtimePermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        viewModelScope.launch {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "PERMISSION DENIED", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }
        }
    }

    fun dataSection(
        runtimePermission: ManagedActivityResultLauncher<String, Boolean>,
        context: Context,
        isDialogBoxVisible: MutableState<Boolean>,
        activityResultLauncher: ManagedActivityResultLauncher<String, Uri?>,
        isImportConflictDialogVisible: MutableState<Boolean>
    ): List<SettingsUIElement> {
        return listOf(
            SettingsUIElement(
                title = "Import Links",
                doesDescriptionExists = true,
                description = "Import Links from external JSON file.",
                isSwitchNeeded = false,
                isSwitchEnabled = Settings.shouldFollowDynamicTheming,
                onSwitchStateChange = {
                    viewModelScope.launch {
                        if (LocalDataBase.localDB.readDao()
                                .isHistoryLinksTableEmpty() && LocalDataBase.localDB.readDao()
                                .isImpLinksTableEmpty() && LocalDataBase.localDB.readDao()
                                .isLinksTableEmpty() && LocalDataBase.localDB.readDao()
                                .isArchivedFoldersTableEmpty() && LocalDataBase.localDB.readDao()
                                .isFoldersTableEmpty() && LocalDataBase.localDB.readDao()
                                .isArchivedLinksTableEmpty()
                        ) {
                            activityResultLauncher.launch("text/*")
                        } else {
                            isImportConflictDialogVisible.value = true
                        }
                    }
                },
                icon = Icons.Default.FileDownload
            ),
            SettingsUIElement(
                title = "Export Links",
                doesDescriptionExists = true,
                description = "Export all of your links in JSON format.",
                isSwitchNeeded = false,
                isSwitchEnabled = Settings.shouldFollowDynamicTheming,
                onSwitchStateChange = {
                    exportDataToAFile(context, isDialogBoxVisible, runtimePermission)
                },
                icon = Icons.Default.FileUpload
            ),
            SettingsUIElement(
                title = "Delete entire data permanently",
                doesDescriptionExists = true,
                description = "Delete all links and folders permanently including archive(s).",
                isSwitchNeeded = false,
                isSwitchEnabled = Settings.shouldFollowDynamicTheming,
                onSwitchStateChange = {
                    shouldDeleteDialogBoxAppear.value = true
                },
                icon = Icons.Default.DeleteForever
            ),
        )
    }

    enum class SettingsPreferences {
        DYNAMIC_THEMING, DARK_THEME, FOLLOW_SYSTEM_THEME, CUSTOM_TABS, AUTO_DETECT_TITLE_FOR_LINK, BTM_SHEET_FOR_SAVING_LINKS, HOME_SCREEN_VISIBILITY, SORTING_PREFERENCE, SEND_CRASH_REPORTS, IS_DATA_MIGRATION_COMPLETED_FROM_V9
    }

    enum class SortingPreferences {
        A_TO_Z, Z_TO_A, NEW_TO_OLD, OLD_TO_NEW
    }

    object Settings {

        val Context.dataStore by preferencesDataStore("linkoraDataStore")

        val shouldFollowDynamicTheming = mutableStateOf(false)
        val shouldFollowSystemTheme = mutableStateOf(true)
        val shouldDarkThemeBeEnabled = mutableStateOf(false)
        val isInAppWebTabEnabled = mutableStateOf(true)
        val isAutoDetectTitleForLinksEnabled = mutableStateOf(false)
        val isBtmSheetEnabledForSavingLinks = mutableStateOf(false)
        val isHomeScreenEnabled = mutableStateOf(true)
        val isSendCrashReportsEnabled = mutableStateOf(true)
        val selectedSortingType = mutableStateOf("")

        suspend fun readSettingPreferenceValue(
            preferenceKey: androidx.datastore.preferences.core.Preferences.Key<Boolean>,
            dataStore: DataStore<androidx.datastore.preferences.core.Preferences>,
        ): Boolean? {
            return dataStore.data.first()[preferenceKey]
        }

        private suspend fun readSortingPreferenceValue(
            preferenceKey: androidx.datastore.preferences.core.Preferences.Key<String>,
            dataStore: DataStore<androidx.datastore.preferences.core.Preferences>,
        ): String? {
            return dataStore.data.first()[preferenceKey]
        }

        suspend fun changeSettingPreferenceValue(
            preferenceKey: androidx.datastore.preferences.core.Preferences.Key<Boolean>,
            dataStore: DataStore<androidx.datastore.preferences.core.Preferences>,
            newValue: Boolean,
        ) {
            dataStore.edit {
                it[preferenceKey] = newValue
            }
        }

        suspend fun changeSortingPreferenceValue(
            preferenceKey: androidx.datastore.preferences.core.Preferences.Key<String>,
            dataStore: DataStore<androidx.datastore.preferences.core.Preferences>,
            newValue: SortingPreferences,
        ) {
            dataStore.edit {
                it[preferenceKey] = newValue.name
            }
        }

        suspend fun readAllPreferencesValues(context: Context) {
            coroutineScope {
                kotlinx.coroutines.awaitAll(async {
                    shouldFollowSystemTheme.value = readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.FOLLOW_SYSTEM_THEME.name),
                        dataStore = context.dataStore
                    ) ?: (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                }, async {
                    shouldDarkThemeBeEnabled.value = readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.DARK_THEME.name),
                        dataStore = context.dataStore
                    ) ?: (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                }, async {
                    shouldFollowDynamicTheming.value = readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.DYNAMIC_THEMING.name),
                        dataStore = context.dataStore
                    ) ?: false
                }, async {
                    isInAppWebTabEnabled.value = readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.CUSTOM_TABS.name),
                        dataStore = context.dataStore
                    ) ?: false
                }, async {
                    isAutoDetectTitleForLinksEnabled.value = readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.AUTO_DETECT_TITLE_FOR_LINK.name),
                        dataStore = context.dataStore
                    ) ?: false
                }, async {
                    isHomeScreenEnabled.value = if (readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.HOME_SCREEN_VISIBILITY.name),
                            dataStore = context.dataStore
                        ) == null
                    ) {
                        true
                    } else {
                        readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.HOME_SCREEN_VISIBILITY.name),
                            dataStore = context.dataStore
                        ) == true
                    }
                }, async {
                    isBtmSheetEnabledForSavingLinks.value = readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.BTM_SHEET_FOR_SAVING_LINKS.name),
                        dataStore = context.dataStore
                    ) ?: false
                }, async {
                    isSendCrashReportsEnabled.value = readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.SEND_CRASH_REPORTS.name),
                        dataStore = context.dataStore
                    ) ?: true
                }, async {
                    selectedSortingType.value = readSortingPreferenceValue(
                        preferenceKey = stringPreferencesKey(SettingsPreferences.SORTING_PREFERENCE.name),
                        dataStore = context.dataStore
                    ) ?: SortingPreferences.NEW_TO_OLD.name
                })
            }
        }

        suspend fun latestAppVersionRetriever() {
            val rawData = withContext(Dispatchers.Default) {
                Jsoup.connect("appInfoURL").get().body().ownText()
            }
            val retrievedData = Json.decodeFromString<AppInfoDTO>(rawData)
            latestAppInfoFromServer.apply {
                this.latestVersion.value = retrievedData.latestVersion
                this.latestStableVersion.value = retrievedData.latestStableVersion
                this.latestStableVersionReleaseURL.value =
                    retrievedData.latestStableVersionReleaseURL
                this.latestVersionReleaseURL.value = retrievedData.latestVersionReleaseURL
                this.changeLogForLatestVersion.value = retrievedData.changeLogForLatestVersion
                this.changeLogForLatestStableVersion.value =
                    retrievedData.changeLogForLatestStableVersion
                this.httpStatusCodeFromServer.value = ""
            }
        }
    }
}