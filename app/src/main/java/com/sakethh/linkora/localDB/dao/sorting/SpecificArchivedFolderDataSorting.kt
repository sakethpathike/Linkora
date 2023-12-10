package com.sakethh.linkora.localDB.dao.sorting

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.LinksTable
import kotlinx.coroutines.flow.Flow

@Dao
interface SpecificArchivedFolderDataSorting {

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolderV10 = :folderID ORDER BY title COLLATE NOCASE ASC")
    fun sortLinksByAToZV10(folderID: Long?): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolderV10 = :folderID ORDER BY title COLLATE NOCASE DESC")
    fun sortLinksByZToAV10(folderID: Long?): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolderV10 = :folderID ORDER BY id COLLATE NOCASE DESC")
    fun sortLinksByLatestToOldestV10(folderID: Long?): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolderV10 = :folderID ORDER BY id COLLATE NOCASE ASC")
    fun sortLinksByOldestToLatestV10(folderID: Long?): Flow<List<LinksTable>>

    @Query("SELECT * FROM folders_table WHERE parentFolderID = :parentFolderID ORDER BY folderName COLLATE NOCASE ASC")
    fun sortSubFoldersByAToZ(parentFolderID: Long): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table WHERE parentFolderID = :parentFolderID ORDER BY folderName COLLATE NOCASE DESC")
    fun sortSubFoldersByZToA(parentFolderID: Long): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table WHERE parentFolderID = :parentFolderID ORDER BY id COLLATE NOCASE DESC")
    fun sortSubFoldersByLatestToOldest(parentFolderID: Long): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table WHERE parentFolderID = :parentFolderID ORDER BY id COLLATE NOCASE ASC")
    fun sortSubFoldersByOldestToLatest(parentFolderID: Long): Flow<List<FoldersTable>>
}