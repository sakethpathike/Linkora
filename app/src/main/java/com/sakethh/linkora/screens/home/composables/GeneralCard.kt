package com.sakethh.linkora.screens.home.composables

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.ImageNotSupported
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.screens.CoilImage

@Composable
fun GeneralCard(
    title: String,
    webBaseURL: String,
    webURL: String,
    imgURL: String,
    onMoreIconClick: () -> Unit,
    onCardClick: () -> Unit,
) {
    val context = LocalContext.current
    val localClipBoardManager = LocalClipboardManager.current
    val localURIHandler = LocalUriHandler.current
    Card(
        shape = RoundedCornerShape(10.dp), modifier = Modifier
            .height(155.dp)
            .width(275.dp)
            .clickable {
                onCardClick()
            }
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.44f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
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
                if (imgURL.startsWith("https://") && imgURL.endsWith(".webp") || imgURL.startsWith("https://") && imgURL.endsWith(
                        ".jpeg"
                    ) || imgURL.startsWith("https://") && imgURL.endsWith(
                        ".jpg"
                    ) || imgURL.startsWith("https://") && imgURL.endsWith(".png") || imgURL.startsWith(
                        "https://"
                    ) && imgURL.endsWith(
                        ".ico"
                    )
                ) {
                    CoilImage(
                        modifier = Modifier
                            .padding(end = 15.dp)
                            .width(95.dp)
                            .height(60.dp)
                            .clip(RoundedCornerShape(15.dp)), imgURL = imgURL
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .padding(end = 15.dp)
                            .width(95.dp)
                            .height(60.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onSecondary,
                            imageVector = Icons.Rounded.ImageNotSupported,
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier.padding(end = 10.dp, bottom = 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.OpenInBrowser, contentDescription = null,
                        modifier = Modifier.clickable {
                            localURIHandler.openUri(webURL)
                        })
                    Icon(imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            localClipBoardManager.setText(
                                AnnotatedString(webURL)
                            )
                            Toast.makeText(
                                context, "Link copied to the clipboard",
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                    Icon(imageVector = Icons.Outlined.Share,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, webURL)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(intent, null)
                            context.startActivity(shareIntent)
                        })
                    Icon(imageVector = Icons.Filled.MoreVert,
                        contentDescription = null,
                        modifier = Modifier.clickable { onMoreIconClick() })
                }
            }
        }
    }
}