package com.sakethh.linkora.data.local.restore

import com.sakethh.linkora.utils.linkoraLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class ImportRequestResult {
    data object Success : ImportRequestResult()
    data class Failure(val exceptionType: ImportFailedException) : ImportRequestResult()

    companion object {
        private val _state = MutableStateFlow(ImportRequestState.IDLE)
        val state = _state.asStateFlow()

        suspend fun updateState(importRequestState: ImportRequestState) {
            linkoraLog(importRequestState.name)
            _state.emit(importRequestState)
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