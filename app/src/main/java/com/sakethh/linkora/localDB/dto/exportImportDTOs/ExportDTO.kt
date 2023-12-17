package com.sakethh.linkora.localDB.dto.exportImportDTOs

import com.sakethh.linkora.localDB.dto.ArchivedFolders
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.localDB.dto.RecentlyVisited
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExportDTO(
    val appVersion: Int,
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
)