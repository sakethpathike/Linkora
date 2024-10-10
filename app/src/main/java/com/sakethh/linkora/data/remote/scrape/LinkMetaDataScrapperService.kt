package com.sakethh.linkora.data.remote.scrape

import com.sakethh.linkora.data.RequestResult
import com.sakethh.linkora.data.remote.scrape.model.LinkMetaData

interface LinkMetaDataScrapperService {
    suspend fun scrapeLinkData(url: String, userAgent: String): RequestResult<LinkMetaData>
}