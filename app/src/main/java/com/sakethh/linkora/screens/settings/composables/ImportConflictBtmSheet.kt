package com.sakethh.linkora.screens.settings.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ImportConflictBtmSheet(
    modalBottomSheetState: SheetState,
    isUIVisible: MutableState<Boolean>,
    onMergeClick: () -> Unit,
    onDeleteExistingDataClick: () -> Unit,
    onExportAndThenImportClick: () -> Unit,
    onDataExportClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = isUIVisible.value) {
        if (isUIVisible.value) {
            modalBottomSheetState.expand()
        } else {
            modalBottomSheetState.hide()
        }
    }
    if (isUIVisible.value) {
        LinkoraTheme {
            ModalBottomSheet(sheetState = modalBottomSheetState,
                onDismissRequest = {
                    coroutineScope.launch {
                        awaitAll(
                            async { isUIVisible.value = false },
                            async { modalBottomSheetState.hide() })
                    }
                }) {
                Text(
                    text = "Heads Up!",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                    lineHeight = 27.sp,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = "You already have links saved, so importing data might cause conflicts in some cases. To prevent any unwanted surprises, export your data before selecting any import options.\n" +
                            "\n" +
                            "Remember, the actions triggered by your choice will happen simultaneously.",
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp),
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Start
                )
                OutlinedButton(colors = ButtonDefaults.outlinedButtonColors(),
                    border = BorderStroke(
                        width = 1.dp, color = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier
                        .padding(
                            end = 20.dp, top = 10.dp, start = 20.dp
                        )
                        .fillMaxWidth()
                        .align(Alignment.End),
                    onClick = {
                        onDataExportClick(
                        )
                    }) {
                    Text(
                        text = "Export data",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 16.sp
                    )
                }
                Button(colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, top = 10.dp)
                        .fillMaxWidth(),
                    onClick = {
                        coroutineScope.launch {
                            awaitAll(
                                async { isUIVisible.value = false },
                                async { modalBottomSheetState.hide() })
                        }
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
                        .padding(start = 20.dp, end = 20.dp, top = 10.dp)
                        .fillMaxWidth(),
                    onClick = {
                        coroutineScope.launch {
                            awaitAll(
                                async { isUIVisible.value = false },
                                async { modalBottomSheetState.hide() })
                        }
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
                        .padding(start = 20.dp, end = 20.dp, top = 10.dp)
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    onClick = {
                        coroutineScope.launch {
                            awaitAll(
                                async { isUIVisible.value = false },
                                async { modalBottomSheetState.hide() })
                        }
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
                Spacer(
                    modifier = Modifier
                        .height(20.dp)
                        .navigationBarsPadding()
                )
            }
        }
    }
}