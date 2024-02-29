package com.sakethh.linkora.localDB.dto

import androidx.room.Entity
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
    var keyOfArchiveLinkedFolder: String? = null
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
)

@Serializable
@Entity(tableName = "home_screen_list_table")
data class HomeScreenListTable(
    @PrimaryKey(autoGenerate = true) var primaryKey: Long = 0,
    val id: Long,
    var position: Long,
    val folderName: String,
    val parentShelfID: Long
)

@Serializable
@Entity("shelf")
data class Shelf(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val shelfName: String,
    val shelfIconName: String,
    val folderIds: List<Long>
)