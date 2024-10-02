@file:Suppress("LocalVariableName")

package com.sakethh.linkora.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.platform.UriHandler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.R
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
open class CustomWebTab @Inject constructor(private val linksRepo: LinksRepo) : ViewModel() {
    fun openInWeb(
        recentlyVisitedData: RecentlyVisited,
        uriHandler: UriHandler,
        context: Context,
        forceOpenInExternalBrowser: Boolean
    ) {
        fun launchCustomWeb() {
            CustomTabsIntent.Builder().apply {
                setInstantAppsEnabled(true)
                setShowTitle(true)
                val customTabBuilder = build()
                customTabBuilder.launchUrl(context, Uri.parse(recentlyVisitedData.webURL))
            }
        }
        viewModelScope.launch {
            awaitAll(async {
                if (!linksRepo
                        .doesThisExistsInRecentlyVisitedLinks(webURL = recentlyVisitedData.webURL)
                ) {
                    linksRepo
                        .addANewLinkInRecentlyVisited(recentlyVisited = recentlyVisitedData)
                } else {
                    this.launch {
                        linksRepo
                            .deleteARecentlyVisitedLink(webURL = recentlyVisitedData.webURL)
                    }.invokeOnCompletion {
                        this.launch {
                            linksRepo
                                .addANewLinkInRecentlyVisited(recentlyVisited = recentlyVisitedData)
                        }
                    }
                }
            }, async {
                if (!SettingsPreference.isInAppWebTabEnabled.value || forceOpenInExternalBrowser) {
                    try {
                        uriHandler.openUri(recentlyVisitedData.webURL)
                    } catch (_: android.content.ActivityNotFoundException) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.no_activity_found_to_handle_intent),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && context.packageManager.getInstalledApplications(
                            0
                        ).find {
                            it.packageName == "com.android.chrome"
                        } != null)
                    ) {
                        launchCustomWeb()
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        try {
                            context.packageManager.getPackageInfo(
                                "com.android.chrome", PackageManager.PackageInfoFlags.of(0.toLong())
                            )
                            launchCustomWeb()
                        } catch (_: PackageManager.NameNotFoundException) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.chrome_isnt_installed),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    } else {
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(recentlyVisitedData.webURL))
                        context.startActivity(browserIntent)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.chrome_isnt_installed),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            })
        }
    }
}
