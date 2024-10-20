package com.sakethh.linkora.data.local.links

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.RecentlyVisited
import kotlinx.coroutines.flow.Flow

@Dao
interface LinksDao {
    @Insert
    suspend fun addANewLinkToSavedLinksOrInFolders(linksTable: LinksTable)

    @Insert
    suspend fun addListOfDataInLinksTable(list: List<LinksTable>)

    @Query("UPDATE links_table SET isLinkedWithSavedLinks = 1, isLinkedWithFolders = 0, keyOfLinkedFolderV10 = NULL WHERE id =:linkID")
    suspend fun markThisLinkFromLinksTableAsSavedLink(linkID: Long)

    @Insert
    suspend fun addALinkInLinksTable(linksTable: LinksTable)

    @Query("UPDATE links_table SET isLinkedWithSavedLinks = 0, isLinkedWithFolders = 1, keyOfLinkedFolderV10 = :targetFolderId WHERE id =:linkID")
    suspend fun markThisLinkFromLinksTableAsFolderLink(linkID: Long, targetFolderId: Long)

    @Query(
        "INSERT INTO links_table (title, webURL, baseURL, imgURL, infoForSaving, " +
                "                        isLinkedWithSavedLinks, isLinkedWithFolders, " +
                "                        isLinkedWithImpFolder, keyOfImpLinkedFolder, " +
                "                        isLinkedWithArchivedFolder, keyOfLinkedFolderV10) " +
                "SELECT title, webURL, baseURL, imgURL, infoForSaving, " +
                "       isLinkedWithSavedLinks, isLinkedWithFolders, " +
                "       isLinkedWithImpFolder, keyOfImpLinkedFolder, " +
                "       isLinkedWithArchivedFolder, :newIdOfLinkedFolder " +
                "FROM links_table WHERE keyOfLinkedFolderV10 = :currentIdOfLinkedFolder"
    )
    suspend fun duplicateFolderBasedLinks(currentIdOfLinkedFolder: Long, newIdOfLinkedFolder: Long)

    @Query("UPDATE archived_links_table SET infoForSaving = \"\" WHERE webURL = :webURL")
    suspend fun deleteANoteFromArchiveLinks(webURL: String)

    @Query("UPDATE archived_links_table SET infoForSaving = \"\" WHERE id = :linkID")
    suspend fun deleteANoteFromArchiveLinks(linkID: Long)

    @Query("UPDATE links_table SET infoForSaving = \"\" WHERE webURL = :webURL AND keyOfArchiveLinkedFolderV10 = :folderID")
    suspend fun deleteALinkNoteFromArchiveBasedFolderLinksV10(
        webURL: String,
        folderID: Long,
    )

    @Query("UPDATE links_table SET infoForSaving = \"\" WHERE webURL = :webURL AND keyOfArchiveLinkedFolder = :folderName")
    suspend fun deleteALinkNoteFromArchiveBasedFolderLinksV9(
        webURL: String,
        folderName: String,
    )

    @Query("UPDATE important_links_table SET infoForSaving = \"\" WHERE webURL = :webURL")
    suspend fun deleteANoteFromImportantLinks(webURL: String)

    @Query("UPDATE important_links_table SET infoForSaving = \"\" WHERE id = :linkID")
    suspend fun deleteANoteFromImportantLinks(linkID: Long)

    @Query("UPDATE links_table SET infoForSaving = \"\" WHERE id = :linkID")
    suspend fun deleteANoteFromLinksTable(linkID: Long)


    @Query("UPDATE recently_visited_table SET infoForSaving = \"\" WHERE webURL = :webURL")
    suspend fun deleteANoteFromRecentlyVisited(webURL: String)


    @Query("UPDATE recently_visited_table SET infoForSaving = \"\" WHERE id = :linkID")
    suspend fun deleteANoteFromRecentlyVisited(linkID: Long)


    @Query("UPDATE links_table SET infoForSaving = \"\" WHERE webURL = :webURL AND isLinkedWithSavedLinks=1")
    suspend fun deleteALinkInfoFromSavedLinks(webURL: String)

    @Query("UPDATE links_table SET infoForSaving = \"\" WHERE id = :linkID AND isLinkedWithFolders=1")
    suspend fun deleteALinkInfoOfFolders(linkID: Long)

