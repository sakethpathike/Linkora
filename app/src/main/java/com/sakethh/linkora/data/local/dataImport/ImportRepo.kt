package com.sakethh.linkora.data.local.dataImport

import com.sakethh.linkora.data.local.ArchivedFolders
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.RecentlyVisited

interface ImportRepo {
    suspend fun addAllLinks(linksTable: List<LinksTable>)

    suspend fun addAllImportantLinks(importantLinks: List<ImportantLinks>)

    suspend fun addAllArchivedLinks(archivedLinks: List<ArchivedLinks>)


    suspend fun addAllHistoryLinks(historyLinks: List<RecentlyVisited>)


    suspend fun addAllRegularFolders(foldersData: List<FoldersTable>)


    suspend fun addAllArchivedFolders(foldersData: List<ArchivedFolders>)

    suspend fun getLatestLinksTableID(): Long

    suspend fun getLatestFoldersTableID(): Long

    suspend fun getLatestArchivedLinksTableID(): Long

    suspend fun getLatestArchivedFoldersTableID(): Long

    suspend fun getLatestImpLinksTableID(): Long

    suspend fun getLatestRecentlyVisitedTableID(): Long
}