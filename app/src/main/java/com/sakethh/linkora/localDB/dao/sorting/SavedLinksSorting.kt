package com.sakethh.linkora.localDB.dao.sorting

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.localDB.LinksTable
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedLinksSorting {

    @Query("SELECT * FROM links_table WHERE isLinkedWithSavedLinks=1 ORDER BY title ASC")
    fun sortByAToZ(): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithSavedLinks=1 ORDER BY title DESC")
    fun sortByZToA(): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithSavedLinks=1 ORDER BY id DESC")
    fun sortByLatestToOldest(): Flow<List<LinksTable>>

    @Query("SELECT * FROM links_table WHERE isLinkedWithSavedLinks=1 ORDER BY id ASC")
    fun sortByOldestToLatest(): Flow<List<LinksTable>>

}