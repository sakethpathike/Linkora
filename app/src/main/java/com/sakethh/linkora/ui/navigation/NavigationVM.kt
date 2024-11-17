package com.sakethh.linkora.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.lifecycle.ViewModel
import com.sakethh.linkora.LocalizedStrings

class NavigationVM : ViewModel() {

    val btmBarList = listOf(
        BtmNavigationItem(
            itemName = LocalizedStrings.search,
            selectedIcon = Icons.Filled.Search,
            nonSelectedIcon = Icons.Outlined.Search,
            navigationRoute = SearchScreenRoute
        ),
        BtmNavigationItem(
            itemName = LocalizedStrings.collections,
            selectedIcon = Icons.Filled.Folder,
            nonSelectedIcon = Icons.Outlined.Folder,
            navigationRoute = CollectionsScreenRoute
        ),
        BtmNavigationItem(
            itemName = LocalizedStrings.settings,
            selectedIcon = Icons.Filled.Settings,
            nonSelectedIcon = Icons.Outlined.Settings,
            navigationRoute = SettingsScreenRoute
        ),
    )

}