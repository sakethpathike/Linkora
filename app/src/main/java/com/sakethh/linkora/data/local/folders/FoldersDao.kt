package com.sakethh.linkora.data.local.folders

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sakethh.linkora.data.local.ArchivedFolders
import com.sakethh.linkora.data.local.FoldersTable
import kotlinx.coroutines.flow.Flow

@Dao
interface FoldersDao {
    @Insert
    suspend fun createANewFolder(foldersTable: FoldersTable)

    @Query("INSERT INTO folders_table(folderName, infoForSaving,parentFolderID,isFolderArchived,isMarkedAsImportant) SELECT folderName, infoForSaving, :parentFolderID, isFolderArchived, isMarkedAsImportant FROM folders_table WHERE id= :actualFolderId")
    suspend fun duplicateAFolder(actualFolderId: Long, parentFolderID: Long?): Long

    @Insert
    suspend fun createMultipleNewFolders(foldersTable: List<FoldersTable>)

    @Query("UPDATE archived_folders_table SET infoForSaving = \"\"  WHERE id= :folderID")
    suspend fun deleteArchiveFolderNote(
        folderID: Long,
    )

    @Query("SELECT * FROM archived_folders_table")
    fun getAllArchiveFoldersV9(): Flow<List<ArchivedFolders>>

    @Query("SELECT * FROM archived_folders_table")
    suspend fun getAllArchiveFoldersV9List(): List<ArchivedFolders>

    @Query("SELECT * FROM folders_table WHERE parentFolderID IS NULL AND isFolderArchived=1")
    fun getAllArchiveFoldersV10(): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table WHERE parentFolderID IS NULL AND isFolderArchived=1")
    suspend fun getAllArchiveFoldersV10AsList(): List<FoldersTable>

    @Query("SELECT * FROM folders_table WHERE parentFolderID IS NULL AND isFolderArchived=0")
    fun getAllRootFolders(): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table WHERE parentFolderID IS NULL AND isFolderArchived=0")
    suspend fun getAllRootFoldersList(): List<FoldersTable>

    @Query("SELECT * FROM folders_table")
    suspend fun getAllFolders(): List<FoldersTable>

    @Query("SELECT COUNT(*) FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolderV10=:folderID")
    suspend fun getSizeOfLinksOfThisFolderV10(folderID: Long): Int

    @Query("SELECT * FROM folders_table WHERE id = :folderID")
    suspend fun getThisFolderData(folderID: Long): FoldersTable

    @Query("SELECT * FROM archived_folders_table WHERE id = :folderID")
    suspend fun getThisArchiveFolderDataV9(folderID: Long): ArchivedFolders

    @Query("SELECT MAX(id) FROM folders_table")
    suspend fun getLastIDOfFoldersTable(): Long

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

    @Query("UPDATE FOLDERS_TABLE SET parentFolderID = :targetParentId WHERE id IN (:sourceFolderId)")
    suspend fun changeTheParentIdOfASpecificFolder(
        sourceFolderId: List<Long>,
        targetParentId: Long?
    )

    @Query("SELECT * FROM folders_table WHERE parentFolderID = :parentFolderID AND isFolderArchived=0")
    fun getChildFoldersOfThisParentID(parentFolderID: Long?): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table WHERE parentFolderID = :parentFolderID AND isFolderArchived=0")
    suspend fun getChildFoldersOfThisParentIDAsList(parentFolderID: Long?): List<FoldersTable>

    @Query("SELECT * FROM folders_table WHERE parentFolderID = :parentFolderID AND isFolderArchived=0")
    suspend fun getChildFoldersOfThisParentIDAsAList(parentFolderID: Long?): List<FoldersTable>

    @Query("SELECT COUNT(*) FROM folders_table WHERE parentFolderID = :parentFolderID")
    suspend fun getSizeOfChildFoldersOfThisParentID(parentFolderID: Long?): Int

    @Query("UPDATE archived_folders_table SET infoForSaving = :newInfo  WHERE archiveFolderName= :folderName")
    suspend fun renameInfoOfArchiveFoldersV9(
        newInfo: String,
        folderName: String,
    )

    @Query("UPDATE folders_table SET folderName = :newFolderName WHERE id = :folderID")
    suspend fun renameAFolderName(folderID: Long, newFolderName: String)

    @Query("UPDATE archived_folders_table SET archiveFolderName = :newFolderName WHERE id= :folderID")
    suspend fun renameAFolderArchiveNameV9(folderID: Long, newFolderName: String)

    @Query("UPDATE folders_table SET isFolderArchived = 1 WHERE id=:folderID")
    suspend fun moveAFolderToArchivesV10(folderID: Long)

    @Query("UPDATE folders_table SET isFolderArchived = 1 WHERE id in (:folderIDs)")
    suspend fun moveAMultipleFoldersToArchivesV10(folderIDs: Array<Long>)

    @Query("UPDATE folders_table SET isFolderArchived = 0 WHERE id=:folderID")
    suspend fun moveArchivedFolderToRegularFolderV10(folderID: Long)


    @Query("UPDATE folders_table SET infoForSaving = :newNote WHERE id = :folderID")
    suspend fun renameAFolderNoteV10(folderID: Long, newNote: String)

    @Query("UPDATE archived_folders_table SET infoForSaving = :newNote WHERE id = :folderID")
    suspend fun renameArchivedFolderNoteV9(folderID: Long, newNote: String)

    @Update
    suspend fun updateAFolderData(foldersTable: FoldersTable)


    @Query("UPDATE folders_table SET infoForSaving = \"\" WHERE id = :folderID")
    suspend fun deleteAFolderNote(folderID: Long)

    @Query("DELETE from folders_table WHERE id = :folderID")
    suspend fun deleteAFolder(folderID: Long)

    @Query("DELETE from folders_table WHERE parentFolderID = :parentFolderId")
    suspend fun deleteChildFoldersOfThisParentID(parentFolderId: Long)

    @Query("DELETE from archived_folders_table WHERE id= :folderID")
    suspend fun deleteAnArchiveFolderV9(folderID: Long)

    @Query("SELECT (SELECT COUNT(*) FROM folders_table) == 0")
    suspend fun isFoldersTableEmpty(): Boolean
}