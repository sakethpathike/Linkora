package com.sakethh.linkora.data.local.folders

import com.sakethh.linkora.data.local.ArchivedFolders
import com.sakethh.linkora.data.local.FoldersTable
import kotlinx.coroutines.flow.Flow

interface FoldersRepo {

    suspend fun isFoldersTableEmpty(): Boolean
    suspend fun deleteAnArchiveFolderV9(folderID: Long)
    suspend fun createANewFolder(foldersTable: FoldersTable)
    suspend fun createMultipleNewFolders(foldersTable: List<FoldersTable>)
    suspend fun duplicateAFolder(actualFolderId: Long, parentFolderID: Long?): Long

    suspend fun deleteArchiveFolderNote(
        folderID: Long,
    )

    fun getAllArchiveFoldersV9(): Flow<List<ArchivedFolders>>

    suspend fun changeTheParentIdOfASpecificFolder(
        sourceFolderId: List<Long>,
        targetParentId: Long?
    )

    suspend fun getAllArchiveFoldersV9List(): List<ArchivedFolders>

    fun getAllArchiveFoldersV10(): Flow<List<FoldersTable>>
    suspend fun getAllArchiveFoldersV10AsList(): List<FoldersTable>

    fun getAllRootFolders(): Flow<List<FoldersTable>>

    suspend fun getAllRootFoldersList(): List<FoldersTable>

    suspend fun getAllFolders(): List<FoldersTable>

    suspend fun getSizeOfLinksOfThisFolderV10(folderID: Long): Int

    suspend fun getThisFolderData(folderID: Long): FoldersTable

    suspend fun getThisArchiveFolderDataV9(folderID: Long): ArchivedFolders

    suspend fun getLastIDOfFoldersTable(): Long

    suspend fun doesThisChildFolderExists(folderName: String, parentFolderID: Long?): Int

    suspend fun doesThisRootFolderExists(folderName: String): Boolean

    suspend fun doesThisArchiveFolderExistsV9(folderName: String): Boolean

    suspend fun doesThisArchiveFolderExistsV10(folderID: Long): Boolean

    suspend fun getLatestAddedFolder(): FoldersTable

    fun getFoldersCount(): Flow<Int>

    fun getChildFoldersOfThisParentID(parentFolderID: Long?): Flow<List<FoldersTable>>

    suspend fun getChildFoldersOfThisParentIDAsList(parentFolderID: Long?): List<FoldersTable>

    suspend fun getSizeOfChildFoldersOfThisParentID(parentFolderID: Long?): Int

    suspend fun renameInfoOfArchiveFoldersV9(
        newInfo: String,
        folderName: String,
    )

    suspend fun updateAFolderName(folderID: Long, newFolderName: String)

    suspend fun renameAFolderArchiveNameV9(folderID: Long, newFolderName: String)

    suspend fun moveAFolderToArchive(folderID: Long)

    suspend fun moveAMultipleFoldersToArchivesV10(folderIDs: Array<Long>)

    suspend fun moveArchivedFolderToRegularFolderV10(folderID: Long)


    suspend fun updateAFolderNote(folderID: Long, newNote: String)

    suspend fun renameArchivedFolderNoteV9(folderID: Long, newNote: String)

    suspend fun updateAFolderData(foldersTable: FoldersTable)


    suspend fun deleteAFolderNote(folderID: Long)

    suspend fun deleteAFolder(folderID: Long)

    suspend fun deleteMultipleFolders(folderIDs: Array<Long>)
}