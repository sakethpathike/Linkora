package com.sakethh.linkora.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sakethh.linkora.screens.collections.CollectionScreen
import com.sakethh.linkora.screens.collections.archiveScreen.ParentArchiveScreen
import com.sakethh.linkora.screens.collections.specificScreen.SpecificScreen
import com.sakethh.linkora.screens.home.HomeScreen
import com.sakethh.linkora.screens.settings.SettingsScreen
import com.sakethh.linkora.screens.settings.SettingsScreenVM

@Composable
fun MainNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = if (SettingsScreenVM.Settings.isHomeScreenEnabled.value) NavigationRoutes.HOME_SCREEN.name else NavigationRoutes.COLLECTIONS_SCREEN.name
    ) {
        composable(route = NavigationRoutes.HOME_SCREEN.name) {
            HomeScreen()
        }
        composable(route = NavigationRoutes.COLLECTIONS_SCREEN.name) {
            CollectionScreen(navController = navController)
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