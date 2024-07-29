package com.sakethh.linkora.ui.screens.settings

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShortText
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SystemUpdateAlt
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.UriHandler
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.data.local.backup.ExportRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.data.local.restore.ImportRepo
import com.sakethh.linkora.data.remote.releases.GitHubReleasesRepo
import com.sakethh.linkora.data.remote.releases.GitHubReleasesResult
import com.sakethh.linkora.data.remote.releases.model.GitHubReleaseDTOItem
import com.sakethh.linkora.ui.screens.CustomWebTab
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM.Settings.dataStore
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM.Settings.isSendCrashReportsEnabled
import com.sakethh.linkora.worker.RefreshLinksWorker
import com.sakethh.linkora.worker.RefreshLinksWorkerRequestBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

data class SettingsUIElement(
    val title: String,
    val doesDescriptionExists: Boolean,
    val description: String?,
    val isSwitchNeeded: Boolean,
    val isSwitchEnabled: MutableState<Boolean>,
    val onSwitchStateChange: (newValue: Boolean) -> Unit,
    val onAcknowledgmentClick: (uriHandler: UriHandler, context: Context) -> Unit = { _, _ -> },
    val icon: ImageVector? = null,
    val isIconNeeded: MutableState<Boolean>,
    val shouldFilledIconBeUsed: MutableState<Boolean> = mutableStateOf(false),
    val shouldArrowIconBeAppear: MutableState<Boolean> = mutableStateOf(false)
)

enum class SettingsSections {
    THEME, GENERAL, DATA, PRIVACY, ABOUT, ACKNOWLEDGMENT
}

