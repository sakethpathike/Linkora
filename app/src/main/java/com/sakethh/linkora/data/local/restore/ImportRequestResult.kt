package com.sakethh.linkora.data.local.restore

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.sakethh.linkora.utils.linkoraLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class ImportRequestResult {
    data object Success : ImportRequestResult()
    data class Failure(val exceptionType: ImportFailedException) : ImportRequestResult()

    companion object {
        private val _state = MutableStateFlow(ImportRequestState.IDLE)
        val state = _state.asStateFlow()

        val isHTMLBasedRequest = mutableStateOf(false)

        val totalLinksFromLinksTable = mutableIntStateOf(0)
        val totalLinksFromArchivedLinksTable = mutableIntStateOf(0)
        val totalLinksFromImpLinksTable = mutableIntStateOf(0)
        val totalLinksFromHistoryLinksTable = mutableIntStateOf(0)
        val totalRegularFolders = mutableIntStateOf(0)
        val totalArchivedFolders = mutableIntStateOf(0)
        val totalPanels = mutableIntStateOf(0)
        val totalPanelFolders = mutableIntStateOf(0)

        val currentIterationOfLinksFromLinksTable = mutableIntStateOf(0)
        val currentIterationOfLinksFromArchivedLinksTable = mutableIntStateOf(0)
        val currentIterationOfLinksFromImpLinksTable = mutableIntStateOf(0)
        val currentIterationOfLinksFromHistoryLinksTable = mutableIntStateOf(0)
        val currentIterationOfPanels = mutableIntStateOf(0)
        val currentIterationOfPanelFolders = mutableIntStateOf(0)
        val currentIterationOfRegularFolders = mutableIntStateOf(0)
        val currentIterationOfArchivedFolders = mutableIntStateOf(0)

        suspend fun updateState(importRequestState: ImportRequestState) {
            linkoraLog(importRequestState.name)
            _state.emit(importRequestState)
        }

        fun resetImportInfo() {
            totalLinksFromLinksTable.intValue = 0
            totalLinksFromArchivedLinksTable.intValue = 0
            totalLinksFromImpLinksTable.intValue = 0
            totalLinksFromHistoryLinksTable.intValue = 0
            totalRegularFolders.intValue = 0
            totalArchivedFolders.intValue = 0
            currentIterationOfLinksFromLinksTable.intValue = 0
            currentIterationOfLinksFromArchivedLinksTable.intValue = 0
            currentIterationOfLinksFromImpLinksTable.intValue = 0
            currentIterationOfLinksFromHistoryLinksTable.intValue = 0
            currentIterationOfRegularFolders.intValue = 0
            currentIterationOfArchivedFolders.intValue = 0
        }
    }

}

enum class ImportRequestState {
    IDLE, PARSING, MODIFYING, ADDING_TO_DATABASE
}

sealed interface ImportFailedException {
    data object InvalidFile : ImportFailedException
    data object NotBasedOnLinkoraSchema : ImportFailedException
}