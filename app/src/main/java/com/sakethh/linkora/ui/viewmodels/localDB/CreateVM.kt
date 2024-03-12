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
import com.sakethh.linkora.utils.LinkDataExtractor
import com.sakethh.linkora.utils.LinkDataExtractorResult
import com.sakethh.linkora.utils.linkDataExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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

    private suspend fun addANewLink(
        webURL: String,
        doesItExists: Boolean,
        onLinkAdd: (LinkDataExtractor?) -> Unit,
        onTaskCompleted: () -> Unit,
        context: Context
    ) = coroutineScope {
        async {
            when (doesItExists) {
                true -> {
                    _showToast.value = true to "given link already exists"
                }

                else -> {
                    when (val linkDataExtractor = linkDataExtractor(context, webURL)) {
                        is LinkDataExtractorResult.Failure.InvalidURL -> {
                            _showToast.value = true to linkDataExtractor.failureMsg
                        }

                        is LinkDataExtractorResult.Failure.NoInternetConnection -> {
                            onLinkAdd(null)
                            _showToast.value = true to linkDataExtractor.failureMsg
                        }

                        is LinkDataExtractorResult.Success -> {
                            _showToast.value = true to "added the link"
                            onLinkAdd(linkDataExtractor.linkDataExtractor)
                        }
                    }
                }
            }
        }.await()
        onTaskCompleted()
    }

    fun addANewLinkInImpLinks(
        title: String,
        webURL: String,
        noteForSaving: String,
        autoDetectTitle: Boolean,
        onTaskCompleted: () -> Unit,
        context: Context
    ) {
        viewModelScope.launch {
            addANewLink(
                webURL = webURL,
                doesItExists = LocalDataBase.localDB.readDao().doesThisExistsInImpLinks(
                    webURL
                ),
                onLinkAdd = {
                    this.launch {
                        LocalDataBase.localDB.createDao().addANewLinkToImpLinks(
                            importantLinks = ImportantLinks(
                                title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) it?.title
                                    ?: title else title,
                                webURL = webURL,
                                baseURL = it?.baseURL ?: webURL,
                                imgURL = it?.imgURL ?: "",
                                infoForSaving = noteForSaving
                            )
                        )
                    }
                },
                onTaskCompleted = { onTaskCompleted() },
                context = context
            )
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
        viewModelScope.launch {
            addANewLink(
                webURL = webURL,
                doesItExists = LocalDataBase.localDB.readDao().doesThisExistsInSavedLinks(
                    webURL
                ),
                onLinkAdd = {
                    this.launch {
                        LocalDataBase.localDB.createDao().addANewLinkToSavedLinksOrInFolders(
                            LinksTable(
                                title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) it?.title
                                    ?: "" else title,
                                webURL = webURL,
                                baseURL = it?.baseURL ?: webURL,
                                imgURL = it?.imgURL ?: "",
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
                },
                onTaskCompleted = { onTaskCompleted() },
                context = context
            )
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
            addANewLink(
                webURL = webURL,
                doesItExists = LocalDataBase.localDB.readDao()
                    .doesThisLinkExistsInAFolderV10(webURL, parentFolderID),
                onLinkAdd = {
                    this.launch {
                        LocalDataBase.localDB.createDao().addANewLinkToSavedLinksOrInFolders(
                            LinksTable(
                                title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) it?.title
                                    ?: title else title,
                                webURL = webURL,
                                baseURL = it?.baseURL ?: "",
                                imgURL = it?.imgURL ?: "",
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
                    }
                },
                onTaskCompleted = { onTaskCompleted() },
                context = context
            )
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