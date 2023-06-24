package com.sakethh.linkora.localDB

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "links_table",
    foreignKeys = [
        ForeignKey(
            entity = FoldersTable::class,
            parentColumns = ["folderName"],
            childColumns = ["keyOfLinkedFolder"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ImportantFolders::class,
            parentColumns = ["folderName"],
            childColumns = ["keyOfLinkedFolder"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ArchivedFolders::class,
            parentColumns = ["folderName"],
            childColumns = ["keyOfLinkedFolder"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
    ]
)
data class LinksTable(
    val title: String,
    @PrimaryKey val webURL: String,
    val baseURL: String,
    val imgURL: String,
    val infoForSaving: String,

    val isLinkedWithSavedLinks: Boolean,

    val isLinkedWithFolders: Boolean,
    val keyOfLinkedFolder: String,

    val isLinkedWithImpFolder: Boolean,
    val keyOfImpLinkedFolder: String,

    val isLinkedWithArchivedFolder: Boolean,
    val keyOfArchiveLinkedFolder: String,
)

@Entity(tableName = "folders_table")
data class FoldersTable(
    @PrimaryKey val folderName: String,
)

@Entity(tableName = "archived_links_table")
data class ArchivedLinks(
    val title: String,
    @PrimaryKey val webURL: String,
    val baseURL: String,
    val imgURL: String,
    val infoForSaving: String,
)

@Entity(tableName = "archived_folders_table")
data class ArchivedFolders(
    @PrimaryKey val folderName: String,
)

@Entity(tableName = "important_links_table")
data class ImportantLinks(
    val title: String,
    @PrimaryKey val webURL: String,
    val baseURL: String,
    val imgURL: String,
    val infoForSaving: String,
)

@Entity(tableName = "important_folders_table")
data class ImportantFolders(
    @PrimaryKey val folderName: String,
)