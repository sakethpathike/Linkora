package com.sakethh.linkora.worker

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.remote.scrape.LinkMetaDataScrapperResult
import com.sakethh.linkora.data.remote.scrape.LinkMetaDataScrapperService
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM.Settings.dataStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltWorker
class RefreshLinksWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val localDatabase: LocalDatabase,
    private val linkMetaDataScrapperService: LinkMetaDataScrapperService,
    private val workManager: WorkManager
) : CoroutineWorker(appContext, params) {

    companion object {
        val successfulRefreshLinksCount = MutableStateFlow(0)
        val unSuccessfulRefreshLinksCount = mutableIntStateOf(0)
        val totalLinksCount = mutableIntStateOf(0)
        var superVisorJob: Job? = null
    }

    override suspend fun doWork(): Result {
        unSuccessfulRefreshLinksCount.intValue = 0
        totalLinksCount.intValue = 0
        val linksTable = localDatabase.linksDao().getAllFromLinksTable()
        val impLinksTable = localDatabase.linksDao().getAllImpLinks()
        val archiveLinksTable = localDatabase.linksDao().getAllArchiveLinks()
        val recentlyVisitedTable = localDatabase.linksDao().getAllRecentlyVisitedLinks()

        totalLinksCount.intValue =
            linksTable.size + impLinksTable.size + archiveLinksTable.size + recentlyVisitedTable.size
        superVisorJob =
            CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler { coroutineContext, throwable -> }).launch {
            val linksTableDeferred = async {
                linksTable.forEach { link ->
                    when (val scrappedData =
                        linkMetaDataScrapperService.scrapeLinkData(link.webURL)) {
                        is LinkMetaDataScrapperResult.Failure -> {
                            unSuccessfulRefreshLinksCount.intValue++
                        }

                        is LinkMetaDataScrapperResult.Success -> {
                            localDatabase.linksDao().updateALinkDataFromLinksTable(
                                link.copy(
                                    title = scrappedData.data.title,
                                    imgURL = scrappedData.data.imgURL
                                )
                            )
                            SettingsScreenVM.Settings.changeSettingPreferenceValue(
                                intPreferencesKey(SettingsScreenVM.SettingsPreferences.CURRENT_WORK_MANAGER_REFRESH_LINK_SUCCESSFUL_COUNT.name),
                                applicationContext.dataStore,
                                successfulRefreshLinksCount.value++
                            )
                            successfulRefreshLinksCount.emit(successfulRefreshLinksCount.value)
                        }
                    }
                }
            }
            val impLinksTableDeferred = async {
                impLinksTable.forEach { link ->
                    when (val scrappedData =
                        linkMetaDataScrapperService.scrapeLinkData(link.webURL)) {
                        is LinkMetaDataScrapperResult.Failure -> {
                            unSuccessfulRefreshLinksCount.intValue++
                        }

                        is LinkMetaDataScrapperResult.Success -> {
                            localDatabase.linksDao().updateALinkDataFromImpLinksTable(
                                link.copy(
                                    title = scrappedData.data.title,
                                    imgURL = scrappedData.data.imgURL
                                )
                            )
                            SettingsScreenVM.Settings.changeSettingPreferenceValue(
                                intPreferencesKey(SettingsScreenVM.SettingsPreferences.CURRENT_WORK_MANAGER_REFRESH_LINK_SUCCESSFUL_COUNT.name),
                                applicationContext.dataStore,
                                successfulRefreshLinksCount.value++
                            )
                            successfulRefreshLinksCount.emit(successfulRefreshLinksCount.value)
                        }
                    }
                }
            }
            val archiveLinksTableDeferred = async {
                archiveLinksTable.forEach { link ->
                    when (val scrappedData =
                        linkMetaDataScrapperService.scrapeLinkData(link.webURL)) {
                        is LinkMetaDataScrapperResult.Failure -> {
                            unSuccessfulRefreshLinksCount.intValue++
                        }

                        is LinkMetaDataScrapperResult.Success -> {
                            localDatabase.linksDao().updateALinkDataFromArchivedLinksTable(
                                link.copy(
                                    title = scrappedData.data.title,
                                    imgURL = scrappedData.data.imgURL
                                )
                            )
                            SettingsScreenVM.Settings.changeSettingPreferenceValue(
                                intPreferencesKey(SettingsScreenVM.SettingsPreferences.CURRENT_WORK_MANAGER_REFRESH_LINK_SUCCESSFUL_COUNT.name),
                                applicationContext.dataStore,
                                successfulRefreshLinksCount.value++
                            )
                            successfulRefreshLinksCount.emit(successfulRefreshLinksCount.value)
                        }
                    }
                }
            }
            val recentlyVisitedTableDeferred =
                async {
                    recentlyVisitedTable.forEach { link ->
                        when (val scrappedData =
                            linkMetaDataScrapperService.scrapeLinkData(link.webURL)) {
                            is LinkMetaDataScrapperResult.Failure -> {
                                unSuccessfulRefreshLinksCount.intValue++
                            }

                            is LinkMetaDataScrapperResult.Success -> {
                                localDatabase.linksDao()
                                    .updateALinkDataFromRecentlyVisitedLinksTable(
                                        link.copy(
                                            title = scrappedData.data.title,
                                            imgURL = scrappedData.data.imgURL
                                        )
                                    )
                                SettingsScreenVM.Settings.changeSettingPreferenceValue(
                                    intPreferencesKey(SettingsScreenVM.SettingsPreferences.CURRENT_WORK_MANAGER_REFRESH_LINK_SUCCESSFUL_COUNT.name),
                                    applicationContext.dataStore,
                                    successfulRefreshLinksCount.value++
                                )
                                successfulRefreshLinksCount.emit(successfulRefreshLinksCount.value)
                            }
                        }
                    }
                }

            linksTableDeferred.await()
            impLinksTableDeferred.await()
            archiveLinksTableDeferred.await()
            recentlyVisitedTableDeferred.await()
        }
        superVisorJob?.join()
        SettingsScreenVM.Settings.changeSettingPreferenceValue(
            intPreferencesKey(SettingsScreenVM.SettingsPreferences.CURRENT_WORK_MANAGER_REFRESH_LINK_SUCCESSFUL_COUNT.name),
            applicationContext.dataStore,
            0
        )
        workManager.cancelWorkById(RefreshLinksWorkerRequestBuilder.REFRESH_LINKS_WORKER_TAG.asStateFlow().value)
        return Result.success()
    }
}