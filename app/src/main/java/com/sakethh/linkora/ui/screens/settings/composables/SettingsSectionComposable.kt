package com.sakethh.linkora.ui.screens.settings.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.ui.commonComposables.pulsateEffect

@Composable
fun SettingsSectionComposable(
    onClick: () -> Unit,
    sectionTitle: String,
    sectionIcon: ImageVector,
    shouldArrowIconAppear: Boolean = true
) {
    Column {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .pulsateEffect()
                .clickable(interactionSource = remember {
                    MutableInteractionSource()
                }, onClick = {
                    onClick()
                }, indication = null)
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            FilledTonalIconButton(onClick = { onClick() }) {
                Icon(
                    imageVector = sectionIcon,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = sectionTitle,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 20.sp,
            )
            if (shouldArrowIconAppear) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row {
                        IconButton(onClick = { onClick() }) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }
            }
        }
        if (shouldArrowIconAppear) {
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}