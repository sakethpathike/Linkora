package com.sakethh.linkora.ui.commonComposables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp


data class AddANewShelfDTO(
    val isDialogBoxVisible: MutableState<Boolean>,
    val onCreateClick: (shelfName: String, shelfIconName: String) -> Unit
)

@Composable
fun AddANewShelfDialogBox(addANewShelfDTO: AddANewShelfDTO) {
    if (addANewShelfDTO.isDialogBoxVisible.value) {
        val customShelfName = rememberSaveable {
            mutableStateOf("")
        }
        val customShelfIconName = rememberSaveable {
            mutableStateOf("")
        }

        AlertDialog(title = {
            Text(
                text = "Create a new Shelf row", style = MaterialTheme.typography.titleMedium,
                fontSize = 22.sp,
                lineHeight = 28.sp
            )
        }, onDismissRequest = {
            addANewShelfDTO.isDialogBoxVisible.value = false
        }, text = {
            Column {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    label = {
                        Text(
                            text = "Shelf name",
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 12.sp
                        )
                    },
                    textStyle = MaterialTheme.typography.titleSmall,
                    singleLine = true,
                    value = customShelfName.value,
                    onValueChange = {
                        customShelfName.value = it
                    })
            }
        }, confirmButton = {
            Button(modifier = Modifier
                .fillMaxWidth()
                .pulsateEffect(), onClick = {
                addANewShelfDTO.onCreateClick(customShelfName.value, customShelfIconName.value)
                addANewShelfDTO.isDialogBoxVisible.value = false
            }) {
                Text(
                    text = "Create",
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp
                )
            }
        }, dismissButton = {
            androidx.compose.material3.OutlinedButton(modifier = Modifier
                .fillMaxWidth()
                .pulsateEffect(),
                onClick = {
                    addANewShelfDTO.isDialogBoxVisible.value = false
                }) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp
                )
            }
        })
    }
}