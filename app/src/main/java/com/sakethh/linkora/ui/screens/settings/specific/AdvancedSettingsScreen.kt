package com.sakethh.linkora.ui.screens.settings.specific

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavController
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.dataStore
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.localizationServerURL
import com.sakethh.linkora.ui.screens.settings.SettingsPreferences
import com.sakethh.linkora.ui.screens.settings.composables.SpecificScreenScaffold
import com.sakethh.linkora.utils.Constants

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
            item(key = "PrimaryJsoupUserAgent") {
                TextFieldForPreferenceComposable(
                    textFieldDescText = LocalizedStrings.userAgentDesc.value,
                    textFieldLabel = LocalizedStrings.userAgent.value,
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
                    textFieldDescText = "Linkora uses this user agent if the request fails with the primary user agent",
                    textFieldLabel = "Secondary User Agent",
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
                            Constants.LINKORA_LOCALIZATION_SERVER
                        )
                        localizationServerURL.value =
                            Constants.LINKORA_LOCALIZATION_SERVER
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
        }
    }
}