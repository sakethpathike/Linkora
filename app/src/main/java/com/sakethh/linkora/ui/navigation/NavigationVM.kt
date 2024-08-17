package com.sakethh.linkora.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.sakethh.linkora.LocalizedStrings


data class BtmNavigationItem(
    val selectedIcon: ImageVector,
    val nonSelectedIcon: ImageVector,
    val navigationRoute: NavigationRoutes,
    val itemName: MutableState<String>,
)

class NavigationVM : ViewModel() {


    val btmBarList = listOf(
        BtmNavigationItem(
            itemName = LocalizedStrings.search,
            selectedIcon = Icons.Filled.Search,
            nonSelectedIcon = Icons.Outlined.Search,
            navigationRoute = NavigationRoutes.SEARCH_SCREEN
        ),
        BtmNavigationItem(
            itemName = LocalizedStrings.collections,
            selectedIcon = Icons.Filled.Folder,
            nonSelectedIcon = Icons.Outlined.Folder,
            navigationRoute = NavigationRoutes.COLLECTIONS_SCREEN
        ),
        BtmNavigationItem(
            itemName = LocalizedStrings.settings,
            selectedIcon = Icons.Filled.Settings,
            nonSelectedIcon = Icons.Outlined.Settings,
            navigationRoute = NavigationRoutes.SETTINGS_SCREEN
        ),
    )


    companion object {
        val startDestination =
            mutableStateOf(NavigationRoutes.HOME_SCREEN.name)
    }

}