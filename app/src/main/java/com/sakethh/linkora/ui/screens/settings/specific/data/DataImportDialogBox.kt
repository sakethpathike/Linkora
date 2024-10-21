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
import androidx.compose.runtime.MutableState
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
import com.sakethh.linkora.data.local.restore.ImportRequestResult
import com.sakethh.linkora.data.local.restore.ImportRequestState
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataImportDialogBox() {
    val importRequestState = ImportRequestResult.state.collectAsStateWithLifecycle()
    val isDialogBoxVisible = rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        ImportRequestResult.state.collectLatest {
            isDialogBoxVisible.value = it != ImportRequestState.IDLE
        }
    }
    if (isDialogBoxVisible.value) {
        LinkoraTheme {
            BasicAlertDialog(
                modifier = Modifier
                    .animateContentSize()
                    .then(if (importRequestState.value == ImportRequestState.PARSING || importRequestState.value == ImportRequestState.ADDING_TO_DATABASE) Modifier else Modifier.fillMaxSize()),
                onDismissRequest = {

                },
                properties = DialogProperties(
                    dismissOnClickOutside = false,
                    dismissOnBackPress = false,
                    usePlatformDefaultWidth = importRequestState.value == ImportRequestState.PARSING || importRequestState.value == ImportRequestState.ADDING_TO_DATABASE
                )
            ) {
                Column(
                    modifier = Modifier
                        .then(
                            if (importRequestState.value != ImportRequestState.PARSING && importRequestState.value != ImportRequestState.ADDING_TO_DATABASE && ImportRequestResult.totalLinksFromLinksTable.intValue != 0 && ImportRequestResult.currentIterationOfLinksFromLinksTable.intValue != 0) Modifier.fillMaxSize() else Modifier.clip(
                                RoundedCornerShape(15.dp)
                            )
                        )
                        .fillMaxWidth()
                        .background(AlertDialogDefaults.containerColor)
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = if (importRequestState.value == ImportRequestState.PARSING) LocalizedStrings.parsingTheFile.value else if (importRequestState.value == ImportRequestState.ADDING_TO_DATABASE) LocalizedStrings.insertingDataIntoTheDatabase.value else LocalizedStrings.modifyingKeysToPreventConflictsWithLocalData.value,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp
                    )
                    if (importRequestState.value == ImportRequestState.PARSING || importRequestState.value == ImportRequestState.ADDING_TO_DATABASE) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 15.dp)
                        )
                        return@BasicAlertDialog
                    }
                    Spacer(Modifier.height(10.dp))
                    val dataImportDialogBoxVM: DataImportDialogBoxVM = viewModel()
                    dataImportDialogBoxVM.dataImportSection().forEach {
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
                            .padding(top = 15.dp)
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
                                text = LocalizedStrings.importingDesc.value,
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

class DataImportDialogBoxVM : ViewModel() {
    fun dataImportSection() = listOf(
        DataImportDialogBox(
            itemType = LocalizedStrings.savedLinksAndLinksFromAllFoldersIncludingArchives.value,
            totalDetectedSize = ImportRequestResult.totalLinksFromLinksTable.intValue.toLong(),
            currentIterationCount = mutableLongStateOf(ImportRequestResult.currentIterationOfLinksFromLinksTable.intValue.toLong())
        ),
        DataImportDialogBox(
            itemType = LocalizedStrings.archivedLinks.value,
            totalDetectedSize = ImportRequestResult.totalLinksFromArchivedLinksTable.intValue.toLong(),
            currentIterationCount = mutableLongStateOf(ImportRequestResult.currentIterationOfLinksFromArchivedLinksTable.intValue.toLong())
        ),
        DataImportDialogBox(
            itemType = LocalizedStrings.importantLinks.value,
            totalDetectedSize = ImportRequestResult.totalLinksFromImpLinksTable.intValue.toLong(),
            currentIterationCount = mutableLongStateOf(ImportRequestResult.currentIterationOfLinksFromImpLinksTable.intValue.toLong())
        ),
        DataImportDialogBox(
            itemType = LocalizedStrings.linksFromHistory.value,
            totalDetectedSize = ImportRequestResult.totalLinksFromHistoryLinksTable.intValue.toLong(),
            currentIterationCount = mutableLongStateOf(ImportRequestResult.currentIterationOfLinksFromHistoryLinksTable.intValue.toLong())
        ),
        DataImportDialogBox(
            itemType = LocalizedStrings.regularFolders.value,
            totalDetectedSize = ImportRequestResult.totalRegularFolders.intValue.toLong(),
            currentIterationCount = mutableLongStateOf(ImportRequestResult.currentIterationOfRegularFolders.intValue.toLong())
        ),
        DataImportDialogBox(
            itemType = LocalizedStrings.archivedFolders.value,
            totalDetectedSize = ImportRequestResult.totalArchivedFolders.intValue.toLong(),
            currentIterationCount = mutableLongStateOf(ImportRequestResult.currentIterationOfArchivedFolders.intValue.toLong())
        ),
        DataImportDialogBox(
            itemType = LocalizedStrings.panels.value,
            totalDetectedSize = ImportRequestResult.totalPanels.intValue.toLong(),
            currentIterationCount = mutableLongStateOf(ImportRequestResult.currentIterationOfPanels.intValue.toLong())
        ),
        DataImportDialogBox(
            itemType = LocalizedStrings.panelFolders.value,
            totalDetectedSize = ImportRequestResult.totalPanelFolders.intValue.toLong(),
            currentIterationCount = mutableLongStateOf(ImportRequestResult.currentIterationOfPanelFolders.intValue.toLong())
        ),
    )
}

data class DataImportDialogBox(
    val itemType: String,
    val totalDetectedSize: Long,
    val currentIterationCount: MutableState<Long>
)