package com.sakethh.linkora.localDB.export

import android.os.Build
import android.os.Environment
import com.sakethh.linkora.localDB.LocalDataBase
import com.sakethh.linkora.localDB.dto.ArchivedFolders
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.localDB.dto.RecentlyVisited
import com.sakethh.linkora.localDB.dto.exportImportDTOs.ExportDTOv8
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
                LocalDataBase.localDB.readDao().getAllFromLinksTable().collect {
                    savedLinks.addAll(it)
                }
            }, async {
                LocalDataBase.localDB.readDao().getAllImpLinks().collect {
                    importantLinks.addAll(it)
                }
            }, async {
                LocalDataBase.localDB.readDao().getAllRootFolders().collect {
                    folders.addAll(it)
                }
            }, async {
                LocalDataBase.localDB.readDao().getAllArchiveLinks().collect {
                    archivedLinks.addAll(it)
                }
            }, async {
                LocalDataBase.localDB.readDao().getAllArchiveFoldersV9().collect {
                    archivedFolders.addAll(it)
                }
            }, async {
                LocalDataBase.localDB.readDao().getAllRecentlyVisitedLinks().collect {
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && !defaultFolder.exists()) {
            File(Environment.getExternalStorageDirectory(), "Linkora/Exports").mkdirs()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !defaultFolder.exists()) {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "Linkora/Exports"
            ).mkdirs()
        }

        val file = File(
            defaultFolder,
            "LinkoraExport-${
                getDateTimeInstance().format(Date()).replace(":", "").replace(" ", "")
            }.txt"
        )
        file.writeText(
            Json.encodeToString(
                ExportDTOv8(
                    appVersion = 9,
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