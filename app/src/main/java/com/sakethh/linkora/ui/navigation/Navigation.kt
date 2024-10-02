package com.sakethh.linkora.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sakethh.linkora.ui.CustomWebTab
import com.sakethh.linkora.ui.screens.collections.CollectionsScreen
import com.sakethh.linkora.ui.screens.collections.allLinks.AllLinksScreen
import com.sakethh.linkora.ui.screens.collections.archive.ParentArchiveScreen
import com.sakethh.linkora.ui.screens.collections.specific.SpecificCollectionScreen
import com.sakethh.linkora.ui.screens.home.ParentHomeScreen
import com.sakethh.linkora.ui.screens.linkLayout.LinkLayoutSettings
import com.sakethh.linkora.ui.screens.search.SearchScreen
import com.sakethh.linkora.ui.screens.settings.SettingsScreen
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.screens.settings.specific.AboutSettingsScreen
import com.sakethh.linkora.ui.screens.settings.specific.AcknowledgmentsSettingsScreen
import com.sakethh.linkora.ui.screens.settings.specific.AdvancedSettingsScreen
import com.sakethh.linkora.ui.screens.settings.specific.DataSettingsScreen
import com.sakethh.linkora.ui.screens.settings.specific.GeneralSettingsScreen
import com.sakethh.linkora.ui.screens.settings.specific.PrivacySettingsScreen
import com.sakethh.linkora.ui.screens.settings.specific.ThemeSettingsScreen
import com.sakethh.linkora.ui.screens.settings.specific.language.LanguageSettingsScreen
import com.sakethh.linkora.ui.screens.shelf.ShelfPanelsScreen
import com.sakethh.linkora.ui.screens.shelf.SpecificPanelScreen

@Composable
fun MainNavigation(
    navController: NavHostController,
    customWebTab: CustomWebTab,
    settingsScreenVM: SettingsScreenVM
) {

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
        composable(route = NavigationRoutes.ABOUT_SETTINGS_SCREEN.name) {
            AboutSettingsScreen(
                navController = navController,
                settingsScreenVM = settingsScreenVM,
                customWebTab = customWebTab
            )
        }
        composable(route = NavigationRoutes.ACKNOWLEDGMENTS_SETTINGS_SCREEN.name) {
            AcknowledgmentsSettingsScreen(
                navController = navController,
                settingsScreenVM = settingsScreenVM,
            )
        }
        composable(route = NavigationRoutes.DATA_SETTINGS_SCREEN.name) {
            DataSettingsScreen(
                navController = navController,
                settingsScreenVM = settingsScreenVM,
            )
        }
        composable(route = NavigationRoutes.GENERAL_SETTINGS_SCREEN.name) {
            GeneralSettingsScreen(
                navController = navController,
                settingsScreenVM = settingsScreenVM
            )
        }
        composable(route = NavigationRoutes.PRIVACY_SETTINGS_SCREEN.name) {
            PrivacySettingsScreen(
                navController = navController,
                settingsScreenVM = settingsScreenVM,
            )
        }
        composable(route = NavigationRoutes.THEME_SETTINGS_SCREEN.name) {
            ThemeSettingsScreen(
                navController = navController,
                settingsScreenVM = settingsScreenVM,
            )
        }
        composable(route = NavigationRoutes.LANGUAGE_SETTINGS_SCREEN.name) {
            LanguageSettingsScreen(
                navController = navController,
                customWebTab = customWebTab
            )
        }
        composable(route = NavigationRoutes.SHELF_SCREEN.name) {
            ShelfPanelsScreen(navController)
        }
        composable(route = NavigationRoutes.SPECIFIC_PANEL_SCREEN.name) {
            SpecificPanelScreen(navController)
        }
        composable(route = NavigationRoutes.LINK_LAYOUT_SETTINGS.name) {
            LinkLayoutSettings(navController)
        }
        composable(route = NavigationRoutes.ALL_LINKS_SCREEN.name) {
            AllLinksScreen(navController)
        }
        composable(route = NavigationRoutes.ADVANCED_SETTINGS_SCREEN.name) {
            AdvancedSettingsScreen(navController)
        }
    }

}