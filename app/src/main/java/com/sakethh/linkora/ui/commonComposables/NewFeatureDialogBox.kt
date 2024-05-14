package com.sakethh.linkora.ui.commonComposables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.UnfoldMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.intPreferencesKey
import com.sakethh.linkora.ui.screens.settings.composables.SettingsAppInfoComponent
import com.sakethh.linkora.ui.theme.LinkoraTheme
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM.Settings.dataStore

@PreviewLightDark
@PreviewScreenSizes
@Composable
fun NewFeatureDialogBox(isDialogBoxVisible: MutableState<Boolean> = mutableStateOf(true)) {
    val context = LocalContext.current
    if (isDialogBoxVisible.value) {
        LinkoraTheme {
            AlertDialog(onDismissRequest = { }, title = {
                Text(
                    text = SettingsScreenVM.APP_VERSION_NAME,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 22.sp,
                    lineHeight = 27.sp,
                    textAlign = TextAlign.Start
                )
            }, text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(
                                start = 10.dp, end = 10.dp, top = 10.dp
                            )
                    ) {
                        Text(text = "• ")
                        Text(
                            text = buildAnnotatedString {
                                append("A new option has been added to the ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("About")
                                }
                                append(" (")
                                appendInlineContent("aboutIcon")
                                append(") section in ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("Settings")
                                }
                                append(" (     ")
                                appendInlineContent("settingsIcon")
                                append("     ), allowing you to easily check what's new in the current release you're using.\nTo access this feature, Tap on:")
                            },
                            inlineContent = mapOf(
                                Pair("aboutIcon", InlineTextContent(
                                    Placeholder(
                                        22.sp, 22.sp, PlaceholderVerticalAlign.TextCenter
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null
                                    )
                                }),
                                Pair("settingsIcon", InlineTextContent(
                                    Placeholder(
                                        22.sp, 22.sp, PlaceholderVerticalAlign.TextCenter
                                    )
                                ) {
                                    NavigationBarItem(selected = true, onClick = { }, icon = {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = null
                                        )
                                    })
                                }),
                            ),
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 18.sp,
                            lineHeight = 28.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                    }
                    SettingsAppInfoComponent(paddingValues = PaddingValues(
                        start = 20.dp, end = 20.dp, top = 10.dp
                    ),
                        hasDescription = false,
                        description = "",
                        icon = Icons.Outlined.UnfoldMore,
                        title = "What's New",
                        onClick = {})
                    Text(
                        text = buildAnnotatedString {
                            append("in ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("About")
                            }
                            append(".")
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(start = 20.dp, top = 10.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(
                                start = 10.dp, end = 10.dp, top = 20.dp
                            )
                    ) {
                        Text(text = "• ")
                        Text(
                            text = "A few minor UI improvements have been made in the Settings Screen and the Search Screen.",
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 18.sp,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(
                                start = 10.dp, end = 10.dp, top = 20.dp
                            )
                    ) {
                        Text(text = "• ")
                        Text(
                            text = "Fixed known reported crashes.",
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 18.sp,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                    }
                }
            }, confirmButton = {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pulsateEffect(), onClick = {
                        SettingsScreenVM.Settings.changeSettingPreferenceValue(
                            intPreferencesKey(
                                SettingsScreenVM.SettingsPreferences.SAVED_APP_CODE.name
                            ), context.dataStore, SettingsScreenVM.APP_VERSION_CODE
                        )
                        isDialogBoxVisible.value = false
                    }) {
                    Text(
                        text = "Ok", style = MaterialTheme.typography.titleSmall, fontSize = 16.sp
                    )
                }
            })
        }
    }
}