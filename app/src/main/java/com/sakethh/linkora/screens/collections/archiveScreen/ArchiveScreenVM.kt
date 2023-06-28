package com.sakethh.linkora.screens.collections.archiveScreen

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.sakethh.linkora.localDB.ArchivedFolders
import com.sakethh.linkora.localDB.ArchivedLinks
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ArchiveScreenModal(val name: String, val screen: @Composable () -> Unit)

enum class ArchiveScreenType {
    LINKS, FOLDERS
}

class ArchiveScreenVM : ViewModel() {
    val parentArchiveScreenData = listOf(
        ArchiveScreenModal(name = "Links", screen = {
            ChildArchiveScreen(
                archiveScreenType = ArchiveScreenType.LINKS
            )
        }), ArchiveScreenModal(name = "Folders",
            screen = { ChildArchiveScreen(archiveScreenType = ArchiveScreenType.FOLDERS) })
    )
    private val _archiveLinksData = MutableStateFlow(emptyList<ArchivedLinks>())
    val archiveLinksData = _archiveLinksData.asStateFlow()

    private val _archiveFoldersData = MutableStateFlow(emptyList<ArchivedFolders>())
    val archiveFoldersData = _archiveLinksData.asStateFlow()

    suspend fun getAllArchiveLinks() {
        CustomLocalDBDaoFunctionsDecl.localDB.localDBData().getAllArchiveLinks().collect {
            _archiveLinksData.emit(it)
        }
    }

    suspend fun getAllArchiveFolders() {
        CustomLocalDBDaoFunctionsDecl.localDB.localDBData().getAllArchiveFolders().collect {
            _archiveFoldersData.emit(it)
        }
    }
}