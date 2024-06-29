package com.sakethh.linkora.data.localDB.dao.crud

import androidx.room.Dao
import androidx.room.Insert
import com.sakethh.linkora.data.localDB.LocalDataBase
import com.sakethh.linkora.data.localDB.models.ArchivedFolders
import com.sakethh.linkora.data.localDB.models.ArchivedLinks
import com.sakethh.linkora.data.localDB.models.FoldersTable
import com.sakethh.linkora.data.localDB.models.ImportantLinks
import com.sakethh.linkora.data.localDB.models.LinksTable
import com.sakethh.linkora.data.localDB.models.RecentlyVisited
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
        val rootFolder = LocalDataBase.localDB.readDao().getThisFolderData(rootParentID)
        LocalDataBase.localDB.updateDao().updateAFolderData(rootFolder)
        addIdsIntoParentHierarchy(currentID)
    }

    private suspend fun addIdsIntoParentHierarchy(currentID: Long) {

        var tempCurrentID = currentID

        while (true) {

            val currentFolderData =
                LocalDataBase.localDB.readDao().getThisFolderData(tempCurrentID)

            val currentParentFolderData = LocalDataBase.localDB.readDao()
                .getThisFolderData(currentFolderData.parentFolderID ?: break)

            val currentParentFolderIdChildData =
                currentParentFolderData.childFolderIDs?.toMutableList()

            currentParentFolderIdChildData?.add(tempCurrentID)

            if (currentParentFolderIdChildData?.contains(currentID) == false) {
                currentParentFolderIdChildData.add(currentID)

            }


            currentParentFolderData.childFolderIDs =
                currentParentFolderIdChildData?.toImmutableList()?.distinct()

            LocalDataBase.localDB.updateDao().updateAFolderData(currentParentFolderData)

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