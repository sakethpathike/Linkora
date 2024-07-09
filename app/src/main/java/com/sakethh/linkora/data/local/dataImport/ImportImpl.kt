package com.sakethh.linkora.data.local.dataImport

import com.sakethh.linkora.data.local.ArchivedFolders
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.RecentlyVisited
import javax.inject.Inject

class ImportImpl @Inject constructor(private val localDatabase: LocalDatabase) : ImportRepo {
    override suspend fun addAllLinks(linksTable: List<LinksTable>) {
        return localDatabase.importDao().addAllLinks(linksTable)
    }

    override suspend fun addAllImportantLinks(importantLinks: List<ImportantLinks>) {
        return localDatabase.importDao().addAllImportantLinks(importantLinks)
    }

    override suspend fun addAllArchivedLinks(archivedLinks: List<ArchivedLinks>) {
        return localDatabase.importDao().addAllArchivedLinks(archivedLinks)
    }

    override suspend fun addAllHistoryLinks(historyLinks: List<RecentlyVisited>) {
        return localDatabase.importDao().addAllHistoryLinks(historyLinks)
    }

    override suspend fun addAllRegularFolders(foldersData: List<FoldersTable>) {
        return localDatabase.importDao().addAllRegularFolders(foldersData)
    }

    override suspend fun addAllArchivedFolders(foldersData: List<ArchivedFolders>) {
        return localDatabase.importDao().addAllArchivedFolders(foldersData)
    }

    override suspend fun getLatestLinksTableID(): Long {
        return localDatabase.importDao().getLatestLinksTableID()
    }

    override suspend fun getLatestFoldersTableID(): Long {
        return localDatabase.importDao().getLatestFoldersTableID()
    }

    override suspend fun getLatestArchivedLinksTableID(): Long {
        return localDatabase.importDao().getLatestArchivedLinksTableID()
    }

    override suspend fun getLatestArchivedFoldersTableID(): Long {
        return localDatabase.importDao().getLatestArchivedFoldersTableID()
    }

    override suspend fun getLatestImpLinksTableID(): Long {
        return localDatabase.importDao().getLatestImpLinksTableID()
    }

    override suspend fun getLatestRecentlyVisitedTableID(): Long {
        return localDatabase.importDao().getLatestRecentlyVisitedTableID()
    }
}