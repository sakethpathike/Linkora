package com.sakethh.linkora.screens.settings.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsDataComposable(
    onClick: () -> Unit,
    shape: RoundedCornerShape,
    title: String,
    description: String,
    icon: ImageVector,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, top = 1.dp, end = 15.dp)
            .clickable {
                onClick()
            }
            .animateContentSize(),
        shape = shape
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
}