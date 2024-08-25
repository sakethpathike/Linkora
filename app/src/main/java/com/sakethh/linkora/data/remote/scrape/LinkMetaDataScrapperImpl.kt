package com.sakethh.linkora.data.remote.scrape

import com.sakethh.linkora.data.RequestResult
import com.sakethh.linkora.data.remote.scrape.model.LinkMetaData
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class LinkMetaDataScrapperImpl : LinkMetaDataScrapperService {
    override suspend fun scrapeLinkData(url: String): RequestResult<LinkMetaData> {
        return withContext(Dispatchers.IO) {
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
                            .userAgent(SettingsPreference.jsoupUserAgent.value)
                            .referrer("http://www.google.com")
                            .followRedirects(true)
                            .header("Accept", "text/html")
                            .header("Accept-Encoding", "gzip,deflate")
                            .header(
                                "Accept-Language",
                                "it-IT,en;q=0.8,en-US;q=0.6,de;q=0.4,it;q=0.2,es;q=0.2"
                            )
                            .header("Connection", "keep-alive")
                            .ignoreContentType(true).maxBodySize(0).ignoreHttpErrors(true).get()
                            .toString()
                    } catch (e: Exception) {
                        return@withContext RequestResult.Failure("reported an error that has been found while scarping the meta data of the given link : " + e.message.toString())
                    }

                val imgURL = rawHTML.split("\n").firstOrNull {
                    it.contains("og:image")
                }.let {
                    "http" + it?.substringAfter("http")?.substringBefore("\"")
                }.trim().let {
                    val statusValue = withContext(Dispatchers.IO) {
                        try {
                            Jsoup.connect(it)
                                .userAgent(SettingsPreference.jsoupUserAgent.value)
                                .referrer("http://www.google.com")
                                .followRedirects(true)
                                .header("Accept", "text/html")
                                .header("Accept-Encoding", "gzip,deflate")
                                .header(
                                    "Accept-Language",
                                    "it-IT,en;q=0.8,en-US;q=0.6,de;q=0.4,it;q=0.2,es;q=0.2"
                                )
                                .header("Connection", "keep-alive")
                                .ignoreContentType(true).maxBodySize(0).ignoreHttpErrors(true)
                                .execute()
                                .statusCode()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    if (statusValue == 200) {
                        it
                    } else {
                        ""
                    }
                }
                val title =
                    rawHTML.substringAfter("<title").substringAfter(">").substringBefore("</title>")
                        .trim()
                RequestResult.Success(
                    LinkMetaData(baseURL = urlHost, imgURL, title)
                )
            } catch (e: Exception) {
                RequestResult.Failure(e.message.toString())
            }
        }
    }
}