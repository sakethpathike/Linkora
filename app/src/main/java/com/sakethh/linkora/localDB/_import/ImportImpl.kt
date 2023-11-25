package com.sakethh.linkora.localDB._import

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import com.sakethh.linkora.localDB.CustomFunctionsForLocalDB
import com.sakethh.linkora.localDB.dto.exportImportDTOs.ExportDTOv8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ImportImpl {
    suspend fun importToLocalDB(
        context: Context,
        exceptionType: MutableState<String?>,
        json: String,
        shouldErrorDialogBeVisible: MutableState<Boolean>
    ) {
        try {
            val jsonDeserialized = Json {
                ignoreUnknownKeys = true
            }.decodeFromString<ExportDTOv8>(json)
            withContext(Dispatchers.IO) {
                awaitAll(async {
                    CustomFunctionsForLocalDB.localDB.importDao()
                        .addAllArchivedFolders(jsonDeserialized.archivedFolders)
                }, async {
                    CustomFunctionsForLocalDB.localDB.importDao()
                        .addAllRegularFolders(jsonDeserialized.folders)
                }, async {
                    CustomFunctionsForLocalDB.localDB.importDao()
                        .addAllArchivedLinks(jsonDeserialized.archivedLinks)
                }, async {
                    CustomFunctionsForLocalDB.localDB.importDao()
                        .addAllHistoryLinks(jsonDeserialized.historyLinks)
                }, async {
                    CustomFunctionsForLocalDB.localDB.importDao()
                        .addAllImportantLinks(jsonDeserialized.importantLinks)
                }, async {
                    CustomFunctionsForLocalDB.localDB.importDao()
                        .addAllLinks(jsonDeserialized.savedLinks)
                })
            }
            exceptionType.value = null
            shouldErrorDialogBeVisible.value = false
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Imported Data Successfully", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IllegalArgumentException) {
            exceptionType.value = IllegalArgumentException().toString()
            shouldErrorDialogBeVisible.value = true
        } catch (e: SerializationException) {
            exceptionType.value = SerializationException().toString()
            shouldErrorDialogBeVisible.value = true
        }

    }
}