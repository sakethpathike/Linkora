@file:Suppress("LocalVariableName")

package com.sakethh.linkora.customWebTab

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.platform.UriHandler
import com.sakethh.linkora.screens.settings.SettingsScreenVM

fun openInWeb(url: String, uriHandler: UriHandler, context: Context) {
    if (!SettingsScreenVM.Settings.isInAppWebTabEnabled.value) {
        uriHandler.openUri(url)
    } else {
        val _customTabBuilder = CustomTabsIntent.Builder()
        _customTabBuilder.setInstantAppsEnabled(true)
        _customTabBuilder.setShowTitle(true)
        val customTabBuilder = _customTabBuilder.build()
        customTabBuilder.intent.setPackage("com.android.chrome")
        customTabBuilder.launchUrl(context, Uri.parse(url))
    }
}