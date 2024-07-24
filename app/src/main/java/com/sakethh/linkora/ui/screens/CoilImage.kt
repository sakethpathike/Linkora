package com.sakethh.linkora.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun CoilImage(
    modifier: Modifier,
    imgURL: String
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(imgURL).crossfade(true).build(),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}