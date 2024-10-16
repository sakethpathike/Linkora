package com.sakethh.linkora.data.local.panels

import com.sakethh.linkora.data.local.Panel
import com.sakethh.linkora.data.local.PanelFolder
import kotlinx.coroutines.flow.Flow

interface PanelsRepo {
    suspend fun addaNewPanel(panel: Panel)
    suspend fun deleteAPanel(id: Long)
    suspend fun deleteConnectedFoldersOfPanel(panelId: Long)
    suspend fun addANewFolderInAPanel(panelFolder: PanelFolder)
    suspend fun deleteAFolderFromAPanel(panelId: Long, folderID: Long)
    fun getAllThePanels(): Flow<List<Panel>>
    fun getAllTheFoldersFromAPanel(panelId: Long): Flow<List<PanelFolder>>
}