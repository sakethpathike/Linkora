package com.sakethh.linkora.localDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sakethh.linkora.localDB.dao._import.ImportDao
import com.sakethh.linkora.localDB.dao.crud.CreateDao
import com.sakethh.linkora.localDB.dao.crud.DeleteDao
import com.sakethh.linkora.localDB.dao.crud.ReadDao
import com.sakethh.linkora.localDB.dao.crud.UpdateDao
import com.sakethh.linkora.localDB.dao.homeLists.HomeListsCRUD
import com.sakethh.linkora.localDB.dao.searching.LinksSearching
import com.sakethh.linkora.localDB.dao.sorting.folders.ParentArchivedFoldersSorting
import com.sakethh.linkora.localDB.dao.sorting.folders.ParentRegularFoldersSorting
import com.sakethh.linkora.localDB.dao.sorting.folders.SubFoldersSorting
import com.sakethh.linkora.localDB.dao.sorting.links.ArchivedFolderLinksSorting
import com.sakethh.linkora.localDB.dao.sorting.links.ArchivedLinksSorting
import com.sakethh.linkora.localDB.dao.sorting.links.HistoryLinksSorting
import com.sakethh.linkora.localDB.dao.sorting.links.ImportantLinksSorting
import com.sakethh.linkora.localDB.dao.sorting.links.RegularFolderLinksSorting
import com.sakethh.linkora.localDB.dao.sorting.links.SavedLinksSorting
import com.sakethh.linkora.localDB.dto.ArchivedFolders
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.HomeScreenListTable
import com.sakethh.linkora.localDB.dto.ImportantFolders
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.localDB.dto.RecentlyVisited
import com.sakethh.linkora.localDB.typeConverters.ChildIDFolderTypeConverter

@Database(
    version = 4,
    exportSchema = true,
    entities = [HomeScreenListTable::class, FoldersTable::class, LinksTable::class, ArchivedFolders::class, ArchivedLinks::class, ImportantFolders::class, ImportantLinks::class, RecentlyVisited::class]
)
@TypeConverters(ChildIDFolderTypeConverter::class)
abstract class LocalDataBase : RoomDatabase() {
    abstract fun createDao(): CreateDao
    abstract fun readDao(): ReadDao
    abstract fun updateDao(): UpdateDao
    abstract fun deleteDao(): DeleteDao
    abstract fun archivedFolderSorting(): ParentArchivedFoldersSorting
    abstract fun archivedLinksSorting(): ArchivedLinksSorting
    abstract fun archivedFolderLinksSorting(): ArchivedFolderLinksSorting
    abstract fun importantLinksSorting(): ImportantLinksSorting
    abstract fun regularFolderLinksSorting(): RegularFolderLinksSorting
    abstract fun regularFolderSorting(): ParentRegularFoldersSorting
    abstract fun savedLinksSorting(): SavedLinksSorting
    abstract fun historyLinksSorting(): HistoryLinksSorting
    abstract fun linksSearching(): LinksSearching
    abstract fun importDao(): ImportDao
    abstract fun subFoldersSortingDao(): SubFoldersSorting
    abstract fun homeListsCrud(): HomeListsCRUD

