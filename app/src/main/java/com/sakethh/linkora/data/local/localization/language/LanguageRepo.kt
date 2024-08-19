package com.sakethh.linkora.data.local.localization.language

import kotlinx.coroutines.flow.Flow

interface LanguageRepo {
    suspend fun addANewLanguage(language: Language)

    suspend fun addNewLanguages(languages: List<Language>)

    suspend fun deleteALanguage(language: Language)

    suspend fun deleteALanguage(languageName: String)

    suspend fun deleteALanguageBasedOnLanguageCode(languageCode: String)

    fun getAllLanguages(): Flow<List<Language>>

    suspend fun getLanguageNameForTheCode(languageCode: String): String

    suspend fun getLanguageCodeForTheLanguageNamed(languageName: String): String
}