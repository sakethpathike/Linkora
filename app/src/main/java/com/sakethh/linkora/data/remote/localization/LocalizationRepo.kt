package com.sakethh.linkora.data.remote.localization

interface LocalizationRepo {
    suspend fun retrieveLanguageStrings(languageCode: String): LocalizationResult
}