package com.sakethh.linkora.localDB.dto.exportImportDTOs

import com.sakethh.linkora.localDB.dto.ArchivedFolders
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.localDB.dto.RecentlyVisited
import kotlinx.serialization.Serializable

@Serializable
data class ExportDTOv8(
    val appVersion: Int,
    val savedLinks: List<LinksTable>,
    val importantLinks: List<ImportantLinks>,
    val folders: List<FoldersTable>,
    val archivedLinks: List<ArchivedLinks>,
    val archivedFolders: List<ArchivedFolders>,
    val historyLinks: List<RecentlyVisited>,
)

@Serializable
data class ArchivedFoldersV8(
    val archiveFolderName: String,
    val infoForSaving: String,
    var id: Long = 0,
)

@Serializable
data class LinksTableV8(
    val id: Long = 0,
    val title: String,
    val webURL: String,
    val baseURL: String,
    val imgURL: String,
    val infoForSaving: String,

    var isLinkedWithSavedLinks: Boolean,

    var isLinkedWithFolders: Boolean,
    var keyOfLinkedFolder: String,

    var isLinkedWithImpFolder: Boolean,
    var keyOfImpLinkedFolder: String,

    var isLinkedWithArchivedFolder: Boolean,
    var keyOfArchiveLinkedFolder: String,
)

@Serializable
data class FoldersTableV8(
    var folderName: String,
    var infoForSaving: String,

    var id: Long = 0,
)