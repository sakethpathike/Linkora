package com.sakethh.linkora.ui.screens.collections.allLinks

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.links.LinkType
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.data.local.sorting.links.archive.ArchivedLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.folder.regular.RegularFolderLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.history.HistoryLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.important.ImportantLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.saved.SavedLinksSortingRepo
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionsScreenUIEvent
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SortingPreferences
import com.sakethh.linkora.utils.linkoraLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllLinksScreenVM @Inject constructor(
    private val linksRepo: LinksRepo,
    savedLinksSortingRepo: SavedLinksSortingRepo,
    importantLinksSortingRepo: ImportantLinksSortingRepo,
    historyLinksSortingRepo: HistoryLinksSortingRepo,
    archivedLinksSortingRepo: ArchivedLinksSortingRepo,
    regularFolderLinksSortingRepo: RegularFolderLinksSortingRepo
) : ViewModel() {


    val linkTypes = listOf(
        LinkTypeSelection(
            linkType = LinkType.SAVED_LINK,
            isChecked = mutableStateOf(false)
        ),
        LinkTypeSelection(
            linkType = LinkType.IMP_LINK,
            isChecked = mutableStateOf(false)
        ),
        LinkTypeSelection(
            linkType = LinkType.HISTORY_LINK,
            isChecked = mutableStateOf(false)
        ),
        LinkTypeSelection(
            linkType = LinkType.ARCHIVE_LINK,
            isChecked = mutableStateOf(false)
        ),
        LinkTypeSelection(
            linkType = LinkType.FOLDER_LINK,
            isChecked = mutableStateOf(false)
        ),
    )

    val savedLinks = when (SettingsPreference.selectedSortingType.value) {
        SortingPreferences.NEW_TO_OLD.name -> {
            savedLinksSortingRepo.sortByLatestToOldest()
        }

        SortingPreferences.OLD_TO_NEW.name -> {
            savedLinksSortingRepo.sortByOldestToLatest()
        }

        SortingPreferences.A_TO_Z.name -> {
            savedLinksSortingRepo.sortByAToZ()
        }

        else -> {
            savedLinksSortingRepo.sortByZToA()
        }
    }
    val importantLinks = when (SettingsPreference.selectedSortingType.value) {
        SortingPreferences.NEW_TO_OLD.name -> {
            importantLinksSortingRepo.sortByLatestToOldest()
        }

        SortingPreferences.OLD_TO_NEW.name -> {
            importantLinksSortingRepo.sortByOldestToLatest()
        }

        SortingPreferences.A_TO_Z.name -> {
            importantLinksSortingRepo.sortByAToZ()
        }

        else -> {
            importantLinksSortingRepo.sortByZToA()
        }
    }
    val historyLinks = when (SettingsPreference.selectedSortingType.value) {
        SortingPreferences.NEW_TO_OLD.name -> {
            historyLinksSortingRepo.sortByLatestToOldest()
        }

        SortingPreferences.OLD_TO_NEW.name -> {
            historyLinksSortingRepo.sortByOldestToLatest()
        }

        SortingPreferences.A_TO_Z.name -> {
            historyLinksSortingRepo.sortByAToZ()
        }

        else -> {
            historyLinksSortingRepo.sortByZToA()
        }
    }
    val archivedLinks = when (SettingsPreference.selectedSortingType.value) {
        SortingPreferences.NEW_TO_OLD.name -> {
            archivedLinksSortingRepo.sortByLatestToOldest()
        }

        SortingPreferences.OLD_TO_NEW.name -> {
            archivedLinksSortingRepo.sortByOldestToLatest()
        }

        SortingPreferences.A_TO_Z.name -> {
            archivedLinksSortingRepo.sortByAToZ()
        }

        else -> {
            archivedLinksSortingRepo.sortByZToA()
        }
    }
    val regularFoldersLinks = when (SettingsPreference.selectedSortingType.value) {
        SortingPreferences.NEW_TO_OLD.name -> {
            regularFolderLinksSortingRepo.sortByLatestToOldestV10()
        }

        SortingPreferences.OLD_TO_NEW.name -> {
            regularFolderLinksSortingRepo.sortByOldestToLatestV10()
        }

        SortingPreferences.A_TO_Z.name -> {
            regularFolderLinksSortingRepo.sortByAToZV10()
        }

        else -> {
            regularFolderLinksSortingRepo.sortByZToAV10()
        }
    }


    private val _uiChannel = Channel<CommonUiEvent>()
    val uiChannel = _uiChannel.receiveAsFlow()

    fun onUIEvent(
        specificCollectionsScreenUIEvent: SpecificCollectionsScreenUIEvent,
    ) {
        when (specificCollectionsScreenUIEvent) {
            is SpecificCollectionsScreenUIEvent.AddExistingLinkToImportantLink -> {
                linkoraLog("AddExistingLinkToImportantLink")
                viewModelScope.launch {
                    if (linksRepo.doesThisExistsInImpLinks(specificCollectionsScreenUIEvent.importantLinks.webURL)) {
                        pushUiEvent(CommonUiEvent.ShowDeleteDialogBox)
                    } else {
                        linksRepo.addANewLinkToImpLinks(
                            specificCollectionsScreenUIEvent.importantLinks
                        )
                    }
                }
            }

            is SpecificCollectionsScreenUIEvent.ArchiveAnExistingLink -> {
                linkoraLog(specificCollectionsScreenUIEvent.linkType.name)
                viewModelScope.launch {
                    linksRepo.archiveLinkTableUpdater(
                        archivedLinks = ArchivedLinks(
                            title = specificCollectionsScreenUIEvent.archivedLink.title,
                            webURL = specificCollectionsScreenUIEvent.archivedLink.webURL,
                            baseURL = specificCollectionsScreenUIEvent.archivedLink.baseURL,
                            imgURL = specificCollectionsScreenUIEvent.archivedLink.imgURL,
                            infoForSaving = specificCollectionsScreenUIEvent.archivedLink.infoForSaving
                        ), context = specificCollectionsScreenUIEvent.context, onTaskCompleted = {
                            onUIEvent(
                                SpecificCollectionsScreenUIEvent.DeleteAnExistingLink(
                                    specificCollectionsScreenUIEvent.archivedLink.id,
                                    specificCollectionsScreenUIEvent.linkType
                                )
                            )
                        }
                    )
                }
            }

            is SpecificCollectionsScreenUIEvent.DeleteAnExistingLink -> {
                linkoraLog(specificCollectionsScreenUIEvent.linkType.name)
                viewModelScope.launch {
                    when (specificCollectionsScreenUIEvent.linkType) {
                        LinkType.FOLDER_LINK, LinkType.SAVED_LINK -> {
                            linksRepo.deleteALinkFromLinksTable(
                                specificCollectionsScreenUIEvent.linkId
                            )
                        }

                        LinkType.IMP_LINK -> {
                            linksRepo.deleteALinkFromImpLinks(
                                specificCollectionsScreenUIEvent.linkId
                            )
                        }

                        LinkType.HISTORY_LINK -> {
                            linksRepo.deleteARecentlyVisitedLink(
                                specificCollectionsScreenUIEvent.linkId
                            )
                        }

                        LinkType.ARCHIVE_LINK -> {
                            linksRepo.deleteALinkFromArchiveLinks(
                                specificCollectionsScreenUIEvent.linkId
                            )
                        }
                    }
                }
            }

            is SpecificCollectionsScreenUIEvent.DeleteAnExistingNote -> {
                linkoraLog(specificCollectionsScreenUIEvent.linkType.name)
                when (specificCollectionsScreenUIEvent.linkType) {
                    LinkType.FOLDER_LINK, LinkType.SAVED_LINK -> {
                        viewModelScope.launch {
                            linksRepo.deleteANoteFromLinksTable(specificCollectionsScreenUIEvent.linkId)
                        }
                    }

                    LinkType.IMP_LINK -> {
                        viewModelScope.launch {
                            linksRepo.deleteANoteFromImportantLinks(specificCollectionsScreenUIEvent.linkId)
                        }
                    }

                    LinkType.HISTORY_LINK -> {
                        viewModelScope.launch {
                            linksRepo.deleteANoteFromRecentlyVisited(
                                specificCollectionsScreenUIEvent.linkId
                            )
                        }
                    }

                    LinkType.ARCHIVE_LINK -> {
                        viewModelScope.launch {
                            linksRepo.deleteANoteFromArchiveLinks(specificCollectionsScreenUIEvent.linkId)
                        }
                    }
                }
            }

            is SpecificCollectionsScreenUIEvent.OnLinkRefresh -> {
                linkoraLog(specificCollectionsScreenUIEvent.linkType.name)
                when (specificCollectionsScreenUIEvent.linkType) {
                    LinkType.FOLDER_LINK, LinkType.SAVED_LINK -> {
                        viewModelScope.launch {
                            linksRepo.reloadLinksTableLink(specificCollectionsScreenUIEvent.linkId)
                        }
                    }

                    LinkType.IMP_LINK -> {
                        viewModelScope.launch {
                            linksRepo.reloadImpLinksTableLink(specificCollectionsScreenUIEvent.linkId)
                        }
                    }

                    LinkType.HISTORY_LINK -> {
                        viewModelScope.launch {
                            linkoraLog(specificCollectionsScreenUIEvent.linkId.toString())
                            linksRepo.reloadHistoryLinksTableLink(
                                specificCollectionsScreenUIEvent.linkId
                            )
                        }
                    }

                    LinkType.ARCHIVE_LINK -> {
                        viewModelScope.launch {
                            linksRepo.reloadArchiveLink(specificCollectionsScreenUIEvent.linkId)
                        }
                    }
                }
            }

            is SpecificCollectionsScreenUIEvent.UpdateLinkNote -> {
                linkoraLog(specificCollectionsScreenUIEvent.linkType.name)
                when (specificCollectionsScreenUIEvent.linkType) {
                    LinkType.FOLDER_LINK, LinkType.SAVED_LINK -> {
                        viewModelScope.launch {
                            linksRepo.updateLinkInfoFromLinksTable(
                                specificCollectionsScreenUIEvent.linkId,
                                specificCollectionsScreenUIEvent.newNote
                            )
                        }
                    }

                    LinkType.IMP_LINK -> {
                        viewModelScope.launch {
                            linksRepo.updateImpLinkNote(
                                specificCollectionsScreenUIEvent.linkId,
                                specificCollectionsScreenUIEvent.newNote
                            )
                        }
                    }

                    LinkType.HISTORY_LINK -> {
                        viewModelScope.launch {
                            linksRepo.renameALinkInfoFromRecentlyVisitedLinks(
                                specificCollectionsScreenUIEvent.linkId,
                                specificCollectionsScreenUIEvent.newNote
                            )
                        }
                    }

                    LinkType.ARCHIVE_LINK -> {
                        viewModelScope.launch {
                            linksRepo.renameALinkInfoFromArchiveLinks(
                                specificCollectionsScreenUIEvent.linkId,
                                specificCollectionsScreenUIEvent.newNote
                            )
                        }
                    }
                }
            }

            is SpecificCollectionsScreenUIEvent.UpdateLinkTitle -> {
                linkoraLog(specificCollectionsScreenUIEvent.linkType.name)
                when (specificCollectionsScreenUIEvent.linkType) {
                    LinkType.FOLDER_LINK, LinkType.SAVED_LINK -> {
                        viewModelScope.launch {
                            linksRepo.updateLinkTitleFromLinksTable(
                                specificCollectionsScreenUIEvent.linkId,
                                specificCollectionsScreenUIEvent.newTitle
                            )
                        }
                    }

                    LinkType.IMP_LINK -> {
                        viewModelScope.launch {
                            linksRepo.updateImpLinkTitle(
                                specificCollectionsScreenUIEvent.linkId,
                                specificCollectionsScreenUIEvent.newTitle
                            )
                        }
                    }

                    LinkType.HISTORY_LINK -> {
                        viewModelScope.launch {
                            linksRepo.renameALinkTitleFromRecentlyVisited(
                                specificCollectionsScreenUIEvent.linkId,
                                specificCollectionsScreenUIEvent.newTitle
                            )
                        }
                    }

                    LinkType.ARCHIVE_LINK -> {
                        viewModelScope.launch {
                            linksRepo.renameALinkTitleFromArchiveLinks(
                                specificCollectionsScreenUIEvent.linkId,
                                specificCollectionsScreenUIEvent.newTitle
                            )
                        }
                    }
                }
            }

            is SpecificCollectionsScreenUIEvent.UnArchiveAnExistingLink -> {
                linkoraLog("UnArchiveAnExistingLink")
                viewModelScope.launch {
                    linksRepo.deleteALinkFromArchiveLinks(specificCollectionsScreenUIEvent.archivedLink.id)
                    linksRepo.addANewLinkToSavedLinks(
                        LinksTable(
                            title = specificCollectionsScreenUIEvent.archivedLink.title,
                            webURL = specificCollectionsScreenUIEvent.archivedLink.webURL,
                            baseURL = specificCollectionsScreenUIEvent.archivedLink.baseURL,
                            imgURL = specificCollectionsScreenUIEvent.archivedLink.imgURL,
                            infoForSaving = specificCollectionsScreenUIEvent.archivedLink.infoForSaving,
                            isLinkedWithSavedLinks = true,
                            isLinkedWithFolders = false,
                            isLinkedWithImpFolder = false,
                            keyOfImpLinkedFolder = "",
                            isLinkedWithArchivedFolder = false
                        ),
                        autoDetectTitle = false,
                        onTaskCompleted = {}
                    )
                }
            }

            else -> {}
        }
    }

    private suspend fun pushUiEvent(commonUiEvent: CommonUiEvent) {
        _uiChannel.send(commonUiEvent)
    }

}