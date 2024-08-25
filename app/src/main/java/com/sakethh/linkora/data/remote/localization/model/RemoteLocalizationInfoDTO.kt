package com.sakethh.linkora.data.remote.localization.model

import kotlinx.serialization.Serializable

@Serializable
data class RemoteLocalizationInfoDTO(
    val availableLanguages: List<AvailableLanguage>,
    val totalAvailableLanguages: Int,
    val totalStrings: Int,
    val lastUpdatedOn: String
)