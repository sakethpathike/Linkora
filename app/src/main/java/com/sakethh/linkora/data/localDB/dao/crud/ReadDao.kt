package com.sakethh.linkora.data.localDB.dao.crud

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.data.localDB.models.ArchivedFolders
import com.sakethh.linkora.data.localDB.models.ArchivedLinks
import com.sakethh.linkora.data.localDB.models.FoldersTable
import com.sakethh.linkora.data.localDB.models.ImportantLinks
import com.sakethh.linkora.data.localDB.models.LinksTable
import com.sakethh.linkora.data.localDB.models.RecentlyVisited
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadDao {
    @Query("SELECT * FROM links_table WHERE isLinkedWithSavedLinks = 1")
    fun getAllSavedLinks(): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table")
    suspend fun getAllFromLinksTable(): List<LinksTable>

    @Query("SELECT * FROM recently_visited_table")
    suspend fun getAllRecentlyVisitedLinks(): List<RecentlyVisited>

    @Query("SELECT * FROM important_links_table")
    suspend fun getAllImpLinks(): List<ImportantLinks>

    @Query("SELECT * FROM archived_links_table")
    suspend fun getAllArchiveLinks(): List<ArchivedLinks>

    @Query("SELECT * FROM archived_folders_table")
    fun getAllArchiveFoldersV9(): Flow<List<ArchivedFolders>>

    @Query("SELECT * FROM archived_folders_table")
    fun getAllArchiveFoldersV9List(): List<ArchivedFolders>

    @Query("SELECT * FROM folders_table WHERE parentFolderID IS NULL AND isFolderArchived=1")
    fun getAllArchiveFoldersV10(): Flow<List<FoldersTable>>


    @Query("SELECT * FROM links_table WHERE isLinkedWithArchivedFolder=1")
    fun getAllArchiveFoldersLinks(): Flow<List<LinksTable>>

    @Query("SELECT * FROM folders_table WHERE parentFolderID IS NULL AND isFolderArchived=0")
    fun getAllRootFolders(): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table WHERE parentFolderID IS NULL AND isFolderArchived=0")
    fun getAllRootFoldersList(): List<FoldersTable>

    @Query("SELECT * FROM folders_table")
    suspend fun getAllFolders(): List<FoldersTable>

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolderV10=:folderID")
    fun getLinksOfThisFolderV10(folderID: Long): Flow<List<LinksTable>>

    @Query("SELECT COUNT(*) FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolderV10=:folderID")
    suspend fun getSizeOfLinksOfThisFolderV10(folderID: Long): Int

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolder=:folderName")
    fun getLinksOfThisFolderV9(folderName: String): Flow<List<LinksTable>>

    @Query("SELECT * FROM folders_table WHERE id = :folderID")
    suspend fun getThisFolderData(folderID: Long): FoldersTable

    @Query("SELECT * FROM archived_folders_table WHERE id = :folderID")
    suspend fun getThisArchiveFolderDataV9(folderID: Long): ArchivedFolders

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

    @Query("SELECT MAX(id) FROM folders_table")
    suspend fun getLastIDOfFoldersTable(): Long

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

    @Query("SELECT COUNT(*) FROM folders_table WHERE folderName = :folderName AND parentFolderID = :parentFolderID")
    suspend fun doesThisChildFolderExists(folderName: String, parentFolderID: Long?): Int

    @Query("SELECT COUNT(*) FROM folders_table WHERE folderName = :folderName AND parentFolderID IS NULL")
    suspend fun doesThisRootFolderExists(folderName: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM archived_folders_table WHERE archiveFolderName = :folderName)")
    suspend fun doesThisArchiveFolderExistsV9(folderName: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM folders_table WHERE id = :folderID AND isFolderArchived = 1)")
    suspend fun doesThisArchiveFolderExistsV10(folderID: Long): Boolean

    @Query("SELECT * FROM folders_table ORDER BY id DESC LIMIT 1")
    suspend fun getLatestAddedFolder(): FoldersTable

    @Query("SELECT COUNT(id) FROM folders_table")
    fun getFoldersCount(): Flow<Int>

    @Query("SELECT (SELECT COUNT(*) FROM links_table) == 0")
    suspend fun isLinksTableEmpty(): Boolean

    @Query("SELECT (SELECT COUNT(*) FROM folders_table) == 0")
    suspend fun isFoldersTableEmpty(): Boolean

    @Query("SELECT (SELECT COUNT(*) FROM archived_links_table) == 0")
    suspend fun isArchivedLinksTableEmpty(): Boolean

    @Query("SELECT (SELECT COUNT(*) FROM archived_folders_table) == 0")
    suspend fun isArchivedFoldersTableEmpty(): Boolean

    @Query("SELECT (SELECT COUNT(*) FROM important_links_table) == 0")
    suspend fun isImpLinksTableEmpty(): Boolean

    @Query("SELECT (SELECT COUNT(*) FROM recently_visited_table) == 0")
    suspend fun isHistoryLinksTableEmpty(): Boolean

    @Query("SELECT * FROM folders_table WHERE parentFolderID = :parentFolderID AND isFolderArchived=0")
    fun getChildFoldersOfThisParentID(parentFolderID: Long?): Flow<List<FoldersTable>>

    @Query("SELECT COUNT(*) FROM folders_table WHERE parentFolderID = :parentFolderID")
    suspend fun getSizeOfChildFoldersOfThisParentID(parentFolderID: Long?): Int
}