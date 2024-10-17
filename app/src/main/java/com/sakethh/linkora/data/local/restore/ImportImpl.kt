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
import com.sakethh.linkora.utils.linkoraLog
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

    private suspend fun getLatestPanelTableID(): Long {
        return localDatabase.importDao().getLatestPanelTableID()
    }

    private suspend fun getLatestPanelFoldersTableID(): Long {
        return localDatabase.importDao().getLatestPanelFoldersTableID()
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

            ImportRequestResult.totalLinksFromLinksTable.intValue = jsonDeserialized.linksTable.size
            ImportRequestResult.totalLinksFromArchivedLinksTable.intValue =
                jsonDeserialized.archivedLinksTable.size
            ImportRequestResult.totalLinksFromImpLinksTable.intValue =
                jsonDeserialized.importantLinksTable.size
            ImportRequestResult.totalLinksFromHistoryLinksTable.intValue =
                jsonDeserialized.historyLinksTable.size
            ImportRequestResult.totalRegularFolders.intValue = jsonDeserialized.foldersTable.size
            ImportRequestResult.totalArchivedFolders.intValue =
                jsonDeserialized.archivedFoldersTable.size
            ImportRequestResult.totalPanels.intValue =
                jsonDeserialized.panels.size
            ImportRequestResult.totalPanelFolders.intValue =
                jsonDeserialized.panelFolders.size

            linkoraLog("Total in LinksTable : " + ImportRequestResult.totalLinksFromLinksTable.intValue.toString())
            linkoraLog("Total in ArchivedLinks : " +ImportRequestResult.totalLinksFromArchivedLinksTable.intValue.toString())
            linkoraLog("Total in ImpLinksTable : " +ImportRequestResult.totalLinksFromImpLinksTable.intValue.toString())
            linkoraLog("Total in HistoryLinks : " +ImportRequestResult.totalLinksFromHistoryLinksTable.intValue.toString())
            linkoraLog("Total in RegularFolders : " +ImportRequestResult.totalRegularFolders.intValue.toString())
            linkoraLog("Total in ArchivedFolders : " +ImportRequestResult.totalArchivedFolders.intValue.toString())
            linkoraLog("Total in ArchivedFolders : " + ImportRequestResult.totalPanels.intValue.toString())
            linkoraLog("Total in ArchivedFolders : " + ImportRequestResult.totalPanelFolders.intValue.toString())

            var getLatestLinksTableID = getLatestLinksTableID()
            var getLatestFoldersTableID = getLatestFoldersTableID()
            var getLatestArchivedLinksTableID =
                getLatestArchivedLinksTableID()
            var getLatestArchivedFoldersTableID =
                getLatestArchivedFoldersTableID()
            var getLatestImpLinksTableID = getLatestImpLinksTableID()
            var getLatestRecentlyVisitedTableID =
                getLatestRecentlyVisitedTableID()

            var getLatestPanelsTableID =
                getLatestPanelTableID()

            var getLatestPanelFoldersTableID =
                getLatestPanelFoldersTableID()


            withContext(Dispatchers.Default) {
                ImportRequestResult.updateState(ImportRequestState.MODIFYING)
                awaitAll(
                    async {
                        jsonDeserialized.linksTable.forEach {
                            it.id = ++getLatestLinksTableID
                            ++ImportRequestResult.currentIterationOfLinksFromLinksTable.intValue
                            linkoraLog("Links Table : " + ImportRequestResult.currentIterationOfLinksFromLinksTable.intValue)
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
                            ++ImportRequestResult.currentIterationOfRegularFolders.intValue
                            linkoraLog("Regular Folders : " + ImportRequestResult.currentIterationOfRegularFolders.intValue)
                        }
                    }, async {
                        jsonDeserialized.importantLinksTable.forEach {
                            it.id = ++getLatestImpLinksTableID
                            ++ImportRequestResult.currentIterationOfLinksFromImpLinksTable.intValue
                            linkoraLog("Imp Links : " + ImportRequestResult.currentIterationOfLinksFromImpLinksTable.intValue)
                        }
                    }, async {
                        jsonDeserialized.archivedFoldersTable.forEach {
                            it.id = ++getLatestArchivedFoldersTableID
                            ++ImportRequestResult.currentIterationOfArchivedFolders.intValue
                            linkoraLog("Archived Folders : " + ImportRequestResult.currentIterationOfArchivedFolders.intValue)
                        }
                    }, async {
                        jsonDeserialized.archivedLinksTable.forEach {
                            it.id = ++getLatestArchivedLinksTableID
                            ++ImportRequestResult.currentIterationOfLinksFromArchivedLinksTable.intValue
                            linkoraLog("Archived Links : " + ImportRequestResult.currentIterationOfLinksFromArchivedLinksTable.intValue)
                        }
                    }, async {
                        jsonDeserialized.historyLinksTable.forEach {
                            it.id = ++getLatestRecentlyVisitedTableID
                            ++ImportRequestResult.currentIterationOfLinksFromHistoryLinksTable.intValue
                            linkoraLog("History Links : " + ImportRequestResult.currentIterationOfLinksFromHistoryLinksTable.intValue)
                        }
                    }, async {
                        jsonDeserialized.panels.forEach { originalPanel ->
                            ++getLatestPanelsTableID
                            originalPanel.panelId = getLatestPanelsTableID
                            ++ImportRequestResult.currentIterationOfPanels.intValue
                            linkoraLog("Panels : " + ImportRequestResult.currentIterationOfPanels.intValue)

                            jsonDeserialized.panelFolders.filter { it.connectedPanelId == originalPanel.panelId }
                                .forEach {
                                    it.connectedPanelId = getLatestPanelsTableID
                                    it.id = ++getLatestPanelFoldersTableID
                                    ++ImportRequestResult.currentIterationOfPanelFolders.intValue
                                    linkoraLog("Panel Folders : " + ImportRequestResult.currentIterationOfPanelFolders.intValue)
                                }
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
            linkoraLog("done")
            ImportRequestResult.resetImportInfo()
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