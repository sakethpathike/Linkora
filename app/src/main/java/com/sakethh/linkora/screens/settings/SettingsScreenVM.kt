package com.sakethh.linkora.screens.settings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.DataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

data class SettingsUIElement(
    val title: String,
    val doesDescriptionExists: Boolean,
    val description: String?,
    val isSwitchNeeded: Boolean,
    val isSwitchEnabled: MutableState<Boolean>,
    val onSwitchStateChange: () -> Unit,
)

class SettingsScreenVM : ViewModel() {

    companion object {
        const val currentAppVersion = "0.0.1"
        val latestAppVersionFromServer = mutableStateOf("0.0.1")
    }

    val themeSection = listOf(
        SettingsUIElement(title = "Use dynamic theming",
            doesDescriptionExists = true,
            description = "Change color theming stuff within the app based on your wallpaper.",
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
            description = "If this is enabled, links will be opened within the app; if this setting is not enabled, your default browser will opened everytime you click on a link when using this app.",
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
        SettingsUIElement(title = "Move entire data to Trash",
            doesDescriptionExists = false,
            description = null,
            isSwitchNeeded = false,
            isSwitchEnabled = Settings.shouldFollowDynamicTheming,
            onSwitchStateChange = {

            }), SettingsUIElement(title = "Delete entire data permanently",
            doesDescriptionExists = true,
            description = "Delete all links and folders; links and folders from Trash also gets deleted permanently.",
            isSwitchNeeded = false,
            isSwitchEnabled = Settings.shouldFollowDynamicTheming,
            onSwitchStateChange = {

            })
    )

    enum class SettingsPreferences {
        DYNAMIC_THEMING, DARK_THEME, FOLLOW_SYSTEM_THEME, CUSTOM_TABS
    }

    object Settings {

        lateinit var dataStore: DataStore<androidx.datastore.preferences.Preferences>

        val shouldFollowDynamicTheming = mutableStateOf(false)
        val shouldFollowSystemTheme = mutableStateOf(true)
        val shouldDarkThemeBeEnabled = mutableStateOf(false)
        val isInAppWebTabEnabled = mutableStateOf(true)

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
                            ) == true
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
                            ) == true
                    },
                    async {
                        isInAppWebTabEnabled.value =
                            readPreferenceValue(
                                preferenceKey = preferencesKey(SettingsPreferences.CUSTOM_TABS.name),
                                dataStore = dataStore
                            ) == true
                    }
                )
            }
        }
    }
}