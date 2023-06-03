package com.sakethh.linkora.screens.home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.screens.CoilImage

@Composable
fun GeneralCard(title: String, webBaseURL: String, imgURL: String) {
    Card(
        shape = RoundedCornerShape(10.dp), modifier = Modifier
            .height(155.dp)
            .width(275.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.55f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 15.dp, top = 20.dp, end = 15.dp),
                    maxLines = 4,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = webBaseURL,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 15.dp, bottom = 20.dp)
                )
            }
            Column(
                modifier = Modifier
                    .padding(start = 15.dp, top = 20.dp)
                    .fillMaxHeight()
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                CoilImage(
                    modifier = Modifier
                        .padding(end = 15.dp)
                        .width(95.dp)
                        .height(60.dp)
                        .clip(RoundedCornerShape(15.dp)), imgURL = imgURL
                )
                Row(
                    modifier = Modifier.padding(end=10.dp,bottom = 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.Star, contentDescription = null)
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null)
                }
            }
        }
    }
}