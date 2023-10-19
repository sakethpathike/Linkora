package com.sakethh.linkora.localDB.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

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
    var keyOfLinkedFolder: String,

    var isLinkedWithImpFolder: Boolean,
    var keyOfImpLinkedFolder: String,

    var isLinkedWithArchivedFolder: Boolean,
    var keyOfArchiveLinkedFolder: String,
)

@Entity(tableName = "folders_table")
data class FoldersTable(
    var folderName: String,
    var infoForSaving: String,

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
)

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

@Entity(tableName = "archived_folders_table")
data class ArchivedFolders(
    val archiveFolderName: String,
    val infoForSaving: String,

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
)

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

@Entity(tableName = "important_folders_table")
data class ImportantFolders(
    val impFolderName: String,
    val infoForSaving: String,

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
)

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