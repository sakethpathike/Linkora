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

            withContext(Dispatchers.IO) {
                awaitAll(async {
                    localDataBase.importDao()
                        .addAllArchivedFolders(jsonDeserialized.archivedFoldersTable)
                }, async {
                    localDataBase.importDao()
                        .addAllRegularFolders(jsonDeserialized.foldersTable)
                }, async {
                    localDataBase.importDao()
                        .addAllArchivedLinks(jsonDeserialized.archivedLinksTable)
                }, async {
                    localDataBase.importDao()
                        .addAllHistoryLinks(jsonDeserialized.historyLinksTable)
                }, async {
                    localDataBase.importDao()
                        .addAllImportantLinks(jsonDeserialized.importantLinksTable)
                }, async {
                    localDataBase.importDao()
                        .addAllLinks(jsonDeserialized.linksTable)
                })
            }
            exceptionType.value = null
            shouldErrorDialogBeVisible.value = false
            if (jsonDeserialized.schemaVersion <= 9) {
                coroutineScope {
                    awaitAll(async {
                        if (jsonDeserialized.foldersTable.isNotEmpty()) {
                            UpdateVM().migrateRegularFoldersLinksDataFromV9toV10()
                        }
                    }, async {
                        if (jsonDeserialized.archivedFoldersTable.isNotEmpty()) {
                            UpdateVM().migrateArchiveFoldersV9toV10()
                        }
                    })
                }
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