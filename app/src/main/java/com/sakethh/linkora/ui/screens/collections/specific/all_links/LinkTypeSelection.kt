package com.sakethh.linkora.ui.screens.collections.specific.all_links

import androidx.compose.runtime.MutableState

data class LinkTypeSelection(
    val linkType: String,
    val isChecked: MutableState<Boolean>
)