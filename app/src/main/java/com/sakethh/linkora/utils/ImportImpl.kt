package com.sakethh.linkora.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import com.sakethh.linkora.data.localDB.LocalDataBase
import com.sakethh.linkora.data.localDB.dto.exportImportDTOs.ExportDTO
import com.sakethh.linkora.ui.viewmodels.localDB.UpdateVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class ImportImpl {
    suspend fun importToLocalDB(
        context: Context,
        exceptionType: MutableState<String?>,
        jsonString: String,
        shouldErrorDialogBeVisible: MutableState<Boolean>,
        updateVM: UpdateVM
    ) = coroutineScope {
        val localDataBase = LocalDataBase.localDB
        try {
            val json = Json {
                ignoreUnknownKeys = true
            }
            val jsonDeserialized = json.decodeFromString<ExportDTO>(jsonString)

            var getLatestLinksTableID = localDataBase.importDao().getLatestLinksTableID()
            var getLatestFoldersTableID = localDataBase.importDao().getLatestFoldersTableID()
            val minFolderID = getLatestFoldersTableID
            var maxFolderID: Long = 0
            var getLatestArchivedLinksTableID =
                localDataBase.importDao().getLatestArchivedLinksTableID()
            var getLatestArchivedFoldersTableID =
                localDataBase.importDao().getLatestArchivedFoldersTableID()
            var getLatestImpLinksTableID = localDataBase.importDao().getLatestImpLinksTableID()
            var getLatestRecentlyVisitedTableID =
                localDataBase.importDao().getLatestRecentlyVisitedTableID()

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

            localDataBase.importDao().addAllArchivedFolders(jsonDeserialized.archivedFoldersTable)

            localDataBase.importDao().addAllRegularFolders(jsonDeserialized.foldersTable)

            localDataBase.importDao().addAllArchivedLinks(jsonDeserialized.archivedLinksTable)

            localDataBase.importDao().addAllHistoryLinks(jsonDeserialized.historyLinksTable)

            localDataBase.importDao().addAllImportantLinks(jsonDeserialized.importantLinksTable)

            localDataBase.importDao().addAllLinks(jsonDeserialized.linksTable)

            exceptionType.value = null
            shouldErrorDialogBeVisible.value = false
            if (jsonDeserialized.schemaVersion <= 9) {
                awaitAll(async {
                    if (jsonDeserialized.foldersTable.isNotEmpty()) {
                        updateVM.migrateRegularFoldersLinksDataFromV9toV10()
                    }
                }, async {
                    if (jsonDeserialized.archivedFoldersTable.isNotEmpty()) {
                        updateVM.migrateArchiveFoldersV9toV10()
                    }
                })
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    if (jsonDeserialized.schemaVersion <= 9) "Imported and Migrated Data Successfully; Schema is based on v${jsonDeserialized.schemaVersion}" else "Imported Data Successfully; Schema is based on v${jsonDeserialized.schemaVersion}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            exceptionType.value = IllegalArgumentException().toString()
            shouldErrorDialogBeVisible.value = true
        } catch (e: SerializationException) {
            exceptionType.value = SerializationException().toString()
            shouldErrorDialogBeVisible.value = true
        }
    }
}