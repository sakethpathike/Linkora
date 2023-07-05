package com.sakethh.linkora.localDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalDBDao {

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

    @Query("DELETE from links_table WHERE webURL = :webURL")
    suspend fun deleteALinkFromSavedLinksOrInFolders(webURL: String)

    @Query("DELETE from important_links_table WHERE webURL = :webURL")
    suspend fun deleteALinkFromImpLinks(webURL: String)

    @Query("DELETE from archived_links_table WHERE webURL = :webURL")
    suspend fun deleteALinkFromArchiveLinks(webURL: String)

    @Query("DELETE from recently_visited_table WHERE webURL = :webURL")
    suspend fun deleteARecentlyVisitedLink(webURL: String)

    @Query("DELETE from folders_table WHERE folderName = :folderName")
    suspend fun deleteAFolder(folderName: String)

    @Query("DELETE from links_table WHERE keyOfLinkedFolder = :folderName")
    suspend fun deleteThisFolderData(folderName: String)

    @Query("DELETE from links_table WHERE keyOfArchiveLinkedFolder = :folderName")
    suspend fun deleteThisArchiveFolderData(folderName: String)

    @Query("DELETE from important_folders_table WHERE impFolderName = :folderName")
    suspend fun deleteAnImpFolder(folderName: String)

    @Query("DELETE from archived_folders_table WHERE archiveFolderName = :folderName")
    suspend fun deleteAnArchiveFolder(folderName: String)

    @Query("SELECT * FROM links_table WHERE isLinkedWithSavedLinks = 1")
    fun getAllSavedLinks(): Flow<List<LinksTable>>

    @Query("SELECT * FROM recently_visited_table")
    fun getAllRecentlyVisitedLinks(): Flow<List<RecentlyVisited>>

    @Query("SELECT * FROM important_links_table")
    fun getAllImpLinks(): Flow<List<ImportantLinks>>

    @Query("SELECT * FROM archived_links_table")
    fun getAllArchiveLinks(): Flow<List<ArchivedLinks>>

    @Query("SELECT * FROM archived_folders_table")
    fun getAllArchiveFolders(): Flow<List<ArchivedFolders>>


    @Query("SELECT * FROM links_table WHERE isLinkedWithArchivedFolder=1")
    fun getAllArchiveFoldersData(): Flow<List<LinksTable>>

    @Query("SELECT * FROM folders_table")
    fun getAllFolders(): Flow<List<FoldersTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolder=:folderName")
    fun getThisFolderData(folderName: String): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithArchivedFolder=1 AND keyOfArchiveLinkedFolder=:folderName")
    fun getThisArchiveFolderData(folderName: String): Flow<List<LinksTable>>

    @Query("SELECT EXISTS(SELECT * FROM important_links_table WHERE webURL = :webURL)")
    suspend fun doesThisExistsInImpLinks(webURL: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM links_table WHERE webURL = :webURL AND isLinkedWithSavedLinks=1)")
    suspend fun doesThisExistsInSavedLinks(webURL: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM links_table WHERE webURL = :webURL AND keyOfLinkedFolder=:folderName)")
    suspend fun doesThisLinkExistsInAFolder(webURL: String, folderName: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM archived_links_table WHERE webURL = :webURL)")
    suspend fun doesThisExistsInArchiveLinks(webURL: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM folders_table WHERE folderName = :folderName)")
    suspend fun doesThisFolderExists(folderName: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM archived_folders_table WHERE archiveFolderName = :folderName)")
    suspend fun doesThisArchiveFolderExists(folderName: String): Boolean

    @Query("UPDATE folders_table SET folderName = :newFolderName WHERE folderName = :existingFolderName")
    suspend fun renameAFolderName(existingFolderName: String, newFolderName: String)

    @Query("UPDATE links_table SET keyOfLinkedFolder = :newFolderName WHERE keyOfLinkedFolder = :existingFolderName")
    suspend fun renameFolderNameForExistingFolderData(
        existingFolderName: String,
        newFolderName: String,
    )

    @Query("UPDATE links_table SET isLinkedWithArchivedFolder = 1 , isLinkedWithFolders = 0, keyOfArchiveLinkedFolder = :folderName, keyOfLinkedFolder = \"\" WHERE keyOfLinkedFolder = :folderName")
    suspend fun moveFolderDataToArchive(
        folderName: String,
    )

    @Query("UPDATE folders_table SET infoForSaving = :newNote WHERE folderName = :folderName")
    suspend fun renameAFolderNote(folderName: String, newNote: String)

    @Query("UPDATE links_table SET title = :newTitle WHERE webURL = :webURL")
    suspend fun renameALinkTitleFromSavedLinksOrInFolders(webURL: String, newTitle: String)

    @Query("UPDATE links_table SET infoForSaving = :newInfo WHERE webURL = :webURL")
    suspend fun renameALinkInfoFromSavedLinksOrInFolders(webURL: String, newInfo: String)


    @Query("UPDATE important_links_table SET title = :newTitle WHERE webURL = :webURL")
    suspend fun renameALinkTitleFromImpLinks(webURL: String, newTitle: String)

    @Query("UPDATE important_links_table SET infoForSaving = :newInfo WHERE webURL = :webURL")
    suspend fun renameALinkInfoFromImpLinks(webURL: String, newInfo: String)

    @Query("UPDATE recently_visited_table SET title = :newTitle WHERE webURL = :webURL")
    suspend fun renameALinkTitleFromRecentlyVisited(webURL: String, newTitle: String)

    @Query("UPDATE recently_visited_table SET infoForSaving = :newInfo WHERE webURL = :webURL")
    suspend fun renameALinkInfoFromRecentlyVisitedLinks(webURL: String, newInfo: String)
}