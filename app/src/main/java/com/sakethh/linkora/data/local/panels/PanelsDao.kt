package com.sakethh.linkora.data.local.panels

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sakethh.linkora.data.local.PanelFolder
import com.sakethh.linkora.data.local.Panel
import kotlinx.coroutines.flow.Flow

@Dao
interface PanelsDao {
    @Insert
    suspend fun addaNewPanel(panel: Panel)

    @Query("DELETE FROM panel WHERE panelId = :id")
    suspend fun deleteAPanel(id: Long) {
        deleteConnectedFoldersOfPanel(id)
    }

    @Query("DELETE FROM panel_folder WHERE connectedPanelId = :panelId")
    suspend fun deleteConnectedFoldersOfPanel(panelId: Long)

    @Insert
    suspend fun addANewFolderInAPanel(panelFolder: PanelFolder)

    @Query("DELETE FROM panel_folder WHERE connectedPanelId = :panelId AND folderId = :folderID ")
    suspend fun deleteAFolderFromAPanel(panelId: Long, folderID: Long)


    @Query("SELECT * FROM panel")
    fun getAllThePanels(): Flow<List<Panel>>

    @Query("SELECT * FROM panel_folder WHERE connectedPanelId = :panelId")
    fun getAllTheFoldersFromAPanel(panelId: Long): Flow<List<PanelFolder>>
}