package com.sakethh.linkora.ui.screens.settings

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShortText
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PublicOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SystemUpdateAlt
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.UriHandler
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.LocalizedStrings.androidJetpack
import com.sakethh.linkora.LocalizedStrings.apacheLicense
import com.sakethh.linkora.LocalizedStrings.autoCheckForUpdates
import com.sakethh.linkora.LocalizedStrings.autoCheckForUpdatesDesc
import com.sakethh.linkora.LocalizedStrings.autoDetectTitle
import com.sakethh.linkora.LocalizedStrings.autoDetectTitleDesc
import com.sakethh.linkora.LocalizedStrings.coil
import com.sakethh.linkora.LocalizedStrings.enableHomeScreen
import com.sakethh.linkora.LocalizedStrings.enableHomeScreenDesc
import com.sakethh.linkora.LocalizedStrings.kotlin
import com.sakethh.linkora.LocalizedStrings.materialDesign3
import com.sakethh.linkora.LocalizedStrings.materialIcons
import com.sakethh.linkora.LocalizedStrings.permissionDeniedTitle
import com.sakethh.linkora.LocalizedStrings.showDescriptionForSettings
import com.sakethh.linkora.LocalizedStrings.showDescriptionForSettingsDesc
import com.sakethh.linkora.LocalizedStrings.successfullyExported
import com.sakethh.linkora.LocalizedStrings.useInAppBrowser
import com.sakethh.linkora.LocalizedStrings.useInAppBrowserDesc
import com.sakethh.linkora.data.RequestResult
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.data.local.export.ExportRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.data.local.restore.ImportRepo
import com.sakethh.linkora.data.remote.releases.GitHubReleasesRepo
import com.sakethh.linkora.data.remote.releases.model.GitHubReleaseDTOItem
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.CustomWebTab
import com.sakethh.linkora.ui.screens.settings.Preferences.dataStore
import com.sakethh.linkora.worker.refreshLinks.RefreshLinksWorker
import com.sakethh.linkora.worker.refreshLinks.RefreshLinksWorkerRequestBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

