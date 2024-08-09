package com.sakethh.linkora.ui.screens.settings.specific.language

import android.content.Context


sealed class LanguageSettingsScreenUIEvent {
    data class ChangeLocalLanguage(
        val context: Context,
        val languageCode: String,
        val languageName: String
    ) :
        LanguageSettingsScreenUIEvent()

    data class Contribute(val languageCode: String) : LanguageSettingsScreenUIEvent()
}