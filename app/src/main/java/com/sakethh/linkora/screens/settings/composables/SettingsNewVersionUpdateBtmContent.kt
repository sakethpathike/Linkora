package com.sakethh.linkora.screens.settings.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.sakethh.linkora.customWebTab.openInWeb
import com.sakethh.linkora.localDB.RecentlyVisited
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
    val verticalScrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = verticalScrollState)
    ) {
        Text(
            text = "Linkora just got better, new update is available.",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 24.sp,
            textAlign = TextAlign.Start,
            lineHeight = 28.sp,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 30.dp)
        )
        VersionCardForBtmSheetContent(
            title = "version you're using",
            value = "v${SettingsScreenVM.currentAppVersion}"
        )
        if (SettingsScreenVM.currentAppVersion != SettingsScreenVM.latestAppInfoFromServer.latestStableVersion.value) {
            VersionCardForBtmSheetContent(
                title = "latest stable version which you should be using",
                value = SettingsScreenVM.latestAppInfoFromServer.latestStableVersion.value
            )
        }

        Divider(
            color = MaterialTheme.colorScheme.outline,
            thickness = 0.5.dp,
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp)
        )

        VersionCardForBtmSheetContent(
            title = "latest version which is available for usage (not stable)",
            value = "v${SettingsScreenVM.latestAppInfoFromServer.latestVersion.value}"
        )

        Divider(
            color = MaterialTheme.colorScheme.outline,
            thickness = 0.5.dp,
            modifier = Modifier.padding(20.dp)
        )
        Text(
            text = "You can find what's new in the latest release(s) and can download them from Github:)",
            style = MaterialTheme.typography.titleSmall,
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            lineHeight = 24.sp,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
        )
        if (SettingsScreenVM.currentAppVersion != SettingsScreenVM.latestAppInfoFromServer.latestStableVersion.value) {
            Button(
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
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
                                webURL = SettingsScreenVM.latestAppInfoFromServer.latestStableVersionReleaseURL.value,
                                baseURL = "github.com",
                                imgURL = "it.imgURL",
                                infoForSaving = "Linkora Stable Release on Github"
                            ),
                            context = context,
                            uriHandler = uriHandler
                        )
                    }
                }) {
                Text(
                    text = "Redirect me to latest Stable Release page",
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                )
            }
        }
        OutlinedButton(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
            shape = RoundedCornerShape(10.dp),
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
                            webURL = SettingsScreenVM.latestAppInfoFromServer.latestVersionReleaseURL.value,
                            baseURL = "github.com",
                            imgURL = "it.imgURL",
                            infoForSaving = "Linkora Release on Github"
                        ),
                        context = context,
                        uriHandler = uriHandler
                    )
                }
            }) {
            Text(
                text = "Redirect me to latest Release page",
                style = MaterialTheme.typography.titleSmall,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            )
        }
        OutlinedButton(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
            shape = RoundedCornerShape(10.dp),
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
                lineHeight = 18.sp,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(55.dp))
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