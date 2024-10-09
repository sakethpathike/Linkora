package com.sakethh.linkora.data.remote.scrape

import com.sakethh.linkora.data.RequestResult
import com.sakethh.linkora.data.remote.scrape.model.LinkMetaData
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.utils.linkoraLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class LinkMetaDataScrapperImpl : LinkMetaDataScrapperService {
    override suspend fun scrapeLinkData(url: String): RequestResult<LinkMetaData> {
        return withContext(Dispatchers.IO) {
            val currentUserAgent =
                if (RequestResult.isThisFirstRequest.not()) SettingsPreference.secondaryJsoupUserAgent.value else SettingsPreference.primaryJsoupUserAgent.value
            RequestResult.currentUserAgent = currentUserAgent
            try {
                val urlHost: String
                try {
                    urlHost = url.split("/")[2]
                } catch (e: Exception) {
                    return@withContext RequestResult.Failure("invalid link : " + e.message.toString())
                }
                val rawHTML =
                    try {
                        Jsoup.connect(
                            "http" + url.substringAfter("http").substringBefore(" ").trim()
                        )
                            .userAgent(currentUserAgent)
                            .followRedirects(true)
                            .header("Accept", "text/html")
                            .header("Accept-Encoding", "gzip,deflate")
                            .header("Accept-Language", "en;q=1.0")
                            .header("Connection", "keep-alive").ignoreContentType(true)
                            .maxBodySize(0).ignoreHttpErrors(true).get()
                            .toString()
                    } catch (e: Exception) {
                        return@withContext RequestResult.Failure(e.message.toString())
                    }
                Jsoup.parse(rawHTML).let { document ->
                    val ogImage = document.select("meta[property=og:image]").attr("content")
                    val twitterImage = document.select("meta[name=twitter:image]").attr("content")
                    val favicon = document.select("link[rel=icon]").attr("href")
                    val ogTitle = document.select("meta[property=og:title]").attr("content")
                    val pageTitle = document.title()

                    val imgURL = when {
                        !ogImage.isNullOrBlank() -> {
                            if (ogImage.startsWith("/")) {
                                linkoraLog("https://$urlHost$ogImage")
                                "https://$urlHost$ogImage"
                            } else {
                                ogImage
                            }
                        }

                        ogImage.isNullOrBlank() && !twitterImage.isNullOrBlank() -> if (twitterImage.startsWith(
                                "/"
                            )
                        ) {
                            linkoraLog("https://$urlHost$twitterImage")
                            "https://$urlHost$twitterImage"
                        } else {
                            twitterImage
                        }
                        ogImage.isNullOrBlank() && twitterImage.isNullOrBlank() && !favicon.isNullOrBlank() -> {
                            if (favicon.startsWith("/")) {
                                linkoraLog("https://$urlHost$favicon")
                                "https://$urlHost$favicon"
                            } else {
                                favicon
                            }
                        }
                        else -> ""
                    }

                    val title = when {
                        !ogTitle.isNullOrBlank() -> ogTitle
                        else -> pageTitle
                    }
                    RequestResult.Success(
                        LinkMetaData(baseURL = urlHost, imgURL, title)
                    )
                }

            } catch (e: Exception) {
                RequestResult.Failure(e.message.toString())
            }
        }
    }
}