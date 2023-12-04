package com.sakethh.linkora.localDB.dao.crud

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.sakethh.linkora.localDB.dto.FoldersTable

@Dao
interface UpdateDao {
    @Query("UPDATE archived_folders_table SET infoForSaving = :newInfo  WHERE id= :folderID")
    suspend fun renameALinkInfoOfArchiveFolders(
        newInfo: String,
        folderID: Long,
    )

    @Update
    suspend fun updateAFolderData(foldersTable: FoldersTable)

    @Query("UPDATE recently_visited_table SET title = :newTitle WHERE webURL = :webURL")
    suspend fun renameALinkTitleFromRecentlyVisited(webURL: String, newTitle: String)

    @Query("UPDATE recently_visited_table SET infoForSaving = :newInfo WHERE webURL = :webURL")
    suspend fun renameALinkInfoFromRecentlyVisitedLinks(webURL: String, newInfo: String)

    @Query("UPDATE important_links_table SET title = :newTitle WHERE webURL = :webURL")
    suspend fun renameALinkTitleFromImpLinks(webURL: String, newTitle: String)

    @Query("UPDATE important_links_table SET infoForSaving = :newInfo WHERE webURL = :webURL")
    suspend fun renameALinkInfoFromImpLinks(webURL: String, newInfo: String)

    @Query("UPDATE links_table SET infoForSaving = :newInfo WHERE webURL = :webURL AND keyOfLinkedFolder = :folderID AND isLinkedWithFolders=1")
    suspend fun renameALinkInfoFromFolders(webURL: String, newInfo: String, folderID: Long)

    @Query("UPDATE archived_links_table SET title = :newTitle WHERE webURL = :webURL")
    suspend fun renameALinkTitleFromArchiveLinks(webURL: String, newTitle: String)

    @Query("UPDATE links_table SET infoForSaving = :newInfo WHERE webURL = :webURL AND isLinkedWithSavedLinks=1")
    suspend fun renameALinkInfoFromSavedLinks(webURL: String, newInfo: String)

    @Query("UPDATE archived_links_table SET infoForSaving = :newInfo WHERE webURL = :webURL")
    suspend fun renameALinkInfoFromArchiveLinks(webURL: String, newInfo: String)

    @Query("UPDATE links_table SET infoForSaving = :newInfo WHERE webURL = :webURL AND keyOfArchiveLinkedFolder = :folderID")
    suspend fun renameALinkInfoFromArchiveBasedFolderLinks(
        webURL: String,
        newInfo: String,
        folderID: Long,
    )

    @Query("UPDATE links_table SET title = :newTitle WHERE webURL = :webURL AND keyOfArchiveLinkedFolder = :folderID")
    suspend fun renameALinkTitleFromArchiveBasedFolderLinksV9(
        webURL: String,
        newTitle: String,
        folderID: Long,
    )

    @Query("UPDATE folders_table SET folderName = :newFolderName WHERE id = :folderID")
    suspend fun renameAFolderName(folderID: Long, newFolderName: String)

    @Query("UPDATE archived_folders_table SET archiveFolderName = :newFolderName WHERE id= :folderID")
    suspend fun renameAFolderArchiveName(folderID: Long, newFolderName: String)

    @Query("UPDATE links_table SET keyOfArchiveLinkedFolder = :newFolderID WHERE keyOfArchiveLinkedFolder = :currentFolderID")
    suspend fun renameFolderIDForExistingArchivedFolderData(
        currentFolderID: Long, newFolderID: Long
    )

    @Query("UPDATE links_table SET keyOfLinkedFolder = :newFolderID WHERE keyOfLinkedFolder = :currentFolderID")
    suspend fun renameFolderIDForExistingFolderData(
        currentFolderID: Long, newFolderID: Long
    )

    @Query("UPDATE links_table SET isLinkedWithArchivedFolder = 1 , isLinkedWithFolders = 0, keyOfArchiveLinkedFolder = :folderID, keyOfLinkedFolder = \"\" WHERE keyOfLinkedFolder = :folderID")
    suspend fun moveFolderLinksDataToArchive(
        folderID: Long,
    )

    @Query("UPDATE folders_table SET isFolderArchived = 1 WHERE id=:folderID")
    suspend fun moveAFolderToArchivesV10(folderID: Long)

    @Query("UPDATE folders_table SET isFolderArchived = 0 WHERE id=:folderID")
    suspend fun moveArchivedFolderToRegularFolderV10(folderID: Long)

    @Query("UPDATE links_table SET isLinkedWithArchivedFolder = 0 , isLinkedWithFolders = 1, keyOfArchiveLinkedFolder = 0, keyOfLinkedFolder =  :folderID WHERE keyOfArchiveLinkedFolder = :folderID")
    suspend fun moveArchiveFolderBackToRootFolder(
        folderID: Long,
    )

    @Query("UPDATE folders_table SET infoForSaving = :newNote WHERE id = :folderID")
    suspend fun renameAFolderNote(folderID: Long, newNote: String)

    @Query("UPDATE archived_folders_table SET infoForSaving = :newNote WHERE id = :folderID")
    suspend fun renameArchivedFolderNote(folderID: Long, newNote: String)

    @Query("UPDATE links_table SET title = :newTitle WHERE webURL = :webURL AND isLinkedWithSavedLinks=1")
    suspend fun renameALinkTitleFromSavedLinks(webURL: String, newTitle: String)

    @Query("UPDATE links_table SET title = :newTitle WHERE webURL = :webURL AND keyOfLinkedFolder = :folderID AND isLinkedWithFolders=1")
    suspend fun renameALinkTitleFromFolders(webURL: String, newTitle: String, folderID: Long)
}