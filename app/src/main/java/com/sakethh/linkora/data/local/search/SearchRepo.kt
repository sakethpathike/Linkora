package com.sakethh.linkora.data.local.search

import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.RecentlyVisited
import kotlinx.coroutines.flow.Flow

interface SearchRepo {
    fun getUnArchivedFolders(query: String): Flow<List<FoldersTable>>

    fun getArchivedFolders(query: String): Flow<List<FoldersTable>>

    fun getLinksFromFolders(query: String): Flow<List<LinksTable>>

    fun getSavedLinks(query: String): Flow<List<LinksTable>>

    fun getFromImportantLinks(query: String): Flow<List<ImportantLinks>>

    fun getArchiveLinks(query: String): Flow<List<ArchivedLinks>>

    fun getHistoryLinks(query: String): Flow<List<RecentlyVisited>>
}