package com.sakethh.linkora.data.local.restore

import android.content.Context
import android.net.Uri
import com.sakethh.linkora.data.local.ArchivedFolders
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.data.models.ExportSchema
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.nio.file.Path
import javax.inject.Inject
import kotlin.io.path.deleteIfExists
import kotlin.io.path.outputStream
import kotlin.io.path.readText

class ImportImpl @Inject constructor(
    private val localDatabase: LocalDatabase,
    private val foldersRepo: FoldersRepo,
    private val linksRepo: LinksRepo
) : ImportRepo {
    private suspend fun addAllLinks(linksTable: List<LinksTable>) {
        return localDatabase.importDao().addAllLinks(linksTable)
    }

    private suspend fun addAllImportantLinks(importantLinks: List<ImportantLinks>) {
        return localDatabase.importDao().addAllImportantLinks(importantLinks)
    }

    private suspend fun addAllArchivedLinks(archivedLinks: List<ArchivedLinks>) {
        return localDatabase.importDao().addAllArchivedLinks(archivedLinks)
    }

    private suspend fun addAllHistoryLinks(historyLinks: List<RecentlyVisited>) {
        return localDatabase.importDao().addAllHistoryLinks(historyLinks)
    }

    private suspend fun addAllRegularFolders(foldersData: List<FoldersTable>) {
        return localDatabase.importDao().addAllRegularFolders(foldersData)
    }

    private suspend fun addAllArchivedFolders(foldersData: List<ArchivedFolders>) {
        return localDatabase.importDao().addAllArchivedFolders(foldersData)
    }

    private suspend fun getLatestLinksTableID(): Long {
        return localDatabase.importDao().getLatestLinksTableID()
    }

    private suspend fun getLatestFoldersTableID(): Long {
        return localDatabase.importDao().getLatestFoldersTableID()
    }

    private suspend fun getLatestArchivedLinksTableID(): Long {
        return localDatabase.importDao().getLatestArchivedLinksTableID()
    }

    private suspend fun getLatestArchivedFoldersTableID(): Long {
        return localDatabase.importDao().getLatestArchivedFoldersTableID()
    }

    private suspend fun getLatestImpLinksTableID(): Long {
        return localDatabase.importDao().getLatestImpLinksTableID()
    }

    private suspend fun getLatestRecentlyVisitedTableID(): Long {
        return localDatabase.importDao().getLatestRecentlyVisitedTableID()
    }

    private var file: Path? = null

    override suspend fun importToLocalDB(
        uri: Uri,
        context: Context
    ): ImportRequestResult {
        return try {

            ImportRequestResult.updateState(ImportRequestState.PARSING)

            file = kotlin.io.path.createTempFile()

            context.contentResolver.openInputStream(uri).use { input ->
                file!!.outputStream().use { output ->
                    input?.copyTo(output)
                }
            }

            val json = Json {
                ignoreUnknownKeys = true
            }

            val jsonDeserialized = json.decodeFromString<ExportSchema>(file!!.readText())

            var getLatestLinksTableID = getLatestLinksTableID()
            var getLatestFoldersTableID = getLatestFoldersTableID()
            var getLatestArchivedLinksTableID =
                getLatestArchivedLinksTableID()
            var getLatestArchivedFoldersTableID =
                getLatestArchivedFoldersTableID()
            var getLatestImpLinksTableID = getLatestImpLinksTableID()
            var getLatestRecentlyVisitedTableID =
                getLatestRecentlyVisitedTableID()


            withContext(Dispatchers.Default) {
                ImportRequestResult.updateState(ImportRequestState.MODIFYING)
                awaitAll(
                    async {
                        jsonDeserialized.linksTable.forEach {
                            it.id = ++getLatestLinksTableID
                        }
                    }, async {
                        jsonDeserialized.foldersTable.forEach { foldersTable ->
                            ++getLatestFoldersTableID

                            jsonDeserialized.linksTable.filter {
                                it.keyOfLinkedFolderV10 == foldersTable.id
                            }.forEach {
                                it.keyOfLinkedFolderV10 = getLatestFoldersTableID
                            }

                            jsonDeserialized.foldersTable.filter { childFolder ->
                                childFolder.parentFolderID == foldersTable.id
                            }.forEach {
                                it.parentFolderID = getLatestFoldersTableID
                            }

                            foldersTable.id = getLatestFoldersTableID
                        }
                    }, async {
                        jsonDeserialized.importantLinksTable.forEach {
                            it.id = ++getLatestImpLinksTableID
                        }
                    }, async {
                        jsonDeserialized.archivedFoldersTable.forEach {
                            it.id = ++getLatestArchivedFoldersTableID
                        }
                    }, async {
                        jsonDeserialized.archivedLinksTable.forEach {
                            it.id = ++getLatestArchivedLinksTableID
                        }
                    }, async {
                        jsonDeserialized.historyLinksTable.forEach {
                            it.id = ++getLatestRecentlyVisitedTableID
                        }
                    }
                )
            }

            withContext(Dispatchers.IO) {
                ImportRequestResult.updateState(ImportRequestState.ADDING_TO_DATABASE)
                awaitAll(
                    async {
                        addAllArchivedFolders(jsonDeserialized.archivedFoldersTable)
                    }, async {
                        addAllRegularFolders(jsonDeserialized.foldersTable)
                    }, async {
                        addAllArchivedLinks(jsonDeserialized.archivedLinksTable)
                    }, async {
                        addAllHistoryLinks(jsonDeserialized.historyLinksTable)
                    }, async {
                        addAllImportantLinks(jsonDeserialized.importantLinksTable)
                    }, async {
                        addAllLinks(jsonDeserialized.linksTable)
                    }
                )
            }

            if (jsonDeserialized.schemaVersion <= 9) {
                coroutineScope {
                    awaitAll(async {
                        if (jsonDeserialized.foldersTable.isNotEmpty()) {
                            migrateRegularFoldersLinksDataFromV9toV10()
                        }
                    }, async {
                        if (jsonDeserialized.archivedFoldersTable.isNotEmpty()) {
                            migrateArchiveFoldersV9toV10()
                        }
                    })
                }
            }

            if (jsonDeserialized.schemaVersion <= 9) "Imported and Migrated Data Successfully; Schema is based on v${jsonDeserialized.schemaVersion}" else "Imported Data Successfully; Schema is based on v${jsonDeserialized.schemaVersion}"
            ImportRequestResult.Success
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            ImportRequestResult.Failure(ImportFailedException.InvalidFile)
        } catch (e: SerializationException) {
            e.printStackTrace()
            ImportRequestResult.Failure(ImportFailedException.NotBasedOnLinkoraSchema)
        } finally {
            file?.deleteIfExists()
            ImportRequestResult.updateState(ImportRequestState.IDLE)
        }
    }


    override suspend fun migrateRegularFoldersLinksDataFromV9toV10() = coroutineScope {
        foldersRepo.getAllRootFolders().collect { rootFolders ->
            rootFolders.forEach { currentFolder ->
                async {
                    currentFolder.childFolderIDs = emptyList()
                    foldersRepo.updateAFolderData(currentFolder)
                }.await()
                linksRepo.getLinksOfThisFolderV9(currentFolder.folderName)
                    .collect { links ->
                        links.forEach { currentLink ->
                            async {
                                currentLink.keyOfLinkedFolderV10 = currentFolder.id
                                linksRepo
                                    .updateALinkDataFromLinksTable(currentLink)
                            }.await()
                        }
                    }
            }
        }
    }

    override suspend fun migrateArchiveFoldersV9toV10() = coroutineScope {
        foldersRepo.getAllArchiveFoldersV9().collect { archiveFolders ->
            archiveFolders.forEach { currentFolder ->
                async {
                    val foldersTable = FoldersTable(
                        folderName = currentFolder.archiveFolderName,
                        infoForSaving = currentFolder.infoForSaving,
                        parentFolderID = null,
                        isFolderArchived = true,
                        isMarkedAsImportant = false
                    )
                    foldersTable.childFolderIDs = emptyList()
                    foldersRepo.createANewFolder(
                        foldersTable
                    )
                }.await()
                val latestAddedFolderID = async {
                    foldersRepo.getLatestAddedFolder().id
                }.await()
                linksRepo
                    .getThisArchiveFolderLinksV9(currentFolder.archiveFolderName)
                    .collect { archiveLinks ->
                        archiveLinks.forEach { currentArchiveLink ->
                            async {
                                currentArchiveLink.isLinkedWithFolders = true
                                currentArchiveLink.keyOfLinkedFolderV10 = latestAddedFolderID
                                linksRepo
                                    .updateALinkDataFromLinksTable(currentArchiveLink)
                            }.await()
                        }
                        async {
                            foldersRepo
                                .deleteAnArchiveFolderV9(currentFolder.id)
                        }.await()
                    }
            }
        }
    }
}