package com.sakethh.linkora.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sakethh.linkora.ui.screens.CustomWebTab
import com.sakethh.linkora.ui.screens.collections.CollectionsScreen
import com.sakethh.linkora.ui.screens.collections.archive.ParentArchiveScreen
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionScreen
import com.sakethh.linkora.ui.screens.home.ParentHomeScreen
import com.sakethh.linkora.ui.screens.search.SearchScreen
import com.sakethh.linkora.ui.screens.settings.SettingsScreen
import com.sakethh.linkora.ui.screens.settings.SpecificSettingSectionScreen
import com.sakethh.linkora.ui.screens.shelf.ShelfPanelsScreen
import com.sakethh.linkora.ui.screens.shelf.SpecificPanelScreen

@Composable
fun MainNavigation(navController: NavHostController, customWebTab: CustomWebTab) {

    NavHost(
        navController = navController,
        startDestination = NavigationVM.startDestination.value
    ) {
        composable(route = NavigationRoutes.HOME_SCREEN.name) {
            ParentHomeScreen(navController = navController, customWebTab)
        }
        composable(route = NavigationRoutes.COLLECTIONS_SCREEN.name) {
            CollectionsScreen(navController = navController)
        }
        composable(route = NavigationRoutes.SETTINGS_SCREEN.name) {
            SettingsScreen(navController = navController)
        }
        composable(route = NavigationRoutes.SPECIFIC_COLLECTION_SCREEN.name) {
            SpecificCollectionScreen(navController = navController)
        }
        composable(route = NavigationRoutes.ARCHIVE_SCREEN.name) {
            ParentArchiveScreen(navController = navController, customWebTab)
        }
        composable(route = NavigationRoutes.SEARCH_SCREEN.name) {
            SearchScreen(navController = navController, customWebTab)
        }
        composable(route = NavigationRoutes.SPECIFIC_SETTINGS_SECTION_SCREEN.name) {
            SpecificSettingSectionScreen(navController = navController, customWebTab)
        }
        composable(route = NavigationRoutes.SHELF_SCREEN.name) {
            ShelfPanelsScreen(navController)
        }
        composable(route = NavigationRoutes.SPECIFIC_PANEL_SCREEN.name) {
            SpecificPanelScreen(navController)
        }
    }

}