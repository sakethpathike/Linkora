package com.sakethh.linkora.ui.screens.settings.specific

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sakethh.linkora.R
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM.Settings.dataStore
import com.sakethh.linkora.ui.screens.settings.SettingsUIElement
import com.sakethh.linkora.ui.screens.settings.composables.RegularSettingComponent
import com.sakethh.linkora.ui.screens.settings.composables.SpecificSettingsScreenTopAppBar
import com.sakethh.linkora.ui.theme.fonts
import com.sakethh.linkora.worker.RefreshLinksWorker
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingsScreen(navController: NavController, settingsScreenVM: SettingsScreenVM) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        settingsScreenVM.eventChannel.collectLatest {
            when (it) {
                is CommonUiEvent.ShowToast -> {
                    Toast.makeText(context, context.getString(it.msg), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    val successfulRefreshLinkCount =
        RefreshLinksWorker.successfulRefreshLinksCount.collectAsStateWithLifecycle()
    val jsoupStringAgent = SettingsScreenVM.Settings.jsoupUserAgent
    val isReadOnlyTextFieldForUserAgent = rememberSaveable {
        mutableStateOf(true)
    }
    val focusRequester = remember { FocusRequester() }
    val successfulRefreshLinksCount =
        RefreshLinksWorker.successfulRefreshLinksCount.collectAsStateWithLifecycle()
    SpecificSettingsScreenTopAppBar(
        topAppBarText = stringResource(id = R.string.general),
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .animateContentSize()
                ) {
                    if (!SettingsScreenVM.isAnyRefreshingTaskGoingOn.value) {
                        RegularSettingComponent(
                            settingsUIElement = SettingsUIElement(
                                title = stringResource(
                                    id = R.string.refresh_all_links_titles_and_images
                                ),
                                doesDescriptionExists = true,
                                description = stringResource(id = R.string.refresh_all_links_titles_and_images_desc),
                                isSwitchNeeded = false,
                                isIconNeeded = rememberSaveable {
                                    mutableStateOf(true)
                                },
                                icon = Icons.Default.Refresh,
                                isSwitchEnabled = rememberSaveable {
                                    mutableStateOf(false)
                                },
                                onSwitchStateChange = {
                                    settingsScreenVM.refreshAllLinksImagesAndTitles()
                                })
                        )
                    }
                }
            }

            item(key = "JsoupUserAgent") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 25.dp, end = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProvideTextStyle(value = TextStyle(fontFamily = fonts)) {
                        OutlinedTextField(
                            supportingText = {
                                Text(
                                    text = stringResource(id = R.string.user_agent_desc),
                                    style = MaterialTheme.typography.titleSmall,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(
                                        top = 5.dp,
                                        bottom = 5.dp
                                    )
                                )
                            },
                            value = jsoupStringAgent.value,
                            onValueChange = {
                                jsoupStringAgent.value = it
                            },
                            readOnly = isReadOnlyTextFieldForUserAgent.value,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .focusRequester(focusRequester),
                            label = {
                                Text(
                                    text = stringResource(id = R.string.user_agent),
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledTonalIconToggleButton(
                            checked = !isReadOnlyTextFieldForUserAgent.value,
                            onCheckedChange = {
                                isReadOnlyTextFieldForUserAgent.value =
                                    !isReadOnlyTextFieldForUserAgent.value
                                if (!isReadOnlyTextFieldForUserAgent.value) {
                                    focusRequester.requestFocus()
                                } else {
                                    focusRequester.freeFocus()
                                }
                                if (isReadOnlyTextFieldForUserAgent.value) {
                                    SettingsScreenVM.Settings.changeSettingPreferenceValue(
                                        stringPreferencesKey(SettingsScreenVM.SettingsPreferences.JSOUP_USER_AGENT.name),
                                        context.dataStore,
                                        jsoupStringAgent.value
                                    )
                                    SettingsScreenVM.Settings.jsoupUserAgent.value =
                                        jsoupStringAgent.value
                                }
                            }) {
                            Icon(
                                imageVector = if (isReadOnlyTextFieldForUserAgent.value) Icons.Default.Edit else Icons.Default.Check,
                                contentDescription = ""
                            )
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        FilledTonalIconButton(onClick = {
                            SettingsScreenVM.Settings.changeSettingPreferenceValue(
                                stringPreferencesKey(SettingsScreenVM.SettingsPreferences.JSOUP_USER_AGENT.name),
                                context.dataStore,
                                "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:125.0) Gecko/20100101 Firefox/125.0"
                            )
                            SettingsScreenVM.Settings.jsoupUserAgent.value =
                                "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:125.0) Gecko/20100101 Firefox/125.0"
                        }) {
                            Icon(
                                imageVector = Icons.Default.Restore,
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .animateContentSize()
                ) {
                    if (SettingsScreenVM.isAnyRefreshingTaskGoingOn.value) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            HorizontalDivider(
                                Modifier.padding(
                                    start = 15.dp,
                                    end = 15.dp,
                                    bottom = 15.dp
                                ),
                                color = DividerDefaults.color.copy(0.5f)
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                            Text(
                                text = stringResource(id = R.string.refreshing_links),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(
                                    start = 15.dp,
                                    end = 15.dp
                                )
                            )
                            if (RefreshLinksWorker.totalLinksCount.intValue != 0) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 15.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    LinearProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth(0.85f),
                                        progress = {
                                            if (!(successfulRefreshLinksCount.value.toFloat() / RefreshLinksWorker.totalLinksCount.intValue.toFloat()).isNaN() && successfulRefreshLinksCount.value.toFloat() < RefreshLinksWorker.totalLinksCount.intValue.toFloat()) {
                                                successfulRefreshLinksCount.value.toFloat() / RefreshLinksWorker.totalLinksCount.intValue.toFloat()
                                            } else {
                                                0f
                                            }
                                        }
                                    )
                                    IconButton(onClick = {
                                        settingsScreenVM.cancelRefreshAllLinksImagesAndTitlesWork()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = ""
                                        )
                                    }
                                }
                            }
                            if (successfulRefreshLinkCount.value == 0 && RefreshLinksWorker.totalLinksCount.intValue == 0) {
                                Spacer(modifier = Modifier.height(15.dp))
                            }
                            Text(
                                text = if (successfulRefreshLinkCount.value == 0 && RefreshLinksWorker.totalLinksCount.intValue == 0) stringResource(
                                    id = R.string.work_manager_desc
                                ) else "${successfulRefreshLinkCount.value} " + stringResource(
                                    id = R.string.of
                                ) + " ${RefreshLinksWorker.totalLinksCount.intValue} " + stringResource(
                                    id = R.string.links_refreshed
                                ),
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(
                                    start = 15.dp,
                                    end = 15.dp
                                ),
                                lineHeight = 18.sp
                            )
                            Card(
                                border = BorderStroke(
                                    1.dp,
                                    contentColorFor(MaterialTheme.colorScheme.surface)
                                ),
                                colors = CardDefaults.cardColors(containerColor = AlertDialogDefaults.containerColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 15.dp,
                                        end = 15.dp,
                                        top = 20.dp
                                    )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .padding(
                                            top = 10.dp, bottom = 10.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(
                                                start = 10.dp, end = 10.dp
                                            )
                                    )
                                    Text(
                                        text = stringResource(id = R.string.refreshing_links_info),
                                        style = MaterialTheme.typography.titleSmall,
                                        lineHeight = 18.sp,
                                        modifier = Modifier.padding(end = 15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}