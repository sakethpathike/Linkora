package com.sakethh.linkora.screens.settings.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.customComposables.pulsateEffect

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsDataComposable(
    onClick: () -> Unit,
    title: String,
    description: String,
    icon: ImageVector,
) {
    Row(
        modifier = Modifier
            .combinedClickable(interactionSource = remember {
                MutableInteractionSource()
            }, indication = null,
                onClick = {
                    onClick()
                },
                onLongClick = {

                })
            .pulsateEffect()
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier.padding(10.dp), onClick = { onClick() },
            colors = IconButtonDefaults.filledIconButtonColors()
        ) {
            Icon(imageVector = icon, contentDescription = null)
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 16.sp,
                modifier = Modifier.padding(
                    top = 20.dp,
                    bottom = 0.dp,
                    end = 20.dp
                ),
                lineHeight = 20.sp
            )
            Text(
                text = description,
                style = MaterialTheme.typography.titleSmall,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(
                    top = 10.dp, bottom = 20.dp,
                    end = 25.dp
                )
            )
        }
    }
}