package com.sakethh.linkora.worker.refreshLinks

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.sakethh.linkora.data.RequestResult
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.remote.metadata.twitter.TwitterMetaDataRepo
import com.sakethh.linkora.data.remote.scrape.LinkMetaDataScrapperService
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.dataStore
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.readSettingPreferenceValue
import com.sakethh.linkora.ui.screens.settings.SettingsPreferences
import com.sakethh.linkora.utils.linkoraLog
import com.sakethh.linkora.utils.sanitizeLink
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
    private val workManager: WorkManager,
    private val twitterMetaDataRepo: TwitterMetaDataRepo
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
            intPreferencesKey(SettingsPreferences.REFRESH_LINKS_TABLE_INDEX.name),
            applicationContext.dataStore
        ) ?: -1
        val impLinksTableCompletedIndex = readSettingPreferenceValue(
            intPreferencesKey(SettingsPreferences.REFRESH_IMP_LINKS_TABLE_INDEX.name),
            applicationContext.dataStore
        ) ?: -1
        val archiveTableIndexCompletedIndex = readSettingPreferenceValue(
            intPreferencesKey(SettingsPreferences.REFRESH_ARCHIVE_LINKS_TABLE_INDEX.name),
            applicationContext.dataStore
        ) ?: -1
        val recentlyTableIndexCompletedIndex = readSettingPreferenceValue(
            intPreferencesKey(SettingsPreferences.REFRESH_RECENTLY_VISITED_LINKS_TABLE_INDEX.name),
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

        linkoraLog(
            "Links Table Total Size : ${linksTable.size} :: Sublist Size : ${linksTableSublist.size}"
        )
        linkoraLog(
            "Imp Links Table Total Size : ${impLinksTable.size} :: Sublist Size : ${impLinksSublist.size}"
        )
        linkoraLog(
            "Links Table Total Size : ${archiveLinksTable.size} :: Sublist Size : ${archiveLinksSublist.size}"
        )
        linkoraLog(
            "Links Table Total Size : ${recentlyVisitedTable.size} :: Sublist Size : ${recentlyVisitedSublist.size}"
        )

        successfulRefreshLinksCount.emit(successfulRefreshLinksCount.value)

        superVisorJob =
            CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                val linksTableDeferred = async {
                    linksTableSublist.forEachIndexed { index, link ->
                        var modifiedLink = link.copy(webURL = link.webURL.sanitizeLink())
                        if (link.webURL.trim().startsWith("https://x.com/") || link.webURL.trim()
                                .startsWith("http://x.com/") || link.webURL.trim()
                                .startsWith("https://twitter.com/") || link.webURL.trim()
                                .startsWith("http://twitter.com/")
                        ) {
                            when (val tweetMetaData =
                                twitterMetaDataRepo.retrieveMetaData(modifiedLink.webURL)) {
                                is RequestResult.Failure -> {

                                }

                                is RequestResult.Success -> {
                                    modifiedLink =
                                        modifiedLink.copy(title = if (!tweetMetaData.data.text.contains(
                                                "https://t.co/"
                                            )
                                        ) tweetMetaData.data.text else modifiedLink.title,
                                            imgURL = if (tweetMetaData.data.hasMedia && tweetMetaData.data.media_extended.isNotEmpty() && tweetMetaData.data.media_extended.any { it.type == "image" }) tweetMetaData.data.media_extended.find { it.type == "image" }?.url
                                                ?: tweetMetaData.data.user_profile_image_url  else if(tweetMetaData.data.hasMedia && tweetMetaData.data.media_extended.isNotEmpty() && tweetMetaData.data.media_extended.any { it.type == "video" }) tweetMetaData.data.media_extended.find { it.type == "video" }?.thumbnail_url ?: tweetMetaData.data.user_profile_image_url else if(tweetMetaData.data.hasMedia && tweetMetaData.data.media_extended.isNotEmpty() && tweetMetaData.data.media_extended.any { it.type == "gif" }) tweetMetaData.data.media_extended.find { it.type == "gif" }?.thumbnail_url ?: tweetMetaData.data.user_profile_image_url else tweetMetaData.data.user_profile_image_url,
                                            userAgent = modifiedLink.userAgent
                                                ?: SettingsPreference.primaryJsoupUserAgent.value
                                        )
                                }
                            }
                        } else {
                            when (val scrappedData =
                                linkMetaDataScrapperService.scrapeLinkData(
                                    modifiedLink.webURL,
                                    modifiedLink.userAgent
                                        ?: SettingsPreference.primaryJsoupUserAgent.value
                                )) {
                                is RequestResult.Failure -> {

                                }

                                is RequestResult.Success -> {
                                    modifiedLink = modifiedLink.copy(
                                        title = scrappedData.data.title,
                                        imgURL = scrappedData.data.imgURL,
                                        userAgent = modifiedLink.userAgent
                                            ?: SettingsPreference.primaryJsoupUserAgent.value
                                    )
                                }
                            }
                        }
                        localDatabase.linksDao().updateALinkDataFromLinksTable(
                            modifiedLink
                        )
                        SettingsPreference.changeSettingPreferenceValue(
                            intPreferencesKey(SettingsPreferences.REFRESH_LINKS_TABLE_INDEX.name),
                            applicationContext.dataStore,
                            linksTableCompletedIndex + 1 + index
                        )
                        successfulRefreshLinksCount.emit(++successfulRefreshLinksCount.value)
                        linkoraLog(
                            "Links Table : ${linksTableCompletedIndex + 1 + index}\n" + "Successful : ${successfulRefreshLinksCount.value}"
                        )
                    }
                }
                val impLinksTableDeferred = async {
                    impLinksSublist.forEachIndexed { index, link ->
                        var modifiedLink = link.copy(webURL = link.webURL.sanitizeLink())
                        if (link.webURL.trim().startsWith("https://x.com/") || link.webURL.trim()
                                .startsWith("http://x.com/") || link.webURL.trim()
                                .startsWith("https://twitter.com/") || link.webURL.trim()
                                .startsWith("http://twitter.com/")
                        ) {
                            when (val tweetMetaData =
                                twitterMetaDataRepo.retrieveMetaData(modifiedLink.webURL)) {
                                is RequestResult.Failure -> {

                                }

                                is RequestResult.Success -> {
                                    modifiedLink =
                                        modifiedLink.copy(title = if (!tweetMetaData.data.text.contains(
                                                "https://t.co/"
                                            )
                                        ) tweetMetaData.data.text else modifiedLink.title,
                                            imgURL = if (tweetMetaData.data.hasMedia && tweetMetaData.data.media_extended.isNotEmpty() && tweetMetaData.data.media_extended.any { it.type == "image" }) tweetMetaData.data.media_extended.find { it.type == "image" }?.url
                                                ?: tweetMetaData.data.user_profile_image_url else tweetMetaData.data.user_profile_image_url,
                                            userAgent = modifiedLink.userAgent
                                                ?: SettingsPreference.primaryJsoupUserAgent.value
                                        )
                                }
                            }
                        } else {
                            when (val scrappedData =
                                linkMetaDataScrapperService.scrapeLinkData(
                                    modifiedLink.webURL,
                                    modifiedLink.userAgent
                                        ?: SettingsPreference.primaryJsoupUserAgent.value
                                )) {
                                is RequestResult.Failure -> {

                                }

                                is RequestResult.Success -> {
                                    modifiedLink = modifiedLink.copy(
                                        title = scrappedData.data.title,
                                        imgURL = scrappedData.data.imgURL,
                                        userAgent = modifiedLink.userAgent
                                            ?: SettingsPreference.primaryJsoupUserAgent.value
                                    )
                                }
                            }
                        }
                        localDatabase.linksDao().updateALinkDataFromImpLinksTable(
                            modifiedLink
                        )
                        SettingsPreference.changeSettingPreferenceValue(
                            intPreferencesKey(SettingsPreferences.REFRESH_IMP_LINKS_TABLE_INDEX.name),
                            applicationContext.dataStore,
                            impLinksTableCompletedIndex + 1 + index
                        )
                        successfulRefreshLinksCount.emit(++successfulRefreshLinksCount.value)
                        linkoraLog(
                            "Imp Link : ${impLinksTableCompletedIndex + 1 + index}\n" + "Successful : ${successfulRefreshLinksCount.value}"
                        )
                    }
                }
                val archiveLinksTableDeferred = async {
                    archiveLinksSublist.forEachIndexed { index, link ->
                        var modifiedLink = link.copy(webURL = link.webURL.sanitizeLink())
                        if (link.webURL.trim().startsWith("https://x.com/") || link.webURL.trim()
                                .startsWith("http://x.com/") || link.webURL.trim()
                                .startsWith("https://twitter.com/") || link.webURL.trim()
                                .startsWith("http://twitter.com/")
                        ) {
                            when (val tweetMetaData =
                                twitterMetaDataRepo.retrieveMetaData(modifiedLink.webURL)) {
                                is RequestResult.Failure -> {

                                }

                                is RequestResult.Success -> {
                                    modifiedLink =
                                        modifiedLink.copy(title = if (!tweetMetaData.data.text.contains(
                                                "https://t.co/"
                                            )
                                        ) tweetMetaData.data.text else modifiedLink.title,
                                            imgURL = if (tweetMetaData.data.hasMedia && tweetMetaData.data.media_extended.isNotEmpty() && tweetMetaData.data.media_extended.any { it.type == "image" }) tweetMetaData.data.media_extended.find { it.type == "image" }?.url
                                                ?: tweetMetaData.data.user_profile_image_url else tweetMetaData.data.user_profile_image_url,
                                            userAgent = modifiedLink.userAgent
                                                ?: SettingsPreference.primaryJsoupUserAgent.value
                                        )
                                }
                            }
                        } else {
                            when (val scrappedData =
                                linkMetaDataScrapperService.scrapeLinkData(
                                    modifiedLink.webURL,
                                    modifiedLink.userAgent
                                        ?: SettingsPreference.primaryJsoupUserAgent.value
                                )) {
                                is RequestResult.Failure -> {

                                }

                                is RequestResult.Success -> {
                                    modifiedLink = modifiedLink.copy(
                                        title = scrappedData.data.title,
                                        imgURL = scrappedData.data.imgURL,
                                        userAgent = modifiedLink.userAgent
                                            ?: SettingsPreference.primaryJsoupUserAgent.value
                                    )
                                }
                            }
                        }
                        localDatabase.linksDao().updateALinkDataFromArchivedLinksTable(
                            modifiedLink
                        )
                        SettingsPreference.changeSettingPreferenceValue(
                            intPreferencesKey(SettingsPreferences.REFRESH_ARCHIVE_LINKS_TABLE_INDEX.name),
                            applicationContext.dataStore,
                            archiveTableIndexCompletedIndex + 1 + index
                        )
                        successfulRefreshLinksCount.emit(++successfulRefreshLinksCount.value)
                        linkoraLog(
                            "Archive Link : ${archiveTableIndexCompletedIndex + 1 + index}\n" + "Successful : ${successfulRefreshLinksCount.value}"
                        )
                    }
                }
                val recentlyVisitedTableDeferred =
                    async {
                        recentlyVisitedSublist.forEachIndexed { index, link ->
                            var modifiedLink = link.copy(webURL = link.webURL.sanitizeLink())
                            if (link.webURL.trim()
                                    .startsWith("https://x.com/") || link.webURL.trim()
                                    .startsWith("http://x.com/") || link.webURL.trim()
                                    .startsWith("https://twitter.com/") || link.webURL.trim()
                                    .startsWith("http://twitter.com/")
                            ) {
                                when (val tweetMetaData =
                                    twitterMetaDataRepo.retrieveMetaData(modifiedLink.webURL)) {
                                    is RequestResult.Failure -> {

                                    }

                                    is RequestResult.Success -> {
                                        modifiedLink =
                                            modifiedLink.copy(title = if (!tweetMetaData.data.text.contains(
                                                    "https://t.co/"
                                                )
                                            ) tweetMetaData.data.text else modifiedLink.title,
                                                imgURL = if (tweetMetaData.data.hasMedia && tweetMetaData.data.media_extended.isNotEmpty() && tweetMetaData.data.media_extended.any { it.type == "image" }) tweetMetaData.data.media_extended.find { it.type == "image" }?.url
                                                    ?: tweetMetaData.data.user_profile_image_url else tweetMetaData.data.user_profile_image_url,
                                                userAgent = modifiedLink.userAgent
                                                    ?: SettingsPreference.primaryJsoupUserAgent.value
                                            )
                                    }
                                }
                            } else {
                                when (val scrappedData =
                                    linkMetaDataScrapperService.scrapeLinkData(
                                        modifiedLink.webURL,
                                        modifiedLink.userAgent
                                            ?: SettingsPreference.primaryJsoupUserAgent.value
                                    )) {
                                    is RequestResult.Failure -> {

                                    }

                                    is RequestResult.Success -> {
                                        modifiedLink = modifiedLink.copy(
                                            title = scrappedData.data.title,
                                            imgURL = scrappedData.data.imgURL,
                                            userAgent = modifiedLink.userAgent
                                                ?: SettingsPreference.primaryJsoupUserAgent.value
                                        )
                                    }
                                }
                            }
                            localDatabase.linksDao().updateALinkDataFromRecentlyVisitedLinksTable(
                                modifiedLink
                            )
                            SettingsPreference.changeSettingPreferenceValue(
                                intPreferencesKey(SettingsPreferences.REFRESH_RECENTLY_VISITED_LINKS_TABLE_INDEX.name),
                                applicationContext.dataStore,
                                recentlyTableIndexCompletedIndex + index + 1
                            )
                            successfulRefreshLinksCount.emit(++successfulRefreshLinksCount.value)
                            linkoraLog(
                                "Recently visited : ${recentlyTableIndexCompletedIndex + 1 + index}\nSuccessful : ${successfulRefreshLinksCount.value}"
                            )
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