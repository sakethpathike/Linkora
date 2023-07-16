package com.sakethh.linkora.screens.settings.appInfo.dto

import androidx.compose.runtime.MutableState
import kotlinx.serialization.Serializable

@Serializable
data class AppInfoDTO(
    val httpStatusCodeFromServer: String,

    val latestVersion: String,
    val latestVersionReleaseURL: String,
    val changeLogForLatestVersion: String,

    val latestStableVersion: String,
    val latestStableVersionReleaseURL: String,
    val changeLogForLatestStableVersion: String,
)

data class MutableStateAppInfoDTO(
    val httpStatusCodeFromServer: MutableState<String>,

    val latestVersion: MutableState<String>,
    val latestVersionReleaseURL: MutableState<String>,
    val changeLogForLatestVersion: MutableState<String>,

    val latestStableVersion: MutableState<String>,
    val latestStableVersionReleaseURL: MutableState<String>,
    val changeLogForLatestStableVersion: MutableState<String>,
)
