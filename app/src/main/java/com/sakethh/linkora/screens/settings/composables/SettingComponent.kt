package com.sakethh.linkora.screens.settings.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.preferencesKey
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.screens.settings.SettingsScreenVM
import com.sakethh.linkora.screens.settings.SettingsUIElement
import kotlinx.coroutines.launch

@Composable
fun SettingComponent(settingsUIElement: SettingsUIElement, data: List<SettingsUIElement>) {
    val coroutineScope = rememberCoroutineScope()
    val cardHeight = remember {
        mutableStateOf(0.dp)
    }
    val settingsScreenVM: SettingsScreenVM = viewModel()
    val localDensity = LocalDensity.current
    Card(
        shape = RoundedCornerShape(
            topStart = if (settingsUIElement.title == data[0].title) 10.dp else 0.dp,
            topEnd = if (settingsUIElement.title == data[0].title) 10.dp else 0.dp,
            bottomStart = if (settingsUIElement.title == data.last().title) 10.dp else 0.dp,
            bottomEnd = if (settingsUIElement.title == data.last().title) 10.dp else 0.dp
        ), modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = if (settingsUIElement.title == data[0].title) 20.dp else 2.dp,
                start = 15.dp,
                end = 15.dp
            )
            .clickable {
                settingsUIElement.onSwitchStateChange()
            }
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(if (settingsUIElement.isSwitchNeeded) 0.70f else 1f)) {
                Column(modifier = Modifier.onGloballyPositioned {
                    cardHeight.value = with(localDensity) {
                        it.size.height.toDp()
                    }
                }) {
                    Text(
                        text = settingsUIElement.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(
                            start = 15.dp,
                            top = 25.dp,
                            bottom = if (!settingsUIElement.doesDescriptionExists) 25.dp else 0.dp
                        )
                    )
                    if (settingsUIElement.doesDescriptionExists) {
                        Text(
                            text = settingsUIElement.description.toString(),
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(
                                start = 15.dp, top = 10.dp, bottom = 25.dp
                            )
                        )
                    }
                }
            }
            if (settingsUIElement.isSwitchNeeded) {
                Box(
                    modifier = Modifier
                        .padding(end = 15.dp)
                        .fillMaxWidth()
                        .height(cardHeight.value),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Switch(
                        checked = settingsUIElement.isSwitchEnabled.value,
                        onCheckedChange = {
                            settingsUIElement.onSwitchStateChange()
                        })
                }
            }
        }
    }
}