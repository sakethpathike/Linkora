package com.sakethh.linkora.data.local.localization.language.translations

import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.data.RequestResult
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.remote.localization.LocalizationRepo
import com.sakethh.linkora.utils.linkoraLog
import javax.inject.Inject

class TranslationsImpl @Inject constructor(
    private val localDatabase: LocalDatabase,
    private val localizationRepo: LocalizationRepo
) :
    TranslationsRepo {
    override suspend fun addLocalizedStrings(languageCode: String): RequestResult<String> {
        when (val localizedData = localizationRepo.getRemoteStrings(languageCode)) {
            is RequestResult.Failure -> {
                return RequestResult.Failure(LocalizedStrings.cannotRetrieveNowPleaseTryAgain.value)
            }

            is RequestResult.Success -> {
                localDatabase.translationDao()
                    .deleteAllLocalizedStringsForThisLanguage(languageCode)
                linkoraLog("deleted localized strings for $languageCode")
                localDatabase.translationDao().addLocalizedStrings(localizedData.data)
                linkoraLog("added localized strings for $languageCode")
                return RequestResult.Success(LocalizedStrings.fetchedSuccessfully.value)
            }
        }
    }

    override suspend fun doesStringsPackForThisLanguageExists(languageCode: String): Boolean {
        return localDatabase.translationDao()
            .doesStringsPackForThisLanguageExists(languageCode)
    }

    override suspend fun getLocalizedStringValueFor(
        stringName: String,
        languageCode: String
    ): String? {
        return localDatabase.translationDao().getLocalizedStringValueFor(stringName, languageCode)
    }

    override suspend fun deleteAllLocalizedStringsForThisLanguage(languageCode: String) {
        localDatabase.translationDao().deleteAllLocalizedStringsForThisLanguage(languageCode)
    }
}