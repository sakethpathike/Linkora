package com.sakethh.linkora.screens.collections.specificScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.localDB.ImportantLinks
import com.sakethh.linkora.localDB.LinksTable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SpecificScreenVM : ViewModel() {
    private val _foldersData = MutableStateFlow(emptyList<LinksTable>())
    val foldersData = _foldersData.asStateFlow()

    private val _linksData = MutableStateFlow(emptyList<LinksTable>())
    val linksTable = _linksData.asStateFlow()

    private val _impLinksData = MutableStateFlow(emptyList<ImportantLinks>())
    val impLinksTable = _impLinksData.asStateFlow()

    val impLinkDataForBtmSheet = ImportantLinks("", "", "", "", "")

    companion object {
        var currentClickedFolderName = ""
        val screenType = mutableStateOf(SpecificScreenType.SPECIFIC_FOLDER_SCREEN)
    }

    init {
        when (screenType.value) {
            SpecificScreenType.SPECIFIC_FOLDER_SCREEN -> {
                viewModelScope.launch {
                    CustomLocalDBDaoFunctionsDecl.localDB.localDBData().getThisFolderData(
                        currentClickedFolderName
                    ).collect {
                        _foldersData.emit(it)
                    }
                }
            }

            SpecificScreenType.IMPORTANT_LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomLocalDBDaoFunctionsDecl.localDB.localDBData().getAllImpLinks().collect {
                        _impLinksData.emit(it)
                    }
                }
            }

            SpecificScreenType.ARCHIVE_SCREEN -> {

            }

            SpecificScreenType.LINKS_SCREEN -> {
                viewModelScope.launch {
                    CustomLocalDBDaoFunctionsDecl.localDB.localDBData().getAllSavedLinks().collect {
                        _linksData.emit(it)
                    }
                }
            }
        }
    }
}

enum class SpecificScreenType {
    IMPORTANT_LINKS_SCREEN, ARCHIVE_SCREEN, LINKS_SCREEN, SPECIFIC_FOLDER_SCREEN
}