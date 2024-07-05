package com.sakethh.linkora.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sakethh.linkora.data.local.folders.FoldersDao
import com.sakethh.linkora.data.local.links.LinksDao
import com.sakethh.linkora.data.local.search.SearchDao
import com.sakethh.linkora.data.local.shelf.ShelfDao
import com.sakethh.linkora.data.local.typeConverters.LongToStringConverter

@Database(
    version = 5,
    exportSchema = true,
    entities = [Shelf::class, HomeScreenListTable::class, FoldersTable::class, LinksTable::class, ArchivedFolders::class, ArchivedLinks::class, ImportantFolders::class, ImportantLinks::class, RecentlyVisited::class]
)
@TypeConverters(LongToStringConverter::class)
abstract class LocalDatabase : RoomDatabase() {
    /* abstract fun createDao(): CreateDao
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
     abstract fun searchDao(): SearchDao
     abstract fun importDao(): ImportDao
     abstract fun subFoldersSortingDao(): SubFoldersSorting
     abstract fun shelfFolders(): ShelfListsCRUD
     abstract fun shelfCrud(): ShelfCRUD*/
    abstract fun foldersDao(): FoldersDao
    abstract fun linksDao(): LinksDao
    abstract fun shelfDao(): ShelfDao
    abstract fun searchDao(): SearchDao
}