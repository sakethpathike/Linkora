package com.sakethh.linkora.data.local.localization.language.translations

interface TranslationsRepo {

    suspend fun addANewLocalizedString(translation: Translation)

    suspend fun deleteAnExistingLocalizedString(translation: Translation)

    suspend fun deleteAnExistingLocalizedString(id: Long)

    suspend fun getLocalizedStringValueFor(stringName: String, languageCode: String): String?
}