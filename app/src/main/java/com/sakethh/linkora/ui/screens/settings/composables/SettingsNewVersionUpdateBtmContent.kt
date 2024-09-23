package com.sakethh.linkora.ui.screens.settings.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sakethh.linkora.LocalizedStrings.currentVersion
import com.sakethh.linkora.LocalizedStrings.latestVersion
import com.sakethh.linkora.LocalizedStrings.linkora
import com.sakethh.linkora.LocalizedStrings.newUpdateIsAvailable
import com.sakethh.linkora.LocalizedStrings.redirectToLatestReleasePage
import com.sakethh.linkora.LocalizedStrings.releasePageOnGithub
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsNewVersionUpdateBtmContent(
    modalBtmSheetState: SheetState,
    shouldBtmModalSheetBeVisible: MutableState<Boolean>, settingsScreenVM: SettingsScreenVM
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        item {
            Text(
                text = newUpdateIsAvailable.value,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 24.sp,
                textAlign = TextAlign.Start,
                lineHeight = 28.sp,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp)
            )
        }
        item {
            VersionCardForBtmSheetContent(
                title = currentVersion.value,
                value = SettingsPreference.APP_VERSION_NAME
            )
        }
        if (SettingsPreference.APP_VERSION_NAME != SettingsScreenVM.latestReleaseInfoFromGitHubReleases.value.releaseName) {
            item {
                VersionCardForBtmSheetContent(
                    title = latestVersion.value,
                    value = SettingsScreenVM.latestReleaseInfoFromGitHubReleases.collectAsStateWithLifecycle().value.releaseName
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
        item {
            Button(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
                    .fillMaxWidth()
                    .pulsateEffect(),
                onClick = {
                    coroutineScope.launch {
                        if (modalBtmSheetState.isVisible) {
                            modalBtmSheetState.hide()
                        }
                    }.invokeOnCompletion {
                        shouldBtmModalSheetBeVisible.value = false
                    }
                    coroutineScope.launch {
                        settingsScreenVM.openInWeb(
                            recentlyVisitedData = RecentlyVisited(
                                title = linkora.value + " ${SettingsScreenVM.latestReleaseInfoFromGitHubReleases.value.releaseName} " +
                                        releasePageOnGithub.value,
                                webURL = SettingsScreenVM.latestReleaseInfoFromGitHubReleases.value.releasePageURL,
                                baseURL = "github.com",
                                imgURL = "it.imgURL",
                                infoForSaving = ""
                            ),
                            context = context,
                            uriHandler = uriHandler, forceOpenInExternalBrowser = false
                        )
                    }
                }) {
                Text(
                    text = redirectToLatestReleasePage.value,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
        item {
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
fun VersionCardForBtmSheetContent(title: String, value: String) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(top = 20.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            lineHeight = 18.sp,
            modifier = Modifier.padding(20.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(start = 20.dp, bottom = 20.dp, end = 20.dp)
        )
    }
}