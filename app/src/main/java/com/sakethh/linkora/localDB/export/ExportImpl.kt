package com.sakethh.linkora.localDB.export

import android.os.Build
import android.os.Environment
import com.sakethh.linkora.localDB.CustomFunctionsForLocalDB
import com.sakethh.linkora.localDB.dto.ArchivedFolders
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.localDB.dto.RecentlyVisited
import com.sakethh.linkora.localDB.dto.exportImportDTOs.ExportDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.DateFormat.getDateTimeInstance
import java.util.Date


class ExportImpl {
    private val savedLinks = mutableListOf<LinksTable>()
    private val importantLinks = mutableListOf<ImportantLinks>()
    private val folders = mutableListOf<FoldersTable>()
    private val archivedLinks = mutableListOf<ArchivedLinks>()
    private val archivedFolders = mutableListOf<ArchivedFolders>()
    private val historyLinks = mutableListOf<RecentlyVisited>()

    init {
        val job = Job()
        CoroutineScope(job).launch {
            awaitAll(async {
                CustomFunctionsForLocalDB.localDB.crudDao().getAllFromLinksTable().collect {
                    savedLinks.addAll(it)
                }
            }, async {
                CustomFunctionsForLocalDB.localDB.crudDao().getAllImpLinks().collect {
                    importantLinks.addAll(it)
                }
            }, async {
                CustomFunctionsForLocalDB.localDB.crudDao().getAllFolders().collect {
                    folders.addAll(it)
                }
            }, async {
                CustomFunctionsForLocalDB.localDB.crudDao().getAllArchiveLinks().collect {
                    archivedLinks.addAll(it)
                }
            }, async {
                CustomFunctionsForLocalDB.localDB.crudDao().getAllArchiveFolders().collect {
                    archivedFolders.addAll(it)
                }
            }, async {
                CustomFunctionsForLocalDB.localDB.crudDao().getAllRecentlyVisitedLinks().collect {
                    historyLinks.addAll(it)
                }
            })
        }.start()
        if (job.isCompleted) {
            job.cancel()
        }
    }

    fun exportToAFile() {
        val defaultFolder = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            File(Environment.getExternalStorageDirectory(), "Linkora/Exports")
        } else {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "Linkora/Exports"
            )
        }
        if (!defaultFolder.exists() && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            File(Environment.getExternalStorageDirectory(), "Linkora/Exports").mkdirs()
        } else if (!defaultFolder.exists() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "Linkora/Exports"
            ).mkdirs()
        }

        val file = File(
            defaultFolder,
            "LinkoraExport_${getDateTimeInstance().format(Date()).replace(":", "_")}.txt"
        )
        file.writeText(
            Json.encodeToString(
                ExportDTO(
                    appVersion = 8,
                    savedLinks = savedLinks,
                    importantLinks = importantLinks,
                    folders = folders,
                    archivedLinks = archivedLinks,
                    archivedFolders = archivedFolders,
                    historyLinks = historyLinks,
                )
            )
        )

    }
}