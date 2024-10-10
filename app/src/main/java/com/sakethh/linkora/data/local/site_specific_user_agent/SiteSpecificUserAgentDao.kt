package com.sakethh.linkora.data.local.site_specific_user_agent

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sakethh.linkora.data.local.SiteSpecificUserAgent
import kotlinx.coroutines.flow.Flow

@Dao
interface SiteSpecificUserAgentDao {

    @Insert
    suspend fun addANewSiteSpecificUserAgent(siteSpecificUserAgent: SiteSpecificUserAgent)

    @Query("DELETE FROM site_specific_user_agent WHERE domain = :domain")
    suspend fun deleteASiteSpecificUserAgent(domain: String)

    @Query("SELECT * FROM site_specific_user_agent")
    fun getAllSiteSpecificUserAgent(): Flow<List<SiteSpecificUserAgent>>

    @Query("SELECT userAgent FROM site_specific_user_agent WHERE domain=:domain")
    suspend fun getUserAgentForASpecificDomain(domain: String): String

    @Query("SELECT userAgent FROM site_specific_user_agent WHERE domain LIKE '%' || :domain || '%'")
    suspend fun getUserAgentByPartialDomain(domain: String): String

    @Query("UPDATE site_specific_user_agent SET userAgent = :newUserAgent WHERE domain = :domain")
    suspend fun updateASpecificUserAgent(domain: String, newUserAgent: String)

    @Query("SELECT EXISTS (SELECT 1 FROM site_specific_user_agent WHERE domain = :domain)")
    suspend fun doesThisDomainExists(domain: String): Boolean

    @Query("SELECT EXISTS (SELECT 1 FROM site_specific_user_agent WHERE domain LIKE '%' || :domain || '%')")
    suspend fun doesDomainExistPartially(domain: String): Boolean
}