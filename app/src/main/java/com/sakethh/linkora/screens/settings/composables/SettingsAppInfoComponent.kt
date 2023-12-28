package com.sakethh.linkora.screens.settings.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
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
    onClick: () -> Unit,
) {
    val heightOfCard = remember {
        mutableStateOf(0.dp)
    }
    val localDensity = LocalDensity.current
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
        shape = RoundedCornerShape(10.dp), modifier = Modifier
            .padding(top = 20.dp, end = 20.dp, start = 20.dp)
            .wrapContentHeight()
            .fillMaxWidth()
            .onGloballyPositioned {
                heightOfCard.value = with(localDensity) {
                    it.size.height.toDp()
                }
            }
            .clickable {
                onClick()
            }
    ) {
        Row {
            if (usingLocalIcon) {
                Icon(
                    painter = painterResource(id = localIcon),
                    contentDescription = null,
                    modifier = Modifier.padding(20.dp)
                )
            } else {
                if (icon != null) {
                    Icon(
                        modifier = Modifier.padding(20.dp),
                        imageVector = icon,
                        contentDescription = null
                    )
                }
            }
            Box(
                modifier = Modifier.height(heightOfCard.value),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(end = 15.dp)
                )
            }
        }
    }
}