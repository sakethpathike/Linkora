package com.sakethh.linkora.data.remote.localization

import com.sakethh.linkora.data.RequestResult
import com.sakethh.linkora.data.local.localization.language.translations.Translation
import com.sakethh.linkora.data.remote.localization.model.RemoteLocalizationInfoDTO

interface LocalizationRepo {
    suspend fun getRemoteStrings(languageCode: String): RequestResult<List<Translation>>

    suspend fun getRemoteLanguages(): RequestResult<RemoteLocalizationInfoDTO>
}