package com.sakethh.linkora.localDB

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URL

data class LinkDataExtractor(val baseURL: String, val imgURL: String, val title: String)

suspend fun linkDataExtractor(webURL: String): LinkDataExtractor {
    val url = URL(webURL)
    val jsoup = withContext(Dispatchers.IO) { Jsoup.connect(webURL).get() }
    val imgURL = jsoup.body().select("img").first()?.absUrl("src")
    val title = jsoup.title()
    return LinkDataExtractor(baseURL = url.host, imgURL = imgURL ?: "", title = title)
}