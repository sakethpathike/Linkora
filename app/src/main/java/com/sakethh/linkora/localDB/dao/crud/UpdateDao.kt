package com.sakethh.linkora.localDB.dao.crud

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.sakethh.linkora.localDB.dto.FoldersTable

@Dao
interface UpdateDao {
    @Query("UPDATE archived_folders_table SET infoForSaving = :newInfo  WHERE archiveFolderName= :folderName")
    suspend fun renameInfoOfArchiveFoldersV9(
        newInfo: String,
        folderName: String,
    )

    @Update
    suspend fun updateAFolderData(foldersTable: FoldersTable)

    @Query("UPDATE recently_visited_table SET title = :newTitle WHERE webURL = :webURL")
    suspend fun renameALinkTitleFromRecentlyVisited(webURL: String, newTitle: String)

    @Query("UPDATE recently_visited_table SET infoForSaving = :newInfo WHERE webURL = :webURL")
    suspend fun renameALinkInfoFromRecentlyVisitedLinks(webURL: String, newInfo: String)

    @Query("UPDATE important_links_table SET title = :newTitle WHERE id = :id")
    suspend fun renameALinkTitleFromImpLinks(id: Long, newTitle: String)

    @Query("UPDATE important_links_table SET infoForSaving = :newInfo WHERE id = :id")
    suspend fun renameALinkInfoFromImpLinks(id: Long, newInfo: String)

    @Query("UPDATE links_table SET infoForSaving = :newInfo WHERE id = :linkID")
    suspend fun renameALinkInfo(linkID: Long, newInfo: String)

    @Query("UPDATE archived_links_table SET title = :newTitle WHERE webURL = :webURL")
    suspend fun renameALinkTitleFromArchiveLinks(webURL: String, newTitle: String)

    @Query("UPDATE links_table SET infoForSaving = :newInfo WHERE webURL = :webURL AND isLinkedWithSavedLinks=1")
    suspend fun renameALinkInfoFromSavedLinks(webURL: String, newInfo: String)

    @Query("UPDATE archived_links_table SET infoForSaving = :newInfo WHERE webURL = :webURL")
    suspend fun renameALinkInfoFromArchiveLinks(webURL: String, newInfo: String)

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

    @Query("UPDATE folders_table SET folderName = :newFolderName WHERE id = :folderID")
    suspend fun renameAFolderName(folderID: Long, newFolderName: String)

    @Query("UPDATE archived_folders_table SET archiveFolderName = :newFolderName WHERE id= :folderID")
    suspend fun renameAFolderArchiveNameV9(folderID: Long, newFolderName: String)

    @Query("UPDATE links_table SET keyOfArchiveLinkedFolder = :newFolderName WHERE keyOfArchiveLinkedFolder = :currentFolderName")
    suspend fun renameFolderNameForExistingArchivedFolderDataV9(
        currentFolderName: Long, newFolderName: String
    )

    @Query("UPDATE links_table SET keyOfLinkedFolder = :newFolderName WHERE keyOfLinkedFolder = :currentFolderName")
    suspend fun renameFolderNameForExistingFolderDataV9(
        currentFolderName: String, newFolderName: String
    )

    @Query("UPDATE links_table SET isLinkedWithArchivedFolder = 1 , isLinkedWithFolders = 0, keyOfArchiveLinkedFolder = :folderName, keyOfLinkedFolder = \"\" WHERE keyOfLinkedFolder = :folderName")
    suspend fun moveFolderLinksDataToArchiveV9(
        folderName: String,
    )

    @Query("UPDATE folders_table SET isFolderArchived = 1 WHERE id=:folderID")
    suspend fun moveAFolderToArchivesV10(folderID: Long)

    @Query("UPDATE folders_table SET isFolderArchived = 0 WHERE id=:folderID")
    suspend fun moveArchivedFolderToRegularFolderV10(folderID: Long)

    @Query("UPDATE links_table SET isLinkedWithArchivedFolder = 0 , isLinkedWithFolders = 1,  keyOfLinkedFolderV10 =  :folderID, keyOfArchiveLinkedFolderV10 = NULL WHERE keyOfArchiveLinkedFolderV10 = :folderID")
    suspend fun moveArchiveFolderBackToRootFolderV10(
        folderID: Long,
    )

    @Query("UPDATE links_table SET isLinkedWithArchivedFolder = 0 , isLinkedWithFolders = 1, keyOfLinkedFolder =  :folderName,keyOfArchiveLinkedFolder=\"\" WHERE keyOfArchiveLinkedFolder = :folderName AND isLinkedWithArchivedFolder = 1")
    suspend fun moveArchiveFolderBackToRootFolderV9(
        folderName: String,
    )

    @Query("UPDATE folders_table SET infoForSaving = :newNote WHERE id = :folderID")
    suspend fun renameAFolderNoteV10(folderID: Long, newNote: String)

    @Query("UPDATE archived_folders_table SET infoForSaving = :newNote WHERE id = :folderID")
    suspend fun renameArchivedFolderNoteV9(folderID: Long, newNote: String)

    @Query("UPDATE links_table SET title = :newTitle WHERE webURL = :webURL AND isLinkedWithSavedLinks=1")
    suspend fun renameALinkTitleFromSavedLinks(webURL: String, newTitle: String)

    @Query("UPDATE links_table SET title = :newTitle WHERE id = :linkID")
    suspend fun renameALinkTitle(linkID: Long, newTitle: String)

    @Query("UPDATE links_table SET title = :newTitle WHERE webURL = :webURL AND keyOfLinkedFolder = :folderName AND isLinkedWithFolders=1")
    suspend fun renameALinkTitleFromFoldersV9(webURL: String, newTitle: String, folderName: String)
}