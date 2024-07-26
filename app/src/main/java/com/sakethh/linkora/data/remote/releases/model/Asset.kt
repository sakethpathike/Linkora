package com.sakethh.linkora.data.remote.releases.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Asset(
    @SerialName("browser_download_url")
    val directDownloadURL: String
)