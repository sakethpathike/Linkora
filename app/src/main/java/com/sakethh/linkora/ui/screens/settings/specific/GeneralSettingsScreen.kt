package com.sakethh.linkora.ui.screens.settings.specific

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavController
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.navigation.CollectionsScreenRoute
import com.sakethh.linkora.ui.navigation.HomeScreenRoute
import com.sakethh.linkora.ui.navigation.SearchScreenRoute
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SettingsPreference.dataStore
import com.sakethh.linkora.ui.screens.settings.SettingsPreferences
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.screens.settings.SettingsUIElement
import com.sakethh.linkora.ui.screens.settings.composables.RegularSettingComponent
import com.sakethh.linkora.ui.screens.settings.composables.SpecificScreenScaffold
import com.sakethh.linkora.ui.theme.poppinsFontFamily
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingsScreen(
    navController: NavController,
    settingsScreenVM: SettingsScreenVM
) {
    val context = LocalContext.current
    val showInitialNavigationChangerDialogBox = rememberSaveable {
        mutableStateOf(false)
    }
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
        topAppBarText = LocalizedStrings.general.value,
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
            items(settingsScreenVM.generalSection(context)) {
                RegularSettingComponent(
                    settingsUIElement = it
                )
            }
            item {
                RegularSettingComponent(
                    settingsUIElement = SettingsUIElement(
                        title = "Initial Screen on Launch",
                        doesDescriptionExists = true,
                        description = "Changes made with this option will reflect in the navigation of the initial screen that will open when you launch Linkora.",
                        isSwitchNeeded = false,
                        isSwitchEnabled = mutableStateOf(false),
                        onSwitchStateChange = {
                            showInitialNavigationChangerDialogBox.value = true
                        },
                        onAcknowledgmentClick = { uriHandler: UriHandler, context: Context ->
                            showInitialNavigationChangerDialogBox.value = true
                        },
                        icon = Icons.Default.Launch,
                        isIconNeeded = mutableStateOf(true),
                    )
                )
            }
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
    if (showInitialNavigationChangerDialogBox.value) {
        val currentlySelectedRoute = rememberSaveable {
            mutableStateOf(SettingsPreference.startDestination.value)
        }
        AlertDialog(onDismissRequest = {
            showInitialNavigationChangerDialogBox.value = false
        }, confirmButton = {
            Button(onClick = {
                SettingsPreference.changeSettingPreferenceValue(
                    stringPreferencesKey(
                        SettingsPreferences.INITIAL_ROUTE.name
                    ), context.dataStore, currentlySelectedRoute.value
                )
                SettingsPreference.startDestination.value = currentlySelectedRoute.value
                showInitialNavigationChangerDialogBox.value = false
            }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Confirm", style = MaterialTheme.typography.titleSmall)
            }
        }, dismissButton = {
            OutlinedButton(onClick = {
                showInitialNavigationChangerDialogBox.value = false
            }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Cancel", style = MaterialTheme.typography.titleSmall)
            }
        }, text = {
            Column {
                listOf(HomeScreenRoute, SearchScreenRoute, CollectionsScreenRoute).forEach {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp, bottom = 5.dp)
                            .clickable(onClick = {
                                currentlySelectedRoute.value = it.toString()
                            }, indication = null, interactionSource = remember {
                                MutableInteractionSource()
                            })
                            .pulsateEffect(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentlySelectedRoute.value == it.toString(),
                            onClick = {
                                currentlySelectedRoute.value = it.toString()
                            })
                        Text(
                            style = if (currentlySelectedRoute.value == it.toString()) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleSmall,
                            text = it.toString().substringBefore("Screen")
                        )
                    }
                }
            }
        }, title = {
            Text(
                text = "Select the initial screen on launch",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 16.sp
            )
        })
    }
}

@Composable
fun TextFieldForPreferenceComposable(
    textFieldDescText: String,
    textFieldLabel: String,
    textFieldValue: String,
    onResetButtonClick: () -> Unit,
    onTextFieldValueChange: (String) -> Unit,
    onConfirmButtonClick: () -> Unit,
    focusRequester: FocusRequester,
    readonly: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 25.dp, end = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ProvideTextStyle(value = TextStyle(fontFamily = poppinsFontFamily)) {
            OutlinedTextField(
                supportingText = {
                    Text(
                        text = textFieldDescText,
                        style = MaterialTheme.typography.titleSmall,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(
                            top = 5.dp,
                            bottom = 5.dp
                        )
                    )
                },
                value = textFieldValue,
                onValueChange = {
                    onTextFieldValueChange(it)
                },
                readOnly = readonly,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .focusRequester(focusRequester),
                label = {
                    Text(
                        text = textFieldLabel,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            FilledTonalIconToggleButton(
                checked = !readonly,
                onCheckedChange = {
                    onConfirmButtonClick()
                }) {
                Icon(
                    imageVector = if (readonly) Icons.Default.Edit else Icons.Default.Check,
                    contentDescription = ""
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            FilledTonalIconButton(onClick = {
                onResetButtonClick()
            }) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = ""
                )
            }
        }
    }
}