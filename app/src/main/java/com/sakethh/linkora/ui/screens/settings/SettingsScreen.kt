package com.sakethh.linkora.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.SettingsInputSvideo
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sakethh.linkora.BuildConfig
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.ui.navigation.NavigationRoutes
import com.sakethh.linkora.ui.screens.settings.composables.SettingsSectionComposable
import com.sakethh.linkora.ui.theme.LinkoraTheme

@OptIn(
    ExperimentalMaterial3Api::class
)
@PreviewLightDark
@Composable
fun SettingsScreen(navController: NavController = rememberNavController()) {
    val topAppBarScrollState = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    LinkoraTheme {
        Scaffold(topBar = {
            Column {
                LargeTopAppBar(scrollBehavior = topAppBarScrollState, title = {
                    Text(
                        text = LocalizedStrings.settings.value,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 24.sp
                    )
                })
            }
        }) { it ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .nestedScroll(topAppBarScrollState.nestedScrollConnection)
            ) {
                item(key = "themeRow") {
                    SettingsSectionComposable(
                        onClick = {
                            SettingsScreenVM.currentSelectedSettingSection.value =
                                SettingsSections.THEME
                            navController.navigate(NavigationRoutes.THEME_SETTINGS_SCREEN.name)
                        },
                        sectionTitle = LocalizedStrings.theme.value,
                        sectionIcon = Icons.Default.ColorLens
                    )
                }
                item(key = "generalRow") {
                    SettingsSectionComposable(
                        onClick = {
                            SettingsScreenVM.currentSelectedSettingSection.value =
                                SettingsSections.GENERAL
                            navController.navigate(NavigationRoutes.GENERAL_SETTINGS_SCREEN.name)
                        },
                        sectionTitle = LocalizedStrings.general.value,
                        sectionIcon = Icons.Default.SettingsInputSvideo
                    )
                }
                item(key = "layoutRow") {
                    SettingsSectionComposable(
                        onClick = {
                            navController.navigate(NavigationRoutes.LINK_LAYOUT_SETTINGS.name)
                        },
                        sectionTitle = "Link Layout",
                        sectionIcon = Icons.Default.Dashboard
                    )
                }
                item(key = "languageRow") {
                    SettingsSectionComposable(
                        onClick = {
                            SettingsScreenVM.currentSelectedSettingSection.value =
                                SettingsSections.LANGUAGE
                            navController.navigate(NavigationRoutes.LANGUAGE_SETTINGS_SCREEN.name)
                        },
                        sectionTitle = LocalizedStrings.language.value,
                        sectionIcon = Icons.Default.Language
                    )
                }
                item(key = "dataRow") {
                    SettingsSectionComposable(
                        onClick = {
                            SettingsScreenVM.currentSelectedSettingSection.value =
                                SettingsSections.DATA
                            navController.navigate(NavigationRoutes.DATA_SETTINGS_SCREEN.name)
                        },
                        sectionTitle = LocalizedStrings.data.value,
                        sectionIcon = Icons.Default.Storage
                    )
                }
                if (BuildConfig.FLAVOR != "fdroid") {
                    item(key = "privacyRow") {
                        SettingsSectionComposable(
                            onClick = {
                                SettingsScreenVM.currentSelectedSettingSection.value =
                                    SettingsSections.PRIVACY
                                navController.navigate(NavigationRoutes.PRIVACY_SETTINGS_SCREEN.name)
                            },
                            sectionTitle = LocalizedStrings.privacy.value,
                            sectionIcon = Icons.Default.PrivacyTip
                        )
                    }
                }
                item(key = "aboutRow") {
                    SettingsSectionComposable(
                        onClick = {
                            SettingsScreenVM.currentSelectedSettingSection.value =
                                SettingsSections.ABOUT
                            navController.navigate(NavigationRoutes.ABOUT_SETTINGS_SCREEN.name)
                        },
                        sectionTitle = LocalizedStrings.about.value,
                        sectionIcon = Icons.Default.Info
                    )
                }
                item(key = "acknowledgmentsRow") {
                    SettingsSectionComposable(
                        onClick = {
                            SettingsScreenVM.currentSelectedSettingSection.value =
                                SettingsSections.ACKNOWLEDGMENT
                            navController.navigate(NavigationRoutes.ACKNOWLEDGMENTS_SETTINGS_SCREEN.name)
                        },
                        sectionTitle = LocalizedStrings.acknowledgments.value,
                        sectionIcon = Icons.Default.Group
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
        BackHandler {
            if (SettingsPreference.isHomeScreenEnabled.value) {
                navController.navigate(NavigationRoutes.HOME_SCREEN.name) {
                    popUpTo(0)
                }
            } else {
                navController.navigate(NavigationRoutes.COLLECTIONS_SCREEN.name) {
                    popUpTo(0)
                }
            }
        }
    }
}