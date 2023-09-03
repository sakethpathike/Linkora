package com.sakethh.linkora.localDB.dao.sorting

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.localDB.ImportantLinks
import kotlinx.coroutines.flow.Flow

@Dao
interface ImportantLinksSorting {

    @Query("SELECT * FROM important_links_table ORDER BY title ASC")
    fun sortByAToZ(): Flow<List<ImportantLinks>>

    @Query("SELECT * FROM important_links_table ORDER BY title DESC")
    fun sortByZToA(): Flow<List<ImportantLinks>>

}