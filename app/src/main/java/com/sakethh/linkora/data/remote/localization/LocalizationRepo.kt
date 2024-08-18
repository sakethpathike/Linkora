package com.sakethh.linkora.data.remote.localization

interface LocalizationRepo {
    suspend fun getRemoteStrings(languageCode: String): LocalizationResult
}