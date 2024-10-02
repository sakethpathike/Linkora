package com.sakethh.linkora.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sakethh.linkora.ui.screens.settings.SettingsPreference

@Composable
fun CoilImage(
    modifier: Modifier,
    imgURL: String,
    contentScale: ContentScale = ContentScale.Crop
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(imgURL).addHeader(
            "User-Agent",
            SettingsPreference.jsoupUserAgent.value
        ).crossfade(true).build(),
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale
    )
}