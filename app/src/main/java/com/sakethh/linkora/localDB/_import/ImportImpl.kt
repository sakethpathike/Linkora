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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ImportImpl {
    suspend fun importToLocalDB(
        context: Context,
        exceptionType: MutableState<String?>,
        jsonString: String,
        shouldErrorDialogBeVisible: MutableState<Boolean>,
    ) {
        try {
            val json = Json {
                ignoreUnknownKeys = true
            }
            val jsonDeserialized = json.decodeFromString<ExportDTO>(jsonString)
            withContext(Dispatchers.IO) {
                awaitAll(async {
                    LocalDataBase.localDB.importDao()
                        .addAllArchivedFolders(jsonDeserialized.archivedFolders)
                }, async {
                    LocalDataBase.localDB.importDao()
                        .addAllRegularFolders(jsonDeserialized.folders)
                }, async {
                    LocalDataBase.localDB.importDao()
                        .addAllArchivedLinks(jsonDeserialized.archivedLinks)
                }, async {
                    LocalDataBase.localDB.importDao()
                        .addAllHistoryLinks(jsonDeserialized.historyLinks)
                }, async {
                    LocalDataBase.localDB.importDao()
                        .addAllImportantLinks(jsonDeserialized.importantLinks)
                }, async {
                    LocalDataBase.localDB.importDao()
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
                Toast.makeText(context, "Imported Data Successfully", Toast.LENGTH_SHORT).show()
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