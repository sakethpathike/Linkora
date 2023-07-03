package com.sakethh.linkora.localDB

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

data class LinkDataExtractor(
    val baseURL: String,
    val imgURL: String,
    val title: String,
    val errorInGivenURL: Boolean,
)

suspend fun linkDataExtractor(webURL: String): LinkDataExtractor {
    val urlHost =
        webURL.split("/")[2]
    var errorInGivenURL = false
    val imgURL =
        withContext(Dispatchers.IO) {
            try {
                Jsoup.connect(webURL).get().getElementsByTag("img").first()?.ownText()
                    ?: Jsoup.connect(webURL).get().head().select("link[href~=.*\\.ico]").first()
                        ?.attr("href")
            } catch (e: Exception) {
                ""
            }
        }
    val title = withContext(Dispatchers.IO) {
        try {
            errorInGivenURL = false
            Jsoup.connect(webURL).get().title()
        } catch (e: Exception) {
            errorInGivenURL = true
            ""
        }
    }
    return LinkDataExtractor(
        baseURL = urlHost,
        imgURL = imgURL ?: "",
        title = title,
        errorInGivenURL = errorInGivenURL
    )
}