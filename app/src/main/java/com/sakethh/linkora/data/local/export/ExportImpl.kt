package com.sakethh.linkora.data.local.export

import android.os.Build
import android.os.Environment
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.data.local.panels.PanelsRepo
import com.sakethh.linkora.data.models.ExportSchema
import com.sakethh.linkora.utils.LinkoraExports
import com.sakethh.linkora.utils.linkoraLog
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
    override suspend fun exportToAFile(exportInHTMLFormat: Boolean) = coroutineScope {
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
            }.${if (exportInHTMLFormat) "html" else "json"}"
        )
        val linksTable = linksTableData.await()
        val importantLinksTable = impLinksTableData.await()
        val foldersTable = foldersData.await()
        val archivedLinksTable = archiveLinksData.await()
        val historyLinksTable = historyLinksData.await()
        val exportPanels = panelsData.await()
        val exportPanelFolders = panelFoldersData.await()


        if (!exportInHTMLFormat) {
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
        } else {

            ExportRequestInfo.totalLinksFromSavedLinks.intValue =
                linksRepo.getAllSavedLinksAsList().size
            ExportRequestInfo.totalLinksFromArchivedLinksTable.intValue = archivedLinksTable.size
            ExportRequestInfo.totalLinksFromImpLinksTable.intValue = importantLinksTable.size
            ExportRequestInfo.totalLinksFromHistoryLinksTable.intValue = historyLinksTable.size
            ExportRequestInfo.totalRegularFoldersAndItsLinks.intValue =
                foldersRepo.getAllRootFoldersList().size
            ExportRequestInfo.totalArchivedFoldersAndItsLinks.intValue =
                foldersRepo.getAllArchiveFoldersV10AsList().size

            ExportRequestInfo.currentIterationOfLinksFromSavedLinks.intValue = 0
            ExportRequestInfo.currentIterationOfLinksFromArchivedLinksTable.intValue = 0
            ExportRequestInfo.currentIterationOfLinksFromImpLinksTable.intValue = 0
            ExportRequestInfo.currentIterationOfLinksFromHistoryLinksTable.intValue = 0
            ExportRequestInfo.currentIterationOfRegularFoldersAndItsLinks.intValue = 0
            ExportRequestInfo.currentIterationOfArchivedFoldersAndItsLinks.intValue = 0


            var htmlFileRawText = ""

            // Saved Links :
            var savedLinksSection = dtH3(LinkoraExports.SAVED_LINKS__LINKORA_EXPORT.name)

            var savedLinks = ""
            val savedLinksAsync = async {
                linksRepo.getAllSavedLinksAsList().forEach { savedLink ->
                    savedLinks += dtA(linkTitle = savedLink.title, link = savedLink.webURL)
                    ++ExportRequestInfo.currentIterationOfLinksFromSavedLinks.intValue
                }
            }

            // Important Links :
            var impLinksSection = dtH3(LinkoraExports.IMPORTANT_LINKS__LINKORA_EXPORT.name)

            var impLinks = ""
            val impLinksAsync = async {
                importantLinksTable.forEach { impLink ->
                    impLinks += dtA(linkTitle = impLink.title, link = impLink.webURL)
                    ++ExportRequestInfo.currentIterationOfLinksFromImpLinksTable.intValue
                }
            }


            // Regular Folders :
            val regularFoldersAndRespectiveLinksAsync = async {
                dtH3(LinkoraExports.REGULAR_FOLDERS__LINKORA_EXPORT.name) + dlP(
                    foldersSectionInHtml(
                        parentFolderId = null,
                        forArchiveFolders = false
                    )
                )
            }
            // Archived Folders :
            val archivedFoldersAndRespectiveLinksAsync = async {
                dtH3(LinkoraExports.ARCHIVED_FOLDERS__LINKORA_EXPORT.name) + dlP(
                    foldersSectionInHtml(
                        parentFolderId = null,
                        forArchiveFolders = true
                    )
                )
            }

            // History Links :
            var historyLinksSection = dtH3(LinkoraExports.HISTORY_LINKS__LINKORA_EXPORT.name)

            var historyLinks = ""
            val historyLinksAsync = async {
                historyLinksTable.forEach { historyLink ->
                    historyLinks += dtA(linkTitle = historyLink.title, link = historyLink.webURL)
                    ++ExportRequestInfo.currentIterationOfLinksFromHistoryLinksTable.intValue
                }
            }

            // Archived Links :
            var archivedLinksSection = dtH3(LinkoraExports.ARCHIVED_LINKS__LINKORA_EXPORT.name)

            var archivedLinks = ""
            val archivedLinksAsync = async {
                archivedLinksTable.forEach { archivedLink ->
                    archivedLinks += dtA(linkTitle = archivedLink.title, link = archivedLink.webURL)
                    ++ExportRequestInfo.currentIterationOfLinksFromArchivedLinksTable.intValue
                }
            }

            ExportRequestInfo.updateState(ExportRequestState.READING_SAVED_LINKS)
            savedLinksAsync.await()
            savedLinksSection += dlP(savedLinks)
            htmlFileRawText += savedLinksSection

            ExportRequestInfo.updateState(ExportRequestState.READING_IMPORTANT_LINKS)
            impLinksAsync.await()
            impLinksSection += dlP(impLinks)
            htmlFileRawText += impLinksSection

            ExportRequestInfo.updateState(ExportRequestState.READING_REGULAR_FOLDERS)
            htmlFileRawText += regularFoldersAndRespectiveLinksAsync.await()

            ExportRequestInfo.updateState(ExportRequestState.READING_ARCHIVED_FOLDERS)
            htmlFileRawText += archivedFoldersAndRespectiveLinksAsync.await()

            ExportRequestInfo.updateState(ExportRequestState.READING_HISTORY_LINKS)
            historyLinksAsync.await()
            historyLinksSection += dlP(historyLinks)
            htmlFileRawText += historyLinksSection

            ExportRequestInfo.updateState(ExportRequestState.READING_ARCHIVED_LINKS)
            archivedLinksAsync.await()
            archivedLinksSection += dlP(archivedLinks)
            htmlFileRawText += archivedLinksSection

            // Result :
            linkoraLog(dlP(htmlFileRawText))

            ExportRequestInfo.updateState(ExportRequestState.WRITING_TO_THE_FILE)

            file.writeText(dlP(htmlFileRawText))
        }

        ExportRequestInfo.updateState(ExportRequestState.IDLE)
    }

    private fun dlP(children: String): String {
        return "<DL><p>\n$children</DL><p>\n"
    }

    private fun dtH3(folderName: String): String {
        return "<DT><H3>$folderName</H3>\n"
    }

    private fun dtA(linkTitle: String, link: String): String {
        return "<DT><A HREF=\"$link\">$linkTitle</A>\n"
    }


    private suspend fun foldersSectionInHtml(
        parentFolderId: Long?, forArchiveFolders: Boolean
    ): String {
        var foldersSection = ""
        if (parentFolderId == null) {
            if (forArchiveFolders) {
                foldersRepo.getAllArchiveFoldersV10AsList()
            } else {
                foldersRepo.getAllRootFoldersList()
            }
        } else {
            foldersRepo.getChildFoldersOfThisParentIDAsList(parentFolderId)
        }.forEach { childFolder ->
            val currentFolderDTH3 = dtH3(childFolder.folderName)
            var folderLinksDTA = ""
            linksRepo.getLinksOfThisFolderAsList(childFolder.id).forEach { filteredLink ->
                folderLinksDTA += dtA(linkTitle = filteredLink.title, link = filteredLink.webURL)
            }
            linkoraLog("folder in ${childFolder.id}")
            val nestedFolderHTML = foldersSectionInHtml(childFolder.id, forArchiveFolders)
            foldersSection += currentFolderDTH3 + dlP(folderLinksDTA + nestedFolderHTML)
            if (childFolder.parentFolderID == null) {
                if (forArchiveFolders) {
                    ++ExportRequestInfo.currentIterationOfArchivedFoldersAndItsLinks.intValue
                } else {
                    ++ExportRequestInfo.currentIterationOfRegularFoldersAndItsLinks.intValue
                }
            }
        }
        return foldersSection
    }
}