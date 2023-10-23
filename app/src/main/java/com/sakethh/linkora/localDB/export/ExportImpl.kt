package com.sakethh.linkora.localDB.export

import android.os.Environment
import android.util.Log
import com.sakethh.linkora.localDB.CustomFunctionsForLocalDB
import com.sakethh.linkora.localDB.dto.ArchivedFolders
import com.sakethh.linkora.localDB.dto.ArchivedLinks
import com.sakethh.linkora.localDB.dto.FoldersTable
import com.sakethh.linkora.localDB.dto.ImportantLinks
import com.sakethh.linkora.localDB.dto.LinksTable
import com.sakethh.linkora.localDB.dto.RecentlyVisited
import com.sakethh.linkora.localDB.dto.exportImportDTOs.ExportDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


class ExportImpl {

    suspend fun exportToAFile() {
        val defaultFolder = File(Environment.getExternalStorageDirectory(), "Linkora/Exports")
        if (!defaultFolder.exists()) {
            File(Environment.getExternalStorageDirectory(), "Linkora/Exports").mkdirs()
        }
        val savedLinks = mutableListOf<LinksTable>()
        val importantLinks = mutableListOf<ImportantLinks>()
        val folders = mutableListOf<FoldersTable>()
        val archivedLinks = mutableListOf<ArchivedLinks>()
        val archivedFolders = mutableListOf<ArchivedFolders>()
        val historyLinks = mutableListOf<RecentlyVisited>()
        withContext(Dispatchers.IO) {
            awaitAll(
                async {
                    CustomFunctionsForLocalDB.localDB.crudDao().getAllSavedLinks().collect {
                        savedLinks.addAll(it)
                    }
                },
                async {
                    CustomFunctionsForLocalDB.localDB.crudDao().getAllImpLinks().collect {
                        importantLinks.addAll(it)
                    }
                },
                async {
                    CustomFunctionsForLocalDB.localDB.crudDao().getAllFolders().collect {
                        folders.addAll(it)
                    }
                },
                async {
                    CustomFunctionsForLocalDB.localDB.crudDao().getAllArchiveLinks().collect {
                        archivedLinks.addAll(it)
                    }
                },
                async {
                    CustomFunctionsForLocalDB.localDB.crudDao().getAllArchiveFolders().collect {
                        archivedFolders.addAll(it)
                    }
                },
                async {
                    CustomFunctionsForLocalDB.localDB.crudDao().getAllRecentlyVisitedLinks()
                        .collect {
                            historyLinks.addAll(it)
                        }
                },
            )
        }

        val json = ExportDTO(
            appVersion = 8,
            savedLinks = savedLinks,
            importantLinks = importantLinks,
            folders = folders,
            archivedLinks = archivedLinks,
            archivedFolders = archivedFolders,
            historyLinks = historyLinks,
        )
        val file =
            File(Environment.getExternalStorageDirectory(), "Linkora/Exports/LinksExport.txt")
        if (file.exists()) {
            file.delete()
        } else {
            withContext(Dispatchers.IO) {
                file.createNewFile()
            }
        }
        val fileOutputStream = withContext(Dispatchers.IO) {
            FileOutputStream(file)
        }
        withContext(Dispatchers.IO) {
            fileOutputStream.write(json.toString().toByteArray())
            Log.d("check export", json.toString())
            fileOutputStream.flush()
            fileOutputStream.close()
        }
    }
}