package com.sakethh.linkora.ui.screens.collections.specific

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.UriHandler
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.data.local.panels.PanelsRepo
import com.sakethh.linkora.data.local.sorting.folders.regular.ParentRegularFoldersSortingRepo
import com.sakethh.linkora.data.local.sorting.folders.subfolders.SubFoldersSortingRepo
import com.sakethh.linkora.data.local.sorting.links.folder.archive.ArchivedFolderLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.folder.regular.RegularFolderLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.important.ImportantLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.saved.SavedLinksSortingRepo
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.CustomWebTab
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.OptionsBtmSheetType
import com.sakethh.linkora.ui.screens.collections.CollectionsScreenVM
import com.sakethh.linkora.ui.screens.home.HomeScreenVM
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SortingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MutableImportantLinks(
    val title: MutableState<String>,
    val webURL: MutableState<String>,
    val baseURL: MutableState<String>,
    val imgURL: MutableState<String>,
    val infoForSaving: MutableState<String>,
    var id: Long = 0,
)

@HiltViewModel
open class SpecificCollectionsScreenVM @Inject constructor(
    private val linksRepo: LinksRepo,
    private val foldersRepo: FoldersRepo,
    private val savedLinksSortingRepo: SavedLinksSortingRepo,
    private val importantLinksSortingRepo: ImportantLinksSortingRepo,
    val folderLinksSortingRepo: RegularFolderLinksSortingRepo,
    private val archiveFolderLinksSortingRepo: ArchivedFolderLinksSortingRepo,
    val subFoldersSortingRepo: SubFoldersSortingRepo,
    private val regularFoldersSortingRepo: ParentRegularFoldersSortingRepo,
    parentRegularFoldersSortingRepo: ParentRegularFoldersSortingRepo,
    panelsRepo: PanelsRepo,
    private val customWebTab: CustomWebTab
) : CollectionsScreenVM(foldersRepo, linksRepo, parentRegularFoldersSortingRepo, panelsRepo) {


    private val _folderLinksData = MutableStateFlow(
        emptyList<LinksTable>()
    )
    val folderLinksData = _folderLinksData.asStateFlow()

    private val _childFoldersData = MutableStateFlow(emptyList<FoldersTable>())
    val childFoldersData = _childFoldersData.asStateFlow()

    private val _savedLinksData = MutableStateFlow(
        emptyList<LinksTable>()
    )
    val savedLinksTable = _savedLinksData.asStateFlow()

    private val _impLinksData = MutableStateFlow(
        emptyList<ImportantLinks>()
    )
    val impLinksTable = _impLinksData.asStateFlow()

    private val _archiveFolderLinksData =
        MutableStateFlow(emptyList<LinksTable>())
    val archiveFoldersLinksData = _archiveFolderLinksData.asStateFlow()

    private val _archiveSubFolderData = MutableStateFlow(emptyList<FoldersTable>())
    val archiveSubFolderData = _archiveSubFolderData.asStateFlow()


    val selectedLinksID = mutableStateListOf<Long>()
    val selectedImpLinks = mutableStateListOf<String>()
    val areAllLinksChecked = mutableStateOf(false)
    fun removeAllLinkSelections() {
        when (screenType.value) {
            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                selectedLinksID.removeAll(savedLinksTable.value.map { it.id })
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                selectedLinksID.removeAll(folderLinksData.value.map { it.id })
            }

            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                selectedLinksID.removeAll(impLinksTable.value.map { it.id })
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                selectedLinksID.removeAll(archiveFoldersLinksData.value.map { it.id })
            }

            else -> {}
        }
    }

    val impLinkDataForBtmSheet = MutableImportantLinks(
        title = mutableStateOf(""),
        webURL = mutableStateOf(""),
        baseURL = mutableStateOf(""),
        imgURL = mutableStateOf(""),
        infoForSaving = mutableStateOf("")
    )

    companion object {
        val screenType = mutableStateOf(SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN)
        val selectedBtmSheetType = mutableStateOf(OptionsBtmSheetType.LINK)
        val inARegularFolder = mutableStateOf(true)
    }

    open fun reloadLinkData(linkID: Long, homeScreenType: HomeScreenVM.HomeScreenType) {
        viewModelScope.launch {
            if (homeScreenType == HomeScreenVM.HomeScreenType.IMP_LINKS) {
                linksRepo.reloadImpLinksTableLink(
                    linkID
                )
            } else {
                when (screenType.value) {
                    SpecificScreenType.SAVED_LINKS_SCREEN, SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN, SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> linksRepo.reloadLinksTableLink(
                        linkID
                    )

                    SpecificScreenType.IMPORTANT_LINKS_SCREEN -> linksRepo.reloadImpLinksTableLink(
                        linkID
                    )

                    else -> {}
                }
            }
        }
    }

    private fun retrieveChildFoldersData() {
        viewModelScope.launch {
            foldersRepo.getChildFoldersOfThisParentID(
                currentClickedFolderData.value.id
            ).collectLatest { it ->
                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                List(it.size) { index ->
                    mutableBooleanList.add(index, mutableStateOf(false))
                }
                _childFoldersData.emit(
                    it
                )
            }
        }
    }

    fun moveMultipleLinksFromImpLinksToArchive() {
        viewModelScope.launch {
            selectedImpLinks.toList().forEach {
                linksRepo
                    .copyLinkFromImpTableToArchiveLinks(it)
                linksRepo.deleteALinkFromImpLinksBasedOnURL(it)
            }
        }
    }

    fun moveMultipleLinksFromLinksTableToArchive() {
        viewModelScope.launch {
            selectedLinksID.toList().forEach {
                linksRepo
                    .copyLinkFromLinksTableToArchiveLinks(it)
                linksRepo.deleteALinkFromLinksTable(it)
            }
        }
    }

    fun updateFolderData(folderID: Long) {
        viewModelScope.launch {
            currentClickedFolderData.value =
                foldersRepo.getThisFolderData(folderID)
        }
    }

    init {
        viewModelScope.launch {
            changeRetrievedData(
                sortingPreferences = SortingPreferences.valueOf(SettingsPreference.selectedSortingType.value),
                folderID = currentClickedFolderData.value.id,
                isFoldersSortingSelected = true,
                isLinksSortingSelected = true
            )
            retrieveChildFoldersData()
        }
    }

    fun changeTheParentIdOfASpecificFolder(sourceFolderId: List<Long>, targetParentId: Long?) {
        viewModelScope.launch {
            foldersRepo.changeTheParentIdOfASpecificFolder(sourceFolderId, targetParentId)
        }
    }

    fun changeRetrievedData(
        sortingPreferences: SortingPreferences,
        folderID: Long,
        screenType: SpecificScreenType = Companion.screenType.value,
        isFoldersSortingSelected: Boolean = false,
        isLinksSortingSelected: Boolean = false
    ) {
        when (screenType) {
            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                when (sortingPreferences) {
                    SortingPreferences.A_TO_Z -> {
                        viewModelScope.launch {
                            savedLinksSortingRepo.sortByAToZ().collectLatest {
                                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                List(it.size) { index ->
                                    mutableBooleanList.add(index, mutableStateOf(false))
                                }
                                _savedLinksData.emit(
                                    it
                                )
                            }
                        }
                    }

                    SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            savedLinksSortingRepo.sortByZToA().collectLatest {
                                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                List(it.size) { index ->
                                    mutableBooleanList.add(index, mutableStateOf(false))
                                }
                                _savedLinksData.emit(
                                    it
                                )
                            }
                        }
                    }

                    SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            savedLinksSortingRepo.sortByLatestToOldest()
                                .collectLatest {
                                    val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                    List(it.size) { index ->
                                        mutableBooleanList.add(index, mutableStateOf(false))
                                    }
                                    _savedLinksData.emit(
                                        it
                                    )
                                }
                        }
                    }

                    SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            savedLinksSortingRepo.sortByOldestToLatest()
                                .collectLatest {
                                    val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                    List(it.size) { index ->
                                        mutableBooleanList.add(index, mutableStateOf(false))
                                    }
                                    _savedLinksData.emit(
                                        it
                                    )
                                }
                        }
                    }
                }
            }

            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                when (sortingPreferences) {
                    SortingPreferences.A_TO_Z -> {
                        viewModelScope.launch {
                            importantLinksSortingRepo.sortByAToZ()
                                .collectLatest {
                                    val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                    List(it.size) { index ->
                                        mutableBooleanList.add(index, mutableStateOf(false))
                                    }
                                    _impLinksData.emit(
                                        it
                                    )
                                }
                        }
                    }

                    SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            importantLinksSortingRepo.sortByZToA()
                                .collectLatest {
                                    val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                    List(it.size) { index ->
                                        mutableBooleanList.add(index, mutableStateOf(false))
                                    }
                                    _impLinksData.emit(
                                        it
                                    )
                                }
                        }
                    }

                    SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            importantLinksSortingRepo.sortByLatestToOldest()
                                .collectLatest {
                                    val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                    List(it.size) { index ->
                                        mutableBooleanList.add(index, mutableStateOf(false))
                                    }
                                    _impLinksData.emit(
                                        it
                                    )
                                }
                        }
                    }

                    SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            importantLinksSortingRepo.sortByOldestToLatest()
                                .collectLatest {
                                    val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                    List(it.size) { index ->
                                        mutableBooleanList.add(index, mutableStateOf(false))
                                    }
                                    _impLinksData.emit(
                                        it
                                    )
                                }
                        }
                    }
                }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                when (sortingPreferences) {
                    SortingPreferences.A_TO_Z -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                if (isLinksSortingSelected) {
                                    archiveFolderLinksSortingRepo
                                        .sortLinksByAToZV10(folderID = folderID).collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _archiveFolderLinksData.emit(
                                                it
                                            )
                                        }
                                }
                            }, async {
                                if (isFoldersSortingSelected) {
                                    subFoldersSortingRepo
                                        .sortSubFoldersByAToZ(parentFolderID = currentClickedFolderData.value.id)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _archiveSubFolderData.emit(
                                                it
                                            )
                                        }
                                }
                            })
                        }
                    }

                    SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                if (isLinksSortingSelected) {
                                    archiveFolderLinksSortingRepo
                                        .sortLinksByZToAV10(folderID = folderID).collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _archiveFolderLinksData.emit(
                                                it
                                            )
                                        }
                                }
                            }, async {
                                if (isFoldersSortingSelected) {
                                    subFoldersSortingRepo
                                        .sortSubFoldersByZToA(parentFolderID = currentClickedFolderData.value.id)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _archiveSubFolderData.emit(
                                                it
                                            )
                                        }
                                }
                            })

                        }
                    }

                    SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                if (isLinksSortingSelected) {
                                    archiveFolderLinksSortingRepo
                                        .sortLinksByLatestToOldestV10(folderID = folderID)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _archiveFolderLinksData.emit(
                                                it
                                            )
                                        }
                                }
                            }, async {
                                if (isFoldersSortingSelected) {
                                    subFoldersSortingRepo
                                        .sortSubFoldersByLatestToOldest(parentFolderID = currentClickedFolderData.value.id)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _archiveSubFolderData.emit(
                                                it
                                            )
                                        }
                                }
                            })
                        }
                    }

                    SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                if (isLinksSortingSelected) {
                                    archiveFolderLinksSortingRepo
                                        .sortLinksByOldestToLatestV10(folderID = folderID)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _archiveFolderLinksData.emit(
                                                it
                                            )
                                        }
                                }
                            }, async {
                                if (isFoldersSortingSelected) {
                                    subFoldersSortingRepo
                                        .sortSubFoldersByOldestToLatest(parentFolderID = currentClickedFolderData.value.id)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _archiveSubFolderData.emit(
                                                it
                                            )
                                        }
                                }
                            })
                        }
                    }
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                when (sortingPreferences) {
                    SortingPreferences.A_TO_Z -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                if (isLinksSortingSelected) {
                                    folderLinksSortingRepo
                                        .sortByAToZV10(folderID = folderID).collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _folderLinksData.emit(
                                                it
                                            )
                                        }
                                }
                            }, async {
                                if (isFoldersSortingSelected) {
                                    subFoldersSortingRepo
                                        .sortSubFoldersByAToZ(parentFolderID = folderID)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _childFoldersData.emit(
                                                it
                                            )
                                        }
                                }
                            })
                        }
                    }

                    SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                if (isLinksSortingSelected) {
                                    folderLinksSortingRepo
                                        .sortByZToAV10(folderID = folderID).collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _folderLinksData.emit(
                                                it
                                            )
                                        }
                                }
                            }, async {
                                if (isFoldersSortingSelected) {
                                    subFoldersSortingRepo
                                        .sortSubFoldersByZToA(parentFolderID = folderID)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _childFoldersData.emit(
                                                it
                                            )
                                        }
                                }
                            })
                        }
                    }

                    SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                if (isLinksSortingSelected) {
                                    folderLinksSortingRepo
                                        .sortByLatestToOldestV10(folderID = folderID)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _folderLinksData.emit(
                                                it
                                            )
                                        }
                                }
                            }, async {
                                if (isFoldersSortingSelected) {
                                    subFoldersSortingRepo
                                        .sortSubFoldersByLatestToOldest(parentFolderID = folderID)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _childFoldersData.emit(
                                                it
                                            )
                                        }
                                }
                            })
                        }
                    }

                    SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                if (isLinksSortingSelected) {
                                    folderLinksSortingRepo
                                        .sortByOldestToLatestV10(folderID = folderID)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _folderLinksData.emit(
                                                it
                                            )
                                        }
                                }
                            }, async {
                                if (isFoldersSortingSelected) {
                                    subFoldersSortingRepo
                                        .sortSubFoldersByOldestToLatest(parentFolderID = folderID)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _childFoldersData.emit(
                                                it
                                            )
                                        }
                                }
                            })
                        }
                    }
                }
            }

            SpecificScreenType.INTENT_ACTIVITY -> {

            }

            SpecificScreenType.ROOT_SCREEN -> {

            }

            SpecificScreenType.ALL_LINKS_SCREEN -> {

            }
        }
    }

    fun onArchiveClick(
        tempImpLinkData: ImportantLinks, context: Context, linkID: Long, onTaskCompleted: () -> Unit
    ) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    awaitAll(async {
                        linksRepo.archiveLinkTableUpdater(
                            archivedLinks = ArchivedLinks(
                                title = tempImpLinkData.title,
                                webURL = tempImpLinkData.webURL,
                                baseURL = tempImpLinkData.baseURL,
                                imgURL = tempImpLinkData.imgURL,
                                infoForSaving = tempImpLinkData.infoForSaving
                            ), context = context, onTaskCompleted = {
                                onTaskCompleted()
                            })
                    }, async {
                        linksRepo
                            .deleteALinkFromImpLinksBasedOnURL(tempImpLinkData.webURL)
                    })
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                viewModelScope.launch {
                    awaitAll(async {
                        linksRepo.archiveLinkTableUpdater(
                            archivedLinks = ArchivedLinks(
                                title = tempImpLinkData.title,
                                webURL = tempImpLinkData.webURL,
                                baseURL = tempImpLinkData.baseURL,
                                imgURL = tempImpLinkData.imgURL,
                                infoForSaving = tempImpLinkData.infoForSaving
                            ), context = context, onTaskCompleted = {
                                onTaskCompleted()
                            })
                    })
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                viewModelScope.launch {
                    awaitAll(async {
                        linksRepo.archiveLinkTableUpdater(
                            archivedLinks = ArchivedLinks(
                                title = tempImpLinkData.title,
                                webURL = tempImpLinkData.webURL,
                                baseURL = tempImpLinkData.baseURL,
                                imgURL = tempImpLinkData.imgURL,
                                infoForSaving = tempImpLinkData.infoForSaving
                            ), context = context, onTaskCompleted = {
                                onTaskCompleted()
                            })
                    }, async {
                        linksRepo
                            .deleteALinkFromSavedLinksBasedOnURL(webURL = tempImpLinkData.webURL)
                    })
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                viewModelScope.launch {
                    awaitAll(async {
                        linksRepo.archiveLinkTableUpdater(
                            archivedLinks = ArchivedLinks(
                                title = tempImpLinkData.title,
                                webURL = tempImpLinkData.webURL,
                                baseURL = tempImpLinkData.baseURL,
                                imgURL = tempImpLinkData.imgURL,
                                infoForSaving = tempImpLinkData.infoForSaving
                            ), context = context, onTaskCompleted = {
                                onTaskCompleted()
                            })
                    }, async {
                        linksRepo.deleteALinkFromLinksTable(linkID)
                    })
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            else -> {}
        }
    }

    fun onDeleteMultipleSelectedLinks() {
        selectedBtmSheetType.value = OptionsBtmSheetType.LINK
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    selectedImpLinks.toList().forEach {
                        linksRepo.deleteALinkFromImpLinksBasedOnURL(it)
                    }
                }
            }

            else -> {
                viewModelScope.launch {
                    selectedLinksID.toList().forEach {
                        linksRepo.deleteALinkFromLinksTable(it)
                    }
                }
            }
        }
    }

    fun onDeleteClick(
        folderID: Long,
        context: Context,
        onTaskCompleted: () -> Unit,
        linkID: Long,
        impLinkURL: String = "",
        shouldShowToastOnCompletion: Boolean = true
    ) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    linksRepo
                        .deleteALinkFromImpLinksBasedOnURL(impLinkURL)
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                viewModelScope.launch {
                    if (selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        linksRepo.deleteALinkFromLinksTable(linkID)
                    } else {
                        foldersRepo.deleteAFolder(folderID)
                    }
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                viewModelScope.launch {
                    linksRepo
                        .deleteALinkFromLinksTable(linkID = linkID)
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                viewModelScope.launch {
                    if (selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        linksRepo.deleteALinkFromLinksTable(linkID)
                    } else {
                        foldersRepo.deleteAFolder(folderID)
                    }
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            else -> {}
        }
        if (shouldShowToastOnCompletion) {
            viewModelScope.launch {
                pushAUIEvent(CommonUiEvent.ShowToast(LocalizedStrings.deletedTheLinkSuccessfully.value))
            }
        }

    }

    fun onNoteDeleteCardClick(
        selectedWebURL: String, context: Context, folderID: Long, folderName: String, linkID: Long
    ) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    linksRepo
                        .deleteANoteFromImportantLinks(webURL = selectedWebURL)
                }.invokeOnCompletion {
                    viewModelScope.launch {
                        pushAUIEvent(CommonUiEvent.ShowToast(LocalizedStrings.deleteTheNote.value))
                    }
                }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                viewModelScope.launch {
                    if (selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        linksRepo.deleteALinkInfoOfFolders(
                            linkID = linkID
                        )
                    } else {
                        foldersRepo.deleteAFolderNote(
                            folderID = folderID
                        )
                    }
                }.invokeOnCompletion {
                    viewModelScope.launch {
                        pushAUIEvent(CommonUiEvent.ShowToast(LocalizedStrings.deleteTheNote.value))
                    }
                }
            }

            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                viewModelScope.launch {
                    linksRepo
                        .deleteALinkInfoFromSavedLinks(webURL = selectedWebURL)
                }.invokeOnCompletion {
                    viewModelScope.launch {
                        pushAUIEvent(CommonUiEvent.ShowToast(LocalizedStrings.deleteTheNote.value))
                    }
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                viewModelScope.launch {
                    if (selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        linksRepo.deleteALinkInfoOfFolders(
                            linkID = linkID
                        )
                    } else {
                        foldersRepo.deleteAFolderNote(
                            folderID = folderID
                        )
                    }
                }.invokeOnCompletion {
                    viewModelScope.launch {
                        pushAUIEvent(CommonUiEvent.ShowToast(LocalizedStrings.deleteTheNote.value))
                    }
                }
            }

            else -> {}
        }

    }

    fun onLinkClick(
        recentlyVisited: RecentlyVisited,
        onTaskCompleted: () -> Unit,
        context: Context,
        uriHandler: UriHandler,
        forceOpenInExternalBrowser: Boolean,
    ) {
        customWebTab.openInWeb(
            recentlyVisitedData = RecentlyVisited(
                title = recentlyVisited.title,
                webURL = recentlyVisited.webURL,
                baseURL = recentlyVisited.baseURL,
                imgURL = recentlyVisited.imgURL,
                infoForSaving = recentlyVisited.infoForSaving
            ),
            context = context,
            uriHandler = uriHandler,
            forceOpenInExternalBrowser = forceOpenInExternalBrowser
        )
        onTaskCompleted()
    }
}

enum class SpecificScreenType {
    IMPORTANT_LINKS_SCREEN, ARCHIVED_FOLDERS_LINKS_SCREEN, SAVED_LINKS_SCREEN,
    SPECIFIC_FOLDER_LINKS_SCREEN, INTENT_ACTIVITY, ROOT_SCREEN, ALL_LINKS_SCREEN
}