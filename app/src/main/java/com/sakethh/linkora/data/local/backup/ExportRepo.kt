package com.sakethh.linkora.data.local.backup

interface ExportRepo {
    suspend fun exportToAFile()
}