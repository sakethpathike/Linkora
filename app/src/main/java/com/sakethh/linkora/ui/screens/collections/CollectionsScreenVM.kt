package com.sakethh.linkora.ui.screens.collections


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.data.local.shelf.ShelfRepo
import com.sakethh.linkora.data.local.sorting.folders.regular.ParentRegularFoldersSortingRepo
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.OptionsBtmSheetType
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenUIEvent
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenVM
import com.sakethh.linkora.ui.screens.search.SearchScreenVM
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class CollectionsScreenVM @Inject constructor(
    private val foldersRepo: FoldersRepo,
    private val linksRepo: LinksRepo,
    private val parentRegularFoldersSortingRepo: ParentRegularFoldersSortingRepo,
    private val shelfRepo: ShelfRepo
) : ViewModel() {
    private val _foldersData = MutableStateFlow(
        emptyList<FoldersTable>()
    )
    val foldersData = _foldersData.asStateFlow()

    val selectedFoldersData = mutableStateListOf<FoldersTable>()
    val areAllFoldersChecked = mutableStateOf(false)

    private val _eventChannel = Channel<CommonUiEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    fun changeAllFoldersSelectedData(
        folderComponent: List<FoldersTable> = emptyList()
    ) {
        if (areAllFoldersChecked.value) {
            selectedFoldersData.addAll(foldersData.value.map { it })
        } else {
            if (folderComponent.isEmpty()) {
                selectedFoldersData.removeAll(foldersData.value.map { it })
            } else {
                selectedFoldersData.removeAll(
                    folderComponent.map { it }
                )
            }
        }
    }

    open fun onDeleteMultipleSelectedFolders() {
        SpecificCollectionsScreenVM.selectedBtmSheetType.value = OptionsBtmSheetType.FOLDER
        viewModelScope.launch {
            awaitAll(async {
                selectedFoldersData.toList().forEach {
                    shelfRepo.deleteAFolderFromShelf(it.id)
                }
            }, async {
                selectedFoldersData.toList().forEach { folder ->
                    folder.childFolderIDs?.toTypedArray()
                        ?.let {
                            foldersRepo.deleteMultipleFolders(it)
                        }
                    folder.childFolderIDs?.toTypedArray()
                        ?.let {
                            linksRepo.deleteMultipleLinksFromLinksTable(it)
                        }
                    linksRepo.deleteThisFolderLinksV10(folder.id)
                    foldersRepo.deleteAFolder(folder.id)
                    shelfRepo.deleteAFolderFromShelf(folder.id)
                }
            }, async {
                SearchScreenVM.selectedArchiveFoldersData.toList().forEach { folder ->
                    folder.childFolderIDs?.toTypedArray()
                        ?.let { foldersRepo.deleteMultipleFolders(it) }
                    folder.childFolderIDs?.toTypedArray()
                        ?.let {
                            linksRepo.deleteMultipleLinksFromLinksTable(it)
                        }
                    linksRepo.deleteThisFolderLinksV10(folder.id)
                    foldersRepo.deleteAFolder(folder.id)
                    shelfRepo.deleteAFolderFromShelf(folder.id)
                }
            })
            pushAUIEvent(CommonUiEvent.ShowToast("Successfully deleted selected folders"))
        }
    }

    fun archiveSelectedMultipleFolders() {
        viewModelScope.launch {
            foldersRepo
                .moveAMultipleFoldersToArchivesV10(selectedFoldersData.toList().map { it.id }
                    .toTypedArray())
            pushAUIEvent(CommonUiEvent.ShowToast("Selected folders archived successfully"))
        }
    }

    companion object {
        var rootFolderID: Long = 0
        val selectedFolderData = mutableStateOf(
            FoldersTable(
                folderName = "",
                infoForSaving = "",
                parentFolderID = 0
            )
        )
        val currentClickedFolderData = mutableStateOf(
            FoldersTable(
                folderName = "",
                infoForSaving = "",
                parentFolderID = 0
            )
        )
    }

    init {
        changeRetrievedFoldersData(
            sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(
                SettingsScreenVM.Settings.selectedSortingType.value
            )
        )
    }

    fun changeRetrievedFoldersData(sortingPreferences: SettingsScreenVM.SortingPreferences) {
        when (sortingPreferences) {
            SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                viewModelScope.launch {
                    parentRegularFoldersSortingRepo.sortByAToZ()
                        .collect {
                            val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                            List(it.size) { index ->
                                mutableBooleanList.add(index, mutableStateOf(false))
                            }
                            _foldersData.emit(
                                it
                            )
                        }
                }
            }

            SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                viewModelScope.launch {
                    parentRegularFoldersSortingRepo.sortByZToA()
                        .collect {
                            val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                            List(it.size) { index ->
                                mutableBooleanList.add(index, mutableStateOf(false))
                            }
                            _foldersData.emit(
                                it
                            )
                        }
                }
            }

            SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                viewModelScope.launch {
                    parentRegularFoldersSortingRepo
                        .sortByLatestToOldest()
                        .collect {
                            val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                            List(it.size) { index ->
                                mutableBooleanList.add(index, mutableStateOf(false))
                            }
                            _foldersData.emit(
                                it
                            )
                        }
                }
            }

            SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                viewModelScope.launch {
                    parentRegularFoldersSortingRepo
                        .sortByOldestToLatest()
                        .collect {
                            val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                            List(it.size) { index ->
                                mutableBooleanList.add(index, mutableStateOf(false))
                            }
                            _foldersData.emit(
                                it
                            )
                        }
                }
            }
        }
    }

    fun onUiEvent(specificCollectionsScreenUIEvent: SpecificCollectionsScreenUIEvent) {
        when (specificCollectionsScreenUIEvent) {
            is SpecificCollectionsScreenUIEvent.AddANewLinkInAFolder -> {
                viewModelScope.launch {
                    linksRepo.addANewLinkInAFolder(
                        linksTable = LinksTable(
                            title = specificCollectionsScreenUIEvent.title,
                            webURL = specificCollectionsScreenUIEvent.webURL,
                            baseURL = specificCollectionsScreenUIEvent.webURL,
                            imgURL = "",
                            infoForSaving = specificCollectionsScreenUIEvent.noteForSaving,
                            isLinkedWithSavedLinks = false,
                            isLinkedWithFolders = true,
                            isLinkedWithImpFolder = false,
                            isLinkedWithArchivedFolder = false,
                            keyOfLinkedFolder = specificCollectionsScreenUIEvent.folderName,
                            keyOfLinkedFolderV10 = specificCollectionsScreenUIEvent.folderID,
                            keyOfImpLinkedFolder = ""
                        ),
                        onTaskCompleted = specificCollectionsScreenUIEvent.onTaskCompleted,
                        autoDetectTitle = specificCollectionsScreenUIEvent.autoDetectTitle
                    )
                    pushAUIEvent(CommonUiEvent.ShowToast("New link added to \"${specificCollectionsScreenUIEvent.folderName}\""))
                }
            }

            is SpecificCollectionsScreenUIEvent.AddANewLinkInImpLinks -> {
                viewModelScope.launch {
                    linksRepo.addANewLinkToImpLinks(
                        importantLink = ImportantLinks(
                            title = specificCollectionsScreenUIEvent.title,
                            webURL = specificCollectionsScreenUIEvent.webURL,
                            baseURL = specificCollectionsScreenUIEvent.webURL,
                            imgURL = "",
                            infoForSaving = specificCollectionsScreenUIEvent.noteForSaving
                        ),
                        autoDetectTitle = specificCollectionsScreenUIEvent.autoDetectTitle,
                        onTaskCompleted = specificCollectionsScreenUIEvent.onTaskCompleted
                    )
                    pushAUIEvent(CommonUiEvent.ShowToast("New link added to \"Important Links\""))
                }
            }

            is SpecificCollectionsScreenUIEvent.AddANewLinkInSavedLinks -> {
                viewModelScope.launch {
                    linksRepo.addANewLinkToSavedLinks(
                        linksTable = LinksTable(
                            title = specificCollectionsScreenUIEvent.title,
                            webURL = specificCollectionsScreenUIEvent.webURL,
                            baseURL = specificCollectionsScreenUIEvent.webURL,
                            imgURL = "",
                            infoForSaving = specificCollectionsScreenUIEvent.noteForSaving,
                            isLinkedWithSavedLinks = true,
                            isLinkedWithFolders = false,
                            isLinkedWithImpFolder = false,
                            isLinkedWithArchivedFolder = false,
                            keyOfLinkedFolder = null,
                            keyOfImpLinkedFolderV10 = null,
                            keyOfImpLinkedFolder = ""
                        ),
                        onTaskCompleted = specificCollectionsScreenUIEvent.onTaskCompleted,
                        autoDetectTitle = specificCollectionsScreenUIEvent.autoDetectTitle
                    )
                    pushAUIEvent(CommonUiEvent.ShowToast("New link added to \"Saved Links\""))
                }
            }

            is SpecificCollectionsScreenUIEvent.ArchiveAFolder -> {
                viewModelScope.launch {
                    foldersRepo.moveAFolderToArchive(specificCollectionsScreenUIEvent.folderId)
                    pushAUIEvent(CommonUiEvent.ShowToast("Folder archived successfully"))
                }
            }

            is SpecificCollectionsScreenUIEvent.UpdateFolderName -> {
                viewModelScope.launch {
                    foldersRepo.updateAFolderName(
                        specificCollectionsScreenUIEvent.folderId,
                        specificCollectionsScreenUIEvent.folderName
                    )
                    pushAUIEvent(CommonUiEvent.ShowToast("Folder info updated successfully"))
                }
            }

            is SpecificCollectionsScreenUIEvent.UpdateFolderNote -> {
                viewModelScope.launch {
                    foldersRepo.updateAFolderNote(
                        specificCollectionsScreenUIEvent.folderId,
                        specificCollectionsScreenUIEvent.newFolderNote
                    )
                    pushAUIEvent(CommonUiEvent.ShowToast("Folder info updated successfully"))
                }
            }

            is SpecificCollectionsScreenUIEvent.UpdateImpLinkNote -> {
                viewModelScope.launch {
                    linksRepo.updateImpLinkNote(
                        specificCollectionsScreenUIEvent.linkId,
                        specificCollectionsScreenUIEvent.newNote
                    )
                    pushAUIEvent(CommonUiEvent.ShowToast("Link info updated successfully"))
                }
            }

            is SpecificCollectionsScreenUIEvent.UpdateImpLinkTitle -> {
                viewModelScope.launch {
                    linksRepo.updateImpLinkTitle(
                        specificCollectionsScreenUIEvent.linkId,
                        specificCollectionsScreenUIEvent.title
                    )
                    pushAUIEvent(CommonUiEvent.ShowToast("Link info updated successfully"))
                }
            }

            is SpecificCollectionsScreenUIEvent.UpdateRegularLinkNote -> {
                viewModelScope.launch {
                    linksRepo.updateLinkInfoFromLinksTable(
                        specificCollectionsScreenUIEvent.linkId,
                        specificCollectionsScreenUIEvent.newNote
                    )
                    pushAUIEvent(CommonUiEvent.ShowToast("Link info updated successfully"))
                }
            }

            is SpecificCollectionsScreenUIEvent.UpdateRegularLinkTitle -> {
                viewModelScope.launch {
                    linksRepo.updateLinkTitleFromLinksTable(
                        specificCollectionsScreenUIEvent.linkId,
                        specificCollectionsScreenUIEvent.title
                    )
                    pushAUIEvent(CommonUiEvent.ShowToast("Link info updated successfully"))
                }
            }

            is SpecificCollectionsScreenUIEvent.CreateANewFolder -> {
                viewModelScope.launch {
                    foldersRepo.createANewFolder(specificCollectionsScreenUIEvent.foldersTable)
                    pushAUIEvent(CommonUiEvent.ShowToast("Folder  \"${specificCollectionsScreenUIEvent.foldersTable.folderName}\" created successfully"))
                }
            }

            is SpecificCollectionsScreenUIEvent.DeleteAFolder -> {
                viewModelScope.launch {
                    foldersRepo.deleteAFolder(specificCollectionsScreenUIEvent.folderId)
                    pushAUIEvent(CommonUiEvent.ShowToast("deleted the folder"))
                }
            }

            is SpecificCollectionsScreenUIEvent.AddExistingLinkToImportantLink -> {
                viewModelScope.launch {
                    if (linksRepo.doesThisExistsInImpLinks(specificCollectionsScreenUIEvent.importantLinks.webURL)) {
                        linksRepo.deleteALinkFromImpLinksBasedOnURL(specificCollectionsScreenUIEvent.importantLinks.webURL)
                        pushAUIEvent(CommonUiEvent.ShowToast("Removed link from \"Important Links\" successfully"))
                    } else {
                        linksRepo.addANewLinkToImpLinks(
                            specificCollectionsScreenUIEvent.importantLinks,
                            onTaskCompleted = {
                                this.launch {
                                    pushAUIEvent(CommonUiEvent.ShowToast("Added link to \"Important Links\""))
                                }
                            },
                            autoDetectTitle = false
                        )
                    }
                }
            }
        }
    }

    fun onNoteDeleteClick(clickedFolderID: Long) {
        viewModelScope.launch {
            foldersRepo
                .deleteAFolderNote(folderID = clickedFolderID)
            pushAUIEvent(CommonUiEvent.ShowToast("deleted the note"))
        }
    }

    protected suspend fun pushAUIEvent(commonUiEvent: CommonUiEvent) {
        _eventChannel.send(commonUiEvent)
    }

}