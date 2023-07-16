package com.sakethh.linkora.screens.settings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.DataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.screens.settings.appInfo.dto.AppInfoDTO
import com.sakethh.linkora.screens.settings.appInfo.dto.MutableStateAppInfoDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SettingsUIElement(
    val title: String,
    val doesDescriptionExists: Boolean,
    val description: String?,
    val isSwitchNeeded: Boolean,
    val isSwitchEnabled: MutableState<Boolean>,
    val onSwitchStateChange: () -> Unit,
)

class SettingsScreenVM : ViewModel() {

    val shouldDeleteDialogBoxAppear = mutableStateOf(false)

    companion object {
        const val currentAppVersion = "0.0.1"
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

    val themeSection = listOf(
        SettingsUIElement(title = "Follow System Theme",
            doesDescriptionExists = false,
            description = null,
            isSwitchNeeded = true,
            isSwitchEnabled = Settings.shouldFollowSystemTheme,
            onSwitchStateChange = {
                viewModelScope.launch {
                    Settings.changePreferenceValue(
                        preferenceKey = preferencesKey(
                            SettingsPreferences.FOLLOW_SYSTEM_THEME.name
                        ),
                        dataStore = Settings.dataStore,
                        newValue = !Settings.shouldFollowSystemTheme.value
                    )
                    Settings.shouldFollowSystemTheme.value = Settings.readPreferenceValue(
                        preferenceKey = preferencesKey(SettingsPreferences.FOLLOW_SYSTEM_THEME.name),
                        dataStore = Settings.dataStore
                    ) == true
                }
            }),
        SettingsUIElement(title = "Use dynamic theming",
            doesDescriptionExists = true,
            description = "Change colour themes within the app based on your wallpaper.",
            isSwitchNeeded = true,
            isSwitchEnabled = Settings.shouldFollowDynamicTheming,
            onSwitchStateChange = {
                viewModelScope.launch {
                    Settings.changePreferenceValue(
                        preferenceKey = preferencesKey(
                            SettingsPreferences.DYNAMIC_THEMING.name
                        ),
                        dataStore = Settings.dataStore,
                        newValue = !Settings.shouldFollowDynamicTheming.value
                    )
                    Settings.shouldFollowDynamicTheming.value = Settings.readPreferenceValue(
                        preferenceKey = preferencesKey(SettingsPreferences.DYNAMIC_THEMING.name),
                        dataStore = Settings.dataStore
                    ) == true
                }
            }),
        SettingsUIElement(title = "Use Dark Mode",
            doesDescriptionExists = false,
            description = null,
            isSwitchNeeded = true,
            isSwitchEnabled = Settings.shouldDarkThemeBeEnabled,
            onSwitchStateChange = {
                viewModelScope.launch {
                    Settings.changePreferenceValue(
                        preferenceKey = preferencesKey(
                            SettingsPreferences.DARK_THEME.name
                        ),
                        dataStore = Settings.dataStore,
                        newValue = !Settings.shouldDarkThemeBeEnabled.value
                    )
                    Settings.shouldDarkThemeBeEnabled.value = Settings.readPreferenceValue(
                        preferenceKey = preferencesKey(SettingsPreferences.DARK_THEME.name),
                        dataStore = Settings.dataStore
                    ) == true
                }
            }),
    )

    val generalSection = listOf(
        SettingsUIElement(title = "Use in-app browser",
            doesDescriptionExists = true,
            description = "If this is enabled, links will be opened within the app; if this setting is not enabled, your default browser will open every time you click on a link when using this app.",
            isSwitchNeeded = true,
            isSwitchEnabled = Settings.isInAppWebTabEnabled,
            onSwitchStateChange = {
                viewModelScope.launch {
                    Settings.changePreferenceValue(
                        preferenceKey = preferencesKey(
                            SettingsPreferences.CUSTOM_TABS.name
                        ),
                        dataStore = Settings.dataStore,
                        newValue = !Settings.isInAppWebTabEnabled.value
                    )
                    Settings.isInAppWebTabEnabled.value = Settings.readPreferenceValue(
                        preferenceKey = preferencesKey(SettingsPreferences.CUSTOM_TABS.name),
                        dataStore = Settings.dataStore
                    ) == true
                }
            }),
        SettingsUIElement(title = "Enable Home Screen",
            doesDescriptionExists = true,
            description = "If this is enabled, Home Screen option will be shown in Bottom Navigation Bar; if this setting is not enabled, Home screen option will NOT be shown.",
            isSwitchNeeded = true,
            isSwitchEnabled = Settings.isHomeScreenEnabled,
            onSwitchStateChange = {
                viewModelScope.launch {
                    Settings.changePreferenceValue(
                        preferenceKey = preferencesKey(
                            SettingsPreferences.HOME_SCREEN_VISIBILITY.name
                        ),
                        dataStore = Settings.dataStore,
                        newValue = !Settings.isHomeScreenEnabled.value
                    )
                    Settings.isHomeScreenEnabled.value = Settings.readPreferenceValue(
                        preferenceKey = preferencesKey(SettingsPreferences.HOME_SCREEN_VISIBILITY.name),
                        dataStore = Settings.dataStore
                    ) == true
                }
            }),
        SettingsUIElement(title = "Use Bottom Sheet UI for saving links",
            doesDescriptionExists = true,
            description = "If this is enabled, Bottom sheet will pop-up while saving a link; if this setting is not enabled, a dialog box will be shown instead of bottom sheet.",
            isSwitchNeeded = true,
            isSwitchEnabled = Settings.isBtmSheetEnabledForSavingLinks,
            onSwitchStateChange = {
                viewModelScope.launch {
                    Settings.changePreferenceValue(
                        preferenceKey = preferencesKey(
                            SettingsPreferences.BTM_SHEET_FOR_SAVING_LINKS.name
                        ),
                        dataStore = Settings.dataStore,
                        newValue = !Settings.isBtmSheetEnabledForSavingLinks.value
                    )
                    Settings.isBtmSheetEnabledForSavingLinks.value = Settings.readPreferenceValue(
                        preferenceKey = preferencesKey(SettingsPreferences.BTM_SHEET_FOR_SAVING_LINKS.name),
                        dataStore = Settings.dataStore
                    ) == true
                }
            }),
        SettingsUIElement(title = "Auto-Detect Title",
            doesDescriptionExists = true,
            description = "If this is enabled, title for the links you save will be detected automatically.\n\nNote: This may not detect every website.\n\nIf this is disabled, you'll get an option while saving link(s) to give a title to the respective link you're saving.",
            isSwitchNeeded = true,
            isSwitchEnabled = Settings.isAutoDetectTitleForLinksEnabled,
            onSwitchStateChange = {
                viewModelScope.launch {
                    Settings.changePreferenceValue(
                        preferenceKey = preferencesKey(
                            SettingsPreferences.AUTO_DETECT_TITLE_FOR_LINK.name
                        ),
                        dataStore = Settings.dataStore,
                        newValue = !Settings.isAutoDetectTitleForLinksEnabled.value
                    )
                    Settings.isAutoDetectTitleForLinksEnabled.value = Settings.readPreferenceValue(
                        preferenceKey = preferencesKey(SettingsPreferences.AUTO_DETECT_TITLE_FOR_LINK.name),
                        dataStore = Settings.dataStore
                    ) == true
                }
            }), SettingsUIElement(title = "Delete entire data permanently",
            doesDescriptionExists = true,
            description = "Delete all links and folders permanently.",
            isSwitchNeeded = false,
            isSwitchEnabled = Settings.shouldFollowDynamicTheming,
            onSwitchStateChange = {
                shouldDeleteDialogBoxAppear.value = true
            })
    )

    enum class SettingsPreferences {
        DYNAMIC_THEMING, DARK_THEME, FOLLOW_SYSTEM_THEME, CUSTOM_TABS,
        AUTO_DETECT_TITLE_FOR_LINK, BTM_SHEET_FOR_SAVING_LINKS, HOME_SCREEN_VISIBILITY
    }

    object Settings {

        lateinit var dataStore: DataStore<androidx.datastore.preferences.Preferences>

        val shouldFollowDynamicTheming = mutableStateOf(false)
        val shouldFollowSystemTheme = mutableStateOf(true)
        val shouldDarkThemeBeEnabled = mutableStateOf(false)
        val isInAppWebTabEnabled = mutableStateOf(true)
        val isAutoDetectTitleForLinksEnabled = mutableStateOf(false)
        val isBtmSheetEnabledForSavingLinks = mutableStateOf(true)
        val isHomeScreenEnabled = mutableStateOf(true)
        suspend fun readPreferenceValue(
            preferenceKey: androidx.datastore.preferences.Preferences.Key<Boolean>,
            dataStore: DataStore<androidx.datastore.preferences.Preferences>,
        ): Boolean? {
            return dataStore.data.first()[preferenceKey]
        }

        suspend fun changePreferenceValue(
            preferenceKey: androidx.datastore.preferences.Preferences.Key<Boolean>,
            dataStore: DataStore<androidx.datastore.preferences.Preferences>, newValue: Boolean,
        ) {
            dataStore.edit {
                it[preferenceKey] = newValue
            }
        }

        suspend fun readAllPreferencesValues() {
            coroutineScope {
                kotlinx.coroutines.awaitAll(
                    async {
                        shouldFollowSystemTheme.value =
                            readPreferenceValue(
                                preferenceKey = preferencesKey(SettingsPreferences.FOLLOW_SYSTEM_THEME.name),
                                dataStore = dataStore
                            ) ?: true
                    },
                    async {
                        shouldDarkThemeBeEnabled.value =
                            readPreferenceValue(
                                preferenceKey = preferencesKey(SettingsPreferences.DARK_THEME.name),
                                dataStore = dataStore
                            ) == true
                    },
                    async {
                        shouldFollowDynamicTheming.value =
                            readPreferenceValue(
                                preferenceKey = preferencesKey(SettingsPreferences.DYNAMIC_THEMING.name),
                                dataStore = dataStore
                            ) ?: false
                    },
                    async {
                        isInAppWebTabEnabled.value =
                            readPreferenceValue(
                                preferenceKey = preferencesKey(SettingsPreferences.CUSTOM_TABS.name),
                                dataStore = dataStore
                            ) ?: true
                    },
                    async {
                        isAutoDetectTitleForLinksEnabled.value =
                            readPreferenceValue(
                                preferenceKey = preferencesKey(SettingsPreferences.AUTO_DETECT_TITLE_FOR_LINK.name),
                                dataStore = dataStore
                            ) ?: true
                    },
                    async {
                        isHomeScreenEnabled.value =
                            readPreferenceValue(
                                preferenceKey = preferencesKey(SettingsPreferences.HOME_SCREEN_VISIBILITY.name),
                                dataStore = dataStore
                            ) ?: true
                    },
                    async {
                        isBtmSheetEnabledForSavingLinks.value =
                            readPreferenceValue(
                                preferenceKey = preferencesKey(SettingsPreferences.BTM_SHEET_FOR_SAVING_LINKS.name),
                                dataStore = dataStore
                            ) ?: true
                    }
                )
            }
        }

        suspend fun latestAppVersionRetriever() {
            val ktorClient = HttpClient(Android)
            val retrievedData =
                ktorClient.get("https://64b38a3fe4fdea7cae88f072--whimsical-gingersnap-139623.netlify.app/")
            val latestAppData =
                retrievedData.body<AppInfoDTO>()
            latestAppInfoFromServer.apply {
                this.latestVersion.value = latestAppData.latestVersion
                this.latestStableVersion.value = latestAppData.latestStableVersion
                this.latestStableVersionReleaseURL.value =
                    latestAppData.latestStableVersionReleaseURL
                this.latestVersionReleaseURL.value = latestAppData.latestVersionReleaseURL
                this.changeLogForLatestVersion.value = latestAppData.changeLogForLatestVersion
                this.changeLogForLatestStableVersion.value =
                    latestAppData.changeLogForLatestStableVersion
                this.httpStatusCodeFromServer.value = retrievedData.status.value.toString()
            }
            ktorClient.close()
        }
    }
}