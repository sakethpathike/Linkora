package com.sakethh.linkora.data.remote.localization

import com.sakethh.linkora.data.remote.localization.model.RemoteLocalizationInfoDTO

interface LocalizationRepo {
    suspend fun getRemoteStrings(languageCode: String): LocalizationResult

    suspend fun getRemoteLanguages(): RemoteLocalizationInfoDTO
}