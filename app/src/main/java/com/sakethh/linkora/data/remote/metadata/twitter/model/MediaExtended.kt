package com.sakethh.linkora.data.remote.metadata.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class MediaExtended(
    val thumbnail_url: String,
    val type: String, // video // image
    val url: String,
)