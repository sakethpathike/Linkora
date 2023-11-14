package com.sakethh.linkora.screens.settings.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.ui.theme.LinkoraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportConflictDialog(
    isVisible: MutableState<Boolean>,
    onMergeClick: () -> Unit,
    onDeleteExistingDataClick: () -> Unit,
    onExportAndThenImportClick: () -> Unit
) {
    if (isVisible.value) {
        LinkoraTheme {
            AlertDialog(modifier = Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(10.dp))
                .background(AlertDialogDefaults.containerColor),
                onDismissRequest = { isVisible.value = false }) {
                Column {
                    Text(
                        text = "Data Conflict",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(start = 20.dp, top = 30.dp, end = 20.dp),
                        lineHeight = 27.sp,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "You already have links saved, so importing data may conflict with the existing data in a few cases.",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp),
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Start
                    )
                    Button(colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        onClick = {
                            isVisible.value = false
                            onMergeClick()
                        }) {
                        Text(
                            text = "Import data, and keep the existing data.",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                    Button(colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp)
                            .fillMaxWidth(),
                        onClick = {
                            isVisible.value = false
                            onExportAndThenImportClick()
                        }) {
                        Text(
                            text = "Import data, export and delete the existing data.",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                    Button(colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        onClick = {
                            isVisible.value = false
                            onDeleteExistingDataClick()
                        }) {
                        Text(
                            text = "Import data, and delete the existing data.",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    }
}