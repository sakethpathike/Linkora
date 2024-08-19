package com.sakethh.linkora.data.local.localization.language

import com.sakethh.linkora.data.local.LocalDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LanguageImpl @Inject constructor(private val localDatabase: LocalDatabase) : LanguageRepo {
    override suspend fun addANewLanguage(language: Language) {
        localDatabase.languageDao().addANewLanguage(language)
    }

    override suspend fun addNewLanguages(languages: List<Language>) {
        localDatabase.languageDao().addNewLanguages(languages)
    }

    override suspend fun deleteALanguage(language: Language) {
        localDatabase.languageDao().deleteALanguage(language)
    }

    override suspend fun deleteALanguage(languageName: String) {
        localDatabase.languageDao().deleteALanguage(languageName)
    }

    override suspend fun deleteALanguageBasedOnLanguageCode(languageCode: String) {
        localDatabase.languageDao().deleteALanguageBasedOnLanguageCode(languageCode)
    }

    override fun getAllLanguages(): Flow<List<Language>> {
        return localDatabase.languageDao().getAllLanguages()
    }

    override suspend fun getLanguageNameForTheCode(languageCode: String): String {
        return localDatabase.languageDao().getLanguageNameForTheCode(languageCode)
    }

    override suspend fun getLanguageCodeForTheLanguageNamed(languageName: String): String {
        return localDatabase.languageDao().getLanguageCodeForTheLanguageNamed(languageName)
    }
}