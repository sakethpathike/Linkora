package com.sakethh.linkora.data.remote.metadata.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class TwitterMetaDataDTO(
    val hasMedia: Boolean,
    val media_extended: List<MediaExtended>,
    val text: String,
    val user_profile_image_url: String,
    val tweetURL: String
)