package com.sakethh.linkora.worker

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.remote.scrape.LinkMetaDataScrapperResult
import com.sakethh.linkora.data.remote.scrape.LinkMetaDataScrapperService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class RefreshLinksWorker @Inject constructor(
    appContext: Context,
    params: WorkerParameters,
    private val localDatabase: LocalDatabase,
    private val linkMetaDataScrapperService: LinkMetaDataScrapperService
) : CoroutineWorker(appContext, params) {

    companion object {
        val successfulRefreshLinksCount = mutableIntStateOf(0)
        val unSuccessfulRefreshLinksCount = mutableIntStateOf(0)
        val totalLinksCount = mutableIntStateOf(0)
    }

    override suspend fun doWork(): Result {
        Log.d("Linkora Log", "doWork1")
        successfulRefreshLinksCount.intValue = 0
        unSuccessfulRefreshLinksCount.intValue = 0
        totalLinksCount.intValue = 0
        Log.d("Linkora Log", "doWork2")
        val linksTable = localDatabase.linksDao().getAllFromLinksTable()
        val impLinksTable = localDatabase.linksDao().getAllImpLinks()
        val archiveLinksTable = localDatabase.linksDao().getAllArchiveLinks()
        val recentlyVisitedTable = localDatabase.linksDao().getAllRecentlyVisitedLinks()

        totalLinksCount.intValue =
            linksTable.size + impLinksTable.size + archiveLinksTable.size + recentlyVisitedTable.size
        Log.d("Linkora Log", "doWork3")
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
                            successfulRefreshLinksCount.intValue++
                            Log.d("Linkora Log", successfulRefreshLinksCount.intValue.toString())
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
                            successfulRefreshLinksCount.intValue++
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
                            successfulRefreshLinksCount.intValue++
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
                                successfulRefreshLinksCount.intValue++
                            }
                        }
                    }
                }

            linksTableDeferred.await()
            impLinksTableDeferred.await()
            archiveLinksTableDeferred.await()
            recentlyVisitedTableDeferred.await()
        }
        Log.d("Linkora Log", "doWork4")
        return Result.success()
    }
}