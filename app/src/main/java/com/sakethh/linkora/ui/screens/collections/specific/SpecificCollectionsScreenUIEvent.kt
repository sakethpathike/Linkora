package com.sakethh.linkora.ui.screens.collections.specific

import com.sakethh.linkora.data.local.FoldersTable

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
        val parentFolderID: Long,
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
}