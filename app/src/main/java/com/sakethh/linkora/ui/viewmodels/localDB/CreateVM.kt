package com.sakethh.linkora.ui.viewmodels.localDB

import android.content.Context
import android.widget.Toast
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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateVM : ViewModel() {

    fun addANewShelf(shelf: Shelf, context: Context) {
        viewModelScope.launch {
            if (LocalDataBase.localDB.shelfCrud().doesThisShelfExists(shelf.shelfName)) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Shelf named ${shelf.shelfName} already exists",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
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
                val doesThisLinkExists = async {
                    LocalDataBase.localDB.readDao().doesThisExistsInImpLinks(
                        webURL
                    )
                }.await()
                when (doesThisLinkExists) {
                    true -> {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "given link already exists in the \"Important Links\"",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    else -> {
                        when (val linkDataExtractor = linkDataExtractor(context, webURL)) {
                            is LinkDataExtractorResult.Failure.InvalidURL -> {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        linkDataExtractor.failureMsg,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
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
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        linkDataExtractor.failureMsg,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            is LinkDataExtractorResult.Success -> {
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
                val doesThisLinkExists = async {
                    LocalDataBase.localDB.readDao().doesThisExistsInSavedLinks(
                        webURL
                    )
                }.await()
                when (doesThisLinkExists) {
                    true -> {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "given link already exists in the \"Saved Links\"",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    else -> {
                        when (val linkDataExtractor = linkDataExtractor(context, webURL)) {
                            is LinkDataExtractorResult.Failure.InvalidURL -> {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        linkDataExtractor.failureMsg,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
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
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        linkDataExtractor.failureMsg,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }

                            is LinkDataExtractorResult.Success -> {
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
        try {
            viewModelScope.launch {
                val doesThisLinkExists = async {
                    LocalDataBase.localDB.readDao().doesThisLinkExistsInAFolderV10(
                        webURL, parentFolderID
                    )
                }.await()
                if (doesThisLinkExists) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "given link already exists in the \"${folderName}\"",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    when (val linkDataExtractor = linkDataExtractor(context, webURL)) {
                        is LinkDataExtractorResult.Failure.InvalidURL -> {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    LinkDataExtractorResult.Failure.InvalidURL.failureMsg,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
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
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    LinkDataExtractorResult.Failure.NoInternetConnection.failureMsg,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        } finally {
            onTaskCompleted()
        }
    }

    fun createANewFolder(
        context: Context,
        infoForSaving: String,
        onTaskCompleted: () -> Unit,
        parentFolderID: Long?,
        folderName: String,
        inAChildFolderScreen: Boolean,
        rootParentID: Long
    ) {
        var doesThisFolderExists = false
        viewModelScope.launch {
            doesThisFolderExists = if (!inAChildFolderScreen) {
                LocalDataBase.localDB.readDao().doesThisRootFolderExists(folderName = folderName)
            } else {
                LocalDataBase.localDB.readDao().doesThisChildFolderExists(
                    folderName = folderName, parentFolderID = parentFolderID
                ) >= 1
            }
        }.invokeOnCompletion {
            if (doesThisFolderExists) {
                viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "\"${folderName}\" already exists", Toast.LENGTH_SHORT
                        ).show()
                    }
                }.invokeOnCompletion {
                    onTaskCompleted()
                }

            } else {
                viewModelScope.launch {
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
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "\"${folderName}\" folder created successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
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

private fun isAValidURL(webURL: String): Boolean {
    return try {
        webURL.split("/")[2]
        true
    } catch (_: Exception) {
        false
    }
}