package com.sakethh.linkora.ui.screens.settings.specific

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sakethh.linkora.LocalizedStrings
import com.sakethh.linkora.ui.CommonUiEvent
import com.sakethh.linkora.ui.screens.settings.SettingsScreenVM
import com.sakethh.linkora.ui.screens.settings.composables.RegularSettingComponent
import com.sakethh.linkora.ui.screens.settings.composables.SpecificScreenScaffold
import com.sakethh.linkora.ui.theme.poppinsFontFamily
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingsScreen(
    navController: NavController,
    settingsScreenVM: SettingsScreenVM
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        settingsScreenVM.eventChannel.collectLatest {
            when (it) {
                is CommonUiEvent.ShowToast -> {
                    Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }

    SpecificScreenScaffold(
        topAppBarText = LocalizedStrings.general.value,
        navController = navController
    ) { paddingValues, topAppBarScrollBehaviour ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(topAppBarScrollBehaviour.nestedScrollConnection)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            items(settingsScreenVM.generalSection(context)) {
                RegularSettingComponent(
                    settingsUIElement = it
                )
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun TextFieldForPreferenceComposable(
    textFieldDescText: String,
    textFieldLabel: String,
    textFieldValue: String,
    onResetButtonClick: () -> Unit,
    onTextFieldValueChange: (String) -> Unit,
    onConfirmButtonClick: () -> Unit,
    focusRequester: FocusRequester,
    readonly: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 25.dp, end = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ProvideTextStyle(value = TextStyle(fontFamily = poppinsFontFamily)) {
            OutlinedTextField(
                supportingText = {
                    Text(
                        text = textFieldDescText,
                        style = MaterialTheme.typography.titleSmall,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(
                            top = 5.dp,
                            bottom = 5.dp
                        )
                    )
                },
                value = textFieldValue,
                onValueChange = {
                    onTextFieldValueChange(it)
                },
                readOnly = readonly,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .focusRequester(focusRequester),
                label = {
                    Text(
                        text = textFieldLabel,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            FilledTonalIconToggleButton(
                checked = !readonly,
                onCheckedChange = {
                    onConfirmButtonClick()
                }) {
                Icon(
                    imageVector = if (readonly) Icons.Default.Edit else Icons.Default.Check,
                    contentDescription = ""
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            FilledTonalIconButton(onClick = {
                onResetButtonClick()
            }) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = ""
                )
            }
        }
    }
}