@HiltViewModel
open class SettingsScreenVM @Inject constructor(
    private val linksRepo: LinksRepo,
    private val importRepo: ImportRepo,
    private val localDatabase: LocalDatabase,
    private val exportRepo: ExportRepo,
    private val gitHubReleasesRepo: GitHubReleasesRepo,
    private val refreshLinksWorkerRequestBuilder: RefreshLinksWorkerRequestBuilder,
    private val workManager: WorkManager
) : CustomWebTab(linksRepo) {

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

    fun cancelRefreshAllLinksImagesAndTitlesWork() {
        RefreshLinksWorker.superVisorJob?.cancel()
        workManager.cancelAllWork()
    }

    companion object {
        val isAnyRefreshingTaskGoingOn = mutableStateOf(false)
        val currentSelectedSettingSection = mutableStateOf(SettingsSection.THEME)
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

    val acknowledgmentsSection: () -> List<SettingsUIElement> = {
        listOf(
            SettingsUIElement(
                title = kotlin.value,
                doesDescriptionExists = true,
                description = apacheLicense.value,
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
                                infoForSaving = "",
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
                title = androidJetpack.value,
                doesDescriptionExists = true,
                description = apacheLicense.value,
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
                title = coil.value,
                doesDescriptionExists = true,
                description = apacheLicense.value,
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
                title = materialDesign3.value,
                doesDescriptionExists = true,
                description = apacheLicense.value,
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
                description = apacheLicense.value,
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
                description = apacheLicense.value,
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
                title = materialIcons.value,
                doesDescriptionExists = true,
                description = apacheLicense.value,
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
            SettingsUIElement(
                title = "vxTwitter",
                doesDescriptionExists = true,
                description = "WTFPL License",
                isSwitchNeeded = false,
                isSwitchEnabled = mutableStateOf(false),
                onSwitchStateChange = {

                },
                onAcknowledgmentClick = { uriHandler, context ->
                    viewModelScope.launch {
                        openInWeb(
                            recentlyVisitedData = RecentlyVisited(
                                title = "dylanpdx/BetterTwitFix: Fix Twitter video embeds in Discord (and Telegram!)",
                                webURL = "https://github.com/dylanpdx/BetterTwitFix",
                                baseURL = "github.com",
                                imgURL = "https://opengraph.githubassets.com/e14998b463f6fccffcd69d5e806a19576cff9fedc5ef525ae9aa32661c425d5c/dylanpdx/BetterTwitFix",
                                infoForSaving = "",
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
                title = "Poppins",
                doesDescriptionExists = true,
                description = "Open Font License",
                isSwitchNeeded = false,
                isSwitchEnabled = mutableStateOf(false),
                onSwitchStateChange = {

                },
                onAcknowledgmentClick = { uriHandler, context ->
                    viewModelScope.launch {
                        openInWeb(
                            recentlyVisitedData = RecentlyVisited(
                                title = "Poppins - Google Fonts",
                                webURL = "https://fonts.google.com/specimen/Poppins",
                                baseURL = "fonts.google.com",
                                imgURL = "https://www.gstatic.com/images/icons/material/apps/fonts/1x/catalog/v5/opengraph_color.png",
                                infoForSaving = "",
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
    }

    val generalSection: (context: Context) -> List<SettingsUIElement> = { context ->
        listOf(
            SettingsUIElement(
                title = useInAppBrowser.value,
                doesDescriptionExists = Preferences.showDescriptionForSettingsState.value,
                description = useInAppBrowserDesc.value,
                isSwitchNeeded = true,
                isSwitchEnabled = Preferences.isInAppWebTabEnabled,
                isIconNeeded = mutableStateOf(true),
                icon = Icons.Default.OpenInBrowser,
                onSwitchStateChange = {
                    viewModelScope.launch {
                        Preferences.changeSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(
                                PreferenceType.CUSTOM_TABS.name
                            ), dataStore = context.dataStore, newValue = it
                        )
                        Preferences.isInAppWebTabEnabled.value = it
                    }
                }), SettingsUIElement(
                title = enableHomeScreen.value,
                doesDescriptionExists = Preferences.showDescriptionForSettingsState.value,
                description = enableHomeScreenDesc.value,
                isSwitchNeeded = true,
                isIconNeeded = mutableStateOf(true),
                icon = Icons.Default.Home,
                isSwitchEnabled = Preferences.isHomeScreenEnabled,
                onSwitchStateChange = {
                    viewModelScope.launch {
                        Preferences.changeSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(
                                PreferenceType.HOME_SCREEN_VISIBILITY.name
                            ), dataStore = context.dataStore, newValue = it
                        )
                        Preferences.isHomeScreenEnabled.value = it
                    }
                }), SettingsUIElement(
                title = autoDetectTitle.value,
                doesDescriptionExists = true,
                description = autoDetectTitleDesc.value,
                isSwitchNeeded = true,
                isSwitchEnabled = Preferences.isAutoDetectTitleForLinksEnabled,
                isIconNeeded = mutableStateOf(true),
                icon = Icons.Default.Search,
                onSwitchStateChange = {
                    viewModelScope.launch {
                        Preferences.changeSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(
                                PreferenceType.AUTO_DETECT_TITLE_FOR_LINK.name
                            ), dataStore = context.dataStore, newValue = it
                        )
                        Preferences.isAutoDetectTitleForLinksEnabled.value = it

                        if (it) {
                            Preferences.changeSettingPreferenceValue(
                                preferenceKey = booleanPreferencesKey(
                                    PreferenceType.FORCE_SAVE_WITHOUT_FETCHING_META_DATA.name
                                ), dataStore = context.dataStore, newValue = false
                            )
                            Preferences.forceSaveWithoutFetchingAnyMetaData.value = false
                        }
                    }
                }), SettingsUIElement(
                title = "Force-save links without fetching metadata",
                doesDescriptionExists = true,
                description = "Link will be saved as you save it, nothing gets fetched. Note that this will impact on refreshing links from link menu, link will NOT be refreshed if this is enabled.",
                isSwitchNeeded = true,
                isSwitchEnabled = Preferences.forceSaveWithoutFetchingAnyMetaData,
                isIconNeeded = mutableStateOf(true),
                icon = Icons.Default.PublicOff,
                onSwitchStateChange = {
                    viewModelScope.launch {
                        Preferences.changeSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(
                                PreferenceType.FORCE_SAVE_WITHOUT_FETCHING_META_DATA.name
                            ), dataStore = context.dataStore, newValue = it
                        )
                        Preferences.forceSaveWithoutFetchingAnyMetaData.value = it

                        if (it) {
                            Preferences.changeSettingPreferenceValue(
                                preferenceKey = booleanPreferencesKey(
                                    PreferenceType.AUTO_DETECT_TITLE_FOR_LINK.name
                                ), dataStore = context.dataStore, newValue = false
                            )
                            Preferences.isAutoDetectTitleForLinksEnabled.value = false
                        }
                    }
                }), SettingsUIElement(
                title = LocalizedStrings.showAssociatedImageInLinkMenu.value,
                doesDescriptionExists = true,
                description = LocalizedStrings.enablesTheDisplayOfAnAssociatedImageWithinTheLinkMenu.value,
                isSwitchNeeded = true,
                isSwitchEnabled = Preferences.showAssociatedImagesInLinkMenu,
                isIconNeeded = mutableStateOf(true),
                icon = Icons.Default.Image,
                onSwitchStateChange = {
                    viewModelScope.launch {
                        Preferences.changeSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(
                                PreferenceType.ASSOCIATED_IMAGES_IN_LINK_MENU_VISIBILITY.name
                            ), dataStore = context.dataStore, newValue = it
                        )
                        Preferences.showAssociatedImagesInLinkMenu.value = it
                    }
                }), SettingsUIElement(
                title = autoCheckForUpdates.value,
                doesDescriptionExists = Preferences.showDescriptionForSettingsState.value,
                description = autoCheckForUpdatesDesc.value,
                isIconNeeded = mutableStateOf(true),
                icon = Icons.Default.SystemUpdateAlt,
                isSwitchNeeded = true,
                isSwitchEnabled = Preferences.isAutoCheckUpdatesEnabled,
                onSwitchStateChange = {
                    viewModelScope.launch {
                        Preferences.changeSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(
                                PreferenceType.AUTO_CHECK_UPDATES.name
                            ), dataStore = context.dataStore, newValue = it
                        )
                        Preferences.isAutoCheckUpdatesEnabled.value = it
                    }
                }), SettingsUIElement(
                title = showDescriptionForSettings.value,
                doesDescriptionExists = true,
                description = showDescriptionForSettingsDesc.value,
                isSwitchNeeded = true,
                isIconNeeded = mutableStateOf(true),
                icon = Icons.AutoMirrored.Default.ShortText,
                isSwitchEnabled = Preferences.showDescriptionForSettingsState,
                onSwitchStateChange = {
                    viewModelScope.launch {
                        Preferences.changeSettingPreferenceValue(
                            preferenceKey = booleanPreferencesKey(
                                PreferenceType.SETTING_COMPONENT_DESCRIPTION_STATE.name
                            ), dataStore = context.dataStore, newValue = it
                        )
                        Preferences.showDescriptionForSettingsState.value = it
                    }
                })
        )
    }

    fun importData(
        uri: Uri,
        context: Context,
        importJsonBasedFile: Boolean
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            if (importJsonBasedFile) {
                importRepo.importToLocalDBBasedOnLinkoraJSONSchema(uri, context)
            } else {
                importRepo.importToLocalDBBasedOnHTML(uri, context)
            }
        }
    }

    private val _eventChannel = Channel<CommonUiEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    protected suspend fun pushUiEvent(commonUiEvent: CommonUiEvent) {
        _eventChannel.send(commonUiEvent)
    }

    fun exportDataToAFile(
        context: Context,
        isDialogBoxVisible: MutableState<Boolean>,
        runtimePermission: ManagedActivityResultLauncher<String, Boolean>,
        exportInHTMLFormat: Boolean
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            viewModelScope.launch(Dispatchers.IO) {
                exportRepo.exportToAFile(exportInHTMLFormat)
                pushUiEvent(CommonUiEvent.ShowToast(successfullyExported.value))
            }
        } else {
            when (ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )) {
                PackageManager.PERMISSION_GRANTED -> {
                    viewModelScope.launch(Dispatchers.IO) {
                        exportRepo.exportToAFile(exportInHTMLFormat)
                        pushUiEvent(CommonUiEvent.ShowToast(successfullyExported.value))
                    }
                    isDialogBoxVisible.value = false
                }

                else -> {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        runtimePermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        viewModelScope.launch {
                            pushUiEvent(CommonUiEvent.ShowToast(permissionDeniedTitle.value))
                        }
                    }
                }
            }
        }
    }

    fun latestAppVersionRetriever(onTaskCompleted: () -> Unit) {
        viewModelScope.launch {
            when (val latestReleaseData = gitHubReleasesRepo.getLatestVersionData()) {
                is RequestResult.Failure -> {

                }

                is RequestResult.Success -> {
                    _latestReleaseInfoFromGitHubReleases.emit(latestReleaseData.data)
                }
            }
        }.invokeOnCompletion {
            onTaskCompleted()
        }
    }

    fun deleteEntireLinksAndFoldersData(onTaskCompleted: () -> Unit = {}, context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                localDatabase.clearAllTables()
                Preferences.lastSelectedPanelID.longValue = -1
                Preferences.changeSettingPreferenceValue(
                    intPreferencesKey(PreferenceType.LAST_SELECTED_PANEL_ID.name),
                    context.dataStore,
                    newValue = -1
                )
            }
        }.invokeOnCompletion {
            onTaskCompleted()
        }
    }
}

