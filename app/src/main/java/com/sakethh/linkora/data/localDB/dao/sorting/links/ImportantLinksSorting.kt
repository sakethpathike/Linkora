package com.sakethh.linkora.data.localDB.dao.sorting.links

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.data.localDB.models.ImportantLinks
import kotlinx.coroutines.flow.Flow

@Dao
interface ImportantLinksSorting {

    @Query("SELECT * FROM important_links_table ORDER BY title COLLATE NOCASE ASC")
    fun sortByAToZ(): Flow<List<ImportantLinks>>

    @Query("SELECT * FROM important_links_table ORDER BY title COLLATE NOCASE DESC")
    fun sortByZToA(): Flow<List<ImportantLinks>>

    @Query("SELECT * FROM important_links_table ORDER BY id COLLATE NOCASE DESC")
    fun sortByLatestToOldest(): Flow<List<ImportantLinks>>

    @Query("SELECT * FROM important_links_table ORDER BY id COLLATE NOCASE ASC")
    fun sortByOldestToLatest(): Flow<List<ImportantLinks>>

}