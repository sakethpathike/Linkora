package com.sakethh.linkora.ui.screens.collections.allLinks

import androidx.compose.runtime.MutableState
import com.sakethh.linkora.data.local.links.LinkType

data class LinkTypeSelection(
    val linkType: LinkType,
    val isChecked: MutableState<Boolean>
)