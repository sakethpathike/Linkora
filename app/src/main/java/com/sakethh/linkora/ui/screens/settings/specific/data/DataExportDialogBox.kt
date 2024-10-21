package com.sakethh.linkora.ui.screens.settings.specific.data

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.data.local.export.ExportRequestInfo
import com.sakethh.linkora.data.local.export.ExportRequestState
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataExportDialogBox() {
    val exportRequestResult = ExportRequestInfo.state.collectAsStateWithLifecycle()
    val isDialogBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        ExportRequestInfo.state.collectLatest {
            isDialogBoxVisible.value = it != ExportRequestState.IDLE
        }
    }
    if (isDialogBoxVisible.value) {
        LinkoraTheme {
            BasicAlertDialog(
                modifier = Modifier
                    .animateContentSize()
                    .then(if (ExportRequestInfo.isHTMLBasedRequest.value) Modifier.fillMaxSize() else Modifier),
                onDismissRequest = {},
                properties = DialogProperties(
                    dismissOnClickOutside = false,
                    dismissOnBackPress = false,
                    usePlatformDefaultWidth = !ExportRequestInfo.isHTMLBasedRequest.value
                )
            ) {
                Column(
                    modifier = Modifier
                        .then(
                            if (ExportRequestInfo.isHTMLBasedRequest.value) Modifier.fillMaxSize() else Modifier.clip(
                                RoundedCornerShape(15.dp)
                            )
                        )
                        .fillMaxWidth()
                        .background(AlertDialogDefaults.containerColor)
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                        .animateContentSize()
                ) {
                    if (!ExportRequestInfo.isHTMLBasedRequest.value) {
                        Text(
                            text = exportRequestResult.value.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 20.sp
                        )
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 15.dp)
                        )
                    } else {
                        Text(
                            text = LocalizedStrings.gatheringDataForExport.value,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 20.sp
                        )
                        val dataExportDialogBoxVM: DataExportDialogBoxVM = viewModel()
                        dataExportDialogBoxVM.dataExportSection().forEach {
                            if (it.totalDetectedSize <= 0) return@forEach
                            Spacer(Modifier.height(10.dp))
                            Text(
                                it.itemType,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(10.dp))
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), progress = {
                                if (it.currentIterationCount.value.toInt() != 0 && it.totalDetectedSize.toInt() != 0
                                ) {
                                    it.currentIterationCount.value.toFloat() / it.totalDetectedSize
                                } else {
                                    0f
                                }
                            })
                            Spacer(Modifier.height(10.dp))
                            Text(
                                "${it.currentIterationCount.value}/${it.totalDetectedSize}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(10.dp))
                            Spacer(Modifier.height(10.dp))
                        }
                        Card(
                            border = BorderStroke(
                                1.dp,
                                contentColorFor(MaterialTheme.colorScheme.surface)
                            ),
                            colors = CardDefaults.cardColors(containerColor = AlertDialogDefaults.containerColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(
                                        top = 10.dp, bottom = 10.dp
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(
                                                start = 10.dp, end = 10.dp
                                            )
                                    )
                                }
                                Text(
                                    text = LocalizedStrings.dataExportDesc.value,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontSize = 14.sp,
                                    lineHeight = 18.sp,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .padding(end = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

class DataExportDialogBoxVM : ViewModel() {

    fun dataExportSection() = listOf(
        DataImportDialogBox(
            itemType = LocalizedStrings.savedLinks.value,
            totalDetectedSize = ExportRequestInfo.totalLinksFromSavedLinks.intValue.toLong(),
            currentIterationCount = mutableLongStateOf(ExportRequestInfo.currentIterationOfLinksFromSavedLinks.intValue.toLong())
        ),
        DataImportDialogBox(
            itemType = LocalizedStrings.importantLinks.value,
            totalDetectedSize = ExportRequestInfo.totalLinksFromImpLinksTable.intValue.toLong(),
            currentIterationCount = mutableLongStateOf(ExportRequestInfo.currentIterationOfLinksFromImpLinksTable.intValue.toLong())
        ),
        DataImportDialogBox(
            itemType = LocalizedStrings.linksFromHistory.value,
            totalDetectedSize = ExportRequestInfo.totalLinksFromHistoryLinksTable.intValue.toLong(),
            currentIterationCount = mutableLongStateOf(ExportRequestInfo.currentIterationOfLinksFromHistoryLinksTable.intValue.toLong())
        ),
        DataImportDialogBox(
            itemType = LocalizedStrings.archivedLinks.value,
            totalDetectedSize = ExportRequestInfo.totalLinksFromArchivedLinksTable.intValue.toLong(),
            currentIterationCount = mutableLongStateOf(ExportRequestInfo.currentIterationOfLinksFromArchivedLinksTable.intValue.toLong())
        ),
        DataImportDialogBox(
            itemType = LocalizedStrings.regularFolders.value,
            totalDetectedSize = ExportRequestInfo.totalRegularFoldersAndItsLinks.intValue.toLong(),
            currentIterationCount = mutableLongStateOf(ExportRequestInfo.currentIterationOfRegularFoldersAndItsLinks.intValue.toLong())
        ),
        DataImportDialogBox(
            itemType = LocalizedStrings.archivedFolders.value,
            totalDetectedSize = ExportRequestInfo.totalArchivedFoldersAndItsLinks.intValue.toLong(),
            currentIterationCount = mutableLongStateOf(ExportRequestInfo.currentIterationOfArchivedFoldersAndItsLinks.intValue.toLong())
        )
    )
}