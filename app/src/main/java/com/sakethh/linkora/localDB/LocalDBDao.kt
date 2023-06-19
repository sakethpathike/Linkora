package com.sakethh.linkora.localDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalDBDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNewLink(linkData: LinksTable)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addALinkToImportant(importantLinks: ImportantLinks)

    @Query("DELETE from important_links_table WHERE link = :url")
    suspend fun removeALinkFromImportant(url: String)

    @Query("SELECT EXISTS(SELECT * FROM important_links_table WHERE link = :url)")
    suspend fun doesThisLinkMarkedAsImportant(url: String): Boolean

    @Query("UPDATE folders_table SET links = json_insert(links, '$[#]', json(:newLinkData)) WHERE folderName = :folderName")
    suspend fun addANewLinkInAFolder(
        folderName: String,
        @TypeConverters(LinkTypeConverter::class)
        newLinkData: LinksTable,
    )

    /*suspend fun deleteALinkFromAFolder(
        folderName: String,
        link: String,
    )*/

    @Insert
    suspend fun addNewFolder(newFolderData: FoldersTable)

    @Query("UPDATE folders_table SET folderName = :newName WHERE folderName = :existingName")
    suspend fun renameFolderName(existingName: String, newName: String)

    @Query("UPDATE links_table SET title = :newTitle WHERE webURL = :webURL")
    suspend fun changeLinkTitle(newTitle: String, webURL: String)

    @Query("UPDATE folders_table SET infoForSaving = :newNote WHERE folderName = :folderName")
    suspend fun renameFolderNote(folderName: String, newNote: String)

    @Query("SELECT EXISTS(SELECT * FROM folders_table WHERE folderName = :folderName)")
    suspend fun doesThisFolderExists(folderName: String): Boolean

    @Query("SELECT * FROM links_table")
    fun getAllLinks(): Flow<List<LinksTable>>

    @Query("SELECT * FROM important_links_table")
    fun getAllImportantLinks(): Flow<List<ImportantLinks>>

    @Query("SELECT * FROM folders_table")
    fun getAllFolders(): Flow<List<FoldersTable>>

    @Query("DELETE from links_table WHERE webURL = :link")
    suspend fun deleteThisLink(link: String)

    @Query("DELETE from folders_table WHERE folderName = :folderName")
    suspend fun deleteThisFolder(folderName: String)

    @Query("DELETE FROM links_table")
    suspend fun deleteAllLinks()

    @Query("DELETE FROM folders_table")
    suspend fun deleteAllFolders()
}