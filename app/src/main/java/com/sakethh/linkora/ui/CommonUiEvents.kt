package com.sakethh.linkora.ui

sealed class CommonUiEvents {
    data class ShowToast(val msg: String) : CommonUiEvents()
}