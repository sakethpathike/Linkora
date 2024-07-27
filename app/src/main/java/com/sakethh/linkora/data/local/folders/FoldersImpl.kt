package com.sakethh.linkora.data.local.folders

import com.sakethh.linkora.data.local.ArchivedFolders
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.ui.screens.collections.CollectionsScreenVM
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import okhttp3.internal.toImmutableList
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
        if (foldersTable.parentFolderID != null) {

            // add folder id into root parent folder
            val rootFolder = getThisFolderData(CollectionsScreenVM.rootFolderID)
            updateAFolderData(
                rootFolder.apply {
                    val modifiedChildIdsList = childFolderIDs?.toMutableList() ?: mutableListOf()
                    modifiedChildIdsList.add(getLatestAddedFolder().id)
                    childFolderIDs = modifiedChildIdsList
                }
            )

            // add folder id into hierarchy except root folder
            addIdsIntoParentHierarchy(getLatestAddedFolder().id)
        }
    }

    private suspend fun addIdsIntoParentHierarchy(currentID: Long) {

        var currentFolderIteratorID = currentID

        while (true) {

            val currentFolder = getThisFolderData(currentFolderIteratorID)
            val parentFolderOfCurrentFolder =
                getThisFolderData(currentFolder.parentFolderID ?: break)

            val parentFolderOfCurrentFolderChildIds =
                parentFolderOfCurrentFolder.childFolderIDs?.toMutableList() ?: mutableListOf()

            parentFolderOfCurrentFolderChildIds.add(currentFolderIteratorID)

            if (!parentFolderOfCurrentFolderChildIds.contains(currentID)) {
                parentFolderOfCurrentFolderChildIds.add(currentID)
            }

            parentFolderOfCurrentFolder.childFolderIDs =
                parentFolderOfCurrentFolderChildIds.toImmutableList().distinct()

            updateAFolderData(parentFolderOfCurrentFolder)

            currentFolderIteratorID = parentFolderOfCurrentFolder.id
        }
    }

    override suspend fun deleteArchiveFolderNote(folderID: Long) {
        localDatabase.foldersDao().deleteArchiveFolderNote(folderID)
    }

    override fun getAllArchiveFoldersV9(): Flow<List<ArchivedFolders>> {
        return localDatabase.foldersDao().getAllArchiveFoldersV9()
    }

    override suspend fun getAllArchiveFoldersV9List(): List<ArchivedFolders> {
        return localDatabase.foldersDao().getAllArchiveFoldersV9List()
    }

    override fun getAllArchiveFoldersV10(): Flow<List<FoldersTable>> {
        return localDatabase.foldersDao().getAllArchiveFoldersV10()
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
        deleteAllChildFoldersAndLinksOfASpecificFolder(folderID)
        return localDatabase.foldersDao().deleteAFolder(folderID)
    }

    private suspend fun deleteAllChildFoldersAndLinksOfASpecificFolder(folderID: Long) {
        val childFolders = getThisFolderData(folderID)
        coroutineScope {
            try {
                childFolders.childFolderIDs?.forEach {
                    deleteAFolder(it)
                    localDatabase.linksDao().deleteThisFolderLinks(it)
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun deleteMultipleFolders(folderIDs: Array<Long>) {
        return localDatabase.foldersDao().deleteMultipleFolders(folderIDs)
    }
}