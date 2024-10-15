package com.sakethh.linkora.ui.screens.settings.specific.data

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sakethh.linkora.data.local.restore.ImportRequestResult
import com.sakethh.linkora.data.local.restore.ImportRequestState
import com.sakethh.linkora.ui.theme.LinkoraTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataImportDialogBox() {
    val importRequestState = ImportRequestResult.state.collectAsStateWithLifecycle()
    val currentState = rememberSaveable {
        mutableStateOf("dfgdfgdfg")
    }
    val isDialogBoxVisible = rememberSaveable {
        mutableStateOf(true)
    }
    LaunchedEffect(Unit) {
        ImportRequestResult.state.collectLatest {
            currentState.value = it.name
            //isDialogBoxVisible.value = it != ImportRequestState.IDLE
        }
    }
    if (isDialogBoxVisible.value) {
        LaunchedEffect(Unit) {
            ImportRequestResult.state.collectLatest {
                currentState.value = it.name
            }
        }
        LinkoraTheme {
            BasicAlertDialog(
                modifier = Modifier.animateContentSize(),
                onDismissRequest = {

                },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    usePlatformDefaultWidth = false
                )
            ) {
                Column(
                    modifier = Modifier
                        .then(if (importRequestState.value != ImportRequestState.PARSING && ImportRequestResult.totalLinksFromLinksTable.intValue != 0 && ImportRequestResult.currentIterationOfLinksFromLinksTable.intValue != 0) Modifier.fillMaxSize() else Modifier.clip(
                            RoundedCornerShape(15.dp)
                        ))
                        .background(AlertDialogDefaults.containerColor)
                        .padding(20.dp)
                ) {
                    /*Text(
                        text = "Parsing the file...",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp
                    )
                    if (importRequestState.value == ImportRequestState.PARSING || ImportRequestResult.totalLinksFromLinksTable.intValue == 0 || ImportRequestResult.currentIterationOfLinksFromLinksTable.intValue == 0) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        return@Column
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(
                        "Saved Links and Links from folders (including archived folders)",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), progress = {
                        ImportRequestResult.currentIterationOfLinksFromLinksTable.intValue.toFloat() / ImportRequestResult.totalLinksFromLinksTable.intValue
                    })*/
                }
            }
        }
    }
}