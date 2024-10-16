package com.sakethh.linkora.ui.screens.settings.specific.language

import android.app.Activity
import android.app.LocaleManager
import android.content.Intent
import android.os.Build
import android.os.LocaleList
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.MainActivity
import com.sakethh.linkora.data.RequestResult
import com.sakethh.linkora.data.RequestState
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.export.ExportRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.data.local.localization.language.LanguageRepo
import com.sakethh.linkora.data.local.localization.language.translations.TranslationsRepo
import com.sakethh.linkora.data.local.restore.ImportRepo
import com.sakethh.linkora.data.remote.localization.LocalizationRepo
import com.sakethh.linkora.data.remote.releases.GitHubReleasesRepo
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.dataStore
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.useLanguageStringsBasedOnFetchedValuesFromServer
import com.sakethh.linkora.ui.screens.settings.SettingsPreferences
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.utils.linkoraLog
import com.sakethh.linkora.worker.refreshLinks.RefreshLinksWorkerRequestBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class LanguageSettingsScreenVM @Inject constructor(
    linksRepo: LinksRepo,
    importRepo: ImportRepo,
    localDatabase: LocalDatabase,
    exportRepo: ExportRepo,
    gitHubReleasesRepo: GitHubReleasesRepo,
    refreshLinksWorkerRequestBuilder: RefreshLinksWorkerRequestBuilder,
    val workManager: WorkManager,
    val translationsRepo: TranslationsRepo,
    private val localizationRepo: LocalizationRepo,
    private val languageRepo: LanguageRepo
) : SettingsScreenVM(
    linksRepo,
    importRepo,
    localDatabase,
    exportRepo,
    gitHubReleasesRepo,
    refreshLinksWorkerRequestBuilder,
    workManager
) {

    val compiledLanguages = listOf(
        Language(
            languageName = "English",
            languageCode = "en",
            languageContributionLink = "",
            localizedStringsCount = SettingsPreference.totalLocalAppStrings.intValue
        ),
        Language(
            languageName = "हिंदी",
            languageCode = "hi",
            languageContributionLink = "",
            localizedStringsCount = SettingsPreference.totalLocalAppStrings.intValue
        ),
    )

    private val _remotelyAvailableLanguages =
        MutableStateFlow<List<com.sakethh.linkora.data.local.localization.language.Language>>(
            emptyList()
        )
    val remotelyAvailableLanguages = _remotelyAvailableLanguages.asStateFlow()

    val shouldRequestingDataFromServerDialogBoxShouldAppear = mutableStateOf(false)

    init {
        viewModelScope.launch {
            languageRepo.getAllLanguages().collectLatest {
                _remotelyAvailableLanguages.emit(it)
            }
        }
        viewModelScope.launch {
            RequestResult.currentState.collectLatest {
                shouldRequestingDataFromServerDialogBoxShouldAppear.value =
                    it == RequestState.REQUESTING
            }
        }
    }

    private var triggeredFromRetrieveStrings = false
    fun onClick(languageSettingsScreenUIEvent: LanguageSettingsScreenUIEvent) {
        when (languageSettingsScreenUIEvent) {
            is LanguageSettingsScreenUIEvent.UpdatePreferredLocalLanguage -> {
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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                    languageSettingsScreenUIEvent.context.getSystemService(LocaleManager::class.java).applicationLocales =
                        LocaleList(
                            Locale.forLanguageTag(languageSettingsScreenUIEvent.languageCode)
                        )
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && !useLanguageStringsBasedOnFetchedValuesFromServer.value) {
                    val intent =
                        Intent(languageSettingsScreenUIEvent.context, MainActivity::class.java)
                    languageSettingsScreenUIEvent.context.startActivity(intent)
                    languageSettingsScreenUIEvent.context as Activity
                    languageSettingsScreenUIEvent.context.finishAffinity()
                }
            }

            is LanguageSettingsScreenUIEvent.DownloadLatestLanguageStrings -> {
                triggeredFromRetrieveStrings = true
                viewModelScope.launch {
                    async {
                        onClick(
                            LanguageSettingsScreenUIEvent.RetrieveRemoteLanguagesInfo(
                                languageSettingsScreenUIEvent.context
                            )
                        )
                    }.await()
                    when (val resultState =
                        translationsRepo.addLocalizedStrings(languageSettingsScreenUIEvent.languageCode)) {
                        is RequestResult.Failure -> {
                            pushUiEvent(CommonUiEvent.ShowToast(resultState.msg))
                        }

                        is RequestResult.Success -> {
                            pushUiEvent(CommonUiEvent.ShowToast(resultState.data))
                        }
                    }
                }.invokeOnCompletion {
                    triggeredFromRetrieveStrings = false
                }
            }

            is LanguageSettingsScreenUIEvent.UseCompiledStrings -> {
                SettingsPreference.changeSettingPreferenceValue(
                    booleanPreferencesKey(
                        SettingsPreferences.USE_REMOTE_LANGUAGE_STRINGS.name
                    ), languageSettingsScreenUIEvent.context.dataStore, newValue = false
                )
                useLanguageStringsBasedOnFetchedValuesFromServer.value =
                    false
                onClick(
                    LanguageSettingsScreenUIEvent.UpdatePreferredLocalLanguage(
                        context = languageSettingsScreenUIEvent.context,
                        languageCode = languageSettingsScreenUIEvent.languageCode,
                        languageName = languageSettingsScreenUIEvent.languageName,
                    )
                )
                LocalizedStrings.loadStrings(languageSettingsScreenUIEvent.context)
            }

            is LanguageSettingsScreenUIEvent.UseStringsFetchedFromTheServer -> {
                SettingsPreference.changeSettingPreferenceValue(
                    booleanPreferencesKey(
                        SettingsPreferences.USE_REMOTE_LANGUAGE_STRINGS.name
                    ), languageSettingsScreenUIEvent.context.dataStore, newValue = true
                )
                useLanguageStringsBasedOnFetchedValuesFromServer.value =
                    true
                onClick(
                    LanguageSettingsScreenUIEvent.UpdatePreferredLocalLanguage(
                        context = languageSettingsScreenUIEvent.context,
                        languageCode = languageSettingsScreenUIEvent.languageCode,
                        languageName = languageSettingsScreenUIEvent.languageName,
                    )
                )
                LocalizedStrings.loadStrings(languageSettingsScreenUIEvent.context)
            }

            is LanguageSettingsScreenUIEvent.DeleteLanguageStrings -> {
                viewModelScope.launch {
                    translationsRepo.deleteAllLocalizedStringsForThisLanguage(
                        languageSettingsScreenUIEvent.languageCode
                    )
                    if (SettingsPreference.preferredAppLanguageCode.value != languageSettingsScreenUIEvent.languageCode) {
                        linkoraLog("not preferredAppLanguageCode")
                        return@launch
                    }
                    SettingsPreference.changeSettingPreferenceValue(
                        booleanPreferencesKey(
                            SettingsPreferences.USE_REMOTE_LANGUAGE_STRINGS.name
                        ), languageSettingsScreenUIEvent.context.dataStore, false
                    )
                    useLanguageStringsBasedOnFetchedValuesFromServer.value = false
                    linkoraLog(languageSettingsScreenUIEvent.languageName + ":" + languageSettingsScreenUIEvent.languageCode)


                    linkoraLog("preferredAppLanguageCode")
                    if (compiledLanguages.map { it.languageCode }
                            .contains(languageSettingsScreenUIEvent.languageCode)) {
                        onClick(
                            LanguageSettingsScreenUIEvent.UpdatePreferredLocalLanguage(
                                context = languageSettingsScreenUIEvent.context,
                                languageCode = languageSettingsScreenUIEvent.languageCode,
                                languageName = languageSettingsScreenUIEvent.languageName
                            )
                        )
                    } else {
                        onClick(
                            LanguageSettingsScreenUIEvent.UpdatePreferredLocalLanguage(
                                context = languageSettingsScreenUIEvent.context,
                                languageCode = "en",
                                languageName = "English"
                            )
                        )
                    }
                    LocalizedStrings.loadStrings(languageSettingsScreenUIEvent.context)
                }
            }

            is LanguageSettingsScreenUIEvent.RetrieveRemoteLanguagesInfo -> {
                viewModelScope.launch {
                    when (val remoteLanguagesData = localizationRepo.getRemoteLanguages()) {
                        is RequestResult.Failure -> {
                            pushUiEvent(CommonUiEvent.ShowToast(remoteLanguagesData.msg))
                        }

                        is RequestResult.Success -> {
                            remoteLanguagesData.let {
                                SettingsPreference.changeSettingPreferenceValue(
                                    intPreferencesKey(SettingsPreferences.TOTAL_REMOTE_STRINGS.name),
                                    languageSettingsScreenUIEvent.context.dataStore,
                                    it.data.totalStrings
                                )
                                SettingsPreference.totalRemoteStrings.intValue =
                                    it.data.totalStrings
                                linkoraLog(triggeredFromRetrieveStrings.toString())
                                if (SettingsPreference.remoteStringsLastUpdatedOn.value == it.data.lastUpdatedOn && !triggeredFromRetrieveStrings) {
                                    pushUiEvent(CommonUiEvent.ShowToast(LocalizedStrings.languageInfoAndStringsAreUpToDate.value))
                                    return@launch
                                }
                                SettingsPreference.changeSettingPreferenceValue(
                                    stringPreferencesKey(SettingsPreferences.REMOTE_STRINGS_LAST_UPDATED_ON.name),
                                    languageSettingsScreenUIEvent.context.dataStore,
                                    it.data.lastUpdatedOn
                                )
                                SettingsPreference.remoteStringsLastUpdatedOn.value =
                                    it.data.lastUpdatedOn
                            }
                            remoteLanguagesData.let {
                                linkoraLog(it.toString())
                            }

                            languageRepo.addNewLanguages(remoteLanguagesData.data.availableLanguages.map {
                                com.sakethh.linkora.data.local.localization.language.Language(
                                    it.languageCode,
                                    it.languageName,
                                    it.localizedStringsCount,
                                    it.contributionLink
                                )
                            })
                            pushUiEvent(CommonUiEvent.ShowToast(LocalizedStrings.updatedLanguageInfoSuccessfully.value))
                        }
                    }

                }
            }

            is LanguageSettingsScreenUIEvent.ResetAppLanguage -> {
                SettingsPreference.changeSettingPreferenceValue(
                    booleanPreferencesKey(
                        SettingsPreferences.USE_REMOTE_LANGUAGE_STRINGS.name
                    ), languageSettingsScreenUIEvent.context.dataStore, false
                )
                useLanguageStringsBasedOnFetchedValuesFromServer.value = false
                onClick(
                    LanguageSettingsScreenUIEvent.UpdatePreferredLocalLanguage(
                        languageSettingsScreenUIEvent.context,
                        "en",
                        "English"
                    )
                )
                LocalizedStrings.loadStrings(languageSettingsScreenUIEvent.context)
            }
        }
    }
}