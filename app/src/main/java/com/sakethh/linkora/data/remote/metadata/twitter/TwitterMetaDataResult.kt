package com.sakethh.linkora.data.remote.metadata.twitter

import com.sakethh.linkora.data.remote.metadata.twitter.model.TwitterMetaDataDTO

sealed class TwitterMetaDataResult {
    data class Success(val data: TwitterMetaDataDTO) : TwitterMetaDataResult()
    data class Failure(val message: String) : TwitterMetaDataResult()

}