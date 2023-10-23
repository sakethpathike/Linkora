package com.sakethh.linkora.screens.settings.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import kotlinx.serialization.SerializationException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportExceptionDialogBox(
    isVisible: MutableState<Boolean>,
    onClick: () -> Unit,
    exceptionType: MutableState<Exception?>,
) {
    if (isVisible.value) {
        LinkoraTheme {
            AlertDialog(modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(AlertDialogDefaults.containerColor),
                onDismissRequest = { isVisible.value = false }) {
                Column {
                    Text(
                        text = when (exceptionType.value) {
                            IllegalArgumentException() -> "Incompatible File Type"

                            SerializationException() -> "Data Conversion Failed"
                            else -> ""
                        },
                        color = AlertDialogDefaults.titleContentColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(start = 20.dp, top = 30.dp, end = 20.dp),
                        lineHeight = 27.sp,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = when (exceptionType.value) {
                            IllegalArgumentException() -> "Selected file doesn't match Linkora's Schema for JSON files. Please choose a compatible file to import your links."

                            SerializationException() -> "Apologies, but there was an issue importing the links. Please try again with a different file."
                            else -> ""
                        }, color = AlertDialogDefaults.titleContentColor,
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp),
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Start
                    )
                    Button(colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .padding(
                                end = 20.dp,
                                top = 20.dp,
                            )
                            .align(Alignment.End),
                        onClick = {
                            isVisible.value = false
                            onClick()
                        }) {
                        Text(
                            text = "Choose another file",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                    }
                    OutlinedButton(colors = ButtonDefaults.outlinedButtonColors(),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier
                            .padding(
                                end = 20.dp,
                                top = 10.dp,
                                bottom = 30.dp
                            )
                            .align(Alignment.End),
                        onClick = {
                            isVisible.value = false
                        }) {
                        Text(
                            text = "Cancel",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}