    @Query("DELETE from links_table WHERE webURL = :webURL AND isLinkedWithSavedLinks = 1 AND isLinkedWithArchivedFolder=0 AND isLinkedWithArchivedFolder=0")
    suspend fun deleteALinkFromSavedLinksBasedOnURL(webURL: String)


    @Query("DELETE from important_links_table WHERE id = :linkID")
    suspend fun deleteALinkFromImpLinks(linkID: Long)

    @Query("DELETE from important_links_table WHERE webURL=:webURL")
    suspend fun deleteALinkFromImpLinksBasedOnURL(webURL: String)

    @Query("DELETE from archived_links_table WHERE webURL = :webURL")
    suspend fun deleteALinkFromArchiveLinksV9(webURL: String)

    @Query("DELETE from archived_links_table WHERE id = :id")
    suspend fun deleteALinkFromArchiveLinks(id: Long)

    @Query("DELETE from links_table WHERE webURL = :webURL AND keyOfArchiveLinkedFolderV10 = :archiveFolderID AND isLinkedWithArchivedFolder=1")
    suspend fun deleteALinkFromArchiveFolderBasedLinksV10(webURL: String, archiveFolderID: Long)

    @Query("DELETE from links_table WHERE webURL = :webURL AND keyOfArchiveLinkedFolder = :folderName AND isLinkedWithArchivedFolder=1")
    suspend fun deleteALinkFromArchiveFolderBasedLinksV9(webURL: String, folderName: String)

    @Query("DELETE from links_table WHERE id = :linkID")
    suspend fun deleteALinkFromLinksTable(linkID: Long)


    @Query("DELETE from links_table WHERE webURL = :webURL AND keyOfLinkedFolderV10 = :folderID AND isLinkedWithFolders=1 AND isLinkedWithArchivedFolder=0 AND isLinkedWithSavedLinks=0")
    suspend fun deleteALinkFromSpecificFolderV10(webURL: String, folderID: Long)

    @Query("DELETE from links_table WHERE webURL = :webURL AND keyOfLinkedFolder = :folderName AND isLinkedWithFolders=1 AND isLinkedWithArchivedFolder=0 AND isLinkedWithSavedLinks=0")
    suspend fun deleteALinkFromSpecificFolderV9(webURL: String, folderName: String)

    @Query("DELETE from recently_visited_table WHERE webURL = :webURL")
    suspend fun deleteARecentlyVisitedLink(webURL: String)

    @Transaction
    @Query("DELETE from recently_visited_table WHERE id = :linkID")
    suspend fun deleteARecentlyVisitedLink(linkID: Long)

    @Query("DELETE from links_table WHERE keyOfLinkedFolderV10 = :folderID")
    suspend fun deleteThisFolderLinksV10(folderID: Long)

    @Query("DELETE from links_table WHERE keyOfLinkedFolderV10 in (:links)")
    suspend fun deleteMultipleLinksFromLinksTable(links: Array<Long>)

    @Query("DELETE from links_table WHERE keyOfLinkedFolder = :folderName")
    suspend fun deleteThisFolderLinksV9(folderName: String)

    @Query("DELETE from links_table WHERE keyOfArchiveLinkedFolder = :folderID")
    suspend fun deleteThisArchiveFolderDataV9(folderID: String)

    @Insert
    suspend fun addANewLinkToImpLinks(importantLinks: ImportantLinks)

    @Insert
    suspend fun addANewLinkToArchiveLink(archivedLinks: ArchivedLinks)

    @Query("SELECT * FROM links_table WHERE isLinkedWithSavedLinks = 1")
    fun getAllSavedLinks(): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithSavedLinks = 1")
    suspend fun getAllSavedLinksAsList(): List<LinksTable>

    @Query("SELECT * FROM links_table")
    suspend fun getAllFromLinksTable(): List<LinksTable>

    @Query("SELECT * FROM recently_visited_table")
    suspend fun getAllRecentlyVisitedLinks(): List<RecentlyVisited>

    @Query("SELECT * FROM important_links_table")
    suspend fun getAllImpLinks(): List<ImportantLinks>

    @Query("SELECT * FROM archived_links_table")
    suspend fun getAllArchiveLinks(): List<ArchivedLinks>


