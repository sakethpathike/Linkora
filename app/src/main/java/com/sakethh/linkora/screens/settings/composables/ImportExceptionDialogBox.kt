package com.sakethh.linkora.screens.settings.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.serialization.SerializationException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportExceptionDialogBox(
    isVisible: MutableState<Boolean>,
    onClick: () -> Unit,
    exceptionType: MutableState<String?>,
) {
    if (isVisible.value) {
        LinkoraTheme {
            AlertDialog(title = {
                Text(
                    text = when (exceptionType.value) {
                        IllegalArgumentException().toString() -> "Incompatible File Type"

                        SerializationException().toString() -> "Data Conversion Failed"
                        else -> ""
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 22.sp,
                    lineHeight = 27.sp,
                    textAlign = TextAlign.Start
                )
            }, text = {
                Text(
                    text = when (exceptionType.value) {
                        IllegalArgumentException().toString() -> "Selected file doesn't match Linkora's Schema for JSON files. Please choose a compatible file to import your links."

                        SerializationException().toString() -> "Apologies, but there was an issue importing the links. Please try again with a different file."
                        else -> ""
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Start
                )
            }, confirmButton = {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        isVisible.value = false
                        onClick()
                    }) {
                    Text(
                        text = "Choose another file",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 16.sp
                    )
                }
            }, dismissButton = {
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        isVisible.value = false
                    }) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 16.sp
                    )
                }
            }, modifier = Modifier
                .wrapContentSize(),
                onDismissRequest = { isVisible.value = false })
        }
    }
}