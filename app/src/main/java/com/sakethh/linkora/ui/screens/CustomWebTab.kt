@file:Suppress("LocalVariableName")

package com.sakethh.linkora.ui.screens

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.platform.UriHandler
import com.sakethh.linkora.data.localDB.LocalDataBase
import com.sakethh.linkora.data.localDB.models.RecentlyVisited
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend fun openInWeb(
    recentlyVisitedData: RecentlyVisited,
    uriHandler: UriHandler,
    context: Context,
    forceOpenInExternalBrowser: Boolean,
) {
    val launchCustomWeb: () -> Unit = {
        val _customTabBuilder = CustomTabsIntent.Builder()
        _customTabBuilder.setInstantAppsEnabled(true)
        _customTabBuilder.setShowTitle(true)
        val customTabBuilder = _customTabBuilder.build()
        customTabBuilder.intent.setPackage("com.android.chrome")
        customTabBuilder.launchUrl(context, Uri.parse(recentlyVisitedData.webURL))
    }
    coroutineScope {
        awaitAll(async {
            if (!LocalDataBase.localDB.readDao()
                    .doesThisExistsInRecentlyVisitedLinks(webURL = recentlyVisitedData.webURL)
            ) {
                LocalDataBase.localDB.createDao()
                    .addANewLinkInRecentlyVisited(recentlyVisited = recentlyVisitedData)
            } else {
                this.launch {
                    LocalDataBase.localDB.deleteDao()
                        .deleteARecentlyVisitedLink(webURL = recentlyVisitedData.webURL)
                }.invokeOnCompletion {
                    this.launch {
                        LocalDataBase.localDB.createDao()
                            .addANewLinkInRecentlyVisited(recentlyVisited = recentlyVisitedData)
                    }
                }
            }
        }, async {
            if (!SettingsScreenVM.Settings.isInAppWebTabEnabled.value || forceOpenInExternalBrowser) {
                try {
                    uriHandler.openUri(recentlyVisitedData.webURL)
                } catch (_: android.content.ActivityNotFoundException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "No Activity found to handle Intent",
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
                            Toast.makeText(context, "Chrome isn't installed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } else {
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(recentlyVisitedData.webURL))
                    context.startActivity(browserIntent)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Chrome isn't installed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}