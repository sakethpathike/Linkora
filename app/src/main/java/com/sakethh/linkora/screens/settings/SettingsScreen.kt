package com.sakethh.linkora.screens.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.sakethh.linkora.ui.theme.LinkoraTheme

@Composable
fun SettingsScreen() {
    LinkoraTheme {
        Text(text = "Settings screen", style = MaterialTheme.typography.titleLarge)
    }
}