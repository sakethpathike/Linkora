package com.sakethh.linkora.ui.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.vector.ImageVector

data class BtmNavigationItem<T : Any>(
    val selectedIcon: ImageVector,
    val nonSelectedIcon: ImageVector,
    val navigationRoute: T,
    val itemName: MutableState<String>
)
