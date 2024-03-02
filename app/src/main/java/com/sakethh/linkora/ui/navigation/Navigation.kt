package com.sakethh.linkora.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sakethh.linkora.ui.screens.collections.CollectionsScreen
import com.sakethh.linkora.ui.screens.collections.archiveScreen.ParentArchiveScreen
import com.sakethh.linkora.ui.screens.collections.specificCollectionScreen.SpecificCollectionScreen
import com.sakethh.linkora.ui.screens.home.ParentHomeScreen
import com.sakethh.linkora.ui.screens.search.SearchScreen
import com.sakethh.linkora.ui.screens.settings.SettingsScreen
import com.sakethh.linkora.ui.screens.settings.SpecificSettingSectionScreen

@Composable
fun MainNavigation(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = NavigationVM.startDestination.value
    ) {
        composable(route = NavigationRoutes.HOME_SCREEN.name) {
            ParentHomeScreen(navController = navController)
        }
        composable(route = NavigationRoutes.COLLECTIONS_SCREEN.name) {
            CollectionsScreen(navController = navController)
        }
        composable(route = NavigationRoutes.SETTINGS_SCREEN.name) {
            SettingsScreen(navController = navController)
        }
        composable(route = NavigationRoutes.SPECIFIC_SCREEN.name) {
            SpecificCollectionScreen(navController = navController)
        }
        composable(route = NavigationRoutes.ARCHIVE_SCREEN.name) {
            ParentArchiveScreen(navController = navController)
        }
        composable(route = NavigationRoutes.SEARCH_SCREEN.name) {
            SearchScreen(navController = navController)
        }
        composable(route = NavigationRoutes.SPECIFIC_SETTINGS_SECTION_SCREEN.name) {
            SpecificSettingSectionScreen(navController = navController)
        }
    }

}