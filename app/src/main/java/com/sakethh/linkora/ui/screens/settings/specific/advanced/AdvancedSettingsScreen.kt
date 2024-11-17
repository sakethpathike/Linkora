package com.sakethh.linkora.ui.screens.settings.specific.advanced

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavController
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.navigation.SiteSpecificUserAgentSettingsScreenRoute
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.dataStore
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.localizationServerURL
import com.sakethh.linkora.ui.screens.settings.SettingsPreferences
import com.sakethh.linkora.ui.screens.settings.composables.SpecificScreenScaffold
import com.sakethh.linkora.ui.screens.settings.specific.TextFieldForPreferenceComposable
import com.sakethh.linkora.utils.LinkoraValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val primaryJsoupStringAgent = SettingsPreference.primaryJsoupUserAgent
    val secondaryJsoupStringAgent = SettingsPreference.secondaryJsoupUserAgent
    val isReadOnlyTextFieldForPrimaryUserAgent = rememberSaveable {
        mutableStateOf(true)
    }
    val isReadOnlyTextFieldForSecondaryUserAgent = rememberSaveable {
        mutableStateOf(true)
    }
    val isReadOnlyTextFieldForLocalizationServer = rememberSaveable {
        mutableStateOf(true)
    }
    val primaryJsoupUserAgentFocusRequester = remember { FocusRequester() }
    val secondaryJsoupUserAgentFocusRequester = remember { FocusRequester() }
    val localizationServerTextFieldFocusRequester = remember { FocusRequester() }
    val colorScheme = MaterialTheme.colorScheme
    SpecificScreenScaffold(
        topAppBarText = LocalizedStrings.advanced.value,
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
            item {
                Spacer(Modifier)
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .pulsateEffect()
                        .clickable(interactionSource = remember {
                            MutableInteractionSource()
                        }, onClick = {
                            navController.navigate(SiteSpecificUserAgentSettingsScreenRoute)
                        }, indication = null)
                ) {
                    Spacer(modifier = Modifier.width(10.dp))
                    FilledTonalIconButton(onClick = {
                        navController.navigate(SiteSpecificUserAgentSettingsScreenRoute)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Rule,
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = LocalizedStrings.siteSpecificUserAgentSettings.value,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 20.dp)
                    )
                }
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier
                        .clickable(onClick = {
                            navController.navigate(SiteSpecificUserAgentSettingsScreenRoute)
                        }, interactionSource = remember {
                            MutableInteractionSource()
                        }, indication = null)
                        .pulsateEffect()
                ) {
                    HorizontalDivider(
                        thickness = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 32.dp)
                    )
                    Row {
                        IconButton(onClick = {
                            navController.navigate(
                                SiteSpecificUserAgentSettingsScreenRoute
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = DividerDefaults.color
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }
            }
            item(key = "PrimaryJsoupUserAgent") {
                TextFieldForPreferenceComposable(
                    textFieldDescText = LocalizedStrings.userAgentDesc.value,
                    textFieldLabel = LocalizedStrings.primaryUserAgent.value,
                    textFieldValue = primaryJsoupStringAgent.value,
                    onResetButtonClick = {
                        SettingsPreference.changeSettingPreferenceValue(
                            stringPreferencesKey(SettingsPreferences.JSOUP_USER_AGENT.name),
                            context.dataStore,
                            "Twitterbot/1.0"
                        )
                        SettingsPreference.primaryJsoupUserAgent.value =
                            "Twitterbot/1.0"
                    },
                    onTextFieldValueChange = {
                        primaryJsoupStringAgent.value = it
                    },
                    onConfirmButtonClick = {
                        isReadOnlyTextFieldForPrimaryUserAgent.value =
                            !isReadOnlyTextFieldForPrimaryUserAgent.value
                        if (!isReadOnlyTextFieldForPrimaryUserAgent.value) {
                            primaryJsoupUserAgentFocusRequester.requestFocus()
                        } else {
                            primaryJsoupUserAgentFocusRequester.freeFocus()
                        }
                        if (isReadOnlyTextFieldForPrimaryUserAgent.value) {
                            SettingsPreference.changeSettingPreferenceValue(
                                stringPreferencesKey(SettingsPreferences.JSOUP_USER_AGENT.name),
                                context.dataStore,
                                primaryJsoupStringAgent.value
                            )
                            SettingsPreference.primaryJsoupUserAgent.value =
                                primaryJsoupStringAgent.value
                        }
                    },
                    focusRequester = primaryJsoupUserAgentFocusRequester,
                    readonly = isReadOnlyTextFieldForPrimaryUserAgent.value
                )
            }
            item(key = "SecondaryJsoupUserAgent") {
                TextFieldForPreferenceComposable(
                    textFieldDescText = LocalizedStrings.secondaryUserAgentDesc.value,
                    textFieldLabel = LocalizedStrings.secondaryUserAgent.value,
                    textFieldValue = secondaryJsoupStringAgent.value,
                    onResetButtonClick = {
                        SettingsPreference.changeSettingPreferenceValue(
                            stringPreferencesKey(SettingsPreferences.SECONDARY_JSOUP_USER_AGENT.name),
                            context.dataStore,
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:131.0) Gecko/20100101 Firefox/131.0"
                        )
                        SettingsPreference.secondaryJsoupUserAgent.value =
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:131.0) Gecko/20100101 Firefox/131.0"
                    },
                    onTextFieldValueChange = {
                        secondaryJsoupStringAgent.value = it
                    },
                    onConfirmButtonClick = {
                        isReadOnlyTextFieldForSecondaryUserAgent.value =
                            !isReadOnlyTextFieldForSecondaryUserAgent.value
                        if (!isReadOnlyTextFieldForSecondaryUserAgent.value) {
                            secondaryJsoupUserAgentFocusRequester.requestFocus()
                        } else {
                            secondaryJsoupUserAgentFocusRequester.freeFocus()
                        }
                        if (isReadOnlyTextFieldForSecondaryUserAgent.value) {
                            SettingsPreference.changeSettingPreferenceValue(
                                stringPreferencesKey(SettingsPreferences.SECONDARY_JSOUP_USER_AGENT.name),
                                context.dataStore,
                                secondaryJsoupStringAgent.value
                            )
                            SettingsPreference.secondaryJsoupUserAgent.value =
                                secondaryJsoupStringAgent.value
                        }
                    },
                    focusRequester = secondaryJsoupUserAgentFocusRequester,
                    readonly = isReadOnlyTextFieldForSecondaryUserAgent.value
                )
            }

            item(key = "LinkoraLocalizationServerURL") {
                TextFieldForPreferenceComposable(
                    textFieldDescText = LocalizedStrings.localizationServerDesc.value,
                    textFieldLabel = LocalizedStrings.localizationServer.value,
                    textFieldValue = localizationServerURL.value,
                    onResetButtonClick = {
                        SettingsPreference.changeSettingPreferenceValue(
                            stringPreferencesKey(SettingsPreferences.LOCALIZATION_SERVER_URL.name),
                            context.dataStore,
                            LinkoraValues.LINKORA_LOCALIZATION_SERVER
                        )
                        localizationServerURL.value =
                            LinkoraValues.LINKORA_LOCALIZATION_SERVER
                    },
                    onTextFieldValueChange = {
                        localizationServerURL.value = it
                    },
                    onConfirmButtonClick = {
                        isReadOnlyTextFieldForLocalizationServer.value =
                            !isReadOnlyTextFieldForLocalizationServer.value
                        if (!isReadOnlyTextFieldForLocalizationServer.value) {
                            localizationServerTextFieldFocusRequester.requestFocus()
                        } else {
                            localizationServerTextFieldFocusRequester.freeFocus()
                        }
                        if (isReadOnlyTextFieldForLocalizationServer.value) {
                            SettingsPreference.changeSettingPreferenceValue(
                                stringPreferencesKey(SettingsPreferences.LOCALIZATION_SERVER_URL.name),
                                context.dataStore,
                                localizationServerURL.value
                            )
                        }
                    },
                    focusRequester = localizationServerTextFieldFocusRequester,
                    readonly = isReadOnlyTextFieldForLocalizationServer.value
                )
            }
            item {
                Spacer(Modifier.height(100.dp))
            }
        }
    }
}