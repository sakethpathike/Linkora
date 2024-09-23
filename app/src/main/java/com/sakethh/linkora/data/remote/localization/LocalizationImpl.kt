package com.sakethh.linkora.data.remote.localization

import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.data.RequestResult
import com.sakethh.linkora.data.RequestState
import com.sakethh.linkora.data.local.localization.language.translations.Translation
import com.sakethh.linkora.data.remote.localization.model.RemoteLocalizationInfoDTO
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import javax.inject.Inject

class LocalizationImpl @Inject constructor(private val ktorClient: HttpClient) :
    LocalizationRepo {
    override suspend fun getRemoteStrings(languageCode: String): RequestResult<List<Translation>> {
        return try {
            val localizedStrings = mutableListOf<Translation>()
            RequestResult.updateState(RequestState.REQUESTING)
            ktorClient.get(SettingsPreference.localizationServerURL.value + languageCode)
                .bodyAsText()
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
            RequestResult.updateState(RequestState.SUCCESS)
            RequestResult.Success(localizedStrings)
        } catch (e: Exception) {
            RequestResult.updateState(RequestState.FAILED)
            e.printStackTrace()
            RequestResult.Failure(LocalizedStrings.cannotRetrieveNowPleaseTryAgain.value)
        }
    }

    override suspend fun getRemoteLanguages(): RequestResult<RemoteLocalizationInfoDTO> {
        return try {
            RequestResult.updateState(RequestState.REQUESTING)
            val remoteLanguageData =
                ktorClient.get(SettingsPreference.localizationServerURL.value + "info")
                    .body<RemoteLocalizationInfoDTO>()
            RequestResult.updateState(RequestState.SUCCESS)
            RequestResult.Success(remoteLanguageData)
        } catch (e: Exception) {
            RequestResult.updateState(RequestState.FAILED)
            e.printStackTrace()
            RequestResult.Failure(LocalizedStrings.cannotRetrieveNowPleaseTryAgain.value)
        }
    }
}