package com.sakethh.linkora.ui.screens.collections.archive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.data.local.ArchivedFolders
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.data.local.sorting.folders.archive.ParentArchivedFoldersSortingRepo
import com.sakethh.linkora.data.local.sorting.links.archive.ArchivedLinksSortingRepo
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.CustomWebTab
import com.sakethh.linkora.ui.screens.collections.CollectionsScreenVM
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SortingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArchiveScreenModal(
    val name: String,
    val screen: @Composable (navController: NavController, customWebTab: CustomWebTab) -> Unit,
)

enum class ArchiveScreenType {
    LINKS, FOLDERS
}

@HiltViewModel
class ArchiveScreenVM @Inject constructor(
    private val linksRepo: LinksRepo,
    private val foldersRepo: FoldersRepo,
    private val archivedLinksSortingRepo: ArchivedLinksSortingRepo,
    private val archivedFoldersSortingRepo: ParentArchivedFoldersSortingRepo
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
    val parentArchiveScreenData =
        listOf(
            ArchiveScreenModal(
                name = LocalizedStrings.links.value,
                screen = { navController, customWebTab ->
                    ChildArchiveScreen(
                        archiveScreenType = ArchiveScreenType.LINKS,
                        navController = navController,
                        customWebTab = customWebTab
                    )
                }), ArchiveScreenModal(
                name = LocalizedStrings.folders.value,
                screen = { navController, customWebTab ->
                    ChildArchiveScreen(
                        archiveScreenType = ArchiveScreenType.FOLDERS,
                        navController = navController,
                        customWebTab
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

    private val _channelEvents = Channel<CommonUiEvent>()
    val channelEvent = _channelEvents.receiveAsFlow()

    init {
        changeRetrievedData(
            sortingPreferences = SortingPreferences.valueOf(
                SettingsPreference.selectedSortingType.value
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
                foldersRepo.moveArchivedFolderToRegularFolderV10(it)
            }
            pushUiEvent(CommonUiEvent.ShowToast(LocalizedStrings.selectedFoldersUnarchivedSuccessfully.value))
        }
    }

    fun refreshArchivedLinkData(archiveLinkId: Long) {
        viewModelScope.launch {
            linksRepo.reloadArchiveLink(archiveLinkId)
        }
    }

    fun deleteMultipleSelectedLinks() {
        viewModelScope.launch {
            selectedLinksData.toList().forEach {
                linksRepo.deleteALinkFromArchiveLinks(it.id)
            }
            pushUiEvent(CommonUiEvent.ShowToast(LocalizedStrings.selectedLinksDeletedSuccessfully.value))
        }.invokeOnCompletion {
            selectedLinksData.clear()
        }
    }

    fun deleteMultipleSelectedFolders() {
        viewModelScope.launch {
            selectedFoldersID.toList().forEach {
                foldersRepo.deleteAFolder(it)
            }
            pushUiEvent(CommonUiEvent.ShowToast(LocalizedStrings.selectedFoldersDeletedSuccessfully.value))
        }.invokeOnCompletion {
            selectedFoldersID.clear()
        }
    }

    fun unArchiveMultipleSelectedLinks() {
        viewModelScope.launch {
            selectedLinksData.toList().forEach { archivedLink ->
                linksRepo.addANewLinkToSavedLinks(
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
                    ), onTaskCompleted = {}, autoDetectTitle = false
                )
                linksRepo.deleteALinkFromArchiveLinks(archivedLink.id)
            }
            pushUiEvent(CommonUiEvent.ShowToast(LocalizedStrings.selectedLinksUnarchivedSuccessfully.value))
        }.invokeOnCompletion {
            selectedLinksData.clear()
        }
    }

    fun onUnArchiveLinkClick(archivedLink: ArchivedLinks) {
        viewModelScope.launch {
            linksRepo.addANewLinkToSavedLinks(
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
                ), onTaskCompleted = {}, autoDetectTitle = false
            )
            linksRepo.deleteALinkFromArchiveLinks(archivedLink.id)
            pushUiEvent(CommonUiEvent.ShowToast(LocalizedStrings.linkUnarchivedSuccessfully.value))
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
                linksRepo
                    .renameALinkInfoFromArchiveLinks(webURL, newNote)
                pushUiEvent(CommonUiEvent.ShowToast(LocalizedStrings.linkInfoUpdatedSuccessfully.value))
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {
            viewModelScope.launch {
                foldersRepo.updateAFolderNote(folderID, newNote)
                pushUiEvent(CommonUiEvent.ShowToast(LocalizedStrings.folderInfoUpdatedSuccessfully.value))
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
                linksRepo
                    .renameALinkTitleFromArchiveLinks(webURL = webURL, newTitle = newTitle)
                pushUiEvent(CommonUiEvent.ShowToast(LocalizedStrings.linkInfoUpdatedSuccessfully.value))
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {
            viewModelScope.launch {
                foldersRepo.updateAFolderName(folderID, newTitle)
                pushUiEvent(CommonUiEvent.ShowToast(LocalizedStrings.folderInfoUpdatedSuccessfully.value))
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        }
    }

    fun changeRetrievedData(sortingPreferences: SortingPreferences) {
        when (sortingPreferences) {
            SortingPreferences.A_TO_Z -> {
                viewModelScope.launch {
                    awaitAll(async {
                        archivedLinksSortingRepo.sortByAToZ()
                            .collect {
                                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                List(it.size) { index ->
                                    mutableBooleanList.add(index, mutableStateOf(false))
                                }
                                _archiveLinksData.emit(
                                    it
                                )
                            }
                    }, async {
                        archivedFoldersSortingRepo.sortByAToZV10()
                            .collect {
                                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                List(it.size) { index ->
                                    mutableBooleanList.add(index, mutableStateOf(false))
                                }
                                _archiveFoldersDataV10.emit(it)
                            }
                    })
                }
            }

            SortingPreferences.Z_TO_A -> {
                viewModelScope.launch {
                    awaitAll(async {
                        archivedLinksSortingRepo.sortByZToA()
                            .collect {
                                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                List(it.size) { index ->
                                    mutableBooleanList.add(index, mutableStateOf(false))
                                }
                                _archiveLinksData.emit(
                                    it
                                )
                            }
                    }, async {
                        archivedFoldersSortingRepo.sortByZToAV10()
                            .collect {
                                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                List(it.size) { index ->
                                    mutableBooleanList.add(index, mutableStateOf(false))
                                }
                                _archiveFoldersDataV10.emit(it)
                            }
                    })
                }
            }

            SortingPreferences.NEW_TO_OLD -> {
                viewModelScope.launch {
                    awaitAll(async {
                        archivedLinksSortingRepo
                            .sortByLatestToOldest().collect {
                                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                List(it.size) { index ->
                                    mutableBooleanList.add(index, mutableStateOf(false))
                                }
                                _archiveLinksData.emit(
                                    it
                                )
                            }
                    }, async {
                        archivedFoldersSortingRepo
                            .sortByLatestToOldestV10().collect {
                                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                List(it.size) { index ->
                                    mutableBooleanList.add(index, mutableStateOf(false))
                                }
                                _archiveFoldersDataV10.emit(it)
                            }
                    })
                }
            }

            SortingPreferences.OLD_TO_NEW -> {
                viewModelScope.launch {
                    awaitAll(async {
                        archivedLinksSortingRepo
                            .sortByOldestToLatest().collect {
                                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                List(it.size) { index ->
                                    mutableBooleanList.add(index, mutableStateOf(false))
                                }
                                _archiveLinksData.emit(
                                    it
                                )
                            }
                    }, async {
                        archivedFoldersSortingRepo
                            .sortByOldestToLatestV10().collect {
                                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                List(it.size) { index ->
                                    mutableBooleanList.add(index, mutableStateOf(false))
                                }
                                _archiveFoldersDataV10.emit(it)
                            }
                    })
                }
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
                linksRepo
                    .deleteALinkFromArchiveLinksV9(webURL = selectedURLOrFolderName)
                pushUiEvent(CommonUiEvent.ShowToast(LocalizedStrings.archivedLinkDeletedSuccessfully.value))
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {
            viewModelScope.launch {
                foldersRepo.deleteAFolder(
                    CollectionsScreenVM.selectedFolderData.value.id
                )
            }
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
                foldersRepo.deleteAFolderNote(folderID)
                pushUiEvent(CommonUiEvent.ShowToast(LocalizedStrings.deletedTheNoteSuccessfully.value))
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        } else {
            viewModelScope.launch {
                linksRepo
                    .deleteANoteFromArchiveLinks(webURL = selectedURLOrFolderName)
                pushUiEvent(CommonUiEvent.ShowToast(LocalizedStrings.deletedTheNoteSuccessfully.value))
            }.invokeOnCompletion {
                onTaskCompleted()
            }
        }
    }

    fun onUnArchiveClickV10(folderID: Long) {
        viewModelScope.launch {
            foldersRepo
                .moveArchivedFolderToRegularFolderV10(folderID)
            pushUiEvent(CommonUiEvent.ShowToast(LocalizedStrings.folderUnarchivedSuccessfully.value))
        }.invokeOnCompletion {
            selectedFoldersID.clear()
        }
    }

    private suspend fun pushUiEvent(commonUiEvent: CommonUiEvent) {
        _channelEvents.send(commonUiEvent)
    }
}