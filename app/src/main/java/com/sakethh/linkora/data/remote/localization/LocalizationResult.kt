package com.sakethh.linkora.data.remote.localization

import com.sakethh.linkora.data.local.localization.language.translations.Translation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class LocalizationResult {
    data class Success(val data: List<Translation>) : LocalizationResult()
    data class Failure(val message: String) : LocalizationResult()

    companion object {
        private val _currentState = MutableStateFlow(RequestState.IDLE)
        val currentState = _currentState.asStateFlow()
        suspend fun updateState(state: RequestState) {
            _currentState.emit(state)
        }
    }
}