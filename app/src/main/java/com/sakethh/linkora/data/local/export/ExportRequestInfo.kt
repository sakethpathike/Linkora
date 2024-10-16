package com.sakethh.linkora.data.local.export

import com.sakethh.linkora.utils.linkoraLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class ExportRequestInfo {

    companion object {
        private val _state = MutableStateFlow(ExportRequestState.IDLE)
        val state = _state.asStateFlow()

        suspend fun updateState(exportRequestState: ExportRequestState) {
            linkoraLog("exportRequestState : " + exportRequestState.name)
            _state.emit(exportRequestState)
        }
    }
}

enum class ExportRequestState {
    GATHERING_DATA, WRITING_TO_THE_FILE, IDLE
}