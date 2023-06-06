package com.sakethh.linkora.screens.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.R
import com.sakethh.linkora.screens.settings.composables.SettingComponent
import com.sakethh.linkora.screens.settings.composables.SettingsAppInfoComponent
import com.sakethh.linkora.ui.theme.LinkoraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val settingsScreenVM: SettingsScreenVM = viewModel()
    val themeSectionData = settingsScreenVM.themeSection
    val generalSectionData = settingsScreenVM.generalSection
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
                            title = "Check for latest version"
                        )
                        Divider(
                            color = MaterialTheme.colorScheme.outline,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(20.dp)
                        )

                        SettingsAppInfoComponent(
                            description = "Source-code for this app is public and open-source, feel free to checkout what this app does under the hood.",
                            icon = null,
                            usingLocalIcon = true,
                            title = "Github",
                            localIcon = R.drawable.github_logo
                        )

                        Divider(
                            color = MaterialTheme.colorScheme.outline,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(20.dp)
                        )

                        SettingsAppInfoComponent(
                            description = "Follow @LinkoraApp on the bird app to get the latest information about releases and all in between about this app :)",
                            icon = null,
                            usingLocalIcon = true,
                            localIcon = R.drawable.twitter_logo,
                            title = "Twitter"
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
                    SettingComponent(settingsUIElement = settingsUIElement, data = themeSectionData)
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
    }
}