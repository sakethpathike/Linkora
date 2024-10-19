package com.sakethh.linkora.data.models

import com.sakethh.linkora.data.local.ArchivedFolders
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.Panel
import com.sakethh.linkora.data.local.PanelFolder
import com.sakethh.linkora.data.local.RecentlyVisited
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Export")
data class ExportSchema(
    @SerialName("appVersion")
    // 11 adds panels
    val schemaVersion: Int = 11,
    @SerialName("savedLinks")
    val linksTable: List<LinksTable>,
    @SerialName("importantLinks")
    val importantLinksTable: List<ImportantLinks>,
    @SerialName("folders")
    val foldersTable: List<FoldersTable>,
    @SerialName("archivedLinks")
    val archivedLinksTable: List<ArchivedLinks>,
    @SerialName("archivedFolders")
    val archivedFoldersTable: List<ArchivedFolders>,
    @SerialName("historyLinks")
    val historyLinksTable: List<RecentlyVisited>,
    val panels: List<Panel> = emptyList(),
    val panelFolders: List<PanelFolder> = emptyList(),
)