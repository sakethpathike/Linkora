package com.sakethh.linkora.localDB.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "links_table")
data class LinksTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val webURL: String,
    val baseURL: String,
    val imgURL: String,
    val infoForSaving: String,

    var isLinkedWithSavedLinks: Boolean,

    var isLinkedWithFolders: Boolean,
    var keyOfLinkedFolder: Long,

    var isLinkedWithImpFolder: Boolean,
    var keyOfImpLinkedFolder: Long,

    var isLinkedWithArchivedFolder: Boolean,
    var keyOfArchiveLinkedFolder: Long,
)

@Serializable
@Entity(tableName = "folders_table")
data class FoldersTable(
    var folderName: String,
    var infoForSaving: String,

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var parentFolderID: Long?,
    var childFolderIDs: List<Long>,
    var isFolderArchived: Boolean = false
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
    val id: Long = 0,
    var title: String,
    var webURL: String,
    var baseURL: String,
    var imgURL: String,
    var infoForSaving: String,
)