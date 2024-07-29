package com.sakethh.linkora.worker

import android.content.Context
import android.util.Log
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
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM.Settings.readSettingPreferenceValue
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
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
        val totalLinksCount = mutableIntStateOf(0)
        var superVisorJob: Job? = null
    }

    override suspend fun doWork(): Result {
        totalLinksCount.intValue = 0
        val linksTable = localDatabase.linksDao().getAllFromLinksTable()
        val impLinksTable = localDatabase.linksDao().getAllImpLinks()
        val archiveLinksTable = localDatabase.linksDao().getAllArchiveLinks()
        val recentlyVisitedTable = localDatabase.linksDao().getAllRecentlyVisitedLinks()

        totalLinksCount.intValue =
            linksTable.size + impLinksTable.size + archiveLinksTable.size + recentlyVisitedTable.size

        val linksTableCompletedIndex = readSettingPreferenceValue(
            intPreferencesKey(SettingsScreenVM.SettingsPreferences.REFRESH_LINKS_TABLE_INDEX.name),
            applicationContext.dataStore
        ) ?: -1
        val impLinksTableCompletedIndex = readSettingPreferenceValue(
            intPreferencesKey(SettingsScreenVM.SettingsPreferences.REFRESH_IMP_LINKS_TABLE_INDEX.name),
            applicationContext.dataStore
        ) ?: -1
        val archiveTableIndexCompletedIndex = readSettingPreferenceValue(
            intPreferencesKey(SettingsScreenVM.SettingsPreferences.REFRESH_ARCHIVE_LINKS_TABLE_INDEX.name),
            applicationContext.dataStore
        ) ?: -1
        val recentlyTableIndexCompletedIndex = readSettingPreferenceValue(
            intPreferencesKey(SettingsScreenVM.SettingsPreferences.REFRESH_RECENTLY_VISITED_LINKS_TABLE_INDEX.name),
            applicationContext.dataStore
        ) ?: -1

        val linksTableSublist = linksTable.subList(
            linksTableCompletedIndex + 1, if (linksTable.isNotEmpty()) linksTable.size - 1 else 0
        )
        val impLinksSublist = impLinksTable.subList(
            impLinksTableCompletedIndex + 1,
            if (impLinksTable.isNotEmpty()) impLinksTable.size - 1 else 0
        )
        val archiveLinksSublist = archiveLinksTable.subList(
            archiveTableIndexCompletedIndex + 1,
            if (archiveLinksTable.isNotEmpty()) archiveLinksTable.size - 1 else 0
        )

        val recentlyVisitedSublist = recentlyVisitedTable.subList(
            recentlyTableIndexCompletedIndex + 1,
            if (recentlyVisitedTable.isNotEmpty()) recentlyVisitedTable.size - 1 else 0
        )

        successfulRefreshLinksCount.value =
            (linksTable.size - linksTableSublist.size) + (impLinksTable.size - impLinksSublist.size) + (archiveLinksTable.size - archiveLinksSublist.size) + (recentlyVisitedTable.size - recentlyVisitedSublist.size)

        Log.d(
            "Linkora Log",
            "Links Table Total Size : ${linksTable.size} :: Sublist Size : ${linksTableSublist.size}"
        )
        Log.d(
            "Linkora Log",
            "Imp Links Table Total Size : ${impLinksTable.size} :: Sublist Size : ${impLinksSublist.size}"
        )
        Log.d(
            "Linkora Log",
            "Links Table Total Size : ${archiveLinksTable.size} :: Sublist Size : ${archiveLinksSublist.size}"
        )
        Log.d(
            "Linkora Log",
            "Links Table Total Size : ${recentlyVisitedTable.size} :: Sublist Size : ${recentlyVisitedSublist.size}"
        )
        successfulRefreshLinksCount.emit(successfulRefreshLinksCount.value)
        
        superVisorJob =
            CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            val linksTableDeferred = async {
                linksTableSublist.forEachIndexed { index, link ->
                    when (val scrappedData =
                        linkMetaDataScrapperService.scrapeLinkData(link.webURL)) {
                        is LinkMetaDataScrapperResult.Failure -> {

                        }

                        is LinkMetaDataScrapperResult.Success -> {
                            localDatabase.linksDao().updateALinkDataFromLinksTable(
                                link.copy(
                                    title = scrappedData.data.title,
                                    imgURL = scrappedData.data.imgURL
                                )
                            )
                            SettingsScreenVM.Settings.changeSettingPreferenceValue(
                                intPreferencesKey(SettingsScreenVM.SettingsPreferences.REFRESH_LINKS_TABLE_INDEX.name),
                                applicationContext.dataStore,
                                linksTableCompletedIndex + 1 + index
                            )
                            successfulRefreshLinksCount.emit(++successfulRefreshLinksCount.value)
                            Log.d(
                                "Linkora Log",
                                "Links Table : ${linksTableCompletedIndex + 1 + index}\n" + "Successful : ${successfulRefreshLinksCount.value}"
                            )
                        }
                    }
                }
            }
            val impLinksTableDeferred = async {
                impLinksSublist.forEachIndexed { index, link ->
                    when (val scrappedData =
                        linkMetaDataScrapperService.scrapeLinkData(link.webURL)) {
                        is LinkMetaDataScrapperResult.Failure -> {

                        }

                        is LinkMetaDataScrapperResult.Success -> {
                            localDatabase.linksDao().updateALinkDataFromImpLinksTable(
                                link.copy(
                                    title = scrappedData.data.title,
                                    imgURL = scrappedData.data.imgURL
                                )
                            )
                            SettingsScreenVM.Settings.changeSettingPreferenceValue(
                                intPreferencesKey(SettingsScreenVM.SettingsPreferences.REFRESH_IMP_LINKS_TABLE_INDEX.name),
                                applicationContext.dataStore,
                                impLinksTableCompletedIndex + 1 + index
                            )
                            successfulRefreshLinksCount.emit(++successfulRefreshLinksCount.value)
                            Log.d(
                                "Linkora Log",
                                "Imp Link : ${impLinksTableCompletedIndex + 1 + index}\n" + "Successful : ${successfulRefreshLinksCount.value}"
                            )
                        }
                    }
                }
            }
            val archiveLinksTableDeferred = async {
                archiveLinksSublist.forEachIndexed { index, link ->
                    when (val scrappedData =
                        linkMetaDataScrapperService.scrapeLinkData(link.webURL)) {
                        is LinkMetaDataScrapperResult.Failure -> {

                        }

                        is LinkMetaDataScrapperResult.Success -> {
                            localDatabase.linksDao().updateALinkDataFromArchivedLinksTable(
                                link.copy(
                                    title = scrappedData.data.title,
                                    imgURL = scrappedData.data.imgURL
                                )
                            )
                            SettingsScreenVM.Settings.changeSettingPreferenceValue(
                                intPreferencesKey(SettingsScreenVM.SettingsPreferences.REFRESH_ARCHIVE_LINKS_TABLE_INDEX.name),
                                applicationContext.dataStore,
                                archiveTableIndexCompletedIndex + 1 + index
                            )
                            successfulRefreshLinksCount.emit(++successfulRefreshLinksCount.value)
                            Log.d(
                                "Linkora Log",
                                "Archive Link : ${archiveTableIndexCompletedIndex + 1 + index}\n" + "Successful : ${successfulRefreshLinksCount.value}"
                            )
                        }
                    }
                }
            }
            val recentlyVisitedTableDeferred =
                async {
                    recentlyVisitedSublist.forEachIndexed { index, link ->
                        when (val scrappedData =
                            linkMetaDataScrapperService.scrapeLinkData(link.webURL)) {
                            is LinkMetaDataScrapperResult.Failure -> {

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
                                    intPreferencesKey(SettingsScreenVM.SettingsPreferences.REFRESH_RECENTLY_VISITED_LINKS_TABLE_INDEX.name),
                                    applicationContext.dataStore,
                                    recentlyTableIndexCompletedIndex + index + 1
                                )
                                successfulRefreshLinksCount.emit(++successfulRefreshLinksCount.value)
                                Log.d(
                                    "Linkora Log",
                                    "Recently visited : ${recentlyTableIndexCompletedIndex + 1 + index}\nSuccessful : ${successfulRefreshLinksCount.value}"
                                )
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
        workManager.cancelWorkById(RefreshLinksWorkerRequestBuilder.REFRESH_LINKS_WORKER_TAG.asStateFlow().value)
        return Result.success()
    }
}