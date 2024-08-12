package com.sakethh.linkora.data.local.localization.language

interface LanguageRepo {
    suspend fun addANewLanguage(language: Language)

    suspend fun deleteALanguage(language: Language)

    suspend fun deleteALanguage(languageName: String)

    suspend fun deleteALanguageBasedOnLanguageCode(languageCode: String)


    suspend fun getLanguageNameForTheCode(languageCode: String): String

    suspend fun getLanguageCodeForTheLanguageNamed(languageName: String): String
}