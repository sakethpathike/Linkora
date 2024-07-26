package com.sakethh.linkora.data.remote.metadata.twitter.model

import kotlinx.serialization.Serializable

@Serializable
data class TwitterMetaDataDTO(
    val allSameType: Boolean,
    val conversationID: String,
    val date: String,
    val date_epoch: Int,
    val hasMedia: Boolean,
    val likes: Int,
    val mediaURLs: List<String>,
    val possibly_sensitive: Boolean,
    val replies: Int,
    val retweets: Int,
    val text: String,
    val tweetID: String,
    val tweetURL: String,
    val user_name: String,
    val user_profile_image_url: String,
    val user_screen_name: String
)