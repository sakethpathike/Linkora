package com.sakethh.linkora.ui.transferActions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TransferActionsBtmBar() {
    BottomAppBar {
        IconButton(onClick = {
            TransferActionsBtmBarValues.currentTransferActionType.value = TransferActionType.NOTHING
            TransferActionsBtmBarValues.sourceFolderId = -675675675
        }) {
            Icon(Icons.Default.Cancel, null)
        }
        Text(
            text = TransferActionsBtmBarValues.currentTransferActionType.value.name.replace(
                "_",
                " "
            )
                .split(" ").map { it[0] + it.substring(1).lowercase() }.joinToString { it }
                .replace(",", ""),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 16.sp
        )
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Row {
                IconButton(onClick = {

                }) {
                    Icon(Icons.Default.ContentPaste, null)
                }
                Spacer(Modifier.width(10.dp))
            }
        }
    }
}