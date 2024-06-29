package com.sakethh.linkora.data.localDB.dao.searching

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.data.localDB.models.ArchivedLinks
import com.sakethh.linkora.data.localDB.models.FoldersTable
import com.sakethh.linkora.data.localDB.models.ImportantLinks
import com.sakethh.linkora.data.localDB.models.LinksTable
import com.sakethh.linkora.data.localDB.models.RecentlyVisited
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    @Query("SELECT * FROM folders_table WHERE isFolderArchived = 0 AND LOWER(folderName) LIKE '%' || LOWER(:query) || '%' AND (LOWER(folderName) <> LOWER(:query) OR LOWER(folderName) = LOWER(:query)) ORDER BY CASE WHEN LOWER(folderName) = LOWER(:query) THEN 1 WHEN LOWER(folderName) LIKE LOWER(:query) || '%' THEN 2 WHEN LOWER(folderName) LIKE '%' || LOWER(:query) || '%' THEN 3 ELSE 4 END;\n")
    fun getUnArchivedFolders(query: String): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table WHERE isFolderArchived = 1 AND LOWER(folderName) LIKE '%' || LOWER(:query) || '%' AND (LOWER(folderName) <> LOWER(:query) OR LOWER(folderName) = LOWER(:query)) ORDER BY CASE WHEN LOWER(folderName) = LOWER(:query) THEN 1 WHEN LOWER(folderName) LIKE LOWER(:query) || '%' THEN 2 WHEN LOWER(folderName) LIKE '%' || LOWER(:query) || '%' THEN 3 ELSE 4 END;\n")
    fun getArchivedFolders(query: String): Flow<List<FoldersTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders = 1 AND LOWER(title) LIKE '%' || LOWER(:query) || '%' AND (LOWER(title) <> LOWER(:query) OR LOWER(title) = LOWER(:query)) ORDER BY CASE WHEN LOWER(title) = LOWER(:query) THEN 1 WHEN LOWER(title) LIKE LOWER(:query) || '%' THEN 2 WHEN LOWER(title) LIKE '%' || LOWER(:query) || '%' THEN 3 ELSE 4 END;\n")
    fun getLinksFromFolders(query: String): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithSavedLinks = 1 AND LOWER(title) LIKE '%' || LOWER(:query) || '%' AND (LOWER(title) <> LOWER(:query) OR LOWER(title) = LOWER(:query)) ORDER BY CASE WHEN LOWER(title) = LOWER(:query) THEN 1 WHEN LOWER(title) LIKE LOWER(:query) || '%' THEN 2 WHEN LOWER(title) LIKE '%' || LOWER(:query) || '%' THEN 3 ELSE 4 END;\n")
    fun getSavedLinks(query: String): Flow<List<LinksTable>>

    @Query("SELECT * FROM important_links_table WHERE LOWER(title) LIKE '%' || LOWER(:query) || '%' AND (LOWER(title) <> LOWER(:query) OR LOWER(title) = LOWER(:query)) ORDER BY CASE WHEN LOWER(title) = LOWER(:query) THEN 1 WHEN LOWER(title) LIKE LOWER(:query) || '%' THEN 2 WHEN LOWER(title) LIKE '%' || LOWER(:query) || '%' THEN 3 ELSE 4 END;\n")
    fun getFromImportantLinks(query: String): Flow<List<ImportantLinks>>

    @Query("SELECT * FROM archived_links_table WHERE LOWER(title) LIKE '%' || LOWER(:query) || '%' AND (LOWER(title) <> LOWER(:query) OR LOWER(title) = LOWER(:query)) ORDER BY CASE WHEN LOWER(title) = LOWER(:query) THEN 1 WHEN LOWER(title) LIKE LOWER(:query) || '%' THEN 2 WHEN LOWER(title) LIKE '%' || LOWER(:query) || '%' THEN 3 ELSE 4 END;\n")
    fun getArchiveLinks(query: String): Flow<List<ArchivedLinks>>

    @Query("SELECT * FROM recently_visited_table WHERE LOWER(title) LIKE '%' || LOWER(:query) || '%' AND (LOWER(title) <> LOWER(:query) OR LOWER(title) = LOWER(:query)) ORDER BY CASE WHEN LOWER(title) = LOWER(:query) THEN 1 WHEN LOWER(title) LIKE LOWER(:query) || '%' THEN 2 WHEN LOWER(title) LIKE '%' || LOWER(:query) || '%' THEN 3 ELSE 4 END;\n")
    fun getHistoryLinks(query: String): Flow<List<RecentlyVisited>>
}