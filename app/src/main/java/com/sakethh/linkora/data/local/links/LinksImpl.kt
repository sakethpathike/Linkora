package com.sakethh.linkora.data.local.links

import android.content.Context
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.LocalizedStrings.addedTheUrl
import com.sakethh.linkora.LocalizedStrings.couldNotRetrieveMetadataNowButLinkoraSavedTheLink
import com.sakethh.linkora.LocalizedStrings.givenLinkAlreadyExists
import com.sakethh.linkora.LocalizedStrings.invalidUrl
import com.sakethh.linkora.LocalizedStrings.movedTheLinkToArchive
import com.sakethh.linkora.LocalizedStrings.removedTheLinkFromArchive
import com.sakethh.linkora.data.RequestResult
import com.sakethh.linkora.data.RequestState
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.LocalDatabase
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.data.local.folders.FoldersRepo
import com.sakethh.linkora.data.local.site_specific_user_agent.SiteSpecificUserAgentRepo
import com.sakethh.linkora.data.remote.metadata.twitter.TwitterMetaDataRepo
import com.sakethh.linkora.data.remote.scrape.LinkMetaDataScrapperService
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets.OptionsBtmSheetVM
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.utils.TempValues
import com.sakethh.linkora.utils.isAValidURL
import com.sakethh.linkora.utils.linkoraLog
import com.sakethh.linkora.utils.sanitizeLink
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LinksImpl @Inject constructor(
    private val localDatabase: LocalDatabase, private val foldersRepo: FoldersRepo,
    private val linkMetaDataScrapperService: LinkMetaDataScrapperService,
    private val twitterMetaDataRepo: TwitterMetaDataRepo,
    private val siteSpecificUserAgentRepo: SiteSpecificUserAgentRepo
) : LinksRepo {

    private suspend fun saveLink(
        linksTable: LinksTable?,
        importantLink: ImportantLinks?,
        recentlyVisited: RecentlyVisited?,
        archivedLinks: ArchivedLinks?,
        onTaskCompleted: () -> Unit,
        linkType: LinkType,
        autoDetectTitle: Boolean,
        existingLinkID: Long,
        updateExistingLink: Boolean
    ): CommonUiEvent {
        if (
            when (linkType) {
                LinkType.FOLDER_LINK, LinkType.SAVED_LINK -> !isAValidURL(linksTable!!.webURL)
                LinkType.IMP_LINK -> !isAValidURL(importantLink!!.webURL)
                LinkType.HISTORY_LINK -> !isAValidURL(recentlyVisited!!.webURL)
                LinkType.ARCHIVE_LINK -> !isAValidURL(archivedLinks!!.webURL)
            }
        ) {
            onTaskCompleted()
            return CommonUiEvent.ShowToast(invalidUrl.value)
        } else if (
            when (linkType) {
                LinkType.FOLDER_LINK, LinkType.SAVED_LINK -> linksTable!!.keyOfLinkedFolderV10?.let {
                    doesThisLinkExistsInAFolderV10(
                        linksTable.webURL,
                        it
                    )
                } == true

                LinkType.IMP_LINK -> doesThisExistsInImpLinks(importantLink!!.webURL)
                LinkType.HISTORY_LINK -> doesThisExistsInRecentlyVisitedLinks(recentlyVisited!!.webURL)
                LinkType.ARCHIVE_LINK -> doesThisExistsInArchiveLinks(archivedLinks!!.webURL)
            } && !updateExistingLink
        ) {
            onTaskCompleted()
            return CommonUiEvent.ShowToast(givenLinkAlreadyExists.value)
        } else {

            suspend fun saveWithGivenData(): CommonUiEvent {
                RequestResult.isThisFirstRequest = true
                when (linkType) {
                    LinkType.FOLDER_LINK, LinkType.SAVED_LINK -> {
                        if (updateExistingLink) {
                            linkoraLog("Update ${linkType.name}")
                            localDatabase.linksDao()
                                .updateALinkDataFromLinksTable(linksTable!!.copy(id = existingLinkID))
                        } else {
                            linkoraLog("Create ${linkType.name}")
                            localDatabase.linksDao()
                                .addANewLinkToSavedLinksOrInFolders(linksTable!!)
                        }
                    }

                    LinkType.IMP_LINK -> {
                        if (updateExistingLink) {
                            linkoraLog("Update ${linkType.name}")
                            localDatabase.linksDao()
                                .updateALinkDataFromImpLinksTable(importantLink!!.copy(id = existingLinkID))
                        } else {
                            linkoraLog("Create ${linkType.name}")
                            localDatabase.linksDao()
                                .addANewLinkToImpLinks(importantLink!!)
                        }
                    }

                    LinkType.HISTORY_LINK -> {
                        if (updateExistingLink) {
                            linkoraLog("Update ${linkType.name}")
                            localDatabase.linksDao()
                                .updateALinkDataFromRecentlyVisitedLinksTable(
                                    recentlyVisited!!.copy(
                                        id = existingLinkID
                                    )
                                )
                        } else {
                            linkoraLog("Create ${linkType.name}")
                            localDatabase.linksDao()
                                .addANewLinkInRecentlyVisited(recentlyVisited!!)
                        }
                    }

                    LinkType.ARCHIVE_LINK -> {
                        if (updateExistingLink) {
                            linkoraLog("Update ${linkType.name}")
                            localDatabase.linksDao()
                                .updateALinkDataFromArchivedLinksTable(
                                    archivedLinks!!.copy(
                                        id = existingLinkID
                                    )
                                )
                        } else {
                            linkoraLog("Create ${linkType.name}")
                            localDatabase.linksDao()
                                .addANewLinkToArchiveLink(archivedLinks!!)
                        }
                    }
                }
                onTaskCompleted()
                return if (RequestResult.isThisFirstRequest.not()) {
                    CommonUiEvent.Nothing
                } else {
                    CommonUiEvent.ShowToast(couldNotRetrieveMetadataNowButLinkoraSavedTheLink.value)
                }
            }

            if (SettingsPreference.forceSaveWithoutFetchingAnyMetaData.value || TempValues.forceSaveWithoutFetchingAnyMetaData.value) {
                saveWithGivenData()
                TempValues.forceSaveWithoutFetchingAnyMetaData.value = false
                return CommonUiEvent.ShowToast("saved without fetching metadata")
            }

            if (RequestResult.isThisFirstRequest) {
                RequestResult.updateState(RequestState.REQUESTING)
            }

            if (
                when (linkType) {
                    LinkType.FOLDER_LINK, LinkType.SAVED_LINK -> linksTable!!.webURL.trim()
                        .startsWith("https://x.com/") || linksTable.webURL.trim()
                        .startsWith("http://x.com/") || linksTable.webURL.trim()
                        .startsWith("https://twitter.com/") || linksTable.webURL.trim()
                        .startsWith("http://twitter.com/")

                    LinkType.IMP_LINK -> importantLink!!.webURL.trim()
                        .startsWith("https://x.com/") || importantLink.webURL.trim()
                        .startsWith("http://x.com/") || importantLink.webURL.trim()
                        .startsWith("https://twitter.com/") || importantLink.webURL.trim()
                        .startsWith("http://twitter.com/")

                    LinkType.HISTORY_LINK -> recentlyVisited!!.webURL.trim()
                        .startsWith("https://x.com/") || recentlyVisited.webURL.trim()
                        .startsWith("http://x.com/") || recentlyVisited.webURL.trim()
                        .startsWith("https://twitter.com/") || recentlyVisited.webURL.trim()
                        .startsWith("http://twitter.com/")

                    LinkType.ARCHIVE_LINK -> archivedLinks!!.webURL.trim()
                        .startsWith("https://x.com/") || archivedLinks.webURL.trim()
                        .startsWith("http://x.com/") || archivedLinks.webURL.trim()
                        .startsWith("https://twitter.com/") || archivedLinks.webURL.trim()
                        .startsWith("http://twitter.com/")
                }
            ) {
                when (val tweetMetaData =
                    twitterMetaDataRepo.retrieveMetaData(
                        when (linkType) {
                            LinkType.FOLDER_LINK, LinkType.SAVED_LINK -> linksTable!!.webURL.trim()
                            LinkType.IMP_LINK -> importantLink!!.webURL.trim()
                            LinkType.HISTORY_LINK -> recentlyVisited!!.webURL.trim()
                            LinkType.ARCHIVE_LINK -> archivedLinks!!.webURL.trim()
                        }
                    )) {
                    is RequestResult.Failure -> {
                        RequestResult.updateState(RequestState.FAILED)
                        return saveWithGivenData()
                    }

                    is RequestResult.Success -> {
                        when (linkType) {
                            LinkType.FOLDER_LINK, LinkType.SAVED_LINK -> {
                                linkoraLog(tweetMetaData.data.toString())
                                val linkTableData = LinksTable(
                                    title = if (SettingsPreference.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) tweetMetaData.data.text else linksTable!!.title,
                                    webURL = tweetMetaData.data.tweetURL,
                                    baseURL = "twitter.com",
                                    imgURL = if (tweetMetaData.data.hasMedia && tweetMetaData.data.media_extended.isNotEmpty() && tweetMetaData.data.media_extended.any { it.type == "image" }) tweetMetaData.data.media_extended.find { it.type == "image" }?.url ?: tweetMetaData.data.user_profile_image_url else if(tweetMetaData.data.hasMedia && tweetMetaData.data.media_extended.isNotEmpty() && tweetMetaData.data.media_extended.any { it.type == "video" }) tweetMetaData.data.media_extended.find { it.type == "video" }?.thumbnail_url ?: tweetMetaData.data.user_profile_image_url else if(tweetMetaData.data.hasMedia && tweetMetaData.data.media_extended.isNotEmpty() && tweetMetaData.data.media_extended.any { it.type == "gif" }) tweetMetaData.data.media_extended.find { it.type == "gif" }?.thumbnail_url ?: tweetMetaData.data.user_profile_image_url
                                         else tweetMetaData.data.user_profile_image_url,
                                    infoForSaving = linksTable!!.infoForSaving,
                                    isLinkedWithSavedLinks = linkType == LinkType.SAVED_LINK,
                                    isLinkedWithFolders = linkType == LinkType.FOLDER_LINK,
                                    keyOfLinkedFolderV10 = linksTable.keyOfLinkedFolderV10,
                                    keyOfLinkedFolder = null,
                                    isLinkedWithImpFolder = false,
                                    isLinkedWithArchivedFolder = false,
                                    keyOfArchiveLinkedFolderV10 = 0,
                                    keyOfImpLinkedFolder = ""
                                )
                                if (updateExistingLink) {
                                    linkoraLog("Update ${linkType.name}")
                                    localDatabase.linksDao()
                                        .updateALinkDataFromLinksTable(linkTableData.copy(id = existingLinkID))
                                } else {
                                    linkoraLog("Create ${linkType.name}")
                                    localDatabase.linksDao()
                                        .addANewLinkToSavedLinksOrInFolders(linkTableData)
                                }
                            }

                            LinkType.IMP_LINK -> {
                                linkoraLog(tweetMetaData.data.toString())
                                val importantLinkScrappedData = ImportantLinks(
                                    title = if (SettingsPreference.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle)
                                        tweetMetaData.data.text else importantLink!!.title,
                                    webURL = sanitizeLink(importantLink!!.webURL),
                                    baseURL = "twitter.com",
                                    imgURL = if (tweetMetaData.data.hasMedia && tweetMetaData.data.media_extended.isNotEmpty() && tweetMetaData.data.media_extended.any { it.type == "image" }) tweetMetaData.data.media_extended.find { it.type == "image" }?.url
                                        ?: tweetMetaData.data.user_profile_image_url else tweetMetaData.data.user_profile_image_url,
                                    infoForSaving = importantLink.infoForSaving
                                )
                                if (updateExistingLink) {
                                    linkoraLog("Update ${linkType.name}")
                                    localDatabase.linksDao().updateALinkDataFromImpLinksTable(
                                        importantLinkScrappedData.copy(id = existingLinkID)
                                    )
                                } else {
                                    linkoraLog("Create ${linkType.name}")
                                    localDatabase.linksDao()
                                        .addANewLinkToImpLinks(importantLinkScrappedData)
                                }
                            }

                            LinkType.HISTORY_LINK -> {
                                linkoraLog(tweetMetaData.data.toString())
                                val recentlyVisitedLinkScrappedData = RecentlyVisited(
                                    title = if (SettingsPreference.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle
                                    ) tweetMetaData.data.text else recentlyVisited!!.title,
                                    webURL = sanitizeLink(recentlyVisited!!.webURL),
                                    baseURL = "twitter.com",
                                    imgURL = if (tweetMetaData.data.hasMedia && tweetMetaData.data.media_extended.isNotEmpty() && tweetMetaData.data.media_extended.any { it.type == "image" }) tweetMetaData.data.media_extended.find { it.type == "image" }?.url
                                        ?: tweetMetaData.data.user_profile_image_url else tweetMetaData.data.user_profile_image_url,
                                    infoForSaving = recentlyVisited.infoForSaving
                                )
                                if (updateExistingLink) {
                                    linkoraLog("Update ${linkType.name}")
                                    localDatabase.linksDao()
                                        .updateALinkDataFromRecentlyVisitedLinksTable(
                                            recentlyVisitedLinkScrappedData.copy(id = existingLinkID)
                                        )
                                } else {
                                    linkoraLog("Create ${linkType.name}")
                                    localDatabase.linksDao()
                                        .addANewLinkInRecentlyVisited(
                                            recentlyVisitedLinkScrappedData
                                        )
                                }
                            }

                            LinkType.ARCHIVE_LINK -> {
                                val archiveLinkScrappedData = ArchivedLinks(
                                    title = if (SettingsPreference.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle
                                    ) tweetMetaData.data.text else archivedLinks!!.title,
                                    webURL = sanitizeLink(archivedLinks!!.webURL),
                                    baseURL = "twitter.com",
                                    imgURL = if (tweetMetaData.data.hasMedia && tweetMetaData.data.media_extended.isNotEmpty() && tweetMetaData.data.media_extended.any { it.type == "image" }) tweetMetaData.data.media_extended.find { it.type == "image" }?.url
                                        ?: tweetMetaData.data.user_profile_image_url else tweetMetaData.data.user_profile_image_url,
                                    infoForSaving = archivedLinks.infoForSaving
                                )
                                if (updateExistingLink) {
                                    linkoraLog("Update ${linkType.name}")
                                    localDatabase.linksDao().updateALinkDataFromArchivedLinksTable(
                                        archiveLinkScrappedData.copy(id = existingLinkID)
                                    )
                                } else {
                                    linkoraLog("Create ${linkType.name}")
                                    localDatabase.linksDao()
                                        .addANewLinkToArchiveLink(archiveLinkScrappedData)
                                }

                            }
                        }
                        onTaskCompleted()
                        return CommonUiEvent.ShowToast(addedTheUrl.value)
                    }
                }
            }

            val domain = when (linkType) {
                LinkType.FOLDER_LINK, LinkType.SAVED_LINK -> {
                    linksTable!!.webURL.substringAfter("http")
                        .substringBefore(" ").trim()
                }

                LinkType.IMP_LINK -> importantLink!!.webURL.substringAfter("http")
                    .substringBefore(" ").trim()

                LinkType.HISTORY_LINK -> recentlyVisited!!.webURL.substringAfter("http")
                    .substringBefore(" ").trim()

                LinkType.ARCHIVE_LINK -> archivedLinks!!.webURL.substringAfter("http")
                    .substringBefore(" ").trim()
            }.split("/")[2].replace("www.", "").replace("http://", "")
                .replace("https://", "")

            linkoraLog("baseurl is $domain")

            val currentUserAgent =
                when (linkType) {
                    LinkType.FOLDER_LINK, LinkType.SAVED_LINK -> {
                        linksTable!!.userAgent
                    }

                    LinkType.IMP_LINK -> importantLink!!.userAgent

                    LinkType.HISTORY_LINK -> recentlyVisited!!.userAgent

                    LinkType.ARCHIVE_LINK -> archivedLinks!!.userAgent
                } ?: if (siteSpecificUserAgentRepo.doesDomainExistPartially(domain)) {
                    val userAgentByPartialDomain =
                        siteSpecificUserAgentRepo.getUserAgentByPartialDomain(domain)
                    linkoraLog("didn't find existing user agent, so retrieved baseurl is $userAgentByPartialDomain")
                    userAgentByPartialDomain
                } else if (RequestResult.isThisFirstRequest.not()) {
                    SettingsPreference.secondaryJsoupUserAgent.value
                } else {
                    SettingsPreference.primaryJsoupUserAgent.value
                }

            when (val linkDataExtractor =
                linkMetaDataScrapperService.scrapeLinkData(
                    userAgent = currentUserAgent,
                    url = sanitizeLink(
                        "http" + when (linkType) {
                            LinkType.FOLDER_LINK, LinkType.SAVED_LINK -> {
                                linksTable!!.webURL.substringAfter("http")
                                    .substringBefore(" ").trim()
                            }

                            LinkType.IMP_LINK -> importantLink!!.webURL.substringAfter("http")
                                .substringBefore(" ").trim()

                            LinkType.HISTORY_LINK -> recentlyVisited!!.webURL.substringAfter("http")
                                .substringBefore(" ").trim()

                            LinkType.ARCHIVE_LINK -> archivedLinks!!.webURL.substringAfter("http")
                                .substringBefore(" ").trim()
                        }
                    )
                )) {
                is RequestResult.Failure -> {
                    RequestResult.isThisFirstRequest = !RequestResult.isThisFirstRequest
                    if (siteSpecificUserAgentRepo.doesDomainExistPartially(domain)) {
                        return saveWithGivenData()
                    }
                    return if (!RequestResult.isThisFirstRequest) {
                        saveLink(
                            linksTable,
                            importantLink,
                            recentlyVisited,
                            archivedLinks,
                            onTaskCompleted,
                            linkType,
                            autoDetectTitle,
                            existingLinkID,
                            updateExistingLink
                        )
                    } else {
                        saveWithGivenData()
                    }
                }

                is RequestResult.Success -> {
                    RequestResult.isThisFirstRequest = true
                    when (linkType) {
                        LinkType.FOLDER_LINK, LinkType.SAVED_LINK -> {
                            val linkData = LinksTable(
                                title = if (SettingsPreference.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) linkDataExtractor.data.title.replace(
                                    "&amp;",
                                    "&"
                                ) else linksTable!!.title,
                                webURL = "http" + linksTable!!.webURL.substringAfter("http")
                                    .substringBefore(" ").trim(),
                                baseURL = linkDataExtractor.data.baseURL,
                                imgURL = linkDataExtractor.data.imgURL,
                                infoForSaving = linksTable.infoForSaving,
                                isLinkedWithSavedLinks = linkType == LinkType.SAVED_LINK,
                                isLinkedWithFolders = linkType == LinkType.FOLDER_LINK,
                                keyOfLinkedFolderV10 = linksTable.keyOfLinkedFolderV10,
                                keyOfLinkedFolder = null,
                                isLinkedWithImpFolder = false,
                                isLinkedWithArchivedFolder = false,
                                keyOfArchiveLinkedFolderV10 = 0,
                                keyOfImpLinkedFolder = "",
                                userAgent = currentUserAgent
                            )
                            if (updateExistingLink) {
                                linkoraLog("Update ${linkType.name}")
                                localDatabase.linksDao()
                                    .updateALinkDataFromLinksTable(linkData.copy(id = existingLinkID))
                            } else {
                                linkoraLog("Create ${linkType.name}")
                                localDatabase.linksDao()
                                    .addANewLinkToSavedLinksOrInFolders(linkData)
                            }
                        }

                        LinkType.IMP_LINK -> {
                            val importantLinkScrappedData = ImportantLinks(
                                title = if (SettingsPreference.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) linkDataExtractor.data.title.replace(
                                    "&amp;",
                                    "&"
                                ) else importantLink!!.title,
                                webURL = sanitizeLink(importantLink!!.webURL),
                                baseURL = linkDataExtractor.data.baseURL,
                                imgURL = linkDataExtractor.data.imgURL,
                                infoForSaving = importantLink.infoForSaving,
                                userAgent = currentUserAgent
                            )
                            if (updateExistingLink) {
                                linkoraLog("Update ${linkType.name}")
                                localDatabase.linksDao().updateALinkDataFromImpLinksTable(
                                    importantLinkScrappedData.copy(id = existingLinkID)
                                )
                            } else {
                                linkoraLog("Create ${linkType.name}")
                                localDatabase.linksDao()
                                    .addANewLinkToImpLinks(importantLinkScrappedData)
                            }
                        }

                        LinkType.HISTORY_LINK -> {
                            val recentlyVisitedLinkScrappedData = RecentlyVisited(
                                title = if (SettingsPreference.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) linkDataExtractor.data.title.replace(
                                    "&amp;",
                                    "&"
                                ) else recentlyVisited!!.title,
                                webURL = sanitizeLink(recentlyVisited!!.webURL),
                                baseURL = linkDataExtractor.data.baseURL,
                                imgURL = linkDataExtractor.data.imgURL,
                                infoForSaving = recentlyVisited.infoForSaving,
                                userAgent = currentUserAgent
                            )
                            if (updateExistingLink) {
                                linkoraLog("Update ${linkType.name}")
                                localDatabase.linksDao()
                                    .updateALinkDataFromRecentlyVisitedLinksTable(
                                        recentlyVisitedLinkScrappedData.copy(id = existingLinkID)
                                    )
                            } else {
                                linkoraLog("Create ${linkType.name}")
                                localDatabase.linksDao()
                                    .addANewLinkInRecentlyVisited(recentlyVisitedLinkScrappedData)
                            }
                        }

                        LinkType.ARCHIVE_LINK -> {
                            val archiveLinkScrappedData = ArchivedLinks(
                                title = if (SettingsPreference.isAutoDetectTitleForLinksEnabled.value || autoDetectTitle) linkDataExtractor.data.title.replace(
                                    "&amp;",
                                    "&"
                                ) else archivedLinks!!.title,
                                webURL = sanitizeLink(archivedLinks!!.webURL),
                                baseURL = linkDataExtractor.data.baseURL,
                                imgURL = linkDataExtractor.data.imgURL,
                                infoForSaving = archivedLinks.infoForSaving,
                                userAgent = currentUserAgent
                            )
                            if (updateExistingLink) {
                                linkoraLog("Update ${linkType.name}")
                                localDatabase.linksDao()
                                    .updateALinkDataFromArchivedLinksTable(
                                        archiveLinkScrappedData.copy(id = existingLinkID)
                                    )
                            } else {
                                linkoraLog("Create ${linkType.name}")
                                localDatabase.linksDao()
                                    .addANewLinkToArchiveLink(archiveLinkScrappedData)
                            }
                        }
                    }

                    onTaskCompleted()
                    return CommonUiEvent.ShowToast(addedTheUrl.value)
                }
            }
        }
    }

    override suspend fun addANewLinkToSavedLinks(
        linksTable: LinksTable,
        onTaskCompleted: () -> Unit,
        autoDetectTitle: Boolean
    ): CommonUiEvent {
        return saveLink(
            linksTable = linksTable,
            importantLink = null,
            onTaskCompleted = onTaskCompleted,
            linkType = LinkType.SAVED_LINK,
            autoDetectTitle = autoDetectTitle,
            existingLinkID = 0L,
            updateExistingLink = false,
            recentlyVisited = null,
            archivedLinks = null
        )
    }

    override suspend fun addANewLinkInAFolder(
        linksTable: LinksTable,
        onTaskCompleted: () -> Unit,
        autoDetectTitle: Boolean
    ): CommonUiEvent {
        return saveLink(
            linksTable = linksTable,
            importantLink = null,
            onTaskCompleted = onTaskCompleted,
            linkType = LinkType.FOLDER_LINK,
            autoDetectTitle = autoDetectTitle,
            existingLinkID = 0L,
            updateExistingLink = false,
            recentlyVisited = null, archivedLinks = null
        )
    }

    override suspend fun markThisLinkFromLinksTableAsFolderLink(
        linkID: Long,
        targetFolderId: Long
    ) {
        localDatabase.linksDao().markThisLinkFromLinksTableAsFolderLink(linkID, targetFolderId)
    }

    override suspend fun duplicateFolderBasedLinks(
        currentIdOfLinkedFolder: Long,
        newIdOfLinkedFolder: Long
    ) {
        localDatabase.linksDao()
            .duplicateFolderBasedLinks(currentIdOfLinkedFolder, newIdOfLinkedFolder)
    }
    override suspend fun markThisLinkFromLinksTableAsSavedLink(linkID: Long) {
        localDatabase.linksDao().markThisLinkFromLinksTableAsSavedLink(linkID)
    }

    override suspend fun addListOfDataInLinksTable(list: List<LinksTable>) {
        return localDatabase.linksDao().addListOfDataInLinksTable(list)
    }

    override suspend fun addALinkInLinksTable(linksTable: LinksTable) {
        localDatabase.linksDao().addALinkInLinksTable(linksTable)
    }
    override suspend fun deleteANoteFromArchiveLinks(linkID: Long) {
        localDatabase.linksDao().deleteANoteFromArchiveLinks(linkID)
    }

    override suspend fun deleteANoteFromArchiveLinks(webURL: String) {
        return localDatabase.linksDao().deleteANoteFromArchiveLinks(webURL)
    }

    override suspend fun deleteALinkNoteFromArchiveBasedFolderLinksV10(
        webURL: String,
        folderID: Long
    ) {
        return localDatabase.linksDao().deleteALinkNoteFromArchiveBasedFolderLinksV10(
            webURL,
            folderID
        )
    }

    override suspend fun deleteALinkNoteFromArchiveBasedFolderLinksV9(
        webURL: String,
        folderName: String
    ) {
        return localDatabase.linksDao().deleteALinkNoteFromArchiveBasedFolderLinksV9(
            webURL,
            folderName
        )
    }

    override suspend fun deleteANoteFromImportantLinks(webURL: String) {
        return localDatabase.linksDao().deleteANoteFromImportantLinks(webURL)
    }

    override suspend fun deleteANoteFromImportantLinks(linkID: Long) {
        return localDatabase.linksDao().deleteANoteFromImportantLinks(linkID)
    }

    override suspend fun deleteANoteFromLinksTable(linkID: Long) {
        localDatabase.linksDao().deleteANoteFromLinksTable(linkID)
    }

    override suspend fun deleteANoteFromRecentlyVisited(webURL: String) {
        return localDatabase.linksDao().deleteANoteFromRecentlyVisited(webURL)
    }

    override suspend fun deleteANoteFromRecentlyVisited(linkID: Long) {
        localDatabase.linksDao().deleteANoteFromRecentlyVisited(linkID)
    }

    override suspend fun deleteALinkInfoFromSavedLinks(webURL: String) {
        return localDatabase.linksDao().deleteALinkInfoFromSavedLinks(webURL)
    }

    override suspend fun deleteALinkInfoOfFolders(linkID: Long) {
        return localDatabase.linksDao().deleteALinkInfoOfFolders(linkID)
    }

    override suspend fun deleteALinkFromSavedLinksBasedOnURL(webURL: String) {
        return localDatabase.linksDao().deleteALinkFromSavedLinksBasedOnURL(webURL)
    }

    override suspend fun deleteALinkFromImpLinks(linkID: Long) {
        return localDatabase.linksDao().deleteALinkFromImpLinks(linkID)
    }

    override suspend fun deleteALinkFromImpLinksBasedOnURL(webURL: String) {
        return localDatabase.linksDao().deleteALinkFromImpLinksBasedOnURL(webURL)
    }

    override suspend fun deleteALinkFromArchiveLinksV9(webURL: String) {
        return localDatabase.linksDao().deleteALinkFromArchiveLinksV9(webURL)
    }

    override suspend fun deleteALinkFromArchiveLinks(id: Long) {
        return localDatabase.linksDao().deleteALinkFromArchiveLinks(id)
    }

    override suspend fun archiveLinkTableUpdater(
        archivedLinks: ArchivedLinks,
        context: Context,
        onTaskCompleted: () -> Unit
    ): CommonUiEvent {
        val toastMsg: String
        if (doesThisExistsInArchiveLinks(webURL = archivedLinks.webURL)) {
            deleteALinkFromArchiveLinksV9(webURL = archivedLinks.webURL)
            toastMsg = removedTheLinkFromArchive.value
            onTaskCompleted()
        } else {
            addANewLinkToArchiveLink(archivedLinks = archivedLinks)
            toastMsg = movedTheLinkToArchive.value
            onTaskCompleted()
        }
        OptionsBtmSheetVM(
            this,
            foldersRepo
        ).updateArchiveLinkCardData(url = archivedLinks.webURL)
        return CommonUiEvent.ShowToast(toastMsg)
    }

    override suspend fun deleteALinkFromArchiveFolderBasedLinksV10(
        webURL: String,
        archiveFolderID: Long
    ) {
        return localDatabase.linksDao().deleteALinkFromArchiveFolderBasedLinksV10(
            webURL,
            archiveFolderID
        )
    }

    override suspend fun deleteALinkFromArchiveFolderBasedLinksV9(
        webURL: String,
        folderName: String
    ) {
        return localDatabase.linksDao().deleteALinkFromArchiveFolderBasedLinksV9(
            webURL,
            folderName
        )
    }

    override suspend fun deleteALinkFromLinksTable(linkID: Long) {
        return localDatabase.linksDao().deleteALinkFromLinksTable(linkID)
    }

    override suspend fun deleteALinkFromSpecificFolderV10(webURL: String, folderID: Long) {
        return localDatabase.linksDao().deleteALinkFromSpecificFolderV10(webURL, folderID)
    }

    override suspend fun deleteALinkFromSpecificFolderV9(webURL: String, folderName: String) {
        return localDatabase.linksDao().deleteALinkFromSpecificFolderV9(webURL, folderName)
    }

    override suspend fun deleteARecentlyVisitedLink(webURL: String) {
        return localDatabase.linksDao().deleteARecentlyVisitedLink(webURL)
    }

    override suspend fun deleteARecentlyVisitedLink(linkID: Long) {
        return localDatabase.linksDao().deleteARecentlyVisitedLink(linkID)
    }

    override suspend fun deleteThisFolderLinksV10(folderID: Long) {
        return localDatabase.linksDao().deleteThisFolderLinksV10(folderID)
    }

    override suspend fun deleteMultipleLinksFromLinksTable(links: Array<Long>) {
        return localDatabase.linksDao().deleteMultipleLinksFromLinksTable(links)
    }

    override suspend fun deleteThisFolderLinksV9(folderName: String) {
        return localDatabase.linksDao().deleteThisFolderLinksV9(folderName)
    }

    override suspend fun deleteThisArchiveFolderDataV9(folderID: String) {
        return localDatabase.linksDao().deleteThisArchiveFolderDataV9(folderID)
    }

    override suspend fun reloadArchiveLink(linkID: Long) {
        val linkData = getThisLinkFromArchiveLinksTable(linkID)
        linkoraLog("${linkData.title} In reloadArchiveLink")
        saveLink(
            linksTable = null,
            importantLink = null,
            recentlyVisited = null,
            onTaskCompleted = {},
            linkType = LinkType.ARCHIVE_LINK,
            autoDetectTitle = true,
            existingLinkID = linkID,
            updateExistingLink = true, archivedLinks = linkData
        )
    }

    override suspend fun reloadLinksTableLink(linkID: Long) {
        val linkData = getThisLinkFromLinksTable(linkID)
        linkoraLog("${linkData.title} In reloadLinksTableLink")
        saveLink(
            linksTable = linkData,
            importantLink = null,
            recentlyVisited = null,
            onTaskCompleted = {},
            linkType = if (linkData.isLinkedWithSavedLinks) LinkType.SAVED_LINK else LinkType.FOLDER_LINK,
            autoDetectTitle = true,
            existingLinkID = linkID,
            updateExistingLink = true, archivedLinks = null
        )
    }

    override suspend fun reloadImpLinksTableLink(linkID: Long) {
        val linkData = getThisLinkFromImpLinksTable(linkID)
        linkoraLog("${linkData.title} In reloadImpLinksTableLink")
        saveLink(
            linksTable = null,
            importantLink = linkData,
            recentlyVisited = null,
            onTaskCompleted = {},
            linkType = LinkType.IMP_LINK,
            autoDetectTitle = true,
            existingLinkID = linkID,
            updateExistingLink = true, archivedLinks = null
        )
    }

    override suspend fun getThisLinkFromLinksTable(linkID: Long): LinksTable {
        return localDatabase.linksDao().getThisLinkFromLinksTable(linkID)
    }

    override suspend fun getThisLinkFromImpLinksTable(linkID: Long): ImportantLinks {
        return localDatabase.linksDao().getThisLinkFromImpLinksTable(linkID)
    }

    override suspend fun getThisLinkFromArchiveLinksTable(linkID: Long): ArchivedLinks {
        return localDatabase.linksDao().getThisLinkFromArchiveLinksTable(linkID)
    }

    override suspend fun getThisLinkFromRecentlyVisitedLinksTable(linkID: Long): RecentlyVisited {
        return localDatabase.linksDao().getThisLinkFromRecentlyVisitedLinksTable(linkID)
    }

    override suspend fun reloadHistoryLinksTableLink(linkID: Long) {
        val linkData = getThisLinkFromRecentlyVisitedLinksTable(linkID)
        linkoraLog("${linkData.title} In reloadHistoryLinksTableLink")
        saveLink(
            linksTable = null,
            importantLink = null,
            recentlyVisited = linkData,
            onTaskCompleted = {},
            linkType = LinkType.HISTORY_LINK,
            autoDetectTitle = true,
            existingLinkID = linkID,
            updateExistingLink = true, archivedLinks = null
        )
    }

    override suspend fun addANewLinkToImpLinks(
        importantLink: ImportantLinks,
        onTaskCompleted: () -> Unit,
        autoDetectTitle: Boolean
    ): CommonUiEvent {
        return saveLink(
            linksTable = null,
            importantLink = importantLink,
            onTaskCompleted = onTaskCompleted,
            linkType = LinkType.IMP_LINK,
            autoDetectTitle = autoDetectTitle,
            existingLinkID = 0L,
            updateExistingLink = false,
            recentlyVisited = null, archivedLinks = null
        )
    }

    override suspend fun addANewLinkToImpLinks(
        importantLink: ImportantLinks
    ): CommonUiEvent {
        localDatabase.linksDao().addANewLinkToImpLinks(importantLink)
        return CommonUiEvent.ShowToast(LocalizedStrings.addedLinkToImportantLinks.value)
    }

    override suspend fun addANewLinkToArchiveLink(archivedLinks: ArchivedLinks) {
        return localDatabase.linksDao().addANewLinkToArchiveLink(archivedLinks)
    }

    override fun getAllSavedLinks(): Flow<List<LinksTable>> {
        return localDatabase.linksDao().getAllSavedLinks()
    }

    override suspend fun getAllSavedLinksAsList(): List<LinksTable> {
        return localDatabase.linksDao().getAllSavedLinksAsList()
    }
    override suspend fun getAllFromLinksTable(): List<LinksTable> {
        return localDatabase.linksDao().getAllFromLinksTable()
    }

    override suspend fun getAllRecentlyVisitedLinks(): List<RecentlyVisited> {
        return localDatabase.linksDao().getAllRecentlyVisitedLinks()
    }

    override suspend fun getAllImpLinks(): List<ImportantLinks> {
        return localDatabase.linksDao().getAllImpLinks()
    }

    override suspend fun getAllArchiveLinks(): List<ArchivedLinks> {
        return localDatabase.linksDao().getAllArchiveLinks()
    }

    override fun getAllArchiveFoldersLinks(): Flow<List<LinksTable>> {
        return localDatabase.linksDao().getAllArchiveFoldersLinks()
    }

    override fun getLinksOfThisFolderV10(folderID: Long): Flow<List<LinksTable>> {
        return localDatabase.linksDao().getLinksOfThisFolderV10(folderID)
    }

    override suspend fun getLinksOfThisFolderAsList(folderID: Long): List<LinksTable> {
        return localDatabase.linksDao().getLinksOfThisFolderAsList(folderID)
    }
    override fun getLinksOfThisFolderV9(folderName: String): Flow<List<LinksTable>> {
        return localDatabase.linksDao().getLinksOfThisFolderV9(folderName)
    }

    override fun getThisArchiveFolderLinksV10(folderID: Long): Flow<List<LinksTable>> {
        return localDatabase.linksDao().getThisArchiveFolderLinksV10(folderID)
    }

    override fun getThisArchiveFolderLinksV9(folderName: String): Flow<List<LinksTable>> {
        return localDatabase.linksDao().getThisArchiveFolderLinksV9(folderName)
    }

    override suspend fun doesThisExistsInImpLinks(webURL: String): Boolean {
        return localDatabase.linksDao().doesThisExistsInImpLinks(webURL)
    }

    override suspend fun doesThisExistsInSavedLinks(webURL: String): Boolean {
        return localDatabase.linksDao().doesThisExistsInSavedLinks(webURL)
    }

    override suspend fun doesThisLinkExistsInAFolderV10(webURL: String, folderID: Long): Boolean {
        return localDatabase.linksDao().doesThisLinkExistsInAFolderV10(webURL, folderID)
    }

    override suspend fun getLastIDOfHistoryTable(): Long {
        return localDatabase.linksDao().getLastIDOfHistoryTable()
    }

    override suspend fun getLastIDOfLinksTable(): Long {
        return localDatabase.linksDao().getLastIDOfLinksTable()
    }

    override suspend fun getLastIDOfImpLinksTable(): Long {
        return localDatabase.linksDao().getLastIDOfImpLinksTable()
    }

    override suspend fun getLastIDOfArchivedLinksTable(): Long {
        return localDatabase.linksDao().getLastIDOfArchivedLinksTable()
    }

    override suspend fun doesThisLinkExistsInAFolderV9(
        webURL: String,
        folderName: String
    ): Boolean {
        return localDatabase.linksDao().doesThisLinkExistsInAFolderV9(webURL, folderName)
    }

    override suspend fun doesThisExistsInArchiveLinks(webURL: String): Boolean {
        return localDatabase.linksDao().doesThisExistsInArchiveLinks(webURL)
    }

    override suspend fun doesThisExistsInRecentlyVisitedLinks(webURL: String): Boolean {
        return localDatabase.linksDao().doesThisExistsInRecentlyVisitedLinks(webURL)
    }

    override suspend fun isLinksTableEmpty(): Boolean {
        return localDatabase.linksDao().isLinksTableEmpty()
    }

    override suspend fun isArchivedLinksTableEmpty(): Boolean {
        return localDatabase.linksDao().isArchivedLinksTableEmpty()
    }

    override suspend fun isArchivedFoldersTableEmpty(): Boolean {
        return localDatabase.linksDao().isArchivedFoldersTableEmpty()
    }

    override suspend fun isFoldersTableEmpty(): Boolean {
        return localDatabase.foldersDao().isFoldersTableEmpty()
    }

    override suspend fun isImpLinksTableEmpty(): Boolean {
        return localDatabase.linksDao().isImpLinksTableEmpty()
    }

    override suspend fun isHistoryLinksTableEmpty(): Boolean {
        return localDatabase.linksDao().isHistoryLinksTableEmpty()
    }

    override suspend fun copyLinkFromLinksTableToArchiveLinks(id: Long) {
        localDatabase.linksDao().copyLinkFromLinksTableToArchiveLinks(id)
    }

    override suspend fun copyLinkFromImpLinksTableToArchiveLinks(id: Long) {
        localDatabase.linksDao().copyLinkFromImpLinksTableToArchiveLinks(id)
    }

    override suspend fun copyLinkFromImpTableToArchiveLinks(link: String) {
        localDatabase.linksDao().copyLinkFromImpTableToArchiveLinks(link)
    }

    override suspend fun updateALinkDataFromLinksTable(linksTable: LinksTable) {
        localDatabase.linksDao().updateALinkDataFromLinksTable(linksTable)
    }

    override suspend fun updateALinkDataFromImpLinksTable(importantLinks: ImportantLinks) {
        localDatabase.linksDao().updateALinkDataFromImpLinksTable(importantLinks)
    }

    override suspend fun updateALinkDataFromRecentlyVisitedLinksTable(recentlyVisited: RecentlyVisited) {
        localDatabase.linksDao().updateALinkDataFromRecentlyVisitedLinksTable(recentlyVisited)
    }

    override suspend fun updateALinkDataFromArchivedLinksTable(archivedLinks: ArchivedLinks) {
        localDatabase.linksDao().updateALinkDataFromArchivedLinksTable(archivedLinks)
    }

    override suspend fun renameALinkTitleFromRecentlyVisited(linkID: Long, newTitle: String) {
        localDatabase.linksDao().renameALinkTitleFromRecentlyVisited(linkID, newTitle)
    }

    override suspend fun renameALinkTitleFromRecentlyVisited(webURL: String, newTitle: String) {
        localDatabase.linksDao().renameALinkTitleFromRecentlyVisited(webURL, newTitle)
    }

    override suspend fun renameALinkInfoFromRecentlyVisitedLinks(linkID: Long, newInfo: String) {
        localDatabase.linksDao().renameALinkInfoFromRecentlyVisitedLinks(linkID, newInfo)
    }

    override suspend fun renameALinkInfoFromRecentlyVisitedLinks(webURL: String, newInfo: String) {
        localDatabase.linksDao().renameALinkInfoFromRecentlyVisitedLinks(webURL, newInfo)
    }

    override suspend fun updateImpLinkTitle(id: Long, newTitle: String) {
        localDatabase.linksDao().renameALinkTitleFromImpLinks(id, newTitle)
    }

    override suspend fun updateImpLinkNote(id: Long, newInfo: String) {
        localDatabase.linksDao().renameALinkInfoFromImpLinks(id, newInfo)
    }

    override suspend fun updateLinkInfoFromLinksTable(linkID: Long, newInfo: String) {
        localDatabase.linksDao().renameALinkInfoFromLinksTable(linkID, newInfo)
    }

    override suspend fun renameALinkTitleFromArchiveLinks(linkID: Long, newTitle: String) {
        localDatabase.linksDao().renameALinkTitleFromArchiveLinks(linkID, newTitle)
    }

    override suspend fun renameALinkTitleFromArchiveLinks(webURL: String, newTitle: String) {
        localDatabase.linksDao().renameALinkTitleFromArchiveLinks(webURL, newTitle)
    }

    override suspend fun renameALinkInfoFromSavedLinks(webURL: String, newInfo: String) {
        localDatabase.linksDao().renameALinkInfoFromSavedLinks(webURL, newInfo)
    }

    override suspend fun renameALinkInfoFromArchiveLinks(linkID: Long, newInfo: String) {
        localDatabase.linksDao().renameALinkInfoFromArchiveLinks(linkID, newInfo)
    }

    override suspend fun renameALinkInfoFromArchiveLinks(webURL: String, newInfo: String) {
        localDatabase.linksDao().renameALinkInfoFromArchiveLinks(webURL, newInfo)
    }

    override suspend fun renameALinkInfoFromArchiveBasedFolderLinksV10(
        webURL: String,
        newInfo: String,
        folderID: Long
    ) {
        localDatabase.linksDao()
            .renameALinkInfoFromArchiveBasedFolderLinksV10(webURL, newInfo, folderID)
    }

    override suspend fun renameALinkInfoFromArchiveBasedFolderLinksV9(
        webURL: String,
        newInfo: String,
        folderName: String
    ) {
        localDatabase.linksDao()
            .renameALinkInfoFromArchiveBasedFolderLinksV9(webURL, newInfo, folderName)
    }

    override suspend fun renameALinkTitleFromArchiveBasedFolderLinksV10(
        webURL: String,
        newTitle: String,
        folderID: Long
    ) {
        localDatabase.linksDao()
            .renameALinkTitleFromArchiveBasedFolderLinksV10(webURL, newTitle, folderID)
    }

    override suspend fun renameALinkTitleFromArchiveBasedFolderLinksV9(
        webURL: String,
        newTitle: String,
        folderName: String
    ) {
        localDatabase.linksDao()
            .renameALinkTitleFromArchiveBasedFolderLinksV9(webURL, newTitle, folderName)
    }

    override suspend fun renameFolderNameForExistingArchivedFolderDataV9(
        currentFolderName: String,
        newFolderName: String
    ) {
        localDatabase.linksDao()
            .renameFolderNameForExistingArchivedFolderDataV9(currentFolderName, newFolderName)
    }

    override suspend fun renameFolderNameForExistingFolderDataV9(
        currentFolderName: String,
        newFolderName: String
    ) {
        localDatabase.linksDao()
            .renameFolderNameForExistingFolderDataV9(currentFolderName, newFolderName)
    }

    override suspend fun moveFolderLinksDataToArchiveV9(folderName: String) {
        localDatabase.linksDao().moveFolderLinksDataToArchiveV9(folderName)
    }

    override suspend fun moveArchiveFolderBackToRootFolderV10(folderID: Long) {
        localDatabase.linksDao().moveArchiveFolderBackToRootFolderV10(folderID)
    }

    override suspend fun moveArchiveFolderBackToRootFolderV9(folderName: String) {
        localDatabase.linksDao().moveArchiveFolderBackToRootFolderV9(folderName)
    }

    override suspend fun renameALinkTitleFromSavedLinks(webURL: String, newTitle: String) {
        localDatabase.linksDao().renameALinkTitleFromSavedLinks(webURL, newTitle)
    }

    override suspend fun updateLinkTitleFromLinksTable(linkID: Long, newTitle: String) {
        localDatabase.linksDao().updateLinkInfoFromLinksTable(linkID, newTitle)
    }

    override suspend fun renameALinkTitleFromFoldersV9(
        webURL: String,
        newTitle: String,
        folderName: String
    ) {
        localDatabase.linksDao().renameALinkTitleFromFoldersV9(webURL, newTitle, folderName)
    }

    override suspend fun addANewLinkInRecentlyVisited(recentlyVisited: RecentlyVisited) {
        localDatabase.linksDao().addANewLinkInRecentlyVisited(recentlyVisited)
    }


    override suspend fun changeUserAgentInLinksTable(newUserAgent: String?, domain: String) {
        localDatabase.linksDao().changeUserAgentInLinksTable(newUserAgent, domain)
    }

    override suspend fun changeUserAgentInArchiveLinksTable(newUserAgent: String?, domain: String) {
        localDatabase.linksDao().changeUserAgentInArchiveLinksTable(newUserAgent, domain)
    }

    override suspend fun changeUserAgentInImportantLinksTable(
        newUserAgent: String?,
        domain: String
    ) {
        localDatabase.linksDao().changeUserAgentInImportantLinksTable(newUserAgent, domain)
    }

    override suspend fun changeUserAgentInHistoryTable(newUserAgent: String?, domain: String) {
        localDatabase.linksDao().changeUserAgentInHistoryTable(newUserAgent, domain)
    }
}