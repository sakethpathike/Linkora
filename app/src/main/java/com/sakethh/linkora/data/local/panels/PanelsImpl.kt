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
    }

    override suspend fun deleteConnectedFoldersOfPanel(panelId: Long) {
        localDatabase.panelsDao().deleteConnectedFoldersOfPanel(panelId)
    }

    override suspend fun addANewFolderInAPanel(panelFolder: PanelFolder) {
        localDatabase.panelsDao().addANewFolderInAPanel(panelFolder)
    }

    override suspend fun deleteAFolderFromAPanel(panelId: Long, folderID: Long) {
        localDatabase.panelsDao().deleteAFolderFromAPanel(panelId, folderID)
    }

    override fun getAllThePanels(): Flow<List<Panel>> {
        return localDatabase.panelsDao().getAllThePanels()
    }

    override fun getAllTheFoldersFromAPanel(panelId: Long): Flow<List<PanelFolder>> {
        return localDatabase.panelsDao().getAllTheFoldersFromAPanel(panelId)
    }

}