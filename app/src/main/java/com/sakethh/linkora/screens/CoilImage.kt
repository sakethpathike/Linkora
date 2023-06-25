package com.sakethh.linkora.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sakethh.linkora.R

@Composable
fun CoilImage(
    modifier: Modifier,
    imgURL: String,
    onError: () -> Unit = {},
    onSuccess: () -> Unit = {},
) {
    AsyncImage(
        error = painterResource(id = R.drawable.image),
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