package com.sakethh.linkora.ui.screens.settings.specific.language

import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.work.WorkManager
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.backup.ExportRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.data.local.restore.ImportRepo
import com.sakethh.linkora.data.remote.releases.GitHubReleasesRepo
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
    fun onClick(languageSettingsScreenUIEvent: LanguageSettingsScreenUIEvent) {
        when (languageSettingsScreenUIEvent) {
            is LanguageSettingsScreenUIEvent.ChangeLocalLanguage -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    languageSettingsScreenUIEvent.context.getSystemService(LocaleManager::class.java).applicationLocales =
                        LocaleList(
                            Locale.forLanguageTag(languageSettingsScreenUIEvent.languageCode)
                        )
                }
            }

            is LanguageSettingsScreenUIEvent.Contribute -> TODO()
        }
    }
}