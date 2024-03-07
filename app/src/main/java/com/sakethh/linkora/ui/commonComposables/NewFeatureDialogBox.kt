package com.sakethh.linkora.ui.commonComposables

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.intPreferencesKey
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM
import com.sakethh.linkora.ui.viewmodels.SettingsScreenVM.Settings.dataStore

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun NewFeatureDialogBox(isDialogBoxVisible: MutableState<Boolean> = mutableStateOf(true)) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val selectedSearchFilters = remember {
        mutableStateListOf<String>()
    }
    val state = rememberLazyListState()
    val interacted = remember {
        mutableStateOf(false)
    }
    if (isDialogBoxVisible.value) {
        AlertDialog(onDismissRequest = { }, title = {
            Text(
                text = "v0.5.0-alpha01", style = MaterialTheme.typography.titleMedium,
                fontSize = 22.sp,
                lineHeight = 27.sp,
                textAlign = TextAlign.Start
            )
        }, text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(
                            10.dp
                        )
                ) {
                    Text(text = "• ")
                    Text(
                        text = buildAnnotatedString {
                            append("Search has been improved! You can now filter out the links and folders you want based on your search.")
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .padding(end = 10.dp)
                    )

                }
                LazyRow(
                    Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                change.consume()
                                interacted.value = true
                            }
                        }, state = state
                ) {
                    items(
                        listOf(
                            "Saved Links",
                            "Important Links",
                            "Archived Links",
                            "Folders",
                            "Archived Folders",
                            "Links from folders",
                            "History"
                        )
                    ) {
                        Row(modifier = Modifier.animateContentSize()) {
                            Spacer(modifier = Modifier.width(10.dp))
                            androidx.compose.material3.FilterChip(selected = selectedSearchFilters.contains(
                                it
                            ), onClick = {
                                if (selectedSearchFilters.contains(it)) {
                                    selectedSearchFilters.remove(it)
                                } else {
                                    selectedSearchFilters.add(it)
                                }
                            }, label = {
                                Text(
                                    text = it, style = MaterialTheme.typography.titleSmall
                                )
                            }, leadingIcon = {
                                if (selectedSearchFilters.contains(it)) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null
                                    )
                                }
                            })
                        }
                    }
                }
            }
        }, confirmButton = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .pulsateEffect(),
                onClick = {
                        SettingsScreenVM.Settings.changeSettingPreferenceValue(
                            intPreferencesKey(
                                SettingsScreenVM.SettingsPreferences.SAVED_APP_CODE.name
                            ), context.dataStore, SettingsScreenVM.APP_VERSION_CODE
                        )
                    isDialogBoxVisible.value = false
                }) {
                Text(
                    text = "Ok",
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp
                )
            }
        })
        LaunchedEffect(key1 = interacted.value) {
            var backIteration = false
            while (!interacted.value) {
                if (state.canScrollForward && !backIteration) {
                    state.animateScrollBy(500f, animationSpec = tween(1500))
                } else {
                    backIteration = true
                    state.animateScrollBy(-500f, animationSpec = tween(1500))
                }
                if (!state.canScrollBackward) {
                    backIteration = false
                }
            }
        }
    }
}