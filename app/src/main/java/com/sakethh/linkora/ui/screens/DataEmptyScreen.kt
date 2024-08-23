package com.sakethh.linkora.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.ui.theme.LinkoraTheme

@Composable
fun DataEmptyScreen(text: String) {
    LinkoraTheme {
        Box(
            modifier = Modifier
                .padding(top = 75.dp, start = 15.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                androidx.compose.material3.Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 32.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 50.dp)
                )
            }
        }
    }
}