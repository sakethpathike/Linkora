package com.sakethh.linkora.screens.collections.archiveScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sakethh.linkora.ui.theme.LinkoraTheme

@Composable
fun ChildArchiveScreen(archiveScreenType: ArchiveScreenType) {
    LinkoraTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)){
            Text(modifier = Modifier.align(Alignment.Center), text = if(archiveScreenType == ArchiveScreenType.LINKS) "Links" else "Folders")
        }
    }
}