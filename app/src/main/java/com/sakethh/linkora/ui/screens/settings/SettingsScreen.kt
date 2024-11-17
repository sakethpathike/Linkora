package com.sakethh.linkora.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sakethh.linkora.BuildConfig
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.ui.navigation.AboutSettingsScreenRoute
import com.sakethh.linkora.ui.navigation.AcknowledgmentsSettingsScreenRoute
import com.sakethh.linkora.ui.navigation.AdvancedSettingsScreenRoute
import com.sakethh.linkora.ui.navigation.CollectionsScreenRoute
import com.sakethh.linkora.ui.navigation.DataSettingsScreenRoute
import com.sakethh.linkora.ui.navigation.GeneralSettingsScreenRoute
import com.sakethh.linkora.ui.navigation.HomeScreenRoute
import com.sakethh.linkora.ui.navigation.LanguageSettingsScreenRoute
import com.sakethh.linkora.ui.navigation.LinkLayoutSettingsRoute
import com.sakethh.linkora.ui.navigation.PrivacySettingsScreenRoute
import com.sakethh.linkora.ui.navigation.ThemeSettingsScreenRoute
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM.Companion.currentSelectedSettingSection
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
        val settingsScreenOptions = remember {
            settingsScreenOptions(navController)
        }
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
                items(settingsScreenOptions) {
                    if (LocalizedStrings.privacy.value == it.sectionTitle && BuildConfig.FLAVOR == "fdroid") {
                        return@items
                    }
                    SettingsSectionComposable(
                        onClick = it.onClick,
                        sectionTitle = it.sectionTitle,
                        sectionIcon = it.sectionIcon,
                        shouldArrowIconAppear = it.shouldArrowIconAppear
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
        BackHandler {
            if (SettingsPreference.isHomeScreenEnabled.value) {
                navController.navigate(HomeScreenRoute) {
                    popUpTo(0)
                }
            } else {
                navController.navigate(CollectionsScreenRoute) {
                    popUpTo(0)
                }
            }
        }
    }
}

private data class SettingsScreenOption(
    val onClick: () -> Unit,
    val sectionTitle: String,
    val sectionIcon: ImageVector,
    val shouldArrowIconAppear: Boolean = true
)

private fun settingsScreenOptions(navController: NavController): List<SettingsScreenOption> {
    return listOf(
        SettingsScreenOption(
            onClick = {
                currentSelectedSettingSection.value =
                    SettingsSections.THEME
                navController.navigate(ThemeSettingsScreenRoute)
            },
            sectionTitle = LocalizedStrings.theme.value,
            sectionIcon = Icons.Default.ColorLens
        ),
        SettingsScreenOption(
            onClick = {
                currentSelectedSettingSection.value =
                    SettingsSections.GENERAL
                navController.navigate(GeneralSettingsScreenRoute)
            },
            sectionTitle = LocalizedStrings.general.value,
            sectionIcon = Icons.Default.SettingsInputSvideo
        ),
        SettingsScreenOption(
            onClick = {
                currentSelectedSettingSection.value =
                    SettingsSections.ADVANCED
                navController.navigate(AdvancedSettingsScreenRoute)
            },
            sectionTitle = LocalizedStrings.advanced.value,
            sectionIcon = Icons.Default.Build
        ),
        SettingsScreenOption(
            onClick = {
                navController.navigate(LinkLayoutSettingsRoute)
            },
            sectionTitle = LocalizedStrings.linkLayout.value,
            sectionIcon = Icons.Default.Dashboard
        ),
        SettingsScreenOption(
            onClick = {
                currentSelectedSettingSection.value =
                    SettingsSections.LANGUAGE
                navController.navigate(LanguageSettingsScreenRoute)
            },
            sectionTitle = LocalizedStrings.language.value,
            sectionIcon = Icons.Default.Language
        ),
        SettingsScreenOption(
            onClick = {
                currentSelectedSettingSection.value =
                    SettingsSections.DATA
                navController.navigate(DataSettingsScreenRoute)
            },
            sectionTitle = LocalizedStrings.data.value,
            sectionIcon = Icons.Default.Storage
        ),
        SettingsScreenOption(
            onClick = {
                currentSelectedSettingSection.value =
                    SettingsSections.PRIVACY
                navController.navigate(PrivacySettingsScreenRoute)
            },
            sectionTitle = LocalizedStrings.privacy.value,
            sectionIcon = Icons.Default.PrivacyTip
        ),
        SettingsScreenOption(
            onClick = {
                currentSelectedSettingSection.value =
                    SettingsSections.ABOUT
                navController.navigate(AboutSettingsScreenRoute)
            },
            sectionTitle = LocalizedStrings.about.value,
            sectionIcon = Icons.Default.Info
        ),
        SettingsScreenOption(
            onClick = {
                currentSelectedSettingSection.value =
                    SettingsSections.ACKNOWLEDGMENT
                navController.navigate(AcknowledgmentsSettingsScreenRoute)
            },
            sectionTitle = LocalizedStrings.acknowledgments.value,
            sectionIcon = Icons.Default.Group
        ),
    )
}