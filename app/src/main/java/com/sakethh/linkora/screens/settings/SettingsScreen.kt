package com.sakethh.linkora.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.screens.settings.composables.SettingComponent
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
                                    .padding(start = 20.dp, top = 30.dp)
                                    .alignByBaseline()
                            )
                            Text(
                                text = "v0.0.1",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 18.sp,
                                modifier = Modifier.alignByBaseline()
                            )

                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { },
                                modifier = Modifier.padding(start = 20.dp, top = 40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = null,
                                    modifier = Modifier.padding(
                                        top = 15.dp, start = 35.dp, bottom = 15.dp, end = 35.dp
                                    )
                                )
                                Text(
                                    text = "Github",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 18.sp,
                                    modifier = Modifier
                                        .padding(start = 20.dp, top = 30.dp)
                                        .alignByBaseline()
                                )
                            }
                            Button(
                                onClick = { }, modifier = Modifier.padding(end = 20.dp, top = 40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = null,
                                    modifier = Modifier.padding(
                                        top = 15.dp, start = 35.dp, bottom = 15.dp, end = 35.dp
                                    )
                                )
                            }
                        }
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(
                            text = "Privacy",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 15.dp, top = 40.dp)
                        )
                        Text(
                            text = "Every single bit of data is stored locally on your device.",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 47.sp,
                            modifier = Modifier.padding(start = 15.dp, bottom = 40.dp, end = 15.dp),
                            textAlign = TextAlign.Start,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}