package com.sakethh.linkora.screens.collections.specificCollectionScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.UriHandler
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.btmSheet.OptionsBtmSheetType
import com.sakethh.linkora.btmSheet.OptionsBtmSheetVM
import com.sakethh.linkora.customWebTab.openInWeb
import com.sakethh.linkora.localDB.LocalDataBase
import com.sakethh.linkora.localDB.commonVMs.DeleteVM
import com.sakethh.linkora.localDB.commonVMs.UpdateVM
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.localDB.dto.RecentlyVisited
import com.sakethh.linkora.screens.collections.CollectionsScreenVM
import com.sakethh.linkora.screens.collections.FolderComponent
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class MutableImportantLinks(
    val title: MutableState<String>,
    val webURL: MutableState<String>,
    val baseURL: MutableState<String>,
    val imgURL: MutableState<String>,
    val infoForSaving: MutableState<String>,
    var id: Long = 0,
)

data class LinkTableComponent(
    val isCheckBoxSelected: List<MutableState<Boolean>>, val linksTableList: List<LinksTable>
)

data class ImpLinkTableComponent(
    val isCheckBoxSelected: List<MutableState<Boolean>>,
    val importantLinksList: List<ImportantLinks>
)

open class SpecificCollectionsScreenVM(
    val updateVM: UpdateVM = UpdateVM(), private val deleteVM: DeleteVM = DeleteVM()
) : CollectionsScreenVM() {


    private val _folderLinksData = MutableStateFlow(
        LinkTableComponent(
            emptyList(), emptyList()
        )
    )
    val folderLinksData = _folderLinksData.asStateFlow()

    private val _childFoldersData = MutableStateFlow(FolderComponent(emptyList(), emptyList()))
    val childFoldersData = _childFoldersData.asStateFlow()

    private val _savedLinksData = MutableStateFlow(
        LinkTableComponent(
            emptyList(), emptyList()
        )
    )
    val savedLinksTable = _savedLinksData.asStateFlow()

    private val _impLinksData = MutableStateFlow(
        ImpLinkTableComponent(
            emptyList(), emptyList()
        )
    )
    val impLinksTable = _impLinksData.asStateFlow()

    private val _archiveFolderLinksData =
        MutableStateFlow(LinkTableComponent(emptyList(), emptyList()))
    val archiveFoldersLinksData = _archiveFolderLinksData.asStateFlow()

    private val _archiveSubFolderData = MutableStateFlow(FolderComponent(emptyList(), emptyList()))
    val archiveSubFolderData = _archiveSubFolderData.asStateFlow()


    val selectedLinksID = mutableStateListOf<Long>()
    val areAllLinksChecked = mutableStateOf(false)
    fun removeAllLinkSelections() {
        when (screenType.value) {
            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                List(savedLinksTable.value.linksTableList.size) {
                    savedLinksTable.value.isCheckBoxSelected[it].value = false
                }
                selectedLinksID.removeAll(savedLinksTable.value.linksTableList.map { it.id })
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                List(folderLinksData.value.linksTableList.size) {
                    folderLinksData.value.isCheckBoxSelected[it].value = false
                }
                selectedLinksID.removeAll(folderLinksData.value.linksTableList.map { it.id })
            }

            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                List(impLinksTable.value.importantLinksList.size) {
                    impLinksTable.value.isCheckBoxSelected[it].value = false
                }
                selectedLinksID.removeAll(impLinksTable.value.importantLinksList.map { it.id })
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                List(archiveFoldersLinksData.value.linksTableList.size) {
                    archiveFoldersLinksData.value.isCheckBoxSelected[it].value = false
                }
                selectedLinksID.removeAll(archiveFoldersLinksData.value.linksTableList.map { it.id })
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

    fun retrieveChildFoldersData() {
        viewModelScope.launch {
            LocalDataBase.localDB.readDao().getChildFoldersOfThisParentID(
                currentClickedFolderData.value.id
            ).collectLatest { it ->
                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                List(it.size) { index ->
                    mutableBooleanList.add(index, mutableStateOf(false))
                }
                _childFoldersData.emit(
                    FolderComponent(
                        isCheckBoxSelected = mutableBooleanList, foldersTableList = it
                    )
                )
            }
        }
    }

    fun updateFolderData(folderID: Long) {
        viewModelScope.launch {
            currentClickedFolderData.value =
                LocalDataBase.localDB.readDao().getThisFolderData(folderID)
        }
    }

    init {
        viewModelScope.launch {
            changeRetrievedData(
                sortingPreferences = SettingsScreenVM.SortingPreferences.valueOf(SettingsScreenVM.Settings.selectedSortingType.value),
                folderID = currentClickedFolderData.value.id,
                isFoldersSortingSelected = true,
                isLinksSortingSelected = true
            )
            retrieveChildFoldersData()
        }
    }

    fun changeRetrievedData(
        sortingPreferences: SettingsScreenVM.SortingPreferences,
        folderID: Long,
        screenType: SpecificScreenType = Companion.screenType.value,
        isFoldersSortingSelected: Boolean = false,
        isLinksSortingSelected: Boolean = false
    ) {
        when (screenType) {
            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                when (sortingPreferences) {
                    SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                        viewModelScope.launch {
                            LocalDataBase.localDB.savedLinksSorting().sortByAToZ().collectLatest {
                                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                List(it.size) { index ->
                                    mutableBooleanList.add(index, mutableStateOf(false))
                                }
                                _savedLinksData.emit(
                                    LinkTableComponent(
                                        isCheckBoxSelected = mutableBooleanList, linksTableList = it
                                    )
                                )
                            }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            LocalDataBase.localDB.savedLinksSorting().sortByZToA().collectLatest {
                                val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                List(it.size) { index ->
                                    mutableBooleanList.add(index, mutableStateOf(false))
                                }
                                _savedLinksData.emit(
                                    LinkTableComponent(
                                        isCheckBoxSelected = mutableBooleanList, linksTableList = it
                                    )
                                )
                            }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            LocalDataBase.localDB.savedLinksSorting().sortByLatestToOldest()
                                .collectLatest {
                                    val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                    List(it.size) { index ->
                                        mutableBooleanList.add(index, mutableStateOf(false))
                                    }
                                    _savedLinksData.emit(
                                        LinkTableComponent(
                                            isCheckBoxSelected = mutableBooleanList,
                                            linksTableList = it
                                        )
                                    )
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            LocalDataBase.localDB.savedLinksSorting().sortByOldestToLatest()
                                .collectLatest {
                                    val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                    List(it.size) { index ->
                                        mutableBooleanList.add(index, mutableStateOf(false))
                                    }
                                    _savedLinksData.emit(
                                        LinkTableComponent(
                                            isCheckBoxSelected = mutableBooleanList,
                                            linksTableList = it
                                        )
                                    )
                                }
                        }
                    }
                }
            }

            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                when (sortingPreferences) {
                    SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                        viewModelScope.launch {
                            LocalDataBase.localDB.importantLinksSorting().sortByAToZ()
                                .collectLatest {
                                    val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                    List(it.size) { index ->
                                        mutableBooleanList.add(index, mutableStateOf(false))
                                    }
                                    _impLinksData.emit(
                                        ImpLinkTableComponent(
                                            isCheckBoxSelected = mutableBooleanList,
                                            importantLinksList = it
                                        )
                                    )
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            LocalDataBase.localDB.importantLinksSorting().sortByZToA()
                                .collectLatest {
                                    val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                    List(it.size) { index ->
                                        mutableBooleanList.add(index, mutableStateOf(false))
                                    }
                                    _impLinksData.emit(
                                        ImpLinkTableComponent(
                                            isCheckBoxSelected = mutableBooleanList,
                                            importantLinksList = it
                                        )
                                    )
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            LocalDataBase.localDB.importantLinksSorting().sortByLatestToOldest()
                                .collectLatest {
                                    val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                    List(it.size) { index ->
                                        mutableBooleanList.add(index, mutableStateOf(false))
                                    }
                                    _impLinksData.emit(
                                        ImpLinkTableComponent(
                                            isCheckBoxSelected = mutableBooleanList,
                                            importantLinksList = it
                                        )
                                    )
                                }
                        }
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            LocalDataBase.localDB.importantLinksSorting().sortByOldestToLatest()
                                .collectLatest {
                                    val mutableBooleanList = mutableListOf<MutableState<Boolean>>()
                                    List(it.size) { index ->
                                        mutableBooleanList.add(index, mutableStateOf(false))
                                    }
                                    _impLinksData.emit(
                                        ImpLinkTableComponent(
                                            isCheckBoxSelected = mutableBooleanList,
                                            importantLinksList = it
                                        )
                                    )
                                }
                        }
                    }
                }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                when (sortingPreferences) {
                    SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                if (isLinksSortingSelected) {
                                    LocalDataBase.localDB.archivedFolderLinksSorting()
                                        .sortLinksByAToZV10(folderID = folderID).collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _archiveFolderLinksData.emit(
                                                LinkTableComponent(
                                                    isCheckBoxSelected = mutableBooleanList,
                                                    linksTableList = it
                                                )
                                            )
                                        }
                                }
                            }, async {
                                if (isFoldersSortingSelected) {
                                    LocalDataBase.localDB.subFoldersSortingDao()
                                        .sortSubFoldersByAToZ(parentFolderID = currentClickedFolderData.value.id)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _archiveSubFolderData.emit(
                                                FolderComponent(
                                                    isCheckBoxSelected = mutableBooleanList,
                                                    foldersTableList = it
                                                )
                                            )
                                        }
                                }
                            })
                        }
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                if (isLinksSortingSelected) {
                                    LocalDataBase.localDB.archivedFolderLinksSorting()
                                        .sortLinksByZToAV10(folderID = folderID).collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _archiveFolderLinksData.emit(
                                                LinkTableComponent(
                                                    isCheckBoxSelected = mutableBooleanList,
                                                    linksTableList = it
                                                )
                                            )
                                        }
                                }
                            }, async {
                                if (isFoldersSortingSelected) {
                                    LocalDataBase.localDB.subFoldersSortingDao()
                                        .sortSubFoldersByZToA(parentFolderID = currentClickedFolderData.value.id)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _archiveSubFolderData.emit(
                                                FolderComponent(
                                                    isCheckBoxSelected = mutableBooleanList,
                                                    foldersTableList = it
                                                )
                                            )
                                        }
                                }
                            })

                        }
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                if (isLinksSortingSelected) {
                                    LocalDataBase.localDB.archivedFolderLinksSorting()
                                        .sortLinksByLatestToOldestV10(folderID = folderID)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _archiveFolderLinksData.emit(
                                                LinkTableComponent(
                                                    isCheckBoxSelected = mutableBooleanList,
                                                    linksTableList = it
                                                )
                                            )
                                        }
                                }
                            }, async {
                                if (isFoldersSortingSelected) {
                                    LocalDataBase.localDB.subFoldersSortingDao()
                                        .sortSubFoldersByLatestToOldest(parentFolderID = currentClickedFolderData.value.id)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _archiveSubFolderData.emit(
                                                FolderComponent(
                                                    isCheckBoxSelected = mutableBooleanList,
                                                    foldersTableList = it
                                                )
                                            )
                                        }
                                }
                            })
                        }
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                if (isLinksSortingSelected) {
                                    LocalDataBase.localDB.archivedFolderLinksSorting()
                                        .sortLinksByOldestToLatestV10(folderID = folderID)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _archiveFolderLinksData.emit(
                                                LinkTableComponent(
                                                    isCheckBoxSelected = mutableBooleanList,
                                                    linksTableList = it
                                                )
                                            )
                                        }
                                }
                            }, async {
                                if (isFoldersSortingSelected) {
                                    LocalDataBase.localDB.subFoldersSortingDao()
                                        .sortSubFoldersByOldestToLatest(parentFolderID = currentClickedFolderData.value.id)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _archiveSubFolderData.emit(
                                                FolderComponent(
                                                    isCheckBoxSelected = mutableBooleanList,
                                                    foldersTableList = it
                                                )
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
                    SettingsScreenVM.SortingPreferences.A_TO_Z -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                if (isLinksSortingSelected) {
                                    LocalDataBase.localDB.regularFolderLinksSorting()
                                        .sortByAToZV10(folderID = folderID).collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _folderLinksData.emit(
                                                LinkTableComponent(
                                                    isCheckBoxSelected = mutableBooleanList,
                                                    linksTableList = it
                                                )
                                            )
                                        }
                                }
                            }, async {
                                if (isFoldersSortingSelected) {
                                    LocalDataBase.localDB.subFoldersSortingDao()
                                        .sortSubFoldersByAToZ(parentFolderID = folderID)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _childFoldersData.emit(
                                                FolderComponent(
                                                    isCheckBoxSelected = mutableBooleanList,
                                                    foldersTableList = it
                                                )
                                            )
                                        }
                                }
                            })
                        }
                    }

                    SettingsScreenVM.SortingPreferences.Z_TO_A -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                if (isLinksSortingSelected) {
                                    LocalDataBase.localDB.regularFolderLinksSorting()
                                        .sortByZToAV10(folderID = folderID).collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _folderLinksData.emit(
                                                LinkTableComponent(
                                                    isCheckBoxSelected = mutableBooleanList,
                                                    linksTableList = it
                                                )
                                            )
                                        }
                                }
                            }, async {
                                if (isFoldersSortingSelected) {
                                    LocalDataBase.localDB.subFoldersSortingDao()
                                        .sortSubFoldersByZToA(parentFolderID = folderID)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _childFoldersData.emit(
                                                FolderComponent(
                                                    isCheckBoxSelected = mutableBooleanList,
                                                    foldersTableList = it
                                                )
                                            )
                                        }
                                }
                            })
                        }
                    }

                    SettingsScreenVM.SortingPreferences.NEW_TO_OLD -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                if (isLinksSortingSelected) {
                                    LocalDataBase.localDB.regularFolderLinksSorting()
                                        .sortByLatestToOldestV10(folderID = folderID)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _folderLinksData.emit(
                                                LinkTableComponent(
                                                    isCheckBoxSelected = mutableBooleanList,
                                                    linksTableList = it
                                                )
                                            )
                                        }
                                }
                            }, async {
                                if (isFoldersSortingSelected) {
                                    LocalDataBase.localDB.subFoldersSortingDao()
                                        .sortSubFoldersByLatestToOldest(parentFolderID = folderID)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _childFoldersData.emit(
                                                FolderComponent(
                                                    isCheckBoxSelected = mutableBooleanList,
                                                    foldersTableList = it
                                                )
                                            )
                                        }
                                }
                            })
                        }
                    }

                    SettingsScreenVM.SortingPreferences.OLD_TO_NEW -> {
                        viewModelScope.launch {
                            awaitAll(async {
                                if (isLinksSortingSelected) {
                                    LocalDataBase.localDB.regularFolderLinksSorting()
                                        .sortByOldestToLatestV10(folderID = folderID)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _folderLinksData.emit(
                                                LinkTableComponent(
                                                    isCheckBoxSelected = mutableBooleanList,
                                                    linksTableList = it
                                                )
                                            )
                                        }
                                }
                            }, async {
                                if (isFoldersSortingSelected) {
                                    LocalDataBase.localDB.subFoldersSortingDao()
                                        .sortSubFoldersByOldestToLatest(parentFolderID = folderID)
                                        .collectLatest {
                                            val mutableBooleanList =
                                                mutableListOf<MutableState<Boolean>>()
                                            List(it.size) { index ->
                                                mutableBooleanList.add(index, mutableStateOf(false))
                                            }
                                            _childFoldersData.emit(
                                                FolderComponent(
                                                    isCheckBoxSelected = mutableBooleanList,
                                                    foldersTableList = it
                                                )
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
        }
    }

    fun onArchiveClick(
        tempImpLinkData: ImportantLinks, context: Context, linkID: Long, onTaskCompleted: () -> Unit
    ) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    awaitAll(async {
                        updateVM.archiveLinkTableUpdater(archivedLinks = ArchivedLinks(
                            title = tempImpLinkData.title,
                            webURL = tempImpLinkData.webURL,
                            baseURL = tempImpLinkData.baseURL,
                            imgURL = tempImpLinkData.imgURL,
                            infoForSaving = tempImpLinkData.infoForSaving
                        ), context = context, onTaskCompleted = {
                            onTaskCompleted()
                        })
                    }, async {
                        LocalDataBase.localDB.deleteDao()
                            .deleteALinkFromImpLinks(linkID = tempImpLinkData.id)
                    })
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                viewModelScope.launch {
                    kotlinx.coroutines.awaitAll(async {
                        updateVM.archiveLinkTableUpdater(archivedLinks = ArchivedLinks(
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
                    kotlinx.coroutines.awaitAll(async {
                        updateVM.archiveLinkTableUpdater(archivedLinks = ArchivedLinks(
                            title = tempImpLinkData.title,
                            webURL = tempImpLinkData.webURL,
                            baseURL = tempImpLinkData.baseURL,
                            imgURL = tempImpLinkData.imgURL,
                            infoForSaving = tempImpLinkData.infoForSaving
                        ), context = context, onTaskCompleted = {
                            onTaskCompleted()
                        })
                    }, async {
                        LocalDataBase.localDB.deleteDao()
                            .deleteALinkFromSavedLinksBasedOnURL(webURL = tempImpLinkData.webURL)
                    })
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                viewModelScope.launch {
                    kotlinx.coroutines.awaitAll(async {
                        updateVM.archiveLinkTableUpdater(archivedLinks = ArchivedLinks(
                            title = tempImpLinkData.title,
                            webURL = tempImpLinkData.webURL,
                            baseURL = tempImpLinkData.baseURL,
                            imgURL = tempImpLinkData.imgURL,
                            infoForSaving = tempImpLinkData.infoForSaving
                        ), context = context, onTaskCompleted = {
                            onTaskCompleted()
                        })
                    }, async {
                        LocalDataBase.localDB.deleteDao().deleteALinkFromLinksTable(linkID)
                    })
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            else -> {}
        }
    }

    fun onDeleteMultipleFolders(context: Context) {
        selectedBtmSheetType.value = OptionsBtmSheetType.FOLDER
        selectedFoldersID.forEach {
            onDeleteClick(
                shouldShowToastOnCompletion = false,
                folderID = it,
                selectedWebURL = "",
                context = context,
                onTaskCompleted = {},
                folderName = selectedFolderData.value.folderName,
                linkID = it,
            )
        }
    }

    fun onDeleteMultipleLinks(context: Context) {
        selectedBtmSheetType.value = OptionsBtmSheetType.LINK
        selectedLinksID.forEach {
            onDeleteClick(
                shouldShowToastOnCompletion = false,
                folderID = it,
                selectedWebURL = "",
                context = context,
                onTaskCompleted = {},
                folderName = selectedFolderData.value.folderName,
                linkID = it,
            )
        }
    }

    fun onDeleteClick(
        folderID: Long,
        selectedWebURL: String,
        context: Context,
        onTaskCompleted: () -> Unit,
        folderName: String,
        linkID: Long,
        shouldShowToastOnCompletion: Boolean = true
    ) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.deleteDao()
                        .deleteALinkFromImpLinks(linkID = linkID)
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                viewModelScope.launch {
                    if (selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        LocalDataBase.localDB.deleteDao().deleteALinkFromLinksTable(linkID)
                    } else {
                        deleteVM.onRegularFolderDeleteClick(folderID)
                    }
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.deleteDao()
                        .deleteALinkFromLinksTable(linkID = linkID)
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                viewModelScope.launch {
                    if (selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        LocalDataBase.localDB.deleteDao().deleteALinkFromLinksTable(linkID)
                    } else {
                        deleteVM.onRegularFolderDeleteClick(folderID)
                    }
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }

            else -> {}
        }
        if (shouldShowToastOnCompletion) {
            Toast.makeText(
                context, "deleted the link successfully", Toast.LENGTH_SHORT
            ).show()
        }

    }

    fun onNoteDeleteCardClick(
        selectedWebURL: String, context: Context, folderID: Long, folderName: String, linkID: Long
    ) {
        when (screenType.value) {
            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.deleteDao()
                        .deleteANoteFromImportantLinks(webURL = selectedWebURL)
                }.invokeOnCompletion {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            }

            SpecificScreenType.ARCHIVED_FOLDERS_LINKS_SCREEN -> {
                viewModelScope.launch {
                    if (selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        LocalDataBase.localDB.deleteDao().deleteALinkInfoOfFolders(
                            linkID = linkID
                        )
                    } else {
                        LocalDataBase.localDB.deleteDao().deleteAFolderNote(
                            folderID = folderID
                        )
                    }
                }.invokeOnCompletion {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            }

            SpecificScreenType.SAVED_LINKS_SCREEN -> {
                viewModelScope.launch {
                    LocalDataBase.localDB.deleteDao()
                        .deleteALinkInfoFromSavedLinks(webURL = selectedWebURL)
                }.invokeOnCompletion {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            }

            SpecificScreenType.SPECIFIC_FOLDER_LINKS_SCREEN -> {
                viewModelScope.launch {
                    if (selectedBtmSheetType.value == OptionsBtmSheetType.LINK) {
                        LocalDataBase.localDB.deleteDao().deleteALinkInfoOfFolders(
                            linkID = linkID
                        )
                    } else {
                        LocalDataBase.localDB.deleteDao().deleteAFolderNote(
                            folderID = folderID
                        )
                    }
                }.invokeOnCompletion {
                    Toast.makeText(context, "deleted the note", Toast.LENGTH_SHORT).show()
                }
            }

            else -> {}
        }

    }

    fun onImportantLinkAdditionInTheTable(
        context: Context,
        onTaskCompleted: () -> Unit, tempImpLinkData: ImportantLinks,
    ) {
        viewModelScope.launch {
            if (LocalDataBase.localDB.readDao()
                    .doesThisExistsInImpLinks(webURL = tempImpLinkData.webURL)
            ) {
                LocalDataBase.localDB.deleteDao()
                    .deleteALinkFromImpLinks(linkID = tempImpLinkData.id)
                Toast.makeText(
                    context,
                    "removed link from the \"Important Links\" successfully",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                LocalDataBase.localDB.createDao().addANewLinkToImpLinks(
                    ImportantLinks(
                        title = tempImpLinkData.title,
                        webURL = tempImpLinkData.webURL,
                        baseURL = tempImpLinkData.baseURL,
                        imgURL = tempImpLinkData.imgURL,
                        infoForSaving = tempImpLinkData.infoForSaving
                    )
                )
                Toast.makeText(
                    context, "added to the \"Important Links\" successfully", Toast.LENGTH_SHORT
                ).show()
            }
            OptionsBtmSheetVM().updateImportantCardData(tempImpLinkData.webURL)
        }.invokeOnCompletion {
            onTaskCompleted()
        }
    }

    fun onLinkClick(
        recentlyVisited: RecentlyVisited,
        onTaskCompleted: () -> Unit,
        context: Context,
        uriHandler: UriHandler,
        forceOpenInExternalBrowser: Boolean,
    ) {
        viewModelScope.launch {
            openInWeb(
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
        }.invokeOnCompletion {
            onTaskCompleted()
        }
    }
}

enum class SpecificScreenType {
    IMPORTANT_LINKS_SCREEN, ARCHIVED_FOLDERS_LINKS_SCREEN, SAVED_LINKS_SCREEN, SPECIFIC_FOLDER_LINKS_SCREEN, INTENT_ACTIVITY, ROOT_SCREEN
}