    @Query("SELECT * FROM links_table WHERE isLinkedWithArchivedFolder=1")
    fun getAllArchiveFoldersLinks(): Flow<List<LinksTable>>


    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolderV10=:folderID")
    fun getLinksOfThisFolderV10(folderID: Long): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolderV10=:folderID")
    suspend fun getLinksOfThisFolderAsList(folderID: Long): List<LinksTable>


    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolder=:folderName")
    fun getLinksOfThisFolderV9(folderName: String): Flow<List<LinksTable>>


    @Query("SELECT * FROM links_table WHERE isLinkedWithArchivedFolder=1 AND keyOfArchiveLinkedFolderV10=:folderID")
    fun getThisArchiveFolderLinksV10(folderID: Long): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithArchivedFolder=1 AND keyOfArchiveLinkedFolder=:folderName")
    fun getThisArchiveFolderLinksV9(folderName: String): Flow<List<LinksTable>>

    @Query("SELECT EXISTS(SELECT * FROM important_links_table WHERE webURL = :webURL)")
    suspend fun doesThisExistsInImpLinks(webURL: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM links_table WHERE webURL = :webURL AND isLinkedWithSavedLinks=1)")
    suspend fun doesThisExistsInSavedLinks(webURL: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM links_table WHERE webURL = :webURL AND keyOfLinkedFolderV10=:folderID)")
    suspend fun doesThisLinkExistsInAFolderV10(webURL: String, folderID: Long): Boolean


    @Query("SELECT MAX(id) FROM recently_visited_table")
    suspend fun getLastIDOfHistoryTable(): Long

    @Query("SELECT MAX(id) FROM links_table")
    suspend fun getLastIDOfLinksTable(): Long

    @Query("SELECT MAX(id) FROM important_links_table")
    suspend fun getLastIDOfImpLinksTable(): Long

    @Query("SELECT MAX(id) FROM archived_links_table")
    suspend fun getLastIDOfArchivedLinksTable(): Long

    @Query("SELECT EXISTS(SELECT * FROM links_table WHERE webURL = :webURL AND keyOfLinkedFolder=:folderName)")
    suspend fun doesThisLinkExistsInAFolderV9(webURL: String, folderName: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM archived_links_table WHERE webURL = :webURL)")
    suspend fun doesThisExistsInArchiveLinks(webURL: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM recently_visited_table WHERE webURL = :webURL)")
    suspend fun doesThisExistsInRecentlyVisitedLinks(webURL: String): Boolean


    @Query("SELECT (SELECT COUNT(*) FROM links_table) == 0")
    suspend fun isLinksTableEmpty(): Boolean


    @Query("SELECT (SELECT COUNT(*) FROM archived_links_table) == 0")
    suspend fun isArchivedLinksTableEmpty(): Boolean

    @Query("SELECT (SELECT COUNT(*) FROM archived_folders_table) == 0")
    suspend fun isArchivedFoldersTableEmpty(): Boolean

    @Query("SELECT (SELECT COUNT(*) FROM important_links_table) == 0")
    suspend fun isImpLinksTableEmpty(): Boolean

    @Query("SELECT (SELECT COUNT(*) FROM recently_visited_table) == 0")
    suspend fun isHistoryLinksTableEmpty(): Boolean


    @Query(
        "INSERT INTO archived_links_table (title, webURL, baseURL, imgURL, infoForSaving)\n" +
                "SELECT title, webURL, baseURL, imgURL, infoForSaving\n" +
                "FROM links_table\n" +
                "WHERE id = :id;\n"
    )
    suspend fun copyLinkFromLinksTableToArchiveLinks(id: Long)

    @Query(
        "INSERT INTO archived_links_table (title, webURL, baseURL, imgURL, infoForSaving)\n" +
                "SELECT title, webURL, baseURL, imgURL, infoForSaving\n" +
                "FROM important_links_table\n" +
                "WHERE id = :id;\n"
    )
    suspend fun copyLinkFromImpLinksTableToArchiveLinks(id: Long)

    @Query(
        "INSERT INTO archived_links_table (title, webURL, baseURL, imgURL, infoForSaving)\n" +
                "SELECT title, webURL, baseURL, imgURL, infoForSaving\n" +
                "FROM important_links_table\n" +
                "WHERE webURL = :link;\n"
    )
    suspend fun copyLinkFromImpTableToArchiveLinks(link: String)

