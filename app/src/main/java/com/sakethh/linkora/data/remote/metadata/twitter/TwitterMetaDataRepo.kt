package com.sakethh.linkora.data.remote.metadata.twitter

interface TwitterMetaDataRepo {
    suspend fun retrieveMetaData(tweetURL: String): TwitterMetaDataResult
}