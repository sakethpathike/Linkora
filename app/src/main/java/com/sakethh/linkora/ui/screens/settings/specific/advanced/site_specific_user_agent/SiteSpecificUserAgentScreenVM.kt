package com.sakethh.linkora.ui.screens.settings.specific.advanced.site_specific_user_agent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.data.local.SiteSpecificUserAgent
import com.sakethh.linkora.data.local.links.LinksRepo
import com.sakethh.linkora.data.local.site_specific_user_agent.SiteSpecificUserAgentRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SiteSpecificUserAgentScreenVM @Inject constructor(
    private val siteSpecificUserAgentRepo: SiteSpecificUserAgentRepo,
    private val linksRepo: LinksRepo
) : ViewModel() {

    private val _allSiteSpecificUserAgents = MutableStateFlow(emptyList<SiteSpecificUserAgent>())
    val allSiteSpecificUserAgents = _allSiteSpecificUserAgents.asStateFlow()

    init {
        viewModelScope.launch {
            siteSpecificUserAgentRepo.getAllSiteSpecificUserAgent().collectLatest {
                _allSiteSpecificUserAgents.emit(it)
            }
        }
    }

    fun addANewSiteSpecificUserAgent(domain: String, userAgent: String) {
        viewModelScope.launch {
            siteSpecificUserAgentRepo.addANewSiteSpecificUserAgent(
                siteSpecificUserAgent = SiteSpecificUserAgent(
                    domain,
                    userAgent
                )
            )
            updateUserAgentInLinkBasedTables(domain, userAgent)
        }
    }

    fun deleteASiteSpecificUserAgent(domain: String) {
        viewModelScope.launch {
            siteSpecificUserAgentRepo.deleteASiteSpecificUserAgent(
                domain
            )
            updateUserAgentInLinkBasedTables(domain = domain, newUserAgent = null)
        }
    }

    fun updateASpecificUserAgent(domain: String, newUserAgent: String) {
        viewModelScope.launch {
            siteSpecificUserAgentRepo.updateASpecificUserAgent(domain, newUserAgent)
            updateUserAgentInLinkBasedTables(domain, newUserAgent)
        }
    }

    private suspend fun updateUserAgentInLinkBasedTables(domain: String, newUserAgent: String?) {
        linksRepo.changeUserAgentInLinksTable(newUserAgent, domain)
        linksRepo.changeUserAgentInArchiveLinksTable(newUserAgent, domain)
        linksRepo.changeUserAgentInImportantLinksTable(newUserAgent, domain)
        linksRepo.changeUserAgentInHistoryTable(newUserAgent, domain)
    }
}