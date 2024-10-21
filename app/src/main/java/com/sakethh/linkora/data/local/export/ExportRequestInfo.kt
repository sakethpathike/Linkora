package com.sakethh.linkora.data.local.export

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class ExportRequestInfo {

    companion object {
        private val _state = MutableStateFlow(ExportRequestState.IDLE)
        val state = _state.asStateFlow()

        val isHTMLBasedRequest = mutableStateOf(false)


        val totalLinksFromSavedLinks = mutableIntStateOf(0)
        val totalLinksFromArchivedLinksTable = mutableIntStateOf(0)
        val totalLinksFromImpLinksTable = mutableIntStateOf(0)
        val totalLinksFromHistoryLinksTable = mutableIntStateOf(0)
        val totalRegularFoldersAndItsLinks = mutableIntStateOf(0)
        val totalArchivedFoldersAndItsLinks = mutableIntStateOf(0)

        val currentIterationOfLinksFromSavedLinks = mutableIntStateOf(0)
        val currentIterationOfLinksFromArchivedLinksTable = mutableIntStateOf(0)
        val currentIterationOfLinksFromImpLinksTable = mutableIntStateOf(0)
        val currentIterationOfLinksFromHistoryLinksTable = mutableIntStateOf(0)
        val currentIterationOfRegularFoldersAndItsLinks = mutableIntStateOf(0)
        val currentIterationOfArchivedFoldersAndItsLinks = mutableIntStateOf(0)

        suspend fun updateState(exportRequestState: ExportRequestState) {
            _state.emit(exportRequestState)
        }
    }
}

enum class ExportRequestState {
    GATHERING_DATA, WRITING_TO_THE_FILE, READING_SAVED_LINKS, READING_IMPORTANT_LINKS, READING_HISTORY_LINKS, READING_ARCHIVED_LINKS, READING_REGULAR_FOLDERS, READING_ARCHIVED_FOLDERS, IDLE
}