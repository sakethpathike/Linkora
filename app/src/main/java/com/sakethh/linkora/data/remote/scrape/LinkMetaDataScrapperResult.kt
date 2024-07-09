package com.sakethh.linkora.data.remote.scrape

import com.sakethh.linkora.data.remote.scrape.model.LinkMetaData

sealed class LinkMetaDataScrapperResult {
    data class Success(val data: LinkMetaData) : LinkMetaDataScrapperResult()
    data class Failure(val message: String) : LinkMetaDataScrapperResult()
}