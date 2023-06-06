package com.sakethh.linkora.screens.settings.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SettingsAppInfoComponent(
    hasDescription: Boolean = true,
    description: String,
    icon: ImageVector?,
    usingLocalIcon: Boolean = false,
    localIcon: Int = 0,
    title: String,
) {

    if (hasDescription) {
        Text(
            text = description,
            style = MaterialTheme.typography.titleSmall,
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            lineHeight = 20.sp,
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp)
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(10.dp), modifier = Modifier
            .padding(top = 20.dp, end = 20.dp, start = 20.dp)
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        Row {

            if (usingLocalIcon) {
                Icon(
                    painter = painterResource(id = localIcon), contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(20.dp)
                )
            } else {
                if (icon != null) {
                    Icon(
                        modifier = Modifier.padding(20.dp),
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 25.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}