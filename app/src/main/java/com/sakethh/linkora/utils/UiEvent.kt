package com.sakethh.linkora.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

object UiEventManager {
    private val _eventChannel = Channel<UiEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()
    fun CoroutineScope.pushUIEvent(uiEvent: UiEvent) {
        this.launch {
            _eventChannel.send(uiEvent)
        }
    }
}

sealed class UiEvent {
    data class ShowSnackBar(val msg: String) : UiEvent()
}