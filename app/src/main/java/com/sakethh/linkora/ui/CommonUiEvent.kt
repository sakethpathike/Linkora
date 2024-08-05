package com.sakethh.linkora.ui

sealed class CommonUiEvent {
    data class ShowToast(val msg: Int) : CommonUiEvent()
}