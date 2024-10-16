package com.sakethh.linkora.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sakethh.linkora.data.local.folders.FoldersDao
import com.sakethh.linkora.data.local.links.LinksDao
import com.sakethh.linkora.data.local.localization.language.Language
import com.sakethh.linkora.data.local.localization.language.LanguageDao
import com.sakethh.linkora.data.local.localization.language.translations.Translation
import com.sakethh.linkora.data.local.localization.language.translations.TranslationsDao
import com.sakethh.linkora.data.local.restore.ImportDao
import com.sakethh.linkora.data.local.search.SearchDao
import com.sakethh.linkora.data.local.panels.PanelsDao
import com.sakethh.linkora.data.local.site_specific_user_agent.SiteSpecificUserAgentDao
import com.sakethh.linkora.data.local.sorting.folders.archive.ParentArchivedFoldersSortingDao
import com.sakethh.linkora.data.local.sorting.folders.regular.ParentRegularFoldersSortingDao
import com.sakethh.linkora.data.local.sorting.folders.subfolders.SubFoldersSortingDao
import com.sakethh.linkora.data.local.sorting.links.archive.ArchivedLinksSortingDao
import com.sakethh.linkora.data.local.sorting.links.folder.archive.ArchivedFolderLinksSortingDao
import com.sakethh.linkora.data.local.sorting.links.folder.regular.RegularFolderLinksSortingDao
import com.sakethh.linkora.data.local.sorting.links.history.HistoryLinksSortingDao
import com.sakethh.linkora.data.local.sorting.links.important.ImportantLinksSortingDao
import com.sakethh.linkora.data.local.sorting.links.saved.SavedLinksSortingDao

@Database(
    version = 8,
    exportSchema = true,
    entities = [Panel::class, PanelFolder::class, FoldersTable::class, LinksTable::class, ArchivedFolders::class,
        ArchivedLinks::class, ImportantFolders::class, ImportantLinks::class, RecentlyVisited::class,
        Language::class, Translation::class, SiteSpecificUserAgent::class
    ]
)
@TypeConverters(LongToStringConverter::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun foldersDao(): FoldersDao
    abstract fun linksDao(): LinksDao
    abstract fun regularFolderLinksSorting(): RegularFolderLinksSortingDao
    abstract fun panelsDao(): PanelsDao
    abstract fun savedLinksSorting(): SavedLinksSortingDao
    abstract fun archivedFolderLinksSorting(): ArchivedFolderLinksSortingDao
    abstract fun historyLinksSorting(): HistoryLinksSortingDao
    abstract fun importantLinksSorting(): ImportantLinksSortingDao
    abstract fun archivedLinksSorting(): ArchivedLinksSortingDao
    abstract fun archivedFolderSorting(): ParentArchivedFoldersSortingDao
    abstract fun regularFolderSorting(): ParentRegularFoldersSortingDao
    abstract fun subFoldersSortingDao(): SubFoldersSortingDao
    abstract fun searchDao(): SearchDao
    abstract fun importDao(): ImportDao
    abstract fun languageDao(): LanguageDao
    abstract fun translationDao(): TranslationsDao
    abstract fun siteSpecificUserAgentDao(): SiteSpecificUserAgentDao
}