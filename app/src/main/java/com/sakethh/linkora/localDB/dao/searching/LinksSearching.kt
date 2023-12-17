package com.sakethh.linkora.localDB.dao.searching

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable

@Dao
interface LinksSearching {

    @Query(
        "SELECT *\n" +
                "FROM links_table\n" +
                "WHERE LOWER(title) LIKE '%' || LOWER(:query) || '%'\n" +
                "    AND (\n" +
                "        LOWER(title) <> LOWER(:query)\n" +
                "        OR LOWER(title) = LOWER(:query)\n" +
                "    )\n" +
                "ORDER BY CASE\n" +
                "    WHEN LOWER(title) = LOWER(:query) THEN 1\n" +
                "    WHEN LOWER(title) LIKE LOWER(:query) || '%' THEN 2\n" +
                "    WHEN LOWER(title) LIKE '%' || LOWER(:query) || '%' THEN 3\n" +
                "    ELSE 4\n" +
                "END;\n"
    )
    suspend fun getFromLinksTable(query: String): List<LinksTable>

    @Query(
        "SELECT *\n" +
                "FROM important_links_table\n" +
                "WHERE LOWER(title) LIKE '%' || LOWER(:query) || '%'\n" +
                "    AND (\n" +
                "        LOWER(title) <> LOWER(:query)\n" +
                "        OR LOWER(title) = LOWER(:query)\n" +
                "    )\n" +
                "ORDER BY CASE\n" +
                "    WHEN LOWER(title) = LOWER(:query) THEN 1\n" +
                "    WHEN LOWER(title) LIKE LOWER(:query) || '%' THEN 2\n" +
                "    WHEN LOWER(title) LIKE '%' || LOWER(:query) || '%' THEN 3\n" +
                "    ELSE 4\n" +
                "END;\n"
    )
    suspend fun getFromImportantLinks(query: String): List<ImportantLinks>

    @Query(
        "SELECT *\n" +
                "FROM archived_links_table\n" +
                "WHERE LOWER(title) LIKE '%' || LOWER(:query) || '%'\n" +
                "    AND (\n" +
                "        LOWER(title) <> LOWER(:query)\n" +
                "        OR LOWER(title) = LOWER(:query)\n" +
                "    )\n" +
                "ORDER BY CASE\n" +
                "    WHEN LOWER(title) = LOWER(:query) THEN 1\n" +
                "    WHEN LOWER(title) LIKE LOWER(:query) || '%' THEN 2\n" +
                "    WHEN LOWER(title) LIKE '%' || LOWER(:query) || '%' THEN 3\n" +
                "    ELSE 4\n" +
                "END;\n"
    )
    suspend fun getFromArchiveLinks(query: String): List<ArchivedLinks>
}