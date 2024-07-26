package com.sakethh.linkora.data.remote.metadata.twitter

import com.sakethh.linkora.data.remote.metadata.twitter.model.TwitterMetaDataDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class TwitterMetaDataImpl @Inject constructor(private val ktorClient: HttpClient) :
    TwitterMetaDataRepo {
    override suspend fun retrieveMetaData(tweetURL: String): TwitterMetaDataResult {
        return try {
            val tweetMetaData =
                ktorClient.get("https://api.vxtwitter.com/${tweetURL.substringAfter(".com/")}")
                    .body<TwitterMetaDataDTO>()
            TwitterMetaDataResult.Success(tweetMetaData)
        } catch (e: Exception) {
            e.printStackTrace()
            TwitterMetaDataResult.Failure("Failed at retrieveMetaData in TwitterMetaDataImpl")
        }
    }
}