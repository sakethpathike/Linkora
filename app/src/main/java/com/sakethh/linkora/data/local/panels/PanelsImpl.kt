package com.sakethh.linkora.data.local.panels

import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.Panel
import com.sakethh.linkora.data.local.PanelFolder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PanelsImpl @Inject constructor(private val localDatabase: LocalDatabase) : PanelsRepo {
    override suspend fun addaNewPanel(panel: Panel) {
        localDatabase.panelsDao().addaNewPanel(panel)
    }

    override suspend fun deleteAPanel(id: Long) {
        localDatabase.panelsDao().deleteAPanel(id)
        localDatabase.panelsDao().deleteConnectedFoldersOfPanel(id)
    }

    override suspend fun updateAPanelName(newName: String, panelId: Long) {
        localDatabase.panelsDao().updateAPanelName(newName, panelId)
    }

    override suspend fun addANewFolderInAPanel(panelFolder: PanelFolder) {
        localDatabase.panelsDao().addANewFolderInAPanel(panelFolder)
    }

    override suspend fun deleteAFolderFromAllPanels(folderID: Long) {
        localDatabase.panelsDao().deleteAFolderFromAllPanels(folderID)
    }

    override suspend fun deleteAFolderFromAPanel(panelId: Long, folderID: Long) {
        localDatabase.panelsDao().deleteAFolderFromAPanel(panelId, folderID)
    }

    override fun getAllThePanels(): Flow<List<Panel>> {
        return localDatabase.panelsDao().getAllThePanels()
    }

    override suspend fun getAllThePanelsAsAList(): List<Panel> {
        return localDatabase.panelsDao().getAllThePanelsAsAList()
    }

    override suspend fun getAllThePanelFoldersAsAList(): List<PanelFolder> {
        return localDatabase.panelsDao().getAllThePanelFoldersAsAList()
    }

    override fun getAllTheFoldersFromAPanel(panelId: Long): Flow<List<PanelFolder>> {
        return localDatabase.panelsDao().getAllTheFoldersFromAPanel(panelId)
    }

}