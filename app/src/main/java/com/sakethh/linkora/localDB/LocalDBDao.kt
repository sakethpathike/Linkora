package com.sakethh.linkora.localDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalDBDao {
    @Insert
    suspend fun addANewLinkToSavedLinksOrInFolders(linksTable: LinksTable)

    @Insert
    suspend fun addANewLinkToImpLinks(importantLinks: ImportantLinks)

    @Insert
    suspend fun addANewLinkToArchiveLink(archivedLinks: ArchivedLinks)

    @Query("DELETE from links_table WHERE webURL = :webURL")
    suspend fun deleteALinkFromSavedLinksOrInFolders(webURL: String)

    @Query("DELETE from important_links_table WHERE webURL = :webURL")
    suspend fun deleteALinkFromImpLinks(webURL: String)

    @Query("DELETE from archived_links_table WHERE webURL = :webURL")
    suspend fun deleteALinkFromArchiveLinks(webURL: String)

    @Query("DELETE from folders_table WHERE folderName = :folderName")
    suspend fun deleteAFolder(folderName: String)

    @Query("DELETE from important_folders_table WHERE folderName = :folderName")
    suspend fun deleteAnImpFolder(folderName: String)

    @Query("DELETE from archived_folders_table WHERE folderName = :folderName")
    suspend fun deleteAnArchiveFolder(folderName: String)

    @Query("SELECT * FROM links_table WHERE isLinkedWithSavedLinks = 1")
    fun getAllSavedLinks(): Flow<List<LinksTable>>

    @Query("SELECT * FROM important_links_table")
    fun getAllImpLinks(): Flow<List<ImportantLinks>>

    @Query("SELECT * FROM archived_links_table")
    fun getAllArchiveLinks(): Flow<List<ArchivedLinks>>

    @Query("SELECT * FROM archived_folders_table")
    fun getAllArchiveFolders(): Flow<List<ArchivedFolders>>


    @Query("SELECT * FROM links_table WHERE isLinkedWithArchivedFolder=1")
    fun getAllArchiveFoldersData(): Flow<List<LinksTable>>

    @Query("SELECT * FROM folders_table")
    fun getAllFolders(): Flow<List<FoldersTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolder=:folderName")
    fun getThisFolderData(folderName: String): Flow<List<LinksTable>>
}