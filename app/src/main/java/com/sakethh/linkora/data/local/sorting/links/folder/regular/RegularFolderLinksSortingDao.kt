package com.sakethh.linkora.data.local.sorting.links.folder.regular

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.data.local.LinksTable
import kotlinx.coroutines.flow.Flow

@Dao
interface RegularFolderLinksSortingDao {

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolderV10 = :folderID ORDER BY title COLLATE NOCASE ASC")
    fun sortByAToZV10(folderID: Long): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolderV10=:folderID ORDER BY title COLLATE NOCASE DESC")
    fun sortByZToAV10(folderID: Long): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolderV10=:folderID ORDER BY id COLLATE NOCASE DESC")
    fun sortByLatestToOldestV10(folderID: Long): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolderV10=:folderID ORDER BY id COLLATE NOCASE ASC")
    fun sortByOldestToLatestV10(folderID: Long): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 ORDER BY title COLLATE NOCASE ASC")
    fun sortByAToZV10(): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 ORDER BY title COLLATE NOCASE DESC")
    fun sortByZToAV10(): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 ORDER BY id COLLATE NOCASE DESC")
    fun sortByLatestToOldestV10(): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 ORDER BY id COLLATE NOCASE ASC")
    fun sortByOldestToLatestV10(): Flow<List<LinksTable>>
}