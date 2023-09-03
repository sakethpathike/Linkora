package com.sakethh.linkora.localDB.dao.sorting

import androidx.room.Dao
import androidx.room.Query
import com.sakethh.linkora.localDB.ArchivedLinks
import kotlinx.coroutines.flow.Flow

@Dao
interface ArchivedLinksSorting {

    @Query("SELECT * FROM archived_links_table ORDER BY title ASC")
    fun sortByAToZ(): Flow<List<ArchivedLinks>>

    @Query("SELECT * FROM archived_links_table ORDER BY title DESC")
    fun sortByZToA(): Flow<List<ArchivedLinks>>

}