package com.sakethh.linkora.data.local.search

import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.RecentlyVisited
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchImpl @Inject constructor(private val localDatabase: LocalDatabase) : SearchRepo {
    override fun getUnArchivedFolders(query: String): Flow<List<FoldersTable>> {
        return localDatabase.searchDao().getUnArchivedFolders(query)
    }

    override fun getArchivedFolders(query: String): Flow<List<FoldersTable>> {
        return localDatabase.searchDao().getArchivedFolders(query)
    }

    override fun getLinksFromFolders(query: String): Flow<List<LinksTable>> {
        return localDatabase.searchDao().getLinksFromFolders(query)
    }

    override fun getSavedLinks(query: String): Flow<List<LinksTable>> {
        return localDatabase.searchDao().getSavedLinks(query)
    }

    override fun getFromImportantLinks(query: String): Flow<List<ImportantLinks>> {
        return localDatabase.searchDao().getFromImportantLinks(query)
    }

    override fun getArchiveLinks(query: String): Flow<List<ArchivedLinks>> {
        return localDatabase.searchDao().getArchiveLinks(query)
    }

    override fun getHistoryLinks(query: String): Flow<List<RecentlyVisited>> {
        return localDatabase.searchDao().getHistoryLinks(query)
    }
}