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
    val title: String
)

suspend fun linkDataExtractor(context: Context, webURL: String): LinkDataExtractorResult {
    if (!isNetworkAvailable(context)) {
        return LinkDataExtractorResult.Failure.NoInternetConnection
    }
    val urlHost =
        try {
            webURL.split("/")[2]
        } catch (_: Exception) {
            return LinkDataExtractorResult.Failure.InvalidURL
        }
    val (imgURL, title) = withContext(Dispatchers.IO) {
        try {
            val jsonDoc = Jsoup.connect(webURL).get()
            val imgURL = jsonDoc.head()
                .select("link[href~=.*\\.ico]").first()
                ?.attr("href").toString()
            val title = jsonDoc.title()
            Pair(imgURL, title)
        } catch (e: Exception) {
            Pair("", "")
        }
    }
    return LinkDataExtractorResult.Success(LinkDataExtractor(baseURL = urlHost, imgURL, title))
}

sealed class LinkDataExtractorResult {
    data class Success(val linkDataExtractor: LinkDataExtractor) :
        LinkDataExtractorResult()

    sealed class Failure(val failureMsg: String) : LinkDataExtractorResult() {
        data object InvalidURL : Failure("Invalid URL")
        data object NoInternetConnection : Failure("network error, title and image couldn't detect")
    }
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
}