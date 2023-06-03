package com.sakethh.linkora.screens.collections

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.sakethh.linkora.ui.theme.LinkoraTheme

@Composable
fun CollectionScreen() {
    LinkoraTheme {
        Text(text = "collection screen", style = MaterialTheme.typography.titleLarge)
    }
}