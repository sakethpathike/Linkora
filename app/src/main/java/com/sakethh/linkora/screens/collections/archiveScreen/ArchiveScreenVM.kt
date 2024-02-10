package com.sakethh.linkora.screens.collections.archiveScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.sakethh.linkora.localDB.LocalDataBase
import com.sakethh.linkora.localDB.commonVMs.DeleteVM
import com.sakethh.linkora.localDB.dto.ArchivedFolders
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.screens.collections.CollectionsScreenVM
import com.sakethh.linkora.screens.collections.FolderComponent
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ArchiveScreenModal(
    val name: String,
    val screen: @Composable (navController: NavController) -> Unit,
)

enum class ArchiveScreenType {
    LINKS, FOLDERS
}

data class ArchiveLinkTableComponent(
    val isCheckBoxSelected: List<MutableState<Boolean>>, val archiveLinksTable: List<ArchivedLinks>
)

class ArchiveScreenVM(
    private val deleteVM: DeleteVM = DeleteVM()
) : ViewModel() {
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
        ArchiveLinkTableComponent(
            emptyList(),
            emptyList()
        )
    )
    val archiveLinksData = _archiveLinksData.asStateFlow()

    private val _archiveFoldersDataV9 = MutableStateFlow(emptyList<ArchivedFolders>())
    val archiveFoldersDataV9 = _archiveFoldersDataV9.asStateFlow()

    private val _archiveFoldersDataV10 = MutableStateFlow(FolderComponent(emptyList(), emptyList()))
    val archiveFoldersDataV10 = _archiveFoldersDataV10.asStateFlow()

    init {
        changeRetrievedData(
            sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                SettingsScreenVM.Settings.selectedSortingType.value
            )
        )
    }

    object ItemsSelectionInfo : ViewModel() {

        val isSelectionModeEnabled = mutableStateOf(false)
        val selectedLinksData = mutableStateListOf<ArchivedLinks>()
        val areAllLinksChecked = mutableStateOf(false)
        val selectedFoldersID = mutableStateListOf<Long>()
        val areAllFoldersChecked = mutableStateOf(false)
        val archiveScreenVM = ArchiveScreenVM()
    }

    fun removeAllLinksSelection() {
        ItemsSelectionInfo.selectedLinksData.removeAll(ItemsSelectionInfo.archiveScreenVM.archiveLinksData.value.archiveLinksTable.map { it })
        ItemsSelectionInfo.archiveScreenVM.archiveLinksData.value.isCheckBoxSelected.forEach {
            it.value = false
        }
    }

    fun changeAllFoldersSelectedData() {
        ItemsSelectionInfo.selectedFoldersID.removeAll(
            ItemsSelectionInfo.archiveScreenVM.archiveFoldersDataV10.value.foldersTableList.map { it.id }
        )
        ItemsSelectionInfo.archiveScreenVM.archiveFoldersDataV10.value.isCheckBoxSelected.forEach {
            it.value = false
        }
    }

    fun unArchiveMultipleFolders() {
        ItemsSelectionInfo.selectedFoldersID.forEach {
            ItemsSelectionInfo.archiveScreenVM.onUnArchiveClickV10(it)
        }
    }

    fun deleteMultipleSelectedLinks() {
        viewModelScope.launch {
            ItemsSelectionInfo.selectedLinksData.forEach {
                LocalDataBase.localDB.deleteDao().deleteALinkFromArchiveLinks(it.id)
            }
        }.invokeOnCompletion {
            removeAllLinksSelection()
        }
    }

    fun deleteMultipleSelectedFolders() {
        viewModelScope.launch {
            ItemsSelectionInfo.selectedFoldersID.forEach {
                LocalDataBase.localDB.deleteDao().deleteAFolder(it)
            }
        }.invokeOnCompletion {
            changeAllFoldersSelectedData()
        }
    }

    fun unArchiveMultipleSelectedLinks() {
        ItemsSelectionInfo.selectedLinksData.forEach {
            ItemsSelectionInfo.archiveScreenVM.onUnArchiveLinkClick(it)
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
            removeAllLinksSelection()
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

    fun changeRetrievedData(sortingPreferences: SettingsScreenVM.SortingPreferences) {
        when (sortingPreferences) {
            SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                viewModelScope.launch {
                    awaitAll(async {
                        LocalDataBase.localDB.archivedLinksSorting().sortByAToZ()
                            .collect {
                                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                List(it.size) { index ->
                                    mutableBooleanList.add(index, mutableStateOf(false))
                                }
                                _archiveLinksData.emit(
                                    ArchiveLinkTableComponent(
                                        mutableBooleanList,
                                        it
                                    )
                                )
                            }
                    }, async {
                        LocalDataBase.localDB.archivedFolderSorting().sortByAToZV10()
                            .collect {
                                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                List(it.size) { index ->
                                    mutableBooleanList.add(index, mutableStateOf(false))
                                }
                                _archiveFoldersDataV10.emit(FolderComponent(mutableBooleanList, it))
                            }
                    })
                }
            }

            SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                viewModelScope.launch {
                    awaitAll(async {
                        LocalDataBase.localDB.archivedLinksSorting().sortByZToA()
                            .collect {
                                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                List(it.size) { index ->
                                    mutableBooleanList.add(index, mutableStateOf(false))
                                }
                                _archiveLinksData.emit(
                                    ArchiveLinkTableComponent(
                                        mutableBooleanList,
                                        it
                                    )
                                )
                            }
                    }, async {
                        LocalDataBase.localDB.archivedFolderSorting().sortByZToAV10()
                            .collect {
                                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                List(it.size) { index ->
                                    mutableBooleanList.add(index, mutableStateOf(false))
                                }
                                _archiveFoldersDataV10.emit(FolderComponent(mutableBooleanList, it))
                            }
                    })
                }
            }

            SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                viewModelScope.launch {
                    awaitAll(async {
                        LocalDataBase.localDB.archivedLinksSorting()
                            .sortByLatestToOldest().collect {
                                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                List(it.size) { index ->
                                    mutableBooleanList.add(index, mutableStateOf(false))
                                }
                                _archiveLinksData.emit(
                                    ArchiveLinkTableComponent(
                                        mutableBooleanList,
                                        it
                                    )
                                )
                            }
                    }, async {
                        LocalDataBase.localDB.archivedFolderSorting()
                            .sortByLatestToOldestV10().collect {
                                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                List(it.size) { index ->
                                    mutableBooleanList.add(index, mutableStateOf(false))
                                }
                                _archiveFoldersDataV10.emit(FolderComponent(mutableBooleanList, it))
                            }
                    })
                }
            }

            SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                viewModelScope.launch {
                    awaitAll(async {
                        LocalDataBase.localDB.archivedLinksSorting()
                            .sortByOldestToLatest().collect {
                                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                List(it.size) { index ->
                                    mutableBooleanList.add(index, mutableStateOf(false))
                                }
                                _archiveLinksData.emit(
                                    ArchiveLinkTableComponent(
                                        mutableBooleanList,
                                        it
                                    )
                                )
                            }
                    }, async {
                        LocalDataBase.localDB.archivedFolderSorting()
                            .sortByOldestToLatestV10().collect {
                                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                List(it.size) { index ->
                                    mutableBooleanList.add(index, mutableStateOf(false))
                                }
                                _archiveFoldersDataV10.emit(FolderComponent(mutableBooleanList, it))
                            }
                    })
                }
            }
        }
    }

    fun onDeleteClick(
        archiveScreenType: ArchiveScreenType,
        selectedURLOrFolderName: String,
        context: Context,
        onTaskCompleted: () -> Unit
    ) {
        if (archiveScreenType == ArchiveScreenType.LINKS) {
            viewModelScope.launch {
                LocalDataBase.localDB.deleteDao()
                    .deleteALinkFromArchiveLinksV9(webURL = selectedURLOrFolderName)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context, "removed the link from archive permanently", Toast.LENGTH_SHORT
                    ).show()
                }
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {

            deleteVM.onRegularFolderDeleteClick(
                CollectionsScreenVM.selectedFolderData.value.id
            )
        }

    }

    fun onNoteDeleteCardClick(
        archiveScreenType: ArchiveScreenType,
        selectedURLOrFolderName: String,
        context: Context,
        onTaskCompleted: () -> Unit,
        folderID: Long
    ) {
        if (archiveScreenType == ArchiveScreenType.FOLDERS) {
            viewModelScope.launch {
                LocalDataBase.localDB.deleteDao().deleteAFolderNote(folderID)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {
            viewModelScope.launch {
                LocalDataBase.localDB.deleteDao()
                    .deleteANoteFromArchiveLinks(webURL = selectedURLOrFolderName)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        }
    }

    fun onUnArchiveClickV10(folderID: Long) {
        viewModelScope.launch {
            LocalDataBase.localDB.updateDao()
                .moveArchivedFolderToRegularFolderV10(folderID)
        }.invokeOnCompletion {
            changeAllFoldersSelectedData()
        }
    }
}