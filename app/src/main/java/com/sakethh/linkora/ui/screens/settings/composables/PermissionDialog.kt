package com.sakethh.linkora.ui.screens.settings.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
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
import com.sakethh.linkora.LocalizedStrings.cancel
import com.sakethh.linkora.LocalizedStrings.goToSettings
import com.sakethh.linkora.LocalizedStrings.permissionDeniedTitle
import com.sakethh.linkora.LocalizedStrings.permissionIsDeniedDesc
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.theme.LinkoraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionDialog(
    isVisible: MutableState<Boolean>,
    permissionDenied: Boolean,
    onClick: () -> Unit,
) {
    if (isVisible.value) {
        LinkoraTheme {
            BasicAlertDialog(
                onDismissRequest = { isVisible.value = false }, modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(AlertDialogDefaults.containerColor)
            ) {
                Column {
                    Text(
                        text = permissionDeniedTitle.value,
                        color = AlertDialogDefaults.titleContentColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(start = 20.dp, top = 30.dp, end = 20.dp),
                        lineHeight = 27.sp,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = permissionIsDeniedDesc.value,
                        color = AlertDialogDefaults.titleContentColor,
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
                            .align(Alignment.End)
                            .pulsateEffect(),
                        onClick = {
                            isVisible.value = false
                            onClick()
                        }) {
                        Text(
                            text = goToSettings.value,
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
                            .align(Alignment.End)
                            .pulsateEffect(),
                        onClick = {
                            isVisible.value = false
                        }) {
                        Text(
                            text = cancel.value,
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