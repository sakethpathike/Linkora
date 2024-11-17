package com.sakethh.linkora

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sakethh.linkora.ui.CustomWebTab
import com.sakethh.linkora.ui.navigation.BottomNavigationBar
import com.sakethh.linkora.ui.navigation.CollectionsScreenRoute
import com.sakethh.linkora.ui.navigation.HomeScreenRoute
import com.sakethh.linkora.ui.navigation.MainNavigation
import com.sakethh.linkora.ui.navigation.SearchScreenRoute
import com.sakethh.linkora.ui.navigation.SettingsScreenRoute
import com.sakethh.linkora.ui.screens.search.SearchScreenVM
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.dataStore
import com.sakethh.linkora.ui.screens.settings.SettingsPreferences
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.theme.LinkoraTheme
import com.sakethh.linkora.ui.transferActions.TransferActionType
import com.sakethh.linkora.ui.transferActions.TransferActions
import com.sakethh.linkora.ui.transferActions.TransferActionsBtmBar
import com.sakethh.linkora.utils.isNetworkAvailable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            SettingsPreference.readAllPreferencesValues(this@MainActivity)
            LocalizedStrings.loadStrings(this@MainActivity)
        }
        setContent {
            LinkoraTheme {
                val context = LocalContext.current
                val coroutineScope = rememberCoroutineScope()
                val systemUIController = rememberSystemUiController()
                val navController = rememberNavController()
                val currentBackStackEntry = navController.currentBackStackEntryAsState()
                val bottomBarSheetState =
                    androidx.compose.material.rememberBottomSheetScaffoldState()
                systemUIController.setStatusBarColor(colorScheme.surface)
                val navigationBarElevation = NavigationBarDefaults.Elevation
                val nonSpecificCollectionScreenBtmNavColor = colorScheme.surfaceColorAtElevation(
                    navigationBarElevation
                )
                val specificCollectionScreenBtmNavColor = colorScheme.surface
                LaunchedEffect(
                    key1 = currentBackStackEntry.value,
                    key2 = SearchScreenVM.isSearchEnabled.value,
                    key3 = TransferActions.currentTransferActionType.value
                ) {
                    val mainRoutes = (listOf(
                        HomeScreenRoute,
                        SearchScreenRoute,
                        CollectionsScreenRoute,
                        SettingsScreenRoute
                    ))
                    systemUIController.setNavigationBarColor(
                        color = if (mainRoutes.any {
                                currentBackStackEntry.value?.destination?.hasRoute(it::class) == true
                            })
                            nonSpecificCollectionScreenBtmNavColor else specificCollectionScreenBtmNavColor
                    )
                    if (TransferActions.currentTransferActionType.value != TransferActionType.NOTHING || (mainRoutes.any {
                            currentBackStackEntry.value?.destination?.hasRoute(it::class) == true
                        } && !SearchScreenVM.isSearchEnabled.value)) {
                        coroutineScope.launch {
                            bottomBarSheetState.bottomSheetState.expand()
                        }
                    } else {
                        coroutineScope.launch {
                            bottomBarSheetState.bottomSheetState.collapse()
                        }
                    }
                }
                val customWebTab: CustomWebTab = hiltViewModel()
                val mainActivityVM: MainActivityVM = hiltViewModel()
                val settingsScreenVM: SettingsScreenVM = hiltViewModel()
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    androidx.compose.material.BottomSheetScaffold(sheetPeekHeight = 0.dp,
                        sheetGesturesEnabled = false,
                        scaffoldState = bottomBarSheetState,
                        sheetContent = {
                            if (TransferActions.currentTransferActionType.value != TransferActionType.NOTHING) {
                                TransferActionsBtmBar(currentBackStackEntry)
                            } else {
                                BottomNavigationBar(navController = navController)
                            }
                        }) {
                        Scaffold {
                            MainNavigation(
                                navController = navController,
                                customWebTab = customWebTab,
                                settingsScreenVM = settingsScreenVM
                            )
                        }
                    }
                }
                LaunchedEffect(
                    key1 = SettingsPreference.isAutoCheckUpdatesEnabled.value,
                    key2 = SettingsScreenVM.latestReleaseInfoFromGitHubReleases.collectAsState().value.releaseName
                ) {
                    async {
                        if (isNetworkAvailable(context) && SettingsPreference.isAutoCheckUpdatesEnabled.value) {
                            settingsScreenVM.latestAppVersionRetriever { }
                        }
                    }.await()
                    if (isNetworkAvailable(context) && SettingsPreference.isAutoCheckUpdatesEnabled.value) {
                        SettingsPreference.isOnLatestUpdate.value =
                            SettingsPreference.APP_VERSION_NAME == SettingsScreenVM.latestReleaseInfoFromGitHubReleases.value.releaseName
                        withContext(Dispatchers.Main) {
                            if (SettingsPreference.APP_VERSION_NAME != SettingsScreenVM.latestReleaseInfoFromGitHubReleases.value.releaseName && SettingsScreenVM.latestReleaseInfoFromGitHubReleases.value.releaseName != "") {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.a_new_update_is_available),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
                LaunchedEffect(key1 = SettingsPreference.didDataAutoDataMigratedFromV9.value) {
                    if (!SettingsPreference.didDataAutoDataMigratedFromV9.value) {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                mainActivityVM.migrateArchiveFoldersV9toV10()
                                mainActivityVM.migrateRegularFoldersLinksDataFromV9toV10()
                            } finally {
                                SettingsPreference.changeSettingPreferenceValue(
                                    preferenceKey = booleanPreferencesKey(
                                        SettingsPreferences.IS_DATA_MIGRATION_COMPLETED_FROM_V9.name
                                    ), dataStore = context.dataStore, newValue = true
                                )
                                SettingsPreference.didDataAutoDataMigratedFromV9.value =
                                    SettingsPreference.readSettingPreferenceValue(
                                        preferenceKey = booleanPreferencesKey(
                                            SettingsPreferences.IS_DATA_MIGRATION_COMPLETED_FROM_V9.name
                                        ), dataStore = context.dataStore
                                    ) ?: true
                            }
                        }
                    }
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        if (newBase != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            CoroutineScope(Dispatchers.Default).launch {
                val appLanguageCode = SettingsPreference.readSettingPreferenceValue(
                    stringPreferencesKey(SettingsPreferences.APP_LANGUAGE_CODE.name),
                    newBase.dataStore
                ) ?: "en"
                val locale = Locale(appLanguageCode)
                val config = Configuration(newBase.resources.configuration)
                Locale.setDefault(locale)
                config.setLocale(locale)
                newBase.resources.updateConfiguration(config, newBase.resources.displayMetrics)
            }
        }
        super.attachBaseContext(newBase)
    }
}