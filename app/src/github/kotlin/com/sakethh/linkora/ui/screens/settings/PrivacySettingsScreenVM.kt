package com.sakethh.linkora.ui.screens.settings

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sakethh.linkora.LocalizedStrings.everySingleBitOfDataIsStoredLocallyOnYourDevice
import com.sakethh.linkora.LocalizedStrings.linkoraCollectsDataRelatedToAppCrashes
import com.sakethh.linkora.LocalizedStrings.sendCrashReports
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.dataStore
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.isSendCrashReportsEnabled
import kotlinx.coroutines.launch

class PrivacySettingsScreenVM : ViewModel() {
    val privacySection: (context: Context) -> SettingsUIElement = { context ->
        SettingsUIElement(
            title = sendCrashReports.value,
            doesDescriptionExists = true,
            description = if (!isSendCrashReportsEnabled.value) everySingleBitOfDataIsStoredLocallyOnYourDevice.value else linkoraCollectsDataRelatedToAppCrashes.value,
            isSwitchNeeded = true,
            isSwitchEnabled = isSendCrashReportsEnabled,
            isIconNeeded = mutableStateOf(true),
            icon = Icons.Default.BugReport,
            onSwitchStateChange = {
                viewModelScope.launch {
                    SettingsPreference.changeSettingPreferenceValue(
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
}