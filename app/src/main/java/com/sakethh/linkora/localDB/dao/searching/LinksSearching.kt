package com.sakethh.linkora.localDB.dao.searching

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.localDB.ImportantLinks
import com.sakethh.linkora.localDB.LinksTable
import kotlinx.coroutines.flow.Flow

@Dao
interface LinksSearching {
    @Query(
        "SELECT *\n" +
                "                FROM links_table\n" +
                "                WHERE isLinkedWithArchivedFolder = 0 AND title LIKE '%' || :query || '%'\n" +
                "        AND title <> :query \n" +
                "                ORDER BY CASE\n" +
                "        WHEN title LIKE :query COLLATE NOCASE THEN 1\n" +
                "        WHEN title LIKE :query || '%' COLLATE NOCASE THEN 2\n" +
                "        WHEN title LIKE '%' || :query || '%' COLLATE NOCASE THEN 3\n" +
                "        ELSE 4\n" +
                "        END;"
    )
    fun getFromLinksTableExcludingArchive(query: String): Flow<List<LinksTable>>

    @Query(
        "SELECT *\n" +
                "                FROM links_table\n" +
                "                WHERE title LIKE '%' || :query || '%'\n" +
                "        AND title <> :query \n" +
                "                ORDER BY CASE\n" +
                "        WHEN title LIKE :query COLLATE NOCASE THEN 1\n" +
                "        WHEN title LIKE :query || '%' COLLATE NOCASE THEN 2\n" +
                "        WHEN title LIKE '%' || :query || '%' COLLATE NOCASE THEN 3\n" +
                "        ELSE 4\n" +
                "        END;"
    )
    fun getFromLinksTableIncludingArchive(query: String): Flow<List<LinksTable>>

    @Query(
        "SELECT *\n" +
                "                FROM important_links_table\n" +
                "                WHERE title LIKE '%' || :query || '%'\n" +
                "        AND title <> :query \n" +
                "                ORDER BY CASE\n" +
                "        WHEN title LIKE :query COLLATE NOCASE THEN 1\n" +
                "        WHEN title LIKE :query || '%' COLLATE NOCASE THEN 2\n" +
                "        WHEN title LIKE '%' || :query || '%' COLLATE NOCASE THEN 3\n" +
                "        ELSE 4\n" +
                "        END;"
    )
    fun getFromImportantLinks(query: String): Flow<List<ImportantLinks>>
}