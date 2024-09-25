package com.sakethh.linkora.ui.screens.settings

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class PrivacySettingsScreenVM : ViewModel() {
    val privacySection: (context: Context) -> SettingsUIElement = { context ->
        SettingsUIElement(
            title = "",
            doesDescriptionExists = false,
            description = null,
            isSwitchNeeded = false,
            isSwitchEnabled = mutableStateOf(false),
            onSwitchStateChange = { _ -> },
            onAcknowledgmentClick = { _, _ -> },
            icon = null,
            isIconNeeded = mutableStateOf(false),
            shouldFilledIconBeUsed = mutableStateOf(false),
            shouldArrowIconBeAppear = mutableStateOf(false)
        )
    }
}