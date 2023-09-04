package com.sakethh.linkora.localDB.dao.sorting

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.localDB.RecentlyVisited
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryLinksSorting {

    @Query("SELECT * FROM recently_visited_table ORDER BY title ASC")
    fun sortByAToZ(): Flow<List<RecentlyVisited>>

    @Query("SELECT * FROM recently_visited_table ORDER BY title DESC")
    fun sortByZToA(): Flow<List<RecentlyVisited>>

    @Query("SELECT * FROM recently_visited_table ORDER BY id DESC")
    fun sortByLatestToOldest(): Flow<List<RecentlyVisited>>

    @Query("SELECT * FROM recently_visited_table ORDER BY id ASC")
    fun sortByOldestToLatest(): Flow<List<RecentlyVisited>>

}