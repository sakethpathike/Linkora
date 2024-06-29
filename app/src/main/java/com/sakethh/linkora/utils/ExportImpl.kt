package com.sakethh.linkora.utils

import android.os.Build
import android.os.Environment
import com.sakethh.linkora.data.localDB.LocalDataBase
import com.sakethh.linkora.data.localDB.models.exportImport.Export
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.DateFormat.getDateTimeInstance
import java.util.Date


class ExportImpl {

    suspend fun exportToAFile() = coroutineScope {
        val _exportAllLinks = async {
            LocalDataBase.localDB.readDao().getAllFromLinksTable()
        }
        val _exportImpLinks = async {
            LocalDataBase.localDB.readDao().getAllImpLinks()
        }
        val _exportAllFolders = async {
            LocalDataBase.localDB.readDao().getAllFolders()
        }
        val _exportArchiveLinks = async {
            LocalDataBase.localDB.readDao().getAllArchiveLinks()
        }
        val _exportHistoryLinks = async {
            LocalDataBase.localDB.readDao().getAllRecentlyVisitedLinks()
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
        val file = File(
            defaultFolder, "LinkoraExport-${
                getDateTimeInstance().format(Date()).replace(":", "").replace(" ", "")
            }.txt"
        )
        val exportAllLinks = _exportAllLinks.await()
        val exportImpLinks = _exportImpLinks.await()
        val exportAllFolders = _exportAllFolders.await()
        val exportArchiveLinks = _exportArchiveLinks.await()
        val exportHistoryLinks = _exportHistoryLinks.await()
        file.writeText(
            Json.encodeToString(
                Export(
                    schemaVersion = 10,
                    linksTable = exportAllLinks,
                    importantLinksTable = exportImpLinks,
                    foldersTable = exportAllFolders,
                    archivedLinksTable = exportArchiveLinks,
                    archivedFoldersTable = emptyList(),
                    historyLinksTable = exportHistoryLinks,
                )
            )
        )
    }
}