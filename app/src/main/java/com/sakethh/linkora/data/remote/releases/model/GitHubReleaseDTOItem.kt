package com.sakethh.linkora.data.remote.releases.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubReleaseDTOItem(
    val assets: List<Asset>,
    val body: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("html_url")
    val releasePageURL: String,
    @SerialName("name")
    var releaseName: String,
)