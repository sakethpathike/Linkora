package com.sakethh.linkora.data.remote.scrape

interface LinkMetaDataScrapperService {
    suspend fun scrapeLinkData(url: String): LinkMetaDataScrapperResult
}