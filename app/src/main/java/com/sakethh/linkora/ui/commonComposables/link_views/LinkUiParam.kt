package com.sakethh.linkora.ui.commonComposables.link_views

import androidx.compose.runtime.MutableState

data class LinkUIComponentParam(
    val title: String,
    val webBaseURL: String,
    val imgURL: String,
    val userAgent: String,
    val onMoreIconClick: () -> Unit,
    val onLinkClick: () -> Unit,
    val webURL: String,
    val onForceOpenInExternalBrowserClicked: () -> Unit,
    val isSelectionModeEnabled: MutableState<Boolean>,
    val isItemSelected: MutableState<Boolean>,
    val onLongClick: () -> Unit,
)