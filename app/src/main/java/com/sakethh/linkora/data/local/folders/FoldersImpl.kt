package com.sakethh.linkora.data.local.folders

import com.sakethh.linkora.data.local.ArchivedFolders
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.LocalDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FoldersImpl @Inject constructor(private val localDatabase: LocalDatabase) : FoldersRepo {
    override suspend fun isFoldersTableEmpty(): Boolean {
        return localDatabase.foldersDao().isFoldersTableEmpty()
    }

    override suspend fun deleteAnArchiveFolderV9(folderID: Long) {
        localDatabase.foldersDao().deleteAnArchiveFolderV9(folderID)
    }

    override suspend fun createANewFolder(foldersTable: FoldersTable) {
        localDatabase.foldersDao().createANewFolder(foldersTable)
    }

    override suspend fun createMultipleNewFolders(foldersTable: List<FoldersTable>) {
        localDatabase.foldersDao().createMultipleNewFolders(foldersTable)
    }

    override suspend fun duplicateAFolder(actualFolderId: Long, parentFolderID: Long?): Long {
        return localDatabase.foldersDao().duplicateAFolder(actualFolderId, parentFolderID)
    }
    override suspend fun deleteArchiveFolderNote(folderID: Long) {
        localDatabase.foldersDao().deleteArchiveFolderNote(folderID)
    }

    override fun getAllArchiveFoldersV9(): Flow<List<ArchivedFolders>> {
        return localDatabase.foldersDao().getAllArchiveFoldersV9()
    }

    override suspend fun changeTheParentIdOfASpecificFolder(
        sourceFolderId: List<Long>,
        targetParentId: Long?
    ) {
        localDatabase.foldersDao()
            .changeTheParentIdOfASpecificFolder(sourceFolderId, targetParentId)
    }

    override suspend fun getAllArchiveFoldersV9List(): List<ArchivedFolders> {
        return localDatabase.foldersDao().getAllArchiveFoldersV9List()
    }

    override fun getAllArchiveFoldersV10(): Flow<List<FoldersTable>> {
        return localDatabase.foldersDao().getAllArchiveFoldersV10()
    }

    override suspend fun getAllArchiveFoldersV10AsList(): List<FoldersTable> {
        return localDatabase.foldersDao().getAllArchiveFoldersV10AsList()
    }

    override fun getAllRootFolders(): Flow<List<FoldersTable>> {
        return localDatabase.foldersDao().getAllRootFolders()
    }

    override suspend fun getAllRootFoldersList(): List<FoldersTable> {
        return localDatabase.foldersDao().getAllRootFoldersList()
    }

    override suspend fun getAllFolders(): List<FoldersTable> {
        return localDatabase.foldersDao().getAllFolders()
    }

    override suspend fun getSizeOfLinksOfThisFolderV10(folderID: Long): Int {
        return localDatabase.foldersDao().getSizeOfLinksOfThisFolderV10(folderID)
    }

    override suspend fun getThisFolderData(folderID: Long): FoldersTable {
        return localDatabase.foldersDao().getThisFolderData(folderID)
    }

    override suspend fun getThisArchiveFolderDataV9(folderID: Long): ArchivedFolders {
        return localDatabase.foldersDao().getThisArchiveFolderDataV9(folderID)
    }

    override suspend fun getLastIDOfFoldersTable(): Long {
        return localDatabase.foldersDao().getLastIDOfFoldersTable()
    }

    override suspend fun doesThisChildFolderExists(folderName: String, parentFolderID: Long?): Int {
        return localDatabase.foldersDao().doesThisChildFolderExists(folderName, parentFolderID)
    }

    override suspend fun doesThisRootFolderExists(folderName: String): Boolean {
        return localDatabase.foldersDao().doesThisRootFolderExists(folderName)
    }

    override suspend fun doesThisArchiveFolderExistsV9(folderName: String): Boolean {
        return localDatabase.foldersDao().doesThisArchiveFolderExistsV9(folderName)
    }

    override suspend fun doesThisArchiveFolderExistsV10(folderID: Long): Boolean {
        return localDatabase.foldersDao().doesThisArchiveFolderExistsV10(folderID)
    }

    override suspend fun getLatestAddedFolder(): FoldersTable {
        return localDatabase.foldersDao().getLatestAddedFolder()
    }

    override fun getFoldersCount(): Flow<Int> {
        return localDatabase.foldersDao().getFoldersCount()
    }

    override fun getChildFoldersOfThisParentID(parentFolderID: Long?): Flow<List<FoldersTable>> {
        return localDatabase.foldersDao().getChildFoldersOfThisParentID(parentFolderID)
    }

    override suspend fun getChildFoldersOfThisParentIDAsList(parentFolderID: Long?): List<FoldersTable> {
        return localDatabase.foldersDao().getChildFoldersOfThisParentIDAsList(parentFolderID)
    }
    override suspend fun getSizeOfChildFoldersOfThisParentID(parentFolderID: Long?): Int {
        return localDatabase.foldersDao().getSizeOfChildFoldersOfThisParentID(parentFolderID)
    }

    override suspend fun renameInfoOfArchiveFoldersV9(newInfo: String, folderName: String) {
        return localDatabase.foldersDao().renameInfoOfArchiveFoldersV9(newInfo, folderName)
    }

    override suspend fun updateAFolderName(folderID: Long, newFolderName: String) {
        return localDatabase.foldersDao().renameAFolderName(folderID, newFolderName)
    }

    override suspend fun renameAFolderArchiveNameV9(folderID: Long, newFolderName: String) {
        return localDatabase.foldersDao().renameAFolderArchiveNameV9(folderID, newFolderName)
    }

    override suspend fun moveAFolderToArchive(folderID: Long) {
        return localDatabase.foldersDao().moveAFolderToArchivesV10(folderID)
    }

    override suspend fun moveAMultipleFoldersToArchivesV10(folderIDs: Array<Long>) {
        return localDatabase.foldersDao().moveAMultipleFoldersToArchivesV10(folderIDs)
    }

    override suspend fun moveArchivedFolderToRegularFolderV10(folderID: Long) {
        return localDatabase.foldersDao().moveArchivedFolderToRegularFolderV10(folderID)
    }

    override suspend fun updateAFolderNote(folderID: Long, newNote: String) {
        return localDatabase.foldersDao().renameAFolderNoteV10(folderID, newNote)
    }

    override suspend fun renameArchivedFolderNoteV9(folderID: Long, newNote: String) {
        return localDatabase.foldersDao().renameArchivedFolderNoteV9(folderID, newNote)
    }

    override suspend fun updateAFolderData(foldersTable: FoldersTable) {
        return localDatabase.foldersDao().updateAFolderData(foldersTable)
    }

    override suspend fun deleteAFolderNote(folderID: Long) {
        return localDatabase.foldersDao().deleteAFolderNote(folderID)
    }

    override suspend fun deleteAFolder(folderID: Long) {
        localDatabase.panelsDao().deleteAFolderFromAllPanels(folderID)

        localDatabase.foldersDao().getChildFoldersOfThisParentIDAsAList(folderID).forEach {
            localDatabase.linksDao().deleteThisFolderLinks(it.id)
            deleteAFolder(it.id)
        }

        localDatabase.linksDao().deleteThisFolderLinks(folderID)
        localDatabase.foldersDao().deleteAFolder(folderID)
    }

    override suspend fun deleteMultipleFolders(folderIDs: Array<Long>) {
        folderIDs.forEach {
            deleteAFolder(it)
        }
    }
}