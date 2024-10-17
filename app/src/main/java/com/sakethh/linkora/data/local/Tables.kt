package com.sakethh.linkora.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "links_table")
data class LinksTable(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val title: String,
    val webURL: String,
    val baseURL: String,
    val imgURL: String,
    val infoForSaving: String,

    var isLinkedWithSavedLinks: Boolean,

    var isLinkedWithFolders: Boolean,
    @SerialName("keyOfLinkedFolderV10")
    var keyOfLinkedFolderV10: Long? = null,
    var keyOfLinkedFolder: String? = null,

    var isLinkedWithImpFolder: Boolean,
    var keyOfImpLinkedFolder: String,
    var keyOfImpLinkedFolderV10: Long? = null,

    var isLinkedWithArchivedFolder: Boolean,
    var keyOfArchiveLinkedFolderV10: Long? = null,
    var keyOfArchiveLinkedFolder: String? = null,
    val userAgent: String? = null
)

@Serializable
@Entity(tableName = "folders_table")
data class FoldersTable(
    var folderName: String,
    var infoForSaving: String,

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var parentFolderID: Long? = null,
    var childFolderIDs: List<Long>? = null,
    var isFolderArchived: Boolean = false,
    var isMarkedAsImportant: Boolean = false
)

@Serializable
@Entity(tableName = "archived_links_table")
data class ArchivedLinks(
    val title: String,
    val webURL: String,
    val baseURL: String,
    val imgURL: String,
    val infoForSaving: String,
    val userAgent: String? = null,

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
)

@Serializable
@Entity(tableName = "archived_folders_table")
data class ArchivedFolders(
    val archiveFolderName: String,
    val infoForSaving: String,

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
)

@Serializable
@Entity(tableName = "important_links_table")
data class ImportantLinks(
    var title: String,
    var webURL: String,
    var baseURL: String,
    var imgURL: String,
    var infoForSaving: String,
    var userAgent: String? = null,

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
)

@Serializable
@Entity(tableName = "important_folders_table")
data class ImportantFolders(
    val impFolderName: String,
    val infoForSaving: String,

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
)

@Serializable
@Entity(tableName = "recently_visited_table")
data class RecentlyVisited(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var title: String,
    var webURL: String,
    var baseURL: String,
    var imgURL: String,
    var infoForSaving: String,
    val userAgent: String? = null,
)

@Serializable
@Entity(tableName = "panel_folder")
data class PanelFolder(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var folderId: Long,
    var panelPosition: Long,
    val folderName: String,
    var connectedPanelId: Long
)

@Serializable
@Entity("panel")
data class Panel(
    @PrimaryKey(autoGenerate = true) var panelId: Long = 0,
    val panelName: String
)

@Entity(
    tableName = "site_specific_user_agent",
    indices = [Index(value = ["domain"], unique = true)]
)
data class SiteSpecificUserAgent(
    val domain: String,
    val userAgent: String,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)