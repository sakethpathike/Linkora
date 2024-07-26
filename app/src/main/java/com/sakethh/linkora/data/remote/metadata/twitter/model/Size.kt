package com.sakethh.linkora.data.remote.metadata.twitter.model

import kotlinx.serialization.Serializable

@Serializable

data class Size(
    val height: Int,
    val width: Int
)