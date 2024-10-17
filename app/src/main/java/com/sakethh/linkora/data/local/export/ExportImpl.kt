package com.sakethh.linkora.data.local.export

import android.os.Build
import android.os.Environment
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.data.local.panels.PanelsRepo
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
    private val foldersRepo: FoldersRepo,
    private val panelsRepo: PanelsRepo
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

        val panelsData = async {
            panelsRepo.getAllThePanelsAsAList()
        }

        val panelFoldersData = async {
            panelsRepo.getAllThePanelFoldersAsAList()
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
            }.json"
        )
        val linksTable = linksTableData.await()
        val importantLinksTable = impLinksTableData.await()
        val foldersTable = foldersData.await()
        val archivedLinksTable = archiveLinksData.await()
        val historyLinksTable = historyLinksData.await()
        val exportPanels = panelsData.await()
        val exportPanelFolders = panelFoldersData.await()

        ExportRequestInfo.updateState(ExportRequestState.WRITING_TO_THE_FILE)

        file.writeText(
            Json.encodeToString(
                ExportSchema(
                    schemaVersion = 11,
                    linksTable = linksTable,
                    importantLinksTable = importantLinksTable,
                    foldersTable = foldersTable,
                    archivedLinksTable = archivedLinksTable,
                    archivedFoldersTable = emptyList(),
                    historyLinksTable = historyLinksTable,
                    panels = exportPanels,
                    panelFolders = exportPanelFolders,
                )
            )
        )

        ExportRequestInfo.updateState(ExportRequestState.IDLE)
    }
}