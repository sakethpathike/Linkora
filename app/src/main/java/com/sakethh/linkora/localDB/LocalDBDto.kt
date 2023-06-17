package com.sakethh.linkora.localDB

import androidx.room.Entity

@Entity(tableName = "links_table")
data class Links(
    val title: String,
    val webURL: String,
    val baseURL: String,
    val imgURL: String,
    val isThisLinkImportant: Boolean,
)

@Entity(tableName = "folders_table")
data class Folders(
    val folderName: String,
    val links: List<Links>,
)

@Entity(tableName = "archived_links_table")
data class ArchivedLinks(val links: List<Links>)

@Entity(tableName = "archived_links_table")
data class ArchivedFolders(val folders: List<Folders>)

@Entity(tableName = "important_links_table")
data class ImportantLinks(val links: List<Links>)

@Entity(tableName = "important_links_table")
data class ImportantFolders(val folders: List<Folders>)