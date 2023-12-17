package com.sakethh.linkora.localDB.dao._import

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Transaction
import com.sakethh.linkora.localDB.dto.ArchivedFolders
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.localDB.dto.RecentlyVisited

@Dao
interface ImportDao {
    @Transaction
    @Insert
    suspend fun addAllLinks(linksTable: List<LinksTable>)

    @Transaction
    @Insert
    suspend fun addAllImportantLinks(importantLinks: List<ImportantLinks>)

    @Transaction
    @Insert
    suspend fun addAllArchivedLinks(archivedLinks: List<ArchivedLinks>)

    @Transaction
    @Insert
    suspend fun addAllHistoryLinks(historyLinks: List<RecentlyVisited>)

    @Transaction
    @Insert
    suspend fun addAllRegularFolders(foldersData: List<FoldersTable>)

    @Transaction
    @Insert
    suspend fun addAllArchivedFolders(foldersData: List<ArchivedFolders>)
}