package com.sakethh.linkora.ui.screens.settings.specific.data

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
                    .animateContentSize(),
                onDismissRequest = {},
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    usePlatformDefaultWidth = false
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .clip(
                            RoundedCornerShape(15.dp)
                        )
                        .background(AlertDialogDefaults.containerColor)
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
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
                }
            }
        }
    }
}