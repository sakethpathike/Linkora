package com.sakethh.linkora.ui.commonComposables.link_views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.ImageNotSupported
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.ui.commonComposables.link_views.LinkUIComponentParam
import com.sakethh.linkora.ui.screens.CoilImage

@Composable
fun StaggeredViewComponent(linkUIComponentParam: LinkUIComponentParam) {
    val colorScheme = MaterialTheme.colorScheme
    if (linkUIComponentParam.imgURL.isBlank()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .clip(RoundedCornerShape(5.dp))
                .defaultMinSize(minHeight = 150.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(5.dp)
                )
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                tint = MaterialTheme.colorScheme.onPrimary,
                imageVector = Icons.Rounded.ImageNotSupported,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                    Text(
                        text = linkUIComponentParam.title,
                        modifier = Modifier
                            .padding(10.dp),
                        maxLines = 5,
                        style = MaterialTheme.typography.titleSmall,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 12.sp
                    )
                }
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        Icons.Default.MoreVert, contentDescription = null
                    )
                }
            }
        }
    } else {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .clip(RoundedCornerShape(5.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(5.dp)
                )
        ) {
            CoilImage(
                modifier = Modifier
                    .fillMaxSize(),
                imgURL = linkUIComponentParam.imgURL,
                contentScale = ContentScale.Fit
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                    Text(
                        text = linkUIComponentParam.title,
                        modifier = Modifier
                            .padding(10.dp),
                        maxLines = 5,
                        style = MaterialTheme.typography.titleSmall,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 12.sp
                    )
                }
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        Icons.Default.MoreVert, contentDescription = null
                    )
                }
            }
        }
    }
}