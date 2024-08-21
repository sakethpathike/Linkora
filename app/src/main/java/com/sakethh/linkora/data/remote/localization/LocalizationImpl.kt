package com.sakethh.linkora.data.remote.localization

import com.sakethh.linkora.data.local.localization.language.translations.Translation
import com.sakethh.linkora.data.remote.localization.model.RemoteLanguageDTO
import com.sakethh.linkora.utils.Constants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import javax.inject.Inject

class LocalizationImpl @Inject constructor(private val ktorClient: HttpClient) :
    LocalizationRepo {
    override suspend fun getRemoteStrings(languageCode: String): LocalizationResult {
        return try {
            val localizedStrings = mutableListOf<Translation>()
            LocalizationResult.updateState(RequestState.REQUESTING)
            ktorClient.get(Constants.LINKORA_LOCALIZATION_SERVER + languageCode).bodyAsText()
                .substringAfter("<resources>")
                .substringBefore("</resources>")
                .split("<string").forEach {
                    if (it.substringAfter("name=\"").substringBefore("\">").trim().isNotBlank()) {
                        localizedStrings.add(
                            Translation(
                                languageCode = languageCode,
                                stringName = it.substringAfter("name=\"").substringBefore("\">")
                                    .substringBefore("\"/>").trim(),
                                stringValue = it.substringAfter("\">").substringAfter("\"/>")
                                    .substringBefore("</string>")
                                    .trim()
                            )
                        )
                    }
                }
            LocalizationResult.updateState(RequestState.SUCCESS)
            LocalizationResult.Success(localizedStrings)
        } catch (e: Exception) {
            LocalizationResult.updateState(RequestState.FAILED)
            e.printStackTrace()
            LocalizationResult.Failure("")
        }
    }

    override suspend fun getRemoteLanguages(): RemoteLanguageDTO {
        return try {
            LocalizationResult.updateState(RequestState.REQUESTING)
            val remoteLanguageData = ktorClient.get(Constants.LINKORA_LOCALIZATION_SERVER + "info")
                .body<RemoteLanguageDTO>()
            LocalizationResult.updateState(RequestState.SUCCESS)
            remoteLanguageData
        } catch (e: Exception) {
            LocalizationResult.updateState(RequestState.FAILED)
            e.printStackTrace()
            RemoteLanguageDTO(
                availableLanguages = listOf(),
                totalAvailableLanguages = 0,
                totalStrings = 0
            )
        }
    }
}