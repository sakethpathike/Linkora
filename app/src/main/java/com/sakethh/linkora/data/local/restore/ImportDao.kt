package com.sakethh.linkora.data.local.restore

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sakethh.linkora.data.local.ArchivedFolders
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.Panel
import com.sakethh.linkora.data.local.PanelFolder
import com.sakethh.linkora.data.local.RecentlyVisited

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

    @Insert
    suspend fun addAllPanels(panels: List<Panel>)

    @Insert
    suspend fun addAllPanelFolders(panelFolders: List<PanelFolder>)


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

    @Query("SELECT panelId FROM panel WHERE panelId = (SELECT MAX(panelId) FROM panel)")
    suspend fun getLatestPanelTableID(): Long

    @Query("SELECT id FROM panel_folder WHERE id = (SELECT MAX(id) FROM panel_folder)")
    suspend fun getLatestPanelFoldersTableID(): Long
}