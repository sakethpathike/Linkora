package com.sakethh.linkora.data.local.search

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.RecentlyVisited
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    @Query(
        "SELECT * FROM folders_table \n" +
                "        WHERE isFolderArchived = 0 \n" +
                "        AND (\n" +
                "            LOWER(folderName) LIKE '%' || LOWER(:query) || '%' \n" +
                "            OR LOWER(infoForSaving) LIKE '%' || LOWER(:query) || '%'\n" +
                "        ) \n" +
                "        AND (LOWER(folderName) <> LOWER(:query) OR LOWER(folderName) = LOWER(:query)) \n" +
                "        ORDER BY \n" +
                "            CASE \n" +
                "                WHEN LOWER(folderName) = LOWER(:query) THEN 1 \n" +
                "                WHEN LOWER(folderName) LIKE LOWER(:query) || '%' THEN 2 \n" +
                "                WHEN LOWER(folderName) LIKE '%' || LOWER(:query) || '%' THEN 3 \n" +
                "                WHEN LOWER(infoForSaving) LIKE LOWER(:query) || '%' THEN 4 \n" +
                "                WHEN LOWER(infoForSaving) LIKE '%' || LOWER(:query) || '%' THEN 5 \n" +
                "                ELSE 6 \n" +
                "            END;\n"
    )
    fun getUnArchivedFolders(query: String): Flow<List<FoldersTable>>

    @Query(
        "SELECT * FROM folders_table \n" +
                "WHERE isFolderArchived = 1 \n" +
                "AND (\n" +
                "    LOWER(folderName) LIKE '%' || LOWER(:query) || '%' \n" +
                "    OR LOWER(infoForSaving) LIKE '%' || LOWER(:query) || '%'\n" +
                ") \n" +
                "AND (LOWER(folderName) <> LOWER(:query) OR LOWER(folderName) = LOWER(:query)) \n" +
                "ORDER BY \n" +
                "    CASE \n" +
                "        WHEN LOWER(folderName) = LOWER(:query) THEN 1 \n" +
                "        WHEN LOWER(folderName) LIKE LOWER(:query) || '%' THEN 2 \n" +
                "        WHEN LOWER(folderName) LIKE '%' || LOWER(:query) || '%' THEN 3 \n" +
                "        WHEN LOWER(infoForSaving) LIKE LOWER(:query) || '%' THEN 4 \n" +
                "        WHEN LOWER(infoForSaving) LIKE '%' || LOWER(:query) || '%' THEN 5 \n" +
                "        ELSE 6 \n" +
                "    END;\n"
    )
    fun getArchivedFolders(query: String): Flow<List<FoldersTable>>

    @Query(
        "SELECT * FROM links_table \n" +
                "WHERE isLinkedWithFolders = 1 \n" +
                "AND (\n" +
                "    LOWER(title) LIKE '%' || LOWER(:query) || '%' \n" +
                "    OR LOWER(infoForSaving) LIKE '%' || LOWER(:query) || '%'\n" +
                ") \n" +
                "AND (LOWER(title) <> LOWER(:query) OR LOWER(title) = LOWER(:query)) \n" +
                "ORDER BY \n" +
                "    CASE \n" +
                "        WHEN LOWER(title) = LOWER(:query) THEN 1 \n" +
                "        WHEN LOWER(title) LIKE LOWER(:query) || '%' THEN 2 \n" +
                "        WHEN LOWER(title) LIKE '%' || LOWER(:query) || '%' THEN 3 \n" +
                "        WHEN LOWER(infoForSaving) LIKE LOWER(:query) || '%' THEN 4 \n" +
                "        WHEN LOWER(infoForSaving) LIKE '%' || LOWER(:query) || '%' THEN 5 \n" +
                "        ELSE 6 \n" +
                "    END;\n"
    )
    fun getLinksFromFolders(query: String): Flow<List<LinksTable>>

    @Query(
        "SELECT * FROM links_table \n" +
                "WHERE isLinkedWithSavedLinks = 1 \n" +
                "AND (\n" +
                "    LOWER(title) LIKE '%' || LOWER(:query) || '%' \n" +
                "    OR LOWER(infoForSaving) LIKE '%' || LOWER(:query) || '%'\n" +
                ") \n" +
                "AND (LOWER(title) <> LOWER(:query) OR LOWER(title) = LOWER(:query)) \n" +
                "ORDER BY \n" +
                "    CASE \n" +
                "        WHEN LOWER(title) = LOWER(:query) THEN 1 \n" +
                "        WHEN LOWER(title) LIKE LOWER(:query) || '%' THEN 2 \n" +
                "        WHEN LOWER(title) LIKE '%' || LOWER(:query) || '%' THEN 3 \n" +
                "        WHEN LOWER(infoForSaving) LIKE LOWER(:query) || '%' THEN 4 \n" +
                "        WHEN LOWER(infoForSaving) LIKE '%' || LOWER(:query) || '%' THEN 5 \n" +
                "        ELSE 6 \n" +
                "    END;\n"
    )
    fun getSavedLinks(query: String): Flow<List<LinksTable>>

    @Query(
        "SELECT * FROM important_links_table \n" +
                "WHERE \n" +
                "    LOWER(title) LIKE '%' || LOWER(:query) || '%' \n" +
                "    OR LOWER(infoForSaving) LIKE '%' || LOWER(:query) || '%'\n" +
                "AND (LOWER(title) <> LOWER(:query) OR LOWER(title) = LOWER(:query)) \n" +
                "ORDER BY \n" +
                "    CASE \n" +
                "        WHEN LOWER(title) = LOWER(:query) THEN 1 \n" +
                "        WHEN LOWER(title) LIKE LOWER(:query) || '%' THEN 2 \n" +
                "        WHEN LOWER(title) LIKE '%' || LOWER(:query) || '%' THEN 3 \n" +
                "        WHEN LOWER(infoForSaving) LIKE LOWER(:query) || '%' THEN 4 \n" +
                "        WHEN LOWER(infoForSaving) LIKE '%' || LOWER(:query) || '%' THEN 5 \n" +
                "        ELSE 6 \n" +
                "    END;\n"
    )
    fun getFromImportantLinks(query: String): Flow<List<ImportantLinks>>

    @Query(
        "SELECT * FROM archived_links_table \n" +
                "WHERE \n" +
                "    LOWER(title) LIKE '%' || LOWER(:query) || '%' \n" +
                "    OR LOWER(infoForSaving) LIKE '%' || LOWER(:query) || '%'\n" +
                "AND (LOWER(title) <> LOWER(:query) OR LOWER(title) = LOWER(:query)) \n" +
                "ORDER BY \n" +
                "    CASE \n" +
                "        WHEN LOWER(title) = LOWER(:query) THEN 1 \n" +
                "        WHEN LOWER(title) LIKE LOWER(:query) || '%' THEN 2 \n" +
                "        WHEN LOWER(title) LIKE '%' || LOWER(:query) || '%' THEN 3 \n" +
                "        WHEN LOWER(infoForSaving) LIKE LOWER(:query) || '%' THEN 4 \n" +
                "        WHEN LOWER(infoForSaving) LIKE '%' || LOWER(:query) || '%' THEN 5 \n" +
                "        ELSE 6 \n" +
                "    END;\n"
    )
    fun getArchiveLinks(query: String): Flow<List<ArchivedLinks>>

    @Query(
        "SELECT * FROM recently_visited_table \n" +
                "WHERE \n" +
                "    LOWER(title) LIKE '%' || LOWER(:query) || '%' \n" +
                "    OR LOWER(infoForSaving) LIKE '%' || LOWER(:query) || '%'\n" +
                "AND (LOWER(title) <> LOWER(:query) OR LOWER(title) = LOWER(:query)) \n" +
                "ORDER BY \n" +
                "    CASE \n" +
                "        WHEN LOWER(title) = LOWER(:query) THEN 1 \n" +
                "        WHEN LOWER(title) LIKE LOWER(:query) || '%' THEN 2 \n" +
                "        WHEN LOWER(title) LIKE '%' || LOWER(:query) || '%' THEN 3 \n" +
                "        WHEN LOWER(infoForSaving) LIKE LOWER(:query) || '%' THEN 4 \n" +
                "        WHEN LOWER(infoForSaving) LIKE '%' || LOWER(:query) || '%' THEN 5 \n" +
                "        ELSE 6 \n" +
                "    END;\n"
    )
    fun getHistoryLinks(query: String): Flow<List<RecentlyVisited>>
}