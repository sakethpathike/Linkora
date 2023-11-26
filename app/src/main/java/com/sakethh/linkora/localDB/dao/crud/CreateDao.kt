package com.sakethh.linkora.localDB.dao.crud

import androidx.room.Dao
import androidx.room.Insert
import com.sakethh.linkora.localDB.dto.ArchivedFolders
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.localDB.dto.RecentlyVisited

@Dao
interface CreateDao {
    @Insert
    suspend fun addANewLinkToSavedLinksOrInFolders(linksTable: LinksTable)

    @Insert
    suspend fun addListOfDataInLinksTable(list: List<LinksTable>)

    @Insert
    suspend fun addANewFolder(foldersTable: FoldersTable)

    @Insert
    suspend fun addANewLinkToImpLinks(importantLinks: ImportantLinks)

    @Insert
    suspend fun addANewLinkToArchiveLink(archivedLinks: ArchivedLinks)

    @Insert
    suspend fun addANewArchiveFolder(archivedFolders: ArchivedFolders)

    @Insert
    suspend fun addANewLinkInRecentlyVisited(recentlyVisited: RecentlyVisited)
}