package com.sakethh.linkora.localDB

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
    val networkError: Boolean,
)

suspend fun linkDataExtractor(context: Context, webURL: String): LinkDataExtractor {
    if (isNetworkAvailable(context)) {
        var errorInGivenURL = false
        val urlHost =
            try {
                errorInGivenURL = false
                webURL.split("/")[2]
            } catch (_: Exception) {
                errorInGivenURL = true
                ""
            }
        val imgURL =
            withContext(Dispatchers.IO) {
                try {
                    Jsoup.connect(webURL).get().head().select("link[href~=.*\\.ico]").first()
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
            errorInGivenURL = errorInGivenURL,
            networkError = false
        )
    } else {
        return LinkDataExtractor(
            baseURL = "",
            imgURL = "",
            title = "",
            errorInGivenURL = false,
            networkError = true
        )
    }
}

private fun isNetworkAvailable(context: Context): Boolean {
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