package com.sakethh.linkora.data.local.links

import android.content.Context
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.LinksTable
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.ui.CommonUiEvent
import kotlinx.coroutines.flow.Flow

interface LinksRepo {

    suspend fun addANewLinkToSavedLinks(
        linksTable: LinksTable,
        onTaskCompleted: () -> Unit,
        autoDetectTitle: Boolean,
    ): CommonUiEvent

    suspend fun addANewLinkInAFolder(
        linksTable: LinksTable,
        onTaskCompleted: () -> Unit,
        autoDetectTitle: Boolean
    ): CommonUiEvent

    suspend fun duplicateFolderBasedLinks(currentIdOfLinkedFolder: Long, newIdOfLinkedFolder: Long)

    suspend fun markThisLinkFromLinksTableAsFolderLink(linkID: Long, targetFolderId: Long)

    suspend fun markThisLinkFromLinksTableAsSavedLink(linkID: Long)

    suspend fun addListOfDataInLinksTable(list: List<LinksTable>)
    suspend fun addALinkInLinksTable(linksTable: LinksTable)

    suspend fun deleteANoteFromArchiveLinks(linkID: Long)
    suspend fun deleteANoteFromArchiveLinks(webURL: String)

    suspend fun deleteALinkNoteFromArchiveBasedFolderLinksV10(
        webURL: String,
        folderID: Long,
    )

    suspend fun deleteALinkNoteFromArchiveBasedFolderLinksV9(
        webURL: String,
        folderName: String,
    )

    suspend fun deleteANoteFromImportantLinks(webURL: String)
    suspend fun deleteANoteFromImportantLinks(linkID: Long)

    suspend fun deleteANoteFromLinksTable(linkID: Long)

    suspend fun deleteANoteFromRecentlyVisited(webURL: String)
    suspend fun deleteANoteFromRecentlyVisited(linkID: Long)


    suspend fun deleteALinkInfoFromSavedLinks(webURL: String)

    suspend fun deleteALinkInfoOfFolders(linkID: Long)

    suspend fun deleteALinkFromSavedLinksBasedOnURL(webURL: String)


    suspend fun deleteALinkFromImpLinks(linkID: Long)

    suspend fun deleteALinkFromImpLinksBasedOnURL(webURL: String)

    suspend fun deleteALinkFromArchiveLinksV9(webURL: String)

    suspend fun deleteALinkFromArchiveLinks(id: Long)
    suspend fun archiveLinkTableUpdater(
        archivedLinks: ArchivedLinks, context: Context,
        onTaskCompleted: () -> Unit,
    ): CommonUiEvent

    suspend fun deleteALinkFromArchiveFolderBasedLinksV10(webURL: String, archiveFolderID: Long)

    suspend fun deleteALinkFromArchiveFolderBasedLinksV9(webURL: String, folderName: String)

    suspend fun deleteALinkFromLinksTable(linkID: Long)


    suspend fun deleteALinkFromSpecificFolderV10(webURL: String, folderID: Long)

    suspend fun deleteALinkFromSpecificFolderV9(webURL: String, folderName: String)

    suspend fun deleteARecentlyVisitedLink(webURL: String)

    suspend fun deleteARecentlyVisitedLink(linkID: Long)

    suspend fun deleteThisFolderLinksV10(folderID: Long)

    suspend fun deleteMultipleLinksFromLinksTable(links: Array<Long>)

    suspend fun deleteThisFolderLinksV9(folderName: String)

    suspend fun deleteThisArchiveFolderDataV9(folderID: String)

    suspend fun reloadArchiveLink(linkID: Long)

    suspend fun reloadLinksTableLink(linkID: Long)

    suspend fun reloadImpLinksTableLink(linkID: Long)
    suspend fun getThisLinkFromLinksTable(linkID: Long): LinksTable
    suspend fun getThisLinkFromImpLinksTable(linkID: Long): ImportantLinks
    suspend fun getThisLinkFromArchiveLinksTable(linkID: Long): ArchivedLinks
    suspend fun getThisLinkFromRecentlyVisitedLinksTable(linkID: Long): RecentlyVisited
    suspend fun reloadHistoryLinksTableLink(linkID: Long)

    suspend fun addANewLinkToImpLinks(
        importantLink: ImportantLinks,
        onTaskCompleted: () -> Unit,
        autoDetectTitle: Boolean
    ): CommonUiEvent

    suspend fun addANewLinkToImpLinks(
        importantLink: ImportantLinks
    ): CommonUiEvent


    suspend fun addANewLinkToArchiveLink(archivedLinks: ArchivedLinks)

    fun getAllSavedLinks(): Flow<List<LinksTable>>

    suspend fun getAllSavedLinksAsList(): List<LinksTable>

    suspend fun getAllFromLinksTable(): List<LinksTable>

    suspend fun getAllRecentlyVisitedLinks(): List<RecentlyVisited>

    suspend fun getAllImpLinks(): List<ImportantLinks>

    suspend fun getAllArchiveLinks(): List<ArchivedLinks>


    fun getAllArchiveFoldersLinks(): Flow<List<LinksTable>>


    fun getLinksOfThisFolderV10(folderID: Long): Flow<List<LinksTable>>

