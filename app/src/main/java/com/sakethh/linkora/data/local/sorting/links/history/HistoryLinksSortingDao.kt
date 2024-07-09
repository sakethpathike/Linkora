package com.sakethh.linkora.data.local.sorting.links.history

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.data.local.RecentlyVisited
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryLinksSortingDao {

    @Query("SELECT * FROM recently_visited_table ORDER BY title COLLATE NOCASE ASC")
    fun sortByAToZ(): Flow<List<RecentlyVisited>>

    @Query("SELECT * FROM recently_visited_table ORDER BY title COLLATE NOCASE DESC")
    fun sortByZToA(): Flow<List<RecentlyVisited>>

    @Query("SELECT * FROM recently_visited_table ORDER BY id COLLATE NOCASE DESC")
    fun sortByLatestToOldest(): Flow<List<RecentlyVisited>>

    @Query("SELECT * FROM recently_visited_table ORDER BY id COLLATE NOCASE ASC")
    fun sortByOldestToLatest(): Flow<List<RecentlyVisited>>

}