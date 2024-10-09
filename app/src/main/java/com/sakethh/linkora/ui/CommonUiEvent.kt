package com.sakethh.linkora.ui

sealed class CommonUiEvent {
    data class ShowToast(val msg: String) : CommonUiEvent()
    data object ShowDeleteDialogBox : CommonUiEvent()
    data object Nothing : CommonUiEvent()
}