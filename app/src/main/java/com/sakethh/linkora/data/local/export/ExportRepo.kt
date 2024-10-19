package com.sakethh.linkora.data.local.export

interface ExportRepo {
    suspend fun exportToAFile(exportInHTMLFormat: Boolean = true)
}