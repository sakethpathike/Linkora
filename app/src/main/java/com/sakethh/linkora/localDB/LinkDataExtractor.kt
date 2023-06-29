package com.sakethh.linkora.localDB

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.MalformedURLException
import java.net.URL

data class LinkDataExtractor(val baseURL: String, val imgURL: String, val title: String)

suspend fun linkDataExtractor(webURL: String): LinkDataExtractor {
    val urlHost = try {
        URL(webURL).host.removePrefix("https://www.").removePrefix("http://www.")
            .removePrefix("http://").removePrefix("https://")
    } catch (_: MalformedURLException) {
        webURL.removePrefix("https://www.").removePrefix("http://www.")
    }
    val jsoup = withContext(Dispatchers.IO) { Jsoup.connect(webURL).get() }
    val imgURL = jsoup.body().select("img").first()?.absUrl("src")
    val title = jsoup.title()
    return LinkDataExtractor(baseURL = urlHost, imgURL = imgURL ?: "", title = title)
}