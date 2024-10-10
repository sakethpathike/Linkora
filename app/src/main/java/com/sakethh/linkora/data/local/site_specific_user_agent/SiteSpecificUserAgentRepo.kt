package com.sakethh.linkora.data.local.site_specific_user_agent

import com.sakethh.linkora.data.local.SiteSpecificUserAgent
import kotlinx.coroutines.flow.Flow

interface SiteSpecificUserAgentRepo {
    suspend fun addANewSiteSpecificUserAgent(siteSpecificUserAgent: SiteSpecificUserAgent)

    suspend fun deleteASiteSpecificUserAgent(domain: String)

    fun getAllSiteSpecificUserAgent(): Flow<List<SiteSpecificUserAgent>>

    suspend fun doesThisDomainExists(domain: String): Boolean
    suspend fun getUserAgentForASpecificDomain(domain: String): String

    suspend fun getUserAgentByPartialDomain(domain: String): String

    suspend fun doesDomainExistPartially(domain: String): Boolean

    suspend fun updateASpecificUserAgent(domain: String, newUserAgent: String)
}