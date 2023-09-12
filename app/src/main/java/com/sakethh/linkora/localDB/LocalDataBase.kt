package com.sakethh.linkora.localDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sakethh.linkora.localDB.dao.CRUDDao
import com.sakethh.linkora.localDB.dao.searching.LinksSearching
import com.sakethh.linkora.localDB.dao.sorting.ArchiveFolderLinksSorting
import com.sakethh.linkora.localDB.dao.sorting.ArchivedFoldersSorting
import com.sakethh.linkora.localDB.dao.sorting.ArchivedLinksSorting
import com.sakethh.linkora.localDB.dao.sorting.HistoryLinksSorting
import com.sakethh.linkora.localDB.dao.sorting.ImportantLinksSorting
import com.sakethh.linkora.localDB.dao.sorting.RegularFolderLinksSorting
import com.sakethh.linkora.localDB.dao.sorting.RegularFoldersSorting
import com.sakethh.linkora.localDB.dao.sorting.SavedLinksSorting

@Database(
    version = 2,
    exportSchema = true,
    entities = [FoldersTable::class, LinksTable::class, ArchivedFolders::class, ArchivedLinks::class, ImportantFolders::class, ImportantLinks::class, RecentlyVisited::class]
)
abstract class LocalDataBase : RoomDatabase() {
    abstract fun crudDao(): CRUDDao
    abstract fun archivedFolderSorting(): ArchivedFoldersSorting
    abstract fun archivedLinksSorting(): ArchivedLinksSorting
    abstract fun archivedFolderLinksSorting(): ArchiveFolderLinksSorting
    abstract fun importantLinksSorting(): ImportantLinksSorting
    abstract fun regularFolderLinksSorting(): RegularFolderLinksSorting
    abstract fun regularFolderSorting(): RegularFoldersSorting
    abstract fun savedLinksSorting(): SavedLinksSorting
    abstract fun historyLinksSorting(): HistoryLinksSorting
    abstract fun linksSearching(): LinksSearching

    companion object {
        @Volatile
        private var dbInstance: LocalDataBase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {

                database.execSQL("DROP TABLE IF EXISTS new_folders_table;")
                database.execSQL("CREATE TABLE IF NOT EXISTS new_folders_table (folderName TEXT NOT NULL, infoForSaving TEXT NOT NULL, id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL);")
                database.execSQL("INSERT INTO new_folders_table (folderName, infoForSaving) SELECT folderName, infoForSaving FROM folders_table;")
                database.execSQL("DROP TABLE IF EXISTS folders_table;")
                database.execSQL("ALTER TABLE new_folders_table RENAME TO folders_table;")

                database.execSQL("DROP TABLE IF EXISTS new_archived_links_table;")
                database.execSQL("CREATE TABLE IF NOT EXISTS new_archived_links_table (title TEXT NOT NULL, webURL TEXT NOT NULL, baseURL TEXT NOT NULL, imgURL TEXT NOT NULL, infoForSaving TEXT NOT NULL, id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL);")
                database.execSQL("INSERT INTO new_archived_links_table (title, webURL, baseURL, imgURL, infoForSaving) SELECT title, webURL, baseURL, imgURL, infoForSaving FROM archived_links_table;")
                database.execSQL("DROP TABLE IF EXISTS archived_links_table;")
                database.execSQL("ALTER TABLE new_archived_links_table RENAME TO archived_links_table;")

                database.execSQL("DROP TABLE IF EXISTS new_archived_folders_table;")
                database.execSQL("CREATE TABLE IF NOT EXISTS new_archived_folders_table (archiveFolderName TEXT NOT NULL, infoForSaving TEXT NOT NULL, id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL);")
                database.execSQL("INSERT INTO new_archived_folders_table (archiveFolderName, infoForSaving) SELECT archiveFolderName, infoForSaving FROM archived_folders_table;")
                database.execSQL("DROP TABLE IF EXISTS archived_folders_table;")
                database.execSQL("ALTER TABLE new_archived_folders_table RENAME TO archived_folders_table;")

                database.execSQL("DROP TABLE IF EXISTS new_important_links_table;")
                database.execSQL("CREATE TABLE IF NOT EXISTS new_important_links_table (title TEXT NOT NULL, webURL TEXT NOT NULL, baseURL TEXT NOT NULL, imgURL TEXT NOT NULL, infoForSaving TEXT NOT NULL, id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL);")
                database.execSQL("INSERT INTO new_important_links_table (title, webURL, baseURL, imgURL, infoForSaving) SELECT title, webURL, baseURL, imgURL, infoForSaving FROM important_links_table;")
                database.execSQL("DROP TABLE IF EXISTS important_links_table;")
                database.execSQL("ALTER TABLE new_important_links_table RENAME TO important_links_table;")

                database.execSQL("DROP TABLE IF EXISTS new_important_folders_table;")
                database.execSQL("CREATE TABLE IF NOT EXISTS new_important_folders_table (impFolderName TEXT NOT NULL, infoForSaving TEXT NOT NULL, id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL);")
                database.execSQL("INSERT INTO new_important_folders_table (impFolderName, infoForSaving) SELECT impFolderName, infoForSaving FROM important_folders_table;")
                database.execSQL("DROP TABLE IF EXISTS important_folders_table;")
                database.execSQL("ALTER TABLE new_important_folders_table RENAME TO important_folders_table;")

            }

        }

        fun getLocalDB(context: Context): LocalDataBase {
            val instance = dbInstance
            return instance ?: synchronized(this) {
                val roomDBInstance = Room.databaseBuilder(
                    context.applicationContext, LocalDataBase::class.java, "linkora_db"
                ).addMigrations(MIGRATION_1_2).build()
                dbInstance = roomDBInstance
                return roomDBInstance
            }
        }
    }
}