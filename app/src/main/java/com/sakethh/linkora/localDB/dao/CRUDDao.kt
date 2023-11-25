package com.sakethh.linkora.localDB.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sakethh.linkora.localDB.dto.ArchivedFolders
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.localDB.dto.RecentlyVisited
import kotlinx.coroutines.flow.Flow

@Dao
interface CRUDDao {
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

    @Query("SELECT * FROM links_table WHERE isLinkedWithSavedLinks = 1 LIMIT 8")
    fun getLatestSavedLinks(): Flow<List<LinksTable>>

    @Query("SELECT * FROM important_links_table LIMIT 8")
    fun getLatestImportantLinks(): Flow<List<ImportantLinks>>

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

    @Query("DELETE from links_table WHERE webURL = :webURL AND isLinkedWithSavedLinks = 1 AND isLinkedWithArchivedFolder=0 AND isLinkedWithArchivedFolder=0")
    suspend fun deleteALinkFromSavedLinks(webURL: String)

    @Query("UPDATE folders_table SET infoForSaving = \"\" WHERE folderName = :folderName")
    suspend fun deleteAFolderNote(folderName: String)

    @Query("DELETE from important_links_table WHERE webURL = :webURL")
    suspend fun deleteALinkFromImpLinks(webURL: String)

    @Query("DELETE from archived_links_table WHERE webURL = :webURL")
    suspend fun deleteALinkFromArchiveLinks(webURL: String)

    @Query("DELETE from links_table WHERE webURL = :webURL AND keyOfArchiveLinkedFolder = :archiveFolderName AND isLinkedWithArchivedFolder=1 AND isLinkedWithSavedLinks = 0 AND isLinkedWithSavedLinks=0")
    suspend fun deleteALinkFromArchiveFolderBasedLinks(webURL: String, archiveFolderName: String)


    @Query("DELETE from links_table WHERE webURL = :webURL AND keyOfLinkedFolder = :folderName AND isLinkedWithFolders=1 AND isLinkedWithArchivedFolder=0 AND isLinkedWithSavedLinks=0")
    suspend fun deleteALinkFromSpecificFolder(webURL: String, folderName: String)

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

    @Query("SELECT * FROM links_table")
    fun getAllFromLinksTable(): Flow<List<LinksTable>>

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

    @Query("SELECT * FROM folders_table WHERE parentFolderID IS NULL")
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

    @Query("SELECT EXISTS(SELECT * FROM recently_visited_table WHERE webURL = :webURL)")
    suspend fun doesThisExistsInRecentlyVisitedLinks(webURL: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM folders_table WHERE folderName = :folderName)")
    suspend fun doesThisFolderExists(folderName: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM archived_folders_table WHERE archiveFolderName = :folderName)")
    suspend fun doesThisArchiveFolderExists(folderName: String): Boolean

    @Query("UPDATE folders_table SET folderName = :newFolderName WHERE folderName = :existingFolderName")
    suspend fun renameAFolderName(existingFolderName: String, newFolderName: String)

    @Query("UPDATE archived_folders_table SET archiveFolderName = :newFolderName WHERE archiveFolderName = :existingFolderName")
    suspend fun renameAFolderArchiveName(existingFolderName: String, newFolderName: String)

    @Query("UPDATE links_table SET keyOfArchiveLinkedFolder = :newFolderName WHERE keyOfArchiveLinkedFolder = :existingFolderName")
    suspend fun renameFolderNameForExistingArchivedFolderData(
        existingFolderName: String,
        newFolderName: String,
    )

    @Query("UPDATE links_table SET keyOfLinkedFolder = :newFolderName WHERE keyOfLinkedFolder = :existingFolderName")
    suspend fun renameFolderNameForExistingFolderData(
        existingFolderName: String,
        newFolderName: String,
    )

    @Query("UPDATE links_table SET isLinkedWithArchivedFolder = 1 , isLinkedWithFolders = 0, keyOfArchiveLinkedFolder = :folderName, keyOfLinkedFolder = \"\" WHERE keyOfLinkedFolder = :folderName")
    suspend fun moveFolderDataToArchive(
        folderName: String,
    )

    @Query("UPDATE links_table SET isLinkedWithArchivedFolder = 0 , isLinkedWithFolders = 1, keyOfArchiveLinkedFolder = \"\", keyOfLinkedFolder =  :folderName WHERE keyOfArchiveLinkedFolder = :folderName")
    suspend fun moveArchiveFolderBackToFolder(
        folderName: String,
    )

    @Query("UPDATE folders_table SET infoForSaving = :newNote WHERE folderName = :folderName")
    suspend fun renameAFolderNote(folderName: String, newNote: String)

    @Query("UPDATE archived_folders_table SET infoForSaving = :newNote WHERE archiveFolderName = :folderName")
    suspend fun renameArchivedFolderNote(folderName: String, newNote: String)

