package com.sakethh.linkora.data.local.sorting.links.folder.archive

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.data.local.LinksTable
import kotlinx.coroutines.flow.Flow

@Dao
interface ArchivedFolderLinksSortingDao {

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolderV10 = :folderID ORDER BY title COLLATE NOCASE ASC")
    fun sortLinksByAToZV10(folderID: Long?): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolderV10 = :folderID ORDER BY title COLLATE NOCASE DESC")
    fun sortLinksByZToAV10(folderID: Long?): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolderV10 = :folderID ORDER BY id COLLATE NOCASE DESC")
    fun sortLinksByLatestToOldestV10(folderID: Long?): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithFolders=1 AND keyOfLinkedFolderV10 = :folderID ORDER BY id COLLATE NOCASE ASC")
    fun sortLinksByOldestToLatestV10(folderID: Long?): Flow<List<LinksTable>>
}