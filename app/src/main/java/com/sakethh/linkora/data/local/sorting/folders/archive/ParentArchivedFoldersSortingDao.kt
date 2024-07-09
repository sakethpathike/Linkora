package com.sakethh.linkora.data.local.sorting.folders.archive

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.data.local.FoldersTable
import kotlinx.coroutines.flow.Flow

@Dao
interface ParentArchivedFoldersSortingDao {
    @Query("SELECT * FROM folders_table WHERE isFolderArchived=1 ORDER BY folderName COLLATE NOCASE ASC")
    fun sortByAToZV10(): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table WHERE isFolderArchived=1 ORDER BY folderName COLLATE NOCASE DESC")
    fun sortByZToAV10(): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table WHERE isFolderArchived=1 ORDER BY id COLLATE NOCASE DESC")
    fun sortByLatestToOldestV10(): Flow<List<FoldersTable>>

    @Query("SELECT * FROM folders_table WHERE isFolderArchived=1 ORDER BY id COLLATE NOCASE ASC")
    fun sortByOldestToLatestV10(): Flow<List<FoldersTable>>

}