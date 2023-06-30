package com.sakethh.linkora.localDB

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

data class LinkDataExtractor(val baseURL: String, val imgURL: String, val title: String)

suspend fun linkDataExtractor(webURL: String): LinkDataExtractor {
    val urlHost =
        webURL.removePrefix("https://www.").removePrefix("http://www.")
    val imgURL =
        withContext(Dispatchers.IO) {
            try {
                Jsoup.connect(webURL).get().body().select("img").first()?.absUrl("src").toString()
            } catch (e: Exception) {
                ""
            }
        }
    val title = withContext(Dispatchers.IO) {
        try {
            Jsoup.connect(webURL).get().title()
        } catch (e: Exception) {
            "Something went wrong while detecting the title:("
        }
    }
    return LinkDataExtractor(baseURL = urlHost, imgURL = imgURL, title = title)
}