    @Query("SELECT * FROM links_table WHERE id=:linkID LIMIT 1")
    suspend fun getThisLinkFromLinksTable(linkID: Long): LinksTable

    @Query("SELECT * FROM important_links_table WHERE id=:linkID LIMIT 1")
    suspend fun getThisLinkFromImpLinksTable(linkID: Long): ImportantLinks

    @Query("SELECT * FROM archived_links_table WHERE id=:linkID LIMIT 1")
    suspend fun getThisLinkFromArchiveLinksTable(linkID: Long): ArchivedLinks

    @Query("SELECT * FROM recently_visited_table WHERE id=:linkID LIMIT 1")
    suspend fun getThisLinkFromRecentlyVisitedLinksTable(linkID: Long): RecentlyVisited

    @Update
    suspend fun updateALinkDataFromLinksTable(linksTable: LinksTable)

    @Update
    suspend fun updateALinkDataFromImpLinksTable(importantLinks: ImportantLinks)

    @Update
    suspend fun updateALinkDataFromRecentlyVisitedLinksTable(recentlyVisited: RecentlyVisited)

    @Update
    suspend fun updateALinkDataFromArchivedLinksTable(archivedLinks: ArchivedLinks)

    @Query("UPDATE recently_visited_table SET title = :newTitle WHERE webURL = :webURL")
    suspend fun renameALinkTitleFromRecentlyVisited(webURL: String, newTitle: String)

    @Query("UPDATE recently_visited_table SET title = :newTitle WHERE id = :linkID")
    suspend fun renameALinkTitleFromRecentlyVisited(linkID: Long, newTitle: String)

    @Query("UPDATE recently_visited_table SET infoForSaving = :newInfo WHERE webURL = :webURL")
    suspend fun renameALinkInfoFromRecentlyVisitedLinks(webURL: String, newInfo: String)

    @Query("UPDATE recently_visited_table SET infoForSaving = :newInfo WHERE id = :linkID")
    suspend fun renameALinkInfoFromRecentlyVisitedLinks(linkID: Long, newInfo: String)

    @Query("UPDATE important_links_table SET title = :newTitle WHERE id = :id")
    suspend fun renameALinkTitleFromImpLinks(id: Long, newTitle: String)

    @Query("UPDATE important_links_table SET infoForSaving = :newInfo WHERE id = :id")
    suspend fun renameALinkInfoFromImpLinks(id: Long, newInfo: String)

    @Query("UPDATE links_table SET infoForSaving = :newInfo WHERE id = :linkID")
    suspend fun renameALinkInfoFromLinksTable(linkID: Long, newInfo: String)

    @Query("UPDATE archived_links_table SET title = :newTitle WHERE webURL = :webURL")
    suspend fun renameALinkTitleFromArchiveLinks(webURL: String, newTitle: String)

    @Query("UPDATE archived_links_table SET title = :newTitle WHERE id = :linkID")
    suspend fun renameALinkTitleFromArchiveLinks(linkID: Long, newTitle: String)

    @Query("UPDATE links_table SET infoForSaving = :newInfo WHERE webURL = :webURL AND isLinkedWithSavedLinks=1")
    suspend fun renameALinkInfoFromSavedLinks(webURL: String, newInfo: String)

    @Query("UPDATE archived_links_table SET infoForSaving = :newInfo WHERE webURL = :webURL")
    suspend fun renameALinkInfoFromArchiveLinks(webURL: String, newInfo: String)

    @Query("UPDATE archived_links_table SET infoForSaving = :newInfo WHERE id = :linkID")
    suspend fun renameALinkInfoFromArchiveLinks(linkID: Long, newInfo: String)

    @Query("UPDATE links_table SET infoForSaving = :newInfo WHERE webURL = :webURL AND keyOfArchiveLinkedFolderV10 = :folderID")
    suspend fun renameALinkInfoFromArchiveBasedFolderLinksV10(
        webURL: String,
        newInfo: String,
        folderID: Long,
    )

    @Query("UPDATE links_table SET infoForSaving = :newInfo WHERE webURL = :webURL AND keyOfArchiveLinkedFolder = :folderName")
    suspend fun renameALinkInfoFromArchiveBasedFolderLinksV9(
        webURL: String,
        newInfo: String,
        folderName: String,
    )

