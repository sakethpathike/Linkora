package com.sakethh.linkora.ui.screens.settings.specific.language

import android.app.Activity
import android.app.LocaleManager
import android.content.Intent
import android.os.Build
import android.os.LocaleList
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.work.WorkManager
import com.sakethh.linkora.MainActivity
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.backup.ExportRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.data.local.restore.ImportRepo
import com.sakethh.linkora.data.remote.releases.GitHubReleasesRepo
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.dataStore
import com.sakethh.linkora.ui.screens.settings.SettingsPreferences
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.worker.RefreshLinksWorkerRequestBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class LanguageSettingsScreenVM @Inject constructor(
    private val linksRepo: LinksRepo,
    private val importRepo: ImportRepo,
    private val localDatabase: LocalDatabase,
    private val exportRepo: ExportRepo,
    private val gitHubReleasesRepo: GitHubReleasesRepo,
    private val refreshLinksWorkerRequestBuilder: RefreshLinksWorkerRequestBuilder,
    private val workManager: WorkManager
) : SettingsScreenVM(
    linksRepo,
    importRepo,
    localDatabase,
    exportRepo,
    gitHubReleasesRepo,
    refreshLinksWorkerRequestBuilder,
    workManager
) {

    val availableLanguages = listOf(
        Language(languageName = "English", languageCode = "en", languageContributionLink = ""),
        Language(languageName = "हिंदी", languageCode = "hi", languageContributionLink = ""),
    )

    fun onClick(languageSettingsScreenUIEvent: LanguageSettingsScreenUIEvent) {
        when (languageSettingsScreenUIEvent) {
            is LanguageSettingsScreenUIEvent.ChangeLocalLanguage -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    languageSettingsScreenUIEvent.context.getSystemService(LocaleManager::class.java).applicationLocales =
                        LocaleList(
                            Locale.forLanguageTag(languageSettingsScreenUIEvent.languageCode)
                        )
                }
                SettingsPreference.preferredAppLanguageCode.value =
                    languageSettingsScreenUIEvent.languageCode

                SettingsPreference.preferredAppLanguageName.value =
                    languageSettingsScreenUIEvent.languageName
                SettingsPreference.changeSettingPreferenceValue(
                    stringPreferencesKey(SettingsPreferences.APP_LANGUAGE_CODE.name),
                    languageSettingsScreenUIEvent.context.dataStore,
                    SettingsPreference.preferredAppLanguageCode.value
                )
                SettingsPreference.changeSettingPreferenceValue(
                    stringPreferencesKey(SettingsPreferences.APP_LANGUAGE_NAME.name),
                    languageSettingsScreenUIEvent.context.dataStore,
                    SettingsPreference.preferredAppLanguageName.value
                )
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    val intent =
                        Intent(languageSettingsScreenUIEvent.context, MainActivity::class.java)
                    languageSettingsScreenUIEvent.context.startActivity(intent)
                    languageSettingsScreenUIEvent.context as Activity
                    languageSettingsScreenUIEvent.context.finishAffinity()
                }
            }

            is LanguageSettingsScreenUIEvent.Contribute -> TODO()
        }
    }
}