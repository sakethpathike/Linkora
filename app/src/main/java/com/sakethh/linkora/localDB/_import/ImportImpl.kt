package com.sakethh.linkora.localDB._import

import androidx.compose.runtime.MutableState
import com.sakethh.linkora.localDB.CustomFunctionsForLocalDB
import com.sakethh.linkora.localDB.dto.exportImportDTOs.ExportDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ImportImpl {
    suspend fun importToLocalDB(
        exceptionType: MutableState<Exception?>,
        json: String,
    ) {
        try {
            val jsonDeserialized = Json.decodeFromString<ExportDTO>(json)
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
        } catch (_: IllegalArgumentException) {
            exceptionType.value = IllegalArgumentException()
        } catch (_: SerializationException) {
            exceptionType.value = SerializationException()
        }

    }
}