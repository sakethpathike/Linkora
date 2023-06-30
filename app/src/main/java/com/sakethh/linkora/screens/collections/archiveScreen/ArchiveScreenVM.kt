package com.sakethh.linkora.screens.collections.archiveScreen

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.sakethh.linkora.localDB.ArchivedFolders
import com.sakethh.linkora.localDB.ArchivedLinks
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ArchiveScreenModal(
    val name: String,
    val screen: @Composable (navController: NavController) -> Unit,
)

enum class ArchiveScreenType {
    LINKS, FOLDERS
}

class ArchiveScreenVM : ViewModel() {
    val parentArchiveScreenData = listOf(
        ArchiveScreenModal(name = "Links", screen = { navController ->
            ChildArchiveScreen(
                archiveScreenType = ArchiveScreenType.LINKS,
                navController = navController
            )
        }), ArchiveScreenModal(name = "Folders",
            screen = { navController ->
                ChildArchiveScreen(
                    archiveScreenType = ArchiveScreenType.FOLDERS,
                    navController = navController
                )
            })
    )
    private val _archiveLinksData = MutableStateFlow(emptyList<ArchivedLinks>())
    val archiveLinksData = _archiveLinksData.asStateFlow()

    private val _archiveFoldersData = MutableStateFlow(emptyList<ArchivedFolders>())
    val archiveFoldersData = _archiveFoldersData.asStateFlow()

    init {
        viewModelScope.launch {
            awaitAll(async { getAllArchiveLinks() }, async { getAllArchiveFolders() })
        }
    }

    private suspend fun getAllArchiveLinks() {
        CustomLocalDBDaoFunctionsDecl.localDB.localDBData().getAllArchiveLinks().collect {
            _archiveLinksData.emit(it)
        }
    }

    private suspend fun getAllArchiveFolders() {
        CustomLocalDBDaoFunctionsDecl.localDB.localDBData().getAllArchiveFolders().collect {
            _archiveFoldersData.emit(it)
        }
    }
}