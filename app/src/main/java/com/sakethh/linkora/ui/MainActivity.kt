package com.sakethh.linkora.ui

import android.annotation.SuppressLint
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sakethh.linkora.data.localDB.LocalDataBase
import com.sakethh.linkora.ui.commonComposables.NewFeatureDialogBox
import com.sakethh.linkora.ui.navigation.BottomNavigationBar
import com.sakethh.linkora.ui.navigation.MainNavigation
import com.sakethh.linkora.ui.navigation.NavigationRoutes
import com.sakethh.linkora.ui.theme.LinkoraTheme
import com.sakethh.linkora.ui.viewmodels.SearchScreenVM
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM.Settings.dataStore
import com.sakethh.linkora.ui.viewmodels.localDB.UpdateVM
import com.sakethh.linkora.utils.isNetworkAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            SettingsScreenVM.Settings.readAllPreferencesValues(this@MainActivity)
        }.invokeOnCompletion {
            val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
            firebaseCrashlytics.setCrashlyticsCollectionEnabled(SettingsScreenVM.Settings.isSendCrashReportsEnabled.value)
        }
        setContent {
            LinkoraTheme {
                val context = LocalContext.current
                val coroutineScope = rememberCoroutineScope()
                val systemUIController = rememberSystemUiController()
                val navController = rememberNavController()
                val currentBackStackEntry = navController.currentBackStackEntryAsState()
                val currentRoute = rememberSaveable(
                    inputs = arrayOf(currentBackStackEntry.value?.destination?.route)
                ) {
                    currentBackStackEntry.value?.destination?.route.toString()
                }
                val bottomBarSheetState =
                    androidx.compose.material.rememberBottomSheetScaffoldState()
                systemUIController.setStatusBarColor(colorScheme.surface)
                val isNewFeatureDialogBoxVisible =
                    rememberSaveable(SettingsScreenVM.Settings.shouldNewFeatureDialogBeVisible.value) {
                        mutableStateOf(SettingsScreenVM.Settings.shouldNewFeatureDialogBeVisible.value)
                    }
                LaunchedEffect(key1 = currentRoute, key2 = SearchScreenVM.isSearchEnabled.value) {
                    if (NavigationRoutes.entries.any {
                            it.name != NavigationRoutes.SPECIFIC_SCREEN.name && it.name == currentRoute && it.name != NavigationRoutes.ARCHIVE_SCREEN.name
                        } && !SearchScreenVM.isSearchEnabled.value) {
                        coroutineScope.launch {
                            bottomBarSheetState.bottomSheetState.expand()
                        }
                    } else {
                        coroutineScope.launch {
                            bottomBarSheetState.bottomSheetState.collapse()
                        }
                    }
                }
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    androidx.compose.material.BottomSheetScaffold(sheetPeekHeight = 0.dp,
                        sheetGesturesEnabled = false,
                        scaffoldState = bottomBarSheetState,
                        sheetContent = {
                            BottomNavigationBar(navController = navController)
                        }) {
                        Scaffold {
                            MainNavigation(navController = navController)
                        }
                    }
                }
                LocalDataBase.localDB = LocalDataBase.getLocalDB(context = context)
                LaunchedEffect(key1 = SettingsScreenVM.Settings.isAutoCheckUpdatesEnabled.value) {
                    async {
                        if (isNetworkAvailable(context) && SettingsScreenVM.Settings.isAutoCheckUpdatesEnabled.value) {
                            SettingsScreenVM.Settings.latestAppVersionRetriever(context)
                        }
                    }.await()
                    if (isNetworkAvailable(context) && SettingsScreenVM.Settings.isAutoCheckUpdatesEnabled.value) {
                        SettingsScreenVM.Settings.isOnLatestUpdate.value =
                            !(SettingsScreenVM.APP_VERSION_CODE < SettingsScreenVM.latestAppInfoFromServer.stableVersionCode.value || SettingsScreenVM.APP_VERSION_CODE < SettingsScreenVM.latestAppInfoFromServer.nonStableVersionCode.value)
                        withContext(Dispatchers.Main) {
                            if (SettingsScreenVM.APP_VERSION_CODE < SettingsScreenVM.latestAppInfoFromServer.stableVersionCode.value || SettingsScreenVM.APP_VERSION_CODE < SettingsScreenVM.latestAppInfoFromServer.nonStableVersionCode.value) {
                                Toast.makeText(
                                    context,
                                    "A new update is available; check out the latest update from settings screen",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
                LaunchedEffect(key1 = SettingsScreenVM.Settings.didDataAutoDataMigratedFromV9.value) {
                    if (!SettingsScreenVM.Settings.didDataAutoDataMigratedFromV9.value) {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                this.launch {
                                    if (LocalDataBase.localDB.readDao().getAllArchiveFoldersV9List()
                                            .isNotEmpty()
                                    ) {
                                        UpdateVM().migrateArchiveFoldersV9toV10()
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(
                                                context,
                                                "Archived folders Data Migrated successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }

                                if (LocalDataBase.localDB.readDao().getAllRootFoldersList()
                                        .isNotEmpty()
                                ) {
                                    UpdateVM().migrateRegularFoldersLinksDataFromV9toV10()
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "Root folders Data Migrated successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } finally {
                                SettingsScreenVM.Settings.changeSettingPreferenceValue(
                                    preferenceKey = booleanPreferencesKey(
                                        SettingsScreenVM.SettingsPreferences.IS_DATA_MIGRATION_COMPLETED_FROM_V9.name
                                    ), dataStore = context.dataStore, newValue = true
                                )
                                SettingsScreenVM.Settings.didDataAutoDataMigratedFromV9.value =
                                    SettingsScreenVM.Settings.readSettingPreferenceValue(
                                        preferenceKey = booleanPreferencesKey(
                                            SettingsScreenVM.SettingsPreferences.IS_DATA_MIGRATION_COMPLETED_FROM_V9.name
                                        ), dataStore = context.dataStore
                                    ) ?: true
                            }
                        }
                    }
                }
                val navigationBarElevation = NavigationBarDefaults.Elevation
                val nonSpecificCollectionScreenBtmNavColor = colorScheme.surfaceColorAtElevation(
                    navigationBarElevation
                )
                val specificCollectionScreenBtmNavColor = colorScheme.surface
                systemUIController.setNavigationBarColor(
                    color = if (currentRoute == NavigationRoutes.SPECIFIC_SCREEN.name || currentRoute == NavigationRoutes.ARCHIVE_SCREEN.name) specificCollectionScreenBtmNavColor else nonSpecificCollectionScreenBtmNavColor
                )
                NewFeatureDialogBox(isDialogBoxVisible = isNewFeatureDialogBoxVisible)
            }
        }
    }
}