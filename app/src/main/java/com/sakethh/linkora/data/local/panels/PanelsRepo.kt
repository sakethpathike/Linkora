package com.sakethh.linkora.data.local.panels

import com.sakethh.linkora.data.local.Panel
import com.sakethh.linkora.data.local.PanelFolder
import kotlinx.coroutines.flow.Flow

interface PanelsRepo {
    suspend fun addaNewPanel(panel: Panel)
    suspend fun deleteAPanel(id: Long)
    suspend fun updateAPanelName(newName: String, panelId: Long)
    suspend fun addANewFolderInAPanel(panelFolder: PanelFolder)
    suspend fun deleteAFolderFromAllPanels(folderID: Long)
    suspend fun deleteAFolderFromAPanel(panelId: Long, folderID: Long)
    fun getAllThePanels(): Flow<List<Panel>>
    suspend fun getAllThePanelsAsAList(): List<Panel>
    suspend fun getAllThePanelFoldersAsAList(): List<PanelFolder>
    fun getAllTheFoldersFromAPanel(panelId: Long): Flow<List<PanelFolder>>
}