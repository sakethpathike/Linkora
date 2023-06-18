package com.sakethh.linkora.localDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    version = 1,
    exportSchema = true,
    entities = [FoldersTable::class, LinksTable::class, ArchivedFolders::class,
        ArchivedLinks::class, ImportantFolders::class, ImportantLinks::class]
)
@TypeConverters(
    FoldersTypeConverter::class,
    LinksTypeConverter::class,
    LinkTypeConverter::class,
    FolderTypeConverter::class
)
abstract class LocalDataBase : RoomDatabase() {
    abstract fun localDBData(): LocalDBDao

    companion object {
        @Volatile
        private var dbInstance: LocalDataBase? = null
        fun getLocalDB(context: Context): LocalDataBase {
            val instance = dbInstance
            return instance
                ?: synchronized(this) {
                    val roomDBInstance = Room.databaseBuilder(
                        context.applicationContext,
                        LocalDataBase::class.java,
                        "linkora_db"
                    ).build()
                    dbInstance = roomDBInstance
                    return roomDBInstance
                }
        }
    }
}