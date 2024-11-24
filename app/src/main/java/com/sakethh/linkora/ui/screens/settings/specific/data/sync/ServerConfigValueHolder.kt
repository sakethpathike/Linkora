package com.sakethh.linkora.ui.screens.settings.specific.data.sync

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ServerConfigValueHolder(
    holderLabel: String,
    value: String,
    info: String,
    onSaveClick: (newValue: String) -> Unit
) {
    val textFieldReadOnly = rememberSaveable {
        mutableStateOf(true)
    }
    val textFieldValue = rememberSaveable(value) {
        mutableStateOf(value)
    }
    val textFieldFocusRequester = remember {
        FocusRequester()
    }
    val isInfoExpanded = rememberSaveable {
        mutableStateOf(false)
    }
    Card(
        border = BorderStroke(
            1.dp, contentColorFor(MaterialTheme.colorScheme.surface)
        ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 15.dp)
            .animateContentSize()
    ) {
        Column(Modifier.padding(15.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(
                            end = 10.dp
                        )
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(0.1f),
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(5.dp)
                        .horizontalScroll(rememberScrollState()),
                    text = holderLabel,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp
                )
                Icon(imageVector = if (isInfoExpanded.value) Icons.Filled.Info else Icons.Outlined.Info,
                    contentDescription = null,
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .clickable {
                            isInfoExpanded.value = isInfoExpanded.value.not()
                        })
            }
        }
        if (isInfoExpanded.value) {
            Text(
                text = info,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
            )
        }
        TextField(
            readOnly = textFieldReadOnly.value,
            value = textFieldValue.value,
            onValueChange = {
                textFieldValue.value = it.replace(" ", "")
            },
            textStyle = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp)
                .fillMaxWidth()
                .focusRequester(textFieldFocusRequester),
        )
        Spacer(Modifier.height(15.dp))
        Column(modifier = Modifier.animateContentSize()) {
            if (!textFieldReadOnly.value) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp),
                    onClick = {
                        onSaveClick(textFieldValue.value)
                        textFieldReadOnly.value = true
                        textFieldFocusRequester.freeFocus()
                    }
                ) {
                    Text("Save", style = MaterialTheme.typography.titleSmall)
                }
            } else {
                FilledTonalButton(
                    onClick = {
                        textFieldReadOnly.value = false
                        textFieldFocusRequester.requestFocus()
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp)
                ) {
                    Text("Edit", style = MaterialTheme.typography.titleSmall)
                }
            }
        }
        Spacer(Modifier.height(15.dp))
    }
}