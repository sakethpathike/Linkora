package com.sakethh.linkora.localDB._import

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import com.sakethh.linkora.localDB.LocalDataBase
import com.sakethh.linkora.localDB.commonVMs.UpdateVM
import com.sakethh.linkora.localDB.dto.exportImportDTOs.ExportDTO
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
    ) {
        val localDataBase = LocalDataBase.localDB
        try {
            val json = Json {
                ignoreUnknownKeys = true
            }
            val jsonDeserialized = json.decodeFromString<ExportDTO>(jsonString)

            var latestImpLinksTableID = localDataBase.readDao().getLastIDOfImpLinksTable()
            var latestSavedLinksTableID = localDataBase.readDao().getLastIDOfLinksTable()
            var latestFoldersTableID = localDataBase.readDao().getLastIDOfFoldersTable()
            var latestArchiveLinksTableID = localDataBase.readDao().getLastIDOfArchivedLinksTable()
            var latestHistoryLinksTableID = localDataBase.readDao().getLastIDOfHistoryTable()

            jsonDeserialized.importantLinks.forEach {
                it.id = latestImpLinksTableID + 1
                latestImpLinksTableID++
            }
            jsonDeserialized.savedLinks.forEach {
                it.id = latestSavedLinksTableID + 1
                latestSavedLinksTableID++
            }
            jsonDeserialized.folders.forEach {
                it.id = latestFoldersTableID + 1
                latestFoldersTableID++
            }
            jsonDeserialized.archivedLinks.forEach {
                it.id = latestArchiveLinksTableID + 1
                latestArchiveLinksTableID++
            }
            jsonDeserialized.historyLinks.forEach {
                it.id = latestHistoryLinksTableID + 1
                latestHistoryLinksTableID++
            }
            jsonDeserialized.archivedFolders.forEach {
                it.id = latestFoldersTableID + 1
                latestFoldersTableID++
            }
            withContext(Dispatchers.IO) {
                awaitAll(async {
                    localDataBase.importDao()
                        .addAllArchivedFolders(jsonDeserialized.archivedFolders)
                }, async {
                    localDataBase.importDao()
                        .addAllRegularFolders(jsonDeserialized.folders)
                }, async {
                    localDataBase.importDao()
                        .addAllArchivedLinks(jsonDeserialized.archivedLinks)
                }, async {
                    localDataBase.importDao()
                        .addAllHistoryLinks(jsonDeserialized.historyLinks)
                }, async {
                    localDataBase.importDao()
                        .addAllImportantLinks(jsonDeserialized.importantLinks)
                }, async {
                    localDataBase.importDao()
                        .addAllLinks(jsonDeserialized.savedLinks)
                })
            }
            exceptionType.value = null
            shouldErrorDialogBeVisible.value = false
            if (jsonDeserialized.appVersion <= 9) {
                coroutineScope {
                    awaitAll(async {
                        if (jsonDeserialized.folders.isNotEmpty()) {
                            UpdateVM().migrateRegularFoldersLinksDataFromV9toV10()
                        }
                    }, async {
                        if (jsonDeserialized.archivedFolders.isNotEmpty()) {
                            UpdateVM().migrateArchiveFoldersV9toV10()
                        }
                    })
                }
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    if (jsonDeserialized.appVersion <= 9) "Imported and Migrated Data Successfully; Schema is based on v${jsonDeserialized.appVersion}" else "Imported Data Successfully; Schema is based on v${jsonDeserialized.appVersion}",
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