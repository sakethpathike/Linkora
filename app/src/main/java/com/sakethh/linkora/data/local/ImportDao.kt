package com.sakethh.linkora.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ImportDao {
    @Insert
    suspend fun addAllLinks(linksTable: List<LinksTable>)

    @Insert
    suspend fun addAllImportantLinks(importantLinks: List<ImportantLinks>)

    @Insert
    suspend fun addAllArchivedLinks(archivedLinks: List<ArchivedLinks>)

    
    @Insert
    suspend fun addAllHistoryLinks(historyLinks: List<RecentlyVisited>)

    
    @Insert
    suspend fun addAllRegularFolders(foldersData: List<FoldersTable>)

    
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