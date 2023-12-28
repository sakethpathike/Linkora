package com.sakethh.linkora.screens.settings.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.customWebTab.openInWeb
import com.sakethh.linkora.localDB.dto.RecentlyVisited
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SettingsNewVersionUpdateBtmContent(
    modalBtmSheetState: SheetState,
    shouldBtmModalSheetBeVisible: MutableState<Boolean>,
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
                text = "Linkora just got better, new update is available.",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 24.sp,
                textAlign = TextAlign.Start,
                lineHeight = 28.sp,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp)
            )
        }
        item {
            VersionCardForBtmSheetContent(
                title = "version you're using",
                value = SettingsScreenVM.appVersionValue
            )
        }
        if (SettingsScreenVM.appVersionCode < SettingsScreenVM.latestAppInfoFromServer.stableVersionCode.value) {
            item {
                VersionCardForBtmSheetContent(
                    title = "latest stable version which you should be using",
                    value = SettingsScreenVM.latestAppInfoFromServer.stableVersionValue.value
                )
            }
        }
        item {
            Divider(
                color = MaterialTheme.colorScheme.outline,
                thickness = 0.5.dp,
                modifier = Modifier.padding(
                    start = 20.dp,
                    top = 20.dp,
                    end = 20.dp,
                    bottom = if (SettingsScreenVM.appVersionCode < SettingsScreenVM.latestAppInfoFromServer.nonStableVersionCode.value) 0.dp else 20.dp
                )
            )
        }
        if (SettingsScreenVM.appVersionCode < SettingsScreenVM.latestAppInfoFromServer.nonStableVersionCode.value) {
            item {
                VersionCardForBtmSheetContent(
                    title = "latest version which is available for usage",
                    value = SettingsScreenVM.latestAppInfoFromServer.nonStableVersionValue.value
                )
            }
            item {
                Divider(
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }
        item {
            ReleaseNotesComponent(
                versionValue = SettingsScreenVM.latestAppInfoFromServer.releaseNotes.value[0].versionValue,
                versionReleaseNote = SettingsScreenVM.latestAppInfoFromServer.releaseNotes.value[0].description
            )
        }
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
        if (SettingsScreenVM.appVersionCode < SettingsScreenVM.latestAppInfoFromServer.stableVersionCode.value) {
            item {
                Button(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                        .fillMaxWidth(),
                    onClick = {
                        coroutineScope.launch {
                            if (modalBtmSheetState.isVisible) {
                                modalBtmSheetState.hide()
                            }
                        }.invokeOnCompletion {
                            shouldBtmModalSheetBeVisible.value = false
                        }
                        coroutineScope.launch {
                            openInWeb(
                                recentlyVisitedData = RecentlyVisited(
                                    title = "Linkora Stable Release on Github",
                                    webURL = SettingsScreenVM.latestAppInfoFromServer.stableVersionGithubReleaseNotesURL.value,
                                    baseURL = "github.com",
                                    imgURL = "it.imgURL",
                                    infoForSaving = "Linkora Stable Release on Github"
                                ),
                                context = context,
                                uriHandler = uriHandler, forceOpenInExternalBrowser = false
                            )
                        }
                    }) {
                    Text(
                        text = "Redirect to latest stable release page",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
        if (SettingsScreenVM.appVersionCode < SettingsScreenVM.latestAppInfoFromServer.nonStableVersionCode.value) {
            item {
                Spacer(modifier = Modifier.height(5.dp))
            }
            item {
                Button(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                    onClick = {
                        coroutineScope.launch {
                            if (modalBtmSheetState.isVisible) {
                                modalBtmSheetState.hide()
                            }
                        }.invokeOnCompletion {
                            shouldBtmModalSheetBeVisible.value = false
                        }
                        coroutineScope.launch {
                            openInWeb(
                                recentlyVisitedData = RecentlyVisited(
                                    title = "Linkora Release on Github",
                                    webURL = SettingsScreenVM.latestAppInfoFromServer.nonStableVersionGithubReleaseNotesURL.value,
                                    baseURL = "github.com",
                                    imgURL = "it.imgURL",
                                    infoForSaving = "Linkora Release on Github"
                                ),
                                context = context,
                                uriHandler = uriHandler, forceOpenInExternalBrowser = false
                            )
                        }
                    }) {
                    Text(
                        text = "Redirect to latest release page",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
        item {
            TextButton(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                onClick = {
                    coroutineScope.launch {
                        if (modalBtmSheetState.isVisible) {
                            modalBtmSheetState.hide()
                        }
                    }.invokeOnCompletion {
                        shouldBtmModalSheetBeVisible.value = false
                    }
                }) {
                Text(
                    text = "Not now",
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
        items(SettingsScreenVM.latestAppInfoFromServer.releaseNotes.value.drop(1)) {
            Divider(
                color = MaterialTheme.colorScheme.outline.copy(0.25f),
                thickness = 0.5.dp,
                modifier = Modifier.padding(20.dp)
            )
            ReleaseNotesComponent(
                versionValue = it.versionValue,
                versionReleaseNote = it.description
            )
        }
        item {
            Divider(
                color = MaterialTheme.colorScheme.outline.copy(0.25f),
                thickness = 0.5.dp,
                modifier = Modifier
                    .padding(20.dp)
                    .navigationBarsPadding()
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

@Composable
private fun ReleaseNotesComponent(versionValue: String, versionReleaseNote: String) {
    Text(
        text = "Updates in $versionValue",
        style = MaterialTheme.typography.titleMedium,
        fontSize = 20.sp,
        textAlign = TextAlign.Start,
        lineHeight = 24.sp,
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
    )

    Text(
        text = versionReleaseNote,
        style = MaterialTheme.typography.titleSmall,
        fontSize = 16.sp,
        textAlign = TextAlign.Start,
        lineHeight = 24.sp,
        modifier = Modifier.padding(start = 20.dp, end = 20.dp)
    )
}