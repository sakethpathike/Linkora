package com.sakethh.linkora.data.local.restore

import android.content.Context
import android.net.Uri


interface ImportRepo {
    suspend fun importToLocalDBBasedOnLinkoraJSONSchema(
        uri: Uri,
        context: Context
    ): ImportRequestResult

    suspend fun importToLocalDBBasedOnHTML(uri: Uri, context: Context): ImportRequestResult

    suspend fun migrateArchiveFoldersV9toV10()

    suspend fun migrateRegularFoldersLinksDataFromV9toV10()
}