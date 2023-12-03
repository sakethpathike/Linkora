package com.sakethh.linkora.localDB.dao.crud

import androidx.room.Dao
import androidx.room.Insert
import com.sakethh.linkora.localDB.CustomFunctionsForLocalDB
import com.sakethh.linkora.localDB.dto.ArchivedFolders
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.localDB.dto.RecentlyVisited
import okhttp3.internal.toImmutableList

@Dao
interface CreateDao {
    @Insert
    suspend fun addANewLinkToSavedLinksOrInFolders(linksTable: LinksTable)

    @Insert
    suspend fun addListOfDataInLinksTable(list: List<LinksTable>)

    @Insert
    suspend fun addANewFolder(foldersTable: FoldersTable)

    suspend fun addANewChildIdToARootAndParentFolders(
        rootParentID: Long, parentID: Long, currentID: Long
    ) {
        val rootFolder = CustomFunctionsForLocalDB.localDB.readDao().getThisFolderData(rootParentID)
        CustomFunctionsForLocalDB.localDB.updateDao().updateAFolderData(rootFolder)
        addIdsIntoParentHierarchy(currentID)
    }

    private suspend fun addIdsIntoParentHierarchy(currentID: Long) {

        var tempCurrentID = currentID

        while (true) {
            val currentFolderData =
                CustomFunctionsForLocalDB.localDB.readDao().getThisFolderData(tempCurrentID)

            val currentParentFolderData =
                CustomFunctionsForLocalDB.localDB.readDao()
                    .getThisFolderData(currentFolderData.parentFolderID ?: break)

            val currentParentFolderIdChildData =
                currentParentFolderData.childFolderIDs.toMutableList()
            currentParentFolderIdChildData.add(tempCurrentID)

            if (!currentParentFolderIdChildData.contains(currentID)) {
                currentParentFolderIdChildData.add(currentID)
            }

            currentParentFolderData.childFolderIDs =
                currentParentFolderIdChildData.toImmutableList().distinct()

            CustomFunctionsForLocalDB.localDB.updateDao().updateAFolderData(currentParentFolderData)

            tempCurrentID = currentParentFolderData.id
        }
    }

    @Insert
    suspend fun addANewLinkToImpLinks(importantLinks: ImportantLinks)

    @Insert
    suspend fun addANewLinkToArchiveLink(archivedLinks: ArchivedLinks)

    @Insert
    suspend fun addANewArchiveFolder(archivedFolders: ArchivedFolders)

    @Insert
    suspend fun addANewLinkInRecentlyVisited(recentlyVisited: RecentlyVisited)
}