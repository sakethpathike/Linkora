package com.sakethh.linkora.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface RequestResult<out T> {
    data class Success<out T>(val data: T) : RequestResult<T>
    data class Failure<T>(val msg: String) : RequestResult<T>

    companion object {
        private val _currentState = MutableStateFlow(RequestState.IDLE)
        val currentState = _currentState.asStateFlow()
        suspend fun updateState(state: RequestState) {
            _currentState.emit(state)
        }
    }
}