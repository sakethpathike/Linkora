package com.sakethh.linkora.data.remote.metadata.twitter

import com.sakethh.linkora.data.RequestResult
import com.sakethh.linkora.data.remote.metadata.twitter.model.TwitterMetaDataDTO

interface TwitterMetaDataRepo {
    suspend fun retrieveMetaData(tweetURL: String): RequestResult<TwitterMetaDataDTO>
}