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
    val jsoupStringAgent = SettingsPreference.jsoupUserAgent
    val isReadOnlyTextFieldForUserAgent = rememberSaveable {
        mutableStateOf(true)
    }
    val isReadOnlyTextFieldForLocalizationServer = rememberSaveable {
        mutableStateOf(true)
    }
    val jsoupUserAgentFocusRequester = remember { FocusRequester() }
    val localizationServerTextFieldFocusRequester = remember { FocusRequester() }
    SpecificScreenScaffold(
        topAppBarText = "Advanced",
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
            item(key = "JsoupUserAgent") {
                TextFieldForPreferenceComposable(
                    textFieldDescText = LocalizedStrings.userAgentDesc.value,
                    textFieldLabel = LocalizedStrings.userAgent.value,
                    textFieldValue = jsoupStringAgent.value,
                    onResetButtonClick = {
                        SettingsPreference.changeSettingPreferenceValue(
                            stringPreferencesKey(SettingsPreferences.JSOUP_USER_AGENT.name),
                            context.dataStore,
                            "Twitterbot/1.0"
                        )
                        SettingsPreference.jsoupUserAgent.value =
                            "Twitterbot/1.0"
                    },
                    onTextFieldValueChange = {
                        jsoupStringAgent.value = it
                    },
                    onConfirmButtonClick = {
                        isReadOnlyTextFieldForUserAgent.value =
                            !isReadOnlyTextFieldForUserAgent.value
                        if (!isReadOnlyTextFieldForUserAgent.value) {
                            jsoupUserAgentFocusRequester.requestFocus()
                        } else {
                            jsoupUserAgentFocusRequester.freeFocus()
                        }
                        if (isReadOnlyTextFieldForUserAgent.value) {
                            SettingsPreference.changeSettingPreferenceValue(
                                stringPreferencesKey(SettingsPreferences.JSOUP_USER_AGENT.name),
                                context.dataStore,
                                jsoupStringAgent.value
                            )
                            SettingsPreference.jsoupUserAgent.value =
                                jsoupStringAgent.value
                        }
                    },
                    focusRequester = jsoupUserAgentFocusRequester,
                    readonly = isReadOnlyTextFieldForUserAgent.value
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