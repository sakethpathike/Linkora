package com.sakethh.linkora.ui.viewmodels.collections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.sakethh.linkora.data.localDB.LocalDataBase
import com.sakethh.linkora.data.localDB.dto.ArchivedFolders
import com.sakethh.linkora.data.localDB.dto.ArchivedLinks
import com.sakethh.linkora.data.localDB.dto.FoldersTable
import com.sakethh.linkora.data.localDB.dto.LinksTable
import com.sakethh.linkora.ui.screens.collections.archiveScreen.ChildArchiveScreen
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM
import com.sakethh.linkora.ui.viewmodels.localDB.DeleteVM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ArchiveScreenModal(
    val name: String,
    val screen: @Composable (navController: NavController) -> Unit,
)

enum class ArchiveScreenType {
    LINKS, FOLDERS
}

class ArchiveScreenVM(
    private val deleteVM: DeleteVM = DeleteVM()
) : ViewModel() {
    private val _showToast = mutableStateOf(Pair(false, ""))
    val showToast = _showToast
    val selectedArchivedLinkData = mutableStateOf(
        ArchivedLinks(
            title = "",
            webURL = "",
            baseURL = "",
            imgURL = "",
            infoForSaving = ""
        )
    )
    val parentArchiveScreenData = listOf(
        ArchiveScreenModal(name = "Links", screen = { navController ->
            ChildArchiveScreen(
                archiveScreenType = ArchiveScreenType.LINKS,
                navController = navController
            )
        }), ArchiveScreenModal(name = "Folders",
            screen = { navController ->
                ChildArchiveScreen(
                    archiveScreenType = ArchiveScreenType.FOLDERS,
                    navController = navController
                )
            })
    )
    private val _archiveLinksData = MutableStateFlow(
        emptyList<ArchivedLinks>()
    )
    val archiveLinksData = _archiveLinksData.asStateFlow()

    private val _archiveFoldersDataV9 = MutableStateFlow(emptyList<ArchivedFolders>())
    val archiveFoldersDataV9 = _archiveFoldersDataV9.asStateFlow()

    private val _archiveFoldersDataV10 = MutableStateFlow(emptyList<FoldersTable>())
    val archiveFoldersDataV10 = _archiveFoldersDataV10.asStateFlow()

    init {
        changeRetrievedData(
            sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                SettingsScreenVM.Settings.selectedSortingType.value
            )
        )
    }

    val isSelectionModeEnabled = mutableStateOf(false)
    val selectedLinksData = mutableStateListOf<ArchivedLinks>()
    val areAllLinksChecked = mutableStateOf(false)
    val selectedFoldersID = mutableStateListOf<Long>()
    val areAllFoldersChecked = mutableStateOf(false)


    fun unArchiveMultipleFolders() {
        viewModelScope.launch {
            selectedFoldersID.toList().forEach {
                LocalDataBase.localDB.updateDao().moveArchivedFolderToRegularFolderV10(it)
            }
        }
    }

    fun deleteMultipleSelectedLinks() {
        viewModelScope.launch {
            selectedLinksData.toList().forEach {
                LocalDataBase.localDB.deleteDao().deleteALinkFromArchiveLinks(it.id)
            }
        }.invokeOnCompletion {
            selectedLinksData.clear()
        }
    }

    fun deleteMultipleSelectedFolders() {
        viewModelScope.launch {
            selectedFoldersID.toList().forEach {
                LocalDataBase.localDB.deleteDao().deleteAFolder(it)
            }
        }.invokeOnCompletion {
            selectedFoldersID.clear()
        }
    }

    fun unArchiveMultipleSelectedLinks() {
        viewModelScope.launch {
            selectedLinksData.toList().forEach { archivedLink ->
                LocalDataBase.localDB.createDao().addANewLinkToSavedLinksOrInFolders(
                    LinksTable(
                        title = archivedLink.title,
                        webURL = archivedLink.webURL,
                        baseURL = archivedLink.baseURL,
                        imgURL = archivedLink.imgURL,
                        infoForSaving = archivedLink.infoForSaving,
                        isLinkedWithSavedLinks = true,
                        isLinkedWithFolders = false,
                        isLinkedWithImpFolder = false,
                        keyOfImpLinkedFolder = "",
                        isLinkedWithArchivedFolder = false
                    )
                )
                LocalDataBase.localDB.deleteDao().deleteALinkFromArchiveLinks(archivedLink.id)
            }
        }.invokeOnCompletion {
            selectedLinksData.clear()
        }
    }

    fun onUnArchiveLinkClick(archivedLink: ArchivedLinks) {
        viewModelScope.launch {
            LocalDataBase.localDB.createDao().addANewLinkToSavedLinksOrInFolders(
                LinksTable(
                    title = archivedLink.title,
                    webURL = archivedLink.webURL,
                    baseURL = archivedLink.baseURL,
                    imgURL = archivedLink.imgURL,
                    infoForSaving = archivedLink.infoForSaving,
                    isLinkedWithSavedLinks = true,
                    isLinkedWithFolders = false,
                    isLinkedWithImpFolder = false,
                    keyOfImpLinkedFolder = "",
                    isLinkedWithArchivedFolder = false
                )
            )
            LocalDataBase.localDB.deleteDao().deleteALinkFromArchiveLinks(archivedLink.id)
        }.invokeOnCompletion {
            selectedLinksData.clear()
        }
    }


    fun onNoteChangeClick(
        archiveScreenType: ArchiveScreenType,
        webURL: String,
        newNote: String,
        onTaskCompleted: () -> Unit,
        folderID: Long
    ) {
        if (archiveScreenType == ArchiveScreenType.LINKS) {
            viewModelScope.launch {
                LocalDataBase.localDB.updateDao()
                    .renameALinkInfoFromArchiveLinks(webURL, newNote)
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {
            viewModelScope.launch {
                LocalDataBase.localDB.updateDao().renameAFolderNoteV10(folderID, newNote)
            }
        }
    }

    fun onTitleChangeClick(
        archiveScreenType: ArchiveScreenType,
        newTitle: String,
        webURL: String,
        onTaskCompleted: () -> Unit,
        folderID: Long
    ) {
        if (archiveScreenType == ArchiveScreenType.LINKS) {
            viewModelScope.launch {
                LocalDataBase.localDB.updateDao()
                    .renameALinkTitleFromArchiveLinks(webURL = webURL, newTitle = newTitle)
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {
            viewModelScope.launch {
                LocalDataBase.localDB.updateDao().renameAFolderName(folderID, newTitle)
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        }
    }

    fun changeRetrievedData(
        sortingPreferences: SettingsScreenVM.SortingPreferences
    ) {
        val (sortedLinks, sortedFolders) = when (sortingPreferences) {
            SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                Pair(
                    LocalDataBase.localDB.archivedLinksSorting().sortByAToZ(),
                    LocalDataBase.localDB.archivedFolderSorting().sortByAToZV10()
                )
            }

            SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                Pair(
                    LocalDataBase.localDB.archivedLinksSorting().sortByZToA(),
                    LocalDataBase.localDB.archivedFolderSorting().sortByZToAV10()
                )
            }

            SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                Pair(
                    LocalDataBase.localDB.archivedLinksSorting().sortByLatestToOldest(),
                    LocalDataBase.localDB.archivedFolderSorting().sortByLatestToOldestV10()
                )
            }

            SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                Pair(
                    LocalDataBase.localDB.archivedLinksSorting().sortByOldestToLatest(),
                    LocalDataBase.localDB.archivedFolderSorting().sortByOldestToLatestV10()
                )
            }
        }
        viewModelScope.launch {
            sortedLinks.collectLatest {
                _archiveLinksData.emit(it)
            }
        }
        viewModelScope.launch {
            sortedFolders.collectLatest {
                _archiveFoldersDataV10.emit(it)
            }
        }
    }
    fun onDeleteClick(
        archiveScreenType: ArchiveScreenType,
        selectedURLOrFolderName: String,
        onTaskCompleted: () -> Unit
    ) {
        if (archiveScreenType == ArchiveScreenType.LINKS) {
            viewModelScope.launch {
                LocalDataBase.localDB.deleteDao()
                    .deleteALinkFromArchiveLinksV9(webURL = selectedURLOrFolderName)
                _showToast.value = true to "removed the link from archive permanently"
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {
            deleteVM.onRegularFolderDeleteClick(
                CollectionsScreenVM.selectedFolderData.value.id
            )
            _showToast.value = true to "deleted the folder"
        }

    }

    fun onNoteDeleteCardClick(
        archiveScreenType: ArchiveScreenType,
        selectedURLOrFolderName: String,
        onTaskCompleted: () -> Unit,
        folderID: Long
    ) {
        if (archiveScreenType == ArchiveScreenType.FOLDERS) {
            viewModelScope.launch {
                LocalDataBase.localDB.deleteDao().deleteAFolderNote(folderID)
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {
            viewModelScope.launch {
                LocalDataBase.localDB.deleteDao()
                    .deleteANoteFromArchiveLinks(webURL = selectedURLOrFolderName)
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        }
        _showToast.value = true to "deleted the note"
    }

    fun onUnArchiveClickV10(folderID: Long) {
        viewModelScope.launch {
            LocalDataBase.localDB.updateDao()
                .moveArchivedFolderToRegularFolderV10(folderID)
        }.invokeOnCompletion {
            selectedFoldersID.clear()
        }
    }
}