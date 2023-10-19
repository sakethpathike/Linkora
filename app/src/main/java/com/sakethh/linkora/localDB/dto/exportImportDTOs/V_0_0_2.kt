package com.sakethh.linkora.localDB.dto.exportImportDTOs

import com.sakethh.linkora.localDB.dto.ArchivedFolders
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.localDB.dto.RecentlyVisited

data class V_0_0_2(
    val appVersion: Int,
    val savedLinks: List<LinksTable>,
    val importantLinks: List<ImportantLinks>,
    val folders: List<FoldersTable>,
    val linksInFolders: List<LinksTable>,
    val archivedLinks: List<ArchivedLinks>,
    val archivedFolders: List<ArchivedFolders>,
    val archivedLinksInFolders: List<LinksTable>,
    val historyLinks: List<RecentlyVisited>,
)