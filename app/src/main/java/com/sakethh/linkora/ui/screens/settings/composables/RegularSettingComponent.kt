package com.sakethh.linkora.ui.screens.settings.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.screens.settings.SettingsUIElement

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RegularSettingComponent(
    settingsUIElement: SettingsUIElement
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .combinedClickable(interactionSource = remember {
                MutableInteractionSource()
            }, indication = null,
                onClick = {
                    settingsUIElement.onSwitchStateChange(!settingsUIElement.isSwitchEnabled.value)
                    settingsUIElement.onAcknowledgmentClick(uriHandler, context)
                },
                onLongClick = {

                })
            .pulsateEffect()
            .fillMaxWidth()
            .animateContentSize(), verticalAlignment = Alignment.CenterVertically
    ) {
        if (settingsUIElement.isIconNeeded.value && settingsUIElement.icon != null) {
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(
                colors = if (settingsUIElement.shouldFilledIconBeUsed.value) IconButtonDefaults.filledTonalIconButtonColors() else IconButtonDefaults.iconButtonColors(),
                onClick = { settingsUIElement.onSwitchStateChange(!settingsUIElement.isSwitchEnabled.value) }) {
                Icon(imageVector = settingsUIElement.icon, contentDescription = null)
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
        Column {
            Text(
                text = rememberSaveable(settingsUIElement.title) {
                    settingsUIElement.title
                },
                style = MaterialTheme.typography.titleMedium,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth(if (settingsUIElement.shouldArrowIconBeAppear.value || settingsUIElement.isSwitchNeeded) 0.75f else 1f)
                    .padding(
                        start = if (settingsUIElement.isIconNeeded.value) 0.dp else 15.dp,
                        end = if (!settingsUIElement.isSwitchNeeded) 25.dp else 0.dp
                    ),
                lineHeight = 20.sp
            )
            if (settingsUIElement.doesDescriptionExists) {
                Text(
                    text = rememberSaveable(settingsUIElement.description) {
                        settingsUIElement.description ?: ""
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth(if (settingsUIElement.shouldArrowIconBeAppear.value || settingsUIElement.isSwitchNeeded) 0.75f else 1f)
                        .padding(
                            start = if (settingsUIElement.isIconNeeded.value) 0.dp else 15.dp,
                            top = 10.dp,
                            end = if (!settingsUIElement.isSwitchNeeded) 25.dp else 15.dp
                        )
                )
            }
        }
        if (settingsUIElement.isSwitchNeeded) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Switch(
                    modifier = Modifier
                        .padding(end = 15.dp),
                    checked = settingsUIElement.isSwitchEnabled.value,
                    onCheckedChange = {
                        settingsUIElement.onSwitchStateChange(it)
                    })
            }
        }
        if (settingsUIElement.shouldArrowIconBeAppear.value) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                IconButton(onClick = {
                    settingsUIElement.onAcknowledgmentClick(
                        uriHandler,
                        context
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null
                    )
                }
            }
        }
    }
}