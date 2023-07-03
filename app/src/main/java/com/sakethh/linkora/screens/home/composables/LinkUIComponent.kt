package com.sakethh.linkora.screens.home.composables

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.screens.CoilImage

@Composable
fun LinkUIComponent(
    title: String,
    webBaseURL: String,
    imgURL: String,
    onMoreIconCLick: () -> Unit,
    onLinkClick: () -> Unit,
    webURL: String,
) {
    val context = LocalContext.current
    val localClipBoardManager = LocalClipboardManager.current
    Column(
        modifier = Modifier
            .clickable {
                onLinkClick()
            }
            .padding(start = 15.dp, end = 15.dp, top = 15.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .padding(end = 15.dp),
                maxLines = 4,
                lineHeight = 20.sp,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis
            )
            CoilImage(
                modifier = Modifier
                    .width(95.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(15.dp)), imgURL = imgURL
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = webBaseURL,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 5.dp)
                    .fillMaxWidth(0.65f)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Icon(imageVector = Icons.Outlined.ContentCopy, contentDescription = null,
                    modifier = Modifier.clickable {
                        localClipBoardManager.setText(
                            AnnotatedString(webURL)
                        )
                        Toast.makeText(
                            context, "Link copied to the clipboard",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                Icon(imageVector = Icons.Outlined.Share, contentDescription = null,
                    modifier = Modifier.clickable {
                        val intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, webURL)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(intent, null)
                        context.startActivity(shareIntent)
                    })
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = null,
                    modifier = Modifier.clickable { onMoreIconCLick() })
            }
        }
    }
    Divider(
        thickness = 0.5.dp,
        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 15.dp)
    )
}