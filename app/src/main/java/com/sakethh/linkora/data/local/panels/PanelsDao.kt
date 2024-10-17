package com.sakethh.linkora.data.local.panels

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sakethh.linkora.data.local.Panel
import com.sakethh.linkora.data.local.PanelFolder
import kotlinx.coroutines.flow.Flow

@Dao
interface PanelsDao {
    @Insert
    suspend fun addaNewPanel(panel: Panel)

    @Query("DELETE FROM panel WHERE panelId = :id")
    suspend fun deleteAPanel(id: Long)

    @Query("UPDATE panel SET panelName = :newName WHERE panelId = :panelId")
    suspend fun updateAPanelName(newName: String, panelId: Long)

    @Query("DELETE FROM panel_folder WHERE connectedPanelId = :panelId")
    suspend fun deleteConnectedFoldersOfPanel(panelId: Long)

    @Insert
    suspend fun addANewFolderInAPanel(panelFolder: PanelFolder)

    @Query("DELETE FROM panel_folder WHERE connectedPanelId = :panelId AND folderId = :folderID ")
    suspend fun deleteAFolderFromAPanel(panelId: Long, folderID: Long)

    @Query("DELETE FROM panel_folder WHERE folderId = :folderID")
    suspend fun deleteAFolderFromAllPanels(folderID: Long)

    @Query("SELECT * FROM panel")
    fun getAllThePanels(): Flow<List<Panel>>

    @Query("SELECT * FROM panel")
    suspend fun getAllThePanelsAsAList(): List<Panel>

    @Query("SELECT * FROM panel_folder")
    suspend fun getAllThePanelFoldersAsAList(): List<PanelFolder>

    @Query("SELECT * FROM panel_folder WHERE connectedPanelId = :panelId")
    fun getAllTheFoldersFromAPanel(panelId: Long): Flow<List<PanelFolder>>
}