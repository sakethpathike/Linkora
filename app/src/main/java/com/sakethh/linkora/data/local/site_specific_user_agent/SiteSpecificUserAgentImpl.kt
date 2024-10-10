package com.sakethh.linkora.data.local.site_specific_user_agent

import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.SiteSpecificUserAgent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SiteSpecificUserAgentImpl @Inject constructor(private val localDatabase: LocalDatabase) :
    SiteSpecificUserAgentRepo {
    override suspend fun addANewSiteSpecificUserAgent(siteSpecificUserAgent: SiteSpecificUserAgent) {
        localDatabase.siteSpecificUserAgentDao().addANewSiteSpecificUserAgent(siteSpecificUserAgent)
    }

    override suspend fun deleteASiteSpecificUserAgent(domain: String) {
        localDatabase.siteSpecificUserAgentDao().deleteASiteSpecificUserAgent(domain)
    }

    override fun getAllSiteSpecificUserAgent(): Flow<List<SiteSpecificUserAgent>> {
        return localDatabase.siteSpecificUserAgentDao().getAllSiteSpecificUserAgent()
    }

    override suspend fun getUserAgentForASpecificDomain(domain: String): String {
        return localDatabase.siteSpecificUserAgentDao().getUserAgentForASpecificDomain(domain)
    }

    override suspend fun doesThisDomainExists(domain: String): Boolean {
        return localDatabase.siteSpecificUserAgentDao().doesThisDomainExists(domain)
    }
    override suspend fun updateASpecificUserAgent(domain: String, newUserAgent: String) {
        localDatabase.siteSpecificUserAgentDao().updateASpecificUserAgent(domain, newUserAgent)
    }
    override suspend fun doesDomainExistPartially(domain: String): Boolean {
        return localDatabase.siteSpecificUserAgentDao().doesDomainExistPartially(domain)
    }

    override suspend fun getUserAgentByPartialDomain(domain: String): String {
        return localDatabase.siteSpecificUserAgentDao().getUserAgentByPartialDomain(domain)
    }
}