@file:Suppress("LocalVariableName")

package com.sakethh.linkora.customWebTab

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.platform.UriHandler
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.localDB.RecentlyVisited
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

suspend fun openInWeb(
    recentlyVisitedData: RecentlyVisited,
    uriHandler: UriHandler,
    context: Context,
) {
    coroutineScope {
        awaitAll(async {
            if (!CustomLocalDBDaoFunctionsDecl.localDB.crudDao()
                    .doesThisExistsInRecentlyVisitedLinks(webURL = recentlyVisitedData.webURL)
            ) {
                CustomLocalDBDaoFunctionsDecl.localDB.crudDao()
                    .addANewLinkInRecentlyVisited(recentlyVisited = recentlyVisitedData)
            }
        }, async {
            if (!SettingsScreenVM.Settings.isInAppWebTabEnabled.value) {
                uriHandler.openUri(recentlyVisitedData.webURL)
            } else {
                if (context.packageManager.getInstalledApplications(0).find {
                        it.packageName == "com.android.chrome"
                    } != null) {
                    val _customTabBuilder = CustomTabsIntent.Builder()
                    _customTabBuilder.setInstantAppsEnabled(true)
                    _customTabBuilder.setShowTitle(true)
                    val customTabBuilder = _customTabBuilder.build()
                    customTabBuilder.intent.setPackage("com.android.chrome")
                    customTabBuilder.launchUrl(context, Uri.parse(recentlyVisitedData.webURL))
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