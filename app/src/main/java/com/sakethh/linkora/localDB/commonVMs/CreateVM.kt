package com.sakethh.linkora.localDB.commonVMs

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.localDB.LocalDataBase
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.localDB.isNetworkAvailable
import com.sakethh.linkora.localDB.linkDataExtractor
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateVM : ViewModel() {

    fun addANewLinkInImpLinks(
        title: String,
        webURL: String,
        noteForSaving: String, autoDetectTitle: Boolean,
        onTaskCompleted: () -> Unit,
        context: Context
    ) {
        viewModelScope.launch {
            val doesThisLinkExists = async {
                LocalDataBase.localDB.readDao()
                    .doesThisExistsInImpLinks(
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
                    if (!isNetworkAvailable(context)) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "network error, check your network connection and try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else if (!isAValidURL(webURL)) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "invalid url", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val linkDataExtractor = async {
                            linkDataExtractor(webURL)
                        }.await()
                        LocalDataBase.localDB.createDao().addANewLinkToImpLinks(
                            importantLinks = ImportantLinks(
                                title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) linkDataExtractor.title else title,
                                webURL = webURL,
                                baseURL = webURL,
                                imgURL = linkDataExtractor.imgURL,
                                infoForSaving = noteForSaving
                            )
                        )
                    }
                }
            }
        }.invokeOnCompletion {
            onTaskCompleted()
        }
    }

    fun addANewLinkInSavedLinks(
        title: String,
        webURL: String,
        noteForSaving: String, autoDetectTitle: Boolean,
        onTaskCompleted: () -> Unit,
        context: Context
    ) {
        var doesThisLinkExists = false
        viewModelScope.launch {
            doesThisLinkExists = LocalDataBase.localDB.readDao()
                .doesThisExistsInSavedLinks(
                    webURL
                )
        }.invokeOnCompletion {
            if (!isNetworkAvailable(context)) {
                viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "network error, check your network connection and try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            } else if (!isAValidURL(webURL)) {
                viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "invalid url", Toast.LENGTH_SHORT).show()
                    }
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            } else if (doesThisLinkExists) {
                viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "given link already exists in the \"Saved Links\"",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            } else {
                viewModelScope.launch {
                    val _linkDataExtractor = linkDataExtractor(webURL)
                    val linkData = LinksTable(
                        title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) _linkDataExtractor.title else title,
                        webURL = webURL,
                        baseURL = _linkDataExtractor.baseURL,
                        imgURL = _linkDataExtractor.imgURL,
                        infoForSaving = noteForSaving,
                        isLinkedWithSavedLinks = true,
                        isLinkedWithFolders = false,
                        keyOfLinkedFolderV10 = 0,
                        isLinkedWithImpFolder = false,
                        keyOfImpLinkedFolder = "",
                        isLinkedWithArchivedFolder = false,
                        keyOfArchiveLinkedFolderV10 = 0
                    )
                    LocalDataBase.localDB.createDao()
                        .addANewLinkToSavedLinksOrInFolders(linkData)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "added the link", Toast.LENGTH_SHORT).show()
                    }
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }
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
            val doesThisLinkExists = async {
                LocalDataBase.localDB.readDao()
                    .doesThisLinkExistsInAFolderV10(
                        webURL, parentFolderID
                    )
            }.await()
            if (!isNetworkAvailable(context)) {
                viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "network error, check your network connection and try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            } else if (!isAValidURL(webURL)) {
                viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "invalid url", Toast.LENGTH_SHORT).show()
                    }
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            } else if (doesThisLinkExists) {
                viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "given link already exists in the \"${folderName}\"",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            } else {
                viewModelScope.launch {
                    val linkDataExtractor = linkDataExtractor(webURL)
                    val linkData = LinksTable(
                        title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) linkDataExtractor.title else title,
                        webURL = webURL,
                        baseURL = linkDataExtractor.baseURL,
                        imgURL = linkDataExtractor.imgURL,
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
                    LocalDataBase.localDB.createDao()
                        .addANewLinkToSavedLinksOrInFolders(linkData)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "added the url", Toast.LENGTH_SHORT)
                            .show()
                    }
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }
        }
    }

    fun createANewFolder(
        context: Context, infoForSaving: String,
        onTaskCompleted: () -> Unit,
        parentFolderID: Long?,
        folderName: String,
        inAChildFolderScreen: Boolean,
        rootParentID: Long
    ) {
        var doesThisFolderExists = false
        viewModelScope.launch {
            doesThisFolderExists = if (!inAChildFolderScreen) {
                LocalDataBase.localDB.readDao()
                    .doesThisRootFolderExists(folderName = folderName)
            } else {
                LocalDataBase.localDB.readDao()
                    .doesThisChildFolderExists(
                        folderName = folderName,
                        parentFolderID = parentFolderID
                    ) >= 1
            }
        }.invokeOnCompletion {
            if (doesThisFolderExists) {
                viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "\"${folderName}\" already exists",
                            Toast.LENGTH_SHORT
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
                    LocalDataBase.localDB.createDao()
                        .addANewFolder(
                            foldersTable
                        )
                    if (parentFolderID != null) {
                        LocalDataBase.localDB.createDao().addANewChildIdToARootAndParentFolders(
                            rootParentID = rootParentID,
                            currentID = LocalDataBase.localDB.readDao()
                                .getLatestAddedFolder().id,
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
}

private fun isAValidURL(webURL: String): Boolean {
    return try {
        webURL.split("/")[2]
        true
    } catch (_: Exception) {
        false
    }
}