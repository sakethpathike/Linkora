package com.sakethh.linkora.localDB.dao._import

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
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

    @Query("SELECT id FROM links_table WHERE id = (SELECT MAX(id) FROM links_table)")
    suspend fun getLatestLinksTableID(): Long

    @Query("SELECT id FROM folders_table WHERE id = (SELECT MAX(id) FROM folders_table)")
    suspend fun getLatestFoldersTableID(): Long

    @Query("SELECT id FROM archived_links_table WHERE id = (SELECT MAX(id) FROM archived_links_table)")
    suspend fun getLatestArchivedLinksTableID(): Long

    @Query("SELECT id FROM archived_folders_table WHERE id = (SELECT MAX(id) FROM archived_folders_table)")
    suspend fun getLatestArchivedFoldersTableID(): Long

    @Query("SELECT id FROM important_links_table WHERE id = (SELECT MAX(id) FROM important_links_table)")
    suspend fun getLatestImpLinksTableID(): Long

    @Query("SELECT id FROM recently_visited_table WHERE id = (SELECT MAX(id) FROM recently_visited_table)")
    suspend fun getLatestRecentlyVisitedTableID(): Long
}