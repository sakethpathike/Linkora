package com.sakethh.linkora.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.Coil
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun CoilImage(modifier: Modifier, imgURL: String, onError: () -> Unit={}, onSuccess: () -> Unit={}) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(imgURL).crossfade(true).build(),
        contentDescription = null,
        onError = {
            onError()
        },
        onSuccess = {
            onSuccess()
        },
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}