    @Query("UPDATE links_table SET title = :newTitle WHERE webURL = :webURL AND isLinkedWithSavedLinks=1")
    suspend fun renameALinkTitleFromSavedLinks(webURL: String, newTitle: String)

    @Query("UPDATE links_table SET title = :newTitle WHERE webURL = :webURL AND keyOfLinkedFolder = :folderName AND isLinkedWithFolders=1")
    suspend fun renameALinkTitleFromFolders(webURL: String, newTitle: String, folderName: String)

    @Query("UPDATE links_table SET infoForSaving = \"\" WHERE webURL = :webURL AND keyOfLinkedFolder = :folderName AND isLinkedWithFolders=1")
    suspend fun deleteALinkInfoOfFolders(webURL: String, folderName: String)

    @Query("UPDATE links_table SET infoForSaving = :newInfo WHERE webURL = :webURL AND isLinkedWithSavedLinks=1")
    suspend fun renameALinkInfoFromSavedLinks(webURL: String, newInfo: String)

    @Query("UPDATE links_table SET infoForSaving = \"\" WHERE webURL = :webURL AND isLinkedWithSavedLinks=1")
    suspend fun deleteALinkInfoFromSavedLinks(webURL: String)

    @Query("UPDATE archived_links_table SET infoForSaving = :newInfo WHERE webURL = :webURL")
    suspend fun renameALinkInfoFromArchiveLinks(webURL: String, newInfo: String)

    @Query("UPDATE links_table SET infoForSaving = :newInfo WHERE webURL = :webURL AND keyOfArchiveLinkedFolder = :folderName")
    suspend fun renameALinkInfoFromArchiveBasedFolderLinks(
        webURL: String,
        newInfo: String,
        folderName: String,
    )

    @Query("UPDATE links_table SET title = :newTitle WHERE webURL = :webURL AND keyOfArchiveLinkedFolder = :folderName")
    suspend fun renameALinkTitleFromArchiveBasedFolderLinks(
        webURL: String,
        newTitle: String,
        folderName: String,
    )

    @Query("UPDATE links_table SET infoForSaving = \"\" WHERE webURL = :webURL AND keyOfArchiveLinkedFolder = :folderName")
    suspend fun deleteALinkNoteFromArchiveBasedFolderLinks(
        webURL: String,
        folderName: String,
    )

    @Query("UPDATE archived_links_table SET title = :newTitle WHERE webURL = :webURL")
    suspend fun renameALinkTitleFromArchiveLinks(webURL: String, newTitle: String)

    @Query("UPDATE archived_links_table SET infoForSaving = \"\" WHERE webURL = :webURL")
    suspend fun deleteANoteFromArchiveLinks(webURL: String)

    @Query("UPDATE links_table SET infoForSaving = :newInfo WHERE webURL = :webURL AND keyOfLinkedFolder = :folderName AND isLinkedWithFolders=1")
    suspend fun renameALinkInfoFromFolders(webURL: String, newInfo: String, folderName: String)

    @Query("UPDATE archived_folders_table SET infoForSaving = :newInfo  WHERE archiveFolderName= :folderName")
    suspend fun renameALinkInfoFromArchiveFolders(
        newInfo: String,
        folderName: String,
    )

    @Query("UPDATE archived_folders_table SET infoForSaving = \"\"  WHERE archiveFolderName= :folderName")
    suspend fun deleteArchiveFolderNote(
        folderName: String,
    )

    @Query("UPDATE archived_folders_table SET archiveFolderName = :newFolderName WHERE archiveFolderName= :existingFolderName")
    suspend fun renameALinkTitleFromArchiveFolders(
        existingFolderName: String,
        newFolderName: String,
    )


    @Query("UPDATE important_links_table SET title = :newTitle WHERE webURL = :webURL")
    suspend fun renameALinkTitleFromImpLinks(webURL: String, newTitle: String)

    @Query("UPDATE important_links_table SET infoForSaving = :newInfo WHERE webURL = :webURL")
    suspend fun renameALinkInfoFromImpLinks(webURL: String, newInfo: String)

    @Query("UPDATE important_links_table SET infoForSaving = \"\" WHERE webURL = :webURL")
    suspend fun deleteANoteFromImportantLinks(webURL: String)

    @Query("UPDATE recently_visited_table SET title = :newTitle WHERE webURL = :webURL")
    suspend fun renameALinkTitleFromRecentlyVisited(webURL: String, newTitle: String)

    @Query("UPDATE recently_visited_table SET infoForSaving = :newInfo WHERE webURL = :webURL")
    suspend fun renameALinkInfoFromRecentlyVisitedLinks(webURL: String, newInfo: String)

    @Query("UPDATE recently_visited_table SET infoForSaving = \"\" WHERE webURL = :webURL")
    suspend fun deleteANoteFromRecentlyVisited(webURL: String)
}