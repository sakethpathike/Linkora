@file:Suppress("LocalVariableName")

package com.sakethh.linkora.customWebTab

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.platform.UriHandler
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.localDB.RecentlyVisited
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun openInWeb(
    recentlyVisitedData: RecentlyVisited,
    uriHandler: UriHandler,
    context: Context,
) {
    coroutineScope {
        awaitAll(async {
            CustomLocalDBDaoFunctionsDecl.localDB.localDBData()
                .addANewLinkInRecentlyVisited(recentlyVisited = recentlyVisitedData)
        }, async {
            if (!SettingsScreenVM.Settings.isInAppWebTabEnabled.value) {
                uriHandler.openUri(recentlyVisitedData.webURL)
            } else {
                val _customTabBuilder = CustomTabsIntent.Builder()
                _customTabBuilder.setInstantAppsEnabled(true)
                _customTabBuilder.setShowTitle(true)
                val customTabBuilder = _customTabBuilder.build()
                customTabBuilder.intent.setPackage("com.android.chrome")
                customTabBuilder.launchUrl(context, Uri.parse(recentlyVisitedData.webURL))
            }
        })
    }
}