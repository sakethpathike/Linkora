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
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Translate
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.LocalizedStrings.checkForLatestVersion
import com.sakethh.linkora.LocalizedStrings.github
import com.sakethh.linkora.LocalizedStrings.githubDesc
import com.sakethh.linkora.LocalizedStrings.isNowAvailable
import com.sakethh.linkora.LocalizedStrings.networkError
import com.sakethh.linkora.LocalizedStrings.twitter
import com.sakethh.linkora.LocalizedStrings.youAreUsingLatestVersionOfLinkora
import com.sakethh.linkora.R
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.CustomWebTab
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.screens.settings.composables.SettingsAppInfoComponent
import com.sakethh.linkora.ui.screens.settings.composables.SettingsNewVersionCheckerDialogBox
import com.sakethh.linkora.ui.screens.settings.composables.SettingsNewVersionUpdateBtmContent
import com.sakethh.linkora.ui.screens.settings.composables.SpecificScreenScaffold
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
                    Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }
    val shouldBtmModalSheetBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val typography = MaterialTheme.typography
    SpecificScreenScaffold(
        topAppBarText = LocalizedStrings.about.value,
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
                        text = LocalizedStrings.linkora.value,
                        style =
                        remember {
                            if (SettingsPreference.preferredAppLanguageCode.value == "en") typography.bodyMedium else typography.titleSmall
                        },
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(start = 15.dp)
                            .alignByBaseline()
                    )
                    Text(
                        text = SettingsPreference.APP_VERSION_NAME,
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 12.sp,
                        modifier = Modifier.alignByBaseline()
                    )
                }
                if (!SettingsPreference.isAutoCheckUpdatesEnabled.value && !SettingsPreference.isOnLatestUpdate.value && isNetworkAvailable(
                        context
                    )
                ) {
                    SettingsAppInfoComponent(hasDescription = false,
                        description = "",
                        icon = Icons.Outlined.Refresh,
                        title = checkForLatestVersion.value,
                        onClick = {
                            shouldVersionCheckerDialogAppear.value = true
                            if (isNetworkAvailable(context)) {
                                settingsScreenVM.latestAppVersionRetriever {
                                    shouldVersionCheckerDialogAppear.value = false
                                    if (SettingsPreference.APP_VERSION_NAME != SettingsScreenVM.latestReleaseInfoFromGitHubReleases.value.releaseName) {
                                        shouldBtmModalSheetBeVisible.value = true
                                        SettingsPreference.isOnLatestUpdate.value =
                                            false
                                    } else {
                                        SettingsPreference.isOnLatestUpdate.value =
                                            true
                                    }
                                }
                            } else {
                                shouldVersionCheckerDialogAppear.value = false
                                Toast.makeText(
                                    context,
                                    networkError.value,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                } else if (SettingsPreference.isAutoCheckUpdatesEnabled.value && !SettingsPreference.isOnLatestUpdate.value && isNetworkAvailable(
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
                        title = "${SettingsScreenVM.latestReleaseInfoFromGitHubReleases.collectAsStateWithLifecycle().value.releaseName} " +
                                isNowAvailable.value,
                        onClick = {
                            shouldVersionCheckerDialogAppear.value = true
                            if (isNetworkAvailable(context)) {
                                if (SettingsScreenVM.latestReleaseInfoFromGitHubReleases.value.releaseName == "") {
                                    settingsScreenVM.latestAppVersionRetriever { }
                                }
                                shouldVersionCheckerDialogAppear.value = false
                                if (SettingsPreference.APP_VERSION_NAME != SettingsScreenVM.latestReleaseInfoFromGitHubReleases.value.releaseName) {
                                    shouldBtmModalSheetBeVisible.value = true
                                    SettingsPreference.isOnLatestUpdate.value =
                                        false
                                } else {
                                    SettingsPreference.isOnLatestUpdate.value =
                                        true
                                }
                            } else {
                                shouldVersionCheckerDialogAppear.value = false
                                Toast.makeText(
                                    context,
                                    networkError.value,
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
                                    text = youAreUsingLatestVersionOfLinkora.value,
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

                Text(
                    text = LocalizedStrings.socials.value,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = 15.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                SettingsAppInfoComponent(
                    description = "",
                    icon = null,
                    hasDescription = false,
                    usingLocalIcon = true,
                    localIcon = R.drawable.twitter_logo,
                    title = twitter.value,
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
                /*HorizontalDivider(
                    modifier = Modifier.padding(
                        start = 20.dp,
                        top = 20.dp,
                        end = 20.dp
                    ),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline
                )*/
                SettingsAppInfoComponent(
                    description = "",
                    icon = null,
                    hasDescription = false,
                    usingLocalIcon = true,
                    localIcon = R.drawable.discord_logo,
                    title = LocalizedStrings.discord.value,
                    onClick = {
                        customWebTab.openInWeb(
                            recentlyVisitedData = RecentlyVisited(
                                title = "Linkora on Discord",
                                webURL = "https://discord.gg/ZDBXNtv8MD",
                                baseURL = "discord.gg",
                                imgURL = "https://cdn.discordapp.com/assets/og_img_discord_home.png",
                                infoForSaving = "Linkora on Discord"
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
                Text(
                    text = LocalizedStrings.development.value,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = 15.dp, bottom = 15.dp, top = 20.dp),
                    color = MaterialTheme.colorScheme.primary
                )

                SettingsAppInfoComponent(
                    description = githubDesc.value,
                    icon = null,
                    usingLocalIcon = true,
                    title = github.value,
                    localIcon = R.drawable.github_logo,
                    onClick = {
                        customWebTab.openInWeb(
                            recentlyVisitedData = RecentlyVisited(
                                title = "Linkora on Github",
                                webURL = "https://www.github.com/sakethpathike/Linkora",
                                baseURL = "github.com",
                                imgURL = "https://repository-images.githubusercontent.com/648784316/d178070a-d517-47b5-bd0c-232568df2f77",
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

                SettingsAppInfoComponent(
                    description = LocalizedStrings.haveASuggestionCreateAnIssueOnGithubToImproveLinkora.value,
                    icon = Icons.Default.Construction,
                    usingLocalIcon = false,
                    title = LocalizedStrings.openAGithubIssue.value,
                    localIcon = R.drawable.github_logo,
                    onClick = {
                        customWebTab.openInWeb(
                            recentlyVisitedData = RecentlyVisited(
                                title = "Issues · sakethpathike/Linkora",
                                webURL = "https://github.com/sakethpathike/Linkora/issues/new",
                                baseURL = "github.com",
                                imgURL = "https://repository-images.githubusercontent.com/648784316/d178070a-d517-47b5-bd0c-232568df2f77",
                                infoForSaving = "Issues · sakethpathike/Linkora on Github"
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

                SettingsAppInfoComponent(
                    description = LocalizedStrings.trackRecentChangesAndUpdatesToLinkora.value,
                    icon = Icons.Default.TrackChanges,
                    usingLocalIcon = false,
                    title = LocalizedStrings.changelog.value,
                    localIcon = R.drawable.github_logo,
                    onClick = {
                        customWebTab.openInWeb(
                            recentlyVisitedData = RecentlyVisited(
                                title = "Releases · sakethpathike/Linkora",
                                webURL = "https://github.com/sakethpathike/Linkora/releases",
                                baseURL = "github.com",
                                imgURL = "https://repository-images.githubusercontent.com/648784316/d178070a-d517-47b5-bd0c-232568df2f77",
                                infoForSaving = "Releases · sakethpathike/Linkora on Github"
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

                SettingsAppInfoComponent(
                    description = LocalizedStrings.helpMakeLinkoraAccessibleInMoreLanguagesByContributingTranslations.value,
                    icon = Icons.Default.Translate,
                    usingLocalIcon = false,
                    title = LocalizedStrings.helpTranslateLinkora.value,
                    localIcon = R.drawable.github_logo,
                    onClick = {
                        customWebTab.openInWeb(
                            recentlyVisitedData = RecentlyVisited(
                                title = "LinkoraLocalizationServer/README.md at master · sakethpathike/LinkoraLocalizationServer",
                                webURL = "https://github.com/sakethpathike/LinkoraLocalizationServer/blob/master/README.md",
                                baseURL = "github.com",
                                imgURL = "https://opengraph.githubassets.com/7db25d40fd9a2a5df86d4efef04c861d53d4d4761b83d2674063980e2996f805/sakethpathike/LinkoraLocalizationServer",
                                infoForSaving = ""
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