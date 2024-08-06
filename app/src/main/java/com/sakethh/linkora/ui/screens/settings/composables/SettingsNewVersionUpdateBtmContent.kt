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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sakethh.linkora.R
import com.sakethh.linkora.data.local.RecentlyVisited
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
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
                text = stringResource(id = R.string.new_update_is_available),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 24.sp,
                textAlign = TextAlign.Start,
                lineHeight = 28.sp,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp)
            )
        }
        item {
            VersionCardForBtmSheetContent(
                title = stringResource(id = R.string.current_version),
                value = SettingsScreenVM.APP_VERSION_NAME
            )
        }
        if (SettingsScreenVM.APP_VERSION_NAME != SettingsScreenVM.latestReleaseInfoFromGitHubReleases.value.releaseName) {
            item {
                VersionCardForBtmSheetContent(
                    title = stringResource(id = R.string.latest_version),
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
                                title = context.getString(R.string.app_name) + " ${SettingsScreenVM.latestReleaseInfoFromGitHubReleases.value.releaseName} " + context.getString(
                                    R.string.release_page_on_gitHub
                                ),
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
                    text = stringResource(id = R.string.redirect_to_latest_release_page),
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
            Spacer(modifier = Modifier.height(5.dp))
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
                    SettingsScreenVM.latestReleaseInfoFromGitHubReleases.value.assets.find {
                        it.directDownloadURL.endsWith(".apk")
                    }?.directDownloadURL?.let { uriHandler.openUri(it) }
                }) {
                Text(
                    text = stringResource(id = R.string.download),
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center, lineHeight = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
        item {
            HorizontalDivider(
                modifier = Modifier
                    .padding(20.dp)
                    .navigationBarsPadding(),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(0.25f)
            )
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