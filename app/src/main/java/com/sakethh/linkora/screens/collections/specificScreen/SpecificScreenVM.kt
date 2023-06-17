package com.sakethh.linkora.screens.collections.specificScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakethh.linkora.localDB.FoldersTable
import com.sakethh.linkora.localDB.LinksTable
import com.sakethh.linkora.localDB.LocalDBFunctions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SpecificScreenVM : ViewModel() {
    private val _foldersData = MutableStateFlow(FoldersTable("", "", emptyList()))
    val foldersData = _foldersData.asStateFlow()

    private val _linksData = MutableStateFlow(emptyList<LinksTable>())
    val linksTable = _linksData.asStateFlow()

    companion object {
        var currentClickedIndexNumber = 0
        val screenType = mutableStateOf(SpecificScreenType.SPECIFIC_FOLDER_SCREEN)
    }

    init {
        when (screenType.value) {
            SpecificScreenType.SPECIFIC_FOLDER_SCREEN -> {
                viewModelScope.launch {
                    LocalDBFunctions.getAllFolders().collect {
                        _foldersData.value = it[currentClickedIndexNumber]
                    }
                }
            }

            SpecificScreenType.FAVORITES_SCREEN -> {

            }

            SpecificScreenType.ARCHIVE_SCREEN -> {

            }

            SpecificScreenType.LINKS_SCREEN -> {
                viewModelScope.launch {
                    LocalDBFunctions.getAllLinks().collect {
                        _linksData.value = it
                    }
                }
            }
        }
    }
}

enum class SpecificScreenType {
    FAVORITES_SCREEN, ARCHIVE_SCREEN, LINKS_SCREEN, SPECIFIC_FOLDER_SCREEN
}