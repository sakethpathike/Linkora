package com.sakethh.linkora.ui.screens.settings.specific.language

import android.content.Context


sealed class LanguageSettingsScreenUIEvent {
    data class UpdatePreferredLocalLanguage(
        val context: Context,
        val languageCode: String,
        val languageName: String
    ) :
        LanguageSettingsScreenUIEvent()

    data class DownloadLatestLanguageStrings(
        val languageCode: String,
        val languageName: String
    ) :
        LanguageSettingsScreenUIEvent()

    data object RetrieveRemoteLanguagesInfo :
        LanguageSettingsScreenUIEvent()

    data class DeleteLanguageStrings(
        val context: Context,
        val languageCode: String
    ) :
        LanguageSettingsScreenUIEvent()

    data class UseStringsFetchedFromTheServer(
        val context: Context,
        val languageCode: String,
        val languageName: String
    ) :
        LanguageSettingsScreenUIEvent()

    data class UseCompiledStrings(
        val context: Context,
        val languageCode: String,
        val languageName: String
    ) :
        LanguageSettingsScreenUIEvent()

    data class Contribute(val languageCode: String) : LanguageSettingsScreenUIEvent()
}