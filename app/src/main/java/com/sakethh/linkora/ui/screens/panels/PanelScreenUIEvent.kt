package com.sakethh.linkora.ui.screens.panels

import com.sakethh.linkora.data.local.Panel

sealed class PanelScreenUIEvent {
    data class DeleteAPanelFolder(val folderId: Long, val panelId: Long) : PanelScreenUIEvent()

    data class AddANewPanelFolder(
        val folderName: String,
        val folderID: Long,
        val connectedPanelId: Long
    ) : PanelScreenUIEvent()

    data class AddANewPanel(
        val panel: Panel
    ) : PanelScreenUIEvent()

    data class DeleteAPanel(
        val panel: Panel
    ) : PanelScreenUIEvent()

    data class UpdateAPanelName(
        val newName: String,
        val panelId: Long
    ) : PanelScreenUIEvent()
}