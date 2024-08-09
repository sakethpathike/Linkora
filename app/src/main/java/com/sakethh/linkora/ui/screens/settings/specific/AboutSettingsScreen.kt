package com.sakethh.linkora.ui.screens.settings.specific

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SystemUpdateAlt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.GetApp
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sakethh.linkora.R
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.screens.CustomWebTab
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM.Settings.dataStore
import com.sakethh.linkora.ui.screens.settings.SettingsUIElement
import com.sakethh.linkora.ui.screens.settings.composables.RegularSettingComponent
import com.sakethh.linkora.ui.screens.settings.composables.SettingsAppInfoComponent
import com.sakethh.linkora.ui.screens.settings.composables.SettingsNewVersionCheckerDialogBox
import com.sakethh.linkora.ui.screens.settings.composables.SettingsNewVersionUpdateBtmContent
import com.sakethh.linkora.ui.screens.settings.composables.SpecificSettingsScreenTopAppBar
import com.sakethh.linkora.utils.isNetworkAvailable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutSettingsScreen(
    navController: NavController,
    settingsScreenVM: SettingsScreenVM,
    customWebTab: CustomWebTab
) {
    val coroutineScope = rememberCoroutineScope()
    val btmModalSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shouldVersionCheckerDialogAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val uriHandler = LocalUriHandler.current
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
    val shouldBtmModalSheetBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    SpecificSettingsScreenTopAppBar(
        topAppBarText = stringResource(id = R.string.about),
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
            item(key = "settingsCard") {
                Row {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(start = 15.dp)
                            .alignByBaseline()
                    )
                    Text(
                        text = SettingsScreenVM.APP_VERSION_NAME,
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 12.sp,
                        modifier = Modifier.alignByBaseline()
                    )
                }
                if (!SettingsScreenVM.Settings.isAutoCheckUpdatesEnabled.value && !SettingsScreenVM.Settings.isOnLatestUpdate.value && isNetworkAvailable(
                        context
                    )
                ) {
                    SettingsAppInfoComponent(hasDescription = false,
                        description = "",
                        icon = Icons.Outlined.Refresh,
                        title = stringResource(id = R.string.check_for_latest_version),
                        onClick = {
                            shouldVersionCheckerDialogAppear.value = true
                            if (isNetworkAvailable(context)) {
                                settingsScreenVM.latestAppVersionRetriever {
                                    shouldVersionCheckerDialogAppear.value = false
                                    if (SettingsScreenVM.APP_VERSION_NAME != SettingsScreenVM.latestReleaseInfoFromGitHubReleases.value.releaseName) {
                                        shouldBtmModalSheetBeVisible.value = true
                                        SettingsScreenVM.Settings.isOnLatestUpdate.value =
                                            false
                                    } else {
                                        SettingsScreenVM.Settings.isOnLatestUpdate.value =
                                            true
                                    }
                                }
                            } else {
                                shouldVersionCheckerDialogAppear.value = false
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.network_error),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                } else if (SettingsScreenVM.Settings.isAutoCheckUpdatesEnabled.value && !SettingsScreenVM.Settings.isOnLatestUpdate.value && isNetworkAvailable(
                        context
                    )
                ) {
                    if (SettingsScreenVM.latestReleaseInfoFromGitHubReleases.collectAsStateWithLifecycle().value.releaseName == "") LaunchedEffect(
                        key1 = Unit
                    ) {
                        settingsScreenVM.latestAppVersionRetriever { }
                    }
                    SettingsAppInfoComponent(hasDescription = false,
                        description = "",
                        icon = Icons.Outlined.GetApp,
                        title = "${SettingsScreenVM.latestReleaseInfoFromGitHubReleases.collectAsStateWithLifecycle().value.releaseName} " + stringResource(
                            id = R.string.is_now_available
                        ),
                        onClick = {
                            shouldVersionCheckerDialogAppear.value = true
                            if (isNetworkAvailable(context)) {
                                if (SettingsScreenVM.latestReleaseInfoFromGitHubReleases.value.releaseName == "") {
                                    settingsScreenVM.latestAppVersionRetriever { }
                                }
                                shouldVersionCheckerDialogAppear.value = false
                                if (SettingsScreenVM.APP_VERSION_NAME != SettingsScreenVM.latestReleaseInfoFromGitHubReleases.value.releaseName) {
                                    shouldBtmModalSheetBeVisible.value = true
                                    SettingsScreenVM.Settings.isOnLatestUpdate.value =
                                        false
                                } else {
                                    SettingsScreenVM.Settings.isOnLatestUpdate.value =
                                        true
                                }
                            } else {
                                shouldVersionCheckerDialogAppear.value = false
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.network_error),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                } else {
                    if (isNetworkAvailable(context)) {
                        Card(
                            border = BorderStroke(
                                1.dp, contentColorFor(MaterialTheme.colorScheme.surface)
                            ),
                            colors = CardDefaults.cardColors(containerColor = AlertDialogDefaults.containerColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 15.dp, end = 15.dp, top = 15.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(
                                        top = 10.dp, bottom = 10.dp
                                    ), verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.padding(
                                            start = 10.dp, end = 10.dp
                                        )
                                    )
                                }
                                Text(
                                    text = stringResource(id = R.string.you_are_using_latest_version_of_linkora),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 14.sp,
                                    lineHeight = 18.sp,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.padding(end = 15.dp)
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(20.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline
                )
                SettingsAppInfoComponent(
                    description = stringResource(id = R.string.github_desc),
                    icon = null,
                    usingLocalIcon = true,
                    title = stringResource(id = R.string.github),
                    localIcon = R.drawable.github_logo,
                    onClick = {
                        customWebTab.openInWeb(
                            recentlyVisitedData = RecentlyVisited(
                                title = "Linkora on Github",
                                webURL = "https://www.github.com/sakethpathike/Linkora",
                                baseURL = "github.com",
                                imgURL = "it.imgURL",
                                infoForSaving = "Linkora on Github"
                            ),
                            context = context,
                            uriHandler = uriHandler,
                            forceOpenInExternalBrowser = false
                        )
                    })

                HorizontalDivider(
                    modifier = Modifier.padding(20.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.follow))
                        withStyle(SpanStyle(fontWeight = SemiBold)) {
                            append(" @LinkoraApp ")
                        }
                        append(stringResource(id = R.string.twitter_desc))
                    }, style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                    lineHeight = 20.sp,
                    modifier = Modifier
                        .padding(start = 15.dp, end = 15.dp)
                )
                SettingsAppInfoComponent(
                    description = "",
                    icon = null,
                    hasDescription = false,
                    usingLocalIcon = true,
                    localIcon = R.drawable.twitter_logo,
                    title = stringResource(id = R.string.twitter),
                    onClick = {
                        customWebTab.openInWeb(
                            recentlyVisitedData = RecentlyVisited(
                                title = "Linkora on Twitter",
                                webURL = "https://www.twitter.com/LinkoraApp",
                                baseURL = "twitter.com",
                                imgURL = "it.imgURL",
                                infoForSaving = "Linkora on Twitter"
                            ),
                            context = context,
                            uriHandler = uriHandler,
                            forceOpenInExternalBrowser = false
                        )
                    })
                HorizontalDivider(
                    modifier = Modifier.padding(
                        start = 20.dp,
                        top = 20.dp,
                        end = 20.dp
                    ),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            item {
                RegularSettingComponent(
                    settingsUIElement = SettingsUIElement(
                        title = stringResource(id = R.string.auto_check_for_updates),
                        doesDescriptionExists = SettingsScreenVM.Settings.showDescriptionForSettingsState.value,
                        description = stringResource(id = R.string.auto_check_for_updates_desc),
                        isIconNeeded = rememberSaveable {
                            mutableStateOf(true)
                        },
                        icon = Icons.Default.SystemUpdateAlt,
                        isSwitchNeeded = true,
                        isSwitchEnabled = SettingsScreenVM.Settings.isAutoCheckUpdatesEnabled,
                        onSwitchStateChange = {
                            SettingsScreenVM.Settings.changeSettingPreferenceValue(
                                preferenceKey = booleanPreferencesKey(
                                    SettingsScreenVM.SettingsPreferences.AUTO_CHECK_UPDATES.name
                                ),
                                dataStore = context.dataStore,
                                newValue = it
                            )
                            coroutineScope.launch {
                                SettingsScreenVM.Settings.isAutoCheckUpdatesEnabled.value =
                                    SettingsScreenVM.Settings.readSettingPreferenceValue(
                                        preferenceKey = booleanPreferencesKey(
                                            SettingsScreenVM.SettingsPreferences.AUTO_CHECK_UPDATES.name
                                        ),
                                        dataStore = context.dataStore
                                    ) == true
                            }
                        })
                )
            }
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
        SettingsNewVersionCheckerDialogBox(shouldDialogBoxAppear = shouldVersionCheckerDialogAppear)
        if (shouldBtmModalSheetBeVisible.value) {
            ModalBottomSheet(sheetState = btmModalSheetState, onDismissRequest = {
                coroutineScope.launch {
                    if (btmModalSheetState.isVisible) {
                        btmModalSheetState.hide()
                    }
                }.invokeOnCompletion {
                    shouldBtmModalSheetBeVisible.value = false
                }
            }) {
                SettingsNewVersionUpdateBtmContent(
                    shouldBtmModalSheetBeVisible = shouldBtmModalSheetBeVisible,
                    modalBtmSheetState = btmModalSheetState,
                    settingsScreenVM = settingsScreenVM
                )
            }
        }
    }
}