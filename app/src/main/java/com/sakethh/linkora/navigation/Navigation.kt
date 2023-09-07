package com.sakethh.linkora.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sakethh.linkora.screens.collections.CollectionsScreen
import com.sakethh.linkora.screens.collections.archiveScreen.ParentArchiveScreen
import com.sakethh.linkora.screens.collections.specificCollectionScreen.SpecificScreen
import com.sakethh.linkora.screens.home.HomeScreen
import com.sakethh.linkora.screens.settings.SettingsScreen

@Composable
fun MainNavigation(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = NavigationVM.startDestination.value
    ) {
        composable(route = NavigationRoutes.HOME_SCREEN.name) {
            HomeScreen()
        }
        composable(route = NavigationRoutes.COLLECTIONS_SCREEN.name) {
            CollectionsScreen(navController = navController)
        }
        composable(route = NavigationRoutes.SETTINGS_SCREEN.name) {
            SettingsScreen(navController = navController)
        }
        composable(route = NavigationRoutes.SPECIFIC_SCREEN.name) {
            SpecificScreen(navController = navController)
        }
        composable(route = NavigationRoutes.ARCHIVE_SCREEN.name) {
            ParentArchiveScreen(navController = navController)
        }
    }

}