    @Query("UPDATE links_table SET title = :newTitle WHERE webURL = :webURL AND keyOfArchiveLinkedFolderV10 = :folderID")
    suspend fun renameALinkTitleFromArchiveBasedFolderLinksV10(
        webURL: String,
        newTitle: String,
        folderID: Long,
    )

    @Query("UPDATE links_table SET title = :newTitle WHERE webURL = :webURL AND keyOfArchiveLinkedFolder = :folderName")
    suspend fun renameALinkTitleFromArchiveBasedFolderLinksV9(
        webURL: String,
        newTitle: String,
        folderName: String,
    )


    @Query("UPDATE links_table SET keyOfArchiveLinkedFolder = :newFolderName WHERE keyOfArchiveLinkedFolder = :currentFolderName")
    suspend fun renameFolderNameForExistingArchivedFolderDataV9(
        currentFolderName: String, newFolderName: String
    )

    @Query("UPDATE links_table SET keyOfLinkedFolder = :newFolderName WHERE keyOfLinkedFolder = :currentFolderName")
    suspend fun renameFolderNameForExistingFolderDataV9(
        currentFolderName: String, newFolderName: String
    )

    @Query("UPDATE links_table SET isLinkedWithArchivedFolder = 1 , isLinkedWithFolders = 0, keyOfArchiveLinkedFolder = :folderName, keyOfLinkedFolder = \"\" WHERE keyOfLinkedFolder = :folderName")
    suspend fun moveFolderLinksDataToArchiveV9(
        folderName: String,
    )


    @Query("UPDATE links_table SET isLinkedWithArchivedFolder = 0 , isLinkedWithFolders = 1,  keyOfLinkedFolderV10 =  :folderID, keyOfArchiveLinkedFolderV10 = NULL WHERE keyOfArchiveLinkedFolderV10 = :folderID")
    suspend fun moveArchiveFolderBackToRootFolderV10(
        folderID: Long,
    )

    @Query("UPDATE links_table SET isLinkedWithArchivedFolder = 0 , isLinkedWithFolders = 1, keyOfLinkedFolder =  :folderName,keyOfArchiveLinkedFolder=\"\" WHERE keyOfArchiveLinkedFolder = :folderName")
    suspend fun moveArchiveFolderBackToRootFolderV9(
        folderName: String,
    )

    @Query("UPDATE links_table SET title = :newTitle WHERE webURL = :webURL AND isLinkedWithSavedLinks=1")
    suspend fun renameALinkTitleFromSavedLinks(webURL: String, newTitle: String)

    @Transaction
    @Query("UPDATE links_table SET title = :newTitle WHERE id = :linkID")
    suspend fun updateLinkInfoFromLinksTable(linkID: Long, newTitle: String)

    @Query("UPDATE links_table SET title = :newTitle WHERE webURL = :webURL AND keyOfLinkedFolder = :folderName AND isLinkedWithFolders=1")
    suspend fun renameALinkTitleFromFoldersV9(webURL: String, newTitle: String, folderName: String)

    @Query("DELETE FROM links_table WHERE keyOfLinkedFolderV10 = :folderID")
    suspend fun deleteThisFolderLinks(folderID: Long)

    @Insert
    suspend fun addANewLinkInRecentlyVisited(recentlyVisited: RecentlyVisited)

    @Query("UPDATE links_table SET userAgent = :newUserAgent WHERE baseURL LIKE '%' || :domain || '%'")
    suspend fun changeUserAgentInLinksTable(newUserAgent: String?, domain: String)

    @Query("UPDATE archived_links_table SET userAgent = :newUserAgent WHERE baseURL LIKE '%' || :domain || '%'")
    suspend fun changeUserAgentInArchiveLinksTable(newUserAgent: String?, domain: String)

    @Query("UPDATE important_links_table SET userAgent = :newUserAgent WHERE baseURL LIKE '%' || :domain || '%'")
    suspend fun changeUserAgentInImportantLinksTable(newUserAgent: String?, domain: String)

    @Query("UPDATE recently_visited_table SET userAgent = :newUserAgent WHERE baseURL LIKE '%' || :domain || '%'")
    suspend fun changeUserAgentInHistoryTable(newUserAgent: String?, domain: String)
}