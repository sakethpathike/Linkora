package com.sakethh.linkora.ui.commonComposables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.intPreferencesKey
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
                            text = "A new option in Settings lets you use any user agent, helping detect images and titles from web pages using meta tags.\nTo set up custom user agents, go to Settings > General > User Agent.",
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 18.sp,
                            lineHeight = 28.sp,
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