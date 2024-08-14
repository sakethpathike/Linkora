package com.sakethh.linkora.data.local.localization.language.translations

import com.sakethh.linkora.data.local.LocalDatabase
import javax.inject.Inject

class TranslationsImpl @Inject constructor(private val localDatabase: LocalDatabase) :
    TranslationsRepo {
    override suspend fun addANewLocalizedString(translation: Translation) {
        localDatabase.translationDao().addANewLocalizedString(translation)
    }

    override suspend fun deleteAnExistingLocalizedString(translation: Translation) {
        localDatabase.translationDao().deleteAnExistingLocalizedString(translation)
    }

    override suspend fun deleteAnExistingLocalizedString(id: Long) {
        localDatabase.translationDao().deleteAnExistingLocalizedString(id)
    }

    override suspend fun getLocalizedStringValueFor(
        stringName: String,
        languageCode: String
    ): String? {
        return localDatabase.translationDao().getLocalizedStringValueFor(stringName, languageCode)
    }
}