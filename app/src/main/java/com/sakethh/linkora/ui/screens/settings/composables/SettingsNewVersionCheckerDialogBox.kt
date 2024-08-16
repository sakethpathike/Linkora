package com.sakethh.linkora.ui.screens.settings.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.LocalizedStrings.cancel
import com.sakethh.linkora.LocalizedStrings.retrievingLatestInformation
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.screens.settings.SettingsPreference
import com.sakethh.linkora.ui.theme.LinkoraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsNewVersionCheckerDialogBox(
    shouldDialogBoxAppear: MutableState<Boolean> = mutableStateOf(
        true
    )
) {
    if (shouldDialogBoxAppear.value && !SettingsPreference.didServerTimeOutErrorOccurred.value) {
        LinkoraTheme {
            BasicAlertDialog(
                onDismissRequest = { }, modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AlertDialogDefaults.containerColor)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                            .padding(top = 20.dp, end = 20.dp)
                            .fillMaxWidth()
                    ) {
                        CircularProgressIndicator(
                            color = AlertDialogDefaults.textContentColor,
                            modifier = Modifier
                                .padding(20.dp)
                                .align(Alignment.CenterVertically),
                            strokeWidth = 4.dp
                        )
                        Text(
                            text = retrievingLatestInformation.value,
                            color = AlertDialogDefaults.textContentColor,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 20.sp,
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Button(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                            .pulsateEffect(),
                        onClick = {
                            shouldDialogBoxAppear.value = false
                        }) {
                        Text(
                            text = cancel.value,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}