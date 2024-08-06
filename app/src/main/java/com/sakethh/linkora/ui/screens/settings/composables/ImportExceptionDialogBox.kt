package com.sakethh.linkora.ui.screens.settings.composables

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.R
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
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
                        IllegalArgumentException().toString() -> stringResource(id = R.string.incompatible_file_type)

                        SerializationException().toString() -> stringResource(id = R.string.data_conversion_failed)
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
                        IllegalArgumentException().toString() -> stringResource(id = R.string.selected_file_does_not_match_Linkora_schema)

                        SerializationException().toString() -> stringResource(id = R.string.there_was_an_issue_importing_the_links)
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
                        .fillMaxWidth()
                        .pulsateEffect(),
                    onClick = {
                        isVisible.value = false
                        onClick()
                    }) {
                    Text(
                        text = stringResource(id = R.string.choose_another_file),
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 16.sp
                    )
                }
            }, dismissButton = {
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pulsateEffect(),
                    onClick = {
                        isVisible.value = false
                    }) {
                    Text(
                        text = stringResource(id = R.string.cancel),
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