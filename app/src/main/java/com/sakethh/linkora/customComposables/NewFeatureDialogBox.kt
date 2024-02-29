package com.sakethh.linkora.customComposables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.screens.settings.SettingsScreenVM.Settings.dataStore
import kotlinx.coroutines.launch

@Composable
fun NewFeatureDialogBox(isDialogBoxVisible: MutableState<Boolean>) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    if (isDialogBoxVisible.value) {
        AlertDialog(onDismissRequest = { }, title = {
            Text(
                text = buildAnnotatedString {
                    append("Introducing ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Shelf")
                    }
                    append("!")
                }, style = MaterialTheme.typography.titleMedium,
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
                            10.dp
                        )
                ) {
                    Text(text = "• ")
                    Text(
                        text = buildAnnotatedString {
                            append("From now on, you can manage the folders you want in each individual section, known as the ")
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Shelf")
                            }
                            append(".")
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .padding(end = 10.dp)
                    )

                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(
                            10.dp
                        )
                ) {
                    Text(text = "• ")
                    Text(
                        text = buildAnnotatedString {
                            append("To create a new shelf, click on the ")
                            appendInlineContent("tuneIcon")
                            append(" at the ")
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("bottom of the Shelf Bar")
                            }
                            append(".")
                        },
                        inlineContent = mapOf(
                            Pair("tuneIcon", InlineTextContent(
                                Placeholder(
                                    22.sp, 22.sp,
                                    PlaceholderVerticalAlign.TextCenter
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null
                                )
                            })
                        ),
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .padding(end = 10.dp)
                    )

                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(
                            10.dp
                        )
                ) {
                    Text(text = "• ")
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Saved Links")
                            }
                            append(" and ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Important Links")
                            }
                            append(" can be accessed from the ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Default Shelf ")
                            }
                            append("(")
                            appendInlineContent("defaultSectionIcon")
                            append(").")
                        },
                        inlineContent = mapOf(
                            Pair(
                                "defaultSectionIcon",
                                InlineTextContent(
                                    Placeholder(
                                        22.sp, 22.sp,
                                        PlaceholderVerticalAlign.TextCenter
                                    )
                                ) {
                                    Icon(
                                        modifier = Modifier.rotate(-90f),
                                        imageVector = Icons.Outlined.Layers,
                                        contentDescription = null
                                    )
                                }
                            )
                        ),
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .padding(end = 10.dp)
                    )
                }
            }
        }, confirmButton = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .pulsateEffect(),
                onClick = {
                    coroutineScope.launch {
                        SettingsScreenVM.Settings.changeSettingPreferenceValue(
                            booleanPreferencesKey(
                                SettingsScreenVM.SettingsPreferences.NEW_FEATURE_DIALOG_BOX_VISIBILITY.name
                            ), context.dataStore, false
                        )
                    }
                    isDialogBoxVisible.value = false
                }) {
                Text(
                    text = "Ok",
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp
                )
            }
        })
    }
}