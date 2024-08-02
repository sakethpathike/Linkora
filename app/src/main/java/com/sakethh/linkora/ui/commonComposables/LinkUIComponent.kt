package com.sakethh.linkora.ui.commonComposables

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ImageNotSupported
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.ui.screens.CoilImage


data class LinkUIComponentParam(
    val title: String,
    val webBaseURL: String,
    val imgURL: String,
    val onMoreIconCLick: () -> Unit,
    val onLinkClick: () -> Unit,
    val webURL: String,
    val onForceOpenInExternalBrowserClicked: () -> Unit,
    val isSelectionModeEnabled: MutableState<Boolean>,
    val isItemSelected: MutableState<Boolean>,
    val onLongClick: () -> Unit
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LinkUIComponent(
    linkUIComponentParam: LinkUIComponentParam
) {
    val context = LocalContext.current
    val localClipBoardManager = LocalClipboardManager.current
    val localURIHandler = LocalUriHandler.current
    Column(
        modifier = Modifier
            .background(
                if (linkUIComponentParam.isItemSelected.value) MaterialTheme.colorScheme.primary.copy(
                    0.25f
                ) else Color.Transparent
            )
            .combinedClickable(interactionSource = remember {
                MutableInteractionSource()
            }, indication = null,
                onClick = { linkUIComponentParam.onLinkClick() },
                onLongClick = {
                    linkUIComponentParam.onLongClick()
                })
            .padding(start = 15.dp, top = 15.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .pulsateEffect()
            .animateContentSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 15.dp)
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = linkUIComponentParam.title,
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
            if (!linkUIComponentParam.isItemSelected.value) {
                if (linkUIComponentParam.imgURL.isNotEmpty()) {
                    CoilImage(
                        modifier = Modifier
                            .width(95.dp)
                            .height(60.dp)
                            .clip(RoundedCornerShape(15.dp)), imgURL = linkUIComponentParam.imgURL
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .width(95.dp)
                            .height(60.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onPrimary,
                            imageVector = Icons.Rounded.ImageNotSupported,
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .width(95.dp)
                        .height(60.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        tint = MaterialTheme.colorScheme.onPrimary,
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                    )
                }
            }
        }
        Text(
            text = linkUIComponentParam.webBaseURL,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 15.dp,
                    end = 15.dp,
                    bottom = if (linkUIComponentParam.isSelectionModeEnabled.value) 15.dp else 0.dp
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                if (!linkUIComponentParam.isSelectionModeEnabled.value) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            linkUIComponentParam.onForceOpenInExternalBrowserClicked()
                            try {
                                localURIHandler.openUri(linkUIComponentParam.webURL)
                            } catch (_: android.content.ActivityNotFoundException) {
                                Toast.makeText(
                                    context,
                                    "No Activity found to handle Intent",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.OpenInBrowser,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = {
                            localClipBoardManager.setText(
                                AnnotatedString(linkUIComponentParam.webURL)
                            )
                            Toast.makeText(
                                context, "Link copied to the clipboard",
                                Toast.LENGTH_SHORT
                            ).show()
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = {
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, linkUIComponentParam.webURL)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(intent, null)
                            context.startActivity(shareIntent)
                        }) {
                            Icon(imageVector = Icons.Outlined.Share, contentDescription = null)
                        }
                        IconButton(onClick = {
                            linkUIComponentParam.onMoreIconCLick()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outline.copy(0.25f)
        )
    }
}