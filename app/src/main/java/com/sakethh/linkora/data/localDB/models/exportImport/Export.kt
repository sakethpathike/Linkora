package com.sakethh.linkora.data.localDB.models.exportImport

import com.sakethh.linkora.data.localDB.models.ArchivedFolders
import com.sakethh.linkora.data.localDB.models.ArchivedLinks
import com.sakethh.linkora.data.localDB.models.FoldersTable
import com.sakethh.linkora.data.localDB.models.ImportantLinks
import com.sakethh.linkora.data.localDB.models.LinksTable
import com.sakethh.linkora.data.localDB.models.RecentlyVisited
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Export(
    @SerialName("appVersion")
    val schemaVersion: Int,
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