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
import com.sakethh.linkora.localDB.commonVMs.UpdateVM
import com.sakethh.linkora.localDB.dto.ArchivedFolders
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.FoldersTable
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
    private val deleteVM: DeleteVM = DeleteVM(),
    private val updateVM: UpdateVM = UpdateVM()
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


    val isSelectionModeEnabled = mutableStateOf(false)

    val selectedLinksID = mutableStateListOf<Long>()
    val areAllLinksChecked = mutableStateOf(false)

    val selectedFoldersID = mutableStateListOf<Long>()
    val areAllFoldersChecked = mutableStateOf(false)
    fun changeAllFoldersSelectedData() {
        selectedFoldersID.removeAll(
            archiveFoldersDataV10.value.foldersTableList.map { it.id }
        )
        archiveFoldersDataV10.value.isCheckBoxSelected.forEach { it.value = false }
    }

    fun unArchiveMultipleFolders() {
        selectedFoldersID.forEach {
            onUnArchiveClickV10(it)
        }
        removeAllLinksSelection()
        changeAllFoldersSelectedData()
    }

    fun unArchiveMultipleLinks() {
        viewModelScope.launch {
            selectedFoldersID.forEach {
                LocalDataBase.localDB.updateDao().copyLinkFromArchiveTableToLinksTable(it)
                LocalDataBase.localDB.updateDao().assignLinkAsSavedLink(it)
                LocalDataBase.localDB.deleteDao().deleteALinkFromArchiveLinks(it)
            }
        }
        removeAllLinksSelection()
        changeAllFoldersSelectedData()
    }

    private fun removeAllLinksSelection() {
        selectedLinksID.removeAll(archiveLinksData.value.archiveLinksTable.map { it.id })
        archiveLinksData.value.isCheckBoxSelected.forEach { it.value = false }
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
        }
    }

    fun onUnArchiveClickV9(
        context: Context,
        archiveScreenType: ArchiveScreenType,
        selectedURLOrFolderName: String,
        selectedURLOrFolderNote: String,
        onTaskCompleted: () -> Unit
    ) {
        if (archiveScreenType == ArchiveScreenType.FOLDERS) {
            viewModelScope.launch {
                if (LocalDataBase.localDB.readDao()
                        .doesThisRootFolderExists(
                            folderName = selectedURLOrFolderName
                        )
                ) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "folder name already exists, rename any one either to unarchive this folder",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    awaitAll(async {
                        val foldersTable = FoldersTable(
                            folderName = selectedURLOrFolderName,
                            infoForSaving = selectedURLOrFolderNote,
                            parentFolderID = null,
                        )
                        foldersTable.childFolderIDs = emptyList()
                        LocalDataBase.localDB.createDao()
                            .addANewFolder(
                                foldersTable = foldersTable
                            )
                    }, async {
                        LocalDataBase.localDB.updateDao()
                            .moveArchiveFolderBackToRootFolderV9(selectedURLOrFolderName)
                    })
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "Unarchived successfully", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {
            viewModelScope.launch {
                if (LocalDataBase.localDB.readDao()
                        .doesThisExistsInSavedLinks(webURL = selectedArchivedLinkData.value.webURL)
                ) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Link already exists in \"Saved Links\"",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    LocalDataBase.localDB.createDao()
                        .addANewLinkToSavedLinksOrInFolders(
                            LinksTable(
                                title = selectedArchivedLinkData.value.title,
                                webURL = selectedArchivedLinkData.value.webURL,
                                baseURL = selectedArchivedLinkData.value.baseURL,
                                imgURL = selectedArchivedLinkData.value.imgURL,
                                infoForSaving = selectedArchivedLinkData.value.infoForSaving,
                                isLinkedWithSavedLinks = true,
                                isLinkedWithFolders = false,
                                keyOfLinkedFolderV10 = 0,
                                isLinkedWithImpFolder = false,
                                keyOfImpLinkedFolder = "",
                                isLinkedWithArchivedFolder = false,
                                keyOfArchiveLinkedFolderV10 = 0
                            )
                        )
                    LocalDataBase.localDB.deleteDao()
                        .deleteALinkFromArchiveLinksV9(selectedArchivedLinkData.value.webURL)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Unarchived and moved the link to \"Saved Links\"",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        }
    }
}