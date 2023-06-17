package com.sakethh.linkora.localDB

import androidx.room.Dao

@Dao
interface LocalDBDao {
    fun getAllLinks()
}