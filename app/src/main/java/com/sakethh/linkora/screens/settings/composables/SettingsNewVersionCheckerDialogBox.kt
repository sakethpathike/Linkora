package com.sakethh.linkora.screens.settings.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.ui.theme.LinkoraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsNewVersionCheckerDialogBox(shouldDialogBoxAppear: MutableState<Boolean>) {
    if (shouldDialogBoxAppear.value) {
        LinkoraTheme {
            AlertDialog(modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(AlertDialogDefaults.containerColor),
                onDismissRequest = { }) {
                Row {
                    CircularProgressIndicator(
                        color = AlertDialogDefaults.textContentColor,
                        modifier = Modifier
                            .padding(20.dp)
                            .align(Alignment.CenterVertically),
                        strokeWidth = 4.dp
                    )
                    Text(
                        text = "Retrieving the latest data—this might take a moment",
                        color = AlertDialogDefaults.textContentColor,
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 20.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(end = 20.dp, top = 40.dp, bottom = 40.dp)
                    )
                }
            }
        }
    }
}