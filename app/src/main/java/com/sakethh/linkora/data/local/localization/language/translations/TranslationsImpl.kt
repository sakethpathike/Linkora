package com.sakethh.linkora.data.local.localization.language.translations

import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.remote.localization.LocalizationRepo
import com.sakethh.linkora.data.remote.localization.LocalizationResult
import com.sakethh.linkora.utils.linkoraLog
import javax.inject.Inject

class TranslationsImpl @Inject constructor(
    private val localDatabase: LocalDatabase,
    private val localizationRepo: LocalizationRepo
) :
    TranslationsRepo {
    override suspend fun addLocalizedStrings(languageCode: String) {
        when (val localizedData = localizationRepo.getRemoteStrings(languageCode)) {
            is LocalizationResult.Failure -> TODO()
            is LocalizationResult.Success -> {
                localDatabase.translationDao().addLocalizedStrings(localizedData.data)
                linkoraLog("added localized string for $languageCode")
            }
        }
    }

    override suspend fun getLocalizedStringValueFor(
        stringName: String,
        languageCode: String
    ): String? {
        return localDatabase.translationDao().getLocalizedStringValueFor(stringName, languageCode)
    }
}