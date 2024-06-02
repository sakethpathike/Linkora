package com.sakethh.linkora.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
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
    var errorInGivenURL: Boolean
    val urlHost =
        try {
            errorInGivenURL = false
            webURL.split("/")[2]
        } catch (_: Exception) {
            errorInGivenURL = true
            ""
        }
    return withContext(Dispatchers.IO) {
        val rawHTML = if (!errorInGivenURL) {
            try {
                Jsoup.connect("http" + webURL.substringAfter("http").substringBefore("?").trim())
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:125.0) Gecko/20100101 Firefox/125.0")
                    .referrer("http://www.google.com")
                    .followRedirects(true)
                    .header("Accept", "text/html")
                    .header("Accept-Encoding", "gzip,deflate")
                    .header(
                        "Accept-Language",
                        "it-IT,en;q=0.8,en-US;q=0.6,de;q=0.4,it;q=0.2,es;q=0.2"
                    )
                    .header("Connection", "keep-alive")
                    .ignoreContentType(true).maxBodySize(0).ignoreHttpErrors(true).get().toString()
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        } else {
            ""
        }
        val imgURL = rawHTML.split("\n").firstOrNull() {
            it.contains("og:image")
        }.let {
            "http" + it?.substringAfter("http")?.substringBefore("\"")
        }.trim().let {
            try {
                val statusValue = withContext(Dispatchers.IO) {
                    Jsoup.connect(it)
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:125.0) Gecko/20100101 Firefox/125.0")
                        .referrer("http://www.google.com")
                        .followRedirects(true)
                        .header("Accept", "text/html")
                        .header("Accept-Encoding", "gzip,deflate")
                        .header(
                            "Accept-Language",
                            "it-IT,en;q=0.8,en-US;q=0.6,de;q=0.4,it;q=0.2,es;q=0.2"
                        )
                        .header("Connection", "keep-alive")
                        .ignoreContentType(true).maxBodySize(0).ignoreHttpErrors(true).execute()
                        .statusCode()
                }
                if (statusValue == 200) {
                    it
                } else {
                    ""
                }
            } catch (e: Exception) {
                e.printStackTrace()
                    ""
                }
            }
        val title =
            rawHTML.substringAfter("<title").substringAfter(">").substringBefore("</title>").trim()
        LinkDataExtractor(
            baseURL = urlHost,
            imgURL = imgURL,
            title = title,
            errorInGivenURL = errorInGivenURL
        )
    }
}

fun isNetworkAvailable(context: Context): Boolean {
    var result = false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting
    }
    return result
}