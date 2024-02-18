package com.sakethh.linkora.screens.settings.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.screens.settings.SettingsUIElement

@Composable
fun RegularSettingComponent(
    settingsUIElement: SettingsUIElement
) {
    val cardHeight = remember {
        mutableStateOf(0.dp)
    }
    val localDensity = LocalDensity.current
    Row(modifier = Modifier
        .clickable {
            settingsUIElement.onSwitchStateChange()
        }
        .fillMaxWidth()
        .animateContentSize()) {
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
                    ),
                    lineHeight = 20.sp
                )
                if (settingsUIElement.doesDescriptionExists) {
                    Text(
                        text = settingsUIElement.description.toString(),
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
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