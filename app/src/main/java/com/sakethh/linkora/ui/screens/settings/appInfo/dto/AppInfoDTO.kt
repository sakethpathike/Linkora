package com.sakethh.linkora.ui.screens.settings.appInfo.dto

import androidx.compose.runtime.MutableState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppInfoDTO(
    @SerialName("isNonStableVersion")
    val isNonStableVersion: Boolean,
    @SerialName("isStableVersion")
    val isStableVersion: Boolean,
    @SerialName("nonStableVersionValue")
    val nonStableVersionValue: String,
    @SerialName("stableVersionValue")
    val stableVersionValue: String,
    @SerialName("nonStableVersionCode")
    val nonStableVersionCode: Int,
    @SerialName("stableVersionCode")
    val stableVersionCode: Int,
    @SerialName("stableVersionGithubReleaseNotesURL")
    val stableVersionGithubReleaseNotesURL: String,
    @SerialName("nonStableVersionGithubReleaseNotesURL")
    val nonStableVersionGithubReleaseNotesURL: String,
    @SerialName("releaseNotes")
    val releaseNotes: List<ReleaseNotesDTO>
)

@Serializable
data class ReleaseNotesDTO(
    @SerialName("versionValue")
    val versionValue: String,
    @SerialName("description")
    val description: String
)

@Serializable
data class MutableAppInfoDTO(
    val isNonStableVersion: MutableState<Boolean>,
    val isStableVersion: MutableState<Boolean>,
    val nonStableVersionValue: MutableState<String>,
    val stableVersionValue: MutableState<String>,
    val stableVersionGithubReleaseNotesURL: MutableState<String>,
    val nonStableVersionGithubReleaseNotesURL: MutableState<String>,
    val releaseNotes: MutableState<List<ReleaseNotesDTO>>,
    val nonStableVersionCode: MutableState<Int>,
    val stableVersionCode: MutableState<Int>,
)