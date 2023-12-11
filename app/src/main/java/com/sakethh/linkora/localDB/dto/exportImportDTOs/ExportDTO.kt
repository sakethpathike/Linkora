package com.sakethh.linkora.localDB.dto.exportImportDTOs

import com.sakethh.linkora.localDB.dto.ArchivedFolders
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.localDB.dto.RecentlyVisited
import kotlinx.serialization.Serializable

@Serializable
data class ExportDTO(
    val appVersion: Int,
    val savedLinks: List<LinksTable>,
    val importantLinks: List<ImportantLinks>,
    val folders: List<FoldersTable>,
    val archivedLinks: List<ArchivedLinks>,
    val archivedFolders: List<ArchivedFolders>,
    val historyLinks: List<RecentlyVisited>,
)