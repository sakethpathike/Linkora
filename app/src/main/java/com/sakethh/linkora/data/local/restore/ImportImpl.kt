package com.sakethh.linkora.data.local.restore

import androidx.compose.runtime.MutableState
import com.sakethh.linkora.data.local.ArchivedFolders
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.data.models.Export
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import javax.inject.Inject

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

    override suspend fun importToLocalDB(
        exceptionType: MutableState<String?>,
        jsonString: String,
        shouldErrorDialogBeVisible: MutableState<Boolean>
    ) = coroutineScope {

        try {
            val json = Json {
                ignoreUnknownKeys = true
            }
            val jsonDeserialized = json.decodeFromString<Export>(jsonString)

            var getLatestLinksTableID = getLatestLinksTableID()
            var getLatestFoldersTableID = getLatestFoldersTableID()
            val minFolderID = getLatestFoldersTableID
            var maxFolderID: Long = 0
            var getLatestArchivedLinksTableID =
                getLatestArchivedLinksTableID()
            var getLatestArchivedFoldersTableID =
                getLatestArchivedFoldersTableID()
            var getLatestImpLinksTableID = getLatestImpLinksTableID()
            var getLatestRecentlyVisitedTableID =
                getLatestRecentlyVisitedTableID()

            // Manipulating IDs for "Links Table":
            jsonDeserialized.linksTable.forEach {
                it.id = ++getLatestLinksTableID
            }

            // Manipulating IDs for "Important Links":
            jsonDeserialized.importantLinksTable.forEach {
                it.id = ++getLatestImpLinksTableID
            }

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

                jsonDeserialized.foldersTable.filter {
                    it.childFolderIDs?.contains(foldersTable.id) == true
                }.forEach {
                    val manipulatedIDs = it.childFolderIDs?.toMutableList() ?: mutableListOf()
                    manipulatedIDs.add(getLatestFoldersTableID)
                    it.childFolderIDs = manipulatedIDs
                }
                foldersTable.id = getLatestFoldersTableID
                maxFolderID = getLatestFoldersTableID
            }

            jsonDeserialized.foldersTable.forEach { foldersTable ->
                val manipulatedChildFolderIDs = foldersTable.childFolderIDs?.toMutableList()
                foldersTable.childFolderIDs?.forEach {
                    if (it > maxFolderID || it <= minFolderID) {
                        manipulatedChildFolderIDs?.remove(it)
                    }
                }
                foldersTable.childFolderIDs = manipulatedChildFolderIDs?.distinct()
            }

            jsonDeserialized.archivedFoldersTable.forEach {
                it.id = ++getLatestArchivedFoldersTableID
            }

            jsonDeserialized.archivedLinksTable.forEach {
                it.id = ++getLatestArchivedLinksTableID
            }

            jsonDeserialized.historyLinksTable.forEach {
                it.id = ++getLatestRecentlyVisitedTableID
            }

            addAllArchivedFolders(jsonDeserialized.archivedFoldersTable)

            addAllRegularFolders(jsonDeserialized.foldersTable)

            addAllArchivedLinks(jsonDeserialized.archivedLinksTable)

            addAllHistoryLinks(jsonDeserialized.historyLinksTable)

            addAllImportantLinks(jsonDeserialized.importantLinksTable)

            addAllLinks(jsonDeserialized.linksTable)

            exceptionType.value = null
            shouldErrorDialogBeVisible.value = false
            if (jsonDeserialized.schemaVersion <= 9) {
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
            TODO()
            /*withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    if (jsonDeserialized.schemaVersion <= 9) "Imported and Migrated Data Successfully; Schema is based on v${jsonDeserialized.schemaVersion}" else "Imported Data Successfully; Schema is based on v${jsonDeserialized.schemaVersion}",
                    Toast.LENGTH_SHORT
                ).show()
            }*/
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            exceptionType.value = IllegalArgumentException().toString()
            shouldErrorDialogBeVisible.value = true
        } catch (e: SerializationException) {
            exceptionType.value = SerializationException().toString()
            shouldErrorDialogBeVisible.value = true
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