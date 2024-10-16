package com.sakethh.linkora.data.models

import com.sakethh.linkora.data.local.ArchivedFolders
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.RecentlyVisited
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