@HiltViewModel
class SettingsScreenVM @Inject constructor(
    private val linksRepo: LinksRepo,
    private val importRepo: ImportRepo,
    private val localDatabase: LocalDatabase,
    private val exportRepo: ExportRepo,
    private val gitHubReleasesRepo: GitHubReleasesRepo,
    private val refreshLinksWorkerRequestBuilder: RefreshLinksWorkerRequestBuilder,
    private val workManager: WorkManager
) : CustomWebTab(linksRepo) {

    val shouldDeleteDialogBoxAppear = mutableStateOf(false)
    val exceptionType: MutableState<String?> = mutableStateOf(null)

    init {
        viewModelScope.launch {
            RefreshLinksWorkerRequestBuilder.REFRESH_LINKS_WORKER_TAG.collectLatest { uuid ->
                workManager.getWorkInfoByIdFlow(uuid).collectLatest {
                    if (it != null) {
                        isAnyRefreshingTaskGoingOn.value =
                            it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED
                    }
                    }
            }
        }
    }

    fun refreshAllLinksImagesAndTitles() {
        viewModelScope.launch {
            refreshLinksWorkerRequestBuilder.request()
        }
    }

    fun cancelRefreshAllLinksImagesAndTitlesWork(context: Context) {
        RefreshLinksWorker.superVisorJob?.cancel()
        workManager.cancelAllWork()
    }
    companion object {
        val isAnyRefreshingTaskGoingOn = mutableStateOf(false)
        val currentSelectedSettingSection = mutableStateOf(SettingsSections.THEME)
        const val APP_VERSION_NAME = "v0.5.0"
        const val APP_VERSION_CODE = 23
        private val _latestReleaseInfoFromGitHubReleases = MutableStateFlow(
            GitHubReleaseDTOItem(
                assets = listOf(),
                body = "",
                createdAt = "",
                releasePageURL = "",
                releaseName = ""
            )
        )
        val latestReleaseInfoFromGitHubReleases = _latestReleaseInfoFromGitHubReleases.asStateFlow()
    }

    val acknowledgmentsSection = listOf(
        SettingsUIElement(
            title = "Kotlin",
            doesDescriptionExists = true,
            description = "Apache License (Version 2.0)",
            isSwitchNeeded = false,
            isSwitchEnabled = mutableStateOf(false),
            onSwitchStateChange = {},
            onAcknowledgmentClick = { uriHandler, context ->
                viewModelScope.launch {
                    openInWeb(
                        recentlyVisitedData = RecentlyVisited(
                            title = "Kotlin - GitHub",
                            webURL = "https://github.com/JetBrains/kotlin",
                            baseURL = "github.com",
                            imgURL = "https://avatars.githubusercontent.com/u/1446536",
                            infoForSaving = "Kotlin on GitHub",
                        ),
                        uriHandler = uriHandler,
                        context = context,
                        forceOpenInExternalBrowser = false
                    )
                }
            },
            isIconNeeded = mutableStateOf(false),
            shouldArrowIconBeAppear = mutableStateOf(true)
        ),
        SettingsUIElement(
            title = "Android Jetpack",
            doesDescriptionExists = true,
            description = "Apache License (Version 2.0)",
            isSwitchNeeded = false,
            isSwitchEnabled = mutableStateOf(false),
            onSwitchStateChange = {},
            onAcknowledgmentClick = { uriHandler, context ->
                viewModelScope.launch {
                    openInWeb(
                        recentlyVisitedData = RecentlyVisited(
                            title = "androidx - GitHub",
                            webURL = "https://github.com/androidx/androidx",
                            baseURL = "github.com",
                            imgURL = "https://play-lh.googleusercontent.com/PCpXdqvUWfCW1mXhH1Y_98yBpgsWxuTSTofy3NGMo9yBTATDyzVkqU580bfSln50bFU",
                            infoForSaving = "androidx on GitHub",
                        ),
                        uriHandler = uriHandler,
                        context = context,
                        forceOpenInExternalBrowser = false
                    )
                }
            },
            shouldArrowIconBeAppear = mutableStateOf(true),
            isIconNeeded = mutableStateOf(false)
        ),
        SettingsUIElement(
            title = "Coil",
            doesDescriptionExists = true,
            description = "Apache License (Version 2.0)",
            isSwitchNeeded = false,
            isSwitchEnabled = mutableStateOf(false),
            onSwitchStateChange = {

            },
            onAcknowledgmentClick = { uriHandler, context ->
                viewModelScope.launch {
                    openInWeb(
                        recentlyVisitedData = RecentlyVisited(
                            title = "Coil - GitHub",
                            webURL = "https://github.com/coil-kt/coil",
                            baseURL = "github.com",
                            imgURL = "https://avatars.githubusercontent.com/u/52722434",
                            infoForSaving = "Coil on GitHub",
                        ),
                        uriHandler = uriHandler,
                        context = context,
                        forceOpenInExternalBrowser = false
                    )
                }
            },
            shouldArrowIconBeAppear = mutableStateOf(true),
            isIconNeeded = mutableStateOf(false)
        ),
        SettingsUIElement(
            title = "jsoup",
            doesDescriptionExists = true,
            description = "MIT License",
            isSwitchNeeded = false,
            isSwitchEnabled = mutableStateOf(false),
            onSwitchStateChange = {

            },
            onAcknowledgmentClick = { uriHandler, context ->
                viewModelScope.launch {
                    openInWeb(
                        recentlyVisitedData = RecentlyVisited(
                            title = "jsoup - GitHub",
                            webURL = "https://github.com/jhy/jsoup",
                            baseURL = "github.com",
                            imgURL = "https://jsoup.org/rez/jsoup%20logo%20twitter.png",
                            infoForSaving = "jsoup on GitHub",
                        ),
                        uriHandler = uriHandler,
                        context = context,
                        forceOpenInExternalBrowser = false
                    )
                }
            },
            shouldArrowIconBeAppear = mutableStateOf(true),
            isIconNeeded = mutableStateOf(false)
        ),
        SettingsUIElement(
            title = "Material Design 3",
            doesDescriptionExists = true,
            description = "Apache License (Version 2.0)",
            isSwitchNeeded = false,
            isSwitchEnabled = mutableStateOf(false),
            onSwitchStateChange = {

            },
            onAcknowledgmentClick = { uriHandler, context ->
                viewModelScope.launch {
                    openInWeb(
                        recentlyVisitedData = RecentlyVisited(
                            title = "Material 3",
                            webURL = "https://m3.material.io/",
                            baseURL = "material.io",
                            imgURL = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/Google_Material_Design_Logo.svg/512px-Google_Material_Design_Logo.svg.png",
                            infoForSaving = "",
                        ),
                        uriHandler = uriHandler,
                        context = context,
                        forceOpenInExternalBrowser = false
                    )
                }
            },
            shouldArrowIconBeAppear = mutableStateOf(true),
            isIconNeeded = mutableStateOf(false)
        ),
        SettingsUIElement(
            title = "Accompanist",
            doesDescriptionExists = true,
            description = "Apache License (Version 2.0)",
            isSwitchNeeded = false,
            isSwitchEnabled = mutableStateOf(false),
            onSwitchStateChange = {

            },
            onAcknowledgmentClick = { uriHandler, context ->
                viewModelScope.launch {
                    openInWeb(
                        recentlyVisitedData = RecentlyVisited(
                            title = "Accompanist",
                            webURL = "https://google.github.io/accompanist/",
                            baseURL = "github.io",
                            imgURL = "https://google.github.io/accompanist/header.png",
                            infoForSaving = "",
                        ),
                        uriHandler = uriHandler,
                        context = context,
                        forceOpenInExternalBrowser = false
                    )
                }
            },
            shouldArrowIconBeAppear = mutableStateOf(true),
            isIconNeeded = mutableStateOf(false)
        ),
        SettingsUIElement(
            title = "kotlinx.serialization",
            doesDescriptionExists = true,
            description = "Apache License (Version 2.0)",
            isSwitchNeeded = false,
            isSwitchEnabled = mutableStateOf(false),
            onSwitchStateChange = {

            },
            onAcknowledgmentClick = { uriHandler, context ->
                viewModelScope.launch {
                    openInWeb(
                        recentlyVisitedData = RecentlyVisited(
                            title = "kotlinx.serialization - GitHub",
                            webURL = "https://github.com/Kotlin/kotlinx.serialization",
                            baseURL = "github.com",
                            imgURL = "https://avatars.githubusercontent.com/u/1446536?v=4",
                            infoForSaving = "kotlinx.serialization on GitHub",
                        ),
                        uriHandler = uriHandler,
                        context = context,
                        forceOpenInExternalBrowser = false
                    )
                }
            },
            shouldArrowIconBeAppear = mutableStateOf(true),
            isIconNeeded = mutableStateOf(false)
        ),
        SettingsUIElement(
            title = "Material Icons",
            doesDescriptionExists = true,
            description = "Apache License (Version 2.0)",
            isSwitchNeeded = false,
            isSwitchEnabled = mutableStateOf(false),
            onSwitchStateChange = {

            },
            onAcknowledgmentClick = { uriHandler, context ->
                viewModelScope.launch {
                    openInWeb(
                        recentlyVisitedData = RecentlyVisited(
                            title = "Material Icons - GitHub",
                            webURL = "https://github.com/google/material-design-icons",
                            baseURL = "google.com",
                            imgURL = "",
                            infoForSaving = "Material Icons on GitHub",
                        ),
                        uriHandler = uriHandler,
                        context = context,
                        forceOpenInExternalBrowser = false
                    )
                }
            },
            isIconNeeded = mutableStateOf(false),
            shouldArrowIconBeAppear = mutableStateOf(true)
        ),
    )

    val privacySection: (context: Context) -> SettingsUIElement = { context ->
        SettingsUIElement(title = "Send crash reports",
            doesDescriptionExists = true,
            description = if (!isSendCrashReportsEnabled.value) mutableStateOf("Every single bit of data is stored locally on your device.").value else mutableStateOf(
                "Linkora collects data related to app crashes and errors, device information, and app version."
            ).value,
            isSwitchNeeded = true,
            isSwitchEnabled = isSendCrashReportsEnabled,
            isIconNeeded = mutableStateOf(true),
            icon = Icons.Default.BugReport,
            onSwitchStateChange = {
                viewModelScope.launch {
                    Settings.changeSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(
                            SettingsPreferences.SEND_CRASH_REPORTS.name
                        ),
                        dataStore = context.dataStore,
                        newValue = !isSendCrashReportsEnabled.value
                    )
                    isSendCrashReportsEnabled.value = !isSendCrashReportsEnabled.value
                }.invokeOnCompletion {
                    val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
                    firebaseCrashlytics.setCrashlyticsCollectionEnabled(isSendCrashReportsEnabled.value)
                }
            })
    }
    val dataRefreshState = mutableIntStateOf(1)
    val generalSection: (context: Context) -> List<SettingsUIElement> = { context ->
        listOf(
            SettingsUIElement(title = "Use in-app browser",
                doesDescriptionExists = Settings.showDescriptionForSettingsState.value,
                description = "If this is enabled, links will be opened within the app; if this setting is not enabled, your default browser will open every time you click on a link when using this app.",
                isSwitchNeeded = true,
                isSwitchEnabled = Settings.isInAppWebTabEnabled,
                isIconNeeded = mutableStateOf(true),
                icon = Icons.Default.OpenInBrowser,
                onSwitchStateChange = {
                    viewModelScope.launch {
                        Settings.changeSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(
                                SettingsPreferences.CUSTOM_TABS.name
                            ), dataStore = context.dataStore, newValue = it
                        )
                        Settings.isInAppWebTabEnabled.value = it
                    }
                }), SettingsUIElement(title = "Enable Home Screen",
                doesDescriptionExists = Settings.showDescriptionForSettingsState.value,
                description = "If this is enabled, Home Screen option will be shown in Bottom Navigation Bar; if this setting is not enabled, Home screen option will NOT be shown.",
                isSwitchNeeded = true,
                isIconNeeded = mutableStateOf(true),
                icon = Icons.Default.Home,
                isSwitchEnabled = Settings.isHomeScreenEnabled,
                onSwitchStateChange = {
                    viewModelScope.launch {
                        Settings.changeSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(
                                SettingsPreferences.HOME_SCREEN_VISIBILITY.name
                            ), dataStore = context.dataStore, newValue = it
                        )
                        Settings.isHomeScreenEnabled.value = it
                    }
                }), SettingsUIElement(
                title = "Auto-Detect Title",
                doesDescriptionExists = true,
                description = "Note: This may not detect every website.",
                isSwitchNeeded = true,
                isSwitchEnabled = Settings.isAutoDetectTitleForLinksEnabled,
                isIconNeeded = mutableStateOf(true),
                icon = Icons.Default.Search,
                onSwitchStateChange = {
                    viewModelScope.launch {
                        Settings.changeSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(
                                SettingsPreferences.AUTO_DETECT_TITLE_FOR_LINK.name
                            ), dataStore = context.dataStore, newValue = it
                        )
                        Settings.isAutoDetectTitleForLinksEnabled.value = it
                    }
                }), SettingsUIElement(title = "Auto-Check for Updates",
                doesDescriptionExists = Settings.showDescriptionForSettingsState.value,
                description = "If this is enabled, Linkora automatically checks for updates when you open the app. If a new update is available, it notifies you with a toast message. If this setting is disabled, manual checks for the latest version can be done from the top of this screen.",
                isIconNeeded = mutableStateOf(true),
                icon = Icons.Default.SystemUpdateAlt,
                isSwitchNeeded = true,
                isSwitchEnabled = Settings.isAutoCheckUpdatesEnabled,
                onSwitchStateChange = {
                    viewModelScope.launch {
                        Settings.changeSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(
                                SettingsPreferences.AUTO_CHECK_UPDATES.name
                            ), dataStore = context.dataStore, newValue = it
                        )
                        Settings.isAutoCheckUpdatesEnabled.value = it
                    }
                }), SettingsUIElement(title = "Show description for Settings",
                doesDescriptionExists = true,
                description = "If this setting is enabled, detailed descriptions will be visible for certain settings, like the one you're reading now. If it is disabled, only the titles will be shown.",
                isSwitchNeeded = true,
                isIconNeeded = mutableStateOf(true),
                icon = Icons.AutoMirrored.Default.ShortText,
                isSwitchEnabled = Settings.showDescriptionForSettingsState,
                onSwitchStateChange = {
                    viewModelScope.launch {
                        Settings.changeSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(
                                SettingsPreferences.SETTING_COMPONENT_DESCRIPTION_STATE.name
                            ), dataStore = context.dataStore, newValue = it
                        )
                        Settings.showDescriptionForSettingsState.value = it
                    }
                })
        )
    }

    fun importData(
        exceptionType: MutableState<String?>,
        json: String,
        shouldErrorDialogBeVisible: MutableState<Boolean>
    ) {
        viewModelScope.launch {
            importRepo.importToLocalDB(
                exceptionType = exceptionType,
                jsonString = json,
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
            viewModelScope.launch {
                exportRepo.exportToAFile()
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
                    viewModelScope.launch {
                        exportRepo.exportToAFile()
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
        importModalBtmSheetState: MutableState<Boolean>
    ): List<SettingsUIElement> {
        return listOf(
            SettingsUIElement(
                isIconNeeded = mutableStateOf(true),
                title = "Import Data",
                doesDescriptionExists = true,
                description = "Import Data from external JSON file.",
                isSwitchNeeded = false,
                isSwitchEnabled = Settings.shouldFollowDynamicTheming,
                onSwitchStateChange = {
                    viewModelScope.launch {
                        if (linksRepo
                                .isHistoryLinksTableEmpty() && linksRepo
                                .isImpLinksTableEmpty() && linksRepo
                                .isLinksTableEmpty() && linksRepo
                                .isArchivedFoldersTableEmpty() && linksRepo
                                .isFoldersTableEmpty() && linksRepo
                                .isArchivedLinksTableEmpty()
                        ) {
                            activityResultLauncher.launch("text/*")
                        } else {
                            importModalBtmSheetState.value = true
                        }
                    }
                },
                icon = Icons.Default.FileDownload,
                shouldFilledIconBeUsed = mutableStateOf(true)
            ),
            SettingsUIElement(
                isIconNeeded = mutableStateOf(true),
                title = "Export Data",
                doesDescriptionExists = true,
                description = "Export all of your data in JSON format.",
                isSwitchNeeded = false,
                isSwitchEnabled = Settings.shouldFollowDynamicTheming,
                onSwitchStateChange = {
                    exportDataToAFile(context, isDialogBoxVisible, runtimePermission)
                },
                icon = Icons.Default.FileUpload,
                shouldFilledIconBeUsed = mutableStateOf(true)
            ),
            SettingsUIElement(
                isIconNeeded = mutableStateOf(true),
                title = "Delete entire data permanently",
                doesDescriptionExists = true,
                description = "Delete all links and folders permanently including archives.",
                isSwitchNeeded = false,
                isSwitchEnabled = Settings.shouldFollowDynamicTheming,
                onSwitchStateChange = {
                    shouldDeleteDialogBoxAppear.value = true
                },
                icon = Icons.Default.DeleteForever,
                shouldFilledIconBeUsed = mutableStateOf(true)
            ),
        )
    }

    enum class SettingsPreferences {
        DYNAMIC_THEMING, JSOUP_USER_AGENT, DARK_THEME, FOLLOW_SYSTEM_THEME, SETTING_COMPONENT_DESCRIPTION_STATE, CUSTOM_TABS, AUTO_DETECT_TITLE_FOR_LINK, AUTO_CHECK_UPDATES, BTM_SHEET_FOR_SAVING_LINKS, HOME_SCREEN_VISIBILITY, NEW_FEATURE_DIALOG_BOX_VISIBILITY, SORTING_PREFERENCE, SEND_CRASH_REPORTS, IS_DATA_MIGRATION_COMPLETED_FROM_V9, SAVED_APP_CODE, CURRENT_WORK_MANAGER_WORK_UUID,
        REFRESH_LINKS_TABLE_INDEX, REFRESH_IMP_LINKS_TABLE_INDEX, REFRESH_ARCHIVE_LINKS_TABLE_INDEX, REFRESH_RECENTLY_VISITED_LINKS_TABLE_INDEX,
    }

    enum class SortingPreferences {
        A_TO_Z, Z_TO_A, NEW_TO_OLD, OLD_TO_NEW
    }

    object Settings : ViewModel() {

        val Context.dataStore by preferencesDataStore("linkoraDataStore")

        val shouldFollowDynamicTheming = mutableStateOf(false)
        val shouldFollowSystemTheme = mutableStateOf(true)
        val shouldDarkThemeBeEnabled = mutableStateOf(false)
        val isInAppWebTabEnabled = mutableStateOf(true)
        val isAutoDetectTitleForLinksEnabled = mutableStateOf(false)
        val isBtmSheetEnabledForSavingLinks = mutableStateOf(false)
        val isHomeScreenEnabled = mutableStateOf(true)
        val isSendCrashReportsEnabled = mutableStateOf(true)
        val didDataAutoDataMigratedFromV9 = mutableStateOf(false)
        val isAutoCheckUpdatesEnabled = mutableStateOf(true)
        val showDescriptionForSettingsState = mutableStateOf(true)
        val isOnLatestUpdate = mutableStateOf(false)
        val didServerTimeOutErrorOccurred = mutableStateOf(false)
        private val savedAppCode = mutableIntStateOf(APP_VERSION_CODE - 1)
        val selectedSortingType = mutableStateOf(SortingPreferences.NEW_TO_OLD.name)
        val jsoupUserAgent =
            mutableStateOf("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:125.0) Gecko/20100101 Firefox/125.0")

        suspend fun <T> readSettingPreferenceValue(
            preferenceKey: androidx.datastore.preferences.core.Preferences.Key<T>,
            dataStore: DataStore<androidx.datastore.preferences.core.Preferences>,
        ): T? {
            return dataStore.data.first()[preferenceKey]
        }

        private suspend fun readSortingPreferenceValue(
            preferenceKey: androidx.datastore.preferences.core.Preferences.Key<String>,
            dataStore: DataStore<androidx.datastore.preferences.core.Preferences>,
        ): String? {
            return dataStore.data.first()[preferenceKey]
        }

        fun <T> changeSettingPreferenceValue(
            preferenceKey: androidx.datastore.preferences.core.Preferences.Key<T>,
            dataStore: DataStore<androidx.datastore.preferences.core.Preferences>,
            newValue: T,
        ) {
            viewModelScope.launch {
                dataStore.edit {
                    it[preferenceKey] = newValue
                }
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
                kotlinx.coroutines.awaitAll(
                    async {
                        shouldFollowSystemTheme.value = readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.FOLLOW_SYSTEM_THEME.name),
                            dataStore = context.dataStore
                        ) ?: (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    },
                    async {
                        shouldDarkThemeBeEnabled.value = readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.DARK_THEME.name),
                            dataStore = context.dataStore
                        ) ?: (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                    },
                    async {
                        shouldFollowDynamicTheming.value = readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.DYNAMIC_THEMING.name),
                            dataStore = context.dataStore
                        ) ?: false
                    },
                    async {
                        jsoupUserAgent.value = readSettingPreferenceValue(
                            preferenceKey = stringPreferencesKey(SettingsPreferences.JSOUP_USER_AGENT.name),
                            dataStore = context.dataStore
                        )
                            ?: "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:125.0) Gecko/20100101 Firefox/125.0"
                    },
                    async {
                        showDescriptionForSettingsState.value = readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.SETTING_COMPONENT_DESCRIPTION_STATE.name),
                            dataStore = context.dataStore
                        ) ?: true
                    },
                    async {
                        isInAppWebTabEnabled.value = readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.CUSTOM_TABS.name),
                            dataStore = context.dataStore
                        ) ?: false
                    },
                    async {
                        didDataAutoDataMigratedFromV9.value = readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.IS_DATA_MIGRATION_COMPLETED_FROM_V9.name),
                            dataStore = context.dataStore
                        ) ?: false
                    },
                    async {
                        isAutoDetectTitleForLinksEnabled.value = readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.AUTO_DETECT_TITLE_FOR_LINK.name),
                            dataStore = context.dataStore
                        ) ?: false
                    },
                    async {
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
                    },
                    async {
                        isBtmSheetEnabledForSavingLinks.value =/* readSettingPreferenceValue(
                        preferenceKey = booleanPreferencesKey(SettingsPreferences.BTM_SHEET_FOR_SAVING_LINKS.name),
                        dataStore = context.dataStore
                    ) ?:*/ false
                    },
                    async {
                        isSendCrashReportsEnabled.value = readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.SEND_CRASH_REPORTS.name),
                            dataStore = context.dataStore
                        ) ?: true
                    },
                    async {
                        isAutoCheckUpdatesEnabled.value = readSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(SettingsPreferences.AUTO_CHECK_UPDATES.name),
                            dataStore = context.dataStore
                        ) ?: true
                    },
                    async {
                        selectedSortingType.value = readSortingPreferenceValue(
                            preferenceKey = stringPreferencesKey(SettingsPreferences.SORTING_PREFERENCE.name),
                            dataStore = context.dataStore
                        ) ?: SortingPreferences.NEW_TO_OLD.name
                    },
                    async {
                        savedAppCode.intValue = readSettingPreferenceValue(
                            preferenceKey = intPreferencesKey(SettingsPreferences.SAVED_APP_CODE.name),
                            dataStore = context.dataStore
                        ) ?: (APP_VERSION_CODE - 1)
                    },
                    async {
                        RefreshLinksWorkerRequestBuilder.REFRESH_LINKS_WORKER_TAG.emit(
                            UUID.fromString(
                                readSettingPreferenceValue(
                                    preferenceKey = stringPreferencesKey(SettingsPreferences.CURRENT_WORK_MANAGER_WORK_UUID.name),
                                    dataStore = context.dataStore
                                ) ?: "d267865d-e1c9-42b7-be38-1ab6db0e312b"
                            )
                        )
                    },
                )
            }
        }
    }

    fun latestAppVersionRetriever(onTaskCompleted: () -> Unit) {
        viewModelScope.launch {
            when (val latestReleaseData = gitHubReleasesRepo.getLatestVersionData()) {
                is GitHubReleasesResult.Failure -> {

                }

                is GitHubReleasesResult.Success -> {
                    _latestReleaseInfoFromGitHubReleases.emit(latestReleaseData.data)
                }
            }
        }.invokeOnCompletion {
            onTaskCompleted()
        }
    }
    fun deleteEntireLinksAndFoldersData(onTaskCompleted: () -> Unit = {}) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                localDatabase.clearAllTables()
            }
        }.invokeOnCompletion {
            onTaskCompleted()
        }
    }
}