    suspend fun getLinksOfThisFolderAsList(folderID: Long): List<LinksTable>


    suspend fun changeUserAgentInLinksTable(newUserAgent: String?, domain: String)

    suspend fun changeUserAgentInArchiveLinksTable(newUserAgent: String?, domain: String)

    suspend fun changeUserAgentInImportantLinksTable(newUserAgent: String?, domain: String)

    suspend fun changeUserAgentInHistoryTable(newUserAgent: String?, domain: String)

    fun getLinksOfThisFolderV9(folderName: String): Flow<List<LinksTable>>


    fun getThisArchiveFolderLinksV10(folderID: Long): Flow<List<LinksTable>>

    fun getThisArchiveFolderLinksV9(folderName: String): Flow<List<LinksTable>>

    suspend fun doesThisExistsInImpLinks(webURL: String): Boolean

    suspend fun doesThisExistsInSavedLinks(webURL: String): Boolean

    suspend fun doesThisLinkExistsInAFolderV10(webURL: String, folderID: Long): Boolean


    suspend fun getLastIDOfHistoryTable(): Long

    suspend fun getLastIDOfLinksTable(): Long

    suspend fun getLastIDOfImpLinksTable(): Long

    suspend fun getLastIDOfArchivedLinksTable(): Long

    suspend fun doesThisLinkExistsInAFolderV9(webURL: String, folderName: String): Boolean

    suspend fun doesThisExistsInArchiveLinks(webURL: String): Boolean

    suspend fun doesThisExistsInRecentlyVisitedLinks(webURL: String): Boolean


    suspend fun isLinksTableEmpty(): Boolean


    suspend fun isArchivedLinksTableEmpty(): Boolean

    suspend fun isArchivedFoldersTableEmpty(): Boolean

    suspend fun isFoldersTableEmpty(): Boolean

    suspend fun isImpLinksTableEmpty(): Boolean

    suspend fun isHistoryLinksTableEmpty(): Boolean


    suspend fun copyLinkFromLinksTableToArchiveLinks(id: Long)


    suspend fun copyLinkFromImpLinksTableToArchiveLinks(id: Long)

    suspend fun copyLinkFromImpTableToArchiveLinks(link: String)


    suspend fun updateALinkDataFromLinksTable(linksTable: LinksTable)

    suspend fun updateALinkDataFromImpLinksTable(importantLinks: ImportantLinks)

    suspend fun updateALinkDataFromRecentlyVisitedLinksTable(recentlyVisited: RecentlyVisited)

    suspend fun updateALinkDataFromArchivedLinksTable(archivedLinks: ArchivedLinks)

    suspend fun renameALinkTitleFromRecentlyVisited(linkID: Long, newTitle: String)
    suspend fun renameALinkTitleFromRecentlyVisited(webURL: String, newTitle: String)

    suspend fun renameALinkInfoFromRecentlyVisitedLinks(linkID: Long, newInfo: String)
    suspend fun renameALinkInfoFromRecentlyVisitedLinks(webURL: String, newInfo: String)

    suspend fun updateImpLinkTitle(id: Long, newTitle: String)

    suspend fun updateImpLinkNote(id: Long, newInfo: String)

    suspend fun updateLinkInfoFromLinksTable(linkID: Long, newInfo: String)

    suspend fun renameALinkTitleFromArchiveLinks(linkID: Long, newTitle: String)
    suspend fun renameALinkTitleFromArchiveLinks(webURL: String, newTitle: String)

    suspend fun renameALinkInfoFromSavedLinks(webURL: String, newInfo: String)

    suspend fun renameALinkInfoFromArchiveLinks(linkID: Long, newInfo: String)
    suspend fun renameALinkInfoFromArchiveLinks(webURL: String, newInfo: String)

    suspend fun renameALinkInfoFromArchiveBasedFolderLinksV10(
        webURL: String,
        newInfo: String,
        folderID: Long,
    )

    suspend fun renameALinkInfoFromArchiveBasedFolderLinksV9(
        webURL: String,
        newInfo: String,
        folderName: String,
    )

    suspend fun renameALinkTitleFromArchiveBasedFolderLinksV10(
        webURL: String,
        newTitle: String,
        folderID: Long,
    )

    suspend fun renameALinkTitleFromArchiveBasedFolderLinksV9(
        webURL: String,
        newTitle: String,
        folderName: String,
    )


    suspend fun renameFolderNameForExistingArchivedFolderDataV9(
        currentFolderName: String, newFolderName: String
    )

    suspend fun renameFolderNameForExistingFolderDataV9(
        currentFolderName: String, newFolderName: String
    )

    suspend fun moveFolderLinksDataToArchiveV9(
        folderName: String,
    )


    suspend fun moveArchiveFolderBackToRootFolderV10(
        folderID: Long,
    )

    suspend fun moveArchiveFolderBackToRootFolderV9(
        folderName: String,
    )

    suspend fun renameALinkTitleFromSavedLinks(webURL: String, newTitle: String)

    suspend fun updateLinkTitleFromLinksTable(linkID: Long, newTitle: String)

    suspend fun renameALinkTitleFromFoldersV9(webURL: String, newTitle: String, folderName: String)


    suspend fun addANewLinkInRecentlyVisited(recentlyVisited: RecentlyVisited)
}