package com.sakethh.linkora.ui.screens.collections.allLinks

import androidx.compose.runtime.MutableState

data class LinkTypeSelection(
    val linkType: String,
    val isChecked: MutableState<Boolean>
)