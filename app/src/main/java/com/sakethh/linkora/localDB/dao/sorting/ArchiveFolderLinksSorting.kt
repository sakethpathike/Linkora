package com.sakethh.linkora.localDB.dao.sorting

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.localDB.dto.LinksTable
import kotlinx.coroutines.flow.Flow

@Dao
interface ArchiveFolderLinksSorting {

    @Query("SELECT * FROM links_table WHERE isLinkedWithArchivedFolder=1 AND keyOfArchiveLinkedFolder = :folderID ORDER BY title COLLATE NOCASE ASC")
    fun sortByAToZ(folderID: Long?): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithArchivedFolder=1 AND keyOfArchiveLinkedFolder=:folderID ORDER BY title COLLATE NOCASE DESC")
    fun sortByZToA(folderID: Long?): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithArchivedFolder=1 AND keyOfArchiveLinkedFolder=:folderID ORDER BY id COLLATE NOCASE DESC")
    fun sortByLatestToOldest(folderID: Long?): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithArchivedFolder=1 AND keyOfArchiveLinkedFolder=:folderID ORDER BY id COLLATE NOCASE ASC")
    fun sortByOldestToLatest(folderID: Long?): Flow<List<LinksTable>>

}