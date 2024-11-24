package com.sakethh.linkora.ui.screens.settings.specific

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.navigation.NavController
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.screens.settings.PreferenceType
import com.sakethh.linkora.ui.screens.settings.Preferences
import com.sakethh.linkora.ui.screens.settings.Preferences.dataStore
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.screens.settings.SettingsUIElement
import com.sakethh.linkora.ui.screens.settings.composables.RegularSettingComponent
import com.sakethh.linkora.ui.screens.settings.composables.SpecificScreenScaffold
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(navController: NavController, settingsScreenVM: SettingsScreenVM) {
    val context = LocalContext.current
    val isSystemInDarkTheme = isSystemInDarkTheme()
    LaunchedEffect(key1 = Unit) {
        settingsScreenVM.eventChannel.collectLatest {
            when (it) {
                is CommonUiEvent.ShowToast -> {
                    Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }
    SpecificScreenScaffold(
        topAppBarText = LocalizedStrings.theme.value,
        navController = navController
    ) { paddingValues, topAppBarScrollBehaviour ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(topAppBarScrollBehaviour.nestedScrollConnection)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Preferences.shouldDarkThemeBeEnabled.value) {
                item(key = "Follow System Theme") {
                    RegularSettingComponent(
                        settingsUIElement = SettingsUIElement(
                            title = LocalizedStrings.followSystemTheme.value,
                            doesDescriptionExists = false,
                            isSwitchNeeded = true,
                            description = null,
                            isSwitchEnabled = Preferences.shouldFollowSystemTheme,
                            onSwitchStateChange = {
                                Preferences.changeSettingPreferenceValue(
                                    preferenceKey = booleanPreferencesKey(
                                        PreferenceType.FOLLOW_SYSTEM_THEME.name
                                    ),
                                    dataStore = context.dataStore,
                                    newValue = !Preferences.shouldFollowSystemTheme.value
                                )
                                Preferences.shouldFollowSystemTheme.value =
                                    !Preferences.shouldFollowSystemTheme.value
                            }, isIconNeeded = remember {
                                mutableStateOf(false)
                            })
                    )
                }
            }
            if (!Preferences.shouldFollowSystemTheme.value) {
                item(key = "Use Dark Mode") {
                    RegularSettingComponent(
                        settingsUIElement = SettingsUIElement(
                            title = LocalizedStrings.useDarkMode.value,
                            doesDescriptionExists = false,
                            description = null,
                            isSwitchNeeded = true,
                            isSwitchEnabled = Preferences.shouldDarkThemeBeEnabled,
                            onSwitchStateChange = {
                                Preferences.changeSettingPreferenceValue(
                                    preferenceKey = booleanPreferencesKey(
                                        PreferenceType.DARK_THEME.name
                                    ),
                                    dataStore = context.dataStore,
                                    newValue = !Preferences.shouldDarkThemeBeEnabled.value
                                )
                                Preferences.shouldDarkThemeBeEnabled.value =
                                    !Preferences.shouldDarkThemeBeEnabled.value
                            }, isIconNeeded = remember {
                                mutableStateOf(false)
                            })
                    )
                }
            }
            if (Preferences.shouldDarkThemeBeEnabled.value || (isSystemInDarkTheme && Preferences.shouldFollowSystemTheme.value)) {
                item(key = PreferenceType.AMOLED_THEME_STATE.name) {
                RegularSettingComponent(
                    settingsUIElement = SettingsUIElement(
                        title = LocalizedStrings.useAmoledTheme.value,
                        doesDescriptionExists = false,
                        description = "",
                        isSwitchNeeded = true,
                        isSwitchEnabled = Preferences.shouldFollowAmoledTheme,
                        onSwitchStateChange = {
                            Preferences.changeSettingPreferenceValue(
                                preferenceKey = booleanPreferencesKey(
                                    PreferenceType.AMOLED_THEME_STATE.name
                                ),
                                dataStore = context.dataStore,
                                newValue = !Preferences.shouldFollowAmoledTheme.value
                            )
                            Preferences.shouldFollowAmoledTheme.value =
                                !Preferences.shouldFollowAmoledTheme.value
                        }, isIconNeeded = remember {
                            mutableStateOf(false)
                        })
                )
            }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                item(key = "Use dynamic theming") {
                    RegularSettingComponent(
                        settingsUIElement = SettingsUIElement(
                            title = LocalizedStrings.useDynamicTheming.value,
                            doesDescriptionExists = true,
                            description = LocalizedStrings.useDynamicThemingDesc.value,
                            isSwitchNeeded = true,
                            isSwitchEnabled = Preferences.shouldFollowDynamicTheming,
                            onSwitchStateChange = {
                                Preferences.changeSettingPreferenceValue(
                                    preferenceKey = booleanPreferencesKey(
                                        PreferenceType.DYNAMIC_THEMING.name
                                    ),
                                    dataStore = context.dataStore,
                                    newValue = !Preferences.shouldFollowDynamicTheming.value
                                )
                                Preferences.shouldFollowDynamicTheming.value =
                                    !Preferences.shouldFollowDynamicTheming.value
                            }, isIconNeeded = remember {
                                mutableStateOf(false)
                            })
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}