package com.sakethh.linkora.localDB.dao.sorting

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.localDB.LinksTable
import kotlinx.coroutines.flow.Flow

@Dao
interface RegularFolderLinksSorting {

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolder = :folderName ORDER BY title COLLATE NOCASE ASC")
    fun sortByAToZ(folderName: String): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithSavedLinks=1 AND keyOfLinkedFolder=:folderName ORDER BY title COLLATE NOCASE DESC")
    fun sortByZToA(folderName: String): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithSavedLinks=1 AND keyOfLinkedFolder=:folderName ORDER BY id COLLATE NOCASE DESC")
    fun sortByLatestToOldest(folderName: String): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithSavedLinks=1 AND keyOfLinkedFolder=:folderName ORDER BY id COLLATE NOCASE ASC")
    fun sortByOldestToLatest(folderName: String): Flow<List<LinksTable>>
}