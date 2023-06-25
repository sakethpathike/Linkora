package com.sakethh.linkora.localDB

import org.jsoup.Jsoup
import java.net.URL

data class LinkDataExtractor(val baseURL: String, val imgURL: String)

fun linkDataExtractor(webURL: String): LinkDataExtractor? {
    val url = URL(webURL)
    val jsoup = Jsoup.connect(webURL).get()
    val imgURL = jsoup.select("img").first()?.attr("src")
    return imgURL?.let { LinkDataExtractor(baseURL = url.host, imgURL = it) }
}