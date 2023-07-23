package com.sakethh.linkora.screens.settings

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sakethh.linkora.R
import com.sakethh.linkora.customWebTab.openInWeb
import com.sakethh.linkora.localDB.CustomLocalDBDaoFunctionsDecl
import com.sakethh.linkora.localDB.RecentlyVisited
import com.sakethh.linkora.localDB.isNetworkAvailable
import com.sakethh.linkora.navigation.NavigationRoutes
import com.sakethh.linkora.screens.home.composables.DataDialogBoxType
import com.sakethh.linkora.screens.home.composables.DeleteDialogBox
import com.sakethh.linkora.screens.settings.composables.SettingComponent
import com.sakethh.linkora.screens.settings.composables.SettingsAppInfoComponent
import com.sakethh.linkora.screens.settings.composables.SettingsNewVersionCheckerDialogBox
import com.sakethh.linkora.screens.settings.composables.SettingsNewVersionUpdateBtmContent
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val settingsScreenVM: SettingsScreenVM = viewModel()
    val themeSectionData = settingsScreenVM.themeSection
    val generalSectionData = settingsScreenVM.generalSection
    val coroutineScope = rememberCoroutineScope()
    val shouldVersionCheckerDialogAppear = rememberSaveable {
        mutableStateOf(false)
    }
    val shouldBtmModalSheetBeVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val btmModalSheetState = androidx.compose.material3.rememberModalBottomSheetState()
    LinkoraTheme {
        Scaffold(topBar = {
            TopAppBar(title = {
                Text(
                    text = "Settings",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 24.sp
                )
            })
        }) {
            LazyColumn(modifier = Modifier.padding(it)) {
                item {
                    Card(
                        modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Row {
                            Text(
                                text = "Linkora",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .padding(start = 15.dp, top = 30.dp)
                                    .alignByBaseline()
                            )
                            Text(
                                text = "v${SettingsScreenVM.currentAppVersion}",
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 12.sp,
                                modifier = Modifier.alignByBaseline()
                            )
                        }
                        SettingsAppInfoComponent(
                            hasDescription = false,
                            description = "",
                            icon = Icons.Outlined.Update,
                            title = "Check for latest version",
                            onClick = {
                                shouldVersionCheckerDialogAppear.value = true
                                if (isNetworkAvailable(context)) {
                                    coroutineScope.launch {
                                        SettingsScreenVM.Settings.latestAppVersionRetriever()
                                    }.invokeOnCompletion {
                                        shouldVersionCheckerDialogAppear.value = false
                                        if (SettingsScreenVM.currentAppVersion != SettingsScreenVM.latestAppInfoFromServer.latestVersion.value || SettingsScreenVM.currentAppVersion != SettingsScreenVM.latestAppInfoFromServer.latestStableVersion.value) {
                                            shouldBtmModalSheetBeVisible.value = true
                                            coroutineScope.launch {
                                                if (!btmModalSheetState.isVisible) {
                                                    btmModalSheetState.show()
                                                }
                                            }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "you're already on latest version",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "network error, check your network connection and try again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                        Divider(
                            color = MaterialTheme.colorScheme.outline,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(20.dp)
                        )

                        SettingsAppInfoComponent(
                            description = "The source code for this app is public and open-source; feel free to check out what this app does under the hood.",
                            icon = null,
                            usingLocalIcon = true,
                            title = "Github",
                            localIcon = R.drawable.github_logo,
                            onClick = {
                                coroutineScope.launch {
                                    openInWeb(
                                        recentlyVisitedData = RecentlyVisited(
                                            title = "Linkora on Github",
                                            webURL = "https://www.github.com/sakethpathike/Linkora",
                                            baseURL = "github.com",
                                            imgURL = "it.imgURL",
                                            infoForSaving = "Linkora on Github"
                                        ),
                                        context = context,
                                        uriHandler = uriHandler
                                    )
                                }
                            }
                        )

                        Divider(
                            color = MaterialTheme.colorScheme.outline,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(20.dp)
                        )

                        SettingsAppInfoComponent(
                            description = "Follow @LinkoraApp on the bird app to get the latest information about releases and everything in between about this app.",
                            icon = null,
                            usingLocalIcon = true,
                            localIcon = R.drawable.twitter_logo,
                            title = "Twitter",
                            onClick = {
                                coroutineScope.launch {
                                    openInWeb(
                                        recentlyVisitedData = RecentlyVisited(
                                            title = "Linkora on Twitter",
                                            webURL = "https://www.twitter.com/LinkoraApp",
                                            baseURL = "twitter.com",
                                            imgURL = "it.imgURL",
                                            infoForSaving = "Linkora on Twitter"
                                        ),
                                        context = context,
                                        uriHandler = uriHandler
                                    )
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
                item {
                    Text(
                        text = "Theme",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 15.dp, top = 40.dp)
                    )
                }
                items(themeSectionData) { settingsUIElement ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && settingsUIElement.title == "Use dynamic theming") {
                        SettingComponent(
                            settingsUIElement = settingsUIElement,
                            data = themeSectionData
                        )
                    } else if (settingsUIElement.title != "Use dynamic theming") {
                        SettingComponent(
                            settingsUIElement = settingsUIElement,
                            data = themeSectionData
                        )
                    }
                }
                item {
                    Text(
                        text = "General",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 15.dp, top = 40.dp)
                    )
                }
                items(generalSectionData) { settingsUIElement ->
                    SettingComponent(
                        settingsUIElement = settingsUIElement,
                        data = generalSectionData
                    )
                }
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Privacy",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 15.dp, top = 20.dp)
                        )
                        Text(
                            text = "Every single bit of data is stored locally on your device.",
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(
                                top = 10.dp,
                                start = 15.dp,
                                bottom = 20.dp,
                                end = 15.dp
                            ),
                            lineHeight = 16.sp,
                            textAlign = TextAlign.Start,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(65.dp))
                }
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
                    modalBtmSheetState = btmModalSheetState
                )
            }
        }
        DeleteDialogBox(
            shouldDialogBoxAppear = settingsScreenVM.shouldDeleteDialogBoxAppear,
            deleteDialogBoxType = DataDialogBoxType.REMOVE_ENTIRE_DATA,
            onDeleteClick = {
                coroutineScope.launch {
                    CustomLocalDBDaoFunctionsDecl.deleteEntireLinksAndFoldersData()
                }
                Toast.makeText(
                    context,
                    "Deleted entire data from the local database",
                    Toast.LENGTH_SHORT
                ).show()
            })
    }
    BackHandler {
        if (btmModalSheetState.isVisible) {
            coroutineScope.launch {
                btmModalSheetState.hide()
            }
        } else if (SettingsScreenVM.Settings.isHomeScreenEnabled.value) {
            navController.navigate(NavigationRoutes.HOME_SCREEN.name) {
                popUpTo(0)
            }
        } else {
            navController.navigate(NavigationRoutes.COLLECTIONS_SCREEN.name) {
                popUpTo(0)
            }
        }
    }
}