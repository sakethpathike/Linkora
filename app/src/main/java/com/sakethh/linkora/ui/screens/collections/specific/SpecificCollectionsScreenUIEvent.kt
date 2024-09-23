package com.sakethh.linkora.ui.screens.collections.specific

import android.content.Context
import com.sakethh.linkora.data.local.ArchivedLinks
import com.sakethh.linkora.data.local.FoldersTable
import com.sakethh.linkora.data.local.ImportantLinks
import com.sakethh.linkora.data.local.links.LinkType

sealed class SpecificCollectionsScreenUIEvent {
    data class ArchiveAFolder(val folderId: Long) : SpecificCollectionsScreenUIEvent()
    data class UpdateFolderNote(val folderId: Long, val newFolderNote: String) :
        SpecificCollectionsScreenUIEvent()

    data class UpdateImpLinkNote(val linkId: Long, val newNote: String) :
        SpecificCollectionsScreenUIEvent()

    data class UpdateRegularLinkNote(val linkId: Long, val newNote: String) :
        SpecificCollectionsScreenUIEvent()

    data class UpdateFolderName(val folderName: String, val folderId: Long) :
        SpecificCollectionsScreenUIEvent()

    data class UpdateImpLinkTitle(val title: String, val linkId: Long) :
        SpecificCollectionsScreenUIEvent()

    data class UpdateRegularLinkTitle(val title: String, val linkId: Long) :
        SpecificCollectionsScreenUIEvent()

    data class AddANewLinkInAFolder(
        val title: String,
        val webURL: String,
        val noteForSaving: String,
        val folderID: Long,
        val autoDetectTitle: Boolean,
        val onTaskCompleted: () -> Unit,
        val folderName: String
    ) :
        SpecificCollectionsScreenUIEvent()

    data class AddANewLinkInSavedLinks(
        val title: String,
        val webURL: String,
        val noteForSaving: String,
        val autoDetectTitle: Boolean,
        val onTaskCompleted: () -> Unit,
    ) :
        SpecificCollectionsScreenUIEvent()

    data class AddANewLinkInImpLinks(
        val title: String,
        val webURL: String,
        val noteForSaving: String,
        val autoDetectTitle: Boolean,
        val onTaskCompleted: () -> Unit,
    ) :
        SpecificCollectionsScreenUIEvent()

    data class CreateANewFolder(val foldersTable: FoldersTable) : SpecificCollectionsScreenUIEvent()

    data class DeleteAFolder(val folderId: Long) : SpecificCollectionsScreenUIEvent()

    data class AddExistingLinkToImportantLink(val importantLinks: ImportantLinks) :
        SpecificCollectionsScreenUIEvent()

    data class DeleteAnExistingLinkFromImportantLinks(val webUrl: String) :
        SpecificCollectionsScreenUIEvent()

    data class OnLinkRefresh(val linkId: Long, val linkType: LinkType) :
        SpecificCollectionsScreenUIEvent()

    data class ArchiveAnExistingLink(
        val archivedLink: ArchivedLinks,
        val context: Context,
        val linkType: LinkType
    ) :
        SpecificCollectionsScreenUIEvent()

    data class UnArchiveAnExistingLink(
        val archivedLink: ArchivedLinks
    ) :
        SpecificCollectionsScreenUIEvent()

    data class DeleteAnExistingNote(val linkId: Long, val linkType: LinkType) :
        SpecificCollectionsScreenUIEvent()

    data class DeleteAnExistingLink(val linkId: Long, val linkType: LinkType) :
        SpecificCollectionsScreenUIEvent()

    data class UpdateLinkTitle(val linkId: Long, val newTitle: String, val linkType: LinkType) :
        SpecificCollectionsScreenUIEvent()

    data class UpdateLinkNote(val linkId: Long, val newNote: String, val linkType: LinkType) :
        SpecificCollectionsScreenUIEvent()
}