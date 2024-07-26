package com.sakethh.linkora.data.local.restore

import androidx.compose.runtime.MutableState

interface ImportRepo {
    suspend fun importToLocalDB(
        exceptionType: MutableState<String?>,
        jsonString: String,
        shouldErrorDialogBeVisible: MutableState<Boolean>
    )

    suspend fun migrateArchiveFoldersV9toV10()

    suspend fun migrateRegularFoldersLinksDataFromV9toV10()
}