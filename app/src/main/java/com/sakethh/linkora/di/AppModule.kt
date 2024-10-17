package com.sakethh.linkora.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.WorkManager
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.export.ExportImpl
import com.sakethh.linkora.data.local.export.ExportRepo
import com.sakethh.linkora.data.local.folders.FoldersImpl
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.links.LinksImpl
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.data.local.localization.language.LanguageImpl
import com.sakethh.linkora.data.local.localization.language.LanguageRepo
import com.sakethh.linkora.data.local.localization.language.translations.TranslationsImpl
import com.sakethh.linkora.data.local.localization.language.translations.TranslationsRepo
import com.sakethh.linkora.data.local.panels.PanelsImpl
import com.sakethh.linkora.data.local.panels.PanelsRepo
import com.sakethh.linkora.data.local.restore.ImportImpl
import com.sakethh.linkora.data.local.restore.ImportRepo
import com.sakethh.linkora.data.local.search.SearchImpl
import com.sakethh.linkora.data.local.search.SearchRepo
import com.sakethh.linkora.data.local.site_specific_user_agent.SiteSpecificUserAgentImpl
import com.sakethh.linkora.data.local.site_specific_user_agent.SiteSpecificUserAgentRepo
import com.sakethh.linkora.data.local.sorting.folders.archive.ParentArchivedFoldersSortingImpl
import com.sakethh.linkora.data.local.sorting.folders.archive.ParentArchivedFoldersSortingRepo
import com.sakethh.linkora.data.local.sorting.folders.regular.ParentRegularFoldersSortingImpl
import com.sakethh.linkora.data.local.sorting.folders.regular.ParentRegularFoldersSortingRepo
import com.sakethh.linkora.data.local.sorting.folders.subfolders.SubFoldersSortingImpl
import com.sakethh.linkora.data.local.sorting.folders.subfolders.SubFoldersSortingRepo
import com.sakethh.linkora.data.local.sorting.links.archive.ArchivedLinksSortingImpl
import com.sakethh.linkora.data.local.sorting.links.archive.ArchivedLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.folder.archive.ArchivedFolderLinksSortingImpl
import com.sakethh.linkora.data.local.sorting.links.folder.archive.ArchivedFolderLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.folder.regular.RegularFolderLinksSortingImpl
import com.sakethh.linkora.data.local.sorting.links.folder.regular.RegularFolderLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.history.HistoryLinksSortingImpl
import com.sakethh.linkora.data.local.sorting.links.history.HistoryLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.important.ImportantLinksSortingImpl
import com.sakethh.linkora.data.local.sorting.links.important.ImportantLinksSortingRepo
import com.sakethh.linkora.data.local.sorting.links.saved.SavedLinksSortingImpl
import com.sakethh.linkora.data.local.sorting.links.saved.SavedLinksSortingRepo
import com.sakethh.linkora.data.remote.localization.LocalizationImpl
import com.sakethh.linkora.data.remote.localization.LocalizationRepo
import com.sakethh.linkora.data.remote.metadata.twitter.TwitterMetaDataImpl
import com.sakethh.linkora.data.remote.metadata.twitter.TwitterMetaDataRepo
import com.sakethh.linkora.data.remote.releases.GitHubReleasesImpl
import com.sakethh.linkora.data.remote.releases.GitHubReleasesRepo
import com.sakethh.linkora.data.remote.scrape.LinkMetaDataScrapperImpl
import com.sakethh.linkora.data.remote.scrape.LinkMetaDataScrapperService
import com.sakethh.linkora.ui.CustomWebTab
import com.sakethh.linkora.worker.refreshLinks.RefreshLinksWorkerRequestBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
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
            db.execSQL("CREATE TABLE IF NOT EXISTS `home_screen_list_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `position` INTEGER NOT NULL, `folderName` TEXT NOT NULL, `shouldSavedLinksTabVisible` INTEGER NOT NULL, `shouldImpLinksTabVisible` INTEGER NOT NULL)")
        }
    }
    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("DROP TABLE IF EXISTS home_screen_list_table")
            db.execSQL("CREATE TABLE IF NOT EXISTS `shelf` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `shelfName` TEXT NOT NULL, `shelfIconName` TEXT NOT NULL, `folderIds` TEXT NOT NULL)")
            db.execSQL("CREATE TABLE IF NOT EXISTS `home_screen_list_table` (`primaryKey` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `id` INTEGER NOT NULL, `position` INTEGER NOT NULL, `folderName` TEXT NOT NULL, `parentShelfID` INTEGER NOT NULL)")
        }
    }
    private val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `language` (`languageCode` TEXT NOT NULL, `languageName` TEXT NOT NULL, `localizedStringsCount` INTEGER NOT NULL, `contributionLink` TEXT NOT NULL, PRIMARY KEY(`languageCode`))")
            db.execSQL("CREATE TABLE IF NOT EXISTS `translation` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `languageCode` TEXT NOT NULL, `stringName` TEXT NOT NULL, `stringValue` TEXT NOT NULL)")
        }
    }
    private val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `site_specific_user_agent` (`domain` TEXT NOT NULL, `userAgent` TEXT NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_site_specific_user_agent_domain` ON `site_specific_user_agent` (`domain`)")
            db.execSQL("ALTER TABLE links_table ADD COLUMN userAgent TEXT DEFAULT NULL")
            db.execSQL("ALTER TABLE archived_links_table ADD COLUMN userAgent TEXT DEFAULT NULL")
            db.execSQL("ALTER TABLE important_links_table ADD COLUMN userAgent TEXT DEFAULT NULL")
            db.execSQL("ALTER TABLE recently_visited_table ADD COLUMN userAgent TEXT DEFAULT NULL")
        }
    }

    private val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {

            db.execSQL("CREATE TABLE IF NOT EXISTS `panel` (`panelId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `panelName` TEXT NOT NULL)")

            db.execSQL(
                """
            INSERT INTO panel (panelId, panelName)
            SELECT id, shelfName FROM shelf
        """.trimIndent()
            )

            db.execSQL("DROP TABLE shelf")

            db.execSQL("CREATE TABLE IF NOT EXISTS `panel_folder` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `folderId` INTEGER NOT NULL, `panelPosition` INTEGER NOT NULL, `folderName` TEXT NOT NULL, `connectedPanelId` INTEGER NOT NULL)")


            db.execSQL(
                """
            INSERT INTO panel_folder (folderId, panelPosition, folderName, connectedPanelId)
            SELECT id, position, folderName, parentShelfID FROM home_screen_list_table
            """.trimIndent()
            )

            db.execSQL("DROP TABLE home_screen_list_table")
        }
    }

    @Provides
    @Singleton
    fun provideLocalDatabase(app: Application): LocalDatabase {
        return Room.databaseBuilder(
            app, LocalDatabase::class.java, "linkora_db"
        ).addMigrations(
            MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6,
            MIGRATION_6_7, MIGRATION_7_8
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideCustomWebTab(
        localDatabase: LocalDatabase,
        ktorClient: HttpClient,
        siteSpecificUserAgentRepo: SiteSpecificUserAgentRepo
    ): CustomWebTab {
        return CustomWebTab(provideLinksRepo(localDatabase, ktorClient, siteSpecificUserAgentRepo))
    }

    @Provides
    @Singleton
    fun provideLinksRepo(
        localDatabase: LocalDatabase,
        ktorClient: HttpClient,
        siteSpecificUserAgentRepo: SiteSpecificUserAgentRepo
    ): LinksRepo {
        return LinksImpl(
            localDatabase,
            provideFoldersRepo(localDatabase),
            provideLinkMetaDataScrapperService(),
            provideTwitterMetaDataRepo(ktorClient),
            siteSpecificUserAgentRepo
        )
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideKtorClient(): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    explicitNulls = false
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    @Provides
    @Singleton
    fun provideGitHubReleasesRepo(ktorClient: HttpClient): GitHubReleasesRepo {
        return GitHubReleasesImpl(ktorClient)
    }

    @Provides
    @Singleton
    fun provideTwitterMetaDataRepo(ktorClient: HttpClient): TwitterMetaDataRepo {
        return TwitterMetaDataImpl(ktorClient)
    }

    @Provides
    @Singleton
    fun provideLocalizationRepo(ktorClient: HttpClient): LocalizationRepo {
        return LocalizationImpl(ktorClient)
    }

    @Provides
    @Singleton
    fun provideArchivedLinksSortingRepo(localDatabase: LocalDatabase): ArchivedLinksSortingRepo {
        return ArchivedLinksSortingImpl(
            localDatabase
        )
    }

    @Provides
    @Singleton
    fun provideArchivedFoldersSortingRepo(localDatabase: LocalDatabase): ParentArchivedFoldersSortingRepo {
        return ParentArchivedFoldersSortingImpl(
            localDatabase
        )
    }

    @Provides
    @Singleton
    fun provideExportRepo(
        localDatabase: LocalDatabase,
        ktorClient: HttpClient,
        siteSpecificUserAgentRepo: SiteSpecificUserAgentRepo,
        panelsRepo: PanelsRepo
    ): ExportRepo {
        return ExportImpl(
            provideLinksRepo(localDatabase, ktorClient, siteSpecificUserAgentRepo),
            provideFoldersRepo(localDatabase),
            panelsRepo
        )
    }

    @Provides
    @Singleton
    fun provideFoldersRepo(localDatabase: LocalDatabase): FoldersRepo {
        return FoldersImpl(localDatabase)
    }

    @Provides
    @Singleton
    fun provideLanguageRepo(localDatabase: LocalDatabase): LanguageRepo {
        return LanguageImpl(localDatabase)
    }

    @Provides
    @Singleton
    fun provideTranslationRepo(
        localDatabase: LocalDatabase,
        localizationRepo: LocalizationRepo
    ): TranslationsRepo {
        return TranslationsImpl(localDatabase, localizationRepo)
    }

    @Provides
    @Singleton
    fun provideSavedLinksSortingRepo(localDatabase: LocalDatabase): SavedLinksSortingRepo {
        return SavedLinksSortingImpl(localDatabase)
    }

    @Provides
    @Singleton
    fun provideImportantLinksSortingRepo(localDatabase: LocalDatabase): ImportantLinksSortingRepo {
        return ImportantLinksSortingImpl(localDatabase)
    }

    @Provides
    @Singleton
    fun provideFolderLinksSortingRepo(localDatabase: LocalDatabase): RegularFolderLinksSortingRepo {
        return RegularFolderLinksSortingImpl(localDatabase)
    }

    @Provides
    @Singleton
    fun provideArchivedFolderLinksSortingRepo(localDatabase: LocalDatabase): ArchivedFolderLinksSortingRepo {
        return ArchivedFolderLinksSortingImpl(localDatabase)
    }

    @Provides
    @Singleton
    fun provideSubFoldersSortingRepo(localDatabase: LocalDatabase): SubFoldersSortingRepo {
        return SubFoldersSortingImpl(localDatabase)
    }

    @Provides
    @Singleton
    fun provideParentRegularFoldersSortingRepo(localDatabase: LocalDatabase): ParentRegularFoldersSortingRepo {
        return ParentRegularFoldersSortingImpl(localDatabase)
    }

    @Provides
    @Singleton
    fun providePanelsRepo(localDatabase: LocalDatabase): PanelsRepo {
        return PanelsImpl(localDatabase)
    }

    @Provides
    @Singleton
    fun provideSiteSpecificUserAgentRepo(localDatabase: LocalDatabase): SiteSpecificUserAgentRepo {
        return SiteSpecificUserAgentImpl(localDatabase)
    }

    @Provides
    @Singleton
    fun provideSearchRepo(localDatabase: LocalDatabase): SearchRepo {
        return SearchImpl(localDatabase)
    }


    @Provides
    @Singleton
    fun provideHistoryLinksSortingRepo(localDatabase: LocalDatabase): HistoryLinksSortingRepo {
        return HistoryLinksSortingImpl(localDatabase)
    }

    @Provides
    @Singleton
    fun provideLinkMetaDataScrapperService(): LinkMetaDataScrapperService {
        return LinkMetaDataScrapperImpl()
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideRefreshLinksWorkerRequestBuilder(
        workManager: WorkManager,
        app: Application
    ): RefreshLinksWorkerRequestBuilder {
        return RefreshLinksWorkerRequestBuilder(workManager, app.applicationContext)
    }

    @Provides
    @Singleton
    fun provideImportRepo(
        localDatabase: LocalDatabase,
        foldersRepo: FoldersRepo,
        linksRepo: LinksRepo
    ): ImportRepo {
        return ImportImpl(localDatabase, foldersRepo, linksRepo)
    }
}