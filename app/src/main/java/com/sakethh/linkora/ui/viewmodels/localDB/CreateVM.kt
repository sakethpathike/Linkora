package com.sakethh.linkora.localDB.commonVMs

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.HomeScreenListTable
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.Shelf
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM
import com.sakethh.linkora.utils.LinkDataExtractor
import com.sakethh.linkora.utils.isNetworkAvailable
import com.sakethh.linkora.utils.linkDataExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateVM : ViewModel() {

    fun addANewShelf(shelf: Shelf, context: Context) {
        viewModelScope.launch {
            if (LocalDatabase.localDB.shelfCrud().doesThisShelfExists(shelf.shelfName)) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Shelf named ${shelf.shelfName} already exists",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                LocalDatabase.localDB.shelfCrud().addANewShelf(shelf)
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
        viewModelScope.launch {
            val doesThisLinkExists = async {
                LocalDatabase.localDB.readDao().doesThisExistsInImpLinks(
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
                    if (!isAValidURL(webURL)) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "invalid url", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val linkDataExtractor = async {
                            if (isNetworkAvailable(context)) {
                                linkDataExtractor(webURL)
                            } else {
                                if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) {
                                    viewModelScope.launch {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(
                                                context,
                                                "network error, check your network connection and try again",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                                LinkDataExtractor(baseURL = webURL, imgURL = "", title = "", false)
                            }
                        }.await()
                        LocalDatabase.localDB.createDao().addANewLinkToImpLinks(
                            importantLinks = ImportantLinks(
                                title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) linkDataExtractor.title else title,
                                webURL = "http" + webURL.substringAfter("http").substringBefore(" ")
                                    .trim(),
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
        noteForSaving: String,
        autoDetectTitle: Boolean,
        onTaskCompleted: () -> Unit,
        context: Context
    ) {
        var doesThisLinkExists = false
        viewModelScope.launch {
            doesThisLinkExists = LocalDatabase.localDB.readDao().doesThisExistsInSavedLinks(
                webURL
            )
        }.invokeOnCompletion {
            if (!isAValidURL(webURL)) {
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
                    val _linkDataExtractor = if (isNetworkAvailable(context)) {
                        linkDataExtractor(webURL)
                    } else {
                        if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) {
                            viewModelScope.launch {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "network error, check your network connection and try again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        LinkDataExtractor(baseURL = webURL, imgURL = "", title = "", false)
                    }
                    val linkData = LinksTable(
                        title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) _linkDataExtractor.title else title,
                        webURL = "http" + webURL.substringAfter("http").substringBefore(" ").trim(),
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
                    LocalDatabase.localDB.createDao().addANewLinkToSavedLinksOrInFolders(linkData)
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
                LocalDatabase.localDB.readDao().doesThisLinkExistsInAFolderV10(
                    webURL, parentFolderID
                )
            }.await()
            if (!isAValidURL(webURL)) {
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
                    val linkDataExtractor = if (isNetworkAvailable(context)) {
                        linkDataExtractor(webURL)
                    } else {
                        if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) {
                            viewModelScope.launch {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "network error, check your network connection and try again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        LinkDataExtractor(baseURL = webURL, imgURL = "", title = "", false)
                    }
                    val linkData = LinksTable(
                        title = if (SettingsScreenVM.Settings.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) linkDataExtractor.title else title,
                        webURL = "http" + webURL.substringAfter("http").substringBefore(" ").trim(),
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
                    LocalDatabase.localDB.createDao().addANewLinkToSavedLinksOrInFolders(linkData)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "added the url", Toast.LENGTH_SHORT).show()
                    }
                }.invokeOnCompletion {
                    onTaskCompleted()
                }
            }
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
                LocalDatabase.localDB.readDao().doesThisRootFolderExists(folderName = folderName)
            } else {
                LocalDatabase.localDB.readDao().doesThisChildFolderExists(
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
                    LocalDatabase.localDB.createDao().addANewFolder(
                        foldersTable
                    )
                    if (parentFolderID != null) {
                        LocalDatabase.localDB.createDao().addANewChildIdToARootAndParentFolders(
                            rootParentID = rootParentID,
                            currentID = LocalDatabase.localDB.readDao().getLatestAddedFolder().id,
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
                        ++LocalDatabase.localDB.shelfFolders().getLastInsertedElement().position
                    } catch (_: NullPointerException) {
                        1
                    }
                }, folderName = folderName, parentShelfID = parentShelfID)
            LocalDatabase.localDB.shelfFolders().addAHomeScreenListFolder(homeScreenListTable)
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