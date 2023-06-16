package com.sakethh.linkora.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel


data class BtmNavigationItem(
    val selectedIcon: ImageVector,
    val nonSelectedIcon: ImageVector,
    val navigationRoute: NavigationRoutes,
)

class NavigationVM : ViewModel() {

    val btmBarList = listOf(
        BtmNavigationItem(
            selectedIcon = Icons.Filled.Home,
            nonSelectedIcon = Icons.Outlined.Home,
            navigationRoute = NavigationRoutes.HOME_SCREEN
        ),
        BtmNavigationItem(
            selectedIcon = Icons.Filled.CollectionsBookmark,
            nonSelectedIcon = Icons.Outlined.CollectionsBookmark,
            navigationRoute = NavigationRoutes.COLLECTIONS_SCREEN
        ),
        BtmNavigationItem(
            selectedIcon = Icons.Filled.Settings,
            nonSelectedIcon = Icons.Outlined.Settings,
            navigationRoute = NavigationRoutes.SETTINGS_SCREEN
        ),
    )

    companion object {
        val btmNavBarContainerColor = mutableStateOf(Color(Color.Transparent.value))
    }
}