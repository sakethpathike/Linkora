package com.sakethh.linkora.ui.screens.settings.specific

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sakethh.linkora.R
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.commonComposables.pulsateEffect
import com.sakethh.linkora.ui.screens.CustomWebTab
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.screens.settings.composables.SpecificSettingsScreenTopAppBar
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LanguageSettingsScreen(
    navController: NavController,
    settingsScreenVM: SettingsScreenVM,
    customWebTab: CustomWebTab
) {
    val context = LocalContext.current
    val availableLanguages = remember {
        mutableStateListOf("English", "Hindi", "Urdu", "Punjabi")
    }
    val isInfoExpanded = rememberSaveable {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = Unit) {
        settingsScreenVM.eventChannel.collectLatest {
            when (it) {
                is CommonUiEvent.ShowToast -> {
                    Toast.makeText(context, context.getString(it.msg), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    SpecificSettingsScreenTopAppBar(
        topAppBarText = stringResource(id = R.string.language),
        navController = navController
    ) { paddingValues, topAppBarScrollBehaviour ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(topAppBarScrollBehaviour.nestedScrollConnection)
                .navigationBarsPadding()
                .padding(start = 15.dp, end = 15.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
            item {
                Text(
                    text = stringResource(id = R.string.current_language),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(15.dp))
            }
            item {
                Text(
                    text = "English",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp
                )
            }
            item {
                FilledTonalButton(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 15.dp, start = 10.dp, end = 10.dp)
                    .pulsateEffect(), onClick = {

                }) {
                    Text(
                        text = stringResource(id = R.string.change_app_language),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            item {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .clickable(interactionSource = remember {
                        MutableInteractionSource()
                    }, indication = null, onClick = {
                        isInfoExpanded.value = !isInfoExpanded.value
                    })
                ) {
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(id = R.string.available_languages),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Icon(
                            imageVector = if (isInfoExpanded.value) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = ""
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (isInfoExpanded.value) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Translate, contentDescription = "")
                                Spacer(modifier = Modifier.width(15.dp))
                                Column {
                                    Text(
                                        text = stringResource(id = R.string.represents_switching_to_the_respective_language),
                                        style = MaterialTheme.typography.titleSmall,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(15.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.ModeEdit, contentDescription = "")
                                Spacer(modifier = Modifier.width(15.dp))
                                Column {
                                    Text(
                                        text = "Represents contributing to the language strings. Click to contribute to the language. If successful, your contribution will be reflected in the app's language strings.",
                                        style = MaterialTheme.typography.titleSmall,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                    }
                }
            }
            items(availableLanguages) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.fillMaxWidth(0.6f)) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 16.sp
                        )
                        LinearProgressIndicator(
                            progress = { 0.75f }, modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 10.dp)
                        )
                        Text(
                            text = "9 of 12 strings localized",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilledTonalIconButton(
                            modifier = Modifier.pulsateEffect(),
                            onClick = { /*TODO*/ }) {
                            Icon(imageVector = Icons.Default.Translate, contentDescription = "")
                        }
                        Spacer(modifier = Modifier.width(15.dp))
                        FilledTonalIconButton(
                            modifier = Modifier.pulsateEffect(),
                            onClick = { /*TODO*/ }) {
                            Icon(imageVector = Icons.Default.ModeEdit, contentDescription = "")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }
}