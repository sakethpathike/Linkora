package com.sakethh.linkora.data.local.dataImport

import androidx.compose.runtime.MutableState

interface ImportRepo {
    suspend fun importToLocalDB(
        exceptionType: MutableState<String?>,
        jsonString: String,
        shouldErrorDialogBeVisible: MutableState<Boolean>
    )
}