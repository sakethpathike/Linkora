package com.sakethh.linkora.data.remote.localization

import com.sakethh.linkora.data.local.localization.language.translations.Translation
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import javax.inject.Inject

class LocalizationImpl @Inject constructor(private val ktorClient: HttpClient) :
    LocalizationRepo {
    private val localizationServerURL = "https://linkoralocalizationserver.onrender.com/"
    override suspend fun retrieveLanguageStrings(languageCode: String): LocalizationResult {
        return try {
            val localizedStrings = mutableListOf<Translation>()
            ktorClient.get(localizationServerURL + languageCode).bodyAsText()
                .substringAfter("<resources>")
                .substringBefore("</resources>")
                .split("\n").forEach {
                    if (it.isNotBlank()) {
                        localizedStrings.add(
                            Translation(
                                languageCode = languageCode,
                                stringName = it.substringAfter("<string name=\"")
                                    .substringBefore("\">"),
                                stringValue = it.substringAfter("<string name=\"")
                                    .substringAfter("\">").substringBefore("</string>")
                            )
                        )
                    }
                }
            LocalizationResult.Success(localizedStrings)
        } catch (e: Exception) {
            e.printStackTrace()
            LocalizationResult.Failure("")
        }
    }
}