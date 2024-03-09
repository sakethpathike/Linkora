package com.sakethh.linkora.ui.viewmodels.localDB

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.localDB.LocalDataBase
import com.sakethh.linkora.data.localDB.dto.FoldersTable
import com.sakethh.linkora.data.localDB.dto.HomeScreenListTable
import com.sakethh.linkora.data.localDB.dto.ImportantLinks
import com.sakethh.linkora.data.localDB.dto.LinksTable
import com.sakethh.linkora.data.localDB.dto.Shelf
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM
import com.sakethh.linkora.utils.LinkDataExtractorResult
import com.sakethh.linkora.utils.linkDataExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateVM : ViewModel() {
    private val _showToast = mutableStateOf(Pair(false, ""))
    val showToast = _showToast
    fun addANewShelf(shelf: Shelf) {
        viewModelScope.launch {
            if (LocalDataBase.localDB.shelfCrud().doesThisShelfExists(shelf.shelfName)) {
                _showToast.value = true to "Shelf named ${shelf.shelfName} already exists"
            } else {
                _showToast.value = true to "created a new shelf named ${shelf.shelfName}"
                LocalDataBase.localDB.shelfCrud().addANewShelf(shelf)
            }
        }
    }

    fun addANewLinkInImpLinks(
        title: String,
        webURL: String,
        noteForSaving: String,
        autoDetectTitle: Boolean,
        onTaskCompleted: () -> Unit,
        context: Context
    ) {
        try {
            viewModelScope.launch {
                val doesThisLinkExists = withContext(Dispatchers.IO) {
                    LocalDataBase.localDB.readDao().doesThisExistsInImpLinks(
                        webURL
                    )
                }
                when (doesThisLinkExists) {
                    true -> {
                        _showToast.value =
                            true to "given link already exists in the \"Important Links\""
                    }

                    else -> {
                        when (val linkDataExtractor = linkDataExtractor(context, webURL)) {
                            is LinkDataExtractorResult.Failure.InvalidURL -> {
                                _showToast.value = true to linkDataExtractor.failureMsg
                            }

                            is LinkDataExtractorResult.Failure.NoInternetConnection -> {
                                LocalDataBase.localDB.createDao().addANewLinkToImpLinks(
                                    importantLinks = ImportantLinks(
                                        title = "",
                                        webURL = webURL,
                                        baseURL = webURL,
                                        imgURL = "",
                                        infoForSaving = noteForSaving
                                    )
                                )
                                _showToast.value = true to linkDataExtractor.failureMsg
                            }

                            is LinkDataExtractorResult.Success -> {
                                _showToast.value = true to "added to Important Links"
                                LocalDataBase.localDB.createDao().addANewLinkToImpLinks(
                                    importantLinks = ImportantLinks(
                                        title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) linkDataExtractor.linkDataExtractor.title else title,
                                        webURL = webURL,
                                        baseURL = webURL,
                                        imgURL = linkDataExtractor.linkDataExtractor.imgURL,
                                        infoForSaving = noteForSaving
                                    )
                                )
                            }
                        }
                    }
                }
            }
        } finally {
            onTaskCompleted()
        }
    }

    fun addANewLinkInSavedLinks(
        title: String,
        webURL: String,
        noteForSaving: String,
        autoDetectTitle: Boolean,
        onTaskCompleted: () -> Unit,
        context: Context
    ) {
        try {
            viewModelScope.launch {
                val doesThisLinkExists = withContext(Dispatchers.IO) {
                    LocalDataBase.localDB.readDao().doesThisExistsInSavedLinks(
                        webURL
                    )
                }
                when (doesThisLinkExists) {
                    true -> {
                        _showToast.value =
                            true to "given link already exists in the \"Saved Links\""
                    }

                    else -> {
                        when (val linkDataExtractor = linkDataExtractor(context, webURL)) {
                            is LinkDataExtractorResult.Failure.InvalidURL -> {
                                _showToast.value =
                                    true to linkDataExtractor.failureMsg
                            }

                            is LinkDataExtractorResult.Failure.NoInternetConnection -> {
                                LocalDataBase.localDB.createDao()
                                    .addANewLinkToSavedLinksOrInFolders(
                                        LinksTable(
                                            title = "",
                                            webURL = webURL,
                                            baseURL = webURL,
                                            imgURL = "",
                                            infoForSaving = noteForSaving,
                                            isLinkedWithSavedLinks = true,
                                            isLinkedWithFolders = false,
                                            keyOfLinkedFolderV10 = 0,
                                            isLinkedWithImpFolder = false,
                                            keyOfImpLinkedFolder = "",
                                            isLinkedWithArchivedFolder = false,
                                            keyOfArchiveLinkedFolderV10 = 0
                                        )
                                    )
                                _showToast.value =
                                    true to linkDataExtractor.failureMsg
                            }

                            is LinkDataExtractorResult.Success -> {
                                _showToast.value = true to "added the link in Saved Links"
                                if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) linkDataExtractor.linkDataExtractor.title else title.let {
                                    LocalDataBase.localDB.createDao()
                                        .addANewLinkToSavedLinksOrInFolders(
                                            LinksTable(
                                                title = it,
                                                webURL = webURL,
                                                baseURL = it,
                                                imgURL = it,
                                                infoForSaving = noteForSaving,
                                                isLinkedWithSavedLinks = true,
                                                isLinkedWithFolders = false,
                                                keyOfLinkedFolderV10 = 0,
                                                isLinkedWithImpFolder = false,
                                                keyOfImpLinkedFolder = "",
                                                isLinkedWithArchivedFolder = false,
                                                keyOfArchiveLinkedFolderV10 = 0
                                            )
                                        )
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            onTaskCompleted()
        }
    }

    fun addANewLinkInAFolderV10(
        title: String,
        webURL: String,
        noteForSaving: String,
        parentFolderID: Long,
        context: Context,
        autoDetectTitle: Boolean,
        onTaskCompleted: () -> Unit,
        folderName: String
    ) {
            viewModelScope.launch {
                val doesThisLinkExists = withContext(Dispatchers.IO) {
                    LocalDataBase.localDB.readDao().doesThisLinkExistsInAFolderV10(
                        webURL, parentFolderID
                    )
                }
                if (doesThisLinkExists) {
                    _showToast.value =
                        true to "given link already exists in the \"${folderName}\""
                } else {
                    when (val linkDataExtractor = linkDataExtractor(context, webURL)) {
                        is LinkDataExtractorResult.Failure.InvalidURL -> {
                            _showToast.value =
                                true to linkDataExtractor.failureMsg
                        }

                        is LinkDataExtractorResult.Success -> {
                            LocalDataBase.localDB.createDao()
                                .addANewLinkToSavedLinksOrInFolders(
                                    LinksTable(
                                        title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) linkDataExtractor.linkDataExtractor.title else title,
                                        webURL = webURL,
                                        baseURL = linkDataExtractor.linkDataExtractor.baseURL,
                                        imgURL = linkDataExtractor.linkDataExtractor.imgURL,
                                        infoForSaving = noteForSaving,
                                        isLinkedWithSavedLinks = false,
                                        isLinkedWithFolders = true,
                                        keyOfLinkedFolderV10 = parentFolderID,
                                        keyOfLinkedFolder = folderName,
                                        isLinkedWithImpFolder = false,
                                        isLinkedWithArchivedFolder = false,
                                        keyOfArchiveLinkedFolderV10 = 0,
                                        keyOfImpLinkedFolder = ""
                                    )
                                )
                            _showToast.value = true to "added the link"
                        }

                        LinkDataExtractorResult.Failure.NoInternetConnection -> {
                            LocalDataBase.localDB.createDao()
                                .addANewLinkToSavedLinksOrInFolders(
                                    LinksTable(
                                        title = title,
                                        webURL = webURL,
                                        baseURL = webURL,
                                        imgURL = "",
                                        infoForSaving = noteForSaving,
                                        isLinkedWithSavedLinks = false,
                                        isLinkedWithFolders = true,
                                        keyOfLinkedFolderV10 = parentFolderID,
                                        keyOfLinkedFolder = folderName,
                                        isLinkedWithImpFolder = false,
                                        isLinkedWithArchivedFolder = false,
                                        keyOfArchiveLinkedFolderV10 = 0,
                                        keyOfImpLinkedFolder = ""
                                    )
                                )
                            _showToast.value =
                                true to LinkDataExtractorResult.Failure.NoInternetConnection.failureMsg
                        }
                    }
                }
            }.invokeOnCompletion {
                onTaskCompleted()
            }
    }

    fun createANewFolder(
        infoForSaving: String,
        onTaskCompleted: () -> Unit,
        parentFolderID: Long?,
        folderName: String,
        inAChildFolderScreen: Boolean,
        rootParentID: Long
    ) {
        viewModelScope.launch {
            try {
                val doesThisFolderExists = withContext(Dispatchers.IO) {
                    if (!inAChildFolderScreen) {
                        LocalDataBase.localDB.readDao()
                            .doesThisRootFolderExists(folderName = folderName)
                    } else {
                        LocalDataBase.localDB.readDao().doesThisChildFolderExists(
                            folderName = folderName, parentFolderID = parentFolderID
                        ) >= 1
                    }
                }
                if (doesThisFolderExists) {
                    _showToast.value = true to "\"${folderName}\" already exists"
                } else {
                    val foldersTable = FoldersTable(
                        folderName = folderName,
                        infoForSaving = infoForSaving,
                        parentFolderID = parentFolderID,
                    )
                    foldersTable.childFolderIDs = emptyList()
                    LocalDataBase.localDB.createDao().addANewFolder(
                        foldersTable
                    )
                    if (parentFolderID != null) {
                        LocalDataBase.localDB.createDao().addANewChildIdToARootAndParentFolders(
                            rootParentID = rootParentID,
                            currentID = LocalDataBase.localDB.readDao().getLatestAddedFolder().id,
                            parentID = parentFolderID
                        )
                    }
                    _showToast.value = true to "\"${folderName}\" folder created successfully"
                }
            } catch (_: Exception) {
                _showToast.value = true to "couldn't create a folder"
            } finally {
                onTaskCompleted()
            }
        }
    }

    fun insertANewElementInHomeScreenList(folderName: String, folderID: Long, parentShelfID: Long) {
        viewModelScope.launch {
            val homeScreenListTable =
                HomeScreenListTable(id = folderID, position = withContext(Dispatchers.IO) {
                    try {
                        ++LocalDataBase.localDB.shelfFolders().getLastInsertedElement().position
                    } catch (_: NullPointerException) {
                        1
                    }
                }, folderName = folderName, parentShelfID = parentShelfID)
            LocalDataBase.localDB.shelfFolders().addAHomeScreenListFolder(homeScreenListTable)
        }
    }
}