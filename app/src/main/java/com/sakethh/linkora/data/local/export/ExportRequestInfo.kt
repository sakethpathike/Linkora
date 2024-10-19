package com.sakethh.linkora.data.local.export

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class ExportRequestInfo {

    companion object {
        private val _state = MutableStateFlow(ExportRequestState.IDLE)
        val state = _state.asStateFlow()

        suspend fun updateState(exportRequestState: ExportRequestState) {
            _state.emit(exportRequestState)
        }
    }
}

enum class ExportRequestState {
    GATHERING_DATA, WRITING_TO_THE_FILE, IDLE
}