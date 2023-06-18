package com.sakethh.linkora.localDB

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.Serializable

@Entity(tableName = "links_table")
@Serializable
data class LinksTable(
    val title: String,
    @PrimaryKey
    val webURL: String,
    val baseURL: String,
    val imgURL: String,
    var isThisLinkImportant: Boolean,
    val dateSavedOn: String,
    val timeSavedOn: String,
    val infoForSaving: String,
)

@Entity(tableName = "folders_table")
@Serializable
@TypeConverters(LinksTypeConverter::class)
data class FoldersTable(
    @PrimaryKey
    val folderName: String,
    val infoForSaving: String,
    @TypeConverters(LinksTypeConverter::class)
    val links: List<LinksTable>,
)

@Entity(tableName = "archived_links_table")
@Serializable
@TypeConverters(LinkTypeConverter::class)
data class ArchivedLinks(
    @PrimaryKey val id: Int,
    @TypeConverters(LinkTypeConverter::class)
    val linkData: LinksTable,
)

@Entity(tableName = "archived_folders_table")
@Serializable
@TypeConverters(FolderTypeConverter::class)
data class ArchivedFolders(
    @PrimaryKey val id: Int,
    @TypeConverters(FolderTypeConverter::class)
    val folderData: FoldersTable,
)

@Entity(tableName = "important_links_table")
@Serializable
@TypeConverters(LinksTypeConverter::class)
data class ImportantLinks(
    @PrimaryKey var link: String,
    @TypeConverters(LinkTypeConverter::class)
    var linkData: LinksTable,
)

@Entity(tableName = "important_folders_table")
@Serializable
@TypeConverters(FolderTypeConverter::class)
data class ImportantFolders(
    @PrimaryKey val id: Int,
    @TypeConverters(FolderTypeConverter::class) val folderData: FoldersTable,
)