    companion object {
        lateinit var localDB: LocalDataBase

        @Volatile
        private var dbInstance: LocalDataBase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("DROP TABLE IF EXISTS new_folders_table;")
                db.execSQL("CREATE TABLE IF NOT EXISTS new_folders_table (folderName TEXT NOT NULL, infoForSaving TEXT NOT NULL, id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL);")
                db.execSQL("INSERT INTO new_folders_table (folderName, infoForSaving) SELECT folderName, infoForSaving FROM folders_table;")
                db.execSQL("DROP TABLE IF EXISTS folders_table;")
                db.execSQL("ALTER TABLE new_folders_table RENAME TO folders_table;")

                db.execSQL("DROP TABLE IF EXISTS new_archived_links_table;")
                db.execSQL("CREATE TABLE IF NOT EXISTS new_archived_links_table (title TEXT NOT NULL, webURL TEXT NOT NULL, baseURL TEXT NOT NULL, imgURL TEXT NOT NULL, infoForSaving TEXT NOT NULL, id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL);")
                db.execSQL("INSERT INTO new_archived_links_table (title, webURL, baseURL, imgURL, infoForSaving) SELECT title, webURL, baseURL, imgURL, infoForSaving FROM archived_links_table;")
                db.execSQL("DROP TABLE IF EXISTS archived_links_table;")
                db.execSQL("ALTER TABLE new_archived_links_table RENAME TO archived_links_table;")

                db.execSQL("DROP TABLE IF EXISTS new_archived_folders_table;")
                db.execSQL("CREATE TABLE IF NOT EXISTS new_archived_folders_table (archiveFolderName TEXT NOT NULL, infoForSaving TEXT NOT NULL, id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL);")
                db.execSQL("INSERT INTO new_archived_folders_table (archiveFolderName, infoForSaving) SELECT archiveFolderName, infoForSaving FROM archived_folders_table;")
                db.execSQL("DROP TABLE IF EXISTS archived_folders_table;")
                db.execSQL("ALTER TABLE new_archived_folders_table RENAME TO archived_folders_table;")

                db.execSQL("DROP TABLE IF EXISTS new_important_links_table;")
                db.execSQL("CREATE TABLE IF NOT EXISTS new_important_links_table (title TEXT NOT NULL, webURL TEXT NOT NULL, baseURL TEXT NOT NULL, imgURL TEXT NOT NULL, infoForSaving TEXT NOT NULL, id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL);")
                db.execSQL("INSERT INTO new_important_links_table (title, webURL, baseURL, imgURL, infoForSaving) SELECT title, webURL, baseURL, imgURL, infoForSaving FROM important_links_table;")
                db.execSQL("DROP TABLE IF EXISTS important_links_table;")
                db.execSQL("ALTER TABLE new_important_links_table RENAME TO important_links_table;")

                db.execSQL("DROP TABLE IF EXISTS new_important_folders_table;")
                db.execSQL("CREATE TABLE IF NOT EXISTS new_important_folders_table (impFolderName TEXT NOT NULL, infoForSaving TEXT NOT NULL, id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL);")
                db.execSQL("INSERT INTO new_important_folders_table (impFolderName, infoForSaving) SELECT impFolderName, infoForSaving FROM important_folders_table;")
                db.execSQL("DROP TABLE IF EXISTS important_folders_table;")
                db.execSQL("ALTER TABLE new_important_folders_table RENAME TO important_folders_table;")

            }

        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("DROP TABLE IF EXISTS folders_table_new")
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `folders_table_new` (`folderName` TEXT NOT NULL, `infoForSaving` TEXT NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `parentFolderID` INTEGER DEFAULT NULL, `childFolderIDs` TEXT DEFAULT NULL, `isFolderArchived` INTEGER NOT NULL DEFAULT 0, `isMarkedAsImportant` INTEGER NOT NULL DEFAULT 0)"
                )
                db.execSQL(
                    "INSERT INTO folders_table_new (folderName, infoForSaving, id) " + "SELECT folderName, infoForSaving, id FROM folders_table"
                )
                db.execSQL("DROP TABLE folders_table")
                db.execSQL("ALTER TABLE folders_table_new RENAME TO folders_table")


                db.execSQL("DROP TABLE IF EXISTS links_table_new")
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `links_table_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `webURL` TEXT NOT NULL, `baseURL` TEXT NOT NULL, `imgURL` TEXT NOT NULL, `infoForSaving` TEXT NOT NULL, `isLinkedWithSavedLinks` INTEGER NOT NULL, `isLinkedWithFolders` INTEGER NOT NULL, `keyOfLinkedFolderV10` INTEGER DEFAULT NULL, `keyOfLinkedFolder` TEXT, `isLinkedWithImpFolder` INTEGER NOT NULL, `keyOfImpLinkedFolder` TEXT NOT NULL, `keyOfImpLinkedFolderV10` INTEGER DEFAULT NULL, `isLinkedWithArchivedFolder` INTEGER NOT NULL, `keyOfArchiveLinkedFolderV10` INTEGER DEFAULT NULL, `keyOfArchiveLinkedFolder` TEXT)"
                )
                db.execSQL(
                    "INSERT INTO links_table_new (id, title, webURL, baseURL, imgURL, infoForSaving, " + "isLinkedWithSavedLinks, isLinkedWithFolders, keyOfLinkedFolder, " + "isLinkedWithImpFolder, keyOfImpLinkedFolder, " + "isLinkedWithArchivedFolder, keyOfArchiveLinkedFolder) " + "SELECT id, title, webURL, baseURL, imgURL, infoForSaving, " + "isLinkedWithSavedLinks, isLinkedWithFolders, keyOfLinkedFolder, " + "isLinkedWithImpFolder, keyOfImpLinkedFolder," + "isLinkedWithArchivedFolder, keyOfArchiveLinkedFolder " + "FROM links_table"
                )
                db.execSQL("DROP TABLE links_table")
                db.execSQL("ALTER TABLE links_table_new RENAME TO links_table")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS home_screen_list_table_new " +
                            "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "folderName TEXT NOT NULL, position INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS home_screen_list_table (\n" +
                            "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                            "    position INTEGER,\n" +
                            "    folderName TEXT\n" +
                            ");\n"
                )
                db.execSQL(
                    "INSERT INTO home_screen_list_table_new (id, folderName, position) " +
                            "SELECT id, folderName FROM home_screen_list_table"
                )
                db.execSQL("DROP TABLE IF EXISTS home_screen_list_table")
                db.execSQL("ALTER TABLE home_screen_list_table_new RENAME TO home_screen_list_table")

            }

        }

        fun getLocalDB(context: Context): LocalDataBase {
            val instance = dbInstance
            return instance ?: synchronized(this) {
                val roomDBInstance = Room.databaseBuilder(
                    context.applicationContext, LocalDataBase::class.java, "linkora_db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).build()
                dbInstance = roomDBInstance
                return roomDBInstance
            }
        }
    }
}