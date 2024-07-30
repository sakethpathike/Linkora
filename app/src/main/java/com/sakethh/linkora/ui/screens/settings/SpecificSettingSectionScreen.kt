package com.sakethh.linkora.ui.screens.settings

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.SystemUpdateAlt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.GetApp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sakethh.linkora.R
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.ui.commonComposables.DataDialogBoxType
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBox
import com.sakethh.linkora.ui.commonComposables.DeleteDialogBoxParam
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.screens.CustomWebTab
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM.Settings.dataStore
import com.sakethh.linkora.ui.screens.settings.composables.ImportConflictBtmSheet
import com.sakethh.linkora.ui.screens.settings.composables.ImportExceptionDialogBox
import com.sakethh.linkora.ui.screens.settings.composables.PermissionDialog
import com.sakethh.linkora.ui.screens.settings.composables.RegularSettingComponent
import com.sakethh.linkora.ui.screens.settings.composables.SettingsAppInfoComponent
import com.sakethh.linkora.ui.screens.settings.composables.SettingsNewVersionCheckerDialogBox
import com.sakethh.linkora.ui.screens.settings.composables.SettingsNewVersionUpdateBtmContent
import com.sakethh.linkora.ui.theme.LinkoraTheme
import com.sakethh.linkora.ui.theme.fonts
import com.sakethh.linkora.utils.isNetworkAvailable
import com.sakethh.linkora.worker.RefreshLinksWorker
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecificSettingSectionScreen(navController: NavController, customWebTab: CustomWebTab) {
    val importModalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val topAppBarScrollState = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    val isPermissionDialogBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val isImportExceptionBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val isImportConflictBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val isNewFeatureDialogBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val successfulRefreshLinkCount =
        RefreshLinksWorker.successfulRefreshLinksCount.collectAsStateWithLifecycle()
    val settingsScreenVM: SettingsScreenVM = hiltViewModel()
    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            val file = createTempFile()
            uri?.let { context.contentResolver.openInputStream(it) }.use { input ->
                file.outputStream().use { output ->
                    input?.copyTo(output)
                }
            }
            settingsScreenVM.importData(
                settingsScreenVM.exceptionType,
                file.readText(),
                isImportExceptionBoxVisible
            )
            file.delete()
        }
    val runtimePermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            isPermissionDialogBoxVisible.value = !it
        })

    val shouldVersionCheckerDialogAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldBtmModalSheetBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()
    val btmModalSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val topAppBarText = when (SettingsScreenVM.currentSelectedSettingSection.value) {
        SettingsSections.THEME -> "Theme"
        SettingsSections.GENERAL -> "General"
        SettingsSections.DATA -> "Data"
        SettingsSections.PRIVACY -> "Privacy"
        SettingsSections.ABOUT -> "About"
        SettingsSections.ACKNOWLEDGMENT -> "Acknowledgments"
    }
    val jsoupStringAgent = SettingsScreenVM.Settings.jsoupUserAgent
    val isReadOnlyTextFieldForUserAgent = rememberSaveable {
        mutableStateOf(true)
    }
    val focusRequester = remember { FocusRequester() }
    val successfulRefreshLinksCount =
        RefreshLinksWorker.successfulRefreshLinksCount.collectAsStateWithLifecycle()
    LinkoraTheme {
        Scaffold(topBar = {
            Column {
                LargeTopAppBar(navigationIcon = {
                    IconButton(modifier = Modifier.pulsateEffect(), onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }, scrollBehavior = topAppBarScrollState, title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = topAppBarText,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 18.sp
                        )
                        if (SettingsScreenVM.currentSelectedSettingSection.value == SettingsSections.DATA) {
                            Text(
                                text = "Beta",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 15.sp,
                                modifier = Modifier
                                    .padding(
                                        start = 5.dp
                                    )
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(5.dp)
                                    )
                                    .padding(5.dp)
                            )
                        }
                    }
                })
            }
        }) { it ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .nestedScroll(topAppBarScrollState.nestedScrollConnection)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                if (SettingsScreenVM.currentSelectedSettingSection.value != SettingsSections.DATA) {
                    item {
                        Spacer(modifier = Modifier)
                    }
                }
                if (SettingsScreenVM.currentSelectedSettingSection.value == SettingsSections.DATA) {
                    item {
                        Text(
                            text = "The Import feature is polished, not perfect. While it's much better, surprises might still pop up during import. However, exporting should be no problem.",
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(start = 15.dp, end = 15.dp)
                        )
                    }
                }
                if (SettingsScreenVM.currentSelectedSettingSection.value == SettingsSections.ACKNOWLEDGMENT) {
                    item {
                        Text(
                            text = "Linkora wouldn't be possible without the following open-source software, libraries.",
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(start = 15.dp, end = 15.dp)
                        )
                    }
                }
                when (SettingsScreenVM.currentSelectedSettingSection.value) {
                    SettingsSections.THEME -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !SettingsScreenVM.Settings.shouldDarkThemeBeEnabled.value) {
                            item(key = "Follow System Theme") {
                                RegularSettingComponent(
                                    settingsUIElement = SettingsUIElement(title = "Follow System Theme",
                                        doesDescriptionExists = false,
                                        isSwitchNeeded = true,
                                        description = null,
                                        isSwitchEnabled = SettingsScreenVM.Settings.shouldFollowSystemTheme,
                                        onSwitchStateChange = {
                                            SettingsScreenVM.Settings.changeSettingPreferenceValue(
                                                preferenceKey = booleanPreferencesKey(
                                                    SettingsScreenVM.SettingsPreferences.FOLLOW_SYSTEM_THEME.name
                                                ),
                                                dataStore = context.dataStore,
                                                newValue = !SettingsScreenVM.Settings.shouldFollowSystemTheme.value
                                            )
                                            SettingsScreenVM.Settings.shouldFollowSystemTheme.value =
                                                !SettingsScreenVM.Settings.shouldFollowSystemTheme.value
                                        }, isIconNeeded = remember {
                                            mutableStateOf(false)
                                        })
                                )
                            }
                        }
                        if (!SettingsScreenVM.Settings.shouldFollowSystemTheme.value) {
                            item(key = "Use Dark Mode") {
                                RegularSettingComponent(
                                    settingsUIElement = SettingsUIElement(title = "Use Dark Mode",
                                        doesDescriptionExists = false,
                                        description = null,
                                        isSwitchNeeded = true,
                                        isSwitchEnabled = SettingsScreenVM.Settings.shouldDarkThemeBeEnabled,
                                        onSwitchStateChange = {
                                            SettingsScreenVM.Settings.changeSettingPreferenceValue(
                                                preferenceKey = booleanPreferencesKey(
                                                    SettingsScreenVM.SettingsPreferences.DARK_THEME.name
                                                ),
                                                dataStore = context.dataStore,
                                                newValue = !SettingsScreenVM.Settings.shouldDarkThemeBeEnabled.value
                                            )
                                            SettingsScreenVM.Settings.shouldDarkThemeBeEnabled.value =
                                                !SettingsScreenVM.Settings.shouldDarkThemeBeEnabled.value
                                        }, isIconNeeded = remember {
                                            mutableStateOf(false)
                                        })
                                )
                            }
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            item(key = "Use dynamic theming") {
                                RegularSettingComponent(
                                    settingsUIElement = SettingsUIElement(title = "Use dynamic theming",
                                        doesDescriptionExists = true,
                                        description = "Change colour themes within the app based on your wallpaper.",
                                        isSwitchNeeded = true,
                                        isSwitchEnabled = SettingsScreenVM.Settings.shouldFollowDynamicTheming,
                                        onSwitchStateChange = {
                                            SettingsScreenVM.Settings.changeSettingPreferenceValue(
                                                preferenceKey = booleanPreferencesKey(
                                                    SettingsScreenVM.SettingsPreferences.DYNAMIC_THEMING.name
                                                ),
                                                dataStore = context.dataStore,
                                                newValue = !SettingsScreenVM.Settings.shouldFollowDynamicTheming.value
                                            )
                                            SettingsScreenVM.Settings.shouldFollowDynamicTheming.value =
                                                !SettingsScreenVM.Settings.shouldFollowDynamicTheming.value
                                        }, isIconNeeded = remember {
                                            mutableStateOf(false)
                                        })
                                )
                            }
                        }
                    }

                    SettingsSections.ABOUT -> {
                        item(key = "settingsCard") {
                            Row {
                                Text(
                                    text = "Linkora",
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
                                    title = "Check for latest version",
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
                                                "network error, check your network connection and try again",
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
                                    title = "${SettingsScreenVM.latestReleaseInfoFromGitHubReleases.collectAsStateWithLifecycle().value.releaseName} is now available.",
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
                                                "network error, check your network connection and try again",
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
                                                text = "You are using latest version of Linkora.",
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
                            SettingsAppInfoComponent(description = "The source code for Linkora is public and open-source; feel free to check out what Linkora does under the hood.",
                                icon = null,
                                usingLocalIcon = true,
                                title = "Github",
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
                                    append("Follow")
                                    withStyle(SpanStyle(fontWeight = SemiBold)) {
                                        append(" @LinkoraApp ")
                                    }
                                    append("on the bird app to get the latest information about releases and everything in between about Linkora.")
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
                                title = "Twitter",
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
                                settingsUIElement = SettingsUIElement(title = "Auto-Check for Updates",
                                    doesDescriptionExists = SettingsScreenVM.Settings.showDescriptionForSettingsState.value,
                                    description = "If this is enabled, Linkora automatically checks for updates when you open the app. If a new update is available, it notifies you with a toast message. If this setting is disabled, manual checks for the latest version can be done from the top of this screen.",
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
                    }

                    else -> {
                        items(
                            when (SettingsScreenVM.currentSelectedSettingSection.value) {
                                SettingsSections.THEME -> emptyList()
                                SettingsSections.GENERAL -> settingsScreenVM.generalSection(context)
                                SettingsSections.DATA -> settingsScreenVM.dataSection(
                                    runtimePermission,
                                    context,
                                    isDialogBoxVisible = isPermissionDialogBoxVisible,
                                    activityResultLauncher = activityResultLauncher,
                                    importModalBtmSheetState = isImportConflictBoxVisible
                                )

                                SettingsSections.PRIVACY -> listOf(
                                    settingsScreenVM.privacySection(
                                        context
                                    )
                                )

                                SettingsSections.ABOUT -> emptyList()
                                SettingsSections.ACKNOWLEDGMENT -> settingsScreenVM.acknowledgmentsSection
                            }
                        ) {
                            RegularSettingComponent(
                                settingsUIElement = it
                            )
                        }
                        if (SettingsScreenVM.currentSelectedSettingSection.value == SettingsSections.GENERAL) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .animateContentSize()
                                ) {
                                    if (!SettingsScreenVM.isAnyRefreshingTaskGoingOn.value) {
                                        RegularSettingComponent(
                                            settingsUIElement = SettingsUIElement(title = "Refresh All Links' Titles and Images",
                                                doesDescriptionExists = true,
                                                description = "Manually entered titles will be replaced with detected titles.",
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
                        }
                        if (SettingsScreenVM.currentSelectedSettingSection.value == SettingsSections.GENERAL) {
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
                                                    text = "Helps detect images and titles from web page meta tags; different agent strings can change the detected data.",
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
                                                    text = "User Agent",
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
                        }
                        if (SettingsScreenVM.currentSelectedSettingSection.value == SettingsSections.GENERAL) {
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
                                                text = "Refreshing links...",
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
                                                        settingsScreenVM.cancelRefreshAllLinksImagesAndTitlesWork(
                                                            context
                                                        )
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
                                                text = if (successfulRefreshLinkCount.value == 0 && RefreshLinksWorker.totalLinksCount.intValue == 0) "Work Manager is scheduling the link refresh. It will continue shortly." else "${successfulRefreshLinkCount.value} of ${RefreshLinksWorker.totalLinksCount.intValue} links refreshed",
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
                                                        text = "Closing Linkora won't interrupt link refreshing, but newly added links might not be processed.",
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
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        PermissionDialog(isVisible = isPermissionDialogBoxVisible,
            permissionDenied = when (ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )) {
                PackageManager.PERMISSION_GRANTED -> false
                else -> true
            },
            onClick = {
                context as Activity
                context.openApplicationSettings()
            })
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
        ImportExceptionDialogBox(
            isVisible = isImportExceptionBoxVisible,
            onClick = { activityResultLauncher.launch("text/*") },
            exceptionType = settingsScreenVM.exceptionType
        )

        ImportConflictBtmSheet(isUIVisible = isImportConflictBoxVisible,
            modalBottomSheetState = importModalBottomSheetState,
            onMergeClick = {
                activityResultLauncher.launch("text/*")
            },
            onDeleteExistingDataClick = {
                settingsScreenVM.deleteEntireLinksAndFoldersData(onTaskCompleted = {
                    activityResultLauncher.launch("text/*")
                })
            },
            onDataExportClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    settingsScreenVM.exportDataToAFile(
                        context = context,
                        isDialogBoxVisible = isPermissionDialogBoxVisible,
                        runtimePermission = runtimePermission
                    )
                } else {
                    when (ContextCompat.checkSelfPermission(
                        context, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )) {
                        PackageManager.PERMISSION_GRANTED -> {
                            settingsScreenVM.exportDataToAFile(
                                context = context,
                                isDialogBoxVisible = isPermissionDialogBoxVisible,
                                runtimePermission = runtimePermission
                            )
                            Toast.makeText(
                                context, "Successfully Exported", Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            runtimePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            Toast.makeText(
                                context, "Permission required to write the data", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            },
            onExportAndThenImportClick = {
                fun exportDataToAFile() {
                    settingsScreenVM.exportDataToAFile(
                        context = context,
                        isDialogBoxVisible = isPermissionDialogBoxVisible,
                        runtimePermission = runtimePermission
                    )
                    Toast.makeText(
                        context, "Successfully Exported", Toast.LENGTH_SHORT
                    ).show()
                    settingsScreenVM.deleteEntireLinksAndFoldersData(onTaskCompleted = {
                        activityResultLauncher.launch("text/*")
                    })
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    exportDataToAFile()
                } else {
                    when (ContextCompat.checkSelfPermission(
                        context, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )) {
                        PackageManager.PERMISSION_GRANTED -> {
                            exportDataToAFile()
                        }

                        else -> {
                            runtimePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            Toast.makeText(
                                context, "Permission required to write the data", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            })
        DeleteDialogBox(
            DeleteDialogBoxParam(shouldDialogBoxAppear = settingsScreenVM.shouldDeleteDialogBoxAppear,
                deleteDialogBoxType = DataDialogBoxType.REMOVE_ENTIRE_DATA,
                onDeleteClick = {
                    settingsScreenVM.deleteEntireLinksAndFoldersData()
                    Toast.makeText(
                        context, "Deleted entire data from the local database", Toast.LENGTH_SHORT
                    ).show()
                })
        )

        if (settingsScreenVM.dataRefreshState.intValue != 1) {
            AlertDialog(onDismissRequest = { }, confirmButton = { }, text = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }, title = {
                Text(
                    text = "Refreshing",
                    style = MaterialTheme.typography.titleSmall,
                )
            })
        }
    }
}

fun Activity.openApplicationSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null)
    ).also {
        startActivity(it)
    }
}