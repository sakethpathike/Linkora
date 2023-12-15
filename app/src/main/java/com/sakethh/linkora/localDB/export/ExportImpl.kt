package com.sakethh.linkora.localDB.export

import android.os.Build
import android.os.Environment
import android.util.Log
import com.sakethh.linkora.localDB.LocalDataBase
import com.sakethh.linkora.localDB.dto.ArchivedFolders
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.localDB.dto.RecentlyVisited
import com.sakethh.linkora.localDB.dto.exportImportDTOs.ExportDTO
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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


    suspend fun exportToAFile() = coroutineScope {
        val exportAllLinks = async {
            savedLinks.addAll(LocalDataBase.localDB.readDao().getAllFromLinksTable())
            Log.d(
                "data collection",
                LocalDataBase.localDB.readDao().getAllFromLinksTable().toString()
            )
        }
        val exportImpLinks = async {
            importantLinks.addAll(
                LocalDataBase.localDB.readDao().getAllImpLinks()
            )
            Log.d("data collection", LocalDataBase.localDB.readDao().getAllImpLinks().toString())

        }
        val exportAllFolders = async {
            folders.addAll(
                LocalDataBase.localDB.readDao().getAllFolders()
            )
            Log.d("data collection", LocalDataBase.localDB.readDao().getAllFolders().toString())

        }
        val exportArchiveLinks = async {
            archivedLinks.addAll(
                LocalDataBase.localDB.readDao().getAllArchiveLinks()
            )
            Log.d(
                "data collection", LocalDataBase.localDB.readDao().getAllArchiveLinks()
                    .toString()
            )

        }
        val exportHistoryLinks = async {
            historyLinks.addAll(
                LocalDataBase.localDB.readDao().getAllRecentlyVisitedLinks()
            )
            Log.d(
                "data collection",
                LocalDataBase.localDB.readDao().getAllRecentlyVisitedLinks().toString()
            )

        }

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
        exportAllFolders.await()
        exportArchiveLinks.await()
        exportHistoryLinks.await()
        exportImpLinks.await()
        exportAllLinks.await()
        val file = File(
            defaultFolder, "LinkoraExport-${
                getDateTimeInstance().format(Date()).replace(":", "").replace(" ", "")
            }.txt"
        )
        file.writeText(
            Json.encodeToString(
                ExportDTO(
                    appVersion = 10,
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