package com.sakethh.linkora.screens.settings

import android.os.Build
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.DataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val themeSection = listOf(
        SettingsUIElement(
            title = "Use dynamic theming",
            doesDescriptionExists = true,
            description = "Change color theming stuff within the app based on your wallpaper.",
            isSwitchNeeded = true,
            isSwitchEnabled = Settings.shouldFollowDynamicTheming,
            onSwitchStateChange = {
                viewModelScope.launch {
                    Settings.changePreferenceValue(
                        preferenceKey = preferencesKey(
                            SettingsPreferences.DYNAMIC_THEMING.name
                        ), dataStore = Settings.dataStore,
                        newValue = Settings.shouldFollowDynamicTheming.value
                    )
                }
            }
        ),
        SettingsUIElement(
            title = "Follow System Theme",
            doesDescriptionExists = false,
            description = null,
            isSwitchNeeded = true,
            isSwitchEnabled = Settings.shouldFollowSystemTheme,
            onSwitchStateChange = {
                viewModelScope.launch {
                    Settings.changePreferenceValue(
                        preferenceKey = preferencesKey(
                            SettingsPreferences.FOLLOW_SYSTEM_THEME.name
                        ), dataStore = Settings.dataStore,
                        newValue = Settings.shouldFollowSystemTheme.value
                    )
                }
            }
        ),
        SettingsUIElement(
            title = "Use Dark Mode",
            doesDescriptionExists = false,
            description = null,
            isSwitchNeeded = true,
            isSwitchEnabled = Settings.shouldDarkThemeBeEnabled,
            onSwitchStateChange = {
                viewModelScope.launch {
                    Settings.changePreferenceValue(
                        preferenceKey = preferencesKey(
                            SettingsPreferences.DARK_THEME.name
                        ), dataStore = Settings.dataStore,
                        newValue = Settings.shouldDarkThemeBeEnabled.value
                    )
                }
            }
        ),
    )

    val generalSection = listOf(
        SettingsUIElement(
            title = "Move entire data to Trash",
            doesDescriptionExists = false,
            description = null,
            isSwitchNeeded = false,
            isSwitchEnabled = Settings.shouldFollowDynamicTheming,
            onSwitchStateChange = {
                viewModelScope.launch {
                    Settings.changePreferenceValue(
                        preferenceKey = preferencesKey(
                            SettingsPreferences.DYNAMIC_THEMING.name
                        ), dataStore = Settings.dataStore,
                        newValue = Settings.shouldFollowDynamicTheming.value
                    )
                }
            }
        ),
        SettingsUIElement(
            title = "Delete entire data permanently",
            doesDescriptionExists = true,
            description = "Delete all links and folders; links and folders from Trash also gets deleted permanently.",
            isSwitchNeeded = false,
            isSwitchEnabled = Settings.shouldFollowDynamicTheming,
            onSwitchStateChange = {
                viewModelScope.launch {
                    Settings.changePreferenceValue(
                        preferenceKey = preferencesKey(
                            SettingsPreferences.DYNAMIC_THEMING.name
                        ), dataStore = Settings.dataStore,
                        newValue = Settings.shouldFollowDynamicTheming.value
                    )
                }
            }
        )
    )

    enum class SettingsPreferences {
        DYNAMIC_THEMING, DARK_THEME, FOLLOW_SYSTEM_THEME
    }

    object Settings {

        lateinit var dataStore: DataStore<androidx.datastore.preferences.Preferences>

        val shouldFollowDynamicTheming = mutableStateOf(false)
        val shouldFollowSystemTheme = mutableStateOf(true)
        val shouldDarkThemeBeEnabled = mutableStateOf(false)

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
    }
}