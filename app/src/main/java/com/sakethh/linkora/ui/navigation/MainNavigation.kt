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
import com.sakethh.linkora.ui.screens.panels.ShelfPanelsScreen
import com.sakethh.linkora.ui.screens.panels.SpecificPanelScreen
import com.sakethh.linkora.ui.screens.search.SearchScreen
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SettingsScreen
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.screens.settings.specific.AboutSettingsScreen
import com.sakethh.linkora.ui.screens.settings.specific.AcknowledgmentsSettingsScreen
import com.sakethh.linkora.ui.screens.settings.specific.GeneralSettingsScreen
import com.sakethh.linkora.ui.screens.settings.specific.PrivacySettingsScreen
import com.sakethh.linkora.ui.screens.settings.specific.ThemeSettingsScreen
import com.sakethh.linkora.ui.screens.settings.specific.advanced.AdvancedSettingsScreen
import com.sakethh.linkora.ui.screens.settings.specific.advanced.site_specific_user_agent.SiteSpecificUserAgentSettingsScreen
import com.sakethh.linkora.ui.screens.settings.specific.data.DataSettingsScreen
import com.sakethh.linkora.ui.screens.settings.specific.language.LanguageSettingsScreen

@Composable
fun MainNavigation(
    navController: NavHostController,
    customWebTab: CustomWebTab,
    settingsScreenVM: SettingsScreenVM
) {

    NavHost(
        navController = navController,
        startDestination = if (SettingsPreference.isHomeScreenEnabled.value.not() && SettingsPreference.startDestination.value == HomeScreenRoute.toString()) {
            CollectionsScreenRoute
        } else when (SettingsPreference.startDestination.value) {
            HomeScreenRoute.toString() -> HomeScreenRoute
            SearchScreenRoute.toString() -> SearchScreenRoute
            else -> CollectionsScreenRoute
        }
    ) {
        composable<HomeScreenRoute> {
            ParentHomeScreen(navController = navController, customWebTab)
        }
        composable<CollectionsScreenRoute> {
            CollectionsScreen(navController = navController)
        }
        composable<SettingsScreenRoute> {
            SettingsScreen(navController = navController)
        }
        composable<SpecificCollectionScreenRoute> {
            SpecificCollectionScreen(navController = navController)
        }
        composable<ArchiveScreenRoute> {
            ParentArchiveScreen(navController = navController, customWebTab)
        }
        composable<SearchScreenRoute> {
            SearchScreen(navController = navController, customWebTab)
        }
        composable<AboutSettingsScreenRoute> {
            AboutSettingsScreen(
                navController = navController,
                settingsScreenVM = settingsScreenVM,
                customWebTab = customWebTab
            )
        }
        composable<AcknowledgmentsSettingsScreenRoute> {
            AcknowledgmentsSettingsScreen(
                navController = navController,
                settingsScreenVM = settingsScreenVM,
            )
        }
        composable<DataSettingsScreenRoute> {
            DataSettingsScreen(
                navController = navController,
                settingsScreenVM = settingsScreenVM,
            )
        }
        composable<GeneralSettingsScreenRoute> {
            GeneralSettingsScreen(
                navController = navController,
                settingsScreenVM = settingsScreenVM
            )
        }
        composable<PrivacySettingsScreenRoute> {
            PrivacySettingsScreen(
                navController = navController,
                settingsScreenVM = settingsScreenVM,
            )
        }
        composable<ThemeSettingsScreenRoute> {
            ThemeSettingsScreen(
                navController = navController,
                settingsScreenVM = settingsScreenVM,
            )
        }
        composable<LanguageSettingsScreenRoute> {
            LanguageSettingsScreen(
                navController = navController,
                customWebTab = customWebTab
            )
        }
        composable<ShelfScreenRoute> {
            ShelfPanelsScreen(navController)
        }
        composable<SpecificPanelScreenRoute> {
            SpecificPanelScreen(navController)
        }
        composable<LinkLayoutSettingsRoute> {
            LinkLayoutSettings(navController)
        }
        composable<AllLinksScreenRoute> {
            AllLinksScreen(navController)
        }
        composable<AdvancedSettingsScreenRoute> {
            AdvancedSettingsScreen(navController)
        }
        composable<SiteSpecificUserAgentSettingsScreenRoute> {
            SiteSpecificUserAgentSettingsScreen(navController)
        }
    }
}