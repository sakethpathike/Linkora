package com.sakethh.linkora.data.local.export

import android.os.Build
import android.os.Environment
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.data.models.ExportSchema
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.DateFormat
import java.util.Date
import javax.inject.Inject

class ExportImpl @Inject constructor(
    private val linksRepo: LinksRepo,
    private val foldersRepo: FoldersRepo
) : ExportRepo {
    override suspend fun exportToAFile() = coroutineScope {
        ExportRequestInfo.updateState(ExportRequestState.GATHERING_DATA)
        val linksTableData = async {
            linksRepo.getAllFromLinksTable()
        }
        val impLinksTableData = async {
            linksRepo.getAllImpLinks()
        }
        val foldersData = async {
            foldersRepo.getAllFolders()
        }
        val archiveLinksData = async {
            linksRepo.getAllArchiveLinks()
        }
        val historyLinksData = async {
            linksRepo.getAllRecentlyVisitedLinks()
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
                DateFormat.getDateTimeInstance().format(Date()).replace(":", "").replace(" ", "")
            }.txt"
        )
        val exportAllLinks = linksTableData.await()
        val exportImpLinks = impLinksTableData.await()
        val exportAllFolders = foldersData.await()
        val exportArchiveLinks = archiveLinksData.await()
        val exportHistoryLinks = historyLinksData.await()

        ExportRequestInfo.updateState(ExportRequestState.WRITING_TO_THE_FILE)

        file.writeText(
            Json.encodeToString(
                ExportSchema(
                    schemaVersion = 11,
                    linksTable = exportAllLinks,
                    importantLinksTable = exportImpLinks,
                    foldersTable = exportAllFolders,
                    archivedLinksTable = exportArchiveLinks,
                    archivedFoldersTable = emptyList(),
                    historyLinksTable = exportHistoryLinks,
                )
            )
        )

        ExportRequestInfo.updateState(ExportRequestState.IDLE)
    }
}