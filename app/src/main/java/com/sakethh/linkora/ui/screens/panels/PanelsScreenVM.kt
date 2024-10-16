package com.sakethh.linkora.ui.screens.panels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.Panel
import com.sakethh.linkora.data.local.PanelFolder
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.panels.PanelsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PanelsScreenVM @Inject constructor(
    private val panelsRepo: PanelsRepo,
    private val foldersRepo: FoldersRepo
) : ViewModel() {

    private val _panelsData = MutableStateFlow(emptyList<Panel>())
    val panelsData = _panelsData.onStart {
        panelsRepo.getAllThePanels()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _foldersOfTheSelectedPanel = MutableStateFlow(emptyList<PanelFolder>())
    val foldersOfTheSelectedPanel = _foldersOfTheSelectedPanel.onStart {
        panelsRepo.getAllTheFoldersFromAPanel(selectedPanelData.panelId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rootFolders = MutableStateFlow(emptyList<FoldersTable>()).onStart {
        foldersRepo.getAllRootFolders()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    companion object {
        var selectedPanelData = Panel(panelName = "")
    }

    fun onUiEvent(panelScreenUIEvent: PanelScreenUIEvent) {
        when (panelScreenUIEvent) {
            is PanelScreenUIEvent.AddANewPanel -> {
                viewModelScope.launch {
                    panelsRepo.addaNewPanel(panelScreenUIEvent.panel)
                }
            }

            is PanelScreenUIEvent.DeleteAPanel -> {
                viewModelScope.launch {
                    panelsRepo.deleteAPanel(panelScreenUIEvent.panel.panelId)
                }
            }

            is PanelScreenUIEvent.DeleteAPanelFolder -> {
                viewModelScope.launch {
                    panelsRepo.deleteAFolderFromAPanel(
                        panelId = panelScreenUIEvent.panelId,
                        folderID = panelScreenUIEvent.folderId
                    )
                }
            }

            is PanelScreenUIEvent.AddANewPanelFolder -> {
                viewModelScope.launch {
                    panelsRepo.addANewFolderInAPanel(
                        PanelFolder(
                            folderId = panelScreenUIEvent.folderID,
                            panelPosition = 0,
                            folderName = panelScreenUIEvent.folderName,
                            connectedPanelId = panelScreenUIEvent.connectedPanelId
                        )
                    )
                }
            }

            is PanelScreenUIEvent.UpdateAPanelName -> {
                viewModelScope.launch {
                    panelsRepo.updateAPanelName(
                        newName = panelScreenUIEvent.newName,
                        panelId = panelScreenUIEvent.panelId
                    )
                }
            }
        }
    }

}